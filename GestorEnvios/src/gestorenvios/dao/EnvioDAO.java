package gestorenvios.dao;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.entities.EmpresaEnvio;
import gestorenvios.entities.Envio;
import gestorenvios.entities.EstadoEnvio;
import gestorenvios.entities.TipoEnvio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Envíos. Gestiona todas las operaciones de
 * persistencia de envios en la base de datos.
 *
 */
public class EnvioDAO implements GenericDAO<Envio> {

    private static final String CAMPOS_ENVIO = " e.id, e.eliminado, e.tracking, e.id_empresa, e.id_tipo_envio, e.costo,"
            + " e.fecha_despacho, e.fecha_estimada, e.id_estado_envio";

    private static final String QUERY_BASE = "SELECT" + CAMPOS_ENVIO + " FROM Envio e";

    private static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM Envio WHERE eliminado = FALSE";

    /* Query de inserción. */
    private static final String INSERT_SQL = "INSERT INTO Envio (eliminado, tracking, id_empresa, id_tipo_envio, costo, fecha_despacho, fecha_estimada, id_estado_envio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /* Query de actualización. */
    private static final String UPDATE_SQL = "UPDATE Envio SET tracking = ?, id_empresa = ?, id_tipo_envio = ?, costo = ?, fecha_despacho = ?, fecha_estimada = ?, id_estado_envio = ? WHERE id = ?";

    /* Query de soft delete. */
    private static final String DELETE_SQL = "UPDATE Envio SET eliminado = TRUE WHERE id = ?";

    /* Query SELECT ALL */
    private static final String SELECT_ALL_SQL = QUERY_BASE
            + " WHERE e.eliminado = FALSE"
            + " LIMIT ? OFFSET ?";

    /* Query SELECT por ID */
    private static final String SELECT_BY_ID_SQL = QUERY_BASE
            + " WHERE e.eliminado = FALSE AND e.id = ?";

    private static final String SELECT_BY_TRACKING_SQL = QUERY_BASE
            + " WHERE e.eliminado = FALSE AND e.tracking = ?";

    private static final String SELECT_BY_NUMERO_SQL = QUERY_BASE
            + " LEFT JOIN Pedido p  ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE"
            + " AND p.numero = ?";


    @Override
    public void insertar(Envio envio, Connection conn) throws Exception {
        try {
            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            setEnvioParameters(pstmt, envio);
            pstmt.setBoolean(1, false);       // forzamos eliminado = false

            pstmt.executeUpdate();

            setGeneratedId(pstmt, envio);     // recuperar ID

        } catch (SQLException e) {
            throw new Exception("Error al insertar el envío: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertar(Envio envio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            insertar(envio, conn);
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
    public void insertTx(Envio envio, Connection conn) throws Exception {
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
    public void actualizar(Envio envio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

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
    public void actualizarTx(Envio envio, Connection conn) throws Exception {
        // NO abre ni cierra la conexión, usa la "conn" recibida
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            // parámetros
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
            pstmt.setLong(8, envio.getId()); // ID para el WHERE

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar (Tx) el envío ID: " + envio.getId() + ". Tal vez no existe.");
            }
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
    public void eliminarLogicoTx(Long id, Connection conn) throws Exception {
        // NO abrimos conexión, usamos la "conn"
        try (PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setLong(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró envío con ID: " + id);
            }
        }
        // NO cerramos la conexión
    }

    @Override
    public Envio buscarPorId(Long id) throws Exception {
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

    public Envio buscarPorTracking(String tracking) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_TRACKING_SQL)) {

            pstmt.setString(1, tracking);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapEnvio(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar envío por Tracking: " + e.getMessage(), e);
        }
        return null;
    }

    public Envio buscarPorNumeroPedido(String numero) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_NUMERO_SQL)) {

            pstmt.setString(1, numero);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapEnvio(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar envío por número de pedido: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Envio> buscarTodos(Long cantidad, Long pagina) throws Exception {
        // Inicializamos la lista vacía
        List<Envio> envios = new ArrayList<>();

        // Valores por defecto
        Long registrosPorPagina = (cantidad != null && cantidad > 0L) ? cantidad : 50L;
        Long numeroPagina = (pagina != null && pagina > 0L) ? pagina : 1L;

        // Calcular el OFFSET (registros a saltar)
        Long offset = (numeroPagina - 1L) * registrosPorPagina;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SQL)) {
            // Seteamos los parámetros LIMIT y OFFSET
            pstmt.setLong(1, registrosPorPagina); // LIMIT
            pstmt.setLong(2, offset);              // OFFSET

            try (ResultSet rs = pstmt.executeQuery()) {
                // Itera sobre CADA fila del resultado
                while (rs.next()) {
                    // Reutilizamos el mismo "mapPedido" para construir el objeto
                    envios.add(mapEnvio(rs));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al listar envíos: " + e.getMessage(), e);
        }
        return envios;
    }

    // OTROS

    /**
     * Setea los parámetros para el INSERT. El parámetro "eliminado" se setea
     * manualmente en el método insert/insertTx. desde el índice 2 en adelante.
     */
    private void setEnvioParameters(PreparedStatement pstmt, Envio envio) throws SQLException {

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

    private void setGeneratedId(PreparedStatement pstmt, Envio envio) throws SQLException {
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                envio.setId(rs.getLong(1));
            }
        }
    }

    private Envio mapEnvio(ResultSet resultSet) throws Exception {
        Envio envio = new Envio();

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

    public Long obtenerCantidadTotalDeEnvios() throws SQLException {
        Long total = 0L;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(COUNT_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getLong("total");
            }

        }

        return total;
    }
}
