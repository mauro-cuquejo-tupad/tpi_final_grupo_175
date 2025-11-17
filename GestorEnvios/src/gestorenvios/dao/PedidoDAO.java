package gestorenvios.dao;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD y consultas sobre la entidad Pedido.
 */
public class PedidoDAO implements GenericDAO<Pedido> {
    private static final String CAMPOS_PEDIDO = " p.id, p.numero, p.fecha, p.cliente_nombre, p.total, p.id_estado_pedido, p.eliminado, p.id_envio,"
            + " e.id AS envio_id, e.tracking AS envio_tracking, e.id_empresa AS envio_id_empresa, e.id_tipo_envio AS envio_id_tipo, "
            + " e.costo AS envio_costo, e.fecha_despacho AS envio_fecha_despacho, e.fecha_estimada AS envio_fecha_estimada, e.id_estado_envio AS envio_id_estado, e.eliminado AS envio_eliminado ";

    private static final String QUERY_BASE = "SELECT" + CAMPOS_PEDIDO + " FROM Pedido p";

    public static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM Pedido WHERE eliminado = FALSE";
    public static final String COUNT_SQL_BY_NAME = "SELECT COUNT(*) AS total FROM Pedido p"
            + " WHERE p.eliminado = FALSE AND (UPPER(p.cliente_nombre) LIKE ?)";

    /**
     * Query de inserción de pedido.
     */
    private static final String INSERT_SQL = "INSERT INTO Pedido (numero, fecha, cliente_nombre, total, id_estado_pedido, id_envio) VALUES (?, ?, ?, ?, ?, ?)";


    /**
     * Query de actualización de pedido por ID.
     * NO actualiza el flag eliminado (solo se modifica en soft delete).
     */
    private static final String UPDATE_SQL = "UPDATE Pedido SET numero = ?, fecha = ?, cliente_nombre = ?, total = ?, id_estado_pedido = ?, id_envio = ? WHERE id = ?";

    /**
     * Query de soft delete. Marca eliminado = TRUE sin borrar físicamente la
     * fila. Preserva integridad referencial y datos históricos.
     */
    private static final String DELETE_SQL = "UPDATE Pedido SET eliminado = TRUE WHERE id = ?";

    /**
     * Query para obtener pedido por ID. LEFT JOIN con envio para cargar la
     * relación de forma eager. Solo retorna pedidos activos (eliminado=FALSE).
     */
    private static final String SELECT_BY_ID_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND p.id = ?";

    /**
     * Query para obtener todos los pedidos activos con paginación.
     * LEFT JOIN con envios para cargar relaciones.
     * Filtra por eliminado=FALSE (solo pedidos activos).
     * Usa LIMIT y OFFSET para paginación dinámica.
     */
    private static final String SELECT_ALL_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE"
            + " LIMIT ? OFFSET ?";

    /**
     * Query de búsqueda por cliente_nombre con LIKE.
     * Permite búsqueda flexible
     * Solo pedidos activos (eliminado=FALSE).
     */
    private static final String SEARCH_BY_NAME_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND (UPPER(p.cliente_nombre) LIKE ?)"
            + " LIMIT ? OFFSET ?";

    /**
     * Query de búsqueda exacta por NÚMERO de pedido. Incluye LEFT JOIN con
     * envíos. Filtra por eliminado=FALSE.
     */
    private static final String SEARCH_BY_NUMERO_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND p.numero = ?";

    /**
     * Query de búsqueda por código de TRACKING del envío asociado.
     * Filtra por e.tracking (tabla Envio) pero devuelve el Pedido.
     */
    private static final String SEARCH_BY_TRACKING_SQL = QUERY_BASE
            + " LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = 0 AND e.tracking = ?";

    private static final String SEARCH_MAX_NUMERO_PEDIDO = "SELECT MAX(numero) AS numero FROM Pedido";
    public static final String PEDIDO_ID = "id";
    public static final String PEDIDO_NUMERO = "numero";
    public static final String PEDIDO_FECHA = "fecha";
    public static final String PEDIDO_CLIENTE_NOMBRE = "cliente_nombre";
    public static final String PEDIDO_TOTAL = "total";
    public static final String PEDIDO_ELIMINADO = "eliminado";
    public static final String PEDIDO_ID_ESTADO_PEDIDO = "id_estado_pedido";
    public static final String PEDIDO_ENVIO_ID = "envio_id";


    /**
     * Inserta un pedido dentro de una transacción existente. NO crea nueva
     * conexión, recibe una Connection externa. NO cierra la conexión
     * (responsabilidad del caller, ej. un TransactionManager).
     * <p>
     * Usado por: - Operaciones que requieren múltiples inserts coordinados (ej.
     * Pedido + Envío) - Rollback automático si alguna operación falla
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
     * Actualiza un pedido existente dentro de una transacción.
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
     * Realiza un borrado lógico de un pedido (marca eliminado).
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
     * Obtiene todos los pedidos activos (eliminado=FALSE) con paginación.
     * Incluye sus envíos asociados mediante LEFT JOIN (carga eager).
     *
     * @param cantidad Número de registros por página (si es null, usa 10 por defecto)
     * @param pagina   Número de página (1-indexed, si es null, usa 1 por defecto)
     * @return Lista de pedidos activos con sus envíos (puede estar vacía)
     * @throws SQLException Si hay error de BD
     */
    @Override
    public List<Pedido> buscarTodos(Long cantidad, Long pagina) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        long registrosPorPagina = (cantidad != null && cantidad > 0L) ? cantidad : 50L;
        long numeroPagina = (pagina != null && pagina > 0L) ? pagina : 1L;

        long offset = (numeroPagina - 1L) * registrosPorPagina;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SQL)) {

            pstmt.setLong(1, registrosPorPagina); // LIMIT
            pstmt.setLong(2, offset);              // OFFSET

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
     * Busca pedidos por nombre de cliente con búsqueda flexible (LIKE). Permite
     * búsqueda parcial.
     * <p>
     * Patrón de búsqueda: LIKE "%filtro%" en clienteNombre
     *
     * @param nombre Texto a buscar en el nombre del cliente (no puede estar
     *               vacío)
     * @return Lista de pedidos que coinciden con el filtro (puede estar vacía)
     * @throws SQLException Si hay error de BD
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
     * Busca un pedido por su NÚMERO exacto (campo UNIQUE). Usa comparación
     * exacta (=).
     *
     * @param numero Número exacto a buscar (ej: "PED-00123")
     * @return Pedido con ese número, o null si no existe o está eliminado
     * @throws SQLException Si hay error de BD
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
     * Busca un pedido basándose en el código de tracking de su envío.
     * Realiza un JOIN y filtra por la tabla Envio.
     *
     * @param tracking Código de seguimiento (ej: "TRK-999")
     * @return El Pedido asociado a ese tracking, o null si no existe.
     * @throws SQLException Si hay error de BD
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
     * @throws SQLException Si hay error de BD
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
     * @param rs ResultSet
     * @return Pedido mapeado
     * @throws SQLException Si hay error de BD
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