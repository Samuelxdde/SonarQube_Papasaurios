package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Producto;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setidProducto(rs.getInt(1));
        p.setnombre_producto(rs.getString(2));
        p.setdescripcion_producto(rs.getString(3));
        p.setprecio_base(rs.getInt(4));
        p.setdisponible(rs.getBoolean(5));
        p.setCategoria_idCategoria(rs.getInt(6));
        p.setimagen_url(rs.getString(7));
        return p;
    }

    /**
     * ¿Ya existe un producto con este mismo nombre? Comparación sin
     * importar mayúsculas ni espacios de más.
     */
    public boolean existeProducto(String nombre, Integer excluirId) throws SQLException {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT 1 FROM Producto WHERE LOWER(TRIM(nombre_producto)) = LOWER(TRIM(?))"
                + (excluirId != null ? " AND idProducto <> ?" : "") + " LIMIT 1";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            if (excluirId != null) ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar producto duplicado: " + e.getMessage());
            throw e;
        }
    }

    public boolean insertarProducto(Producto p) throws SQLException {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Producto (nombre_producto, descripcion_producto, precio_base, disponible, Categoria_idCategoria, imagen_url) VALUES (?, ?, ?, ?, ?, ?)";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getnombre_producto());
            ps.setString(2, p.getdescripcion_producto());
            ps.setInt(3, p.getprecio_base());
            ps.setBoolean(4, p.isdisponible());
            ps.setInt(5, p.getCategoria_idCategoria());
            ps.setString(6, p.getimagen_url());
            ps.executeUpdate();
            insertado = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
            throw e;
        }
        return insertado;
    }

    public Producto consultarPorId(int idProducto) throws SQLException {
        Producto p = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idProducto, nombre_producto, descripcion_producto, precio_base, disponible, Categoria_idCategoria, imagen_url FROM Producto WHERE idProducto = ?";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar producto: " + e.getMessage());
            throw e;
        }
        return p;
    }

    public boolean actualizarProducto(Producto p) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Producto SET nombre_producto = ?, descripcion_producto = ?, precio_base = ?, disponible = ?, Categoria_idCategoria = ?, imagen_url = ? WHERE idProducto = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getnombre_producto());
            ps.setString(2, p.getdescripcion_producto());
            ps.setInt(3, p.getprecio_base());
            ps.setBoolean(4, p.isdisponible());
            ps.setInt(5, p.getCategoria_idCategoria());
            ps.setString(6, p.getimagen_url());
            ps.setInt(7, p.getidProducto());
            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            throw e;
        }
        return actualizado;
    }

    public boolean eliminarProducto(int id) throws SQLException {
        boolean eliminado = false;
        String sql = "DELETE FROM Producto WHERE idProducto = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) eliminado = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            throw e;
        }
        return eliminado;
    }

    public List<Producto> listarProductos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idProducto, nombre_producto, descripcion_producto, precio_base, disponible, Categoria_idCategoria, imagen_url FROM Producto";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * Productos de una categoría específica, solo los disponibles.
     * Es lo que usa el menú público para mostrar cada sección.
     */
    public List<Producto> listarPorCategoria(int idCategoria) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT idProducto, nombre_producto, descripcion_producto, precio_base, disponible, Categoria_idCategoria, imagen_url FROM Producto WHERE Categoria_idCategoria = ? AND disponible = TRUE";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos por categoría: " + e.getMessage());
            throw e;
        }
        return lista;
    }
}
