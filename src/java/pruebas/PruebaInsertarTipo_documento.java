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
 * @author Aprendiz
 */
public class PruebaInsertarTipo_documento {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Tipo_documento documento = new Tipo_documento();
        Tipo_documentoDAO dao = new Tipo_documentoDAO();

        System.out.print("Ingrese el ID del documento:");
        documento.setidTipo_documento(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese la descripción del tipo documento:");
        documento.setdescripcion_doc(sc.nextLine());

        boolean resultado = dao.insertarTipo_documento(documento);

        if (resultado) {
            System.out.println("\nEl Tipo de documento se guardo con exito.");

        } else {
            System.out.println("\nNo se pudo guardar el Tipo de documento.");
        }
    }
}
