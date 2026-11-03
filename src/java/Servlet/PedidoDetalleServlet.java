package Servlet;

import Controlador.PedidoDAO;
import Controlador.Detalle_pedidoDAO;
import Controlador.ProductoDAO;
import Controlador.UsuariosDAO;
import Modelo.Pedido;
import Modelo.Detalle_pedido;
import Modelo.Producto;
import Modelo.Usuarios;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Apartado dedicado a UN pedido: el "¿dónde va y cómo va mi pedido?"
 * que antes no existía. Muestra el seguimiento completo (estado paso
 * a paso, hora estimada, a dónde va o si es para recoger en tienda,
 * y el detalle de productos) en vez del mensaje suelto de "pedido
 * solicitado" que había antes.
 *
 * Solo el dueño del pedido (o un admin) puede verlo — se valida el
 * Usuarios_idUsuarios contra la sesión para que nadie pueda ver el
 * pedido de otra persona cambiando el número en la URL.
 */
@WebServlet("/PedidoDetalle")
public class PedidoDetalleServlet extends HttpServlet {

    private static final int ROL_ADMIN = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuarios") == null) {
            response.sendRedirect(request.getContextPath() + "/Iniciar");
            return;
        }

        int idUsuarioSesion = (Integer) session.getAttribute("idUsuarios");
        Object perfilObj = session.getAttribute("perfil");
        boolean esAdmin = perfilObj != null && (Integer) perfilObj == ROL_ADMIN;

        // El id nunca debe quedar visible ni fijo en la URL (ej. "?id=5"):
        // así nadie puede ir cambiando el número a mano para intentar ver
        // pedidos ajenos, y el link se ve limpio. La primera vez que se
        // entra sí llega con "?id=", pero solo se usa para guardarlo en
        // la sesión y de inmediato se redirige a la URL limpia — desde
        // ahí en adelante (incluyendo recargar la página) el id se lee
        // de la sesión, nunca de la URL.
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int idSolicitado = Integer.parseInt(idParam);
                session.setAttribute("pedidoVerActual", idSolicitado);
            } catch (NumberFormatException e) {
                // Parámetro inválido: se ignora: si ya había un pedido
                // guardado en sesión de antes, se sigue usando ese.
            }
            response.sendRedirect(request.getContextPath() + "/PedidoDetalle");
            return;
        }

        Object idSesion = session.getAttribute("pedidoVerActual");
        if (idSesion == null) {
            response.sendRedirect(request.getContextPath() + "/PanelUsuario");
            return;
        }
        int idPedido = (Integer) idSesion;

        try {
            PedidoDAO pedidoDAO = new PedidoDAO();
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            if (pedido == null || (!esAdmin && pedido.getUsuarios_idUsuarios() != idUsuarioSesion)) {
                // No existe, o existe pero no es de este usuario: no revelamos
                // cuál es el caso, simplemente lo mandamos de vuelta a su panel.
                response.sendRedirect(request.getContextPath() + "/PanelUsuario");
                return;
            }

            Detalle_pedidoDAO detalleDAO = new Detalle_pedidoDAO();
            List<Detalle_pedido> detalles = detalleDAO.listarPorPedido(idPedido);

            Map<Integer, String> nombresProducto = new HashMap<>();
            for (Producto prod : new ProductoDAO().listarProductos()) {
                nombresProducto.put(prod.getidProducto(), prod.getnombre_producto());
            }

            // Si ya hay un repartidor llevando este pedido, se manda su nombre
            // y teléfono a la vista (para mostrarlos) y se habilita el bloque
            // de mapa en vivo.
            if (pedido.getRepartidor_idUsuarios() != null) {
                Usuarios repartidor = new UsuariosDAO().consultarPorId(pedido.getRepartidor_idUsuarios());
                request.setAttribute("repartidor", repartidor);
            }

            request.setAttribute("pedido", pedido);
            request.setAttribute("detalles", detalles);
            request.setAttribute("nombresProducto", nombresProducto);
            request.getRequestDispatcher("/PedidoDetalle.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "No se pudo cargar el pedido: " + e.getMessage());
            request.getRequestDispatcher("/PanelUsuario").forward(request, response);
        }
    }
}
