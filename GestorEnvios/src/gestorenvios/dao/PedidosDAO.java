package gestorenvios.dao;

import gestorenvios.config.DatabaseConnection;
import gestorenvios.entities.EmpresaEnvio;
import gestorenvios.entities.Envios;
import gestorenvios.entities.EstadoEnvio;
import gestorenvios.entities.EstadoPedido;
import gestorenvios.entities.Pedidos;
import gestorenvios.entities.TipoEnvio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Grupo_175
 */
/**
 * Data Access Object para la entidad Pedidos. Gestiona todas las operaciones de
 * persistencia de pedidos en la base de datos.
 *
 */
public class PedidosDAO implements GenericDAO<Pedidos> {

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

    /**
     * Query para obtener pedido por ID. LEFT JOIN con envio para cargar la
     * relación de forma eager. Solo retorna pedidos activos (eliminado=FALSE).
     */
    private static final String SELECT_BY_ID_SQL
            = "SELECT p.id, p.numero, p.fecha, p.clienteNombre, p.total, p.id_estado_pedido, p.eliminado, p.id_envio, "
            + "e.id AS envio_id, e.tracking AS envio_tracking, e.id_empresa AS envio_id_empresa, e.id_tipo_envio AS envio_id_tipo, "
            + "e.costo AS envio_costo, e.fechaDespacho AS envio_fechaDespacho, e.fechaEstimada AS envio_fechaEstimada, e.id_estado_envio AS envio_id_estado "
            + "FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id "
            + "WHERE p.eliminado = FALSE AND p.id = ?";

    /*
         * Query para obtener todos los pedidos activos.
         * LEFT JOIN con envios para cargar relaciones.
         * Filtra por eliminado=FALSE (solo pedidos activos).
     */
    private static final String SELECT_ALL_SQL
            = "SELECT p.id, p.numero, p.fecha, p.clienteNombre, p.total, p.id_estado_pedido, p.eliminado, p.id_envio, "
            + "e.id AS envio_id, e.tracking AS envio_tracking, e.id_empresa AS envio_id_empresa, e.id_tipo_envio AS envio_id_tipo, "
            + "e.costo AS envio_costo, e.fechaDespacho AS envio_fechaDespacho, e.fechaEstimada AS envio_fechaEstimada, e.id_estado_envio AS envio_id_estado "
            + "FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id "
            + "WHERE p.eliminado = FALSE";

    /*
         * Query de búsqueda por clienteNombre con LIKE.
         * Permite búsqueda flexible
         * Solo pedidos activos (eliminado=FALSE).
     */
    private static final String SEARCH_BY_NAME_SQL
            = "SELECT p.id, p.numero, p.fecha, p.clienteNombre, p.total, p.id_estado_pedido, p.eliminado, p.id_envio, "
            + "e.id AS envio_id, e.tracking AS envio_tracking, e.id_empresa AS envio_id_empresa, e.id_tipo_envio AS envio_id_tipo, "
            + "e.costo AS envio_costo, e.fechaDespacho AS envio_fechaDespacho, e.fechaEstimada AS envio_fechaEstimada, e.id_estado_envio AS envio_id_estado "
            + "FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id "
            + "WHERE p.eliminado = FALSE AND (p.clienteNombre LIKE ?)";

    /**
     * Query de búsqueda exacta por NÚMERO de pedido. Incluye LEFT JOIN con
     * envíos. Filtra por eliminado=FALSE.
     */
    private static final String SEARCH_BY_NUMERO_SQL
            = "SELECT p.id, p.numero, p.fecha, p.clienteNombre, p.total, p.id_estado_pedido, p.eliminado, p.id_envio, "
            + "e.id AS envio_id, e.tracking AS envio_tracking, e.id_empresa AS envio_id_empresa, e.id_tipo_envio AS envio_id_tipo, "
            + "e.costo AS envio_costo, e.fechaDespacho AS envio_fechaDespacho, e.fechaEstimada AS envio_fechaEstimada, e.id_estado_envio AS envio_id_estado "
            + "FROM Pedido p LEFT JOIN Envio e ON p.id_envio = e.id "
            + "WHERE p.eliminado = FALSE AND p.numero = ?";

    // inyección de dependencias//
    /**
     * DAO de envios (actualmente no usado, pero disponible para operaciones
     * futuras). Inyectado en el constructor por si se necesita coordinar
     * operaciones.
     */
    private final EnviosDAO enviosDAO;

    /**
     * Constructor con inyección de EnviosDAO. Valida que la dependencia no sea
     * null (fail-fast).
     *
     * @param enviosDAO DAO de envios
     * @throws IllegalArgumentException si domicilioDAO es null
     */
    public PedidosDAO(EnviosDAO enviosDAO) {
        if (enviosDAO == null) {
            throw new IllegalArgumentException("EnviosDAO no puede ser null");
        }
        this.enviosDAO = enviosDAO;

    }

    /// @param pedido/
    /// @throws java.lang.Exception
    
       @Override
    public void insertar(Pedidos pedido) throws Exception {

        // Usamos try-with-resources para la conexión y el statement
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Llamada al método para setear los VALUES '?'
            setPedidoParameters(pstmt, pedido);

            // Ejecuta la inserción
            pstmt.executeUpdate();

            // Método para recuperar los ID autogenerados
            setGeneratedId(pstmt, pedido);

        } catch (SQLException e) {
            // Captura el error de SQL y lo relanza como una excepción genérica
            throw new Exception("Error al insertar el pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Inserta un pedido dentro de una transacción existente. NO crea nueva
     * conexión, recibe una Connection externa. NO cierra la conexión
     * (responsabilidad del caller, ej. un TransactionManager).
     *
     * Usado por: - Operaciones que requieren múltiples inserts coordinados (ej.
     * Pedido + Envío) - Rollback automático si alguna operación falla
     *
     * @param pedido Pedido a insertar
     * @param conn Conexión transaccional (NO se cierra en este método)
     * @throws Exception Si falla la inserción
     */
    @Override
    public void insertTx(Pedidos pedido, Connection conn) throws Exception {

        // Este método asume que la conexión (conn) es manejada 
        // (abierta, cerrada, commit, rollback) por un servicio externo.
        // El try-with-resources SÓLO se aplica al PreparedStatement.
        // ¡No cierra la 'conn'!
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Reutilizamos el primer método
            setPedidoParameters(pstmt, pedido);

            // ejecución
            pstmt.executeUpdate();

            // Reutilizamos el segundo método
            setGeneratedId(pstmt, pedido);
        }

        // Si ocurre una SQLException, se lanza 'throws Exception'
        // y el servicio que maneja la transacción deberá hacer un 'rollback'.
    }

    /**
     * Actualiza un pedido existente en la base de datos. NO actualiza el flag
     * 'eliminado'.
     *
     * Validaciones: - Si rowsAffected == 0 → El pedido no existe o ya está
     * eliminado (y no se puede encontrar).
     *
     * IMPORTANTE: Este método puede cambiar las FKs: - Si pedido.estado == null
     * → id_estado_pedido = NULL (desasociar) - Si pedido.envio == null →
     * id_envio = NULL (desasociar)
     *
     * @param pedido pedido con los datos actualizados (id debe ser > 0)
     * @throws Exception Si el pedido no existe o hay error de BD
     */
    @Override
    public void actualizar(Pedidos pedido) throws Exception {

        // El try-with-resources para la conexión y el statement
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

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

        } catch (SQLException e) {
            throw new Exception("Error al actualizar el pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina lógicamente un pedido (soft delete). Marca eliminado=TRUE sin
     * borrar físicamente la fila.
     *
     * Validaciones: - Si rowsAffected == 0 → El pedido no existe o ya está
     * eliminado
     *
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
     * Obtiene un pedido por su ID. Incluye su envío asociado mediante LEFT JOIN
     * (carga eager).
     *
     * @param id ID del pedido a buscar
     * @return Pedido encontrado con su envío, o null si no existe o está
     * eliminado
     * @throws Exception Si hay error de BD (captura SQLException y re-lanza)
     */
    @Override
    public Pedidos buscarPorId(Long id) throws Exception {

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

        // Si no se encontró nada, 'if (rs.next())' fue falso y retornamos null
        return null;
    }

    /**
     * Obtiene todos los pedidos activos (eliminado=FALSE). Incluye sus envíos
     * asociados mediante LEFT JOIN (carga eager).
     *
     * Nota: Usa Statement (no PreparedStatement) porque no hay parámetros.
     *
     * @return Lista de pedidos activos con sus envíos (puede estar vacía)
     * @throws Exception Si hay error de BD
     */
    @Override
    public List<Pedidos> buscarTodos() throws Exception {
        // Inicializamos la lista vacía
        List<Pedidos> pedidos = new ArrayList<>();

        // Usamos la constante SELECT_ALL_SQL
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SQL); 
                ResultSet rs = pstmt.executeQuery()) {

            // Itera sobre CADA fila del resultado
            while (rs.next()) {
                // Reutilizamos el mismo "mapPedido" para construir el objeto
                pedidos.add(mapPedido(rs));
            }
        } catch (SQLException e) {
            // verificamos el error
            throw new Exception("Error al obtener todos los pedidos: " + e.getMessage(), e);
        }

        // 6. Retorna la lista (estará vacía si no se encontró nada)
        return pedidos;
    }

    // OTROS
    /**
     * Busca pedidos por nombre de cliente con búsqueda flexible (LIKE). Permite
     * búsqueda parcial.
     *
     * Patrón de búsqueda: LIKE '%filtro%' en clienteNombre
     *
     * @param filtro Texto a buscar en el nombre del cliente (no puede estar
     * vacío)
     * @return Lista de pedidos que coinciden con el filtro (puede estar vacía)
     * @throws Exception Si hay error de BD
     */
    public List<Pedidos> buscarPorClienteNombre(String filtro) throws Exception {
        if (filtro == null || filtro.trim().isEmpty()) {
            throw new IllegalArgumentException("El filtro de búsqueda no puede estar vacío");
        }

        List<Pedidos> pedidos = new ArrayList<>();

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
    public Pedidos buscarPorNumero(String numero) throws Exception {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido no puede estar vacío");
        }

        // Usamos la constante
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_NUMERO_SQL)) {

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

    // OTROS MÉTODOS HELPERS
    /**
     * Método privado para "setear" todos los parámetros del PreparedStatement
     * de inserción.
     */
    private void setPedidoParameters(PreparedStatement pstmt, Pedidos pedido) throws SQLException {

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
     * 'pedido'.
     */
    private void setGeneratedId(PreparedStatement pstmt, Pedidos pedido) throws SQLException {
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
    private Pedidos mapPedido(ResultSet rs) throws Exception {

        // --- Mapeamos la entidad principal: Pedidos ---
        Pedidos pedido = new Pedidos();
        pedido.setId(rs.getLong("id"));
        pedido.setNumero(rs.getString("numero"));
        pedido.setFecha(rs.getDate("fecha").toLocalDate()); // Convertir sql.Date a time.LocalDate
        pedido.setClienteNombre(rs.getString("clienteNombre"));
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
            Envios envio = new Envios();
            envio.setId(idEnvio);
            envio.setTracking(rs.getString("envio_tracking")); // ¡Usa el alias!
            envio.setCosto(rs.getDouble("envio_costo"));       // ¡Usa el alias!

            // Asumiendo que 'fechaDespacho' y 'fechaEstimada' pueden ser nulas
            if (rs.getDate("envio_fechaDespacho") != null) {
                envio.setFechaDespacho(rs.getDate("envio_fechaDespacho").toLocalDate());
            }
            if (rs.getDate("envio_fechaEstimada") != null) {
                envio.setFechaEstimada(rs.getDate("envio_fechaEstimada").toLocalDate());
            }

            // Mapear los Enums de Envío (asumiendo que tienes helpers 'fromId')
            envio.setEmpresa(EmpresaEnvio.fromId(rs.getInt("envio_id_empresa")));
            envio.setTipo(TipoEnvio.fromId(rs.getInt("envio_id_tipo")));
            envio.setEstado(EstadoEnvio.fromId(rs.getInt("envio_id_estado")));

            // Finalmente, asociamos el envío al pedido
            pedido.setEnvio(envio);
        }

        return pedido;
    }

}
