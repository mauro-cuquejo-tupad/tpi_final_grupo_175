package gestorenvios.services;

import java.sql.SQLException;

public interface GenericPedidosService<T> extends GenericService<T> {
    void eliminarEnvioDePedido(Long idPedido);

    T buscarPorNumeroPedido(String numero) throws Exception;
    T buscarPorNumeroTracking(String tracking) throws Exception;

    Long obtenerCantidadTotalDePedidos() throws SQLException;

    void eliminarPorNumero(String numero) throws Exception;
}
