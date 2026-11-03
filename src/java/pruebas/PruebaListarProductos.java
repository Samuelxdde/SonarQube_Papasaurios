/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.ProductoDAO;
import Modelo.Producto;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Aprendiz
 */
public class PruebaListarProductos {

    public static void main(String[] args) throws SQLException {
        System.out.println("\n=== Listado de Productos ===");

        ProductoDAO dao = new ProductoDAO();
        List<Producto> lista = dao.listarProductos();

        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay productos registrados o hubo un error en la consulta.");
        } else {
            for (Producto p : lista) {
                System.out.println("ID: " + p.getidProducto() + " | Nombre: " + p.getnombre_producto() + " | Precio: " + p.getprecio_base());
            }
        }
    }
}
