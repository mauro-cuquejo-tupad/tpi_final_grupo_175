package gestorenvios.services;

import com.mysql.cj.util.StringUtils;
import gestorenvios.config.DatabaseConnection;
import gestorenvios.config.TransactionManager;
import gestorenvios.dao.PedidoDAO;
import gestorenvios.entities.Pedido;
import gestorenvios.models.exceptions.ActualizacionEntityException;
import gestorenvios.models.exceptions.ConsultaEntityException;
import gestorenvios.models.exceptions.CreacionEntityException;
import gestorenvios.models.exceptions.EliminacionEntityException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PedidoServiceImpl implements GenericPedidosService<Pedido> {
    PedidoDAO pedidoDAO;

    public PedidoServiceImpl(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public void crear(Pedido pedido) throws CreacionEntityException {
        try {
            validarPedido(pedido);
            pedido.setNumero(generarNuevoNumeroPedido());

            pedidoDAO.insertar(pedido);
        } catch (Exception e) {
            throw new CreacionEntityException("Error al crear el pedido: " + e.getMessage());
        }
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
    public List<Pedido> buscarTodos(Long cantidad, Long pagina) throws ConsultaEntityException {
        try {
            return pedidoDAO.buscarTodos(cantidad, pagina);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al listar pedidos: " + e.getMessage());
        }
    }

    @Override
    public Pedido buscarPorId(Long id) throws ConsultaEntityException {
        try {
            return pedidoDAO.buscarPorId(id);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar pedido por ID: " + e.getMessage());
        }
    }

    @Override
    public Pedido buscarPorNumeroPedido(String numero) throws ConsultaEntityException {
        try {
            return pedidoDAO.buscarPorNumero(numero);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar pedido por número: " + e.getMessage());
        }
    }

    @Override
    public Pedido buscarPorNumeroTracking(String tracking) throws ConsultaEntityException {
        try {
            return pedidoDAO.buscarPorTracking(tracking);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar pedido por número de tracking: " + e.getMessage());
        }
    }

    @Override
    public List<Pedido> buscarPorCliente(String cliente, Long cantidad, Long pagina) {
        try {
            return pedidoDAO.buscarPorClienteNombre(cliente, cantidad, pagina);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar pedido por Cliente: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Pedido pedido, Connection conn) throws ActualizacionEntityException {
        try {
            pedidoDAO.actualizar(pedido, conn);
        } catch (Exception e) {
            throw new ActualizacionEntityException("Error al actualizar el pedido (transaccional): " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Pedido pedido) throws ActualizacionEntityException {
        try {
            pedidoDAO.actualizar(pedido);
        } catch (Exception e) {
            throw new ActualizacionEntityException("Error al actualizar el pedido: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(Long id) throws EliminacionEntityException {
        try {
            pedidoDAO.eliminarLogico(id);
        } catch (Exception e) {
            throw new EliminacionEntityException("Error al eliminar el pedido: " + e.getMessage());
        }
    }

    @Override
    public Long obtenerCantidadTotalDePedidos() throws ConsultaEntityException {
        try {
            return pedidoDAO.obtenerCantidadTotalDePedidos();
        } catch (SQLException e) {
            throw new ConsultaEntityException("Error al obtener la cantidad total de pedidos: " + e.getMessage());
        }
    }

    @Override
    public Long obtenerCantidadTotalDePedidosPorCliente(String clienteNombre) {
        try {
            return pedidoDAO.obtenerCantidadTotalDePedidosPorNombre(clienteNombre);
        } catch (SQLException e) {
            throw new ConsultaEntityException("Error al obtener la cantidad total de pedidos: " + e.getMessage());
        }
    }

    @Override
    public void eliminarPorNumero(String numero) throws EliminacionEntityException {
        try {
            pedidoDAO.eliminarLogicoPorNumero(numero);
        } catch (Exception e) {
            throw new EliminacionEntityException("Error al eliminar el pedido por número: " + e.getMessage());
        }

    }

    @Override
    public void eliminarEnvioDePedido(Long idPedido) throws EliminacionEntityException {
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
            throw new EliminacionEntityException("Error al eliminar el envío del pedido: " + e.getMessage());
        } finally {
            // Cerrar el TransactionManager y la conexión
            // Esto se hace en el método close() del TransactionManager
            if (transactionManager != null) {
                transactionManager.close();
            }
        }

    }
}
