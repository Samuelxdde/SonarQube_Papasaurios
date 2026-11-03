package Servlet;

import Controlador.CategoriaDAO;
import Controlador.ProductoDAO;
import Controlador.Producto_varianteDAO;
import Modelo.Categoria;
import Modelo.Producto;
import Modelo.Producto_variante;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Carga el menú público completo: categorías en orden, los productos
 * de cada una, y las variantes (tamaños/combos) de cada producto que
 * las tenga. Es lo que arma cada sección del menú (Papas Saurios,
 * Sandwich, Alitas BBQ...) en index.jsp / Vista/Menu.jsp.
 */
@WebServlet("/Menu")
public class MenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<Categoria, List<Producto>> menuPorCategoria = new LinkedHashMap<>();
        Map<Integer, List<Producto_variante>> variantesPorProducto = new HashMap<>();

        // Orden en que se quieren ver las secciones del menú. La primera
        // de esta lista es la pestaña que aparece abierta al entrar.
        // Lo que no esté aquí se acomoda después, respetando el campo
        // "orden" de la base de datos.
        List<String> ordenPreferido = Arrays.asList(
                "Patacones",
                "Entradas",
                "Dino Dog",
                "Sandwich",
                "Dino Burguer",
                "Combos",
                "Alitas BBQ",
                "Dorilocos",
                "Picadas",
                "Malteadas y Sodas",
                "Jugos y Limonadas",
                "Bebidas y Cervezas");

        // Categorías de solo texto (sin fotos de producto): se muestran
        // al final del menú y de la barra lateral, después de todas las
        // categorías que sí tienen imágenes.
        Set<String> categoriasSinImagen = new HashSet<>(Arrays.asList(
                "Papas, Salchichas y Quesos", "Proteínas y Toppings", "Salsas"));

        // Papas Saurios va de últimas en el menú, cerrando todas las secciones.
        String categoriaAlFinal = "Papas Saurios";

        try {
            CategoriaDAO categoriaDAO = new CategoriaDAO();
            ProductoDAO productoDAO = new ProductoDAO();
            Producto_varianteDAO varianteDAO = new Producto_varianteDAO();

            List<Categoria> categorias = categoriaDAO.listarCategorias();

            // Tres bloques: primero las categorías "fuertes" en el orden
            // pedido, luego las de solo texto (adicionales, salsas...) y
            // de últimas Papas Saurios.
            List<Categoria> conImagen = new ArrayList<>();
            List<Categoria> sinImagen = new ArrayList<>();
            List<Categoria> alFinal = new ArrayList<>();
            for (Categoria cat : categorias) {
                String nombre = cat.getnombre_categoria() == null
                        ? "" : cat.getnombre_categoria().trim();
                if (categoriaAlFinal.equalsIgnoreCase(nombre)) {
                    alFinal.add(cat);
                } else if (categoriasSinImagen.contains(nombre)) {
                    sinImagen.add(cat);
                } else {
                    conImagen.add(cat);
                }
            }

            // Dentro del primer bloque manda "ordenPreferido"; las categorías
            // nuevas que no estén en la lista quedan al final de ese bloque,
            // ordenadas como vengan de la base de datos.
            Collections.sort(conImagen, new Comparator<Categoria>() {
                @Override
                public int compare(Categoria a, Categoria b) {
                    int ra = rangoPreferido(a, ordenPreferido);
                    int rb = rangoPreferido(b, ordenPreferido);
                    if (ra != rb) return Integer.compare(ra, rb);
                    return Integer.compare(a.getorden(), b.getorden());
                }
            });

            List<Categoria> categoriasOrdenadas = new ArrayList<>();
            categoriasOrdenadas.addAll(conImagen);
            categoriasOrdenadas.addAll(sinImagen);
            categoriasOrdenadas.addAll(alFinal);

            for (Categoria cat : categoriasOrdenadas) {
                List<Producto> productos = productoDAO.listarPorCategoria(cat.getidCategoria());
                menuPorCategoria.put(cat, productos);

                for (Producto p : productos) {
                    List<Producto_variante> variantes = varianteDAO.listarPorProducto(p.getidProducto());
                    if (!variantes.isEmpty()) {
                        variantesPorProducto.put(p.getidProducto(), variantes);
                    }
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "No se pudo cargar el menú: " + e.getMessage());
        }

        request.setAttribute("menuPorCategoria", menuPorCategoria);
        request.setAttribute("variantesPorProducto", variantesPorProducto);

        request.getRequestDispatcher("/Vista/Menu.jsp").forward(request, response);
    }

    /**
     * Posición de una categoría dentro del orden preferido del menú.
     * Si no está en la lista devuelve un número muy alto, para que quede
     * después de todas las que sí fueron nombradas.
     */
    private static int rangoPreferido(Categoria cat, List<String> ordenPreferido) {
        String nombre = cat.getnombre_categoria() == null
                ? "" : cat.getnombre_categoria().trim();
        for (int i = 0; i < ordenPreferido.size(); i++) {
            if (ordenPreferido.get(i).equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }
}
