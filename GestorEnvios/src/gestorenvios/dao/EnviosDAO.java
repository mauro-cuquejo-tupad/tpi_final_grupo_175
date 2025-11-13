package gestorenvios.dao;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.entities.EmpresaEnvio;
import gestorenvios.entities.Envios;
import gestorenvios.entities.EstadoEnvio;
import gestorenvios.entities.TipoEnvio;
import java.sql.Statement;
import java.sql.Connection;
import java.util.List;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 *
 * @author Grupo_175
 */
/**
 * Data Access Object para la entidad Envíos. Gestiona todas las operaciones de
 * persistencia de envios en la base de datos.
 *
 */
public class EnviosDAO implements GenericDAO<Envios> {

    /* Query de inserción. */
    private static final String INSERT_SQL = "INSERT INTO Envio (eliminado, tracking, id_empresa, id_tipo_envio, costo, fecha_despacho, fecha_estimada, id_estado_envio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /* Query de actualización. */
    private static final String UPDATE_SQL = "UPDATE Envio SET tracking = ?, id_empresa = ?, id_tipo_envio = ?, costo = ?, fecha_despacho = ?, fecha_estimada = ?, id_estado_envio = ? WHERE id = ?";

    /* Query de soft delete. */
    private static final String DELETE_SQL = "UPDATE Envio SET eliminado = TRUE WHERE id = ?";

    /* Query SELECT por ID */
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Envio WHERE eliminado = FALSE AND id = ?";

    /* Query SELECT ALL */
    private static final String SELECT_ALL_SQL = "SELECT * FROM Envio WHERE eliminado = FALSE";

    /**
     * Constructor vacío.
     */
    public EnviosDAO() {
    }

    @Override
    public void insertar(Envios envio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setEnvioParameters(pstmt, envio);
            pstmt.setBoolean(1, false);       // forzamos eliminado = false 

            pstmt.executeUpdate();

            setGeneratedId(pstmt, envio);     // recuperar ID

        } catch (SQLException e) {
            throw new Exception("Error al insertar el envío: " + e.getMessage(), e);
        }
    }

    /**
     * Inserta un envío dentro de una transacción externa. Es vital para cuando
     * PedidosDAO guarda el pedido y necesita guardar el envío al mismo tiempo.
     *
     * @param envio
     * @param conn
     * @throws java.lang.Exception
     */
    @Override
    public void insertTx(Envios envio, Connection conn) throws Exception {
        // No cerramos conexión 
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setEnvioParameters(pstmt, envio);
            pstmt.setBoolean(1, false); // El primero es "eliminado"

            pstmt.executeUpdate();

            setGeneratedId(pstmt, envio);
        }
        // Si falla, la excepción sube y el Transaction Manager hace rollback
    }

    @Override
    public void actualizar(Envios envio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            // no está el campo "eliminado" al principio
            pstmt.setString(1, envio.getTracking());
            pstmt.setInt(2, envio.getEmpresa().getId());
            pstmt.setInt(3, envio.getTipo().getId());
            pstmt.setDouble(4, envio.getCosto());

            if (envio.getFechaDespacho() != null) {
                pstmt.setDate(5, java.sql.Date.valueOf(envio.getFechaDespacho()));
            } else {
                pstmt.setNull(5, java.sql.Types.DATE);
            }

            if (envio.getFechaEstimada() != null) {
                pstmt.setDate(6, java.sql.Date.valueOf(envio.getFechaEstimada()));
            } else {
                pstmt.setNull(6, java.sql.Types.DATE);
            }

            pstmt.setInt(7, envio.getEstado().getId());

            pstmt.setLong(8, envio.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el envío ID: " + envio.getId() + ". Tal vez no existe o está eliminado.");
            }

        } catch (SQLException e) {
            throw new Exception("Error al actualizar el envío: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarLogico(Long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró envío con ID: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al eliminar lógicamente el envío: " + e.getMessage(), e);
        }
    }

    @Override
    public Envios buscarPorId(Long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapEnvio(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar envío por ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Envios> buscarTodos() throws Exception {
        List<Envios> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SQL); 
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapEnvio(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al listar envíos: " + e.getMessage(), e);
        }
        return lista;
    }

    // OTROS
    /**
     * Setea los parámetros para el INSERT. El parámetro "eliminado" se setea
     * manualmente en el método insert/insertTx. desde el índice 2 en adelante.
     */
    private void setEnvioParameters(PreparedStatement pstmt, Envios envio) throws SQLException {

        pstmt.setString(2, envio.getTracking());

        pstmt.setInt(3, envio.getEmpresa().getId());
        pstmt.setInt(4, envio.getTipo().getId());
        pstmt.setDouble(5, envio.getCosto());

        if (envio.getFechaDespacho() != null) {
            pstmt.setDate(6, java.sql.Date.valueOf(envio.getFechaDespacho()));
        } else {
            pstmt.setNull(6, java.sql.Types.DATE);
        }

        if (envio.getFechaEstimada() != null) {
            pstmt.setDate(7, java.sql.Date.valueOf(envio.getFechaEstimada()));
        } else {
            pstmt.setNull(7, java.sql.Types.DATE);
        }

        pstmt.setInt(8, envio.getEstado().getId());
    }

    private void setGeneratedId(PreparedStatement pstmt, Envios envio) throws SQLException {
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                envio.setId(rs.getLong(1));
            }
        }
    }

    private Envios mapEnvio(ResultSet resultSet) throws Exception {
        Envios envio = new Envios();

        envio.setId(resultSet.getLong("id"));
        envio.setEliminado(resultSet.getBoolean("eliminado"));
        envio.setTracking(resultSet.getString("tracking"));
        envio.setCosto(resultSet.getDouble("costo"));

        if (resultSet.getDate("fecha_despacho") != null) {
            envio.setFechaDespacho(resultSet.getDate("fecha_despacho").toLocalDate());
        }
        if (resultSet.getDate("fecha_estimada") != null) {
            envio.setFechaEstimada(resultSet.getDate("fecha_estimada").toLocalDate());
        }

        // Mapeo de ID (BD) a Enum (Java) usando método 'fromId'
        envio.setEmpresa(EmpresaEnvio.fromId(resultSet.getInt("id_empresa")));
        envio.setTipo(TipoEnvio.fromId(resultSet.getInt("id_tipo_envio")));
        envio.setEstado(EstadoEnvio.fromId(resultSet.getInt("id_estado_envio")));

        return envio;
    }
}
