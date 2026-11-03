/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servlet;

// OBSOLETO: Recuperar.jsp ya no envía formularios a este servlet. Se
// reemplazó por el flujo con código de verificación (SolicitarRecuperacion
// + ConfirmarRecuperacion), que confirma que quien pide el cambio de clave
// realmente tiene acceso al correo, y deja elegir la contraseña nueva en
// vez de generar una al azar. Se deja esta clase por si algo externo la
// referencia todavía; se puede borrar junto con su urlPattern "/RecuperarClave"
// cuando se confirme que ya no hace falta.
import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import Controlador.EnviarCorreo;

@WebServlet(name = "RecuperarClave", urlPatterns = {"/RecuperarClave"})
public class RecuperarClave extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String documento = request.getParameter("documento");
        UsuariosDAO dao = new UsuariosDAO();
        Usuarios usuario = dao.ConsultaUsuarios(documento);

        if (usuario != null) {
            String nuevaClave = java.util.UUID.randomUUID().toString().substring(0, 8);
            usuario.setclave(Controlador.PasswordUtil.hash(nuevaClave)); // se guarda el hash; el correo lleva la clave en texto plano

            try {
                // Actualizar la clave en BD
                dao.actualizarClave(usuario);

                // Validar que el correo no esté vacío
                if (usuario.getcorreo() != null && !usuario.getcorreo().isEmpty()) {
                    boolean enviado = EnviarCorreo.enviar(usuario.getcorreo(), "Recuperación de contraseña",
                        "Tu nueva contraseña temporal es: " + nuevaClave);
                    if (enviado) {
                        request.setAttribute("mensaje", "Se envió una nueva contraseña al correo registrado.");
                    } else {
                        request.setAttribute("mensaje", "No se pudo enviar el correo. Contacta al administrador del sitio.");
                    }
                } else {
                    request.setAttribute("mensaje", "El usuario no tiene correo registrado.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("mensaje", "Error al actualizar la contraseña: " + e.getMessage());
            }
        } else {
            request.setAttribute("mensaje", "No existe un usuario con ese documento.");
        }

        request.getRequestDispatcher("/Vista/Recuperar.jsp").forward(request, response);
    }
}
