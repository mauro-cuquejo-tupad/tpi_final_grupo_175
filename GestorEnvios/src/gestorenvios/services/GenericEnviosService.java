package gestorenvios.services;

import gestorenvios.entities.Pedido;

import java.sql.SQLException;

public interface GenericEnviosService<T> extends GenericService<T> {
    T buscarPorTracking(String tracking) throws Exception;
    T buscarPorNumeroPedido(String numero) throws Exception;

    Long obtenerCantidadTotalDeEnvios() throws SQLException;
    Long crearEnvioYActualizarPedido(T envio, Pedido pedido) throws Exception;
}
