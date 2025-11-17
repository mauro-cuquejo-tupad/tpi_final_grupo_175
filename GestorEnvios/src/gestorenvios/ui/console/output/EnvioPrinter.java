// java
package gestorenvios.ui.console.output;

import gestorenvios.entities.Envio;

public class EnvioPrinter {

    private EnvioPrinter(){
        // Constructor privado para evitar instanciación
    }
    public static void mostrarResumen(Envio envio) {
        if (envio == null || envio.getEliminado()) {
            System.out.println("❌ No se encontró el envio.");
            return;
        }
        System.out.println("ID: "
                + envio.getId()
                + " | Empresa: " + envio.getEmpresa()
                + " | Tracking Nº: " + envio.getTracking()
                + "| Estado: " + envio.getEstado());
    }
}
