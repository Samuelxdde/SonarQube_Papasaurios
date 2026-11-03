/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.PedidoDAO;
import Modelo.Pedido;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarPedido {

    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        PedidoDAO pedidoDao = new PedidoDAO();

        System.out.print("Ingrese el ID del pedido: ");
        int idPedido = leer.nextInt();

        Pedido pedido = pedidoDao.consultarPorId(idPedido);

        if (pedido != null) {
            System.out.println("Pedido encontrado");
            System.out.println("Fecha: " + pedido.getfecha());
            System.out.println("Hora: " + pedido.gethora());
            System.out.println("Total: " + pedido.gettotal());
        } else {
            System.out.println("Pedido NO encontrado");
        }
    }
}
