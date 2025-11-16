package gestorenvios.services;

import gestorenvios.models.exceptions.ActualizacionEntityException;
import gestorenvios.models.exceptions.ConsultaEntityException;
import gestorenvios.models.exceptions.EliminacionEntityException;

import java.sql.Connection;

public interface GenericPedidosService<T> extends GenericService<T> {
    void eliminarEnvioDePedido(Long idPedido) throws EliminacionEntityException;

    void actualizar(T pedido, Connection conn) throws ActualizacionEntityException;

    T buscarPorNumeroPedido(String numero) throws ConsultaEntityException;

    T buscarPorNumeroTracking(String tracking) throws ConsultaEntityException;

    Long obtenerCantidadTotalDePedidos() throws ConsultaEntityException;

    void eliminarPorNumero(String numero) throws EliminacionEntityException;
}
