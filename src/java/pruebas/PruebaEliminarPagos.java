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
public class PruebaEliminarPagos {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        PagosDAO dao = new PagosDAO();

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

