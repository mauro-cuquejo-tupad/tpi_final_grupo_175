package gestorenvios.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @param <T>
 * @author Grupo_175
 */
public interface GenericDAO<T> {

    void insertarTx(T entity, Connection conn) throws SQLException;

    void actualizarTx(T entity, Connection conn) throws SQLException;

    void eliminarLogicoTx(Long id, Connection conn) throws SQLException;

    T buscarPorId(Long id) throws SQLException; // SELECT * FROM... WHERE id = ? AND eliminado = 0 (para sacar eliminados de la vista)

    List<T> buscarTodos(Long cantidad, Long pagina) throws SQLException; // SELECT * FROM... WHERE eliminado = 0 (para sacar los eliminados de la vista)

}
