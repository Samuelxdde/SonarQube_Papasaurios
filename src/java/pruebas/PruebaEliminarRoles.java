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
public class PruebaEliminarRoles {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        RolesDAO dao = new RolesDAO();

        System.out.println("\nEliminar rol");
        System.out.print("Ingrese ID del rol a eliminar: ");
        int idDel = sc.nextInt();

        if (dao.eliminarRol(idDel)) {
            System.out.println("Rol Eliminado de VIVEROBD.");
        } else {
            System.out.println("No se encontro el ID.");
        }
    }
}
