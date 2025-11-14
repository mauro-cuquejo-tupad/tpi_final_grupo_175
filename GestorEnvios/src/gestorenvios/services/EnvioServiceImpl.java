package gestorenvios.services;

import gestorenvios.dao.EnviosDAO;
import gestorenvios.entities.Envios;

import java.util.List;

public class EnvioServiceImpl implements GenericEnviosService<Envios> {
    EnviosDAO enviosDAO;

    public EnvioServiceImpl(EnviosDAO envioDAO) {
        this.enviosDAO = envioDAO;
    }

    @Override
    public void crear(Envios entity) {
        System.out.println("Creando envio: " + entity);
    }

    @Override
    public List<Envios> buscarTodos() {
        return List.of();
    }

    @Override
    public Envios buscarPorId(Long id) {
        return null;
    }

    @Override
    public void actualizar(Envios pedido) {
        System.out.println("Actualizando envio: " + pedido);
    }

    @Override
    public void eliminar(Long id) {
        System.out.println("Eliminando envio con id: " + id);
    }
}
