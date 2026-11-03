/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.Date;

/**
 *
 * @author Aprendiz
 */
public class Usuarios {
    private int idUsuarios;
    private String nombre;
    private String apellido;
    private String documento; 
    private String telefono;
    private String correo;
    private String clave;
    private Date fecha_nac;
    private Date fecha_cad;
    private boolean checkbox;
    private int Tipo_documento_idTipo_documento;
    private int Roles_idRoles;
    
    
    public int getidUsuarios () {
       return idUsuarios;
    }
    
    public void setidUsuarios (int idUsuarios) {
        this.idUsuarios = idUsuarios;
    }
    
    public String getnombre () {
       return nombre;
    }
    
    public void setnombre (String nombre) {
        this.nombre = nombre;
    }
    
    public String getapellido () {
        return apellido;
    }
    
    public void setapellido (String apellido) {
        this.apellido = apellido;
    }
    
    public String getdocumento () {
    return documento;
    }
    
    public void setdocumento (String documento) {
        this.documento = documento;
    }
    
    public String gettelefono() {
        return telefono;
    }
    
    public void settelefono (String telefono) {
        this.telefono = telefono;
    }
    
    public String getcorreo () {
        return correo;
    }
    
    public void setcorreo (String correo) {
        this.correo = correo;
    }
    
    public String getclave() {
        return clave;
    }
    
    public void setclave (String clave) {
        this.clave = clave;
    }
    
    public Date getfecha_nac (){
        return fecha_nac;
    }
    
    public void setfecha_nac (Date fecha_nac) {
        this.fecha_nac = fecha_nac;
    }
    
    public Date getfecha_cad (){
        return fecha_cad;
    }
    
    public void setfecha_cad (java.util.Date fecha_cad){
        this.fecha_cad = fecha_cad;
    }
    
    public boolean ischeckbox(){
        return checkbox;
    }
    
    public void setcheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

    public int getTipo_documento_idTipo_documento() {
       return Tipo_documento_idTipo_documento;   
    }
    
    public void setTipo_documento_idTipo_documento( int Tipo_documento_idTipo_documento) {
        this.Tipo_documento_idTipo_documento = Tipo_documento_idTipo_documento;
    }
    
    public int getRoles_idRoles() {
        return Roles_idRoles;  
    }
    
    public void setRoles_idRoles( int Roles_idRoles) {
        this.Roles_idRoles = Roles_idRoles; 
    }

    

   
  
}
