/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Modelo.Usuarios;
import java.util.Scanner;
import Controlador.UsuariosDAO;
import java.sql.SQLException;

/**
 *
 * @author isabe
 */
public class PruebaEliminarUsuarios {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        UsuariosDAO dao = new UsuariosDAO();

        System.out.println("\nEliminar Usuario");
        System.out.print("ID del usuario a eliminar: ");
        int idDel = sc.nextInt();

        if (dao.eliminarUsuario(idDel)) {
            System.out.println("Usuario eliminado con exito.");
        } else {
            System.out.println("No se encontró el usuario.");
        }
    }
}