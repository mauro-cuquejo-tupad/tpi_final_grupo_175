package gestorenvios.ui.console.output;

import gestorenvios.ui.console.controllers.DbInitController;
import gestorenvios.ui.console.input.InputReader;
import gestorenvios.ui.console.utils.ConsoleUtils;

public class PantallaBienvenida {

    private PantallaBienvenida() {
    }

    /***
     * Muestra la pantalla de bienvenida y verifica la conexión a la base de datos.
     *
     * @return true si la conexión es exitosa, false en caso contrario.
     */
    public static boolean mostrar(InputReader input) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.imprimirInfo("=============================================");
        ConsoleUtils.imprimirInfo("=                                           =");
        ConsoleUtils.imprimirInfo("=      BIENVENIDO AL GESTOR DE ENVÍOS       =");
        ConsoleUtils.imprimirInfo("=                                           =");
        ConsoleUtils.imprimirInfo("=============================================");
        ConsoleUtils.imprimirLineaVacia();
        ConsoleUtils.imprimirMensaje("Verificando conexión con la base de datos... ");

        try {
            if (DbInitController.checkConnection()) {
                ConsoleUtils.imprimirMensaje("✅ ¡Conexión exitosa!");
                ConsoleUtils.imprimirLineaVacia();
                if (!DbInitController.hasTables()) {
                    ConsoleUtils.imprimirAdvertencia("La base de datos está vacía.");
                    ConsoleUtils.imprimirMensaje("Inicializando...");
                    DbInitController.inicializarBaseDeDatos();
                }
                if (input.prompt("¿Desea cargar datos de prueba? (s/n): ").equalsIgnoreCase("s")) {
                    DbInitController.inicializarBaseDeDatos();
                    DbInitController.cargarDatosIniciales();
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ ¡ERROR!");
            ConsoleUtils.imprimirMensaje("Detalle del error: " + e.getMessage());
            ConsoleUtils.imprimirAdvertencia("Por favor, revise la configuración en 'application.properties' y asegúrese de que el servidor de base de datos esté en ejecución.");
        }
        return false;
    }
}
