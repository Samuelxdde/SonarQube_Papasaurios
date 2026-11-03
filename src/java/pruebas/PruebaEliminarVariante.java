/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Producto_varianteDAO;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaEliminarVariante {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Producto_varianteDAO dao = new Producto_varianteDAO();

        System.out.println("\nEliminar variante");
        System.out.print("Ingrese el ID de la variante que va a eliminar: ");
        int idEliminar = sc.nextInt();

        if (dao.eliminarVariante(idEliminar)) {
            System.out.println("Variante eliminada de papasauriosdb.");
        } else {
            System.out.println("No se encontro ninguna variante con el ID: " + idEliminar);
        }
    }
}
