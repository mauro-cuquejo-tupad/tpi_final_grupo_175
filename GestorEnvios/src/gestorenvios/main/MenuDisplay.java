package gestorenvios.main;

public class MenuDisplay {

    /**
     * Muestra el menú principal con todas las opciones CRUD.
     * <p>
     * Opciones para Pedidos (1-4):
     * 1. Crear pedido: Permite crear pedido con envio opcional
     * 2. Listar pedidos: Lista todos o busca por nombre de cliente o numero
     * 3. Actualizar pedido: Actualiza datos de pedido y opcionalmente su envio
     * 4. Eliminar pedido: Soft delete de pedido
     * (NO elimina envio asociado)
     * <p>
     * Opciones para envio (5-10):
     * 5. Crear envio: genera envio independiente (sin asociar a pedido)
     * 6. Listar envios: Lista todos los envios activos
     * 7. Actualizar envio por ID: Actualiza envio directamente
     * (afecta a TODOS los pedidos)
     * 8. Eliminar envio por ID: PELIGROSO - puede dejar FKs huérfanas
     * 9. Actualizar envio por ID de pedido: Busca pedido primero, luego
     * actualiza su envio
     * 10. Eliminar envio por ID de pedido: SEGURO -
     * actualiza FK primero, luego elimina
     * 11. Buscar un pedido por Tracking
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
        System.out.println("\n========= MENU =========");
        System.out.println("1. Crear un pedido");
        System.out.println("2. Listar pedidos");
        System.out.println("3. Actualizar pedido");
        System.out.println("4. Eliminar pedido");
        System.out.println("5. Crear envio");
        System.out.println("6. Listar envios");
        System.out.println("7. Actualizar envio por ID");
        System.out.println("8. Eliminar envio por ID");
        System.out.println("9. Actualizar envio por ID de pedido");
        System.out.println("10. Eliminar envio por ID de pedido");
        System.out.println("11. Buscar Pedido por Tracking");
        System.out.println("0. Salir");
        System.out.print("Ingrese una opción: ");
    }
}
