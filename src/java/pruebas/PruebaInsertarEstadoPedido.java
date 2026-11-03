/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Estado_pedidoDAO;
import Modelo.Estado_pedido;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaInsertarEstadoPedido {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Estado_pedido estado = new Estado_pedido();
        Estado_pedidoDAO dao = new Estado_pedidoDAO();

        System.out.print("Ingrese la descripcion del estado del pedido (ej: Recibido): ");
        estado.setdescripcion_esta(sc.nextLine());

        boolean resultado = dao.insertarEstadoPedido(estado);
        if (resultado) {
            System.out.println("\nEl estado del pedido se guardo correctamente en papasauriosdb.");
        } else {
            System.out.println("\nNo se pudo guardar el estado del pedido.");
        }
    }
}
