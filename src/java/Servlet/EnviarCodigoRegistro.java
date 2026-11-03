package Servlet;

import Controlador.CodigoVerificacionDAO;
import Controlador.EmailValidator;
import Controlador.EnviarCorreo;
import Controlador.UsuariosDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Primer paso de la verificación de correo en el registro: recibe el correo
 * que la persona escribió en el formulario, genera un código de 6 dígitos
 * y se lo envía. El formulario de Registrarse.jsp llama a este servlet por
 * fetch (AJAX) antes de dejar avanzar al segundo paso.
 */
@WebServlet(name = "EnviarCodigoRegistro", urlPatterns = {"/EnviarCodigoRegistro"})
public class EnviarCodigoRegistro extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String correo = request.getParameter("correo");

        String errorFormato = EmailValidator.validar(correo);
        if (errorFormato != null) {
            responder(response, false, errorFormato);
            return;
        }
        if (new UsuariosDAO().existeCorreo(correo, null)) {
            responder(response, false, "Ese correo ya está registrado.");
            return;
        }

        try {
            String codigo = new CodigoVerificacionDAO().generarCodigo(correo, "registro");
            boolean enviado = EnviarCorreo.enviar(correo, "Tu código de verificación - Papasaurios",
                    Controlador.PlantillaCorreo.codigoRegistro(codigo));
            if (enviado) {
                responder(response, true, "Te enviamos un código de verificación a tu correo.");
            } else {
                responder(response, false, "No se pudo enviar el correo. Intenta de nuevo en unos minutos.");
            }
        } catch (Exception e) {
            responder(response, false, "Error interno: " + e.getMessage());
        }
    }

    private void responder(HttpServletResponse response, boolean ok, String mensaje) throws IOException {
        String json = "{\"ok\":" + ok + ",\"mensaje\":\"" + mensaje.replace("\"", "'") + "\"}";
        response.getWriter().write(json);
    }
}
