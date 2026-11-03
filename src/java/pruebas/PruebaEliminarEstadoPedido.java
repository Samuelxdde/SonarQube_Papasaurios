/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Estado_pedidoDAO;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaEliminarEstadoPedido {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Estado_pedidoDAO dao = new Estado_pedidoDAO();

        System.out.println("\nEliminar estado del pedido");
        System.out.print("ID del estado a eliminar: ");
        int idEstado = sc.nextInt();

        if (dao.eliminarEstadoPedido(idEstado)) {
            System.out.println("Estado eliminado con exito.");
        } else {
            System.out.println("No se encontró el estado.");
        }
    }
}
