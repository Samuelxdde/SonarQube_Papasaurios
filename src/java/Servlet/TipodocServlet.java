package Servlet;

import Controlador.CsrfUtil;
import Controlador.Tipo_documentoDAO;
import Modelo.Tipo_documento;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Ver CategoriaAdmiServlet para la explicación completa: todas las
// acciones que cambian datos van por POST (nunca GET) y validan el
// token CSRF de sesión.
@WebServlet("/Tipodoc")
public class TipodocServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarLista(request, response, new Tipo_documentoDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Tipo_documentoDAO dao = new Tipo_documentoDAO();
        String accion = request.getParameter("accion");

        if (!CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarLista(request, response, dao);
            return;
        }

        try {
            switch (accion == null ? "" : accion) {
                case "editar":
                    request.setAttribute("docEditar",
                            dao.ConsultarTipo_documento(Integer.parseInt(request.getParameter("id"))));
                    break;

                case "eliminar":
                    dao.eliminarTipoDocumento(Integer.parseInt(request.getParameter("id")));
                    request.setAttribute("mensaje", "Tipo de documento eliminado.");
                    break;

                case "actualizar": {
                    Tipo_documento doc = leerFormulario(request);
                    doc.setidTipo_documento(Integer.parseInt(request.getParameter("idTipo_documento")));
                    if (dao.existeTipoDocumento(doc.getdescripcion_doc(), doc.getidTipo_documento())) {
                        request.setAttribute("error", "Ya existe otro tipo de documento llamado \"" + doc.getdescripcion_doc() + "\".");
                    } else {
                        dao.actualizarTipoDocumento(doc);
                        request.setAttribute("mensaje", "Tipo de documento actualizado.");
                    }
                    break;
                }

                default: { // "insertar"
                    Tipo_documento doc = leerFormulario(request);
                    if (dao.existeTipoDocumento(doc.getdescripcion_doc(), null)) {
                        request.setAttribute("error", "Ya existe un tipo de documento llamado \"" + doc.getdescripcion_doc() + "\".");
                    } else {
                        dao.insertarTipo_documento(doc);
                        request.setAttribute("mensaje", "Tipo de documento insertado.");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarLista(request, response, dao);
    }

    private Tipo_documento leerFormulario(HttpServletRequest request) {
        Tipo_documento doc = new Tipo_documento();
        doc.setdescripcion_doc(request.getParameter("descripcion_doc"));
        return doc;
    }

    private void cargarLista(HttpServletRequest request, HttpServletResponse response, Tipo_documentoDAO dao)
            throws ServletException, IOException {
        try {
            request.setAttribute("lista", dao.listarTipoDocumento());
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar tipos de documento: " + e.getMessage());
        }
        request.getRequestDispatcher("/Vista/Tipodocumentoadmi.jsp").forward(request, response);
    }
}
