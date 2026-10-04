package pe.pcsac.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexion centralizada a MySQL. Ninguna otra clase construye su propia
 * conexion: los DAO la piden aqui y la cierran con try-with-resources.
 *
 * Los datos de acceso se leen de db.properties si existe; si no, se usan
 * los valores por defecto de un entorno local.
 */
public final class ConexionDB {

    private static final String ARCHIVO = "db.properties";
    private static String url;
    private static String usuario;
    private static String clave;

    static {
        Properties p = new Properties();
        // 1) db.properties junto al ejecutable (carpeta de trabajo)
        java.io.File local = new java.io.File(ARCHIVO);
        if (local.isFile()) {
            try (InputStream in = new java.io.FileInputStream(local)) {
                p.load(in);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + local.getAbsolutePath());
            }
        } else {
            // 2) db.properties dentro del classpath
            try (InputStream in = ConexionDB.class.getClassLoader().getResourceAsStream(ARCHIVO)) {
                if (in != null) p.load(in);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + ARCHIVO + ", se usan valores por defecto.");
            }
        }
        url = p.getProperty("db.url",
                "jdbc:mysql://localhost:3306/pcsac_certificados"
                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima");
        usuario = p.getProperty("db.usuario", "root");
        clave = p.getProperty("db.clave", "");
    }

    private ConexionDB() { }

    /** Devuelve una conexion nueva; quien la pide es responsable de cerrarla. */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    /** Comprueba que la base responde; util al arrancar la aplicacion. */
    public static boolean probar() {
        try (Connection c = obtener()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("Error de conexion: " + e.getMessage());
            return false;
        }
    }

    public static String getUrl() { return url; }
}
