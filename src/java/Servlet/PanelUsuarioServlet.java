package Servlet;

import Controlador.PedidoDAO;
import Controlador.Detalle_pedidoDAO;
import Controlador.ProductoDAO;
import Modelo.Pedido;
import Modelo.Detalle_pedido;
import Modelo.Producto;
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
 * Carga el panel del usuario logueado: sus propios pedidos (con el
 * detalle de cada uno), para que pueda ver el estado sin mezclarse
 * con la información de otros usuarios.
 */
@WebServlet("/PanelUsuario")
public class PanelUsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("nombreUsuario") == null) {
            response.sendRedirect(request.getContextPath() + "/Iniciar");
            return;
        }

        // Endpoint liviano para el auto-refresco: el cliente pregunta
        // cada pocos segundos "¿cambió el estado de mis pedidos?" sin
        // recargar toda la página (por ejemplo, cuando cocina lo marca
        // "Listo" o el repartidor lo marca "Entregado").
        if ("estado".equals(request.getParameter("check"))) {
            responderFirmaEstado(session, response);
            return;
        }

        cargarListasYMostrar(request, response, session);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // "Firma" = una foto simple de los pedidos del cliente y su estado,
    // para detectar cuándo alguno avanzó (Recibido -> En preparación ->
    // Listo -> Entregado, o Cancelado).
    private String construirFirma(List<Pedido> misPedidos) {
        StringBuilder sb = new StringBuilder();
        for (Pedido p : misPedidos) {
            sb.append(p.getidPedido()).append(':').append(p.getEstado_pedido_idEstado_pedido()).append(',');
        }
        return sb.toString();
    }

    private void responderFirmaEstado(HttpSession session, HttpServletResponse response) throws IOException {
        String firma;
        try {
            Object idObj = session.getAttribute("idUsuarios");
            List<Pedido> misPedidos = idObj != null
                    ? new PedidoDAO().listarPorUsuario((Integer) idObj)
                    : new java.util.ArrayList<>();
            firma = construirFirma(misPedidos);
        } catch (Exception e) {
            firma = "error-" + System.currentTimeMillis();
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"firma\":\"" + firma.replace("\"", "") + "\"}");
    }

    private void cargarListasYMostrar(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        Object idObj = session.getAttribute("idUsuarios");
        PedidoDAO pedidoDAO = new PedidoDAO();
        Detalle_pedidoDAO detalleDAO = new Detalle_pedidoDAO();

        List<Pedido> misPedidos = new java.util.ArrayList<>();
        Map<Integer, List<Detalle_pedido>> detallesPorPedido = new HashMap<>();
        Map<Integer, String> nombresProducto = new HashMap<>();

        try {
            if (idObj != null) {
                int idUsuarios = (Integer) idObj;
                misPedidos = pedidoDAO.listarPorUsuario(idUsuarios);
                for (Pedido p : misPedidos) {
                    detallesPorPedido.put(p.getidPedido(), detalleDAO.listarPorPedido(p.getidPedido()));
                }
            }
            // Se cargan todos los productos una sola vez para poder mostrar
            // el nombre real en cada línea del pedido, en vez del id crudo.
            for (Producto prod : new ProductoDAO().listarProductos()) {
                nombresProducto.put(prod.getidProducto(), prod.getnombre_producto());
            }
        } catch (Exception e) {
            request.setAttribute("error", "No se pudieron cargar tus pedidos: " + e.getMessage());
        }

        request.setAttribute("misPedidos", misPedidos);
        request.setAttribute("detallesPorPedido", detallesPorPedido);
        request.setAttribute("nombresProducto", nombresProducto);
        request.setAttribute("firmaEstado", construirFirma(misPedidos));

        // Si el usuario acaba de confirmar un pedido en /Pedido, aquí está
        // el resumen (a dónde va, tipo de entrega, total). Se pasa una sola
        // vez a la vista y se borra de la sesión para que no se repita si
        // recarga la página.
        if (session.getAttribute("confPedidoId") != null) {
            request.setAttribute("confPedidoId", session.getAttribute("confPedidoId"));
            request.setAttribute("confTipoEntrega", session.getAttribute("confTipoEntrega"));
            request.setAttribute("confDireccion", session.getAttribute("confDireccion"));
            request.setAttribute("confTotal", session.getAttribute("confTotal"));
            request.setAttribute("confHoraEstimada", session.getAttribute("confHoraEstimada"));
            session.removeAttribute("confPedidoId");
            session.removeAttribute("confTipoEntrega");
            session.removeAttribute("confDireccion");
            session.removeAttribute("confTotal");
            session.removeAttribute("confHoraEstimada");
        }

        request.getRequestDispatcher("/PanelUsuario.jsp").forward(request, response);
    }
}
