package Modelo;

public class Producto_variante {

    private int idVariante;
    private String nombre_variante;
    private int precio_variante;
    private int Producto_idProducto;

    public int getidVariante() {
        return idVariante;
    }

    public void setidVariante(int idVariante) {
        this.idVariante = idVariante;
    }

    public String getnombre_variante() {
        return nombre_variante;
    }

    public void setnombre_variante(String nombre_variante) {
        this.nombre_variante = nombre_variante;
    }

    public int getprecio_variante() {
        return precio_variante;
    }

    public void setprecio_variante(int precio_variante) {
        this.precio_variante = precio_variante;
    }

    public int getProducto_idProducto() {
        return Producto_idProducto;
    }

    public void setProducto_idProducto(int Producto_idProducto) {
        this.Producto_idProducto = Producto_idProducto;
    }
}
