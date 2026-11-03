/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Producto_varianteDAO;
import Modelo.Producto_variante;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarVariante {

    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        Producto_varianteDAO varianteDao = new Producto_varianteDAO();

        System.out.print("Ingrese el ID de la variante: ");
        int idVariante = leer.nextInt();

        Producto_variante variante = varianteDao.consultarPorId(idVariante);

        if (variante != null) {
            System.out.println("Variante encontrada");
            System.out.println("Nombre: " + variante.getnombre_variante());
            System.out.println("Precio: " + variante.getprecio_variante());
        } else {
            System.out.println("Variante NO encontrada");
        }
    }
}
