/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.PagosDAO;
import Modelo.Pagos;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Paula Gisedt
 */
public class PruebaPagos {

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

        System.out.println("\n Listado de estados de pago disponibles");

        List<Pagos> lista = new ArrayList<>();
        
        if (lista.isEmpty()) {
            System.out.println("No hay estados de pago registrados.");
        } else {
            for (Pagos r : lista) {
                System.out.println("ID: " + r.getidPagos() + " | Pago: " + r.getestado_pago());
            }
            
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

          
            System.out.println("\nEliminar estado de pago");
            System.out.print("Ingrese ID del estado de pago que desea eliminar: ");
            int idDel = sc.nextInt();

            if (dao.eliminarPagos(idDel)) {
                System.out.println("Estado eliminado de VIVEROBD.");
            } else {
                System.out.println("No se encontro el ID.");
            }
        }
    }
    
}
