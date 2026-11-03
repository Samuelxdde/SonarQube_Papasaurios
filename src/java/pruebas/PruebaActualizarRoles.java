/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;
import Controlador.RolesDAO;
import Modelo.Roles;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarRoles {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        RolesDAO dao = new RolesDAO();

        System.out.println("\nActualizar rol");
        System.out.print("Ingrese ID del rol que va a modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();

        Roles rolMod = new Roles();
        rolMod.setidRoles(idMod);
        System.out.print("Ingrese la nueva descripcion del rol: ");
        rolMod.setdescripcion_rol(sc.nextLine());

        if (dao.actualizarRol(rolMod)) {
            System.out.println("Cambio realizado");
        }

    }

}
