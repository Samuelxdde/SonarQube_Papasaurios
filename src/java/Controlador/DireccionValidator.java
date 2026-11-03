package Controlador;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Valida que la dirección de entrega tenga forma de dirección real, no
 * cualquier texto, y que esté dentro de la zona de cobertura (Soacha y
 * Bogotá — donde de verdad se hacen domicilios).
 *
 * Antes este campo solo se validaba como "no vacío": un pedido a domicilio
 * se podía confirmar con "asdf", "1234" o cualquier relleno, y el
 * repartidor terminaba con un pedido que no podía ubicar. No pretende ser
 * una verificación geográfica real (para eso haría falta un servicio de
 * geocodificación, como la API de Google Maps o Nominatim/OpenStreetMap,
 * que confirme que la dirección existe y cae dentro de la zona de
 * cobertura) — es un filtro barato que descarta los casos más obvios de
 * relleno y de ciudades fuera de cobertura, sin depender de un servicio
 * externo ni de una llave de API.
 */
public class DireccionValidator {

    private DireccionValidator() {
    }

    private static final int LONGITUD_MINIMA = 10;
    private static final int LONGITUD_MAXIMA = 150;

    // Una dirección real en Colombia casi siempre trae al menos un número
    // (Calle 10 # 5-20, Carrera 12 #34-56, Diagonal 34 #13A-40...). Exigir
    // al menos un dígito descarta relleno como "no tengo direccion" o
    // "asdfasdf" sin bloquear direcciones legítimas.
    private static final Pattern CONTIENE_NUMERO = Pattern.compile(".*\\d.*");

    // Detecta el mismo carácter repetido de principio a fin (ignorando
    // mayúsculas), como "aaaaaaaaaa" o "1111111111" — relleno típico para
    // pasar un mínimo de longitud sin escribir nada real.
    private static final Pattern MISMO_CARACTER_REPETIDO = Pattern.compile("(?i)^(.)\\1*$");

    // Ciudades grandes de Colombia que NO son Soacha ni Bogotá. Si el
    // cliente escribe alguna de estas en la dirección, es casi seguro que
    // el pedido cae fuera de la zona de cobertura (los domicilios solo
    // llegan a Soacha y Bogotá), así que se rechaza aquí mismo en vez de
    // dejar que el repartidor descubra la sorpresa después. Comparación
    // sin tildes (ver quitarTildes) para no fallar por "Medellin" vs
    // "Medellín".
    private static final String[] CIUDADES_FUERA_DE_COBERTURA = {
        "medellin", "cali", "barranquilla", "cartagena", "cucuta",
        "bucaramanga", "pereira", "manizales", "ibague", "santa marta",
        "villavicencio", "pasto", "monteria", "neiva", "armenia",
        "popayan", "valledupar", "sincelejo", "tunja", "florencia",
        "riohacha", "quibdo", "yopal", "mocoa", "leticia", "san andres"
    };

    /**
     * Quita tildes/diacríticos para poder comparar "Medellín" y "Medellin"
     * (o "Bogotá" y "Bogota") como si fueran el mismo texto.
     */
    private static String quitarTildes(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{M}", "");
    }

    /**
     * @param valor dirección ingresada
     * @return null si es válida, o un mensaje de error listo para mostrar al usuario
     */
    public static String validar(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "Para un pedido a domicilio necesitamos la dirección de entrega.";
        }
        String direccion = valor.trim();

        if (direccion.length() < LONGITUD_MINIMA) {
            return "La dirección parece incompleta. Incluye calle/carrera, número y barrio "
                    + "(ej: Cra 12 #34-56, Barrio Centro).";
        }
        if (direccion.length() > LONGITUD_MAXIMA) {
            return "La dirección es demasiado larga (máximo " + LONGITUD_MAXIMA + " caracteres).";
        }
        if (!CONTIENE_NUMERO.matcher(direccion).matches()) {
            return "La dirección debe incluir un número (de la calle/carrera y de la casa o "
                    + "apartamento), para que el repartidor pueda ubicarte.";
        }
        if (MISMO_CARACTER_REPETIDO.matcher(direccion.replace(" ", "")).matches()) {
            return "Esa dirección no parece real. Escribe la dirección completa de entrega.";
        }

        String direccionSinTildes = quitarTildes(direccion).toLowerCase();
        for (String ciudad : CIUDADES_FUERA_DE_COBERTURA) {
            // \b...\b para no disparar con coincidencias parciales dentro
            // de otra palabra (ej. que "Neiva" no active falsamente algo
            // como "Avenida Neivalandia" — caso límite, pero más correcto).
            if (Pattern.compile("\\b" + Pattern.quote(ciudad) + "\\b").matcher(direccionSinTildes).find()) {
                return "Por ahora solo hacemos domicilios en Soacha y Bogotá. Si tu dirección "
                        + "queda en otra ciudad, escríbenos por WhatsApp para ver otras opciones.";
            }
        }

        return null;
    }
}
