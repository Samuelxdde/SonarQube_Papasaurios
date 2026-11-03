/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Roles;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;


public class RolesDAO {

    public boolean existeRol(String descripcion, Integer excluirId) throws SQLException {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT 1 FROM Roles WHERE LOWER(TRIM(descripcion_rol)) = LOWER(TRIM(?))"
                + (excluirId != null ? " AND idRoles <> ?" : "") + " LIMIT 1";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            if (excluirId != null) ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar rol duplicado: " + e.getMessage());
            throw e;
        }
    }

    public boolean insertarRol(Roles rol) throws SQLException {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Roles (descripcion_rol) VALUES (?)";

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol.getdescripcion_rol());
            ps.executeUpdate();
            insertado = true;
            System.out.println("Rol insertado correctamente en la base de datos viverobd.");
        } catch (SQLException e) {
            System.out.println("Error al insertar rol:" + e.getMessage());
        }
        return insertado;
    }

    
    public Roles ConsultarRoles(int idRoles) {
        Roles roles = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String querySQL = "SELECT idRoles, descripcion_rol FROM Roles WHERE idRoles = ?";
        try (conexion; PreparedStatement ps = con.prepareStatement(querySQL)) {
            ps.setInt(1, idRoles);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    roles = new Roles();
                    roles.setidRoles(rs.getInt(1));
                    roles.setdescripcion_rol(rs.getString(2));
                }
            }
            return roles;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return roles;
        }

    }

    public boolean actualizarRol(Roles rol) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Roles SET descripcion_rol = ? WHERE idRoles = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol.getdescripcion_rol());
            ps.setInt(2, rol.getidRoles());

            if (ps.executeUpdate() > 0) {
                actualizado = true;
                System.out.println("Rol actualizado exitosamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar rol: " + e.getMessage());
        }
        return actualizado;
    }


    public boolean eliminarRol(int id) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Roles WHERE idRoles = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                eliminado = true;
                System.out.println("Rol eliminado de VIVEROBD.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar rol: " + e.getMessage());
        }
        return eliminado;
    }
    public List<Roles> listarRoles() {
    List<Roles> lista = new ArrayList<>();
    Conexion conexion = new Conexion();
    Connection con = conexion.getConn();
    String sql = "SELECT idRoles, descripcion_rol FROM Roles";
    try (conexion; PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Roles rol = new Roles();
            rol.setidRoles(rs.getInt(1));
            rol.setdescripcion_rol(rs.getString(2));
            lista.add(rol);
        }
    } catch (Exception e) {
        System.out.println("Error al listar roles: " + e.getMessage());
    }
    return lista;
}
}
