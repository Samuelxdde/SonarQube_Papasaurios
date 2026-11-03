package Controlador;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hashing de contraseñas con PBKDF2WithHmacSHA256.
 *
 * PBKDF2 viene incluido en el propio Java (paquete javax.crypto), así que
 * no hace falta agregar ningún jar nuevo al proyecto para usarlo.
 *
 * El valor que se guarda en la base de datos tiene el formato:
 *   iteraciones:sal_en_base64:hash_en_base64
 * Guardar las iteraciones junto con el hash permite subir ese número más
 * adelante (equipos más rápidos = hay que iterar más) sin invalidar las
 * contraseñas ya guardadas con el número anterior.
 */
public class PasswordUtil {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 120_000;
    private static final int LONGITUD_HASH_BITS = 256;
    private static final int LONGITUD_SAL_BYTES = 16;

    /** Genera un hash nuevo (con una sal aleatoria nueva) para una contraseña en texto plano. */
    public static String hash(String claveEnTextoPlano) {
        try {
            byte[] sal = new byte[LONGITUD_SAL_BYTES];
            new SecureRandom().nextBytes(sal);
            byte[] hash = pbkdf2(claveEnTextoPlano.toCharArray(), sal, ITERACIONES, LONGITUD_HASH_BITS);
            return ITERACIONES + ":"
                    + Base64.getEncoder().encodeToString(sal) + ":"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException("No se pudo generar el hash de la contraseña", ex);
        }
    }

    /**
     * true si el valor guardado en BD tiene el formato de hash de esta clase
     * ("iteraciones:sal:hash"). Si es false, es una contraseña vieja en
     * texto plano de antes de este cambio.
     */
    public static boolean esHash(String valorGuardado) {
        return valorGuardado != null && valorGuardado.matches("\\d+:[A-Za-z0-9+/=]+:[A-Za-z0-9+/=]+");
    }

    /**
     * Compara una contraseña en texto plano (lo que el usuario acaba de
     * escribir en el formulario) contra el valor guardado en BD. Funciona
     * tanto si ese valor ya es un hash como si todavía es texto plano (de
     * antes de este cambio) — en ese segundo caso hace una comparación
     * directa, para no romper cuentas viejas.
     */
    public static boolean verificar(String claveEnTextoPlano, String valorGuardado) {
        if (claveEnTextoPlano == null || valorGuardado == null) return false;

        if (!esHash(valorGuardado)) {
            // Contraseña antigua sin hashear: se compara tal cual.
            return claveEnTextoPlano.equals(valorGuardado);
        }

        try {
            String[] partes = valorGuardado.split(":");
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[2]);
            byte[] hashCalculado = pbkdf2(claveEnTextoPlano.toCharArray(), sal, iteraciones, hashEsperado.length * 8);
            return compararEnTiempoConstante(hashEsperado, hashCalculado);
        } catch (Exception ex) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] clave, byte[] sal, int iteraciones, int longitudBits)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(clave, sal, iteraciones, longitudBits);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITMO);
        return skf.generateSecret(spec).getEncoded();
    }

    // Comparar byte a byte sin cortar apenas encuentra una diferencia: así
    // el tiempo que tarda la comparación no delata cuántos bytes acertó un
    // posible atacante (ataque de "timing").
    private static boolean compararEnTiempoConstante(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diferencia = 0;
        for (int i = 0; i < a.length; i++) {
            diferencia |= a[i] ^ b[i];
        }
        return diferencia == 0;
    }
}
