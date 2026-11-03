/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.UbicacionDAO;
import Modelo.Ubicacion;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarUbicacion {

    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        UbicacionDAO dao = new UbicacionDAO();

        System.out.print("Ingrese el ID del pedido: ");
        int idPedido = leer.nextInt();

        Ubicacion ubicacion = dao.obtenerUbicacion(idPedido);

        if (ubicacion != null) {
            System.out.println("Ubicacion encontrada");
            System.out.println("Latitud: " + ubicacion.getlatitud());
            System.out.println("Longitud: " + ubicacion.getlongitud());
            System.out.println("Actualizado: " + ubicacion.getactualizado());
        } else {
            System.out.println("Ubicacion NO encontrada");
        }
    }
}
