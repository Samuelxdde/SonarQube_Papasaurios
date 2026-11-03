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
public class PruebaConsultarCategoria {

    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        CategoriaDAO categoriaDao = new CategoriaDAO();

        System.out.print("Ingrese el ID de la categoria: ");
        int idCategoria = leer.nextInt();

        Categoria categoria = categoriaDao.consultarPorId(idCategoria);

        if (categoria != null) {
            System.out.println("Categoria encontrada");
            System.out.println("Nombre: " + categoria.getnombre_categoria());
            System.out.println("Orden: " + categoria.getorden());
        } else {
            System.out.println("Categoria NO encontrada");
        }
    }
}
