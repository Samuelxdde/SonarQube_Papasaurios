/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexion implements AutoCloseable {
    private Connection conn;
    private String driver = "com.mysql.cj.jdbc.Driver";

    // Mismo patrón que Controlador.Conexion (que es la que realmente usan
    // los DAO del proyecto): lee las variables de entorno que crea Railway
    // para el servicio de MySQL, con respaldo local si no existen.
    private String host = System.getenv().getOrDefault("MYSQLHOST", "localhost");
    private String port = System.getenv().getOrDefault("MYSQLPORT", "3307");
    private String baseDatos = System.getenv().getOrDefault("MYSQLDATABASE", "papasauriosdb");
    private String user = System.getenv().getOrDefault("MYSQLUSER", "root");
    private String password = System.getenv().getOrDefault("MYSQLPASSWORD", "");

    private String url = "jdbc:mysql://" + host + ":" + port + "/" + baseDatos
            + "?useSSL=false&useTimezone=true&serverTimezone=UTC"
            + "&socketTimeout=30000&connectTimeout=10000";
    private String viverodb;

     public Conexion() {
    conn = null;
    try {
        Class.forName(driver);
        conn = DriverManager.getConnection(url, user, password);
        if (conn == null) {
            System.out.println("No se estableció la conexion" + "\n" + url);
        } else {
            System.out.println("Conexión Establecida ");
        }
    } catch (Exception ex) {
        System.err.println(ex.getMessage());
    }
}
public Connection getConn() {
    return conn;
}

    @Override
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
