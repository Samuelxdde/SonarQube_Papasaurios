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
public class PruebaActualizarVariante {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Producto_varianteDAO dao = new Producto_varianteDAO();

        System.out.println("\nActualizar variante");
        System.out.print("Ingrese el ID de la variante que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();

        Producto_variante varMod = new Producto_variante();
        varMod.setidVariante(idMod);

        System.out.print("Ingrese el NUEVO nombre de la variante: ");
        varMod.setnombre_variante(sc.nextLine());

        System.out.print("Ingrese el NUEVO precio: ");
        varMod.setprecio_variante(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese el ID del producto al que pertenece: ");
        varMod.setProducto_idProducto(sc.nextInt());

        if (dao.actualizarVariante(varMod)) {
            System.out.println("Variante actualizada con éxito.");
        } else {
            System.out.println("Error: No se encontró variante con ID: " + idMod);
        }
    }
}
