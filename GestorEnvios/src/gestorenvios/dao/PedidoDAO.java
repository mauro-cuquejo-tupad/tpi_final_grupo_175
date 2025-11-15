package gestorenvios.dao;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.entities.*;
import gestorenvios.models.exceptions.pedidos.CreacionPedidoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Grupo_175
 */

/**
 * Data Access Object para la entidad Pedidos. Gestiona todas las operaciones de
 * persistencia de pedidos en la base de datos.
 *
 */
public class PedidoDAO implements GenericDAO<Pedido> {
    public static String CAMPOS_PEDIDO = " p.id, p.numero, p.fecha, p.cliente_nombre, p.total, p.id_estado_pedido, p.eliminado, p.id_envio,"
            + " e.id AS envio_id, e.tracking AS envio_tracking, e.id_empresa AS envio_id_empresa, e.id_tipo_envio AS envio_id_tipo, "
            + " e.costo AS envio_costo, e.fecha_despacho AS envio_fecha_despacho, e.fecha_estimada AS envio_fecha_estimada, e.id_estado_envio AS envio_id_estado ";

    public static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM Pedido WHERE eliminado = FALSE";

    /* Query de inserción de pedido.*/
    private static final String INSERT_SQL = "INSERT INTO Pedido (numero, fecha, cliente_nombre, total, id_estado_pedido, id_envio) VALUES (?, ?, ?, ?, ?, ?)";


    /* Query de actualización de pedido por ID.
       NO actualiza el flag eliminado (solo se modifica en soft delete).
     */
    private static final String UPDATE_SQL = "UPDATE Pedido SET numero = ?, fecha = ?, cliente_nombre = ?, total = ?, id_estado_pedido = ?, id_envio = ? WHERE id = ?";

    /**
     * Query de soft delete. Marca eliminado = TRUE sin borrar físicamente la
     * fila. Preserva integridad referencial y datos históricos.
     */
    private static final String DELETE_SQL = "UPDATE Pedido SET eliminado = TRUE WHERE id = ?";
    private static final String DELETE_SQL_POR_NUMERO = "UPDATE Pedido SET eliminado = TRUE WHERE numero = ?";

    /**
     * Query para obtener pedido por ID. LEFT JOIN con envio para cargar la
     * relación de forma eager. Solo retorna pedidos activos (eliminado=FALSE).
     */
    private static final String SELECT_BY_ID_SQL = "SELECT" + CAMPOS_PEDIDO
            + " FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND p.id = ?";

    /*
     * Query para obtener todos los pedidos activos con paginación.
     * LEFT JOIN con envios para cargar relaciones.
     * Filtra por eliminado=FALSE (solo pedidos activos).
     * Usa LIMIT y OFFSET para paginación dinámica.
     */
    private static final String SELECT_ALL_SQL = "SELECT" + CAMPOS_PEDIDO
            + " FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE"
            + " LIMIT ? OFFSET ?";

    /*
     * Query de búsqueda por cliente_nombre con LIKE.
     * Permite búsqueda flexible
     * Solo pedidos activos (eliminado=FALSE).
     */
    private static final String SEARCH_BY_NAME_SQL = "SELECT" + CAMPOS_PEDIDO
            + " FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND (p.cliente_nombre LIKE ?)";

    /**
     * Query de búsqueda exacta por NÚMERO de pedido. Incluye LEFT JOIN con
     * envíos. Filtra por eliminado=FALSE.
     */
    private static final String SEARCH_BY_NUMERO_SQL = "SELECT" + CAMPOS_PEDIDO
            + " FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = FALSE AND p.numero = ?";

    private static final String SEARCH_MAX_NUMERO_PEDIDO = "SELECT MAX(numero) numero AS max_numero FROM Pedido";
    /**
     * Query para desvincular envio de un pedido
     */
    private static final String DESVINCULAR_ENVIO_SQL = "UPDATE Pedido SET id_envio = NULL WHERE id = ?";


    /**
     * Query de búsqueda por código de TRACKING del envío asociado.
     * Filtra por e.tracking (tabla Envio) pero devuelve el Pedido.
     */
    private static final String SEARCH_BY_TRACKING_SQL = "SELECT" + CAMPOS_PEDIDO
            + " FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id"
            + " WHERE p.eliminado = 0 AND e.tracking = ?";


    /**
     * Inserta un nuevo pedido en la base de datos.
     *
     * @param pedido Pedido a insertar
     * @throws java.lang.Exception Si falla la inserción
     */

    @Override
    public void insertar(Pedido pedido) throws Exception {

        // Usamos try-with-resources para la conexión y el statement
        try (Connection conn = DatabaseConnection.getConnection()) {
            insertar(pedido, conn);
        } catch (SQLException e) {
            // Captura el error de SQL y lo relanza como una excepción específica
            throw new CreacionPedidoException("Error al insertar el pedido: " + e.getMessage());
        }
    }

    @Override
    public void insertar(Pedido pedido, Connection conn) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
        // Llamada al método para setear los VALUES "?"
        setPedidoParameters(pstmt, pedido);

        // Ejecuta la inserción
        pstmt.executeUpdate();

        // Método para recuperar los ID autogenerados
        setGeneratedId(pstmt, pedido);

    }

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
     * @throws Exception Si falla la inserción
     */
    @Override
    public void insertTx(Pedido pedido, Connection conn) throws Exception {

        // Este método asume que la conexión (conn) es manejada 
        // (abierta, cerrada, commit, rollback) por un servicio externo.
        // El try-with-resources SÓLO se aplica al PreparedStatement.
        // ¡No cierra la "conn"!
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Reutilizamos el primer método
            setPedidoParameters(pstmt, pedido);

            // ejecución
            pstmt.executeUpdate();

            // Reutilizamos el segundo método
            setGeneratedId(pstmt, pedido);
        }

        // Si ocurre una SQLException, se lanza "throws Exception"
        // y el servicio que maneja la transacción deberá hacer un "rollback".
    }

    public void actualizar(Pedido pedido, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            // Seteamos los parámetros del UPDATE
            pstmt.setString(1, pedido.getNumero());
            pstmt.setDate(2, java.sql.Date.valueOf(pedido.getFecha()));
            pstmt.setString(3, pedido.getClienteNombre());
            pstmt.setDouble(4, pedido.getTotal());

            // --- Manejo de FK para el Estado (Enum) ---
            if (pedido.getEstado() != null) {
                pstmt.setInt(5, pedido.getEstado().getId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            // --- Manejo de FK para el Envío ---
            // Chequeamos si el envío no es nulo Y si su ID no es nulo
            if (pedido.getEnvio() != null && pedido.getEnvio().getId() != null && pedido.getEnvio().getId() > 0) {
                pstmt.setLong(6, pedido.getEnvio().getId());
            } else {
                pstmt.setNull(6, java.sql.Types.BIGINT);
            }

            // --- ID para el WHERE clause ---
            pstmt.setLong(7, pedido.getId());

            // Ejecutamos la actualización
            int rowsAffected = pstmt.executeUpdate();

            // Verificamos si la actualización fue exitosa
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el pedido con ID: " + pedido.getId() + ". Es posible que no exista.");
            }
        }
    }

    /**
     * Actualiza un pedido existente en la base de datos. NO actualiza el flag
     * "eliminado".
     * <p>
     * Validaciones: - Si rowsAffected == 0 → El pedido no existe o ya está
     * eliminado (y no se puede encontrar).
     * <p>
     * IMPORTANTE: Este método puede cambiar las FKs: - Si pedido.estado == null
     * → id_estado_pedido = NULL (desasociar) - Si pedido.envio == null →
     * id_envio = NULL (desasociar)
     *
     * @param pedido pedido con los datos actualizados (id debe ser > 0)
     * @throws Exception Si el pedido no existe o hay error de BD
     */
    @Override
    public void actualizar(Pedido pedido) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            actualizar(pedido, conn);
        } catch (SQLException e) {
            throw new Exception("Error al actualizar el pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizarTx(Pedido pedido, Connection conn) throws Exception {
        // NO abre ni cierra la conexión, usa la "conn" recibida
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            // Seteamos los parámetros del UPDATE
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

            pstmt.setLong(7, pedido.getId()); // ID para el WHERE

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar (Tx) el pedido ID: " + pedido.getId() + ".");
            }
        }
    }

    /**
     * Elimina lógicamente un pedido (soft delete). Marca eliminado=TRUE sin
     * borrar físicamente la fila.
     * <p>
     * Validaciones: - Si rowsAffected == 0 → El pedido no existe o ya está
     * eliminado
     * <p>
     * IMPORTANTE: NO elimina el envío asociado.
     *
     * @param id ID del pedido a eliminar
     * @throws Exception Si el pedido no existe o hay error de BD
     */
    @Override
    public void eliminarLogico(Long id) throws Exception {

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setLong(1, id);

            // Ejecutamos el UPDATE
            int rowsAffected = pstmt.executeUpdate();

            // Si no se actualizó ninguna fila, es que no se encontró el ID
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido con ID: " + id + " (o ya estaba eliminado).");
            }
        } catch (SQLException e) {
            // Captura y relanza cualquier error de SQL
            throw new Exception("Error al eliminar lógicamente el pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina lógicamente un pedido (soft delete). Marca eliminado=TRUE sin
     * borrar físicamente la fila.
     * <p>
     * Validaciones: - Si rowsAffected == 0 → El pedido no existe o ya está
     * eliminado
     * <p>
     * IMPORTANTE: NO elimina el envío asociado.
     *
     * @param numero numero del pedido a eliminar
     * @throws Exception Si el pedido no existe o hay error de BD
     */
    public void eliminarLogicoPorNumero(String numero) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL_POR_NUMERO)) {

            pstmt.setString(1, numero);

            // Ejecutamos el UPDATE
            int rowsAffected = pstmt.executeUpdate();

            // Si no se actualizó ninguna fila, es que no se encontró el ID
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido con Numero: " + numero + " (o ya estaba eliminado).");
            }
        } catch (SQLException e) {
            // Captura y relanza cualquier error de SQL
            throw new Exception("Error al eliminar lógicamente el pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarLogicoTx(Long id, Connection conn) throws Exception {
        // NO abre ni cierra la conexión, usa la "conn" recibida
        try (PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido (Tx) con ID: " + id + ".");
            }
        }
    }

    /**
     * Método especial para transacciones: Desvincula un envío de un pedido
     * (SET id_envio = NULL) usando conexión externa.
     *
     * @param pedidoId El ID del pedido a modificar.
     * @param conn     La conexión transaccional.
     * @throws Exception Si falla el UPDATE.
     */
    public void desvincularEnvioTx(long pedidoId, Connection conn) throws Exception {
        try (PreparedStatement pstmt = conn.prepareStatement(DESVINCULAR_ENVIO_SQL)) {
            pstmt.setLong(1, pedidoId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                // esto puede pasar si el pedido no existe, lo cual no debería
                // fallar la transacción si la lógica es "asegurar que esté desvinculado".
                // Pero por consistencia, lanzamos error si no encontró el pedido.
                throw new SQLException("No se encontró el pedido ID: " + pedidoId + " para desvincular.");
            }
        }
    }

    /**
     * Obtiene un pedido por su ID. Incluye su envío asociado mediante LEFT JOIN
     * (carga eager).
     *
     * @param id ID del pedido a buscar
     * @return Pedido encontrado con su envío, o null si no existe o está
     * eliminado
     * @throws Exception Si hay error de BD (captura SQLException y re-lanza)
     */
    @Override
    public Pedido buscarPorId(Long id) throws Exception {

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            // Seteamos el ID
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Si se encuentra resultado:
                if (rs.next()) {
                    // construimos cno el método
                    return mapPedido(rs);
                }
            }
        } catch (SQLException e) {
            // Relanzamos el error con un mensaje claro
            throw new Exception("Error al obtener pedido por ID: " + e.getMessage(), e);
        }

        // Si no se encontró nada, "if (rs.next())" fue falso y retornamos null
        return null;
    }

    /**
     * Obtiene todos los pedidos activos (eliminado=FALSE) con paginación.
     * Incluye sus envíos asociados mediante LEFT JOIN (carga eager).
     *
     * @param cantidad Número de registros por página (si es null, usa 10 por defecto)
     * @param pagina   Número de página (1-indexed, si es null, usa 1 por defecto)
     * @return Lista de pedidos activos con sus envíos (puede estar vacía)
     * @throws Exception Si hay error de BD
     */
    @Override
    public List<Pedido> buscarTodos(Long cantidad, Long pagina) throws Exception {
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
                    pedidos.add(mapPedido(rs));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al listar pedidos: " + e.getMessage(), e);
        }

        return pedidos;
    }

    // OTROS

    /**
     * Busca pedidos por nombre de cliente con búsqueda flexible (LIKE). Permite
     * búsqueda parcial.
     * <p>
     * Patrón de búsqueda: LIKE "%filtro%" en clienteNombre
     *
     * @param filtro Texto a buscar en el nombre del cliente (no puede estar
     *               vacío)
     * @return Lista de pedidos que coinciden con el filtro (puede estar vacía)
     * @throws Exception Si hay error de BD
     */
    public List<Pedido> buscarPorClienteNombre(String filtro) throws Exception {
        if (filtro == null || filtro.trim().isEmpty()) {
            throw new IllegalArgumentException("El filtro de búsqueda no puede estar vacío");
        }

        List<Pedido> pedidos = new ArrayList<>();

        // Usamos la constante SEARCH_BY_NAME_SQL
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_NAME_SQL)) {

            // Construimos el patrón LIKE: %filtro%
            String searchPattern = "%" + filtro + "%";

            pstmt.setString(1, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Reutilizamos el "mapPedido" que ya construimos
                    pedidos.add(mapPedido(rs));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar pedidos por nombre de cliente: " + e.getMessage(), e);
        }
        return pedidos;
    }

    /**
     * Busca un pedido por su NÚMERO exacto (campo UNIQUE). Usa comparación
     * exacta (=).
     *
     * @param numero Número exacto a buscar (ej: "PED-00123")
     * @return Pedido con ese número, o null si no existe o está eliminado
     * @throws Exception Si hay error de BD
     */
    public Pedido buscarPorNumero(String numero) throws Exception {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido no puede estar vacío");
        }

        // Usamos la constante
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_NUMERO_SQL)) {

            // Seteamos el número exacto
            pstmt.setString(1, numero.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Reutilizamos el "mapPedido"
                    return mapPedido(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar pedido por número: " + e.getMessage(), e);
        }

        // No se encontró
        return null;
    }

    /**
     * Busca un pedido basándose en el código de tracking de su envío.
     * Realiza un JOIN y filtra por la tabla Envio.
     *
     * @param tracking Código de seguimiento (ej: "TRK-999")
     * @return El Pedido asociado a ese tracking, o null si no existe.
     * @throws Exception Si hay error de BD
     */
    public Pedido buscarPorTracking(String tracking) throws Exception {
        if (tracking == null || tracking.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de tracking no puede estar vacío");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_TRACKING_SQL)) {

            // Seteamos el tracking (String)
            pstmt.setString(1, tracking.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    return mapPedido(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar pedido por tracking: " + e.getMessage(), e);
        }

        return null;
    }

    // OTROS MÉTODOS HELPERS

    /**
     * Método privado para "setear" todos los parámetros del PreparedStatement
     * de inserción.
     */
    private void setPedidoParameters(PreparedStatement pstmt, Pedido pedido) throws SQLException {

        pstmt.setString(1, pedido.getNumero());

        pstmt.setDate(2, java.sql.Date.valueOf(pedido.getFecha()));

        pstmt.setString(3, pedido.getClienteNombre());

        pstmt.setDouble(4, pedido.getTotal());

        // --- Manejo de FK para el Estado (Enum) ---
        // Verificamos que el objeto Enum no sea nulo antes de pedir su ID
        if (pedido.getEstado() != null) {
            pstmt.setInt(5, pedido.getEstado().getId());
        } else {
            pstmt.setNull(5, java.sql.Types.INTEGER);
        }

        // --- Manejo de FK para el Envío (Objeto) ---
        // Chequeamos si el envío no es nulo Y si su ID no es nulo
        if (pedido.getEnvio() != null && pedido.getEnvio().getId() != null && pedido.getEnvio().getId() > 0) {
            pstmt.setLong(6, pedido.getEnvio().getId());
        } else {
            pstmt.setNull(6, java.sql.Types.BIGINT);
        }
    }

    /**
     * Método privado para recuperar el ID autogenerado y guardarlo en el objeto
     * "pedido".
     */
    private void setGeneratedId(PreparedStatement pstmt, Pedido pedido) throws SQLException {
        // Usamos try-with-resources para el ResultSet
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                // Seteamos el ID generado en el objeto original
                pedido.setId(generatedKeys.getLong(1));
            }
        }
    }

    /**
     * Método helper para mapear un ResultSet a un objeto Pedidos. Está diseñado
     * para manejar el LEFT JOIN de la consulta SELECT_BY_ID_SQL.
     *
     * @param rs El ResultSet en la fila actual
     * @return Un objeto Pedidos completamente populado
     * @throws SQLException
     */
    private Pedido mapPedido(ResultSet rs) throws Exception {

        // --- Mapeamos la entidad principal: Pedidos ---
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setNumero(rs.getString("numero"));
        pedido.setFecha(rs.getDate("fecha").toLocalDate()); // Convertir sql.Date a time.LocalDate
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setTotal(rs.getDouble("total"));
        pedido.setEliminado(rs.getBoolean("eliminado"));

        // --- Mapeamos el Enum EstadoPedido ---
        int idEstadoPedido = rs.getInt("id_estado_pedido");
        pedido.setEstado(EstadoPedido.fromId(idEstadoPedido)); // Usa "fromId" del Enum

        // --- Mapeamos la entidad asociada: Envios (del LEFT JOIN) ---
        // Primero, revisamos si el LEFT JOIN trajo un envío.
        // Lo hacemos revisando el ID del envío.
        long idEnvio = rs.getLong("envio_id");

        if (rs.wasNull()) {
            // No se encontró envío (id_envio era NULL en la BD o el LEFT JOIN falló)
            pedido.setEnvio(null);
        } else {
            // ¡Sí encontramos un envío! Lo construimos.
            Envio envio = new Envio();
            envio.setId(idEnvio);
            envio.setTracking(rs.getString("envio_tracking")); // ¡Usa el alias!
            envio.setCosto(rs.getDouble("envio_costo"));       // ¡Usa el alias!

            // Asumiendo que 'fecha_despacho' y 'fecha_estimada' pueden ser nulas
            if (rs.getDate("envio_fecha_despacho") != null) {
                envio.setFechaDespacho(rs.getDate("envio_fecha_despacho").toLocalDate());

            }
            if (rs.getDate("envio_fecha_estimada") != null) {
                envio.setFechaEstimada(rs.getDate("envio_fecha_estimada").toLocalDate());
            }

            // Mapear los Enums de Envío (asumiendo que tienes helpers "fromId")
            envio.setEmpresa(EmpresaEnvio.fromId(rs.getInt("envio_id_empresa")));
            envio.setTipo(TipoEnvio.fromId(rs.getInt("envio_id_tipo")));
            envio.setEstado(EstadoEnvio.fromId(rs.getInt("envio_id_estado")));

            // Finalmente, asociamos el envío al pedido
            pedido.setEnvio(envio);
        }

        return pedido;
    }

    public String buscarUltimoNumeroPedido() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_MAX_NUMERO_PEDIDO)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    return rs.getString("numero");
                }
            }
        }
        return null;
    }

    public Long obtenerCantidadTotalDePedidos() throws SQLException {
        Long cantidad = 0L;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(COUNT_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                cantidad = rs.getLong("total");
            }
        }
        return cantidad;
    }


}
