/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.PedidoDAO;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaEliminarPedido {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        PedidoDAO dao = new PedidoDAO();

        System.out.println("\nEliminar pedido");
        System.out.print("Ingrese el ID del pedido que va a eliminar: ");
        int idEliminar = sc.nextInt();

        if (dao.eliminarPedido(idEliminar)) {
            System.out.println("Pedido eliminado de papasauriosdb.");
        } else {
            System.out.println("No se encontro ningun pedido con el ID: " + idEliminar);
        }
    }
}
