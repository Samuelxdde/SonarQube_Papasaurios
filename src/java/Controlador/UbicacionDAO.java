package Controlador;

import Modelo.Ubicacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UbicacionDAO {

    /**
     * Guarda o actualiza la posición del repartidor para un pedido.
     * Es un "upsert": si ya había una fila para ese pedido (lo normal,
     * porque el repartidor manda su posición cada pocos segundos
     * mientras va en camino), la pisa en vez de acumular historial.
     */
    public boolean guardarUbicacion(int idPedido, double lat, double lng) throws SQLException {
        boolean guardado = false;
        String sql = "INSERT INTO Ubicacion_pedido (Pedido_idPedido, latitud, longitud, actualizado) "
                + "VALUES (?, ?, ?, NOW()) "
                + "ON DUPLICATE KEY UPDATE latitud = VALUES(latitud), longitud = VALUES(longitud), actualizado = NOW()";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setDouble(2, lat);
            ps.setDouble(3, lng);
            int filas = ps.executeUpdate();
            guardado = filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar ubicación: " + e.getMessage());
            throw e;
        }
        return guardado;
    }

    public Ubicacion obtenerUbicacion(int idPedido) throws SQLException {
        Ubicacion u = null;
        String sql = "SELECT Pedido_idPedido, latitud, longitud, actualizado FROM Ubicacion_pedido WHERE Pedido_idPedido = ?";
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Ubicacion();
                    u.setidPedido(rs.getInt(1));
                    u.setlatitud(rs.getDouble(2));
                    u.setlongitud(rs.getDouble(3));
                    u.setactualizado(rs.getTimestamp(4));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar ubicación: " + e.getMessage());
            throw e;
        }
        return u;
    }

    public boolean eliminarUbicacion(int idPedido) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Ubicacion_pedido WHERE Pedido_idPedido = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            int filas = ps.executeUpdate();
            eliminado = filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar ubicación: " + e.getMessage());
            throw e;
        }
        return eliminado;
    }
}
