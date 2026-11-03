/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexion implements AutoCloseable {
    private Connection conn;
    private String driver = "com.mysql.cj.jdbc.Driver";

    // En Railway, el servicio de MySQL crea automáticamente estas variables
    // de entorno (MYSQLHOST, MYSQLPORT, MYSQLDATABASE, MYSQLUSER,
    // MYSQLPASSWORD) en el servicio del backend, cuando se agregan como
    // "referencia" desde la pestaña Variables. Si esas variables no existen
    // (por ejemplo corriendo local en NetBeans con XAMPP/MySQL Workbench),
    // se usan los valores de respaldo de siempre.
    private String host = System.getenv().getOrDefault("MYSQLHOST", "localhost");
    private String port = System.getenv().getOrDefault("MYSQLPORT", "3307");
    private String baseDatos = System.getenv().getOrDefault("MYSQLDATABASE", "papasauriosdb");
    private String user = System.getenv().getOrDefault("MYSQLUSER", "root");
    private String password = System.getenv().getOrDefault("MYSQLPASSWORD", "");

    private String url = "jdbc:mysql://" + host + ":" + port + "/" + baseDatos
            + "?useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC"
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

    // Antes nadie cerraba nunca la conexión que se abre en el constructor
    // — cada consulta dejaba una conexión abierta para siempre, hasta que
    // Railway/MySQL se quedaba sin cupo y todo empezaba a fallar con
    // "con is null". Al implementar AutoCloseable, cada DAO puede usar
    // "try (conexion; PreparedStatement ps = ...)" y esta conexión se
    // cierra sola al terminar, sin importar si hubo error o no.
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
