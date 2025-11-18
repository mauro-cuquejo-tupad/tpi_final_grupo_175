package gestorenvios.ui.console.output;

import gestorenvios.entities.Envio;
import gestorenvios.ui.console.utils.ConsoleUtils;

public class EnvioPrinter {

    private static final int ANCHO_ID = 6;
    private static final int ANCHO_EMPRESA = 20;
    private static final int ANCHO_TRACKING = 36;
    private static final int ANCHO_ESTADO = 15;

    public static final String SEP_COLUMNA = " | ";

    private EnvioPrinter() {
        // Constructor privado para evitar instanciación
    }

    public static void mostrarCabecera() {
        String cabecera = ConsoleUtils.padRight("ID", ANCHO_ID) + SEP_COLUMNA +
                ConsoleUtils.padRight("Empresa", ANCHO_EMPRESA) + SEP_COLUMNA +
                ConsoleUtils.padRight("Tracking Nº", ANCHO_TRACKING) + SEP_COLUMNA +
                ConsoleUtils.padRight("Estado", ANCHO_ESTADO) + SEP_COLUMNA;
        ConsoleUtils.imprimirInfo(cabecera);
    }

    public static void mostrarDetalle(Envio envio) {
        if (envio == null || envio.getEliminado()) {
            ConsoleUtils.imprimirError("❌ No se encontró el envio.");
            return;
        }
        String detalle = ConsoleUtils.padRight(envio.getId().toString(), ANCHO_ID) + SEP_COLUMNA +
                ConsoleUtils.padRight(envio.getEmpresa().name(), ANCHO_EMPRESA) + SEP_COLUMNA +
                ConsoleUtils.padRight(envio.getTracking(), ANCHO_TRACKING) + SEP_COLUMNA +
                ConsoleUtils.padRight(envio.getEstado().name(), ANCHO_ESTADO) + SEP_COLUMNA;
        ConsoleUtils.imprimirInfo(detalle);
        ConsoleUtils.imprimirInfo(separadorFila(detalle.length() - 1));
    }

    private static String separadorFila(int n) {
        return new String(new char[n]).replace("\0", "-");
    }
}
