package Modelo;

public class Producto {

    private int idProducto;
    private String nombre_producto;
    private String descripcion_producto;
    private int precio_base;
    private boolean disponible;
    private int Categoria_idCategoria;
    private String imagen_url;

    public int getidProducto() {
        return idProducto;
    }

    public void setidProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getnombre_producto() {
        return nombre_producto;
    }

    public void setnombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public String getdescripcion_producto() {
        return descripcion_producto;
    }

    public void setdescripcion_producto(String descripcion_producto) {
        this.descripcion_producto = descripcion_producto;
    }

    public int getprecio_base() {
        return precio_base;
    }

    public void setprecio_base(int precio_base) {
        this.precio_base = precio_base;
    }

    public boolean isdisponible() {
        return disponible;
    }

    public void setdisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public int getCategoria_idCategoria() {
        return Categoria_idCategoria;
    }

    public void setCategoria_idCategoria(int Categoria_idCategoria) {
        this.Categoria_idCategoria = Categoria_idCategoria;
    }

    public String getimagen_url() {
        return imagen_url;
    }

    public void setimagen_url(String imagen_url) {
        this.imagen_url = imagen_url;
    }
}
