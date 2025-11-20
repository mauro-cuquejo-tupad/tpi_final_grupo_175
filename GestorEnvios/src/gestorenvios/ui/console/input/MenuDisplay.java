package gestorenvios.ui.console.input;

import gestorenvios.ui.console.utils.ConsoleUtils;

/***
 * Clase utilitaria para mostrar menús en la consola.
 */
public class MenuDisplay {

    private MenuDisplay() {
        // Constructor privado para evitar instanciación
    }

    /***
     * Muestra el menú principal de la aplicación en la consola.
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