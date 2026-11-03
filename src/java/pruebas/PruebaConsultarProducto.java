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
public class PruebaConsultarProducto {

    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        ProductoDAO productoDao = new ProductoDAO();

        System.out.print("Ingrese el ID del producto: ");
        int idProducto = leer.nextInt();

        Producto producto = productoDao.consultarPorId(idProducto);

        if (producto != null) {
            System.out.println("Producto encontrado");
            System.out.println("Nombre: " + producto.getnombre_producto());
            System.out.println("Precio base: " + producto.getprecio_base());
            System.out.println("Disponible: " + producto.isdisponible());
        } else {
            System.out.println("Producto NO encontrado");
        }
    }
}
