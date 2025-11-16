package gestorenvios.services;

import gestorenvios.models.exceptions.ConsultaEntityException;
import gestorenvios.models.exceptions.CreacionEntityException;

public interface GenericEnviosService<T, U> extends GenericService<T> {
    T buscarPorTracking(String tracking) throws ConsultaEntityException;

    T buscarPorNumeroPedido(String numero) throws ConsultaEntityException;

    Long obtenerCantidadTotalDeEnvios() throws ConsultaEntityException;

    String crearEnvioYActualizarPedido(T envio, U pedido) throws CreacionEntityException;
}
