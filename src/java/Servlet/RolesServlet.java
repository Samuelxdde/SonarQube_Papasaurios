// ================================================
// RolesServlet.java — Source Packages/Servlet/
// ================================================
package Servlet;

import Controlador.CsrfUtil;
import Controlador.RolesDAO;
import Modelo.Roles;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Ver CategoriaAdmiServlet para la explicación completa: todas las
// acciones que cambian datos van por POST (nunca GET) y validan el
// token CSRF de sesión.
@WebServlet("/RolesAdmi")
public class RolesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarLista(request, response, new RolesDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RolesDAO dao = new RolesDAO();
        String accion = request.getParameter("accion");

        if (!CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarLista(request, response, dao);
            return;
        }

        try {
            switch (accion == null ? "" : accion) {
                case "editar":
                    request.setAttribute("rolEditar",
                            dao.ConsultarRoles(Integer.parseInt(request.getParameter("id"))));
                    break;

                case "eliminar":
                    dao.eliminarRol(Integer.parseInt(request.getParameter("id")));
                    request.setAttribute("mensaje", "Rol eliminado correctamente.");
                    break;

                case "actualizar": {
                    Roles rol = leerFormulario(request);
                    rol.setidRoles(Integer.parseInt(request.getParameter("idRoles")));
                    if (dao.existeRol(rol.getdescripcion_rol(), rol.getidRoles())) {
                        request.setAttribute("error", "Ya existe otro rol llamado \"" + rol.getdescripcion_rol() + "\".");
                    } else {
                        dao.actualizarRol(rol);
                        request.setAttribute("mensaje", "Rol actualizado correctamente.");
                    }
                    break;
                }

                default: { // "insertar"
                    Roles rol = leerFormulario(request);
                    if (dao.existeRol(rol.getdescripcion_rol(), null)) {
                        request.setAttribute("error", "Ya existe un rol llamado \"" + rol.getdescripcion_rol() + "\".");
                    } else {
                        dao.insertarRol(rol);
                        request.setAttribute("mensaje", "Rol insertado correctamente.");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarLista(request, response, dao);
    }

    private Roles leerFormulario(HttpServletRequest request) {
        Roles rol = new Roles();
        rol.setdescripcion_rol(request.getParameter("descripcion_rol"));
        return rol;
    }

    private void cargarLista(HttpServletRequest request, HttpServletResponse response, RolesDAO dao)
            throws ServletException, IOException {
        try {
            request.setAttribute("lista", dao.listarRoles());
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar roles: " + e.getMessage());
        }
        request.getRequestDispatcher("/Vista/Rolesadmi.jsp").forward(request, response);
    }
}
