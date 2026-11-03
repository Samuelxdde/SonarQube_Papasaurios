/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Tipo_documento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Usuarios;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;


/**
 *
 * @author Aprendiz
 */
public class UsuariosDAO {

    Conexion conexion = new Conexion();

    public boolean insertarUsuarios(Usuarios usuarios) throws SQLException {
        boolean insertado = false;
        Connection con = conexion.getConn();

        String sql = "INSERT INTO Usuarios (nombre, apellido, documento, telefono, correo, clave, fecha_nac, fecha_cad, checkbox, Tipo_documento_idTipo_documento, Roles_idRoles) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuarios.getnombre());
            ps.setString(2, usuarios.getapellido());
            ps.setString(3, usuarios.getdocumento());
            ps.setString(4, usuarios.gettelefono());
            ps.setString(5, usuarios.getcorreo());
            ps.setString(6, usuarios.getclave());
            ps.setDate(7, new java.sql.Date(usuarios.getfecha_nac().getTime()));
            ps.setDate(8, new java.sql.Date(usuarios.getfecha_cad().getTime()));
            ps.setBoolean(9, usuarios.ischeckbox());
            ps.setInt(10, usuarios.getTipo_documento_idTipo_documento());
            ps.setInt(11, usuarios.getRoles_idRoles());

            ps.executeUpdate();
            insertado = true;

            System.out.println("Usuario insertado con éxito.");
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario." + e.getMessage());
        } finally {
            conexion.close();
        }
        return insertado;
    }

    public Usuarios ConsultaUsuarios(String documento) {
        Usuarios usuario = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String querySQL = "SELECT idUsuarios, nombre, apellido, "
                + "documento, telefono, correo, clave, fecha_nac, fecha_cad, checkbox,"
                + " Tipo_documento_idTipo_documento, Roles_idRoles FROM Usuarios WHERE documento = ? ";
        try (conexion; PreparedStatement ps = con.prepareStatement(querySQL)) {
            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuarios();

                    usuario.setidUsuarios(rs.getInt(1));
                    usuario.setnombre(rs.getString(2));
                    usuario.setapellido(rs.getString(3));
                    usuario.setdocumento(rs.getString(4));
                    usuario.settelefono(rs.getString(5));
                    usuario.setcorreo(rs.getString(6));
                    usuario.setclave(rs.getString(7));
                    usuario.setfecha_nac(rs.getDate(8));
                    usuario.setfecha_cad(rs.getDate(9));
                    usuario.setcheckbox(rs.getBoolean(10));
                    usuario.setTipo_documento_idTipo_documento(rs.getInt(11));
                    usuario.setRoles_idRoles(rs.getInt(12));
                }
            }

            return usuario;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return usuario;
        }
    }

    public boolean actualizarUsuario(Usuarios usuarios) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Usuarios SET nombre=?, apellido=?, documento=?, telefono=?, correo=?, clave=?, fecha_nac=?, fecha_cad=?, checkbox=?, Tipo_documento_idTipo_documento=?, Roles_idRoles=? WHERE idUsuarios=?"; 
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuarios.getnombre());
            ps.setString(2, usuarios.getapellido());
            ps.setString(3, usuarios.getdocumento());
            ps.setString(4, usuarios.gettelefono());
            ps.setString(5, usuarios.getcorreo());
            ps.setString(6, usuarios.getclave());
            ps.setDate(7, new java.sql.Date(usuarios.getfecha_nac().getTime()));
            ps.setDate(8, new java.sql.Date(usuarios.getfecha_cad().getTime()));
            ps.setBoolean(9, usuarios.ischeckbox());
            ps.setInt(10, usuarios.getTipo_documento_idTipo_documento());
            ps.setInt(11, usuarios.getRoles_idRoles());
            ps.setInt(12, usuarios.getidUsuarios());

            int filas = ps.executeUpdate();
            // executeUpdate() devuelve 0 también cuando los valores no cambiaron
            // (el WHERE sí encontró la fila), así que igualmente lo tratamos como éxito.
            actualizado = true;
            System.out.println("Filas afectadas al actualizar usuario " + usuarios.getidUsuarios() + ": " + filas);
        } catch (SQLException e) {
            System.out.println("Error al actualizar el usuario: " + e.getMessage());
            throw e;
        }
        return actualizado;
    }

    public boolean eliminarUsuario(int id) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Usuarios WHERE idUsuarios = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                eliminado = true;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar el usuario: " + e.getMessage());
        }
        return eliminado;
    }

    public boolean existeUsuario(String documento) {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();
        try (conexion; PreparedStatement ps = con.prepareStatement("SELECT documento FROM Usuarios WHERE documento = ?")) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Igual que existeUsuario(documento), pero puede excluir un id — lo
     * usa la edición desde el panel de admin, para no rechazar al propio
     * usuario que se está editando por "chocar" con su mismo documento.
     */
    public boolean existeUsuario(String documento, Integer excluirId) {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();
        String sql = "SELECT 1 FROM Usuarios WHERE documento = ?"
                + (excluirId != null ? " AND idUsuarios <> ?" : "") + " LIMIT 1";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, documento);
            if (excluirId != null) ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * ¿Ya hay otro usuario con este correo? Comparación sin importar
     * mayúsculas — "Ana@Mail.com" y "ana@mail.com" cuentan como el mismo.
     */
    public boolean existeCorreo(String correo, Integer excluirId) {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();
        String sql = "SELECT 1 FROM Usuarios WHERE LOWER(correo) = LOWER(?)"
                + (excluirId != null ? " AND idUsuarios <> ?" : "") + " LIMIT 1";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            if (excluirId != null) ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("Error al verificar correo: " + e.getMessage());
            return false;
        }
    }    
             
    public boolean actualizarClave(Usuarios usuario) throws SQLException {
        String sql = "UPDATE Usuarios SET clave=? WHERE documento=?";
        try (PreparedStatement ps = conexion.getConn().prepareStatement(sql)) {
            ps.setString(1, usuario.getclave());
            ps.setString(2, usuario.getdocumento());
            return ps.executeUpdate() > 0;
        } finally {
            conexion.close();
        }
    }

    /**
     * Igual que actualizarClave, pero busca por correo en vez de documento.
     * La usa la recuperación de contraseña por código, que solo conoce el
     * correo de quien la solicitó.
     */
    public boolean actualizarClavePorCorreo(String correo, String claveHasheada) throws SQLException {
        String sql = "UPDATE Usuarios SET clave=? WHERE LOWER(correo)=LOWER(?)";
        try (PreparedStatement ps = conexion.getConn().prepareStatement(sql)) {
            ps.setString(1, claveHasheada);
            ps.setString(2, correo);
            return ps.executeUpdate() > 0;
        } finally {
            conexion.close();
        }
    }


    public List<Usuarios> listarUsuarios() {
        List<Usuarios> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();
        String sql = "SELECT idUsuarios, nombre, apellido, documento, telefono, correo, clave, fecha_nac, fecha_cad, checkbox, Tipo_documento_idTipo_documento, Roles_idRoles FROM Usuarios";  

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Usuarios usuario = new Usuarios();
                usuario.setidUsuarios(rs.getInt(1));
                usuario.setnombre(rs.getString(2));
                usuario.setapellido(rs.getString(3));
                usuario.setdocumento(rs.getString(4));
                usuario.settelefono(rs.getString(5));
                usuario.setcorreo(rs.getString(6));
                usuario.setclave(rs.getString(7));
                usuario.setfecha_nac(rs.getDate(8));
                usuario.setfecha_cad(rs.getDate(9));
                usuario.setcheckbox(rs.getBoolean(10));
                usuario.setTipo_documento_idTipo_documento(rs.getInt(11));
                usuario.setRoles_idRoles(rs.getInt(12));
                

                lista.add(usuario);
            }
        } catch (Exception e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }
        public int contarUsuarios() {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM Usuarios";
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public Usuarios consultarPorId(int id) {
        Usuarios usuario = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();
        String sql = "SELECT idUsuarios, nombre, apellido, documento, telefono, correo, clave, fecha_nac, fecha_cad, checkbox, Tipo_documento_idTipo_documento, Roles_idRoles FROM Usuarios WHERE idUsuarios = ?";

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                usuario = new Usuarios();
                usuario.setidUsuarios(rs.getInt(1));
                usuario.setnombre(rs.getString(2));
                usuario.setapellido(rs.getString(3));
                usuario.setdocumento(rs.getString(4));
                usuario.settelefono(rs.getString(5));
                usuario.setcorreo(rs.getString(6));
                usuario.setclave(rs.getString(7));
                usuario.setfecha_nac(rs.getDate(8));
                usuario.setfecha_cad(rs.getDate(9));
                usuario.setcheckbox(rs.getBoolean(10));
                usuario.setTipo_documento_idTipo_documento(rs.getInt(11));
                usuario.setRoles_idRoles(rs.getInt(12));
            }
        } catch (Exception e) {
            System.out.println("Error al consultar usuario por id: " + e.getMessage());
        }
        return usuario;
    }

}

