package gestorenvios.services;

import java.util.List;

public interface GenericService<T> {
    void crear(T entity);

    List<T> buscarTodos();

    T buscarPorId(Long id);

    void actualizar(T pedido);

    void eliminar(Long id);
}
