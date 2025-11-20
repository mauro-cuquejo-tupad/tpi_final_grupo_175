package gestorenvios.dao;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/***
 * DAO para operaciones CRUD y consultas sobre la entidad Pedido.
 */
public class PedidoDAO implements GenericDAO<Pedido> {

    /*** Campos estándar seleccionados en las consultas de pedido. */
    private static final String CAMPOS_PEDIDO = " p.id, p.numero, p.fecha, p.cliente_nombre, p.total, p.id_estado_pedido, p.eliminado, p.id_envio,"
            + " e.id AS envio_id, e.tracking AS envio_tracking, e.id_empresa AS envio_id_empresa, e.id_tipo_envio AS envio_id_tipo, "
            + " e.costo AS envio_costo, e.fecha_despacho AS envio_fecha_despacho, e.fecha_estimada AS envio_fecha_estimada, e.id_estado_envio AS envio_id_estado, e.eliminado AS envio_eliminado ";


    /*** Query base para SELECT de pedido. */
    private static final String QUERY_BASE = "SELECT" + CAMPOS_PEDIDO + " FROM Pedido p";

    /*** Query para contar envíos activos (no eliminados). */
    private static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM Pedido WHERE eliminado = FALSE";
    private static final String COUNT_SQL_BY_NAME = "SELECT COUNT(*) AS total FROM Pedido p"
            + " WHERE p.eliminado = FALSE AND (UPPER(p.cliente_nombre) LIKE ?)";

    /*** Query para insertar un nuevo pedido. */
    private static final String INSERT_SQL = "INSERT INTO Pedido (numero, fecha, cliente_nombre, total, id_estado_pedido, id_envio) VALUES (?, ?, ?, ?, ?, ?)";


    /*** Query para actualizar un pedido existente por ID. */
    private static final String UPDATE_SQL = "UPDATE Pedido SET numero = ?, fecha = ?, cliente_nombre = ?, total = ?, id_estado_pedido = ?, id_envio = ? WHERE id = ?";

    /*** Query para eliminación lógica de un envío. */
    private static final String DELETE_SQL = "UPDATE Pedido SET eliminado = TRUE WHERE id = ?";

    /***
     * Query para obtener pedido por ID. LEFT JOIN con envio. Solo retorna pedidos activos (eliminado=FALSE). */
    private static final String SELECT_BY_ID_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND p.id = ?";

    /*** Query para listar pedidos activos con paginación. */
    private static final String SELECT_ALL_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE"
            + " LIMIT ? OFFSET ?";

    /*** Query de pedidos activos búsqueda por cliente_nombre con LIKE y UPPER para permitir búsqueda flexible. */
    private static final String SEARCH_BY_NAME_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND (UPPER(p.cliente_nombre) LIKE ?)"
            + " LIMIT ? OFFSET ?";

    /*** Query para buscar un pedido activo por número de pedido. */
    private static final String SEARCH_BY_NUMERO_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND p.numero = ?";

    /*** Query de búsqueda de pedidos activos por código de TRACKING del envío asociado. */
    private static final String SEARCH_BY_TRACKING_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = 0 AND e.tracking = ?";

    /*** Query para buscar el último número de pedido. */
    private static final String SEARCH_MAX_NUMERO_PEDIDO = "SELECT MAX(numero) AS numero FROM Pedido";

    /*** Constantes de nombres de columnas en ResultSet. */
    public static final String PEDIDO_ID = "id";
    public static final String PEDIDO_NUMERO = "numero";
    public static final String PEDIDO_FECHA = "fecha";
    public static final String PEDIDO_CLIENTE_NOMBRE = "cliente_nombre";
    public static final String PEDIDO_TOTAL = "total";
    public static final String PEDIDO_ELIMINADO = "eliminado";
    public static final String PEDIDO_ID_ESTADO_PEDIDO = "id_estado_pedido";
    public static final String PEDIDO_ENVIO_ID = "envio_id";


    /**
     * Inserta un pedido dentro de una transacción externa.
     * No gestiona la conexión, debe ser proporcionada por el invocador.
     *
     * @param pedido Pedido a insertar
     * @param conn   Conexión transaccional (NO se cierra en este método)
     * @throws SQLException Si falla la inserción
     */
    @Override
    public void insertarTx(Pedido pedido, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
        setPedidoParameters(pstmt, pedido);
        pstmt.executeUpdate();
        setGeneratedId(pstmt, pedido);
    }

    /**
     * Actualiza un envío existente dentro de una transacción externa.
     * No gestiona la conexión, debe ser proporcionada por el invocador.
     *
     * @param pedido Pedido a actualizar
     * @param conn   Conexión transaccional
     * @throws SQLException Si falla la actualización
     */
    @Override
    public void actualizarTx(Pedido pedido, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setString(1, pedido.getNumero());
            pstmt.setDate(2, Date.valueOf(pedido.getFecha()));
            pstmt.setString(3, pedido.getClienteNombre());
            pstmt.setDouble(4, pedido.getTotal());

            if (pedido.getEstado() != null) {
                pstmt.setInt(5, pedido.getEstado().getId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            if (pedido.getEnvio() != null && pedido.getEnvio().getId() != null && pedido.getEnvio().getId() > 0) {
                pstmt.setLong(6, pedido.getEnvio().getId());
            } else {
                pstmt.setNull(6, java.sql.Types.BIGINT);
            }

            pstmt.setLong(7, pedido.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el pedido ID: " + pedido.getId() + ".");
            }
        }
    }

    /**
     * Realiza eliminación lógica de un pedido dentro de una transacción externa.
     *
     * @param id   ID del pedido
     * @param conn Conexión transaccional
     * @throws SQLException Si falla la operación
     */
    @Override
    public void eliminarLogicoTx(Long id, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido (Tx) con ID: " + id + ".");
            }
        }
    }

    /**
     * Busca un pedido por su ID, incluyendo su envío asociado.
     *
     * @param id ID del pedido a buscar
     * @return Pedido encontrado con su envío, o null si no existe o está
     * eliminado
     * @throws SQLException Si hay error de BD (captura SQLException y re-lanza)
     */
    @Override
    public Pedido buscarPorId(Long id) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toPedido(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener pedido por ID: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Busca todos los pedidos activos con paginación, incluyendo sus envíos asociados.
     *
     * @param cantidad Número de registros por página (si es null, usa 50 por defecto)
     * @param pagina   Número de página (si es null, usa 1 por defecto)
     * @return Lista de pedidos activos con sus envíos (puede estar vacía)
     * @throws SQLException si ocurre un error en la consulta
     */
    @Override
    public List<Pedido> buscarTodos(Long cantidad, Long pagina) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        long registrosPorPagina = (cantidad != null && cantidad > 0L) ? cantidad : 50L;
        long numeroPagina = (pagina != null && pagina > 0L) ? pagina : 1L;

        long offset = (numeroPagina - 1L) * registrosPorPagina;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SQL)) {

            pstmt.setLong(1, registrosPorPagina);
            pstmt.setLong(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(toPedido(rs));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al listar pedidos: " + e.getMessage(), e);
        }

        return pedidos;
    }

    /**
     * Busca pedidos por nombre de cliente. Permite búsqueda parcial. No es case sensitive.
     *
     * @param nombre Texto a buscar en el nombre del cliente (no puede estar vacío)
     * @return Lista de pedidos que coinciden con el filtro (puede estar vacía)
     * @throws SQLException si ocurre un error en la consulta
     */
    public List<Pedido> buscarPorClienteNombre(String nombre, Long cantidad, Long pagina) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El Nombre del Cliente no puede estar vacío");
        }
        List<Pedido> pedidos = new ArrayList<>();

        long registrosPorPagina = (cantidad != null && cantidad > 0L) ? cantidad : 50L;
        long numeroPagina = (pagina != null && pagina > 0L) ? pagina : 1L;

        long offset = (numeroPagina - 1L) * registrosPorPagina;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_NAME_SQL)) {

            String searchPattern = "%" + nombre.toUpperCase() + "%";

            pstmt.setString(1, searchPattern);
            pstmt.setLong(2, registrosPorPagina); // LIMIT
            pstmt.setLong(3, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(toPedido(rs));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al buscar pedidos por nombre de cliente: " + e.getMessage(), e);
        }
        return pedidos;
    }

    /**
     * Busca un pedido por Número.
     *
     * @param numero Número exacto a buscar (ej: "PED-00123")
     * @return Pedido con ese número, o null si no existe o está eliminado
     * @throws SQLException si ocurre un error en la consulta
     */
    public Pedido buscarPorNumero(String numero) throws SQLException {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido no puede estar vacío");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_NUMERO_SQL)) {

            pstmt.setString(1, numero.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toPedido(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al buscar pedido por número: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Busca un pedido por código de tracking de su envío.
     *
     * @param tracking Código de tracking (ej: "TRK-999")
     * @return El Pedido asociado a ese tracking, o null si no existe.
     * @throws SQLException si ocurre un error en la consulta
     */
    public Pedido buscarPorTracking(String tracking) throws SQLException {
        if (tracking == null || tracking.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de tracking no puede estar vacío");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_TRACKING_SQL)) {

            pstmt.setString(1, tracking.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    return toPedido(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al buscar pedido por tracking: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Setea los parámetros de un PreparedStatement para insertar un pedido.
     *
     * @param pstmt  PreparedStatement
     * @param pedido Pedido
     * @throws SQLException si ocurre un error al setear los parámetros
     */
    private void setPedidoParameters(PreparedStatement pstmt, Pedido pedido) throws SQLException {

        pstmt.setString(1, pedido.getNumero());
        pstmt.setDate(2, java.sql.Date.valueOf(pedido.getFecha()));
        pstmt.setString(3, pedido.getClienteNombre());
        pstmt.setDouble(4, pedido.getTotal());

        if (pedido.getEstado() != null) {
            pstmt.setInt(5, pedido.getEstado().getId());
        } else {
            pstmt.setNull(5, java.sql.Types.INTEGER);
        }

        if (pedido.getEnvio() != null && pedido.getEnvio().getId() != null && pedido.getEnvio().getId() > 0) {
            pstmt.setLong(6, pedido.getEnvio().getId());
        } else {
            pstmt.setNull(6, java.sql.Types.BIGINT);
        }
    }

    /**
     * Recupera el ID autogenerado y lo asigna al pedido.
     *
     * @param pstmt  PreparedStatement
     * @param pedido Pedido
     * @throws SQLException Si hay error de BD
     */
    private void setGeneratedId(PreparedStatement pstmt, Pedido pedido) throws SQLException {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                pedido.setId(generatedKeys.getLong(1));
            }
        }
    }

    /**
     * Mapea un ResultSet a un objeto Pedido, incluyendo su envío asociado.
     *
     * @param rs ResultSet del registro a mapear
     * @return Pedido mapeado
     * @throws SQLException Si ocurre un error al leer el ResultSet
     */
    private Pedido toPedido(ResultSet rs) throws SQLException {

        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong(PEDIDO_ID));
        pedido.setNumero(rs.getString(PEDIDO_NUMERO));
        pedido.setFecha(rs.getDate(PEDIDO_FECHA).toLocalDate());
        pedido.setClienteNombre(rs.getString(PEDIDO_CLIENTE_NOMBRE));
        pedido.setTotal(rs.getDouble(PEDIDO_TOTAL));
        pedido.setEliminado(rs.getBoolean(PEDIDO_ELIMINADO));

        int idEstadoPedido = rs.getInt(PEDIDO_ID_ESTADO_PEDIDO);
        pedido.setEstado(EstadoPedido.fromId(idEstadoPedido));

        long idEnvio = rs.getLong(PEDIDO_ENVIO_ID);

        if (rs.wasNull()) {
            pedido.setEnvio(null);
        } else {
            Envio envio = EnvioDAO.toEnvio(rs, idEnvio);
            pedido.setEnvio(envio);
        }
        return pedido;
    }

    /**
     * Obtiene el último número de pedido insertado en una transacción.
     *
     * @param conn Conexión transaccional
     * @return Último número de pedido
     * @throws SQLException Si hay error de BD
     */
    public String buscarUltimoNumeroPedidoTx(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SEARCH_MAX_NUMERO_PEDIDO)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(PEDIDO_NUMERO);
                }
            }
        }
        return null;
    }

    /**
     * Obtiene la cantidad total de pedidos activos.
     *
     * @return Cantidad total
     * @throws SQLException Si hay error de BD
     */
    public Long obtenerCantidadTotalDePedidos() throws SQLException {
        long cantidad = 0L;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(COUNT_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                cantidad = rs.getLong(PEDIDO_TOTAL);
            }
        }
        return cantidad;
    }

    /**
     * Obtiene la cantidad total de pedidos filtrando por nombre de cliente.
     *
     * @param nombre Nombre del cliente
     * @return Cantidad total
     * @throws SQLException Si hay error de BD
     */
    public Long obtenerCantidadTotalDePedidosPorNombre(String nombre) throws SQLException {
        long cantidad = 0L;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(COUNT_SQL_BY_NAME)) {
            String searchPattern = "%" + nombre + "%";
            pstmt.setString(1, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                cantidad = rs.getLong(PEDIDO_TOTAL);
            }
        }
        return cantidad;
    }
}