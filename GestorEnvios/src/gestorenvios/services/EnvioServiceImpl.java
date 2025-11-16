package gestorenvios.services;

import com.mysql.cj.util.StringUtils;
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
    public void crear(Envio envio) throws CreacionEntityException {
        try {
            validarEnvio(envio);
            envioDAO.insertar(envio);
        } catch (Exception e) {
            throw new CreacionEntityException("Error al crear el envío: " + e.getMessage());
        }
    }

    private void validarEnvio(Envio envio) {
        if (StringUtils.isNullOrEmpty(envio.getTracking())) {
            throw new IllegalArgumentException("El numero de tracking no puede estar vacío.");
        }
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
        try {
            envioDAO.actualizar(envio);
        } catch (Exception e) {
            throw new ActualizacionEntityException("Error al actualizar el envío: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(Long id) throws EliminacionEntityException {
        try {
            envioDAO.eliminarLogico(id);
        } catch (Exception e) {
            throw new EliminacionEntityException("Error al eliminar el envío: " + e.getMessage());
        }
    }

    @Override
    public void crearEnvioYActualizarPedido(Envio envio, Pedido pedido) throws CreacionEntityException {
        validarEnvio(envio);
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager transactionManager = new TransactionManager(conn)) {

            transactionManager.startTransaction();
            envioDAO.insertarTx(envio, conn);
            // actualizo el pedido con el envio creado
            pedido.setEnvio(envio);
            // actualizo el estado del pedido
            pedido.setEstado(EstadoPedido.ENVIADO);
            pedidosService.actualizarTx(pedido, conn);
            transactionManager.commit();
        } catch (Exception e) {
            throw new CreacionEntityException("Error al crear el envío y actualizar el pedido: " + e.getMessage());
        }
    }
}