package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Página estática, sin lógica: solo existe para que la URL en el navegador
 * sea /PoliticaDatos en vez de /Vista/PoliticaDatos.jsp.
 */
@WebServlet("/PoliticaDatos")
public class PoliticaDatosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Vista/PoliticaDatos.jsp").forward(request, response);
    }
}
