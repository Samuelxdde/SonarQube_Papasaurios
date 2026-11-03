/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Detalle_pedidoDAO;
import Modelo.Detalle_pedido;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaInsertarDetallePedido {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Detalle_pedido detalle = new Detalle_pedido();
        Detalle_pedidoDAO dao = new Detalle_pedidoDAO();

        System.out.print("Ingrese la cantidad: ");
        detalle.setcantidad(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese el precio unitario: ");
        detalle.setprecio_unitario(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese una nota (vacio si no aplica): ");
        detalle.setnota(sc.nextLine());

        System.out.print("Ingrese el ID del pedido: ");
        detalle.setPedido_idPedido(sc.nextInt());

        System.out.print("Ingrese el ID del producto: ");
        detalle.setProducto_idProducto(sc.nextInt());

        System.out.print("Ingrese el ID de la variante (0 si no aplica): ");
        int idVariante = sc.nextInt();
        detalle.setProducto_variante_idVariante(idVariante == 0 ? null : idVariante);

        boolean resultado = dao.insertarDetalle(detalle);
        if (resultado) {
            System.out.println("\nEl detalle se guardo correctamente en papasauriosdb.");
        } else {
            System.out.println("\nNo se pudo guardar el detalle.");
        }
    }
}
