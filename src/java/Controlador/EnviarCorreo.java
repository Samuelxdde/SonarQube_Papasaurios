package Controlador;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class EnviarCorreo {

    // La API key se lee de una variable de entorno (Railway → Variables → BREVO_API_KEY).
    // Se consigue en Brevo: Menu (arriba a la derecha) -> SMTP & API -> API Keys -> Generate a new API key.
    private static final String BREVO_API_KEY = System.getenv("BREVO_API_KEY");

    // Remitente que aparece en el correo. En el plan gratuito de Brevo puedes usar
    // el correo con el que te registraste (debe quedar verificado como "sender" en
    // Brevo: Senders, Domains & Dedicated IPs -> Senders -> Add a sender).
    // Si más adelante verificas un dominio propio (ej. papasaurios.com), cámbialo por
    // algo como "no-reply@papasaurios.com".
    private static final String REMITENTE_EMAIL = System.getenv("BREVO_SENDER_EMAIL");
    private static final String REMITENTE_NOMBRE = "Papasaurios";

    private static final HttpClient CLIENTE = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static boolean enviar(String destinatario, String asunto, String cuerpo) {
        if (BREVO_API_KEY == null) {
            System.err.println("EnviarCorreo: falta la variable de entorno "
                    + "BREVO_API_KEY, no se puede enviar el correo.");
            return false;
        }
        if (REMITENTE_EMAIL == null) {
            System.err.println("EnviarCorreo: falta la variable de entorno "
                    + "BREVO_SENDER_EMAIL, no se puede enviar el correo.");
            return false;
        }

        String json = construirJson(destinatario, asunto, cuerpo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("api-key", BREVO_API_KEY)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = CLIENTE.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Correo enviado correctamente a " + destinatario);
                return true;
            } else {
                System.err.println("EnviarCorreo: fallo al enviar a " + destinatario
                        + " -> HTTP " + response.statusCode() + " " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.err.println("EnviarCorreo: fallo al enviar a " + destinatario + " -> " + e.getMessage());
            return false;
        }
    }

    // Escapa comillas y saltos de línea para armar el JSON a mano sin depender
    // de ninguna librería externa (no hace falta agregar Gson/Jackson).
    // "cuerpoHtml" ahora es HTML (no texto plano), para correos con diseño.
    private static String construirJson(String destinatario, String asunto, String cuerpoHtml) {
        return "{"
                + "\"sender\":{\"name\":\"" + escapar(REMITENTE_NOMBRE) + "\",\"email\":\"" + escapar(REMITENTE_EMAIL) + "\"},"
                + "\"to\":[{\"email\":\"" + escapar(destinatario) + "\"}],"
                + "\"subject\":\"" + escapar(asunto) + "\","
                + "\"htmlContent\":\"" + escapar(cuerpoHtml) + "\""
                + "}";
    }

    private static String escapar(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
