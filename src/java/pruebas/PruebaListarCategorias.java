/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import Controlador.CategoriaDAO;
import Modelo.Categoria;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Aprendiz
 */
public class PruebaListarCategorias {

    public static void main(String[] args) throws SQLException {
        System.out.println("\n=== Listado de Categorias ===");

        CategoriaDAO dao = new CategoriaDAO();
        List<Categoria> lista = dao.listarCategorias();

        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay categorias registradas o hubo un error en la consulta.");
        } else {
            for (Categoria c : lista) {
                System.out.println("ID: " + c.getidCategoria() + " | Nombre: " + c.getnombre_categoria() + " | Orden: " + c.getorden());
            }
        }
    }
}
