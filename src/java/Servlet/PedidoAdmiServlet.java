package Servlet;

import Controlador.CsrfUtil;
import Controlador.PedidoDAO;
import Controlador.Detalle_pedidoDAO;
import Controlador.Estado_pedidoDAO;
import Controlador.UsuariosDAO;
import Controlador.ProductoDAO;
import Modelo.Pedido;
import Modelo.Detalle_pedido;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Ver CategoriaAdmiServlet para la explicación completa: todas las
// acciones que cambian datos van por POST (nunca GET) y validan el
// token CSRF de sesión.
@WebServlet("/PedidoAdmi")
public class PedidoAdmiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarListasYMostrar(request, response, new PedidoDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PedidoDAO dao = new PedidoDAO();
        String accion = request.getParameter("accion");

        if (!CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarListasYMostrar(request, response, dao);
            return;
        }

        try {
            if ("eliminar".equals(accion)) {
                dao.eliminarPedido(Integer.parseInt(request.getParameter("id")));
                request.setAttribute("mensaje", "Pedido eliminado.");
            } else {
                // "actualizarEstado" (valor por defecto, así no rompe el
                // formulario existente que no mandaba "accion").
                int idPedido = Integer.parseInt(request.getParameter("idPedido"));
                int idEstado = Integer.parseInt(request.getParameter("Estado_pedido_idEstado_pedido"));
                dao.actualizarEstado(idPedido, idEstado);
                request.setAttribute("mensaje", "Estado del pedido actualizado.");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarListasYMostrar(request, response, dao);
    }

    private void cargarListasYMostrar(HttpServletRequest request, HttpServletResponse response, PedidoDAO dao)
            throws ServletException, IOException {
        try {
            List<Pedido> pedidos = dao.listarPedidos();
            request.setAttribute("lista", pedidos);
            request.setAttribute("listaEstados", new Estado_pedidoDAO().listarEstados());
            request.setAttribute("listaUsuarios", new UsuariosDAO().listarUsuarios());
            request.setAttribute("listaProductos", new ProductoDAO().listarProductos());

            Detalle_pedidoDAO detalleDAO = new Detalle_pedidoDAO();
            Map<Integer, List<Detalle_pedido>> detallesPorPedido = new HashMap<>();
            for (Pedido p : pedidos) {
                detallesPorPedido.put(p.getidPedido(), detalleDAO.listarPorPedido(p.getidPedido()));
            }
            request.setAttribute("detallesPorPedido", detallesPorPedido);
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar pedidos: " + e.getMessage());
        }
        request.getRequestDispatcher("/Vista/Pedidoadmi.jsp").forward(request, response);
    }
}
