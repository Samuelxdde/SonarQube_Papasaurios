/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Detalle_pedidoDAO;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaEliminarDetallePedido {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Detalle_pedidoDAO dao = new Detalle_pedidoDAO();

        System.out.println("\nEliminar detalle de pedido");
        System.out.print("Ingrese el ID del detalle que va a eliminar: ");
        int idEliminar = sc.nextInt();

        if (dao.eliminarDetalle(idEliminar)) {
            System.out.println("Detalle eliminado de papasauriosdb.");
        } else {
            System.out.println("No se encontro ningun detalle con el ID: " + idEliminar);
        }
    }
}
