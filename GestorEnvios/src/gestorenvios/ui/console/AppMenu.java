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
import gestorenvios.ui.console.output.PantallaBienvenida;
import gestorenvios.ui.console.utils.ConsoleUtils;

import java.util.Scanner;

/***
 * Clase principal que maneja el menú de la aplicación de gestión de envíos y
 * pedidos.
 * <p>
 * Responsabilidades: - Inicializar dependencias (DAOs, Services, Handlers) -
 * Ejecutar loop principal del menú - Procesar opciones seleccionadas por el
 * usuario
 * <p>
 * Patrón de diseño: - Inyección de dependencias (DI) manual mediante factory
 * methods - Separación clara entre capa de presentación (AppMenu,
 * MenuDisplay), lógica de negocio (MenuHandler, Services) y acceso a datos
 * (DAOs)
 */
public class AppMenu {

    /***
     * Scanner único compartido por toda la aplicación.
     */
    private final InputReader input;

    /***
     * Handler que ejecuta las operaciones del menú. Contiene toda la lógica de
     * interacción con el usuario.
     */
    private final MenuHandler menuHandler;

    /***
     * Flag que controla el loop principal del menú. Se setea a false cuando el
     * usuario selecciona "0 - Salir".
     */
    private boolean running;

    private final boolean conexionExitosa;

    /***
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
        this.conexionExitosa = PantallaBienvenida.mostrar(input);
    }

    /***
     * Crea el servicio de envíos con la dependencia del servicio de pedidos.
     *
     * @param pedidoService Servicio de pedidos a inyectar en el servicio de
     *                      envíos
     * @return Instancia de GenericEnviosService<Envio, Pedido>
     */
    private GenericEnviosService<Envio, Pedido> crearEnvioService(GenericPedidosService<Pedido> pedidoService) {
        return new EnvioServiceImpl(new EnvioDAO(), pedidoService);
    }

    /***
     * Punto de entrada de la aplicación Java. Crea instancia de AppMenu y
     * ejecuta el menú principal.
     *
     */
    static void main() {
        AppMenu app = new AppMenu();
        app.run();
    }

    /***
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
        if (!conexionExitosa) {
            ConsoleUtils.imprimirInfo("Saliendo...");
            return;
        }

        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(input.nextLine());
                processOption(opcion);
            } catch (NumberFormatException _) {
                ConsoleUtils.imprimirError("Entrada inválida. Por favor, ingrese un número.");
            }
        }
        input.close();
    }

    /***
     * Procesa la opción seleccionada por el usuario.
     * <p>
     * Flujo: 1. Recibe opción como int 2. Usa switch-case para mapear opción a
     * método de MenuHandler 3. Llama al método correspondiente 4. Si opción es
     * 0, setea running=false para salir del loop 5. Si opción no es válida,
     * muestra mensaje de error
     *
     * @param opcion Opción seleccionada por el usuario
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
            case 7 -> menuHandler.actualizarPedidoPorNumero();
            case 8 -> menuHandler.eliminarPedidoPorNumero();
            case 9 -> menuHandler.eliminarPedidoPorId();

            //envios
            case 10 -> menuHandler.crearEnvio();
            case 11 -> menuHandler.listarEnvios();
            case 12 -> menuHandler.buscarEnvioPorTracking();
            case 13 -> menuHandler.buscarEnvioPorNumeroPedido();
            case 14 -> menuHandler.buscarEnvioPorId();
            case 15 -> menuHandler.actualizarEstadoEnvioPorTracking();
            case 16 -> menuHandler.actualizarEstadoEnvioPorNumeroPedido();
            case 17 -> menuHandler.actualizarEstadoEnvioPorId();
            case 18 -> menuHandler.eliminarEnvioPorTracking();
            case 19 -> menuHandler.eliminarEnvioPorNumeroPedido();
            case 20 -> menuHandler.eliminarEnvioPorId();

            //salir
            case 0 -> {
                ConsoleUtils.imprimirInfo("Saliendo...");
                running = false;
            }
            default -> ConsoleUtils.imprimirError("Opción no válida.");
        }
    }

    /***
     * Crea el servicio de pedidos con su dependencia del DAO.
     *
     * @return Instancia de GenericPedidosService<Pedido>
     */
    private GenericPedidosService<Pedido> createPedidoService() {
        PedidoDAO pedidoDAO = new PedidoDAO();
        return new PedidoServiceImpl(pedidoDAO);
    }
}
