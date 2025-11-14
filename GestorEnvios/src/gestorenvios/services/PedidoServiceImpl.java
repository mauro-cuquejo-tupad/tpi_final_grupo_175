package gestorenvios.services;

import gestorenvios.dao.PedidosDAO;
import gestorenvios.entities.Envios;
import gestorenvios.entities.Pedidos;

import java.util.List;

public class PedidoServiceImpl implements GenericPedidosService<Pedidos> {
    PedidosDAO pedidosDAO;
    GenericService<Envios> envioService;

    public PedidoServiceImpl(PedidosDAO pedidoDAO, GenericService<Envios> envioService) {
        this.pedidosDAO = pedidoDAO;
        this.envioService = envioService;
    }

    @Override
    public void crear(Pedidos entity) {
        System.out.println("Creando pedido: " + entity);
    }

    @Override
    public List<Pedidos> buscarTodos() {
        List<Pedidos> resultados = List.of();
        try {
            resultados = pedidosDAO.buscarTodos();
        } catch (Exception e) {
            System.out.println("Falló la búsqueda de pedidos: " + e.getMessage());
        }
        return resultados;
    }

    @Override
    public Pedidos buscarPorId(Long id) {
        return null;
    }

    @Override
    public void actualizar(Pedidos pedido) {
        System.out.println("Actualizando pedido: " + pedido);
    }

    @Override
    public void eliminar(Long id) {
        System.out.println("Eliminando pedido con id: " + id);
    }

    @Override
    public Pedidos buscarPorTracking(String tracking) {
        return null;
    }

    @Override
    public void eliminarEnvioDePedido(Long idPedido) {
        System.out.println("Eliminando envio del pedido con id: " + idPedido);
    }
}
