package gestorenvios.ui.console;

import gestorenvios.dao.EnvioDAO;
import gestorenvios.dao.PedidoDAO;
import gestorenvios.entities.Envio;
import gestorenvios.entities.Pedido;
import gestorenvios.services.EnvioServiceImpl;
import gestorenvios.services.GenericEnviosService;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.services.PedidoServiceImpl;
import gestorenvios.ui.console.controllers.MenuHandler;
import gestorenvios.ui.console.input.ConsoleInputReader;
import gestorenvios.ui.console.input.InputReader;
import gestorenvios.ui.console.input.MenuDisplay;

import java.util.Scanner;

public class AppMenu {

    /**
     * Scanner único compartido por toda la aplicación.
     */
    private final InputReader input;

    /**
     * Handler que ejecuta las operaciones del menú. Contiene toda la lógica de
     * interacción con el usuario.
     */
    private final MenuHandler menuHandler;

    /**
     * Flag que controla el loop principal del menú. Se setea a false cuando el
     * usuario selecciona "0 - Salir".
     */
    private boolean running;

    /**
     * Constructor que inicializa la aplicación.
     * <p>
     * Flujo de inicialización: 1. Crea Scanner único para toda la aplicación 2.
     * Crea cadena de dependencias (DAOs → Services) mediante
     * createPedidoService() 3. Crea MenuHandler con Scanner y PedidoService
     * 4. Setea running=true para iniciar el loop
     * <p>
     * Patrón de inyección de dependencias (DI) manual: - EnviosDAO (sin
     * dependencias) - PedidosDAO (depende de EnviosDAO) -
     * EnvioServiceImpl (depende de EnviosDAO) - PedidoServiceImpl
     * (depende de PedidosDAO y EnvioServiceImpl) - MenuHandler (depende de
     * Scanner y PedidoServiceImpl)
     * <p>
     * Esta inicialización garantiza que todas las dependencias estén
     * correctamente conectadas.
     */
    public AppMenu() {

        this.input = new ConsoleInputReader(new Scanner(System.in));

        GenericPedidosService<Pedido> pedidoService = createPedidoService();
        GenericEnviosService<Envio, Pedido> enviosService = crearEnvioService(pedidoService);

        this.menuHandler = new MenuHandler(pedidoService, enviosService, input);
        this.running = true;
    }

    private GenericEnviosService<Envio, Pedido> crearEnvioService(GenericPedidosService<Pedido> pedidoService) {
        return new EnvioServiceImpl(new EnvioDAO(), pedidoService);
    }

    /**
     * Punto de entrada de la aplicación Java. Crea instancia de AppMenu y
     * ejecuta el menú principal.
     *
     */
    static void main() {
        AppMenu app = new AppMenu();
        app.run();
    }

    /**
     * Loop principal del menú.
     * <p>
     * Flujo: 1. Mientras running==true:
     * a. Muestra menú conMenuDisplay.mostrarMenuPrincipal()
     * b. Lee opción del usuario (scanner.nextLine())
     * c. Convierte a int (puede lanzar NumberFormatException)
     * d. Procesa opción con processOption()
     * <p>
     * 2. Si el usuario ingresa texto no numérico: Muestra mensaje de error y continúa 3.
     * Cuando running==false (opción 0): Sale del loop y cierra Scanner
     * <p>
     * Manejo de errores: - NumberFormatException: Captura entrada no numérica
     * (ej: "abc") - Muestra mensaje amigable y NO termina la aplicación - El
     * usuario puede volver a intentar
     * <p>
     * IMPORTANTE: El Scanner se cierra al salir del loop. Cerrar
     * Scanner(System.in) cierra System.in para toda la aplicación.
     */
    public void run() {
        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(input.nextLine());
                processOption(opcion);
            } catch (NumberFormatException _) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }
        input.close();
    }

    /**
     * Procesa la opción seleccionada por el usuario y delega a MenuHandler.
     * <p>
     * Switch expression (Java 14+) con operador arrow (->): - Más conciso que
     * switch tradicional - No requiere break (cada caso es independiente) -
     * Permite bloques con {} para múltiples statements
     * <p>
     * Mapeo de opciones (corresponde a MenuDisplay): 1 → Crear pedido (con
     * enví opcional) 2 → Listar pedidos (todos o filtradas) 3 →
     * Actualizar pedido 4 → Eliminar pedido (soft delete) 5 → Crear envio
     * independiente 6 → Listar envios 7 → Actualizar envio por ID
     * (afecta a todas los pedidos que lo comparten) 8 → Eliminar envio por
     * ID (PELIGROSO - puede dejar FKs huérfanas) 9 → ActualiEnvios de
     * una pedido (afecta a todos las pedidos que lo comparten) 10 → Eliminar
     * envio de un pedido (SEGURO - actualiza FK primero) 0 → Salir (setea
     * running=false para terminar el loop)
     * <p>
     * Opción inválida: Muestra mensaje y continúa el loop.
     * <p>
     * IMPORTANTE: Todas las excepciones de MenuHandler se capturan dentro de
     * los métodos. processOption() NO propaga excepciones al caller (run()).
     *
     * @param opcion Número de opción ingresado por el usuario
     */

    private void processOption(int opcion) {
        switch (opcion) {
            //pedidos
            case 1 -> menuHandler.crearPedido();
            case 2 -> menuHandler.listarPedidos();
            case 3 -> menuHandler.buscarPedidoPorNumero();
            case 4 -> menuHandler.buscarPedidoPorTracking();
            case 5 -> menuHandler.buscarPedidoPorId();
            case 6 -> menuHandler.buscarPedidoPorCliente();
            case 7 -> menuHandler.eliminarPedidoPorNumero();
            case 8 -> menuHandler.eliminarPedidoPorId();

            //envios
            case 9 -> menuHandler.crearEnvio();
            case 10 -> menuHandler.listarEnvios();
            case 11 -> menuHandler.buscarEnvioPorTracking();
            case 12 -> menuHandler.buscarEnvioPorNumeroPedido();
            case 13 -> menuHandler.buscarEnvioPorId();
            case 14 -> menuHandler.actualizarEstadoEnvioPorTracking();
            case 15 -> menuHandler.actualizarEstadoEnvioPorNumeroPedido();
            case 16 -> menuHandler.actualizarEstadoEnvioPorId();
            case 17 -> menuHandler.eliminarEnvioPorTracking();
            case 18 -> menuHandler.eliminarEnvioPorNumeroPedido();
            case 19 -> menuHandler.eliminarEnvioPorId();

            //salir
            case 0 -> {
                System.out.println("Saliendo...");
                running = false;
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    /**
     * Factory method que crea la cadena de dependencias de servicios.
     * Implementa inyección de dependencias manual.
     * <p>
     * Orden de creación (bottom-up desde la capa más baja): 1. EnviosDAO:
     * Sin dependencias, acceso directo a BD 2. PedidoDAO: Depende de
     * EnviosDAO (inyectado en constructor) 3. EnvioServiceImpl: Depende
     * de EnviosDAO 4. PedidoServiceImpl: Depende de PedidoDAO y
     * EnvioServiceImpl
     * <p>
     * Arquitectura resultante (4 capas): Main (AppMenu, MenuHandler) ↓ Service
     * (PedidoServiceImpl, EnvioServiceImpl) ↓ DAO (PedidoDAO,
     * EnviosDAO) ↓ Models (Pedido, Envios, Base)
     * <p>
     * ¿Por qué PedidoDAO necesita EnviosDAO? - Actualmente NO lo usa
     * (inyección preparada para futuras operaciones) - Podría usarse para
     * operaciones transaccionales coordinadas
     * <p>
     * ¿Por qué PedidoService necesita EnvioService? - Para
     * insertar/actualizar envios al crear/actualizar pedidos - Para
     * eliminar envios de forma segura (eliminarEnviosDePedido)
     * <p>
     * Patrón: Factory Method para construcción de dependencias
     *
     * @return PedidoServiceImpl completamente inicializado con todas sus
     * dependencias
     */

    private GenericPedidosService<Pedido> createPedidoService() {
        PedidoDAO pedidoDAO = new PedidoDAO();
        return new PedidoServiceImpl(pedidoDAO);
    }
}
