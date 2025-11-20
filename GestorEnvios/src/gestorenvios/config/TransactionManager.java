package gestorenvios.config;

import gestorenvios.ui.console.utils.ConsoleUtils;

import java.sql.Connection;
import java.sql.SQLException;

/***
 * Gestiona transacciones JDBC sobre una conexión.
 * Permite iniciar, confirmar, revertir y cerrar transacciones de forma segura.
 * Implementa AutoCloseable para facilitar el manejo de recursos.
 */
public class TransactionManager implements AutoCloseable {
    private final Connection conn;
    private boolean transactionActive;

    /***
     * Crea un TransactionManager para la conexión dada.
     * @param conn Conexión JDBC a gestionar
     * @throws IllegalArgumentException si la conexión es null
     */
    public TransactionManager(Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("La conexión no puede ser null");
        }
        this.conn = conn;
        this.transactionActive = false;
    }

    /***
     * Devuelve la conexión gestionada.
     * @return Conexión JDBC
     */
    public Connection getConnection() {
        return conn;
    }

    /***
     * Inicia una transacción desactivando el autocommit.
     * @throws SQLException si la conexión no está disponible o cerrada
     */
    public void startTransaction() throws SQLException {
        if (conn == null) {
            throw new SQLException("No se puede iniciar la transacción: conexión no disponible");
        }
        if (conn.isClosed()) {
            throw new SQLException("No se puede iniciar la transacción: conexión cerrada");
        }
        conn.setAutoCommit(false);
        transactionActive = true;
    }

    /***
     * Confirma la transacción activa.
     * @throws SQLException si no hay transacción activa o no hay conexión
     */
    public void commit() throws SQLException {
        if (conn == null) {
            throw new SQLException("Error al hacer commit: no hay conexión establecida");
        }
        if (!transactionActive) {
            throw new SQLException("No hay una transacción activa para hacer commit");
        }
        conn.commit();
        transactionActive = false;
    }

    /***
     * Revierte la transacción activa si existe.
     */
    public void rollback() {
        if (conn != null && transactionActive) {
            try {
                conn.rollback();
                transactionActive = false;
            } catch (SQLException e) {
                ConsoleUtils.imprimirError("Error durante el rollback: " + e.getMessage());
            }
        }
    }

    /***
     * Cierra la conexión y revierte la transacción si está activa.
     */
    @Override
    public void close() {
        if (conn != null) {
            try {
                if (transactionActive) {
                    rollback();
                }
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                ConsoleUtils.imprimirError("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /***
     * Indica si hay una transacción activa.
     * @return true si la transacción está activa, false si no
     */
    public boolean isTransactionActive() {
        return transactionActive;
    }
}
