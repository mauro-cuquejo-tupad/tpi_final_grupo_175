package gestorenvios.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Grupo 175
 */
public class DatabaseConnection {

    private static final String DB_URL = ApplicationConfig.get("db.url");
    private static final String DB_USER = ApplicationConfig.get("db.user");
    private static final String DB_PASSWORD = ApplicationConfig.get("db.password");
    private static final String DB_DRIVER = ApplicationConfig.get("db.driver");

    static {
        try {
            Class.forName(DB_DRIVER);

            validateConfiguration();
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "Error: No se encontró el driver JDBC de MySQL: "
                            + e.getMessage());
        } catch (IllegalStateException e) {
            throw new ExceptionInInitializerError(
                    "Error en la configuración de la base de datos: "
                            + e.getMessage());
        }
    }

    private DatabaseConnection() {
        throw new UnsupportedOperationException(
                "Esta es una clase utilitaria y no debe ser instanciada");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void validateConfiguration() {
        if (DB_URL == null || DB_URL.trim().isEmpty()) {
            throw new IllegalStateException(
                    "La URL de la base de datos no está configurada");
        }
        if (DB_USER == null || DB_USER.trim().isEmpty()) {
            throw new IllegalStateException(
                    "El usuario de la base de datos no está configurado");
        }
        if (DB_PASSWORD == null) {
            throw new IllegalStateException(
                    "La contraseña de la base de datos no está configurada");
        }
    }
}
