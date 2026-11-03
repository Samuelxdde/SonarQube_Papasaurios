package Controlador;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Validación del token CSRF que Filtros.Filtro genera por sesión.
 * Cada Servlet de administración que reciba un POST que cambia datos
 * (insertar / actualizar / eliminar) debe llamar a
 * {@link #esValido(HttpServletRequest)} antes de ejecutar la acción.
 */
public class CsrfUtil {

    private CsrfUtil() {
    }

    public static boolean esValido(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object tokenSesion = session.getAttribute("csrfToken");
        String tokenForm = request.getParameter("csrfToken");
        return tokenSesion != null && tokenSesion.equals(tokenForm);
    }
}
