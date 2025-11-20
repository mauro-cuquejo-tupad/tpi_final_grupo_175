package gestorenvios.ui.console.output;

import gestorenvios.entities.Pedido;
import gestorenvios.ui.console.utils.ConsoleUtils;

/***
 * Utilidad para imprimir información de pedidos en formato de tabla en consola.
 */
public class PedidoPrinter {
    private static final int ANCHO_ID = 6;
    private static final int ANCHO_CLIENTE = 20;
    private static final int ANCHO_NUMERO = 12;
    private static final int ANCHO_ESTADO = 12;
    private static final int ANCHO_ENVIO = 36;
    private static final int ANCHO_ESTADO_ENVIO = 15;

    /*** Separador de columnas para la tabla. */
    public static final String SEP_COLUMNA = " | ";

    /***
     * Constructor privado para evitar instanciación.
     */
    private PedidoPrinter() {
        // Constructor privado para evitar instanciación
    }

    /***
     * Imprime la cabecera de la tabla de pedidos en consola.
     */
    public static void mostrarCabecera() {
        String cabecera = ConsoleUtils.padRight("ID", ANCHO_ID) + SEP_COLUMNA +
                ConsoleUtils.padRight("Cliente", ANCHO_CLIENTE) + SEP_COLUMNA +
                ConsoleUtils.padRight("Nº", ANCHO_NUMERO) + SEP_COLUMNA +
                ConsoleUtils.padRight("Estado", ANCHO_ESTADO) + SEP_COLUMNA +
                ConsoleUtils.padRight("Envío", ANCHO_ENVIO) + SEP_COLUMNA +
                ConsoleUtils.padRight("Estado envío", ANCHO_ESTADO_ENVIO) + SEP_COLUMNA;

        ConsoleUtils.imprimirInfo(cabecera);
        ConsoleUtils.imprimirInfo(separadorFila(cabecera.length() - 1));
    }

    /***
     * Imprime el detalle de un pedido en formato de fila de tabla.
     * @param pedido Pedido a mostrar
     */
    public static void mostrarDetalle(Pedido pedido) {
        if (pedido == null || pedido.getEliminado()) {
            ConsoleUtils.imprimirError("❌ No se encontró el pedido.");
            return;
        }
        String envioTracking = pedido.getEnvio() != null && !pedido.getEnvio().getEliminado()
                ? pedido.getEnvio().getTracking()
                : "Sin envío";
        String envioEstado = pedido.getEnvio() != null && !pedido.getEnvio().getEliminado()
                ? pedido.getEnvio().getEstado().name()
                : "-";

        String detalle = ConsoleUtils.padRight(pedido.getId().toString(), ANCHO_ID) + SEP_COLUMNA +
                ConsoleUtils.padRight(pedido.getClienteNombre(), ANCHO_CLIENTE) + SEP_COLUMNA +
                ConsoleUtils.padRight(pedido.getNumero(), ANCHO_NUMERO) + SEP_COLUMNA +
                ConsoleUtils.padRight(pedido.getEstado().name(), ANCHO_ESTADO) + SEP_COLUMNA +
                ConsoleUtils.padRight(envioTracking, ANCHO_ENVIO) + SEP_COLUMNA +
                ConsoleUtils.padRight(envioEstado, ANCHO_ESTADO_ENVIO) + SEP_COLUMNA;

        ConsoleUtils.imprimirInfo(detalle);
    }

    /***
     * Devuelve una línea separadora para la tabla, útil para mejorar la visualización.
     * @param n Longitud de la línea
     * @return Cadena de guiones de longitud n
     */
    private static String separadorFila(int n) {
        return new String(new char[n]).replace("\0", "-");
    }
}
