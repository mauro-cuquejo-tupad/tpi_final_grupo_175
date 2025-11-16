package gestorenvios.services;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.config.TransactionManager;
import gestorenvios.dao.EnvioDAO;
import gestorenvios.entities.Envio;
import gestorenvios.entities.EstadoPedido;
import gestorenvios.entities.Pedido;

import java.sql.Connection;
import java.sql.SQLException;
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
        System.out.println("Creando envio: " + envio);
        //TODO: validar envio
        envioDAO.insertar(envio);
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
