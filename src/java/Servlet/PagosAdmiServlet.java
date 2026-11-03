package Servlet;

import Controlador.CsrfUtil;
import Controlador.PagosDAO;
import Modelo.Pagos;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Ver CategoriaAdmiServlet para la explicación completa: todas las
// acciones que cambian datos van por POST (nunca GET) y validan el
// token CSRF de sesión.
@WebServlet("/PagosAdmi")
public class PagosAdmiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarLista(request, response, new PagosDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PagosDAO dao = new PagosDAO();
        String accion = request.getParameter("accion");

        if (!CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarLista(request, response, dao);
            return;
        }

        try {
            switch (accion == null ? "" : accion) {
                case "editar":
                    request.setAttribute("pagoEditar",
                            dao.consultarPagos(Integer.parseInt(request.getParameter("id"))));
                    break;

                case "eliminar":
                    dao.eliminarPagos(Integer.parseInt(request.getParameter("id")));
                    request.setAttribute("mensaje", "Pago eliminado correctamente.");
                    break;

                case "actualizar": {
                    Pagos p = leerFormulario(request);
                    p.setidPagos(Integer.parseInt(request.getParameter("idPagos")));
                    if (dao.existeEstadoPago(p.getestado_pago(), p.getidPagos())) {
                        request.setAttribute("error", "Ya existe otro estado de pago llamado \"" + p.getestado_pago() + "\".");
                    } else {
                        dao.actualizarPagos(p);
                        request.setAttribute("mensaje", "Pago actualizado correctamente.");
                    }
                    break;
                }

                default: { // "insertar"
                    Pagos p = leerFormulario(request);
                    if (dao.existeEstadoPago(p.getestado_pago(), null)) {
                        request.setAttribute("error", "Ya existe un estado de pago llamado \"" + p.getestado_pago() + "\".");
                    } else {
                        dao.insertarPago(p);
                        request.setAttribute("mensaje", "Pago registrado correctamente.");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarLista(request, response, dao);
    }

    private Pagos leerFormulario(HttpServletRequest request) {
        Pagos p = new Pagos();
        p.setestado_pago(request.getParameter("estado_pago"));
        return p;
    }

    private void cargarLista(HttpServletRequest request, HttpServletResponse response, PagosDAO dao)
            throws ServletException, IOException {
        try {
            request.setAttribute("lista", dao.listarPagos());
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar pagos: " + e.getMessage());
        }
        request.getRequestDispatcher("/Vista/Pagosadmi.jsp").forward(request, response);
    }
}
