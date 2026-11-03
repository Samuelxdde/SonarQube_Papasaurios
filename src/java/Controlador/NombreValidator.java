package Controlador;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Valida que un nombre o apellido contenga solo letras (incluye tildes, ñ
 * y espacios para nombres compuestos), sin números ni símbolos.
 *
 * Antes los campos "Nombre" y "Apellido" solo tenían el atributo
 * `required` en el formulario: no se validaba el contenido, ni en el
 * navegador ni en el servidor. Se podía registrar un usuario con nombre
 * "12345" o apellido "asd123" sin ningún aviso.
 */
public class NombreValidator {

    private NombreValidator() {
    }

    // Letras (con tildes/ñ), espacios y apóstrofes o guiones para nombres
    // compuestos como "Ana María" o "O'Connor" o "Pérez-Gómez".
    private static final Pattern PATRON_NOMBRE = Pattern.compile(
            "^[A-Za-zÀ-ÖØ-öø-ÿ]+([ '-][A-Za-zÀ-ÖØ-öø-ÿ]+)*$"
    );

    private static final int LONGITUD_MAXIMA = 100;

    /**
     * @param valor texto ingresado
     * @param etiqueta nombre del campo para el mensaje de error (p. ej. "nombre", "apellido")
     * @return null si es válido, o un mensaje de error listo para mostrar al usuario
     */
    public static String validar(String valor, String etiqueta) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El " + etiqueta + " es obligatorio.";
        }
        String texto = valor.trim();
        if (texto.length() > LONGITUD_MAXIMA) {
            return "El " + etiqueta + " es demasiado largo.";
        }
        if (!PATRON_NOMBRE.matcher(texto).matches()) {
            return "El " + etiqueta + " solo debe contener letras.";
        }
        return null;
    }
}
