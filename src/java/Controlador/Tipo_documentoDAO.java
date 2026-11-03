/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Tipo_documento;
import java.io.PrintStream;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;



/**
 *
 * @author Aprendiz
 */
public class Tipo_documentoDAO {

    public boolean existeTipoDocumento(String descripcion, Integer excluirId) throws SQLException {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT 1 FROM Tipo_documento WHERE LOWER(TRIM(descripcion_doc)) = LOWER(TRIM(?))"
                + (excluirId != null ? " AND idTipo_documento <> ?" : "") + " LIMIT 1";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            if (excluirId != null) ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar tipo de documento duplicado: " + e.getMessage());
            throw e;
        }
    }

    public boolean insertarTipo_documento(Tipo_documento documento) throws SQLException {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Tipo_documento (descripcion_doc) VALUES (?)";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento.getdescripcion_doc());
            ps.executeUpdate();

            insertado = true;

            System.out.println("Tipo de documento insertado correctamente en viverodb.");

        } catch (SQLException e) {
            System.out.println("Error al insertar el Tipo de documento." + e.getMessage());
        }
        return insertado;
    }
    
    public Tipo_documento ConsultarTipo_documento(int idTipo_documento) throws SQLException {
        Tipo_documento documento = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String querySQL = "SELECT idTipo_documento, descripcion_doc FROM Tipo_documento WHERE idTipo_documento = ? ";
        try (conexion; PreparedStatement ps = con.prepareStatement(querySQL)) {
            ps.setInt(1, idTipo_documento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    documento = new Tipo_documento();
                    documento.setidTipo_documento(rs.getInt(1));
                    documento.setdescripcion_doc(rs.getString(2));
                }
            }
            return documento;

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return documento;
        }
    }
    

    
    public boolean actualizarTipoDocumento(Tipo_documento documento) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Tipo_documento SET descripcion_doc = ? WHERE idTipo_documento = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) { 
            ps.setString(1, documento.getdescripcion_doc());
            ps.setInt(2, documento.getidTipo_documento());

            if (ps.executeUpdate() > 0) {
                actualizado = true;
                System.out.println("Tipo de documento actualizado correctamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
        }
        return actualizado;
    }


    public boolean eliminarTipoDocumento(int id) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Tipo_documento WHERE idTipo_documento = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                eliminado = true;
                System.out.println("Tipo de documento eliminado de viverodb.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
        return eliminado;
    }
    public List<Tipo_documento> listarTipoDocumento() {
    List<Tipo_documento> lista = new ArrayList<>();
    Conexion conexion = new Conexion();
    Connection con = conexion.getConn();
    String sql = "SELECT idTipo_documento, descripcion_doc FROM Tipo_documento";
    try (conexion; PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Tipo_documento doc = new Tipo_documento();
            doc.setidTipo_documento(rs.getInt(1));
            doc.setdescripcion_doc(rs.getString(2));
            lista.add(doc);
        }
    } catch (Exception e) {
        System.out.println("Error al listar tipos de documento: " + e.getMessage());
    }
    return lista;
}
}
