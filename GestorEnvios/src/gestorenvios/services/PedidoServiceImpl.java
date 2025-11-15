package gestorenvios.services;

import com.mysql.cj.util.StringUtils;
import gestorenvios.dao.PedidoDAO;
import gestorenvios.entities.Envio;
import gestorenvios.entities.Pedido;

import java.sql.SQLException;
import java.util.List;

public class PedidoServiceImpl implements GenericPedidosService<Pedido> {
    PedidoDAO pedidoDAO;
    GenericService<Envio> envioService;

    public PedidoServiceImpl(PedidoDAO pedidoDAO, GenericService<Envio> envioService) {
        this.pedidoDAO = pedidoDAO;
        this.envioService = envioService;
    }

    @Override
    public void crear(Pedido pedido) throws Exception {
            validarPedido(pedido);
            pedido.setNumero(generarNuevoNumeroPedido());

            System.out.println("⌛ Creando pedido: " + pedido);
            pedidoDAO.insertar(pedido);
    }

    private String generarNuevoNumeroPedido() throws SQLException {

        String ultimoPedido = pedidoDAO.buscarUltimoNumeroPedido();

        if (ultimoPedido == null || ultimoPedido.isEmpty()) {
            throw new SQLException("No se pudo obtener el último número de pedido.");
        }
        String[] partes = ultimoPedido.split("-");
        int numero = Integer.parseInt(partes[1]);
        numero++;
        return String.format("PED-%04d", numero);
    }

    private void validarPedido(Pedido pedido) {
        if(StringUtils.isNullOrEmpty(pedido.getClienteNombre())) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (pedido.getTotal() == null || pedido.getTotal() < 0) {
            throw new IllegalArgumentException("El total del pedido no puede ser nulo o negativo.");
        }
    }

    @Override
    public List<Pedido> buscarTodos(Long cantidad, Long pagina) {
        List<Pedido> resultados = List.of();
        try {
            resultados = pedidoDAO.buscarTodos(cantidad, pagina);
        } catch (Exception e) {
            System.out.println("Falló la búsqueda de pedidos: " + e.getMessage());
        }
        return resultados;
    }

    @Override
    public Pedido buscarPorId(Long id) {
        return null;
    }

    @Override
    public void actualizar(Pedido pedido) throws Exception {
        System.out.println("⌛ Actualizando pedido: " + pedido);
        pedidoDAO.actualizar(pedido);
    }

    @Override
    public void eliminar(Long id) {
        System.out.println("Eliminando pedido con id: " + id);
    }

    @Override
    public Pedido buscarPorNumeroPedido(String numero) throws Exception {
        return pedidoDAO.buscarPorNumero(numero);
    }

    @Override
    public Pedido buscarPorNumeroTracking(String tracking) throws Exception {
        return pedidoDAO.buscarPorTracking(tracking);
    }

    @Override
    public Long obtenerCantidadTotalDePedidos() throws SQLException {
        return pedidoDAO.obtenerCantidadTotalDePedidos();
    }

    @Override
    public void eliminarPorNumero(String numero) throws Exception {
        pedidoDAO.eliminarLogicoPorNumero(numero);

    }

    @Override
    public void eliminarEnvioDePedido(Long idPedido) {
        System.out.println("Eliminando envio del pedido con id: " + idPedido);
    }
}
