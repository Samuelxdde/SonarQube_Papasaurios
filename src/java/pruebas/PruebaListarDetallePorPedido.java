/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Detalle_pedidoDAO;
import Modelo.Detalle_pedido;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaListarDetallePorPedido {

    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        Detalle_pedidoDAO dao = new Detalle_pedidoDAO();

        System.out.print("Ingrese el ID del pedido: ");
        int idPedido = leer.nextInt();

        List<Detalle_pedido> lista = dao.listarPorPedido(idPedido);

        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay detalles registrados para este pedido o hubo un error en la consulta.");
        } else {
            for (Detalle_pedido d : lista) {
                System.out.println("ID: " + d.getidDetalle() + " | Cantidad: " + d.getcantidad() + " | Subtotal: " + d.getsubtotal());
            }
        }
    }
}
