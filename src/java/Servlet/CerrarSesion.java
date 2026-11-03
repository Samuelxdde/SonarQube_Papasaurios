package Servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/CerrarSesion")
public class CerrarSesion extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtiene la sesión actual y la destruye
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            Object idUsuarios = sesion.getAttribute("idUsuarios");
            Object perfil = sesion.getAttribute("perfil");
            // Si quien cierra sesión es un repartidor, lo sacamos de una vez
            // del registro de "sesiones activas" (no hace falta esperar a
            // que expire el umbral de 45s para que deje de verse como
            // "trabajando" en el panel del admin/cocina).
            if (idUsuarios instanceof Integer && perfil instanceof Integer && (Integer) perfil == 3) {
                Controlador.RepartidoresActivosRegistro.marcarInactivo((Integer) idUsuarios);
            }
            sesion.invalidate();
        }
        // Redirige al inicio
        response.sendRedirect(request.getContextPath() + "/");
    }
}