package Servlet;

import Controlador.UsuariosDAO;
import Controlador.PedidoDAO;
import Controlador.PagosDAO;
import Controlador.ProductoDAO;
import Modelo.Usuarios;
import Modelo.Pedido;
import Modelo.Pagos;
import Modelo.Producto;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/PanelAdmin")
public class PanelAdminServlet extends HttpServlet {

    private static final int ROL_REPARTIDOR = 3;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("perfil") == null) {
            response.sendRedirect(request.getContextPath() + "/Iniciar");
            return;
        }

        UsuariosDAO usuariosDAO = new UsuariosDAO();

        // Endpoint liviano para refrescar solo la tarjeta de "Repartidores
        // trabajando ahora" cada pocos segundos, sin recargar todo el
        // dashboard (ver el fetch() en PanelAdmin.jsp).
        if ("repartidores".equals(request.getParameter("check"))) {
            responderRepartidoresActivos(usuariosDAO, response);
            return;
        }

        PedidoDAO pedidoDAO     = new PedidoDAO();
        PagosDAO pagosDAO       = new PagosDAO();
        ProductoDAO productoDAO = new ProductoDAO();

        List<Producto> productos;
        List<Pedido> pedidos;
        try {
            productos = productoDAO.listarProductos();
            pedidos = pedidoDAO.listarPedidos();
        } catch (Exception e) {
            productos = new java.util.ArrayList<>();
            pedidos = new java.util.ArrayList<>();
            request.setAttribute("error", "No se pudieron cargar los datos: " + e.getMessage());
        }

        // Conteos para tarjetas resumen
        int totalUsuarios = usuariosDAO.contarUsuarios();
        int totalPedidos   = pedidos.size();
        int totalPagos     = pagosDAO.listarPagos().size();
        int totalProductos = productos.size();

        // Pedidos a domicilio que aún no se han entregado ni cancelado
        // (1 Recibido, 2 En preparación, 3 Listo) — le da al admin una
        // idea rápida de cuánto trabajo tiene el repartidor en este momento.
        int totalEntregasPendientes = 0;
        for (Pedido p : pedidos) {
            int estado = p.getEstado_pedido_idEstado_pedido();
            if ("Domicilio".equalsIgnoreCase(p.gettipo_entrega()) && estado >= 1 && estado <= 3) {
                totalEntregasPendientes++;
            }
        }

        // Listas para tablas
        List<Usuarios> usuarios = usuariosDAO.listarUsuarios();
        List<Pagos>    pagos    = pagosDAO.listarPagos();

        // Repartidores con sesión activa ahora mismo (tienen el panel
        // abierto / están trabajando), para la tarjeta "En línea ahora".
        List<Usuarios> repartidoresActivos = filtrarRepartidoresActivos(usuarios);

        // Pasar al JSP
        request.setAttribute("totalUsuarios",  totalUsuarios);
        request.setAttribute("totalPedidos",   totalPedidos);
        request.setAttribute("totalPagos",     totalPagos);
        request.setAttribute("totalProductos", totalProductos);
        request.setAttribute("totalEntregasPendientes", totalEntregasPendientes);
        request.setAttribute("usuarios",  usuarios);
        request.setAttribute("pedidos",   pedidos);
        request.setAttribute("pagos",     pagos);
        request.setAttribute("productos", productos);
        request.setAttribute("repartidoresActivos", repartidoresActivos);

        request.getRequestDispatcher("/PanelAdmin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // Del total de usuarios ya cargado, se queda solo con los que son
    // Repartidor (rol 3) y cuyo id está en el registro de sesiones
    // activas (RepartidoresActivosRegistro).
    private List<Usuarios> filtrarRepartidoresActivos(List<Usuarios> usuarios) {
        List<Integer> idsActivos = Controlador.RepartidoresActivosRegistro.idsActivos();
        List<Usuarios> resultado = new java.util.ArrayList<>();
        for (Usuarios u : usuarios) {
            if (u.getRoles_idRoles() == ROL_REPARTIDOR && idsActivos.contains(u.getidUsuarios())) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    // Misma idea que filtrarRepartidoresActivos(), pero como respuesta
    // JSON liviana para el auto-refresco de la tarjeta, sin tener que
    // recargar el resto del dashboard (pedidos, pagos, productos...).
    private void responderRepartidoresActivos(UsuariosDAO usuariosDAO, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<Usuarios> activos;
        try {
            activos = filtrarRepartidoresActivos(usuariosDAO.listarUsuarios());
        } catch (Exception e) {
            activos = new java.util.ArrayList<>();
        }
        List<String> objetos = new java.util.ArrayList<>();
        for (Usuarios u : activos) {
            objetos.add(Controlador.JsonUtil.objeto(
                    "id", u.getidUsuarios(),
                    "nombre", u.getnombre() + " " + u.getapellido(),
                    "telefono", u.gettelefono()
            ));
        }
        response.getWriter().write(Controlador.JsonUtil.objeto(
                "repartidores", "@raw:" + Controlador.JsonUtil.arregloDeObjetos(objetos)
        ));
    }
}
