package gestorenvios.services;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.config.TransactionManager;
import gestorenvios.dao.EnvioDAO;
import gestorenvios.entities.Envio;
import gestorenvios.entities.EstadoPedido;
import gestorenvios.entities.Pedido;
import gestorenvios.models.exceptions.ActualizacionEntityException;
import gestorenvios.models.exceptions.ConsultaEntityException;
import gestorenvios.models.exceptions.CreacionEntityException;
import gestorenvios.models.exceptions.EliminacionEntityException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EnvioServiceImpl implements GenericEnviosService<Envio, Pedido> {
    EnvioDAO envioDAO;
    GenericPedidosService<Pedido> pedidosService;

    public EnvioServiceImpl(EnvioDAO envioDAO, GenericPedidosService<Pedido> pedidosService) {
        this.envioDAO = envioDAO;
        this.pedidosService = pedidosService;
    }

    @Override
    public String crear(Envio envio) throws CreacionEntityException {
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {
            validarEnvio(envio);
            return crearTx(envio, transactionManager, conn);
        } catch (Exception e) {
            throw new CreacionEntityException("Error al crear el envío: " + e.getMessage());
        }
    }

    private String crearTx(Envio envio, TransactionManager transactionManager, Connection conn) {
        try {
            transactionManager.startTransaction();
            envio.setTracking(generarNuevoNumeroTrackingTx(conn));
            envioDAO.insertarTx(envio, conn);
            transactionManager.commit();
            return envio.getTracking();
        } catch (Exception e) {
            transactionManager.rollback();
            throw new CreacionEntityException(e.getMessage());
        }
    }

    private String generarNuevoNumeroTrackingTx(Connection conn) {
        try {
            String ultimoTracking = envioDAO.buscarUltimoNumeroTrackingTx(conn);

            if (ultimoTracking == null || ultimoTracking.isEmpty()) {
                return "TRK-00000001";
            }
            String[] partes = ultimoTracking.split("-");
            int numero = Integer.parseInt(partes[1]);
            numero++;
            return String.format("TRK-%08d", numero);
        } catch (SQLException e) {
            throw new CreacionEntityException("No se pudo generar un nuevo número de tracking: " + e.getMessage());
        }
    }

    private void validarEnvio(Envio envio) {
        if (envio.getCosto() < 0) {
            throw new IllegalArgumentException("El total del pedido no puede negativo.");
        }
        if (envio.getFechaDespacho().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de despacho no puede ser anterior a la fecha actual.");
        }
        if (envio.getFechaEstimada().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha estimada no puede ser anterior a la fecha actual.");
        }
    }

    @Override
    public List<Envio> buscarTodos(Long cantidad, Long pagina) throws ConsultaEntityException {
        try {
            return envioDAO.buscarTodos(cantidad, pagina);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar envíos: " + e.getMessage());
        }
    }

    @Override
    public Envio buscarPorId(Long id) throws ConsultaEntityException {
        try {
            return envioDAO.buscarPorId(id);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar envío por ID: " + e.getMessage());
        }
    }

    @Override
    public Envio buscarPorTracking(String tracking) throws ConsultaEntityException {
        try {
            return envioDAO.buscarPorTracking(tracking);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar envío por tracking: " + e.getMessage());
        }
    }

    @Override
    public Envio buscarPorNumeroPedido(String numero) throws ConsultaEntityException {
        try {
            return envioDAO.buscarPorNumeroPedido(numero);
        } catch (Exception e) {
            throw new ConsultaEntityException("Error al buscar envío por número de pedido: " + e.getMessage());
        }
    }

    @Override
    public Long obtenerCantidadTotalDeEnvios() throws ConsultaEntityException {
        try {
            return envioDAO.obtenerCantidadTotalDeEnvios();
        } catch (SQLException e) {
            throw new ConsultaEntityException("Error al obtener la cantidad total de envíos: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Envio envio) throws ActualizacionEntityException {
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {
            actualizarTx(envio, transactionManager, conn);
        } catch (Exception e) {
            throw new ActualizacionEntityException("Error al actualizar el envío: " + e.getMessage());
        }
    }

    private void actualizarTx(Envio envio,
                              TransactionManager transactionManager,
                              Connection conn) {
        try {
            transactionManager.startTransaction();
            envioDAO.actualizarTx(envio, conn);
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw new ActualizacionEntityException(e.getMessage());
        }
    }

    @Override
    public void eliminar(Envio envio) throws EliminacionEntityException {
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {
            eliminarTx(envio, transactionManager, conn);
        } catch (Exception e) {
            throw new EliminacionEntityException("Error al eliminar envío: " + e.getMessage());
        }
    }

    private void eliminarTx(Envio envio, TransactionManager transactionManager, Connection conn) {
        try {
            transactionManager.startTransaction();
            envioDAO.eliminarLogicoTx(envio.getId(), conn);
            Pedido pedido = pedidosService.buscarPorNumeroTracking(envio.getTracking());
            pedido.setEstado(EstadoPedido.NUEVO);
            pedido.setEnvio(null);
            pedidosService.actualizarTx(pedido, conn);
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw new EliminacionEntityException(e.getMessage());
        }
    }

    @Override
    public String crearEnvioYActualizarPedido(Envio envio, Pedido pedido) throws CreacionEntityException {
        //validamos para evitar una conexion a la base de datos con registros invalidos
        validarEnvio(envio);
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {
            return crearEnvioYActualizarPedidoTx(envio, pedido, transactionManager, conn);
        } catch (Exception e) {
            throw new CreacionEntityException("Error al crear el envío y actualizar el pedido: " + e.getMessage());
        }
    }

    private String crearEnvioYActualizarPedidoTx(Envio envio,
                                                 Pedido pedido,
                                                 TransactionManager transactionManager,
                                                 Connection conn) {
        try {
            transactionManager.startTransaction();
            envio.setTracking(generarNuevoNumeroTrackingTx(conn));
            envioDAO.insertarTx(envio, conn);
            pedido.setEnvio(envio);
            pedido.setEstado(EstadoPedido.FACTURADO);
            pedidosService.actualizarTx(pedido, conn);
            transactionManager.commit();
            return envio.getTracking();
        } catch (Exception e) {
            transactionManager.rollback();
            throw new CreacionEntityException(e.getMessage());
        }
    }

    @Override
    public void actualizarEstado(Envio envio, Pedido pedido) {
        validarEnvio(envio);
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {
            actualizarEstadoTx(envio, pedido, transactionManager, conn);
        } catch (Exception e) {
            throw new ActualizacionEntityException("Error al actualizar estado del envío: " + e.getMessage());
        }

    }

    private void actualizarEstadoTx(Envio envio, Pedido pedido, TransactionManager transactionManager, Connection conn) {
        try {
            transactionManager.startTransaction();
            envioDAO.actualizarTx(envio, conn);
            pedido.setEstado(EstadoPedido.ENVIADO);
            pedidosService.actualizarTx(pedido, conn);
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw new ActualizacionEntityException(e.getMessage());
        }
    }
}