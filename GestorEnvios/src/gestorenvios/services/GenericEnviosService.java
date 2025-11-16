package gestorenvios.services;

import java.sql.SQLException;

public interface GenericEnviosService<T, U> extends GenericService<T> {
    T buscarPorTracking(String tracking) throws Exception;

    T buscarPorNumeroPedido(String numero) throws Exception;

    Long obtenerCantidadTotalDeEnvios() throws SQLException;

    void crearEnvioYActualizarPedido(T envio, U pedido) throws Exception;
}
