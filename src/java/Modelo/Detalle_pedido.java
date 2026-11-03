package Modelo;

public class Detalle_pedido {

    private int idDetalle;
    private int cantidad;
    private int precio_unitario;
    private String nota;
    private int Pedido_idPedido;
    private int Producto_idProducto;
    private Integer Producto_variante_idVariante; // puede ser null si el producto no tiene variantes

    public int getidDetalle() {
        return idDetalle;
    }

    public void setidDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getcantidad() {
        return cantidad;
    }

    public void setcantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getprecio_unitario() {
        return precio_unitario;
    }

    public void setprecio_unitario(int precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public String getnota() {
        return nota;
    }

    public void setnota(String nota) {
        this.nota = nota;
    }

    public int getPedido_idPedido() {
        return Pedido_idPedido;
    }

    public void setPedido_idPedido(int Pedido_idPedido) {
        this.Pedido_idPedido = Pedido_idPedido;
    }

    public int getProducto_idProducto() {
        return Producto_idProducto;
    }

    public void setProducto_idProducto(int Producto_idProducto) {
        this.Producto_idProducto = Producto_idProducto;
    }

    public Integer getProducto_variante_idVariante() {
        return Producto_variante_idVariante;
    }

    public void setProducto_variante_idVariante(Integer Producto_variante_idVariante) {
        this.Producto_variante_idVariante = Producto_variante_idVariante;
    }

    public int getsubtotal() {
        return cantidad * precio_unitario;
    }
}
