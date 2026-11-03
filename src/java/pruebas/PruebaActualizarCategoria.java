/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.CategoriaDAO;
import Modelo.Categoria;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaActualizarCategoria {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        CategoriaDAO dao = new CategoriaDAO();

        System.out.println("\nActualizar categoria");
        System.out.print("Ingrese el ID de la categoria que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();

        Categoria catMod = new Categoria();
        catMod.setidCategoria(idMod);

        System.out.print("Ingrese el NUEVO nombre de la categoria: ");
        catMod.setnombre_categoria(sc.nextLine());

        System.out.print("Ingrese el NUEVO orden: ");
        catMod.setorden(sc.nextInt());

        if (dao.actualizarCategoria(catMod)) {
            System.out.println("Categoria actualizada con éxito.");
        } else {
            System.out.println("Error: No se encontró categoria con ID: " + idMod);
        }
    }
}
