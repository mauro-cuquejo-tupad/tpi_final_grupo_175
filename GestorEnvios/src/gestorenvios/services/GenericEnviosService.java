package gestorenvios.services;

import java.sql.SQLException;

public interface GenericEnviosService<T> extends GenericService<T> {
    T buscarPorTracking(String tracking);

    void eliminarPorTracking(String tracking);

    Long obtenerCantidadTotalDeEnvios() throws SQLException;
}
