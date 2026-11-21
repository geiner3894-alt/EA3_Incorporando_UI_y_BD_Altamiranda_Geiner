import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PruebaJdbc {
    public String conectar(String url, String usuario, String clave)
            throws ClassNotFoundException, SQLException {
        // Si la dirección es de SQLite, intento cargar su controlador antes de abrir la base.
        if (url.startsWith("jdbc:sqlite:")) {
            Class.forName("org.sqlite.JDBC");
        }

        // Abro la conexión con los datos recibidos. Al salir de este bloque, Java la cierra automáticamente.
        try (Connection conexion =
                DriverManager.getConnection(url, usuario, clave)) {
            // Leo el nombre del motor desde la conexión abierta para mostrar qué motor respondió.
            String motor = conexion.getMetaData().getDatabaseProductName();
            return "Conexión exitosa.\nMotor de base de datos: " + motor;
        }
    }
}
