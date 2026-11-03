/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.PedidoDAO;
import Modelo.Pedido;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaInsertarPedido {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Pedido pedido = new Pedido();
        PedidoDAO dao = new PedidoDAO();

        System.out.print("Ingrese la fecha del pedido (aaaa-mm-dd): ");
        pedido.setfecha(Date.valueOf(sc.nextLine()));

        System.out.print("Ingrese la hora del pedido (hh:mm:ss): ");
        pedido.sethora(Time.valueOf(sc.nextLine()));

        System.out.print("Ingrese el tipo de entrega (Recoger en tienda / Domicilio): ");
        pedido.settipo_entrega(sc.nextLine());

        System.out.print("Ingrese la direccion de entrega (vacio si no aplica): ");
        pedido.setdireccion_entrega(sc.nextLine());

        System.out.print("Ingrese el total del pedido: ");
        pedido.settotal(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese el ID del usuario: ");
        pedido.setUsuarios_idUsuarios(sc.nextInt());

        System.out.print("Ingrese el ID del estado del pedido: ");
        pedido.setEstado_pedido_idEstado_pedido(sc.nextInt());

        System.out.print("Ingrese el ID del estado de pago: ");
        pedido.setPagos_idPagos(sc.nextInt());

        int idGenerado = dao.insertarPedido(pedido);
        if (idGenerado > 0) {
            System.out.println("\nEl pedido se guardo correctamente en papasauriosdb con ID: " + idGenerado);
        } else {
            System.out.println("\nNo se pudo guardar el pedido.");
        }
    }
}
