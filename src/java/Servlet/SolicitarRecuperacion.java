package Servlet;

import Controlador.CodigoVerificacionDAO;
import Controlador.EnviarCorreo;
import Controlador.UsuariosDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Primer paso de la recuperación de contraseña: la persona escribe su
 * correo, y si está registrado le llega un código de 6 dígitos para poder
 * elegir una contraseña nueva (ConfirmarRecuperacion). Reemplaza el flujo
 * anterior, que generaba una clave al azar y la mandaba por correo sin
 * pedir confirmación de que quien la solicitó era el dueño de la cuenta.
 */
@WebServlet(name = "SolicitarRecuperacion", urlPatterns = {"/SolicitarRecuperacion"})
public class SolicitarRecuperacion extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String correo = request.getParameter("correo");
        boolean existe = correo != null && new UsuariosDAO().existeCorreo(correo, null);

        // El mensaje es igual exista o no el correo: así este formulario no
        // sirve para "adivinar" qué correos están registrados en el sistema.
        String mensaje = "Si el correo está registrado, te enviamos un código de recuperación.";

        if (existe) {
            try {
                String codigo = new CodigoVerificacionDAO().generarCodigo(correo, "recuperacion");
                EnviarCorreo.enviar(correo, "Código de recuperación - Papasaurios",
                        Controlador.PlantillaCorreo.codigoRecuperacion(codigo));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("tipoMensaje", "info");
        // Se manda el correo de vuelta a la vista para mostrar el segundo
        // formulario (código + contraseña nueva) sin que la persona tenga
        // que volver a escribirlo.
        request.setAttribute("correo", correo);
        request.getRequestDispatcher("/Vista/Recuperar.jsp").forward(request, response);
    }
}
