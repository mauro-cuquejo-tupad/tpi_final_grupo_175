package gestorenvios.services;

public interface GenericPedidosService<T> extends GenericService<T> {
    void eliminarEnvioDePedido(Long idPedido);

    T buscarPorTracking(String tracking);
}
