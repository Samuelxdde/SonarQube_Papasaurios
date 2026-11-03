package Servlet;

import Controlador.CsrfUtil;
import Controlador.ProductoDAO;
import Controlador.CategoriaDAO;
import Controlador.Producto_varianteDAO;
import Modelo.Producto;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Ver CategoriaAdmiServlet para la explicación completa: todas las
// acciones que cambian datos van por POST (nunca GET) y validan el
// token CSRF de sesión.
@WebServlet("/ProductoAdmi")
public class ProductoAdmiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarListasYMostrar(request, response, new ProductoDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductoDAO dao = new ProductoDAO();
        String accion = request.getParameter("accion");

        if (!CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarListasYMostrar(request, response, dao);
            return;
        }

        try {
            switch (accion == null ? "" : accion) {
                case "editar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("productoEditar", dao.consultarPorId(id));
                    request.setAttribute("variantesProducto", new Producto_varianteDAO().listarPorProducto(id));
                    break;
                }

                case "eliminar":
                    dao.eliminarProducto(Integer.parseInt(request.getParameter("id")));
                    request.setAttribute("mensaje", "Producto eliminado.");
                    break;

                case "actualizar": {
                    Producto p = leerFormulario(request);
                    p.setidProducto(Integer.parseInt(request.getParameter("idProducto")));
                    if (dao.existeProducto(p.getnombre_producto(), p.getidProducto())) {
                        request.setAttribute("error", "Ya existe otro producto llamado \"" + p.getnombre_producto() + "\".");
                    } else {
                        dao.actualizarProducto(p);
                        request.setAttribute("mensaje", "Producto actualizado.");
                    }
                    break;
                }

                default: { // "insertar"
                    Producto p = leerFormulario(request);
                    if (dao.existeProducto(p.getnombre_producto(), null)) {
                        request.setAttribute("error", "Ya existe un producto llamado \"" + p.getnombre_producto() + "\".");
                    } else {
                        dao.insertarProducto(p);
                        request.setAttribute("mensaje", "Producto registrado.");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarListasYMostrar(request, response, dao);
    }

    private Producto leerFormulario(HttpServletRequest request) {
        Producto p = new Producto();
        p.setnombre_producto(request.getParameter("nombre_producto"));
        p.setdescripcion_producto(request.getParameter("descripcion_producto"));
        p.setprecio_base(Integer.parseInt(request.getParameter("precio_base")));
        p.setdisponible(request.getParameter("disponible") != null);
        p.setCategoria_idCategoria(Integer.parseInt(request.getParameter("Categoria_idCategoria")));
        p.setimagen_url(request.getParameter("imagen_url"));
        return p;
    }

    private void cargarListasYMostrar(HttpServletRequest request, HttpServletResponse response, ProductoDAO dao)
            throws ServletException, IOException {
        try {
            request.setAttribute("lista", dao.listarProductos());
            request.setAttribute("listaCategorias", new CategoriaDAO().listarCategorias());
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
        }
        request.getRequestDispatcher("/Vista/Productoadmi.jsp").forward(request, response);
    }
}
