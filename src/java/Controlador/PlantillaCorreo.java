package Controlador;

/**
 * Genera el HTML de los correos transaccionales de Papasaurios (código de
 * verificación de registro y código de recuperación de contraseña), usando
 * la misma paleta de colores del sitio (Vista/Css/style.css).
 */
public class PlantillaCorreo {

    private static final String DORADO = "#e3702b";
    private static final String VERDE_OSCURO = "#1c2e23";
    private static final String CREMA = "#f7ecd8";

    public static String codigoVerificacion(String codigo, String tituloInterno, String textoIntro) {
        return "<!DOCTYPE html>"
            + "<html><body style=\"margin:0;padding:0;background-color:" + CREMA + ";font-family:Arial,Helvetica,sans-serif;\">"
            + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:" + CREMA + ";padding:24px 0;\">"
            + "<tr><td align=\"center\">"
            + "<table role=\"presentation\" width=\"420\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 14px rgba(28,46,35,0.12);\">"

            // Encabezado
            + "<tr><td style=\"background-color:" + VERDE_OSCURO + ";padding:24px 32px;text-align:center;\">"
            + "<span style=\"font-size:22px;font-weight:bold;color:" + CREMA + ";letter-spacing:0.5px;\">&#129430; PAPASAURIOS</span>"
            + "</td></tr>"

            // Cuerpo
            + "<tr><td style=\"padding:32px;\">"
            + "<h2 style=\"margin:0 0 12px 0;color:" + VERDE_OSCURO + ";font-size:18px;\">" + tituloInterno + "</h2>"
            + "<p style=\"margin:0 0 24px 0;color:#4a4a4a;font-size:14px;line-height:1.5;\">" + textoIntro + "</p>"

            // Caja del código
            + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
            + "<tr><td align=\"center\" style=\"background-color:" + CREMA + ";border-radius:8px;padding:18px;\">"
            + "<span style=\"font-size:32px;font-weight:bold;letter-spacing:8px;color:" + DORADO + ";\">" + codigo + "</span>"
            + "</td></tr></table>"

            + "<p style=\"margin:24px 0 0 0;color:#7a6a5a;font-size:13px;line-height:1.5;\">"
            + "Este código vence en <strong>10 minutos</strong>. Si tú no solicitaste esto, puedes ignorar este correo con tranquilidad."
            + "</p>"
            + "</td></tr>"

            // Pie de página
            + "<tr><td style=\"background-color:" + CREMA + ";padding:16px 32px;text-align:center;\">"
            + "<span style=\"font-size:11px;color:#7a6a5a;\">Papasaurios &middot; Salchipapería &middot; DG 34 #13A-40, Soacha</span>"
            + "</td></tr>"

            + "</table></td></tr></table>"
            + "</body></html>";
    }

    public static String codigoRegistro(String codigo) {
        return codigoVerificacion(
            codigo,
            "Verifica tu correo",
            "Usa este código para confirmar tu correo y completar tu registro en Papasaurios:"
        );
    }

    public static String codigoRecuperacion(String codigo) {
        return codigoVerificacion(
            codigo,
            "Recupera tu contraseña",
            "Recibimos una solicitud para restablecer tu contraseña. Usa este código para continuar:"
        );
    }
}
