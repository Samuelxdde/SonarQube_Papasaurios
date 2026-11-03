/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Pagos;
import Modelo.Roles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Paula Gisedt
 */
public class PagosDAO {
    
    
    public boolean existeEstadoPago(String descripcion, Integer excluirId) throws SQLException {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT 1 FROM Pagos WHERE LOWER(TRIM(estado_pago)) = LOWER(TRIM(?))"
                + (excluirId != null ? " AND idPagos <> ?" : "") + " LIMIT 1";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            if (excluirId != null) ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar estado de pago duplicado: " + e.getMessage());
            throw e;
        }
    }

    public boolean insertarPago (Pagos pago) throws SQLException {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Pagos (estado_pago) VALUES (?)";

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pago.getestado_pago());
            ps.executeUpdate();
            insertado = true;
            System.out.println("Estado del pago insertado correctamente en la base de datos viverobd.");
        } catch (SQLException e) {
            System.out.println("Error al insertar el estado del pago:" + e.getMessage());
        }
        return insertado;
    }

    public Pagos consultarPagos(int idPagos) {
        Pagos pagos = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String querySQL = "SELECT idPagos, estado_pago FROM Pagos WHERE idPagos = ? ";
        try (conexion; PreparedStatement ps = con.prepareStatement(querySQL)) {
            ps.setInt(1, idPagos);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pagos = new Pagos();
                    pagos.setidPagos(rs.getInt(1));
                    pagos.setestado_pago(rs.getString(2));
                }
            }
            return pagos;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return pagos;
        }
    }

    public boolean actualizarPagos(Pagos pago) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Pagos SET estado_pago = ? WHERE idPagos = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pago.getestado_pago());
            ps.setInt(2, pago.getidPagos());

            if (ps.executeUpdate() > 0) {
                actualizado = true;
                System.out.println("Estado de pago actualizado exitosamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el estado de pago: " + e.getMessage());
        }
        return actualizado;
    }


    public boolean eliminarPagos(int id) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Pagos WHERE idPagos = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                eliminado = true;
                System.out.println("Estado de pago eliminado de VIVEROBD.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar el estado de pago: " + e.getMessage());
        }
        return eliminado;
    }
        public List<Pagos> listarPagos() {
    List<Pagos> lista = new ArrayList<>();
    Conexion conexion = new Conexion();
    Connection con = conexion.getConn();
    String sql = "SELECT idPagos, estado_pago FROM Pagos";
    try (conexion; PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Pagos pago = new Pagos();
            pago.setidPagos(rs.getInt(1));
            pago.setestado_pago(rs.getString(2));
            lista.add(pago);
        }
    } catch (Exception e) {
        System.out.println("Error al listar roles: " + e.getMessage());
    }
    return lista;
}

    public double sumarPagosMes() {
        double total = 0;
        String sql = "SELECT SUM(monto) FROM Pagos WHERE MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE())";

        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
}

