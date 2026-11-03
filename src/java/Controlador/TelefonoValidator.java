package Controlador;

import java.util.regex.Pattern;

/**
 * Valida que el teléfono contenga solo números (con un '+' opcional al
 * inicio para indicativo de país) y una longitud razonable.
 *
 * Antes este campo era de tipo texto libre: se validaba que no viniera
 * vacío (formulario web y formulario del panel admin), pero nada impedía
 * guardar letras, símbolos o textos como "no tengo" en una columna que se
 * usa después para contactar al cliente (WhatsApp, llamadas, envíos). El
 * mismo hueco existía en el endpoint /api/registro de la app móvil.
 */
public class TelefonoValidator {

    private TelefonoValidator() {
    }

    // Dígitos, con un '+' opcional al inicio para el indicativo de país
    // (p. ej. +573001234567). Sin espacios, guiones ni paréntesis: se le
    // pide al usuario el número "limpio" para no tener que normalizarlo
    // después a la hora de usarlo (WhatsApp, envíos, etc.).
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\+?\\d+$");

    private static final int LONGITUD_MINIMA = 7;
    private static final int LONGITUD_MAXIMA = 15;

    /**
     * @param valor teléfono ingresado
     * @return null si es válido, o un mensaje de error listo para mostrar al usuario
     */
    public static String validar(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El teléfono es obligatorio.";
        }
        String telefono = valor.trim();
        if (!PATRON_TELEFONO.matcher(telefono).matches()) {
            return "El teléfono solo debe contener números (puede empezar con '+' para el indicativo de país).";
        }
        String soloDigitos = telefono.startsWith("+") ? telefono.substring(1) : telefono;
        if (soloDigitos.length() < LONGITUD_MINIMA || soloDigitos.length() > LONGITUD_MAXIMA) {
            return "El teléfono debe tener entre " + LONGITUD_MINIMA + " y " + LONGITUD_MAXIMA + " dígitos.";
        }
        return null;
    }
}
