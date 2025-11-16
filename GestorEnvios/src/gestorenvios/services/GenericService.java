package gestorenvios.services;

import java.util.List;

public interface GenericService<T> {
    void crear(T entity) throws Exception;

    List<T> buscarTodos(Long cantidad, Long pagina) throws Exception;

    T buscarPorId(Long id) throws Exception;

    void actualizar(T entity) throws Exception;

    void eliminar(Long id) throws Exception;
}
