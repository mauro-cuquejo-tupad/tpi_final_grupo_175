package gestorenvios.services;

import com.mysql.cj.util.StringUtils;
import gestorenvios.config.DatabaseConnection;
import gestorenvios.config.TransactionManager;
import gestorenvios.dao.EnvioDAO;
import gestorenvios.entities.Envio;
import gestorenvios.entities.EstadoPedido;
import gestorenvios.entities.Pedido;

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
    public void crear(Envio envio) throws Exception {
        validarEnvio(envio);
        envioDAO.insertar(envio);
    }

    private void validarEnvio(Envio envio) {
        if (StringUtils.isNullOrEmpty(envio.getTracking())) {
            throw new IllegalArgumentException("El numero de tracking no puede estar vacío.");
        }
        if (envio.getCosto() < 0) {
            throw new IllegalArgumentException("El total del pedido no puede negativo.");
        }
        if(envio.getFechaDespacho().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de despacho no puede ser anterior a la fecha actual.");
        }
        if(envio.getFechaEstimada().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha estimada no puede ser anterior a la fecha actual.");
        }
    }

    @Override
    public List<Envio> buscarTodos(Long cantidad, Long pagina) throws Exception {
        return envioDAO.buscarTodos(cantidad, pagina);
    }

    @Override
    public Envio buscarPorId(Long id) throws Exception {
        return envioDAO.buscarPorId(id);
    }

    @Override
    public Envio buscarPorTracking(String tracking) throws Exception {
        return envioDAO.buscarPorTracking(tracking);
    }

    @Override
    public Envio buscarPorNumeroPedido(String numero) throws Exception {
        return envioDAO.buscarPorNumeroPedido(numero);
    }

    @Override
    public Long obtenerCantidadTotalDeEnvios() throws SQLException {
        return envioDAO.obtenerCantidadTotalDeEnvios();
    }

    @Override
    public void actualizar(Envio envio) throws Exception {
        envioDAO.actualizar(envio);
    }

    @Override
    public void eliminar(Long id) throws Exception {
        envioDAO.eliminarLogico(id);
    }

    @Override
    public void crearEnvioYActualizarPedido(Envio envio, Pedido pedido) throws Exception {
        TransactionManager transactionManager = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            transactionManager = new TransactionManager(conn);

            transactionManager.startTransaction();
            envioDAO.insertar(envio, conn);
            //actualizo el pedido con el envio creado
            pedido.setEnvio(envio);
            //actualizo el estado del pedido
            pedido.setEstado(EstadoPedido.ENVIADO);
            pedidosService.actualizar(pedido, conn);
            transactionManager.commit();
        } catch (Exception e) {
            System.out.println("Error en la transacción: " + e.getMessage());
            if(transactionManager != null) {
                transactionManager.rollback();
            }
            throw e;
        } finally {
            // Cerrar el TransactionManager y la conexión
            // Esto se hace en el método close() del TransactionManager
            if(transactionManager != null) {
                transactionManager.close();
            }
        }
    }
}
