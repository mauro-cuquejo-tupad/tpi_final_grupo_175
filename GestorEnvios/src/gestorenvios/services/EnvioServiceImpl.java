package gestorenvios.services;

import gestorenvios.dao.EnvioDAO;
import gestorenvios.entities.Envio;

import java.sql.SQLException;
import java.util.List;

public class EnvioServiceImpl implements GenericEnviosService<Envio> {
    EnvioDAO envioDAO;

    public EnvioServiceImpl(EnvioDAO envioDAO) {
        this.envioDAO = envioDAO;
    }

    @Override
    public void crear(Envio envio) throws Exception {
        System.out.println("Creando envio: " + envio);
        //TODO: validar envio
        envioDAO.insertar(envio);
    }

    @Override
    public List<Envio> buscarTodos(Long cantidad, Long pagina) {
        return List.of();
    }

    @Override
    public Envio buscarPorId(Long id) {
        return null;
    }

    @Override
    public void actualizar(Envio pedido) {
        System.out.println("Actualizando envio: " + pedido);
    }

    @Override
    public void eliminar(Long id) {
        System.out.println("Eliminando envio con id: " + id);
    }

    @Override
    public Envio buscarPorTracking(String tracking) {
        return null;
    }

    @Override
    public void eliminarPorTracking(String tracking) {
        System.out.println("Eliminando envio con tracking: " + tracking);
    }

    @Override
    public Long obtenerCantidadTotalDeEnvios() throws SQLException {
        return envioDAO.obtenerCantidadTotalDeEnvios();
    }
}
