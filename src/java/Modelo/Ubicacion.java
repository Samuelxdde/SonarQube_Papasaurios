package Modelo;

import java.sql.Timestamp;

/**
 * Última posición GPS reportada por el repartidor para un pedido
 * específico. Vive en su propia tabla (Ubicacion_pedido) en vez de
 * dentro de Pedido porque se sobrescribe constantemente mientras el
 * repartidor está en camino (cada pocos segundos), y no tiene
 * sentido cargar ese ruido en la tabla principal de pedidos.
 */
public class Ubicacion {

    private int idPedido;
    private double latitud;
    private double longitud;
    private Timestamp actualizado;

    public Ubicacion() {
    }

    public int getidPedido() {
        return idPedido;
    }

    public void setidPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public double getlatitud() {
        return latitud;
    }

    public void setlatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getlongitud() {
        return longitud;
    }

    public void setlongitud(double longitud) {
        this.longitud = longitud;
    }

    public Timestamp getactualizado() {
        return actualizado;
    }

    public void setactualizado(Timestamp actualizado) {
        this.actualizado = actualizado;
    }
}
