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
public class PruebaInsertarCategoria {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Categoria categoria = new Categoria();
        CategoriaDAO dao = new CategoriaDAO();

        System.out.print("Ingrese el nombre de la categoria: ");
        categoria.setnombre_categoria(sc.nextLine());

        System.out.print("Ingrese el orden de la categoria: ");
        categoria.setorden(sc.nextInt());

        boolean resultado = dao.insertarCategoria(categoria);
        if (resultado) {
            System.out.println("\nLa categoria se guardo correctamente en papasauriosdb.");
        } else {
            System.out.println("\nNo se pudo guardar la categoria.");
        }
    }
}
