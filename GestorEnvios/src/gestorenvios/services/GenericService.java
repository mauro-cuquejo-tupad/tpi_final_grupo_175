package gestorenvios.services;

import java.util.List;

public interface GenericService<T> {
    void crear(T entity) throws Exception;

    List<T> buscarTodos(Long cantidad, Long pagina);

    T buscarPorId(Long id);

    void actualizar(T pedido) throws Exception;

    void eliminar(Long id);
}
