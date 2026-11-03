/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Producto_varianteDAO;
import Modelo.Producto_variante;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaListarVariantes {

    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        Producto_varianteDAO dao = new Producto_varianteDAO();

        System.out.print("Ingrese el ID del producto (0 para listar todas las variantes): ");
        int idProducto = leer.nextInt();

        List<Producto_variante> lista = idProducto == 0
                ? dao.listarVariantes()
                : dao.listarPorProducto(idProducto);

        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay variantes registradas o hubo un error en la consulta.");
        } else {
            for (Producto_variante v : lista) {
                System.out.println("ID: " + v.getidVariante() + " | Nombre: " + v.getnombre_variante() + " | Precio: " + v.getprecio_variante());
            }
        }
    }
}
