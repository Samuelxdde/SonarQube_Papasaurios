package Servlet;

import Controlador.CategoriaDAO;
import Controlador.CsrfUtil;
import Modelo.Categoria;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CRUD de categorías del menú.
 *
 * Importante: TODAS las acciones (insertar, actualizar, editar, eliminar)
 * viajan por POST, nunca por GET. Dos razones:
 *  1) La URL nunca debe mostrar ids ni parámetros de acción — con POST
 *     los datos van en el cuerpo de la petición, así que la barra de
 *     direcciones siempre queda en "/CategoriaAdmi" sin importar el botón.
 *  2) Un GET que cambia datos (como el "eliminar" que había antes) es
 *     vulnerable a CSRF: cualquier link o imagen externa podía disparar
 *     el borrado usando la sesión ya iniciada del administrador. Por eso
 *     además se valida un token CSRF de sesión (ver Filtros.Filtro y
 *     Controlador.CsrfUtil) en cada POST que cambia datos.
 */
@WebServlet("/CategoriaAdmi")
public class CategoriaAdmiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // El GET solo muestra la lista, sin aceptar ninguna acción por
        // parámetro. Así la URL nunca lleva ids ni "accion" en la barra
        // de direcciones.
        cargarLista(request, response, new CategoriaDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CategoriaDAO dao = new CategoriaDAO();
        String accion = request.getParameter("accion");

        if (!CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarLista(request, response, dao);
            return;
        }

        try {
            switch (accion == null ? "" : accion) {
                case "editar":
                    request.setAttribute("categoriaEditar",
                            dao.consultarPorId(Integer.parseInt(request.getParameter("id"))));
                    break;

                case "eliminar":
                    dao.eliminarCategoria(Integer.parseInt(request.getParameter("id")));
                    request.setAttribute("mensaje", "Categoría eliminada.");
                    break;

                case "actualizar": {
                    Categoria c = leerFormulario(request);
                    c.setidCategoria(Integer.parseInt(request.getParameter("idCategoria")));
                    if (dao.existeCategoria(c.getnombre_categoria(), c.getidCategoria())) {
                        request.setAttribute("error", "Ya existe otra categoría llamada \"" + c.getnombre_categoria() + "\".");
                    } else {
                        dao.actualizarCategoria(c);
                        request.setAttribute("mensaje", "Categoría actualizada.");
                    }
                    break;
                }

                default: { // "insertar"
                    Categoria c = leerFormulario(request);
                    if (dao.existeCategoria(c.getnombre_categoria(), null)) {
                        request.setAttribute("error", "Ya existe una categoría llamada \"" + c.getnombre_categoria() + "\".");
                    } else {
                        dao.insertarCategoria(c);
                        request.setAttribute("mensaje", "Categoría registrada.");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarLista(request, response, dao);
    }

    private Categoria leerFormulario(HttpServletRequest request) {
        Categoria c = new Categoria();
        c.setnombre_categoria(request.getParameter("nombre_categoria"));
        String ordenParam = request.getParameter("orden");
        c.setorden(ordenParam != null && !ordenParam.isEmpty() ? Integer.parseInt(ordenParam) : 0);
        return c;
    }

    private void cargarLista(HttpServletRequest request, HttpServletResponse response, CategoriaDAO dao)
            throws ServletException, IOException {
        try {
            request.setAttribute("lista", dao.listarCategorias());
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar categorías: " + e.getMessage());
        }
        request.getRequestDispatcher("/Vista/Categoriaadmi.jsp").forward(request, response);
    }
}
