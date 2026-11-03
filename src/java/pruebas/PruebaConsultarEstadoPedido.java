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
public class PruebaConsultarEstadoPedido {

    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        Estado_pedidoDAO estadoDao = new Estado_pedidoDAO();

        System.out.print("Ingrese el ID del estado del pedido: ");
        int idEstado = leer.nextInt();

        Estado_pedido estado = estadoDao.consultarPorId(idEstado);

        if (estado != null) {
            System.out.println("Estado de pedido encontrado");
            System.out.println("Estado: " + estado.getdescripcion_esta());
        } else {
            System.out.println("Estado de pedido NO encontrado");
        }
    }
}
