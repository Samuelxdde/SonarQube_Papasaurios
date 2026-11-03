package Controlador;

import java.util.List;
import java.util.Map;

/**
 * Utilidad mínima para armar respuestas JSON a mano, sin depender de
 * ninguna librería externa (el proyecto no tenía Gson/Jackson/org.json
 * en WEB-INF/lib). Alcanza perfectamente para las respuestas simples
 * que necesita la app de Flutter — objetos y listas planas, sin
 * anidamiento profundo.
 *
 * Uso típico:
 *   JsonUtil.objeto(
 *       "idProducto", 1,
 *       "nombre", "Dino",
 *       "precioBase", 32000,
 *       "disponible", true
 *   )
 */
public class JsonUtil {

    private JsonUtil() {
    }

    /** Escapa comillas, backslashes y saltos de línea para que el texto no rompa el JSON. */
    public static String escapar(String texto) {
        if (texto == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /** Convierte un valor Java suelto (String, Number, Boolean, null) a su forma JSON. */
    private static String valorJson(Object valor) {
        if (valor == null) return "null";
        if (valor instanceof String) {
            String texto = (String) valor;
            // Convención interna: un String que empieza con "@raw:" ya es
            // JSON armado de antemano (por ejemplo, un arreglo anidado de
            // variantes) y se inserta tal cual, sin escaparlo como texto.
            if (texto.startsWith("@raw:")) {
                return texto.substring("@raw:".length());
            }
            return "\"" + escapar(texto) + "\"";
        }
        if (valor instanceof Boolean || valor instanceof Number) return String.valueOf(valor);
        // Cualquier otro tipo (por si acaso) se trata como texto.
        return "\"" + escapar(String.valueOf(valor)) + "\"";
    }

    /**
     * Arma un objeto JSON a partir de pares clave/valor alternados:
     * objeto("nombre", "Dino", "precio", 32000, "disponible", true)
     */
    public static String objeto(Object... paresClaveValor) {
        if (paresClaveValor.length % 2 != 0) {
            throw new IllegalArgumentException("Se esperaban pares clave/valor (cantidad par de argumentos)");
        }
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < paresClaveValor.length; i += 2) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapar(String.valueOf(paresClaveValor[i]))).append("\":");
            sb.append(valorJson(paresClaveValor[i + 1]));
        }
        return sb.append("}").toString();
    }

    /** Igual que {@link #objeto}, pero recibiendo un Map ya armado (útil para construir objetos dinámicamente). */
    public static String objetoDesdeMapa(Map<String, Object> campos) {
        StringBuilder sb = new StringBuilder("{");
        boolean primero = true;
        for (Map.Entry<String, Object> e : campos.entrySet()) {
            if (!primero) sb.append(",");
            primero = false;
            sb.append("\"").append(escapar(e.getKey())).append("\":");
            sb.append(valorJson(e.getValue()));
        }
        return sb.append("}").toString();
    }

    /** Arma un arreglo JSON a partir de objetos JSON ya armados (strings con "{...}" cada uno). */
    public static String arregloDeObjetos(List<String> objetosJson) {
        return "[" + String.join(",", objetosJson) + "]";
    }
}
