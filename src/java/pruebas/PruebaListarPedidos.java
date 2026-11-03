/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.PedidoDAO;
import Modelo.Pedido;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Aprendiz
 */
public class PruebaListarPedidos {

    public static void main(String[] args) throws SQLException {
        System.out.println("\n=== Listado de Pedidos ===");

        PedidoDAO dao = new PedidoDAO();
        List<Pedido> lista = dao.listarPedidos();

        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay pedidos registrados o hubo un error en la consulta.");
        } else {
            for (Pedido p : lista) {
                System.out.println("ID: " + p.getidPedido() + " | Fecha: " + p.getfecha() + " | Total: " + p.gettotal());
            }
        }
    }
}
