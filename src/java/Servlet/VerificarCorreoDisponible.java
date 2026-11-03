package Servlet;

import Controlador.EmailValidator;
import Controlador.UsuariosDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Comprueba si un correo ya tiene cuenta, sin enviar ningún código.
 * Registrarse.jsp lo usa para avisar en tiempo real (al salir del campo de
 * correo) si ese correo ya está registrado, antes de que la persona llegue
 * a pedir el código de verificación — así no hay que esperar a ese paso,
 * ni se manda un código de más, para enterarse de que ya tiene cuenta.
 */
@WebServlet(name = "VerificarCorreoDisponible", urlPatterns = {"/VerificarCorreoDisponible"})
public class VerificarCorreoDisponible extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String correo = request.getParameter("correo");

        // El formato del correo ya lo valida el propio JS del formulario;
        // si llega algo mal formado respondemos "disponible" para no pisar
        // el mensaje de formato que el JS ya está mostrando en ese caso.
        if (EmailValidator.validar(correo) != null) {
            response.getWriter().write("{\"disponible\":true}");
            return;
        }

        boolean existe = new UsuariosDAO().existeCorreo(correo, null);
        response.getWriter().write("{\"disponible\":" + !existe + "}");
    }
}
