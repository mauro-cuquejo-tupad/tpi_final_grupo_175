// java
package gestorenvios.ui.console.output;

import gestorenvios.entities.Pedido;

public class PedidoPrinter {

    private PedidoPrinter() {
        // Constructor privado para evitar instanciación
    }
    public static void mostrarResumen(Pedido p) {
        if (p == null) {
            System.out.println("❌ No se encontró el pedido.");
            return;
        }
        System.out.println("ID: " + p.getId()
                + " | Cliente: "
                + p.getClienteNombre()
                + " | Nº: " + p.getNumero()
                + " | Estado: " + p.getEstado());
        if (p.getEnvio() != null) {
            System.out.println("Envío: " + p.getEnvio().getTracking() + " | Estado envío: " + p.getEnvio().getEstado());
        } else {
            System.out.println("⚠ El pedido no tiene envío asociado.");
        }
    }
}
