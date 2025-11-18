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
        ConsoleUtils.imprimirLineaVacia();
        ConsoleUtils.imprimirDivisores("GESTOR DE ENVIOS");
        ConsoleUtils.imprimirDivisores("MENU");
        ConsoleUtils.imprimirLineaVacia();
        ConsoleUtils.imprimirDivisores("PEDIDOS");
        ConsoleUtils.formatearOpcion(1, "Crear pedido");
        ConsoleUtils.formatearOpcion(2, "Listar pedidos");
        ConsoleUtils.formatearOpcion(3, "Buscar pedido por número");
        ConsoleUtils.formatearOpcion(4, "Buscar pedido por tracking");
        ConsoleUtils.formatearOpcion(5, "Buscar pedido por ID");
        ConsoleUtils.formatearOpcion(6, "Buscar pedido por Cliente");
        ConsoleUtils.formatearOpcion(7, "Actualizar pedido por número");
        ConsoleUtils.formatearOpcion(8, "Eliminar pedido por número");
        ConsoleUtils.formatearOpcion(9, "Eliminar pedido por ID");
        ConsoleUtils.imprimirLineaVacia();
        ConsoleUtils.imprimirDivisores("ENVIOS");
        ConsoleUtils.formatearOpcion(10, "Crear envío");
        ConsoleUtils.formatearOpcion(11, "Listar envíos");
        ConsoleUtils.formatearOpcion(12, "Buscar envío por tracking");
        ConsoleUtils.formatearOpcion(13, "Buscar envío por número de pedido");
        ConsoleUtils.formatearOpcion(14, "Buscar envío por ID");
        ConsoleUtils.formatearOpcion(15, "Actualizar estado envío por tracking");
        ConsoleUtils.formatearOpcion(16, "Actualizar estado envío por número de pedido");
        ConsoleUtils.formatearOpcion(17, "Actualizar estado envío por ID");
        ConsoleUtils.formatearOpcion(18, "Eliminar envío por tracking");
        ConsoleUtils.formatearOpcion(19, "Eliminar envío por número de pedido");
        ConsoleUtils.formatearOpcion(20, "Eliminar envío por ID");
        ConsoleUtils.imprimirLineaVacia();
        ConsoleUtils.formatearOpcion(0, "Salir");
        ConsoleUtils.imprimirLineaVacia();
        ConsoleUtils.imprimirInfo("Seleccione una opción: ");
    }


}
