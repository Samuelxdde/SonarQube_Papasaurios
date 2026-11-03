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
public class PruebaConsultarTipoDocumento {


    
    public static void main(String[] args) throws SQLException {
        Scanner leer = new Scanner(System.in);
        Tipo_documentoDAO documentoDao = new Tipo_documentoDAO();
        
        
        System.out.print("Ingrese el ID del Tipo documento a consultar: ");
        String idTipo_documento = leer.nextLine();
        
        Tipo_documento documento = documentoDao.ConsultarTipo_documento(1);
       
        
        if (documento != null) {
            System.out.println("Tipo de documento encontrado");
            System.out.println("Descripción: " + documento.getdescripcion_doc()); 
        } else {
            
            System.out.println("Tipo de documento NO encontrado");
        }
    }

 
    
}
