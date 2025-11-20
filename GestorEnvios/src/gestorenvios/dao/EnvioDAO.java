package gestorenvios.dao;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.entities.EmpresaEnvio;
import gestorenvios.entities.Envio;
import gestorenvios.entities.EstadoEnvio;
import gestorenvios.entities.TipoEnvio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/***
 * DAO para operaciones CRUD y consultas sobre la entidad Envio.
 */
public class EnvioDAO implements GenericDAO<Envio> {

    /*** Campos estándar seleccionados en las consultas de envío. */
    private static final String CAMPOS_ENVIO = " e.id, e.eliminado, e.tracking, e.id_empresa, e.id_tipo_envio, e.costo,"
            + " e.fecha_despacho, e.fecha_estimada, e.id_estado_envio";

    /*** Query base para SELECT de envíos. */
    private static final String QUERY_BASE = "SELECT" + CAMPOS_ENVIO + " FROM Envio e";

    /*** Query para contar envíos activos (no eliminados). */
    private static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM Envio WHERE eliminado = FALSE";

    /*** Query para insertar un nuevo envío. */
    private static final String INSERT_SQL = "INSERT INTO Envio (eliminado, tracking, id_empresa, id_tipo_envio, costo, fecha_despacho, fecha_estimada, id_estado_envio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /*** Query para actualizar un envío existente por ID. */
    private static final String UPDATE_SQL = "UPDATE Envio SET tracking = ?, id_empresa = ?, id_tipo_envio = ?, costo = ?, fecha_despacho = ?, fecha_estimada = ?, id_estado_envio = ? WHERE id = ?";

    /*** Query para eliminación lógica de un envío. */
    private static final String DELETE_SQL = "UPDATE Envio SET eliminado = TRUE WHERE id = ?";

    /*** Query para listar envíos activos con paginación. */
    private static final String SELECT_ALL_SQL = QUERY_BASE
            + " WHERE e.eliminado = FALSE"
            + " LIMIT ? OFFSET ?";

    /*** Query para buscar un envío activo por ID. */
    private static final String SELECT_BY_ID_SQL = QUERY_BASE
            + " WHERE e.eliminado = FALSE AND e.id = ?";

    /*** Query para buscar un envío activo por código de tracking. */
    private static final String SELECT_BY_TRACKING_SQL = QUERY_BASE
            + " WHERE e.eliminado = FALSE AND e.tracking = ?";

    /*** Query para buscar el último número de tracking, cuyo código tenga el string 'TRK-' al inicio. */
    private static final String SEARCH_MAX_TRACKING = "SELECT MAX(tracking) AS tracking FROM Envio "
    + "WHERE tracking like 'TRK-%' ";

    /*** Query para buscar un envío por número de pedido asociado. Incluye JOIN con tabla Pedido. */
    private static final String SELECT_BY_NUMERO_SQL = QUERY_BASE
            + " LEFT JOIN Pedido p  ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE"
            + " AND p.numero = ?";

    /*** Nombre del campo tracking en la tabla envio */
    private static final String ENVIO_TRACKING = "tracking";

    /***
     * Inserta un envío dentro de una transacción externa.
     * No gestiona la conexión, debe ser proporcionada por el invocador.
     *
     * @param envio Envío a insertar
     * @param conn Conexión activa de base de datos
     * @throws SQLException si ocurre un error durante la inserción
     */
    @Override
    public void insertarTx(Envio envio, Connection conn) throws SQLException {
        try {
            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            setEnvioParameters(pstmt, envio);
            pstmt.setBoolean(1, false);

            pstmt.executeUpdate();

            setGeneratedId(pstmt, envio);

        } catch (SQLException e) {
            throw new SQLException("Error al insertar el envío: " + e.getMessage(), e);
        }
    }

    /***
     * Actualiza un envío existente dentro de una transacción externa.
     * No gestiona la conexión, debe ser proporcionada por el invocador.
     *
     * @param envio Envío con datos actualizados
     * @param conn Conexión activa de base de datos
     * @throws SQLException si el envío no existe o hay error en la actualización
     */
    @Override
    public void actualizarTx(Envio envio, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

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
                throw new SQLException("No se pudo actualizar (Tx) el envío ID: " + envio.getId() + ". Tal vez no existe.");
            }
        }
    }

    /***
     * Realiza eliminación lógica de un envío dentro de una transacción externa.
     *
     * @param id ID del envío a eliminar
     * @param conn Conexión activa de base de datos
     * @throws SQLException si el envío no existe
     */
    @Override
    public void eliminarLogicoTx(Long id, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setLong(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró envío con ID: " + id);
            }
        }
    }

    /***
     * Busca un envío activo por su ID.
     *
     * @param id ID del envío a buscar
     * @return Envío encontrado o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     */
    @Override
    public Envio buscarPorId(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toEnvio(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al buscar envío por ID: " + e.getMessage(), e);
        }
        return null;
    }

    /***
     * Busca un envío activo por su código de tracking.
     *
     * @param tracking Código de tracking del envío
     * @return Envío encontrado o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     */
    public Envio buscarPorTracking(String tracking) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_TRACKING_SQL)) {

            pstmt.setString(1, tracking);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toEnvio(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al buscar envío por Tracking: " + e.getMessage(), e);
        }
        return null;
    }

    /***
     * Busca un envío por el número del pedido asociado. Realiza JOIN con la tabla Pedido.
     *
     * @param numero Número del pedido
     * @return Envío encontrado o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     */
    public Envio buscarPorNumeroPedido(String numero) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_NUMERO_SQL)) {

            pstmt.setString(1, numero);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toEnvio(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al buscar envío por número de pedido: " + e.getMessage(), e);
        }
        return null;
    }

    /***
     * Lista todos los envíos activos con paginación.
     *
     * @param cantidad Número de registros por página (si es null, usa 50 por defecto)
     * @param pagina Número de página (si es null, usa 1 por defecto)
     * @return Lista de envíos de la página solicitada (puede estar vacía)
     * @throws SQLException si ocurre un error en la consulta
     */
    @Override
    public List<Envio> buscarTodos(Long cantidad, Long pagina) throws SQLException {
        List<Envio> envios = new ArrayList<>();

        long registrosPorPagina = (cantidad != null && cantidad > 0L) ? cantidad : 50L;
        long numeroPagina = (pagina != null && pagina > 0L) ? pagina : 1L;

        long offset = (numeroPagina - 1L) * registrosPorPagina;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SQL)) {
            pstmt.setLong(1, registrosPorPagina);
            pstmt.setLong(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    envios.add(toEnvio(rs));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al listar envíos: " + e.getMessage(), e);
        }
        return envios;
    }

    /***
     * Obtiene el último tracking de envio insertado en una transacción.
     *
     * @param conn Conexión transaccional
     * @return Último número de pedido
     * @throws SQLException Si hay error de BD
     */
    public String buscarUltimoNumeroTrackingTx(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SEARCH_MAX_TRACKING)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(ENVIO_TRACKING);
                }
            }
        }
        return null;
    }

    /***
     * Setea los parámetros del PreparedStatement para insertar un envío.
     * El parámetro eliminado (índice 1) debe setearse manualmente antes de llamar este método.
     *
     * @param pstmt PreparedStatement a configurar
     * @param envio Envío con los datos a insertar
     * @throws SQLException si ocurre un error al setear los parámetros
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

    /***
     * Recupera el ID generado automáticamente y lo asigna al envío.
     *
     * @param pstmt PreparedStatement que ejecutó el INSERT
     * @param envio Envío al que se le asignará el ID generado
     * @throws SQLException si no se puede obtener el ID generado
     */
    private void setGeneratedId(PreparedStatement pstmt, Envio envio) throws SQLException {
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                envio.setId(rs.getLong(1));
            }
        }
    }

    /***
     * Mapea un ResultSet a un objeto Envío.
     *
     * @param resultSet ResultSet del registro a mapear
     * @return Envío construido con los datos del registro
     * @throws SQLException si ocurre un error al leer el ResultSet
     */
    private Envio toEnvio(ResultSet resultSet) throws SQLException {
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

        envio.setEmpresa(EmpresaEnvio.fromId(resultSet.getInt("id_empresa")));
        envio.setTipo(TipoEnvio.fromId(resultSet.getInt("id_tipo_envio")));
        envio.setEstado(EstadoEnvio.fromId(resultSet.getInt("id_estado_envio")));

        return envio;
    }

    /***
     * Mapea un ResultSet a un objeto Envío, usando un ID proporcionado.
     *
     * @param rs ResultSet del registro a mapear
     * @param idEnvio ID del envío
     * @return Envío construido con los datos del registro
     * @throws SQLException si ocurre un error al leer el ResultSet
     */
    public static Envio toEnvio(ResultSet rs, Long idEnvio) throws SQLException {
        Envio envio = new Envio();
        envio.setId(idEnvio);
        envio.setTracking(rs.getString("envio_tracking"));
        envio.setCosto(rs.getDouble("envio_costo"));

        if (rs.getDate("envio_fecha_despacho") != null) {
            envio.setFechaDespacho(rs.getDate("envio_fecha_despacho").toLocalDate());

        }
        if (rs.getDate("envio_fecha_estimada") != null) {
            envio.setFechaEstimada(rs.getDate("envio_fecha_estimada").toLocalDate());
        }

        envio.setEmpresa(EmpresaEnvio.fromId(rs.getInt("envio_id_empresa")));
        envio.setTipo(TipoEnvio.fromId(rs.getInt("envio_id_tipo")));
        envio.setEstado(EstadoEnvio.fromId(rs.getInt("envio_id_estado")));
        envio.setEliminado(rs.getBoolean("envio_eliminado"));
        return envio;
    }

    /***
     * Obtiene el total de envíos activos en la base de datos.
     *
     * @return Cantidad total de envíos
     * @throws SQLException si ocurre un error en la consulta
     */
    public Long obtenerCantidadTotalDeEnvios() throws SQLException {
        long total = 0L;

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
