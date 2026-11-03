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
 * Panel de cocina. Es la cola de trabajo de la cocina: todos los
 * pedidos (a domicilio o para recoger en tienda, da igual — a
 * cocina le toca prepararlos de todas formas) que están "Recibido"
 * o "En preparación", del más viejo al más nuevo (FIFO).
 *
 * Solo puede avanzar el estado hacia adelante, un paso a la vez:
 *   Recibido (1) -> En preparación (2) -> Listo (3)
 * No toca tipo de entrega, pagos, ni nada de eso — eso lo sigue
 * viendo el admin desde /PedidoAdmi si lo necesita.
 *
 * Estados de Estado_pedido usados aquí:
 *  1 Recibido, 2 En preparación, 3 Listo, 4 Entregado, 5 Cancelado.
 */
@WebServlet("/PanelCocina")
public class PanelCocinaServlet extends HttpServlet {

    private static final int ROL_COCINA = 4;
    private static final int ROL_REPARTIDOR = 3;
    private static final int RECIBIDO = 1;
    private static final int EN_PREPARACION = 2;
    private static final int LISTO = 3;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("perfil") == null) {
            response.sendRedirect(request.getContextPath() + "/Iniciar");
            return;
        }
        if (!esCocina(session)) {
            response.sendRedirect(request.getContextPath() + "/PanelUsuario");
            return;
        }

        // Endpoint liviano para refrescar solo la tarjeta de "Repartidores
        // trabajando ahora" cada pocos segundos (ver el fetch() en PanelCocina.jsp).
        if ("repartidores".equals(request.getParameter("check"))) {
            responderRepartidoresActivos(response);
            return;
        }

        // Endpoint liviano para el auto-refresco: la página pregunta cada
        // pocos segundos "¿cambió algo?" sin recargar todo. Solo consulta
        // los pedidos activos (recibidos + en preparación), no el resto
        // de listas ni productos, para que sea una consulta barata.
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
        if (session == null || session.getAttribute("perfil") == null || !esCocina(session)) {
            response.sendRedirect(request.getContextPath() + "/Iniciar");
            return;
        }

        PedidoDAO pedidoDAO = new PedidoDAO();
        String accion = request.getParameter("accion");

        if (!Controlador.CsrfUtil.esValido(request)) {
            request.setAttribute("error", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarListasYMostrar(request, response);
            return;
        }

        try {
            int idPedido = Integer.parseInt(request.getParameter("id"));
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            if (pedido == null) {
                request.setAttribute("error", "Ese pedido no existe.");
            } else if ("empezar".equals(accion)) {
                if (pedido.getEstado_pedido_idEstado_pedido() != RECIBIDO) {
                    request.setAttribute("error", "Ese pedido ya no está en \"Recibido\".");
                } else {
                    pedidoDAO.actualizarEstado(idPedido, EN_PREPARACION);
                    request.setAttribute("mensaje", "Pedido #" + idPedido + " en preparación.");
                }
            } else if ("listo".equals(accion)) {
                if (pedido.getEstado_pedido_idEstado_pedido() != EN_PREPARACION) {
                    request.setAttribute("error", "Ese pedido todavía no está \"En preparación\".");
                } else {
                    pedidoDAO.actualizarEstado(idPedido, LISTO);
                    request.setAttribute("mensaje", "Pedido #" + idPedido + " marcado como listo.");
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        cargarListasYMostrar(request, response);
    }

    private boolean esCocina(HttpSession session) {
        Object perfil = session.getAttribute("perfil");
        return perfil != null && (Integer) perfil == ROL_COCINA;
    }

    // "Firma" = una foto simple de qué pedidos activos hay y en qué
    // estado, para poder comparar "¿es lo mismo que la última vez que
    // miré?" sin tener que comparar objetos completos.
    private String construirFirma(List<Pedido> recibidos, List<Pedido> enPreparacion) {
        StringBuilder sb = new StringBuilder();
        for (Pedido p : recibidos) {
            sb.append('r').append(p.getidPedido()).append(',');
        }
        for (Pedido p : enPreparacion) {
            sb.append('p').append(p.getidPedido()).append(',');
        }
        return sb.toString();
    }

    private void responderFirmaEstado(HttpServletResponse response) throws IOException {
        PedidoDAO pedidoDAO = new PedidoDAO();
        String firma;
        try {
            firma = construirFirma(
                    pedidoDAO.listarPorEstado(RECIBIDO),
                    pedidoDAO.listarPorEstado(EN_PREPARACION)
            );
        } catch (Exception e) {
            // Si la consulta falla, se manda una firma que nunca va a
            // coincidir con la anterior, así el navegador termina
            // recargando la página y el usuario ve el error ahí, en vez
            // de quedarse pegado en un estado viejo sin darse cuenta.
            firma = "error-" + System.currentTimeMillis();
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"firma\":\"" + firma.replace("\"", "") + "\"}");
    }

    private void cargarListasYMostrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PedidoDAO pedidoDAO = new PedidoDAO();
        Detalle_pedidoDAO detalleDAO = new Detalle_pedidoDAO();

        List<Pedido> recibidos = new ArrayList<>();
        List<Pedido> enPreparacion = new ArrayList<>();
        List<Pedido> listosRecientes = new ArrayList<>();
        Map<Integer, List<Detalle_pedido>> detallesPorPedido = new HashMap<>();
        Map<Integer, String> nombresProducto = new HashMap<>();

        try {
            recibidos = pedidoDAO.listarPorEstado(RECIBIDO);
            enPreparacion = pedidoDAO.listarPorEstado(EN_PREPARACION);
            listosRecientes = pedidoDAO.listarPorEstado(LISTO);

            // Cocina ya cumplió su parte con estos; se muestran solo como
            // referencia reciente, más nuevos primero, sin dejar crecer
            // la lista indefinidamente en la pantalla.
            java.util.Collections.reverse(listosRecientes);
            if (listosRecientes.size() > 10) {
                listosRecientes = listosRecientes.subList(0, 10);
            }

            for (Pedido p : recibidos) {
                detallesPorPedido.put(p.getidPedido(), detalleDAO.listarPorPedido(p.getidPedido()));
            }
            for (Pedido p : enPreparacion) {
                detallesPorPedido.put(p.getidPedido(), detalleDAO.listarPorPedido(p.getidPedido()));
            }
            for (Pedido p : listosRecientes) {
                detallesPorPedido.put(p.getidPedido(), detalleDAO.listarPorPedido(p.getidPedido()));
            }

            for (Producto prod : new ProductoDAO().listarProductos()) {
                nombresProducto.put(prod.getidProducto(), prod.getnombre_producto());
            }
        } catch (Exception e) {
            request.setAttribute("error", "No se pudieron cargar los pedidos: " + e.getMessage());
        }

        request.setAttribute("recibidos", recibidos);
        request.setAttribute("enPreparacion", enPreparacion);
        request.setAttribute("listosRecientes", listosRecientes);
        request.setAttribute("detallesPorPedido", detallesPorPedido);
        request.setAttribute("nombresProducto", nombresProducto);
        request.setAttribute("repartidoresActivos", listarRepartidoresActivos());
        // Firma "de arranque" para que el JS de auto-refresco en el JSP
        // sepa contra qué comparar la primera vez, sin recargar de
        // inmediato al abrir la página.
        request.setAttribute("firmaEstado", construirFirma(recibidos, enPreparacion));

        request.getRequestDispatcher("/PanelCocina.jsp").forward(request, response);
    }

    // Repartidores (rol 3) cuyo id está en el registro de sesiones
    // activas (RepartidoresActivosRegistro): están con el panel abierto
    // ahora mismo, es decir, trabajando.
    private List<Usuarios> listarRepartidoresActivos() {
        List<Integer> idsActivos = Controlador.RepartidoresActivosRegistro.idsActivos();
        List<Usuarios> resultado = new ArrayList<>();
        try {
            for (Usuarios u : new UsuariosDAO().listarUsuarios()) {
                if (u.getRoles_idRoles() == ROL_REPARTIDOR && idsActivos.contains(u.getidUsuarios())) {
                    resultado.add(u);
                }
            }
        } catch (Exception e) {
            // Si falla la consulta, simplemente no se muestra nadie; el
            // resto del panel de cocina sigue funcionando con normalidad.
        }
        return resultado;
    }

    // Misma idea que listarRepartidoresActivos(), pero como respuesta
    // JSON liviana para el auto-refresco de la tarjeta.
    private void responderRepartidoresActivos(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<String> objetos = new ArrayList<>();
        for (Usuarios u : listarRepartidoresActivos()) {
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
