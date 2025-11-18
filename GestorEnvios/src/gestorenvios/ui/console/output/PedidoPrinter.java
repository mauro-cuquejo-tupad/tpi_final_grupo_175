package gestorenvios.ui.console.output;

import gestorenvios.entities.Pedido;
import gestorenvios.ui.console.utils.ConsoleUtils;

public class PedidoPrinter {

    private static final int ANCHO_ID = 6;
    private static final int ANCHO_CLIENTE = 20;
    private static final int ANCHO_NUMERO = 12;
    private static final int ANCHO_ESTADO = 12;
    private static final int ANCHO_ENVIO = 36;
    private static final int ANCHO_ESTADO_ENVIO = 12;

    private static final String ALINEACION_IZQ = "%-";

    private PedidoPrinter() {
        // Constructor privado para evitar instanciación
    }

    public static void mostrarCabecera() {
        String cabeceraTabla = String.format(ALINEACION_IZQ + ANCHO_ID + "s | " +
                        ALINEACION_IZQ + ANCHO_CLIENTE + "s | " +
                        ALINEACION_IZQ + ANCHO_NUMERO + "s | " +
                        ALINEACION_IZQ + ANCHO_ESTADO + "s | " +
                        ALINEACION_IZQ + ANCHO_ENVIO + "s | " +
                        ALINEACION_IZQ + ANCHO_ESTADO_ENVIO + "s",
                "ID", "Cliente", "Nº", "Estado", "Envío", "Estado envío");
        ConsoleUtils.imprimirInfo(cabeceraTabla);
        ConsoleUtils.imprimirInfo(finalizarCabeceraTabla(cabeceraTabla.length()));
    }

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
        String detalle = String.format(ALINEACION_IZQ + ANCHO_ID + "s | %-" + ANCHO_CLIENTE + "s | %-" + ANCHO_NUMERO + "s | %-" + ANCHO_ESTADO + "s | %-" + ANCHO_ENVIO + "s | %-" + ANCHO_ESTADO_ENVIO + "s",
                pedido.getId(), pedido.getClienteNombre(), pedido.getNumero(), pedido.getEstado(), envioTracking, envioEstado);
        ConsoleUtils.imprimirInfo(detalle);
    }

    private static String finalizarCabeceraTabla(int n) {
        return new String(new char[n]).replace("\0", "-");
    }
}
