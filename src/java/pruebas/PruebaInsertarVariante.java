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
public class PruebaInsertarVariante {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Producto_variante variante = new Producto_variante();
        Producto_varianteDAO dao = new Producto_varianteDAO();

        System.out.print("Ingrese el nombre de la variante: ");
        variante.setnombre_variante(sc.nextLine());

        System.out.print("Ingrese el precio de la variante: ");
        variante.setprecio_variante(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese el ID del producto al que pertenece: ");
        variante.setProducto_idProducto(sc.nextInt());

        boolean resultado = dao.insertarVariante(variante);
        if (resultado) {
            System.out.println("\nLa variante se guardo correctamente en papasauriosdb.");
        } else {
            System.out.println("\nNo se pudo guardar la variante.");
        }
    }
}
