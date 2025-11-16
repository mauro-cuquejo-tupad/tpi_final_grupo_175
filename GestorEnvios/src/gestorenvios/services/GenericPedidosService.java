package gestorenvios.services;

import java.sql.Connection;

public interface GenericPedidosService<T> extends GenericService<T> {
    void eliminarEnvioDePedido(Long idPedido) throws Exception;

    void actualizar(T pedido, Connection conn) throws Exception;

    T buscarPorNumeroPedido(String numero) throws Exception;

    T buscarPorNumeroTracking(String tracking) throws Exception;

    Long obtenerCantidadTotalDePedidos() throws Exception;

    void eliminarPorNumero(String numero) throws Exception;
}
