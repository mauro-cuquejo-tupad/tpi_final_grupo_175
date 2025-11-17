package gestorenvios.ui.console.controllers;

import java.sql.Connection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;

import gestorenvios.config.DatabaseConnection;

public class DbInitController {

    private DbInitController() {
        // Constructor privado para evitar instanciación
    }

    public static void inicializarBaseDeDatos() throws SQLException {
        System.out.println("\n--- INICIALIZAR BASE DE DATOS ---");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Desactivar la verificación de claves foráneas temporalmente
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");

            // Eliminar tablas si existen
            stmt.execute("DROP TABLE IF EXISTS Empresas;");
            stmt.execute("DROP TABLE IF EXISTS Envio;");
            stmt.execute("DROP TABLE IF EXISTS Estados_Envio;");
            stmt.execute("DROP TABLE IF EXISTS Estados_Pedido;");
            stmt.execute("DROP TABLE IF EXISTS Pedido;");
            stmt.execute("DROP TABLE IF EXISTS Tipos_Envio;");

            // Crea tabla de empresas
            stmt.execute("""
                    CREATE TABLE `Empresas` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `nombre` (`nombre`)
                    ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
                    """);

            // Crear tabla de envios
            stmt.execute("""
                    CREATE TABLE `Envio` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `eliminado` tinyint(1) NOT NULL DEFAULT '0',
                    `tracking` varchar(40) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
                    `id_empresa` int DEFAULT NULL,
                    `id_tipo_envio` int DEFAULT NULL,
                    `costo` decimal(10,2) DEFAULT NULL,
                    `fecha_despacho` date DEFAULT NULL,
                    `fecha_estimada` date DEFAULT NULL,
                    `id_estado_envio` int NOT NULL,
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `tracking` (`tracking`),
                    KEY `id_empresa` (`id_empresa`),
                    KEY `id_tipo_envio` (`id_tipo_envio`),
                    KEY `id_estado_envio` (`id_estado_envio`),
                    KEY `idx_costo_envio` (`costo`),
                    CONSTRAINT `Envio_ibfk_1` FOREIGN KEY (`id_empresa`) REFERENCES `Empresas` (`id`),
                    CONSTRAINT `Envio_ibfk_2` FOREIGN KEY (`id_tipo_envio`) REFERENCES `Tipos_Envio` (`id`),
                    CONSTRAINT `Envio_ibfk_3` FOREIGN KEY (`id_estado_envio`) REFERENCES `Estados_Envio` (`id`),
                    CONSTRAINT `chk_costo_positivo` CHECK ((`costo` >= 0))
                    ) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
                    """);

            // Crear tabla de estados de envío
            stmt.execute("""
                    CREATE TABLE `Estados_Envio` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `nombre` (`nombre`)
                    ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
                    """);

            // Crear tabla de estados de pedido
            stmt.execute("""
                    CREATE TABLE `Estados_Pedido` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `nombre` (`nombre`)
                    ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
                    """);

            // Crear tabla de pedidos
            stmt.execute("""
                    CREATE TABLE `Pedido` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `eliminado` tinyint(1) NOT NULL DEFAULT '0',
                    `numero` varchar(20) COLLATE utf8mb4_spanish_ci NOT NULL,
                    `fecha` date NOT NULL,
                    `cliente_nombre` varchar(120) COLLATE utf8mb4_spanish_ci NOT NULL,
                    `total` decimal(12,2) DEFAULT NULL,
                    `id_estado_pedido` int NOT NULL,
                    `id_envio` int DEFAULT NULL,
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `numero` (`numero`),
                    UNIQUE KEY `id_envio` (`id_envio`),
                    KEY `id_estado_pedido` (`id_estado_pedido`),
                    CONSTRAINT `Pedido_ibfk_1` FOREIGN KEY (`id_estado_pedido`) REFERENCES `Estados_Pedido` (`id`),
                    CONSTRAINT `Pedido_ibfk_2` FOREIGN KEY (`id_envio`) REFERENCES `Envio` (`id`),
                    CONSTRAINT `chk_formato_numero_pedido` CHECK (regexp_like(`numero`,_utf8mb4'^PED-[0-9]{8}$')),
                    CONSTRAINT `chk_total_positivo` CHECK ((`total` >= 0))
                    ) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
                    """);

            // Tabla de tipos de envío
            stmt.execute("""
                    CREATE TABLE `Tipos_Envio` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `nombre` (`nombre`)
                    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
                    """);

            // Reactivar la verificación de claves foráneas
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");

            System.out.println("✅ Base de datos inicializada correctamente (tablas 'pedidos' y 'envios' creadas/recreadas).");

        } catch (Exception e) {
            throw new SQLException("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    public static void cargarDatosIniciales() throws SQLException, IOException {
        System.out.println("\n--- CARGANDO DATOS DE PRUEBA ---");
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                InputStream isr = DbInitController.class.getResourceAsStream("/resources/db/tfi_bd_grupo175_dump.sql");
                BufferedReader reader = new BufferedReader(new InputStreamReader(isr))) {

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line);
                if (line.endsWith(";")) {
                    stmt.execute(sql.toString());
                    sql.setLength(0); // Limpiar el StringBuilder para la siguiente sentencia
                }
            }
            System.out.println("✅ Datos de prueba cargados correctamente.");
        } catch (SQLException | IOException e) {
            throw new SQLException("Error al cargar datos iniciales: " + e.getMessage());
        }
    }
    
    public static boolean checkConnection() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            throw new SQLException("Error al verificar la conexión: " + e.getMessage());
        }
    }

    public static boolean hasTables() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SHOW TABLES");
            if (rs.next()) {
                return true; // Si hay al menos una tabla, asumimos que la BD está poblada
            }
            return false; // No se encontraron tablas
        } catch (SQLException e) {
            throw new SQLException("Error al verificar las tablas: " + e.getMessage());
        }
    }
}
