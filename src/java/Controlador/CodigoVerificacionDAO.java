package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.security.SecureRandom;

/**
 * Genera y valida códigos de un solo uso enviados por correo, tanto para
 * verificar que un correo existe/es del dueño de la cuenta (registro) como
 * para la recuperación de contraseña.
 *
 * Cada código tiene un "tipo" ("registro" o "recuperacion") porque el mismo
 * correo puede estar pidiendo los dos al mismo tiempo (por ejemplo si deja
 * a medias el registro y luego intenta recuperar una clave vieja).
 */
public class CodigoVerificacionDAO {

    private static final int MINUTOS_VALIDEZ = 10;
    private static final int MAX_INTENTOS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Genera un código de 6 dígitos, lo guarda y lo devuelve para enviarlo por correo. */
    public String generarCodigo(String correo, String tipo) throws SQLException {
        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        Timestamp expira = new Timestamp(System.currentTimeMillis() + MINUTOS_VALIDEZ * 60_000L);

        // Invalida códigos anteriores del mismo correo/tipo que sigan pendientes,
        // así solo el último código enviado es válido.
        String invalidarAnteriores = "UPDATE Codigo_verificacion SET usado = TRUE "
                + "WHERE correo = ? AND tipo = ? AND usado = FALSE";
        String insertar = "INSERT INTO Codigo_verificacion (correo, codigo, tipo, expira) VALUES (?, ?, ?, ?)";

        try (Conexion conexion = new Conexion()) {
            Connection con = conexion.getConn();

            try (PreparedStatement psInvalidar = con.prepareStatement(invalidarAnteriores)) {
                psInvalidar.setString(1, correo);
                psInvalidar.setString(2, tipo);
                psInvalidar.executeUpdate();
            }
            try (PreparedStatement psInsertar = con.prepareStatement(insertar)) {
                psInsertar.setString(1, correo);
                psInsertar.setString(2, codigo);
                psInsertar.setString(3, tipo);
                psInsertar.setTimestamp(4, expira);
                psInsertar.executeUpdate();
            }
        }
        return codigo;
    }

    /**
     * Valida el código ingresado contra el último que se generó para ese
     * correo/tipo. Si es correcto lo marca como usado (para que no sirva
     * dos veces) y devuelve null. Si no, devuelve un mensaje de error listo
     * para mostrarle al usuario.
     */
    public String validarCodigo(String correo, String codigoIngresado, String tipo) throws SQLException {
        String sql = "SELECT idCodigo, codigo, expira, usado, intentos FROM Codigo_verificacion "
                + "WHERE correo = ? AND tipo = ? ORDER BY creado DESC LIMIT 1";

        try (Conexion conexion = new Conexion()) {
            Connection con = conexion.getConn();

            int idCodigo;
            String codigoGuardado;
            Timestamp expira;
            boolean usado;
            int intentos;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, correo);
                ps.setString(2, tipo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return "No se ha solicitado un código para este correo.";
                    }
                    idCodigo = rs.getInt("idCodigo");
                    codigoGuardado = rs.getString("codigo");
                    expira = rs.getTimestamp("expira");
                    usado = rs.getBoolean("usado");
                    intentos = rs.getInt("intentos");
                }
            }

            if (usado) {
                return "Este código ya fue utilizado o quedó invalidado. Solicita uno nuevo.";
            }
            if (expira.before(new Timestamp(System.currentTimeMillis()))) {
                return "El código expiró. Solicita uno nuevo.";
            }
            if (intentos >= MAX_INTENTOS) {
                return "Demasiados intentos fallidos. Solicita un código nuevo.";
            }
            if (!codigoGuardado.equals(codigoIngresado)) {
                try (PreparedStatement psIntento = con.prepareStatement(
                        "UPDATE Codigo_verificacion SET intentos = intentos + 1 WHERE idCodigo = ?")) {
                    psIntento.setInt(1, idCodigo);
                    psIntento.executeUpdate();
                }
                return "El código ingresado no es correcto.";
            }

            try (PreparedStatement psUsado = con.prepareStatement(
                    "UPDATE Codigo_verificacion SET usado = TRUE WHERE idCodigo = ?")) {
                psUsado.setInt(1, idCodigo);
                psUsado.executeUpdate();
            }
            return null; // código válido
        }
    }
}
