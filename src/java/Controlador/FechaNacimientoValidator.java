package Controlador;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Valida la fecha de nacimiento antes de convertirla a java.sql.Date.
 *
 * Antes, tanto el registro público (Servlet/Registrarse.java) como el
 * panel de administración de usuarios (Servlet/UsuarioAdmi.java) llamaban
 * directamente a Date.valueOf(request.getParameter("fecha_nac")). El
 * input HTML es type="date", pero nada impide enviar el formulario sin
 * pasar por ese control del navegador (herramientas como Postman, un
 * formulario editado a mano, o simplemente escribiendo un año de 5+
 * dígitos si el navegador lo permite). Un valor como "10000-01-01" hace
 * que Date.valueOf lance una IllegalArgumentException que no se
 * capturaba en ningún lado y terminaba como un error 500 sin control
 * ("error de código crítico en el servidor").
 *
 * Este validador exige un año de exactamente 4 dígitos (0001-9999, el
 * mismo criterio ya usado en Servlet/MobileApiServlet.java para el
 * endpoint de registro de la app móvil) y una fecha que no esté en el
 * futuro.
 */
public class FechaNacimientoValidator {

    private FechaNacimientoValidator() {
    }

    // yyyy-MM-dd con año de exactamente 4 dígitos.
    private static final Pattern PATRON_FECHA = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    /**
     * @param valor fecha ingresada (formato esperado AAAA-MM-DD)
     * @return null si es válida, o un mensaje de error listo para mostrar al usuario
     */
    public static String validar(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "La fecha de nacimiento es obligatoria.";
        }
        String fecha = valor.trim();
        if (!PATRON_FECHA.matcher(fecha).matches()) {
            return "La fecha de nacimiento no es válida. Usa el formato AAAA-MM-DD con un año de 4 dígitos.";
        }
        try {
            LocalDate fechaNac = LocalDate.parse(fecha);
            if (fechaNac.isAfter(LocalDate.now())) {
                return "La fecha de nacimiento no puede ser una fecha futura.";
            }
        } catch (DateTimeParseException e) {
            return "La fecha de nacimiento no es válida.";
        }
        return null;
    }
}
