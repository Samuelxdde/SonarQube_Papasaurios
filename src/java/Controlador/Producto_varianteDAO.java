package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Producto_variante;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Producto_varianteDAO {

    private Producto_variante mapear(ResultSet rs) throws SQLException {
        Producto_variante v = new Producto_variante();
        v.setidVariante(rs.getInt(1));
        v.setnombre_variante(rs.getString(2));
        v.setprecio_variante(rs.getInt(3));
        v.setProducto_idProducto(rs.getInt(4));
        return v;
    }

    /**
     * ¿Ya existe una variante con este nombre PARA ESE MISMO PRODUCTO?
     * A propósito no es una unicidad global: dos productos distintos sí
     * pueden tener cada uno su propia variante "Grande", por ejemplo.
     */
    public boolean existeVariante(String nombre, int idProducto, Integer excluirId) throws SQLException {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT 1 FROM Producto_variante WHERE LOWER(TRIM(nombre_variante)) = LOWER(TRIM(?)) AND Producto_idProducto = ?"
                + (excluirId != null ? " AND idVariante <> ?" : "") + " LIMIT 1";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idProducto);
            if (excluirId != null) ps.setInt(3, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar variante duplicada: " + e.getMessage());
            throw e;
        }
    }

    public boolean insertarVariante(Producto_variante v) throws SQLException {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Producto_variante (nombre_variante, precio_variante, Producto_idProducto) VALUES (?, ?, ?)";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getnombre_variante());
            ps.setInt(2, v.getprecio_variante());
            ps.setInt(3, v.getProducto_idProducto());
            ps.executeUpdate();
            insertado = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar variante: " + e.getMessage());
            throw e;
        }
        return insertado;
    }

    public Producto_variante consultarPorId(int idVariante) throws SQLException {
        Producto_variante v = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idVariante, nombre_variante, precio_variante, Producto_idProducto FROM Producto_variante WHERE idVariante = ?";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVariante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    v = mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar variante: " + e.getMessage());
            throw e;
        }
        return v;
    }

    public boolean actualizarVariante(Producto_variante v) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Producto_variante SET nombre_variante = ?, precio_variante = ?, Producto_idProducto = ? WHERE idVariante = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getnombre_variante());
            ps.setInt(2, v.getprecio_variante());
            ps.setInt(3, v.getProducto_idProducto());
            ps.setInt(4, v.getidVariante());
            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar variante: " + e.getMessage());
            throw e;
        }
        return actualizado;
    }

    public boolean eliminarVariante(int id) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Producto_variante WHERE idVariante = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) eliminado = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar variante: " + e.getMessage());
            throw e;
        }
        return eliminado;
    }

    public List<Producto_variante> listarVariantes() throws SQLException {
        List<Producto_variante> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idVariante, nombre_variante, precio_variante, Producto_idProducto FROM Producto_variante";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar variantes: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * Variantes de un producto específico (ej. las 4 opciones de
     * tamaño/combo de un Sandwich, o los 3 niveles de salsas de
     * un Dino). Es lo que llena el <select> en el menú público.
     */
    public List<Producto_variante> listarPorProducto(int idProducto) throws SQLException {
        List<Producto_variante> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idVariante, nombre_variante, precio_variante, Producto_idProducto FROM Producto_variante WHERE Producto_idProducto = ?";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar variantes por producto: " + e.getMessage());
            throw e;
        }
        return lista;
    }
}
