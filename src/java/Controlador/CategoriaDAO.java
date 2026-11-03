package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Categoria;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    /**
     * ¿Ya existe una categoría con este mismo nombre? Comparación sin
     * importar mayúsculas ni espacios de más, para que "Bebidas" y
     * "  bebidas " cuenten como la misma cosa. Al editar, se excluye
     * la propia fila (excluirId) para no chocar contra sí misma.
     */
    public boolean existeCategoria(String nombre, Integer excluirId) throws SQLException {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT 1 FROM Categoria WHERE LOWER(TRIM(nombre_categoria)) = LOWER(TRIM(?))"
                + (excluirId != null ? " AND idCategoria <> ?" : "") + " LIMIT 1";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            if (excluirId != null) ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar categoría duplicada: " + e.getMessage());
            throw e;
        }
    }

    public boolean insertarCategoria(Categoria c) throws SQLException {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Categoria (nombre_categoria, orden) VALUES (?, ?)";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getnombre_categoria());
            ps.setInt(2, c.getorden());
            ps.executeUpdate();
            insertado = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar categoría: " + e.getMessage());
            throw e;
        }
        return insertado;
    }

    public Categoria consultarPorId(int idCategoria) throws SQLException {
        Categoria c = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idCategoria, nombre_categoria, orden FROM Categoria WHERE idCategoria = ?";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new Categoria();
                    c.setidCategoria(rs.getInt(1));
                    c.setnombre_categoria(rs.getString(2));
                    c.setorden(rs.getInt(3));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar categoría: " + e.getMessage());
            throw e;
        }
        return c;
    }

    public boolean actualizarCategoria(Categoria c) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Categoria SET nombre_categoria = ?, orden = ? WHERE idCategoria = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getnombre_categoria());
            ps.setInt(2, c.getorden());
            ps.setInt(3, c.getidCategoria());
            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar categoría: " + e.getMessage());
            throw e;
        }
        return actualizado;
    }

    public boolean eliminarCategoria(int id) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Categoria WHERE idCategoria = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) eliminado = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar categoría: " + e.getMessage());
            throw e;
        }
        return eliminado;
    }

    public List<Categoria> listarCategorias() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idCategoria, nombre_categoria, orden FROM Categoria ORDER BY orden";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setidCategoria(rs.getInt(1));
                c.setnombre_categoria(rs.getString(2));
                c.setorden(rs.getInt(3));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
            throw e;
        }
        return lista;
    }
}
