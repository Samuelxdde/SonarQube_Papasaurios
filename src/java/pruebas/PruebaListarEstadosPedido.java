/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.Estado_pedidoDAO;
import Modelo.Estado_pedido;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Aprendiz
 */
public class PruebaListarEstadosPedido {

    public static void main(String[] args) throws SQLException {
        System.out.println("\n=== Listado de Estados de Pedido ===");

        Estado_pedidoDAO dao = new Estado_pedidoDAO();
        List<Estado_pedido> lista = dao.listarEstados();

        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay estados registrados o hubo un error en la consulta.");
        } else {
            for (Estado_pedido e : lista) {
                System.out.println("ID: " + e.getidEstado_pedido() + " | Estado: " + e.getdescripcion_esta());
            }
        }
    }
}
