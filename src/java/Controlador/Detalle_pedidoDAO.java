package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Detalle_pedido;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Detalle_pedidoDAO {

    private Detalle_pedido mapear(ResultSet rs) throws SQLException {
        Detalle_pedido d = new Detalle_pedido();
        d.setidDetalle(rs.getInt(1));
        d.setcantidad(rs.getInt(2));
        d.setprecio_unitario(rs.getInt(3));
        d.setnota(rs.getString(4));
        d.setPedido_idPedido(rs.getInt(5));
        d.setProducto_idProducto(rs.getInt(6));
        int variante = rs.getInt(7);
        d.setProducto_variante_idVariante(rs.wasNull() ? null : variante);
        return d;
    }

    private static final String COLUMNAS =
        "idDetalle, cantidad, precio_unitario, nota, Pedido_idPedido, " +
        "Producto_idProducto, Producto_variante_idVariante";

    public boolean insertarDetalle(Detalle_pedido d) throws SQLException {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Detalle_pedido (cantidad, precio_unitario, nota, Pedido_idPedido, Producto_idProducto, Producto_variante_idVariante) VALUES (?, ?, ?, ?, ?, ?)";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getcantidad());
            ps.setInt(2, d.getprecio_unitario());
            ps.setString(3, d.getnota());
            ps.setInt(4, d.getPedido_idPedido());
            ps.setInt(5, d.getProducto_idProducto());
            if (d.getProducto_variante_idVariante() != null) {
                ps.setInt(6, d.getProducto_variante_idVariante());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            insertado = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar detalle de pedido: " + e.getMessage());
            throw e;
        }
        return insertado;
    }

    public boolean eliminarDetalle(int idDetalle) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Detalle_pedido WHERE idDetalle = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDetalle);
            int filas = ps.executeUpdate();
            if (filas > 0) eliminado = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar detalle de pedido: " + e.getMessage());
            throw e;
        }
        return eliminado;
    }

    /**
     * Todas las líneas (productos) que componen un pedido específico.
     * Es lo que muestra el detalle de "Mis pedidos" y la vista admin.
     */
    public List<Detalle_pedido> listarPorPedido(int idPedido) throws SQLException {
        List<Detalle_pedido> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT " + COLUMNAS + " FROM Detalle_pedido WHERE Pedido_idPedido = ?";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar detalle del pedido: " + e.getMessage());
            throw e;
        }
        return lista;
    }
}
