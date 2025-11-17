// java
package gestorenvios.ui.console.output;

import gestorenvios.entities.Pedido;

public class PedidoPrinter {

    private PedidoPrinter() {
        // Constructor privado para evitar instanciación
    }
    public static void mostrarResumen(Pedido pedido) {
        if (pedido == null || pedido.getEliminado()) {
            System.out.println("❌ No se encontró el pedido.");
            return;
        }
        System.out.println("ID: " + pedido.getId()
                + " | Cliente: "
                + pedido.getClienteNombre()
                + " | Nº: " + pedido.getNumero()
                + " | Estado: " + pedido.getEstado());
        if (pedido.getEnvio() != null && !pedido.getEnvio().getEliminado()) {
            System.out.println("Envío: " + pedido.getEnvio().getTracking() + " | Estado envío: " + pedido.getEnvio().getEstado());
        } else {
            System.out.println("⚠ El pedido no tiene envío asociado.");
        }
    }
}
