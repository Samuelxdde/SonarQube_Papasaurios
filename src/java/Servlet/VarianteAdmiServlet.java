package Servlet;

import Controlador.CsrfUtil;
import Controlador.Producto_varianteDAO;
import Controlador.ProductoDAO;
import Modelo.Producto_variante;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Ver CategoriaAdmiServlet para la explicación completa: todas las
// acciones que cambian datos van por POST (nunca GET) y validan el
// token CSRF de sesión.
@WebServlet("/VarianteAdmi")
public class VarianteAdmiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarListasYMostrar(request, response, new Producto_varianteDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Producto_varianteDAO dao = new Producto_varianteDAO();
        String accion = request.getParameter("accion");

        if (!CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarListasYMostrar(request, response, dao);
            return;
        }

        try {
            switch (accion == null ? "" : accion) {
                case "editar":
                    request.setAttribute("varianteEditar",
                            dao.consultarPorId(Integer.parseInt(request.getParameter("id"))));
                    break;

                case "eliminar":
                    dao.eliminarVariante(Integer.parseInt(request.getParameter("id")));
                    request.setAttribute("mensaje", "Variante eliminada.");
                    break;

                case "actualizar": {
                    Producto_variante v = leerFormulario(request);
                    v.setidVariante(Integer.parseInt(request.getParameter("idVariante")));
                    if (dao.existeVariante(v.getnombre_variante(), v.getProducto_idProducto(), v.getidVariante())) {
                        request.setAttribute("error", "Ese producto ya tiene una variante llamada \"" + v.getnombre_variante() + "\".");
                    } else {
                        dao.actualizarVariante(v);
                        request.setAttribute("mensaje", "Variante actualizada.");
                    }
                    break;
                }

                default: { // "insertar"
                    Producto_variante v = leerFormulario(request);
                    if (dao.existeVariante(v.getnombre_variante(), v.getProducto_idProducto(), null)) {
                        request.setAttribute("error", "Ese producto ya tiene una variante llamada \"" + v.getnombre_variante() + "\".");
                    } else {
                        dao.insertarVariante(v);
                        request.setAttribute("mensaje", "Variante registrada.");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarListasYMostrar(request, response, dao);
    }

    private Producto_variante leerFormulario(HttpServletRequest request) {
        Producto_variante v = new Producto_variante();
        v.setnombre_variante(request.getParameter("nombre_variante"));
        v.setprecio_variante(Integer.parseInt(request.getParameter("precio_variante")));
        v.setProducto_idProducto(Integer.parseInt(request.getParameter("Producto_idProducto")));
        return v;
    }

    private void cargarListasYMostrar(HttpServletRequest request, HttpServletResponse response, Producto_varianteDAO dao)
            throws ServletException, IOException {
        try {
            request.setAttribute("lista", dao.listarVariantes());
            request.setAttribute("listaProductos", new ProductoDAO().listarProductos());
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
        }
        request.getRequestDispatcher("/Vista/Varianteadmi.jsp").forward(request, response);
    }
}
