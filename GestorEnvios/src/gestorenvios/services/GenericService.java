package gestorenvios.services;

import gestorenvios.models.exceptions.ActualizacionEntityException;
import gestorenvios.models.exceptions.ConsultaEntityException;
import gestorenvios.models.exceptions.CreacionEntityException;
import gestorenvios.models.exceptions.EliminacionEntityException;

import java.util.List;

public interface GenericService<T> {
    String crear(T entity) throws CreacionEntityException;

    List<T> buscarTodos(Long cantidad, Long pagina) throws ConsultaEntityException;

    T buscarPorId(Long id) throws ConsultaEntityException;

    void actualizar(T entity) throws ActualizacionEntityException;

    void eliminar(T entity) throws EliminacionEntityException;
}
