package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Pedido;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setidPedido(rs.getInt(1));
        p.setfecha(rs.getDate(2));
        p.sethora(rs.getTime(3));
        p.sethora_estimada(rs.getTime(4));
        p.settipo_entrega(rs.getString(5));
        p.setdireccion_entrega(rs.getString(6));
        p.settotal(rs.getInt(7));
        p.setUsuarios_idUsuarios(rs.getInt(8));
        p.setEstado_pedido_idEstado_pedido(rs.getInt(9));
        p.setPagos_idPagos(rs.getInt(10));
        int repartidor = rs.getInt(11);
        p.setRepartidor_idUsuarios(rs.wasNull() ? null : repartidor);
        return p;
    }

    private static final String COLUMNAS =
        "idPedido, fecha, hora, hora_estimada, tipo_entrega, direccion_entrega, total, " +
        "Usuarios_idUsuarios, Estado_pedido_idEstado_pedido, Pagos_idPagos, Repartidor_idUsuarios";

    /**
     * Inserta el encabezado del pedido y devuelve el idPedido generado,
     * para que el servlet pueda usarlo al insertar las líneas del
     * carrito en Detalle_pedido (necesita saber a qué pedido pertenecen).
     */
    public int insertarPedido(Pedido p) throws SQLException {
        int idGenerado = -1;
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        String sql = "INSERT INTO Pedido (fecha, hora, hora_estimada, tipo_entrega, direccion_entrega, total, Usuarios_idUsuarios, Estado_pedido_idEstado_pedido, Pagos_idPagos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, p.getfecha());
            ps.setTime(2, p.gethora());
            ps.setTime(3, p.gethora_estimada());
            ps.setString(4, p.gettipo_entrega());
            ps.setString(5, p.getdireccion_entrega());
            ps.setInt(6, p.gettotal());
            ps.setInt(7, p.getUsuarios_idUsuarios());
            ps.setInt(8, p.getEstado_pedido_idEstado_pedido());
            ps.setInt(9, p.getPagos_idPagos());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar pedido: " + e.getMessage());
            throw e;
        }
        return idGenerado;
    }

    public Pedido consultarPorId(int idPedido) throws SQLException {
        Pedido p = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT " + COLUMNAS + " FROM Pedido WHERE idPedido = ?";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar pedido: " + e.getMessage());
            throw e;
        }
        return p;
    }

    public boolean actualizarEstado(int idPedido, int idEstadoPedido) throws SQLException {
        boolean actualizado = false;
        String sql = "UPDATE Pedido SET Estado_pedido_idEstado_pedido = ? WHERE idPedido = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstadoPedido);
            ps.setInt(2, idPedido);
            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del pedido: " + e.getMessage());
            throw e;
        }
        return actualizado;
    }

    public boolean eliminarPedido(int id) throws SQLException {
        boolean eliminado = false;
        // ON DELETE CASCADE en Detalle_pedido se encarga de borrar sus líneas.
        String sql = "DELETE FROM Pedido WHERE idPedido = ?";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) eliminado = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar pedido: " + e.getMessage());
            throw e;
        }
        return eliminado;
    }

    public List<Pedido> listarPedidos() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT " + COLUMNAS + " FROM Pedido ORDER BY fecha DESC, hora DESC";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * Pedidos de un usuario específico. Es lo que usa "Mis pedidos"
     * en el panel del cliente, igual que listarReservaPorUsuario
     * en el sistema anterior de La Suculentería.
     */
    public List<Pedido> listarPorUsuario(int idUsuario) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT " + COLUMNAS + " FROM Pedido WHERE Usuarios_idUsuarios = ? ORDER BY fecha DESC, hora DESC";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pedidos por usuario: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * Pedidos filtrados por tipo de entrega (p.ej. "Domicilio").
     * Es lo que usa el panel del repartidor: solo le interesan los
     * pedidos que hay que llevar a una dirección, no los que el
     * cliente recoge en tienda.
     */
    public List<Pedido> listarPorTipoEntrega(String tipoEntrega) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT " + COLUMNAS + " FROM Pedido WHERE tipo_entrega = ? ORDER BY fecha DESC, hora DESC";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipoEntrega);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pedidos por tipo de entrega: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * Pedidos en un estado específico, del más antiguo al más nuevo.
     * Es lo que usa el panel de cocina para armar su cola de trabajo
     * (FIFO: el que llegó primero se prepara primero).
     */
    public List<Pedido> listarPorEstado(int idEstado) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT " + COLUMNAS + " FROM Pedido WHERE Estado_pedido_idEstado_pedido = ? ORDER BY fecha ASC, hora ASC";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pedidos por estado: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * Un repartidor "toma" un pedido a domicilio que está listo. Solo
     * asigna si todavía no tiene repartidor (WHERE ... IS NULL), así
     * dos repartidores no pueden quedarse con el mismo pedido por una
     * carrera entre clics — el que llegue primero a la base de datos
     * gana, el segundo simplemente no actualiza ninguna fila.
     */
    public boolean asignarRepartidor(int idPedido, int idRepartidor) throws SQLException {
        boolean asignado = false;
        String sql = "UPDATE Pedido SET Repartidor_idUsuarios = ? WHERE idPedido = ? AND Repartidor_idUsuarios IS NULL";
        Conexion conexion = new Conexion();
        Connection con = (Connection) conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRepartidor);
            ps.setInt(2, idPedido);
            int filas = ps.executeUpdate();
            if (filas > 0) asignado = true;
        } catch (SQLException e) {
            System.out.println("Error al asignar repartidor: " + e.getMessage());
            throw e;
        }
        return asignado;
    }

    /**
     * Pedidos asignados a un repartidor específico (los que ya tomó),
     * sin importar el estado — la app del repartidor separa "pendientes"
     * de "historial" según el estado, igual que hace PanelRepartidor.jsp.
     */
    public List<Pedido> listarPorRepartidor(int idRepartidor) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        String sql = "SELECT " + COLUMNAS + " FROM Pedido WHERE Repartidor_idUsuarios = ? ORDER BY fecha DESC, hora DESC";
        try (conexion; PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRepartidor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pedidos por repartidor: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    public int contarPedidos() throws SQLException {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM Pedido";
        Conexion conexion = new Conexion();
        Connection con = conexion.getConn();

        try (conexion; PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error al contar pedidos: " + e.getMessage());
            throw e;
        }
        return total;
    }
}
