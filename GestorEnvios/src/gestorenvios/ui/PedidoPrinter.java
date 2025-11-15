// java
package gestorenvios.ui;

import gestorenvios.entities.Pedido;

public class PedidoPrinter {
    public static void printSummary(Pedido p) {
        if (p == null) {
            System.out.println("❌ No se encontró el pedido.");
            return;
        }
        System.out.println("ID: " + p.getId() + " | Cliente: " + p.getClienteNombre() + " | Nº: " + p.getNumero());
        if (p.getEnvio() != null) {
            System.out.println("Envío: " + p.getEnvio().getTracking() + " | Estado envío: " + p.getEnvio().getEstado());
        } else {
            System.out.println("⚠ El pedido no tiene envío asociado.");
        }
    }
}
