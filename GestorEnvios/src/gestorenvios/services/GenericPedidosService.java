package gestorenvios.services;

import gestorenvios.entities.Pedido;

import java.sql.Connection;

public interface GenericPedidosService<T> extends GenericService<T> {
    void eliminarEnvioDePedido(Long idPedido);

    void actualizar(Pedido pedido, Connection conn) throws Exception;

    T buscarPorNumeroPedido(String numero) throws Exception;
    T buscarPorNumeroTracking(String tracking) throws Exception;

    Long obtenerCantidadTotalDePedidos() throws Exception;

    void eliminarPorNumero(String numero) throws Exception;
}
