/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.UbicacionDAO;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaEliminarUbicacion {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        UbicacionDAO dao = new UbicacionDAO();

        System.out.println("\nEliminar ubicacion");
        System.out.print("Ingrese el ID del pedido cuya ubicacion desea eliminar: ");
        int idPedido = sc.nextInt();

        if (dao.eliminarUbicacion(idPedido)) {
            System.out.println("Ubicacion eliminada de papasauriosdb.");
        } else {
            System.out.println("No se encontro ubicacion para el pedido con ID: " + idPedido);
        }
    }
}
