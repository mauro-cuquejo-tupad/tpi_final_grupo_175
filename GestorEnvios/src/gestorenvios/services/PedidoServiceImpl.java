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

/***
 * Implementación del servicio genérico para la gestión de pedidos.
 */
public class PedidoServiceImpl implements GenericPedidosService<Pedido> {
    PedidoDAO pedidoDAO;

    /***
     * Constructor de la clase PedidoServiceImpl.
     * @param pedidoDAO DAO para la gestión de pedidos.
     */
    public PedidoServiceImpl(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    /***
     * Crea un nuevo pedido en la base de datos.
     * @param pedido Pedido a crear.
     * @return Número del pedido creado.
     * @throws CreacionEntityException Si ocurre un error durante la creación del pedido.
     */
    @Override
    public String crear(Pedido pedido) throws CreacionEntityException {
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {
            validarPedido(pedido);
            return crearTx(pedido, transactionManager, conn);
        } catch (Exception e) {
            throw new CreacionEntityException("Error al crear el pedido: " + e.getMessage());
        }
    }

    /***
     * Crea un nuevo pedido dentro de una transacción.
     * @param pedido Pedido a crear.
     * @param transactionManager Gestor de transacciones.
     * @param conn Conexión a la base de datos.
     * @return Número del pedido creado.
     */
    private String crearTx(Pedido pedido,
                           TransactionManager transactionManager,
                           Connection conn) {
        try {
            transactionManager.startTransaction();
            String numeroPedido = generarNuevoNumeroPedidoTx(conn);
            pedido.setNumero(numeroPedido);
            pedidoDAO.insertarTx(pedido, conn);
            transactionManager.commit();
            return numeroPedido;
        } catch (Exception e) {
            transactionManager.rollback();
            throw new CreacionEntityException(e.getMessage());
        }
    }

    /***
     * Genera un nuevo número de pedido basado en el último número registrado.
     * @param conn Conexión a la base de datos.
     * @return Nuevo número de pedido.
     * @throws SQLException Si ocurre un error al obtener el último número de pedido.
     */
    private String generarNuevoNumeroPedidoTx(Connection conn) throws SQLException {
        String ultimoPedido = pedidoDAO.buscarUltimoNumeroPedidoTx(conn);

        if (ultimoPedido == null || ultimoPedido.isEmpty()) {
            throw new SQLException("No se pudo obtener el último número de pedido.");
        }
        String[] partes = ultimoPedido.split("-");
        int numero = Integer.parseInt(partes[1]);
        numero++;
        return String.format("PED-%08d", numero);
    }

    /***
     * Valida los datos del pedido.
     * @param pedido Pedido a validar.
     */
    private void validarPedido(Pedido pedido) {
        if (StringUtils.isNullOrEmpty(pedido.getClienteNombre())) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (pedido.getTotal() == null || pedido.getTotal() < 0) {
            throw new IllegalArgumentException("El total del pedido no puede ser nulo o negativo.");
        }
    }

    /***
     * Busca todos los pedidos con paginación.
     * @param cantidad Cantidad de pedidos por página.
     * @param pagina Número de página.
     * @return Lista de pedidos.
     * @throws ConsultaEntityException Si ocurre un error durante la consulta.
     */
    @Override
    public List<Pedido> buscarTodos(Long cantidad, Long pagina) throws ConsultaEntityException {
        try {
            return pedidoDAO.buscarTodos(cantidad, pagina);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al listar pedidos: " + e.getMessage());
        }
    }

    /***
     * Busca un pedido por su ID.
     * @param id ID del pedido.
     * @return Pedido encontrado.
     * @throws ConsultaEntityException Si ocurre un error durante la consulta.
     */
    @Override
    public Pedido buscarPorId(Long id) throws ConsultaEntityException {
        try {
            return pedidoDAO.buscarPorId(id);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar pedido por ID: " + e.getMessage());
        }
    }

    /***
     * Busca un pedido por su número de pedido.
     * @param numero Número del pedido.
     * @return Pedido encontrado.
     * @throws ConsultaEntityException Si ocurre un error durante la consulta.
     */
    @Override
    public Pedido buscarPorNumeroPedido(String numero) throws ConsultaEntityException {
        try {
            return pedidoDAO.buscarPorNumero(numero);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar pedido por número: " + e.getMessage());
        }
    }

    /***
     * Busca un pedido por su número de tracking.
     * @param tracking Número de tracking del pedido.
     * @return Pedido encontrado.
     * @throws ConsultaEntityException Si ocurre un error durante la consulta.
     */
    @Override
    public Pedido buscarPorNumeroTracking(String tracking) throws ConsultaEntityException {
        try {
            return pedidoDAO.buscarPorTracking(tracking);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar pedido por número de tracking: " + e.getMessage());
        }
    }

    /***
     * Busca pedidos por el nombre del cliente con paginación.
     * @param cliente Nombre del cliente.
     * @param cantidad Cantidad de pedidos por página.
     * @param pagina Número de página.
     * @return Lista de pedidos encontrados.
     * @throws ConsultaEntityException Si ocurre un error durante la consulta.
     */
    @Override
    public List<Pedido> buscarPorCliente(String cliente, Long cantidad, Long pagina) {
        try {
            return pedidoDAO.buscarPorClienteNombre(cliente, cantidad, pagina);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar pedido por Cliente: " + e.getMessage());
        }
    }

    /***
     * Obtiene la cantidad total de pedidos.
     * @return Cantidad total de pedidos.
     * @throws ConsultaEntityException Si ocurre un error durante la consulta.
     */
    @Override
    public Long obtenerCantidadTotalDePedidos() throws ConsultaEntityException {
        try {
            return pedidoDAO.obtenerCantidadTotalDePedidos();
        } catch (SQLException e) {
            throw new ConsultaEntityException("Error al obtener la cantidad total de pedidos: " + e.getMessage());
        }
    }

    /***
     * Obtiene la cantidad total de pedidos por nombre de cliente.
     * @param clienteNombre Nombre del cliente.
     * @return Cantidad total de pedidos del cliente.
     * @throws ConsultaEntityException Si ocurre un error durante la consulta.
     */
    @Override
    public Long obtenerCantidadTotalDePedidosPorCliente(String clienteNombre) {
        try {
            return pedidoDAO.obtenerCantidadTotalDePedidosPorNombre(clienteNombre);
        } catch (SQLException e) {
            throw new ConsultaEntityException("Error al obtener la cantidad total de pedidos: " + e.getMessage());
        }
    }

    /***
     * Actualiza un pedido existente.
     * @param pedido Pedido a actualizar.
     * @throws ActualizacionEntityException Si ocurre un error durante la actualización.
     */
    @Override
    public void actualizar(Pedido pedido) throws ActualizacionEntityException {
        validarPedido(pedido);
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {
            actualizarTx(pedido, transactionManager, conn);
        } catch (Exception e) {
            throw new ActualizacionEntityException("Error al actualizar el envío: " + e.getMessage());
        }
    }

    /***
     * Actualiza un pedido dentro de una transacción.
     * @param pedido Pedido a actualizar.
     * @param transactionManager Gestor de transacciones.
     * @param conn Conexión a la base de datos.
     * @throws ActualizacionEntityException Si ocurre un error durante la actualización.
     */
    private void actualizarTx(Pedido pedido,
                              TransactionManager transactionManager,
                              Connection conn) throws ActualizacionEntityException {
        try {
            transactionManager.startTransaction();
            pedidoDAO.actualizarTx(pedido, conn);
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw new EliminacionEntityException(e.getMessage());
        }
    }

    /***
     * Actualiza un pedido dentro de una transacción con conexión proporcionada.
     * @param pedido Pedido a actualizar.
     * @param conn Conexión a la base de datos.
     * @throws ActualizacionEntityException Si ocurre un error durante la actualización.
     */
    @Override
    public void actualizarTx(Pedido pedido, Connection conn) throws ActualizacionEntityException {
        try {
            pedidoDAO.actualizarTx(pedido, conn);
        } catch (Exception e) {
            throw new ActualizacionEntityException("Error al actualizar el pedido (transaccional): " + e.getMessage());
        }
    }

    /***
     * Elimina lógicamente un pedido.
     * @param pedido Pedido a eliminar.
     * @throws EliminacionEntityException Si ocurre un error durante la eliminación.
     */
    @Override
    public void eliminar(Pedido pedido) throws EliminacionEntityException {
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {
            eliminarTx(pedido, transactionManager, conn);
        } catch (Exception e) {
            throw new EliminacionEntityException("Error al eliminar el pedido: " + e.getMessage());
        }
    }

    /***
     * Elimina lógicamente un pedido dentro de una transacción.
     * @param pedido Pedido a eliminar.
     * @param transactionManager Gestor de transacciones.
     * @param conn Conexión a la base de datos.
     * @throws EliminacionEntityException Si ocurre un error durante la eliminación.
     */
    private void eliminarTx(Pedido pedido,
                            TransactionManager transactionManager,
                            Connection conn) {
        try {
            transactionManager.startTransaction();
            String numeroPedido = generarNuevoNumeroPedidoTx(conn);
            pedido.setNumero(numeroPedido);
            pedidoDAO.eliminarLogicoTx(pedido.getId(), conn);
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw new EliminacionEntityException(e.getMessage());
        }
    }
}