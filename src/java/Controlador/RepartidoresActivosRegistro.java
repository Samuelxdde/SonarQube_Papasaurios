package Controlador;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Lleva el registro de qué repartidores tienen el Panel repartidor
 * abierto "en este momento" (sesión activa = trabajando).
 *
 * No usa la base de datos a propósito: es información de vida muy
 * corta (segundos), no algo que tenga sentido guardar de forma
 * permanente ni consultar con SQL. Vive en memoria del servidor,
 * en un mapa idUsuario -> "hace cuánto se supo de él por última vez".
 *
 * ¿Cómo se entera de que alguien sigue activo? PanelRepartidorServlet
 * llama a marcarActivo(idUsuario) en cada petición que hace ese
 * repartidor: al cargar la página, al tomar/entregar/cancelar un
 * pedido, y — lo más importante — en el ping de auto-refresco que la
 * página ya hace sola cada 8 segundos (ver PanelRepartidor.jsp). Así,
 * mientras tenga la pestaña abierta, sigue "vivo" en este registro sin
 * que tengamos que inventar un mecanismo aparte.
 *
 * ¿Cuándo se considera que ya no está activo? Si no se sabe nada de
 * él en más de UMBRAL_MS (45 segundos: bastante más que los 8
 * segundos del ping, para tolerar que se demore una petición o que
 * pierda señal un instante sin que "parpadee" en el panel del admin/
 * cocina). También se le da de baja al instante si cierra sesión
 * (ver CerrarSesion.java).
 *
 * Nota: al ser en memoria del proceso, si la app corre en más de una
 * instancia (varios servidores detrás de un balanceador) cada una
 * tendría su propia foto parcial. Para un solo servidor (el caso de
 * este proyecto en Railway) funciona tal cual.
 */
public class RepartidoresActivosRegistro {

    private static final long UMBRAL_MS = 45_000;

    private static final Map<Integer, Long> ultimaActividad = new ConcurrentHashMap<>();

    private RepartidoresActivosRegistro() {
    }

    /** Llamar cada vez que sepamos que este repartidor sigue con la página abierta. */
    public static void marcarActivo(int idUsuario) {
        ultimaActividad.put(idUsuario, System.currentTimeMillis());
    }

    /** Llamar al cerrar sesión, para que desaparezca de inmediato y no haya que esperar el umbral. */
    public static void marcarInactivo(int idUsuario) {
        ultimaActividad.remove(idUsuario);
    }

    /** Ids de los repartidores considerados activos ahora mismo (dentro del umbral). */
    public static List<Integer> idsActivos() {
        long ahora = System.currentTimeMillis();
        return ultimaActividad.entrySet().stream()
                .filter(e -> (ahora - e.getValue()) <= UMBRAL_MS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
