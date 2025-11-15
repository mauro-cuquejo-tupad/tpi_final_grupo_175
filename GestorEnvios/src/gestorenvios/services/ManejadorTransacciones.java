package gestorenvios.services;

import java.sql.Connection;

public interface ManejadorTransacciones<T> {
    T apply(Connection conn) throws Exception;
}
