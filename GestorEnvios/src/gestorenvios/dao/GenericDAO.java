/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package gestorenvios.dao;

import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Grupo_175
 * @param <T>
 * @param <K>
 */
public interface GenericDAO<T> {

    public void insertar(T entidad) throws Exception;

    public void insertTx(T entidad, Connection conn) throws Exception;

    public void actualizar(T entidad) throws Exception;

    public void eliminarLogico(Long id) throws Exception; // BORRADO LÓGICO. UPDATE

    T buscarPorId(Long id) throws Exception; // SELECT * FROM... WHERE id = ? AND eliminado = 0 (para sacar eliminados de la vista)

    List<T> buscarTodos() throws Exception; // SELECT * FROM... WHERE eliminado = 0 (para sacar los eliminados de la vista)

}
