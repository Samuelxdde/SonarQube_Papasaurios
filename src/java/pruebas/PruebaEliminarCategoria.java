/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.CategoriaDAO;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaEliminarCategoria {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        CategoriaDAO dao = new CategoriaDAO();

        System.out.println("\nEliminar categoria");
        System.out.print("Ingrese el ID de la categoria que va a eliminar: ");
        int idEliminar = sc.nextInt();

        if (dao.eliminarCategoria(idEliminar)) {
            System.out.println("Categoria eliminada de papasauriosdb.");
        } else {
            System.out.println("No se encontro ninguna categoria con el ID: " + idEliminar);
        }
    }
}
