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
public class PruebaActualizarEstadoDePedido {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        PedidoDAO dao = new PedidoDAO();

        System.out.println("\nActualizar estado del pedido");
        System.out.print("Ingrese el ID del pedido que desea modificar: ");
        int idPedido = sc.nextInt();

        System.out.print("Ingrese el NUEVO ID del estado del pedido: ");
        int idEstado = sc.nextInt();

        if (dao.actualizarEstado(idPedido, idEstado)) {
            System.out.println("Estado del pedido actualizado con éxito.");
        } else {
            System.out.println("Error: No se encontró pedido con ID: " + idPedido);
        }
    }
}
