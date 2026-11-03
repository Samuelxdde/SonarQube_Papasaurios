package Servlet;

import Controlador.PedidoDAO;
import Controlador.Detalle_pedidoDAO;
import Controlador.DireccionValidator;
import Controlador.HorarioTiendaValidator;
import Modelo.Pedido;
import Modelo.Detalle_pedido;
import Modelo.ItemCarrito;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Checkout: toma el carrito guardado en sesión, lo valida, calcula el
 * total en el servidor (nunca se confía en un total enviado desde el
 * formulario) y lo persiste como un Pedido con sus líneas en
 * Detalle_pedido. Al terminar, vacía el carrito de la sesión.
 */
@WebServlet("/Pedido")
@SuppressWarnings("unchecked")
public class PedidoServlet extends HttpServlet {

    private static final int ESTADO_RECIBIDO = 1;
    private static final int PAGO_PENDIENTE = 1;

    // Pedido mínimo en pesos: evita carritos armados solo con adicionales
    // sueltos (una salsa, un topping) que ni siquiera llegan a este monto.
    private static final int TOTAL_MINIMO = 15000;

    // Categorías que representan un plato completo (no un ingrediente o
    // adicional suelto). El carrito debe tener al menos un producto de
    // alguna de estas categorías para poder confirmar el pedido; así se
    // evita, por ejemplo, un pedido de "una sola salchicha" (categoría 2)
    // sin ningún plato armado.
    // 1 Papas Saurios, 5 Combos, 6 Entradas, 7 Sandwich, 8 Dino Burguer,
    // 9 Dino Dog, 10 Patacones, 11 Alitas BBQ, 12 Dorilocos, 13 Picadas.
    private static final java.util.Set<Integer> CATEGORIAS_PLATO_PRINCIPAL =
            java.util.Set.of(1, 5, 6, 7, 8, 9, 10, 11, 12, 13);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/Carrito");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuarios") == null) {
            response.sendRedirect(request.getContextPath() + "/Iniciar");
            return;
        }

        LinkedHashMap<String, ItemCarrito> carrito =
                (LinkedHashMap<String, ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            request.setAttribute("error", "Tu carrito está vacío.");
            request.getRequestDispatcher("/Vista/Carrito.jsp").forward(request, response);
            return;
        }

        int idUsuario = (Integer) session.getAttribute("idUsuarios");
        String tipoEntrega = request.getParameter("tipo_entrega");
        String direccion = request.getParameter("direccion_entrega");

        // Nunca confiamos solo en la validación del navegador: si es a
        // domicilio, la dirección se valida también aquí — no solo que no
        // esté vacía, sino que tenga forma de dirección real (longitud
        // razonable y al menos un número), para mitigar direcciones falsas
        // o de relleno que dejarían al repartidor sin poder ubicar al
        // cliente.
        if ("Domicilio".equalsIgnoreCase(tipoEntrega)) {
            String errorDireccion = DireccionValidator.validar(direccion);
            if (errorDireccion != null) {
                request.setAttribute("error", errorDireccion);
                request.getRequestDispatcher("/Vista/Carrito.jsp").forward(request, response);
                return;
            }
        }

        try {
            // El total se calcula aquí, sumando lo que de verdad hay en el
            // carrito del servidor — así nadie puede manipular el precio
            // final modificando un campo oculto del formulario.
            int total = 0;
            int cantidadItems = 0;
            boolean tienePlatoPrincipal = false;
            for (ItemCarrito item : carrito.values()) {
                total += item.getSubtotal();
                cantidadItems += item.getCantidad();
                if (CATEGORIAS_PLATO_PRINCIPAL.contains(item.getCategoriaIdCategoria())) {
                    tienePlatoPrincipal = true;
                }
            }

            // El carrito debe tener al menos un plato armado (no solo
            // ingredientes/adicionales sueltos como una salsa o una
            // salchicha) y superar el pedido mínimo.
            if (!tienePlatoPrincipal) {
                request.setAttribute("error",
                        "Tu pedido necesita al menos un plato armado (no solo adicionales sueltos).");
                request.getRequestDispatcher("/Vista/Carrito.jsp").forward(request, response);
                return;
            }
            if (total < TOTAL_MINIMO) {
                request.setAttribute("error",
                        "El pedido mínimo es de $" + TOTAL_MINIMO + ". Te faltan $" + (TOTAL_MINIMO - total) + ".");
                request.getRequestDispatcher("/Vista/Carrito.jsp").forward(request, response);
                return;
            }

            long ahoraMillis = System.currentTimeMillis();

            // Tiempo estimado de preparación en cocina: una base fija más un
            // poco más por cada producto pedido (un pedido grande tarda más
            // que uno de un solo ítem), con un tope para no prometer horas
            // absurdas en pedidos enormes.
            int minutosPreparacion = Math.min(15 + (cantidadItems * 2), 45);

            // Si es a domicilio, se suma el tiempo estimado de trayecto del
            // repartidor una vez el pedido ya está listo en cocina.
            int minutosTransporte = "Domicilio".equalsIgnoreCase(tipoEntrega) ? 20 : 0;

            long horaEstimadaMillis = ahoraMillis + (minutosPreparacion + minutosTransporte) * 60_000L;
            Time horaEstimada = new Time(horaEstimadaMillis);

            // El establecimiento tiene un horario de atención (ver
            // "Horarios de atención" en la página principal): no se puede
            // tomar un pedido si en este momento está cerrado, y si es
            // "Recoger en tienda", la hora estimada en que quedaría listo
            // tampoco puede caer después del cierre — de lo contrario el
            // cliente llegaría a un local ya cerrado.
            ZoneId zonaHoraria = ZoneId.systemDefault();
            LocalDateTime ahoraLocal = Instant.ofEpochMilli(ahoraMillis).atZone(zonaHoraria).toLocalDateTime();
            LocalDateTime horaEstimadaLocal = Instant.ofEpochMilli(horaEstimadaMillis).atZone(zonaHoraria).toLocalDateTime();
            String errorHorario = HorarioTiendaValidator.validar(ahoraLocal, horaEstimadaLocal, tipoEntrega);
            if (errorHorario != null) {
                request.setAttribute("error", errorHorario);
                request.getRequestDispatcher("/Vista/Carrito.jsp").forward(request, response);
                return;
            }

            Pedido pedido = new Pedido();
            pedido.setfecha(new Date(ahoraMillis));
            pedido.sethora(new Time(ahoraMillis));
            pedido.sethora_estimada(horaEstimada);
            pedido.settipo_entrega(tipoEntrega);
            pedido.setdireccion_entrega("Domicilio".equalsIgnoreCase(tipoEntrega) ? direccion.trim() : null);
            pedido.settotal(total);
            pedido.setUsuarios_idUsuarios(idUsuario);
            pedido.setEstado_pedido_idEstado_pedido(ESTADO_RECIBIDO);
            pedido.setPagos_idPagos(PAGO_PENDIENTE);

            PedidoDAO pedidoDAO = new PedidoDAO();
            int idPedidoNuevo = pedidoDAO.insertarPedido(pedido);

            if (idPedidoNuevo == -1) {
                request.setAttribute("error", "No se pudo registrar el pedido. Intenta de nuevo.");
                request.getRequestDispatcher("/Vista/Carrito.jsp").forward(request, response);
                return;
            }

            Detalle_pedidoDAO detalleDAO = new Detalle_pedidoDAO();
            for (ItemCarrito item : carrito.values()) {
                Detalle_pedido detalle = new Detalle_pedido();
                detalle.setcantidad(item.getCantidad());
                detalle.setprecio_unitario(item.getPrecioUnitario());
                detalle.setnota(item.getNota());
                detalle.setPedido_idPedido(idPedidoNuevo);
                detalle.setProducto_idProducto(item.getIdProducto());
                detalle.setProducto_variante_idVariante(item.getIdVariante());
                detalleDAO.insertarDetalle(detalle);
            }

            // Pedido confirmado: el carrito se vacía. Guardamos un resumen
            // en sesión para mostrar una confirmación clara en el panel del
            // usuario (número de pedido, tipo de entrega y a dónde va),
            // en vez de simplemente redirigir sin decir nada.
            session.removeAttribute("carrito");
            session.setAttribute("confPedidoId", idPedidoNuevo);
            session.setAttribute("confTipoEntrega", tipoEntrega);
            session.setAttribute("confDireccion", direccion == null ? "" : direccion);
            session.setAttribute("confTotal", total);
            session.setAttribute("confHoraEstimada", horaEstimada);
            response.sendRedirect(request.getContextPath() + "/PanelUsuario");

        } catch (Exception e) {
            request.setAttribute("error", "Error al confirmar el pedido: " + e.getMessage());
            request.getRequestDispatcher("/Vista/Carrito.jsp").forward(request, response);
        }
    }
}
