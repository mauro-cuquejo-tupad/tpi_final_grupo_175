package gestorenvios.ui.console.input;

import gestorenvios.ui.console.utils.ConsoleUtils;

public class MenuDisplay {

    private MenuDisplay() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Muestra el menú principal con todas las opciones CRUD.
     * <p>
     * Opciones para Pedidos (1-5):
     * 1. Crear pedido: Permite crear pedido
     * 2. Listar pedidos: Lista todos o busca por nombre de cliente o numero
     * 3. Actualizar pedido: Actualiza datos de pedido y opcionalmente su envio
     * 4. Eliminar pedido: Soft delete de pedido (NO elimina envio asociado)
     * 5. Buscar un pedido por Tracking
     * <p>
     * Opciones para envio (6-13):
     * 6. Crear envio: genera envio independiente (sin asociar a pedido)
     * 7. Listar envios: Lista todos los envios activos
     * 8. Actualizar envio por Numero: Actualiza envio directamente
     * 9. Actualizar envio por ID: Actualiza envio directamente (afecta a TODOS los pedidos)
     * 10. Eliminar envio por ID: PELIGROSO - puede dejar FKs huérfanas
     * 11. Eliminar envio por Numero: Elimina envio por numero
     * 12. Actualizar envio por ID de pedido: Busca pedido primero, luego actualiza su envio
     * 13. Eliminar envio por ID de pedido: SEGURO - actualiza FK primero, luego elimina
     * <p>
     * Opción de salida: 0. Salir: Termina la aplicación
     * <p>
     * Formato: - Separador visual "========= MENU =========" - Lista numerada
     * clara - Prompt "Ingrese una opcion: " sin salto de línea (espera input)
     * <p>
     * Nota: Los números de opción corresponden al switch en
     * AppMenu.processOption().
     */
    public static void mostrarMenuPrincipal() {
        ConsoleUtils.enterParaContinuar();
        ConsoleUtils.clearScreen();
        System.out.println();
        System.out.println("=== GESTOR DE ENVIOS ===");
        System.out.println("=== MENU ===");
        System.out.println();
        System.out.println("PEDIDOS");
        System.out.println("  1  - Crear pedido");
        System.out.println("  2  - Listar pedidos");
        System.out.println("  3  - Buscar pedido por número");
        System.out.println("  4  - Buscar pedido por tracking");
        System.out.println("  5  - Buscar pedido por ID");
        System.out.println("  6  - Buscar pedido por Cliente");
        System.out.println("  7  - Actualizar pedido por número");
        System.out.println("  8  - Eliminar pedido por número");
        System.out.println("  9  - Eliminar pedido por ID");
        System.out.println();
        System.out.println("ENVIOS");
        System.out.println(" 10   - Crear envío");
        System.out.println(" 11  - Listar envíos");
        System.out.println(" 12  - Buscar envío por tracking");
        System.out.println(" 13  - Buscar envío por número de pedido");
        System.out.println(" 14  - Buscar envío por ID");
        System.out.println(" 15  - Actualizar estado envío por tracking");
        System.out.println(" 16  - Actualizar estado envío por número de pedido");
        System.out.println(" 17  - Actualizar estado envío por ID");
        System.out.println(" 18  - Eliminar envío por tracking");
        System.out.println(" 19  - Eliminar envío por número de pedido");
        System.out.println(" 20  - Eliminar envío por ID");
        System.out.println();
        System.out.println("  0  - Salir");
        System.out.println();
        System.out.print("Seleccione una opción: ");
    }
}
