package Controlador;

import java.text.Normalizer;

/**
 * Valida el número de documento según el tipo de documento seleccionado.
 *
 * Antes no existía ningún control de longitud: se podía elegir "Cédula de
 * Ciudadanía" e ingresar un número de 20+ dígitos sin que nada lo impidiera
 * (ni el formulario, ni el servlet, ni la base de datos, que solo tiene un
 * VARCHAR(45) sin restricción por tipo).
 *
 * Este validador se apoya en el texto de descripcion_doc (no en el id, que
 * es autoincremental y puede cambiar si el admin edita/reordena los tipos
 * desde Tipodocumentoadmi.jsp) para decidir qué regla aplicar.
 */
public class DocumentoValidator {

    private DocumentoValidator() {
    }

    /**
     * @param descripcionTipo texto de Tipo_documento.descripcion_doc (p. ej. "Cédula de Ciudadanía")
     * @param documento número de documento ingresado
     * @return null si es válido, o un mensaje de error listo para mostrar al usuario
     */
    public static String validar(String descripcionTipo, String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            return "El documento es obligatorio.";
        }
        String doc = documento.trim();
        String tipoNormalizado = normalizar(descripcionTipo);

        if (tipoNormalizado.contains("cedula de ciudadania")) {
            if (!doc.matches("\\d+")) {
                return "La cédula de ciudadanía solo debe contener números.";
            }
            if (doc.length() < 6 || doc.length() > 10) {
                return "La cédula de ciudadanía debe tener entre 6 y 10 dígitos.";
            }
        } else if (tipoNormalizado.contains("tarjeta de identidad")) {
            if (!doc.matches("\\d+")) {
                return "La tarjeta de identidad solo debe contener números.";
            }
            if (doc.length() < 8 || doc.length() > 11) {
                return "La tarjeta de identidad debe tener entre 8 y 11 dígitos.";
            }
        } else if (tipoNormalizado.contains("cedula de extranjeria")) {
            if (!doc.matches("[A-Za-z0-9]+")) {
                return "La cédula de extranjería solo debe contener letras y números.";
            }
            if (doc.length() < 6 || doc.length() > 15) {
                return "La cédula de extranjería debe tener entre 6 y 15 caracteres.";
            }
        } else {
            // Tipo de documento no reconocido (p. ej. uno nuevo creado desde el
            // panel admin): igual se pone un tope razonable para que la BD no
            // termine guardando cadenas absurdamente largas.
            if (doc.length() > 20) {
                return "El documento no debe superar los 20 caracteres.";
            }
        }
        return null;
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().trim();
    }
}
