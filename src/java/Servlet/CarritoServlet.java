package Servlet;

import Controlador.ProductoDAO;
import Controlador.Producto_varianteDAO;
import Controlador.JsonUtil;
import Modelo.Producto;
import Modelo.Producto_variante;
import Modelo.ItemCarrito;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Maneja el carrito de compras en sesión: agregar, quitar, cambiar
 * cantidad y vaciar. El carrito vive en memoria (session) hasta que
 * el cliente confirma el pedido en PedidoServlet, que es cuando
 * recién se escribe en la base de datos.
 */
@WebServlet("/Carrito")
@SuppressWarnings("unchecked")
public class CarritoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        LinkedHashMap<String, ItemCarrito> carrito =
                (LinkedHashMap<String, ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new LinkedHashMap<>();
        }

        String accion = request.getParameter("accion");
        String error = null;

        // Agregar al carrito exige tener sesión iniciada. El menú en sí es
        // público (cualquiera puede verlo sin cuenta), pero el carrito vive
        // ligado al usuario logueado, así que si todavía no inició sesión no
        // se agrega nada: se avisa para que primero inicie sesión.
        if ("agregar".equals(accion) && session.getAttribute("nombreUsuario") == null) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().print("{\"ok\":false,\"requiereLogin\":true}");
            } else {
                response.sendRedirect(request.getContextPath() + "/Iniciar");
            }
            return;
        }

        try {
            switch (accion == null ? "" : accion) {
                case "agregar":
                    agregarItem(request, carrito);
                    session.setAttribute("mensajeCarrito", "Producto agregado al carrito.");
                    break;
                case "quitar":
                    carrito.remove(request.getParameter("clave"));
                    break;
                case "actualizar":
                    String clave = request.getParameter("clave");
                    int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                    if (carrito.containsKey(clave)) {
                        if (cantidad <= 0) {
                            carrito.remove(clave);
                        } else {
                            carrito.get(clave).setCantidad(cantidad);
                        }
                    }
                    break;
                case "vaciar":
                    carrito.clear();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            error = "Error al actualizar el carrito: " + e.getMessage();
            // request.setAttribute no sobrevive a un sendRedirect (es una
            // petición HTTP nueva), así que el error se guarda en sesión
            // para que el JSP de destino pueda mostrarlo una sola vez.
            session.setAttribute("errorCarrito", error);
        }

        session.setAttribute("carrito", carrito);

        // El menú público agrega productos al carrito por AJAX (fetch) para
        // no recargar toda la página cada vez — antes cada clic en "agregar"
        // mandaba a Carrito.jsp y de paso reiniciaba la pestaña de categoría
        // activa, perdiendo el lugar donde ibas navegando. Cuando la petición
        // viene por fetch, respondemos JSON en vez de redirigir.
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.setContentType("application/json; charset=UTF-8");
            // El feedback en este camino lo da el propio JS del menú (toast +
            // contador del carrito actualizado al instante), así que no dejamos
            // este mensaje guardado — si quedara, aparecería sin venir a cuento
            // la próxima vez que el usuario recargue el menú de otra forma.
            session.removeAttribute("mensajeCarrito");
            session.removeAttribute("errorCarrito");
            int totalItems = 0;
            for (ItemCarrito item : carrito.values()) {
                totalItems += item.getCantidad();
            }
            String json = error == null
                    ? "{\"ok\":true,\"totalItems\":" + totalItems + "}"
                    : "{\"ok\":false,\"error\":\"" + error.replace("\"", "'") + "\"}";
            response.getWriter().print(json);
            return;
        }

        String redirect = request.getParameter("redirect");
        response.sendRedirect(request.getContextPath() + (redirect != null ? redirect : "/Carrito"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // El panel lateral del carrito (Menu.jsp) pide el contenido actual
        // por fetch para no tener que navegar a /Carrito solo para verlo.
        // Se distingue de una visita normal por el mismo header que ya usa
        // el resto del carrito para las peticiones AJAX.
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            responderJson(request, response);
            return;
        }

        // Antes redirigía (sendRedirect) a /Vista/Carrito.jsp: eso hacía que
        // el navegador mostrara la URL del .jsp en la barra de direcciones.
        // Con forward, la URL que ve la persona sigue siendo /Carrito.
        request.getRequestDispatcher("/Vista/Carrito.jsp").forward(request, response);
    }

    @SuppressWarnings("unchecked")
    private void responderJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        LinkedHashMap<String, ItemCarrito> carrito =
                (LinkedHashMap<String, ItemCarrito>) session.getAttribute("carrito");

        List<String> itemsJson = new ArrayList<>();
        int total = 0;
        int totalItems = 0;

        if (carrito != null) {
            for (ItemCarrito item : carrito.values()) {
                total += item.getSubtotal();
                totalItems += item.getCantidad();
                itemsJson.add(JsonUtil.objeto(
                        "clave", item.getClaveLinea(),
                        "nombreProducto", item.getNombreProducto(),
                        "nombreVariante", item.getNombreVariante() != null ? item.getNombreVariante() : "",
                        "precioUnitario", item.getPrecioUnitario(),
                        "cantidad", item.getCantidad(),
                        "subtotal", item.getSubtotal()
                ));
            }
        }

        String json = JsonUtil.objeto(
                "ok", true,
                "items", "@raw:" + JsonUtil.arregloDeObjetos(itemsJson),
                "total", total,
                "totalItems", totalItems
        );

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(json);
    }

    private void agregarItem(HttpServletRequest request, LinkedHashMap<String, ItemCarrito> carrito)
            throws Exception {

        int idProducto = Integer.parseInt(request.getParameter("idProducto"));
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        String nota = request.getParameter("nota");

        String varianteParam = request.getParameter("idVariante");
        Integer idVariante = (varianteParam != null && !varianteParam.isEmpty())
                ? Integer.parseInt(varianteParam) : null;

        Producto producto = new ProductoDAO().consultarPorId(idProducto);
        if (producto == null) {
            return;
        }

        int precioUnitario;
        String nombreVariante = null;

        if (idVariante != null) {
            Producto_variante variante = new Producto_varianteDAO().consultarPorId(idVariante);
            if (variante == null) {
                return;
            }
            precioUnitario = variante.getprecio_variante();
            nombreVariante = variante.getnombre_variante();
        } else {
            precioUnitario = producto.getprecio_base();
        }

        ItemCarrito nuevo = new ItemCarrito(
                idProducto, producto.getnombre_producto(), idVariante,
                nombreVariante, precioUnitario, cantidad, nota,
                producto.getCategoria_idCategoria()
        );

        String clave = nuevo.getClaveLinea();
        if (carrito.containsKey(clave)) {
            // Mismo producto + misma variante ya en el carrito: solo suma cantidad.
            carrito.get(clave).setCantidad(carrito.get(clave).getCantidad() + cantidad);
        } else {
            carrito.put(clave, nuevo);
        }
    }
}
