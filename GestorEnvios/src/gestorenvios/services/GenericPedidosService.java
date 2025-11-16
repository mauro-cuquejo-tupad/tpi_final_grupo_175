package gestorenvios.services;

import gestorenvios.models.exceptions.ActualizacionEntityException;
import gestorenvios.models.exceptions.ConsultaEntityException;
import gestorenvios.models.exceptions.EliminacionEntityException;

import java.sql.Connection;
import java.util.List;

public interface GenericPedidosService<T> extends GenericService<T> {
    void eliminarEnvioDePedido(Long idPedido) throws EliminacionEntityException;

    void actualizar(T pedido, Connection conn) throws ActualizacionEntityException;

    T buscarPorNumeroPedido(String numero) throws ConsultaEntityException;

    T buscarPorNumeroTracking(String tracking) throws ConsultaEntityException;

    List<T> buscarPorCliente(String cliente, Long cantidad, Long pagina);

    Long obtenerCantidadTotalDePedidos() throws ConsultaEntityException;

    Long obtenerCantidadTotalDePedidosPorCliente(String clienteNombre);

    void eliminarPorNumero(String numero) throws EliminacionEntityException;


}
