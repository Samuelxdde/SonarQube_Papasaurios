package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Muestra el primer paso de "Recuperar contraseña" (pedir el correo) en la
 * URL /Recuperar en vez de /Vista/Recuperar.jsp. El segundo paso (código +
 * contraseña nueva) lo muestra SolicitarRecuperacion, que hace forward a
 * la misma vista después de enviar el código.
 */
@WebServlet("/Recuperar")
public class RecuperarServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Vista/Recuperar.jsp").forward(request, response);
    }
}
