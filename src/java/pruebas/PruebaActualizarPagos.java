/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;
import Controlador.PagosDAO;
import Modelo.Pagos;
import java.sql.SQLException;
import java.util.Scanner;
/**
 *
 * @author isabe
 */
public class PruebaActualizarPagos {

    public static void main(String[] args) throws SQLException {
       Scanner sc = new Scanner(System.in);
       PagosDAO dao = new PagosDAO();
       
       System.out.println("\nActualizar estado de pago");
            System.out.print("Ingrese ID del estado de pago que desea modificar: ");
            int idMod = sc.nextInt();
            sc.nextLine();

            Pagos pagosMod = new Pagos();
            pagosMod.setidPagos(idMod);
            System.out.print("Ingrese el nuevo estado de pago: ");
            pagosMod.setestado_pago(sc.nextLine());

            if (dao.actualizarPagos(pagosMod)) {
                System.out.println("Cambio realizado");
            }

    }
    
}
