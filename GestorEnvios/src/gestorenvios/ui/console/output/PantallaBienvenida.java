package gestorenvios.ui.console.output;

import gestorenvios.ui.console.controllers.DbInitController;
import gestorenvios.ui.console.input.InputReader;
import gestorenvios.ui.console.utils.ConsoleUtils;

public class PantallaBienvenida {

    private PantallaBienvenida() {
    }

    /**
     * Muestra la pantalla de bienvenida y verifica la conexión a la base de datos.
     *
     * @return true si la conexión es exitosa, false en caso contrario.
     */
    public static boolean mostrar(InputReader input) {
        ConsoleUtils.clearScreen();
        System.out.println("=============================================");
        System.out.println("=                                           =");
        System.out.println("=      BIENVENIDO AL GESTOR DE ENVÍOS       =");
        System.out.println("=                                           =");
        System.out.println("=============================================");
        System.out.println();
        System.out.print("Verificando conexión con la base de datos... ");

        try {
            if (DbInitController.checkConnection()) {
                System.out.println("✅ ¡Conexión exitosa!");
                System.out.println();
                if (!DbInitController.hasTables()) {
                    System.out.println("La base de datos está vacía.");
                    System.out.println("Inicializando...");
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
            System.out.println("❌ ¡ERROR!");
            System.out.println("Detalle del error: " + e.getMessage());
            System.out.println("Por favor, revise la configuración en 'application.properties' y asegúrese de que el servidor de base de datos esté en ejecución.");
        }
        return false;
    }
}
