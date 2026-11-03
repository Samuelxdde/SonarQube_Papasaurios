package Servlet;

import Controlador.CodigoVerificacionDAO;
import Controlador.PasswordUtil;
import Controlador.UsuariosDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Segundo paso de la recuperación de contraseña: recibe el código enviado
 * por SolicitarRecuperacion junto con la contraseña nueva. Si el código es
 * válido, hashea y guarda la contraseña con PasswordUtil (igual que el
 * resto del sistema) y la deja lista para iniciar sesión.
 */
@WebServlet(name = "ConfirmarRecuperacion", urlPatterns = {"/ConfirmarRecuperacion"})
public class ConfirmarRecuperacion extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String correo = request.getParameter("correo");
        String codigo = request.getParameter("codigo");
        String claveNueva = request.getParameter("claveNueva");
        String claveConfirmar = request.getParameter("claveConfirmar");

        String mensaje;
        boolean exito = false;

        if (claveNueva == null || claveNueva.length() < 6) {
            mensaje = "La contraseña debe tener al menos 6 caracteres.";
        } else if (!claveNueva.equals(claveConfirmar)) {
            mensaje = "Las contraseñas no coinciden.";
        } else {
            try {
                String error = new CodigoVerificacionDAO().validarCodigo(correo, codigo, "recuperacion");
                if (error != null) {
                    mensaje = error;
                } else {
                    new UsuariosDAO().actualizarClavePorCorreo(correo, PasswordUtil.hash(claveNueva));
                    mensaje = "Contraseña actualizada correctamente. Ya puedes iniciar sesión.";
                    exito = true;
                }
            } catch (Exception e) {
                mensaje = "Error interno: " + e.getMessage();
            }
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("tipoMensaje", exito ? "exito" : "error");
        // Si no hubo éxito, se vuelve a mostrar el formulario de código +
        // contraseña (por eso se manda "correo" de nuevo); si hubo éxito,
        // no se manda "correo" para que la vista vuelva a mostrar el primer
        // formulario en vez de dejar reintentar con un código ya usado.
        if (!exito) {
            request.setAttribute("correo", correo);
        }
        request.getRequestDispatcher("/Vista/Recuperar.jsp").forward(request, response);
    }
}
