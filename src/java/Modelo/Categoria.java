package Modelo;

public class Categoria {

    private int idCategoria;
    private String nombre_categoria;
    private int orden;

    public int getidCategoria() {
        return idCategoria;
    }

    public void setidCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getnombre_categoria() {
        return nombre_categoria;
    }

    public void setnombre_categoria(String nombre_categoria) {
        this.nombre_categoria = nombre_categoria;
    }

    public int getorden() {
        return orden;
    }

    public void setorden(int orden) {
        this.orden = orden;
    }
}
