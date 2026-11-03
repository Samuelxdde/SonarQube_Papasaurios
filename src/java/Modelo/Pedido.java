package Modelo;

import java.sql.Date;
import java.sql.Time;

public class Pedido {

    private int idPedido;
    private Date fecha;
    private Time hora;
    private Time hora_estimada;
    private String tipo_entrega;
    private String direccion_entrega;
    private int total;
    private int Usuarios_idUsuarios;
    private int Estado_pedido_idEstado_pedido;
    private int Pagos_idPagos;
    private Integer Repartidor_idUsuarios; // null hasta que un repartidor lo toma

    public Pedido() {
    }

    public Integer getRepartidor_idUsuarios() {
        return Repartidor_idUsuarios;
    }

    public void setRepartidor_idUsuarios(Integer Repartidor_idUsuarios) {
        this.Repartidor_idUsuarios = Repartidor_idUsuarios;
    }

    public int getidPedido() {
        return idPedido;
    }

    public void setidPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Date getfecha() {
        return fecha;
    }

    public void setfecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time gethora() {
        return hora;
    }

    public void sethora(Time hora) {
        this.hora = hora;
    }

    public Time gethora_estimada() {
        return hora_estimada;
    }

    public void sethora_estimada(Time hora_estimada) {
        this.hora_estimada = hora_estimada;
    }

    public String gettipo_entrega() {
        return tipo_entrega;
    }

    public void settipo_entrega(String tipo_entrega) {
        this.tipo_entrega = tipo_entrega;
    }

    public String getdireccion_entrega() {
        return direccion_entrega;
    }

    public void setdireccion_entrega(String direccion_entrega) {
        this.direccion_entrega = direccion_entrega;
    }

    public int gettotal() {
        return total;
    }

    public void settotal(int total) {
        this.total = total;
    }

    public int getUsuarios_idUsuarios() {
        return Usuarios_idUsuarios;
    }

    public void setUsuarios_idUsuarios(int Usuarios_idUsuarios) {
        this.Usuarios_idUsuarios = Usuarios_idUsuarios;
    }

    public int getEstado_pedido_idEstado_pedido() {
        return Estado_pedido_idEstado_pedido;
    }

    public void setEstado_pedido_idEstado_pedido(int Estado_pedido_idEstado_pedido) {
        this.Estado_pedido_idEstado_pedido = Estado_pedido_idEstado_pedido;
    }

    public int getPagos_idPagos() {
        return Pagos_idPagos;
    }

    public void setPagos_idPagos(int Pagos_idPagos) {
        this.Pagos_idPagos = Pagos_idPagos;
    }
}
