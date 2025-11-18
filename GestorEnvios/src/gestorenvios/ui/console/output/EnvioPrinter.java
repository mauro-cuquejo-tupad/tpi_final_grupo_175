package gestorenvios.ui.console.output;

import gestorenvios.entities.Envio;
import gestorenvios.ui.console.utils.ConsoleUtils;

public class EnvioPrinter {

    private static final int ANCHO_ID = 6;
    private static final int ANCHO_EMPRESA = 20;
    private static final int ANCHO_TRACKING = 36;
    private static final int ANCHO_ESTADO = 12;

    private EnvioPrinter(){
        // Constructor privado para evitar instanciación
    }
    public static void mostrarCabecera() {
        String cabecera = String.format("%-" + ANCHO_ID + "s | %-" + ANCHO_EMPRESA + "s | %-" + ANCHO_TRACKING + "s | %-" + ANCHO_ESTADO + "s",
                "ID", "Empresa", "Tracking Nº", "Estado");
        ConsoleUtils.imprimirInfo(cabecera);
        ConsoleUtils.imprimirInfo(repeat("-", cabecera.length()));
    }

    public static void mostrarDetalle(Envio envio) {
        if (envio == null || envio.getEliminado()) {
            ConsoleUtils.imprimirError("❌ No se encontró el envio.");
            return;
        }
        String detalle = String.format("%-" + ANCHO_ID + "s | %-" + ANCHO_EMPRESA + "s | %-" + ANCHO_TRACKING + "s | %-" + ANCHO_ESTADO + "s",
                envio.getId(), envio.getEmpresa(), envio.getTracking(), envio.getEstado());
        ConsoleUtils.imprimirInfo(detalle);
    }

    private static String repeat(String s, int n) {
        return new String(new char[n]).replace("\0", s);
    }
}
