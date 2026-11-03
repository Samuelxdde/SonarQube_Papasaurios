package Servlet;

import Controlador.CodigoVerificacionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Segundo paso de la verificación de correo en el registro: recibe el
 * código que la persona ingresó y, si es válido, guarda el correo en la
 * sesión como "correoVerificado". Registrarse.java exige que ese atributo
 * exista y coincida con el correo del formulario antes de crear la cuenta,
 * así nadie puede registrarse saltándose el paso de verificación (por
 * ejemplo enviando el formulario directo sin pasar por el JS).
 */
@WebServlet(name = "VerificarCodigoRegistro", urlPatterns = {"/VerificarCodigoRegistro"})
public class VerificarCodigoRegistro extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String correo = request.getParameter("correo");
        String codigo = request.getParameter("codigo");

        try {
            String error = new CodigoVerificacionDAO().validarCodigo(correo, codigo, "registro");
            if (error == null) {
                request.getSession().setAttribute("correoVerificado", correo);
                response.getWriter().write("{\"ok\":true,\"mensaje\":\"Correo verificado correctamente.\"}");
            } else {
                response.getWriter().write("{\"ok\":false,\"mensaje\":\"" + error.replace("\"", "'") + "\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"ok\":false,\"mensaje\":\"Error interno: "
                    + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
