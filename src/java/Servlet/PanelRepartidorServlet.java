package Servlet;

import Controlador.PedidoDAO;
import Controlador.Detalle_pedidoDAO;
import Controlador.ProductoDAO;
import Controlador.UsuariosDAO;
import Controlador.UbicacionDAO;
import Modelo.Pedido;
import Modelo.Detalle_pedido;
import Modelo.Producto;
import Modelo.Usuarios;
import java.io.IOException;
import java.util.ArrayList;
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
 * Panel del repartidor. Es exclusivo del rol Repartidor — el admin NO
 * entra aquí ni puede tomar/entregar/cancelar pedidos desde este panel;
 * el admin gestiona todo (incluido el estado de cocina) desde
 * /PedidoAdmi. Mantener esto separado evita que el admin "juegue" a
 * ser repartidor y que el panel operativo del repartidor se mezcle
 * con tareas administrativas.
 *
 * Solo le muestra los pedidos a domicilio (los de "Recoger en tienda"
 * no le competen) y le deja tomar los que ya están "Listo" en cocina,
 * marcarlos "Entregado", o "Cancelado" si no se pudo entregar.
 *
 * Estados de Estado_pedido usados aquí:
 *  1 Recibido, 2 En preparación, 3 Listo, 4 Entregado, 5 Cancelado.
 */
@WebServlet("/PanelRepartidor")
public class PanelRepartidorServlet extends HttpServlet {

    private static final int ROL_REPARTIDOR = 3;
    private static final String TIPO_DOMICILIO = "Domicilio";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("perfil") == null) {
            response.sendRedirect(request.getContextPath() + "/Iniciar");
            return;
        }
        if (!esRepartidor(session)) {
            // Alguien con otro rol (incluido el admin) intentó entrar
            // directo por la URL: lo mandamos a su propio panel.
            response.sendRedirect(request.getContextPath() + "/PanelUsuario");
            return;
        }

        // Cada visita a esta página (incluido el ping automático de abajo)
        // cuenta como "sigue trabajando" para el registro de sesiones
        // activas que ven el admin y cocina.
        Controlador.RepartidoresActivosRegistro.marcarActivo((Integer) session.getAttribute("idUsuarios"));

        // Endpoint liviano para el auto-refresco: la página pregunta cada
        // pocos segundos "¿cambió algo?" sin recargar todo (por ejemplo,
        // un pedido nuevo que ya quedó "Listo" en cocina, o que otro
        // repartidor tomó uno antes que tú).
        if ("estado".equals(request.getParameter("check"))) {
            responderFirmaEstado(response);
            return;
        }

        cargarListasYMostrar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("perfil") == null || !esRepartidor(session)) {
            response.sendRedirect(request.getContextPath() + "/Iniciar");
            return;
        }

        int idUsuarioSesion = (Integer) session.getAttribute("idUsuarios");
        Controlador.RepartidoresActivosRegistro.marcarActivo(idUsuarioSesion);

        if (!Controlador.CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarListasYMostrar(request, response);
            return;
        }

        PedidoDAO pedidoDAO = new PedidoDAO();
        String accion = request.getParameter("accion");

        try {
            int idPedido = Integer.parseInt(request.getParameter("id"));
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            if (pedido == null || !TIPO_DOMICILIO.equalsIgnoreCase(pedido.gettipo_entrega())) {
                request.setAttribute("error", "Ese pedido no existe o no es a domicilio.");
            } else if ("tomar".equals(accion)) {
                if (pedido.getEstado_pedido_idEstado_pedido() != 3) {
                    request.setAttribute("error", "Solo puedes tomar un pedido que ya esté \"Listo\" en cocina.");
                } else if (pedido.getRepartidor_idUsuarios() != null) {
                    request.setAttribute("error", "Ese pedido ya lo tomó otro repartidor.");
                } else if (pedidoDAO.asignarRepartidor(idPedido, idUsuarioSesion)) {
                    request.setAttribute("mensaje", "Pedido #" + idPedido + " asignado a ti. Activa \"Compartir mi ubicación\" para que el cliente vea tu recorrido.");
                } else {
                    request.setAttribute("error", "No se pudo tomar el pedido (puede que alguien más se te haya adelantado).");
                }
            } else if ("entregar".equals(accion)) {
                if (pedido.getRepartidor_idUsuarios() == null || pedido.getRepartidor_idUsuarios() != idUsuarioSesion) {
                    request.setAttribute("error", "Este pedido no está asignado a ti.");
                } else if (pedido.getEstado_pedido_idEstado_pedido() != 3) {
                    request.setAttribute("error", "Solo puedes marcar como entregado un pedido que ya esté \"Listo\".");
                } else {
                    pedidoDAO.actualizarEstado(idPedido, 4); // 4 = Entregado
                    new UbicacionDAO().eliminarUbicacion(idPedido); // ya no hay nada que rastrear
                    request.setAttribute("mensaje", "Pedido #" + idPedido + " marcado como entregado.");
                }
            } else if ("cancelar".equals(accion)) {
                if (pedido.getRepartidor_idUsuarios() == null || pedido.getRepartidor_idUsuarios() != idUsuarioSesion) {
                    request.setAttribute("error", "Este pedido no está asignado a ti.");
                } else {
                    pedidoDAO.actualizarEstado(idPedido, 5); // 5 = Cancelado
                    new UbicacionDAO().eliminarUbicacion(idPedido);
                    request.setAttribute("mensaje", "Pedido #" + idPedido + " marcado como no entregado / cancelado.");
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarListasYMostrar(request, response);
    }

    private boolean esRepartidor(HttpSession session) {
        Object perfil = session.getAttribute("perfil");
        return perfil != null && (Integer) perfil == ROL_REPARTIDOR;
    }

    // "Firma" = una foto simple de los pedidos a domicilio activos, su
    // estado y quién los tiene asignados. Incluir el repartidor asignado
    // importa aquí: si otro repartidor toma un pedido, este repartidor
    // necesita enterarse (deja de estar disponible) aunque el estado del
    // pedido en sí no haya cambiado de "Listo".
    private String construirFirma(List<Pedido> pendientes) {
        StringBuilder sb = new StringBuilder();
        for (Pedido p : pendientes) {
            sb.append(p.getidPedido()).append(':')
              .append(p.getEstado_pedido_idEstado_pedido()).append(':')
              .append(p.getRepartidor_idUsuarios() == null ? 0 : p.getRepartidor_idUsuarios())
              .append(',');
        }
        return sb.toString();
    }

    private void responderFirmaEstado(HttpServletResponse response) throws IOException {
        PedidoDAO pedidoDAO = new PedidoDAO();
        String firma;
        try {
            List<Pedido> pendientes = new ArrayList<>();
            for (Pedido p : pedidoDAO.listarPorTipoEntrega(TIPO_DOMICILIO)) {
                int estado = p.getEstado_pedido_idEstado_pedido();
                if (estado != 4 && estado != 5) {
                    pendientes.add(p);
                }
            }
            firma = construirFirma(pendientes);
        } catch (Exception e) {
            firma = "error-" + System.currentTimeMillis();
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"firma\":\"" + firma.replace("\"", "") + "\"}");
    }

    private void cargarListasYMostrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        PedidoDAO pedidoDAO = new PedidoDAO();
        Detalle_pedidoDAO detalleDAO = new Detalle_pedidoDAO();

        List<Pedido> pendientes = new ArrayList<>();
        List<Pedido> completados = new ArrayList<>();
        Map<Integer, List<Detalle_pedido>> detallesPorPedido = new HashMap<>();
        Map<Integer, String> nombresProducto = new HashMap<>();
        Map<Integer, Usuarios> clientesPorId = new HashMap<>();

        try {
            List<Pedido> domicilios = pedidoDAO.listarPorTipoEntrega(TIPO_DOMICILIO);
            for (Pedido p : domicilios) {
                int estado = p.getEstado_pedido_idEstado_pedido();
                if (estado == 4 || estado == 5) {
                    completados.add(p);
                } else {
                    pendientes.add(p);
                }
                detallesPorPedido.put(p.getidPedido(), detalleDAO.listarPorPedido(p.getidPedido()));
            }

            for (Producto prod : new ProductoDAO().listarProductos()) {
                nombresProducto.put(prod.getidProducto(), prod.getnombre_producto());
            }

            UsuariosDAO usuariosDAO = new UsuariosDAO();
            for (Usuarios u : usuariosDAO.listarUsuarios()) {
                clientesPorId.put(u.getidUsuarios(), u);
            }
        } catch (Exception e) {
            request.setAttribute("error", "No se pudieron cargar los pedidos: " + e.getMessage());
        }

        // Los completados más recientes primero, y solo se muestran los últimos 15
        // para no dejar el historial creciendo sin límite en la pantalla.
        if (completados.size() > 15) {
            completados = completados.subList(0, 15);
        }

        request.setAttribute("pendientes", pendientes);
        request.setAttribute("completados", completados);
        request.setAttribute("detallesPorPedido", detallesPorPedido);
        request.setAttribute("nombresProducto", nombresProducto);
        request.setAttribute("clientesPorId", clientesPorId);
        request.setAttribute("idRepartidorSesion", session.getAttribute("idUsuarios"));
        request.setAttribute("firmaEstado", construirFirma(pendientes));

        request.getRequestDispatcher("/PanelRepartidor.jsp").forward(request, response);
    }
}
