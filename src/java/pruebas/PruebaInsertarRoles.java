 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.RolesDAO;
import Modelo.Roles;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Aprendiz
 */
public class PruebaInsertarRoles {

    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        Roles rol = new Roles();
        RolesDAO dao = new RolesDAO();

        System.out.print("Ingrese el ID del rol:");
        rol.setidRoles(sc.nextInt
        ());
        sc.nextLine();

        System.out.print("Ingrese la descripción del rol: ");
        rol.setdescripcion_rol(sc.nextLine());

        boolean resultado = dao.insertarRol(rol);

        if (resultado) {
            System.out.println("\nEl rol se guardo correctamente en VIVEROBD.");
        } else {
            System.out.println("\nNo se pudo guardar el rol.");
        }

    }
}

