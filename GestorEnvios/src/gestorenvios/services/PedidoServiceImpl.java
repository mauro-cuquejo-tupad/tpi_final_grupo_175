package gestorenvios.services;

import com.mysql.cj.util.StringUtils;
import gestorenvios.config.DatabaseConnection;
import gestorenvios.config.TransactionManager;
import gestorenvios.dao.PedidoDAO;
import gestorenvios.entities.Pedido;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PedidoServiceImpl implements GenericPedidosService<Pedido> {
    PedidoDAO pedidoDAO;

    public PedidoServiceImpl(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public void crear(Pedido pedido) throws Exception {
        validarPedido(pedido);
        pedido.setNumero(generarNuevoNumeroPedido());

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
        return String.format("PED-%08d", numero);
    }

    private void validarPedido(Pedido pedido) {
        if (StringUtils.isNullOrEmpty(pedido.getClienteNombre())) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (pedido.getTotal() == null || pedido.getTotal() < 0) {
            throw new IllegalArgumentException("El total del pedido no puede ser nulo o negativo.");
        }
    }

    @Override
    public List<Pedido> buscarTodos(Long cantidad, Long pagina) throws Exception {
        return pedidoDAO.buscarTodos(cantidad, pagina);
    }

    @Override
    public Pedido buscarPorId(Long id) {
        return null;
    }

    @Override
    public void actualizar(Pedido pedido, Connection conn) throws Exception {
        pedidoDAO.actualizar(pedido, conn);
    }

    @Override
    public void actualizar(Pedido pedido) throws Exception {
        pedidoDAO.actualizar(pedido);
    }

    @Override
    public void eliminar(Long id) throws Exception {
        pedidoDAO.eliminarLogico(id);
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
    public Long obtenerCantidadTotalDePedidos() throws Exception {
        return pedidoDAO.obtenerCantidadTotalDePedidos();
    }

    @Override
    public void eliminarPorNumero(String numero) throws Exception {
        pedidoDAO.eliminarLogicoPorNumero(numero);

    }

    @Override
    public void eliminarEnvioDePedido(Long idPedido) throws Exception {
        TransactionManager transactionManager = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            transactionManager = new TransactionManager(conn);

            transactionManager.startTransaction();
            pedidoDAO.desvincularEnvioTx(idPedido, conn);
            transactionManager.commit();
        } catch (Exception e) {
            System.out.println("Error en la transacción: " + e.getMessage());
            if (transactionManager != null) {
                transactionManager.rollback();
            }
            throw e;
        } finally {
            // Cerrar el TransactionManager y la conexión
            // Esto se hace en el método close() del TransactionManager
            if (transactionManager != null) {
                transactionManager.close();
            }
        }

    }
}
