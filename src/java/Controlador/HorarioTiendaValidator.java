package Controlador;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

/**
 * Horario de atención del establecimiento (el mismo que se muestra en
 * index.jsp, sección "Horarios de atención"): martes a sábado, de
 * 11:00 AM a 7:00 PM. Domingos y lunes permanece cerrado.
 *
 * Antes el checkout (PedidoServlet) no comprobaba nada de esto: se podía
 * confirmar un pedido a cualquier hora de cualquier día, incluyendo un
 * pedido "para recoger en tienda" cuya hora estimada de entrega caía
 * después del cierre — el cliente terminaba yendo a un local cerrado.
 */
public final class HorarioTiendaValidator {

    private HorarioTiendaValidator() {
    }

    /**
     * Interruptor para demos/sustentaciones fuera del horario real de
     * atención: en true, el validador siempre deja pasar el pedido sin
     * comprobar día ni hora. IMPORTANTE: volver a poner en false antes
     * de desplegar a producción, o la tienda "nunca cerrará".
     */
    private static final boolean MODO_DEMO = true;

    private static final Set<DayOfWeek> DIAS_ABIERTO = EnumSet.of(
            DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

    private static final LocalTime HORA_APERTURA = LocalTime.of(11, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(19, 0);

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("h:mm a");

    /**
     * @param momento fecha y hora a comprobar
     * @return true si el establecimiento está abierto en ese momento
     */
    public static boolean estaAbierto(LocalDateTime momento) {
        if (MODO_DEMO) {
            return true;
        }
        LocalTime hora = momento.toLocalTime();
        return DIAS_ABIERTO.contains(momento.getDayOfWeek())
                && !hora.isBefore(HORA_APERTURA)
                && hora.isBefore(HORA_CIERRE);
    }

    /**
     * Valida que se pueda tomar un pedido en este momento y, si es para
     * recoger en tienda, que la hora estimada de entrega no supere el
     * horario de cierre del establecimiento.
     *
     * @param ahora momento en el que se está confirmando el pedido
     * @param horaEstimadaEntrega fecha y hora completas en las que el
     *        pedido estaría listo (no solo la hora, para comparar bien
     *        contra el cierre aunque la preparación cruce la medianoche)
     * @param tipoEntrega "Recoger en tienda" o "Domicilio"
     * @return null si es válido, o un mensaje de error listo para
     *         mostrarle al cliente
     */
    public static String validar(LocalDateTime ahora, LocalDateTime horaEstimadaEntrega, String tipoEntrega) {
        if (!estaAbierto(ahora)) {
            return "En este momento el establecimiento está cerrado. Nuestro horario de atención es "
                    + "martes a sábado, de " + FORMATO_HORA.format(HORA_APERTURA) + " a "
                    + FORMATO_HORA.format(HORA_CIERRE) + ".";
        }

        if (!MODO_DEMO && "Recoger en tienda".equalsIgnoreCase(tipoEntrega)) {
            boolean mismoDia = horaEstimadaEntrega.toLocalDate().isEqual(ahora.toLocalDate());
            LocalTime horaEntrega = horaEstimadaEntrega.toLocalTime();
            if (!mismoDia || horaEntrega.isAfter(HORA_CIERRE) || horaEntrega.equals(HORA_CIERRE)) {
                return "Tu pedido no alcanzaría a estar listo antes de que cerremos ("
                        + FORMATO_HORA.format(HORA_CIERRE)
                        + "). Prueba con un pedido más pequeño o vuelve dentro de nuestro horario de atención.";
            }
        }

        return null;
    }
}
