/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;
import Controlador.Tipo_documentoDAO;
import Modelo.Tipo_documento;
import java.sql.SQLException;
import java.util.Scanner;
/**
 *
 * @author isabe
 */
public class PruebaEliminarTipo_documento {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Tipo_documentoDAO dao = new Tipo_documentoDAO();

        System.out.println("\nEliminar Tipo documento");
        System.out.print("ID del documento que va a eliminar: ");
        int idDel = sc.nextInt();

        if (dao.eliminarTipoDocumento(idDel)) {
            System.out.println("Tipo documento eliminado de viverodb.");
        }
    }

}
