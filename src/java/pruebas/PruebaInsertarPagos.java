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
 * @author Paula Gisedt
 */
public class PruebaInsertarPagos {

    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        Pagos pago = new Pagos();
        PagosDAO dao = new PagosDAO();

        System.out.print("Ingrese el ID del estado de pago:");
        pago.setidPagos(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese el estado de pago: ");
        pago.setestado_pago(sc.nextLine());

        boolean resultado = dao.insertarPago(pago);

        if (resultado) {
            System.out.println("\nEl estado de pago se guardo correctamente en VIVEROBD.");
        } else {
            System.out.println("\nNo se pudo guardar el estado.");
        }
    }
}

      

       
            
          
       
    
    

