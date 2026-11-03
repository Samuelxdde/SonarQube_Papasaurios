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
public class PruebaActualizarTipo_documento {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Tipo_documentoDAO dao = new Tipo_documentoDAO();

        System.out.println("\nActualizar tipo documento");
        System.out.print("ID del documento a modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();

        Tipo_documento docMod = new Tipo_documento();
        docMod.setidTipo_documento(idMod);
        System.out.print("Nueva descripción del tipo de documeno: ");
        docMod.setdescripcion_doc(sc.nextLine());

        if (dao.actualizarTipoDocumento(docMod)) {
            System.out.println("Cambio guardado en viverodb.");
        }

    }

}
