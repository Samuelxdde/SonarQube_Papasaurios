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
public class PruebaInsertarProducto {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Producto producto = new Producto();
        ProductoDAO dao = new ProductoDAO();

        System.out.print("Ingrese el nombre del producto: ");
        producto.setnombre_producto(sc.nextLine());

        System.out.print("Ingrese la descripcion del producto: ");
        producto.setdescripcion_producto(sc.nextLine());

        System.out.print("Ingrese el precio base: ");
        producto.setprecio_base(sc.nextInt());
        sc.nextLine();

        System.out.print("¿Esta disponible? (true/false): ");
        producto.setdisponible(sc.nextBoolean());
        sc.nextLine();

        System.out.print("Ingrese el ID de la categoria: ");
        producto.setCategoria_idCategoria(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese la URL de la imagen: ");
        producto.setimagen_url(sc.nextLine());

        boolean resultado = dao.insertarProducto(producto);
        if (resultado) {
            System.out.println("\nEl producto se guardo correctamente en papasauriosdb.");
        } else {
            System.out.println("\nNo se pudo guardar el producto.");
        }
    }
}
