/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.RolesDAO;
import Modelo.Roles;
import java.util.Scanner;
import java.sql.SQLException;
/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarRoles {

    public static void main(String[] args) throws SQLException {
       Scanner leer = new Scanner (System.in);
       RolesDAO rolesDao = new RolesDAO();
       
       
       System.out.print("Ingrese el ID del Rol: ");
       String idRoles = leer.nextLine();
       
       Roles roles = rolesDao.ConsultarRoles(1);
       
       if (roles != null){
           System.out.println("Rol encontrado");
           System.out.println("Rol: " + roles.getdescripcion_rol());
       }else{
           System.out.println("Rol no encontrado");
       }
    }
    
}
