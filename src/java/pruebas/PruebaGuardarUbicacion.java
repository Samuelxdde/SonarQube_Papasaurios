/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.UbicacionDAO;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaGuardarUbicacion {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        UbicacionDAO dao = new UbicacionDAO();

        System.out.print("Ingrese el ID del pedido: ");
        int idPedido = sc.nextInt();

        System.out.print("Ingrese la latitud: ");
        double lat = sc.nextDouble();

        System.out.print("Ingrese la longitud: ");
        double lng = sc.nextDouble();

        boolean resultado = dao.guardarUbicacion(idPedido, lat, lng);
        if (resultado) {
            System.out.println("\nLa ubicacion se guardo correctamente en papasauriosdb.");
        } else {
            System.out.println("\nNo se pudo guardar la ubicacion.");
        }
    }
}
