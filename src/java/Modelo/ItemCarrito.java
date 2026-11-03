package Modelo;

/**
 * Representa una línea del carrito de compras mientras el usuario
 * sigue navegando el menú, antes de confirmar el pedido. Vive en la
 * sesión HTTP (no en la base de datos) — solo cuando el usuario hace
 * checkout, cada ItemCarrito se convierte en un Detalle_pedido real.
 */
public class ItemCarrito implements java.io.Serializable {

    private int idProducto;
    private String nombreProducto;
    private Integer idVariante;     // null si el producto no tiene variantes
    private String nombreVariante;  // null si el producto no tiene variantes
    private int precioUnitario;
    private int cantidad;
    private String nota;
    // Categoría del producto al momento de agregarlo al carrito. Se usa en
    // el checkout (PedidoServlet) para exigir que el pedido tenga al menos
    // un plato armado y no sea solo ingredientes/adicionales sueltos.
    private int categoriaIdCategoria;

    public ItemCarrito() {
    }

    public ItemCarrito(int idProducto, String nombreProducto, Integer idVariante,
                        String nombreVariante, int precioUnitario, int cantidad, String nota,
                        int categoriaIdCategoria) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.idVariante = idVariante;
        this.nombreVariante = nombreVariante;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.nota = nota;
        this.categoriaIdCategoria = categoriaIdCategoria;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getIdVariante() {
        return idVariante;
    }

    public void setIdVariante(Integer idVariante) {
        this.idVariante = idVariante;
    }

    public String getNombreVariante() {
        return nombreVariante;
    }

    public void setNombreVariante(String nombreVariante) {
        this.nombreVariante = nombreVariante;
    }

    public int getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(int precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public int getCategoriaIdCategoria() {
        return categoriaIdCategoria;
    }

    public void setCategoriaIdCategoria(int categoriaIdCategoria) {
        this.categoriaIdCategoria = categoriaIdCategoria;
    }

    public int getSubtotal() {
        return precioUnitario * cantidad;
    }

    /**
     * Clave única para identificar la línea dentro del carrito:
     * mismo producto + misma variante = se suma cantidad, no se
     * duplica la línea. Distinta variante = línea aparte.
     */
    public String getClaveLinea() {
        return idProducto + "-" + (idVariante != null ? idVariante : 0);
    }
}
