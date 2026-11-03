package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Iniciar")
public class InicioSesion extends HttpServlet {

    /**
     * Método para procesar el login con POST
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Captura los datos del formulario
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("pass");

        // Consulta el usuario en la base de datos
        UsuariosDAO midao = new UsuariosDAO();
        Usuarios usuarioBD = midao.ConsultaUsuarios(usuario);

        if (usuarioBD == null) {
            // Usuario no existe
            request.setAttribute("mensaje", "El documento no existe");
            request.getRequestDispatcher("/Vista/InicioSesion.jsp").forward(request, response);

        } else if (!Controlador.PasswordUtil.verificar(password, usuarioBD.getclave())) {
            // Contraseña incorrecta
            request.setAttribute("mensaje", "Clave incorrecta");

            request.getRequestDispatcher("/Vista/InicioSesion.jsp").forward(request, response);

        } else {
            // Si la cuenta todavía tenía la contraseña vieja en texto plano
            // (de antes de este cambio), aprovechamos que sabemos que es
            // correcta para guardar ya mismo su versión hasheada, sin que
            // el usuario tenga que hacer nada.
            if (!Controlador.PasswordUtil.esHash(usuarioBD.getclave())) {
                usuarioBD.setclave(Controlador.PasswordUtil.hash(password));
                try {
                    midao.actualizarClave(usuarioBD);
                } catch (java.sql.SQLException ex) {
                    System.err.println("No se pudo migrar la contraseña a hash: " + ex.getMessage());
                }
            }

            // Login exitoso, enviamos datos al JSP
            HttpSession sesion = request.getSession();
            sesion.setAttribute("nombreUsuario", usuarioBD.getnombre());
            sesion.setAttribute("perfil", usuarioBD.getRoles_idRoles());
            sesion.setAttribute("idUsuarios", usuarioBD.getidUsuarios());
            sesion.setAttribute("correoUsuario", usuarioBD.getcorreo());
            sesion.setAttribute("telefonoUsuario", usuarioBD.gettelefono());
            request.setAttribute("mensaje", "Bienvenido: " + usuarioBD.getnombre());

            // Aquí va la separación de roles
           if (usuarioBD.getRoles_idRoles() == 1) {
                response.sendRedirect(request.getContextPath() + "/PanelAdmin");
            } else if (usuarioBD.getRoles_idRoles() == 3) {
                response.sendRedirect(request.getContextPath() + "/PanelRepartidor");
            } else if (usuarioBD.getRoles_idRoles() == 4) {
                response.sendRedirect(request.getContextPath() + "/PanelCocina");
            } else {
                response.sendRedirect(request.getContextPath() + "/PanelUsuario");
            }

        }
    }

    /**
     * Método GET (opcional, aquí solo puedes redirigir al login)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirige al login si alguien intenta acceder vía GET
        request.getRequestDispatcher("/Vista/InicioSesion.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para manejar inicio de sesión de usuarios";
    }
}