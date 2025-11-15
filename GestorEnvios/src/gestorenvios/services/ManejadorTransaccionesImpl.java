package gestorenvios.services;

import gestorenvios.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class ManejadorTransaccionesImpl {

    public <T> T execute(ManejadorTransacciones<T> callback) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            T result = callback.apply(conn);
            conn.commit();
            return result;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException _) {
                //ignorar excepcion
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException _) {
                //ignorar excepcion
            }
        }
    }
}
