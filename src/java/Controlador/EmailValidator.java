package Controlador;

import java.util.regex.Pattern;

public class EmailValidator {

    private EmailValidator() {
    }


    private static final Pattern PATRON_CORREO = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final int LONGITUD_MAXIMA = 254;


    public static String validar(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return "El correo es obligatorio.";
        }
        String valor = correo.trim();
        if (valor.length() > LONGITUD_MAXIMA) {
            return "El correo es demasiado largo.";
        }
        if (!PATRON_CORREO.matcher(valor).matches()) {
            return "El correo no tiene un formato válido (ejemplo: nombre@dominio.com).";
        }
        return null;
    }
}
