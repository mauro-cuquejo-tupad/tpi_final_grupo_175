package gestorenvios.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public interface GenericDAO<T> {

    void insertarTx(T entity, Connection conn) throws SQLException;

    void actualizarTx(T entity, Connection conn) throws SQLException;

    void eliminarLogicoTx(Long id, Connection conn) throws SQLException;

    T buscarPorId(Long id) throws SQLException;

    List<T> buscarTodos(Long cantidad, Long pagina) throws SQLException;

}
