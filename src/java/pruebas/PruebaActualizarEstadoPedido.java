/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Estado_pedidoDAO;
import Modelo.Estado_pedido;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaActualizarEstadoPedido {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Estado_pedidoDAO dao = new Estado_pedidoDAO();

        System.out.println("\nActualizar estado del pedido");
        System.out.print("ID del estado a modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();

        Estado_pedido estMod = new Estado_pedido();
        estMod.setidEstado_pedido(idMod);

        System.out.print("Nueva descripcion: ");
        estMod.setdescripcion_esta(sc.nextLine());

        if (dao.actualizarEstadoPedido(estMod)) {
            System.out.println("Estado actualizado correctamente.");
        } else {
            System.out.println("Error: No se encontró estado con ID: " + idMod);
        }
    }
}
