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
 * @author Aprendiz
 */
public class PruebaConsultarPagos {

    
    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        PagosDAO pagosDao = new PagosDAO();

        System.out.print("Ingrese el ID de Pago: ");
        String idPagos = leer.nextLine();

        Pagos pagos = pagosDao.consultarPagos(1);

        if (pagos != null) {

            System.out.println("Pago encontrado");
            System.out.println("Estdo del pago: " + pagos.getestado_pago());
        } else {
            System.out.println("Pago NO encontrado");
        }

    }

}
