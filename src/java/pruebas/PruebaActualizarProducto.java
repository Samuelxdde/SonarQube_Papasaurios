/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.ProductoDAO;
import Modelo.Producto;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaActualizarProducto {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        ProductoDAO dao = new ProductoDAO();

        System.out.println("\nActualizar producto");
        System.out.print("Ingrese el ID del producto que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();

        Producto prodMod = new Producto();
        prodMod.setidProducto(idMod);

        System.out.print("Ingrese el NUEVO nombre del producto: ");
        prodMod.setnombre_producto(sc.nextLine());

        System.out.print("Ingrese la NUEVA descripcion: ");
        prodMod.setdescripcion_producto(sc.nextLine());

        System.out.print("Ingrese el NUEVO precio base: ");
        prodMod.setprecio_base(sc.nextInt());
        sc.nextLine();

        System.out.print("¿Esta disponible? (true/false): ");
        prodMod.setdisponible(sc.nextBoolean());
        sc.nextLine();

        System.out.print("Ingrese el ID de la categoria: ");
        prodMod.setCategoria_idCategoria(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese la NUEVA URL de imagen: ");
        prodMod.setimagen_url(sc.nextLine());

        if (dao.actualizarProducto(prodMod)) {
            System.out.println("Producto actualizado con éxito.");
        } else {
            System.out.println("Error: No se encontró producto con ID: " + idMod);
        }
    }
}
