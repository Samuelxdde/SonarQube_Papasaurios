package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Estado_pedido;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Estado_pedidoDAO {

    public boolean insertarEstadoPedido(Estado_pedido e) throws SQLException {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Estado_pedido (descripcion_esta) VALUES (?)";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getdescripcion_esta());
            ps.executeUpdate();
            insertado = true;
        } catch (SQLException ex) {
            System.out.println("Error al insertar estado de pedido: " + ex.getMessage());
            throw ex;
        }
        return insertado;
    }

    public Estado_pedido consultarPorId(int id) throws SQLException {
        Estado_pedido e = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idEstado_pedido, descripcion_esta FROM Estado_pedido WHERE idEstado_pedido = ?";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    e = new Estado_pedido();
                    e.setidEstado_pedido(rs.getInt(1));
                    e.setdescripcion_esta(rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al consultar estado de pedido: " + ex.getMessage());
            throw ex;
        }
        return e;
    }

    public boolean actualizarEstadoPedido(Estado_pedido e) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Estado_pedido SET descripcion_esta = ? WHERE idEstado_pedido = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getdescripcion_esta());
            ps.setInt(2, e.getidEstado_pedido());
            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;
        } catch (SQLException ex) {
            System.out.println("Error al actualizar estado de pedido: " + ex.getMessage());
            throw ex;
        }
        return actualizado;
    }

    public boolean eliminarEstadoPedido(int id) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Estado_pedido WHERE idEstado_pedido = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) eliminado = true;
        } catch (SQLException ex) {
            System.out.println("Error al eliminar estado de pedido: " + ex.getMessage());
            throw ex;
        }
        return eliminado;
    }

    public List<Estado_pedido> listarEstados() throws SQLException {
        List<Estado_pedido> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idEstado_pedido, descripcion_esta FROM Estado_pedido";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Estado_pedido e = new Estado_pedido();
                e.setidEstado_pedido(rs.getInt(1));
                e.setdescripcion_esta(rs.getString(2));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar estados de pedido: " + ex.getMessage());
            throw ex;
        }
        return lista;
    }
}
