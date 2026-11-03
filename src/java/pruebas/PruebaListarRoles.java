/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.RolesDAO;
import Modelo.Roles;
import java.util.List;

/**
 *
 * @author isabe
 */
public class PruebaListarRoles {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("\nListado de Roles");
        RolesDAO rolesDao = new RolesDAO();
        List<Roles> lista = rolesDao.listarRoles();

        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay roles registrados.");
        } else {
            for (Roles r : lista) {
                System.out.println("Id: " + r.getidRoles() + " Rol: " + r.getdescripcion_rol());
            }
        }
    }
}
