package gestorenvios.dao;

import gestorenvios.entities.Envio;

import java.sql.Connection;
import java.util.List;

/**
 *
 * @param <T>
 * @author Grupo_175
 */
public interface GenericDAO<T> {

    void insertar(T entidad, Connection conn) throws Exception;

    public void insertar(T entidad) throws Exception;

    public void insertTx(T entidad, Connection conn) throws Exception;

    public void actualizar(T entidad) throws Exception;

    public void actualizarTx(T entidad, Connection conn) throws Exception;

    public void eliminarLogico(Long id) throws Exception; // BORRADO LÓGICO. UPDATE

    public void eliminarLogicoTx(Long id, Connection conn) throws Exception;

    T buscarPorId(Long id) throws Exception; // SELECT * FROM... WHERE id = ? AND eliminado = 0 (para sacar eliminados de la vista)

    List<T> buscarTodos(Long cantidad, Long pagina) throws Exception; // SELECT * FROM... WHERE eliminado = 0 (para sacar los eliminados de la vista)

}
