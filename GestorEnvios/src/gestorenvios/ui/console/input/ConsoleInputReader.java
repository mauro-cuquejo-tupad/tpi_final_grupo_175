package gestorenvios.ui.console.input;

import gestorenvios.ui.console.utils.ConsoleUtils;

import java.util.Scanner;

/***
 * Implementación de InputReader para leer entradas desde la consola.
 */
public class ConsoleInputReader implements InputReader {
    private final Scanner scanner;

    /***
     * Constructor que recibe un Scanner para leer entradas.
     * @param scanner Scanner para leer entradas desde la consola.
     */
    public ConsoleInputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /***
     * Lee la siguiente línea de entrada desde la consola.
     * @return La línea leída como una cadena.
     */
    @Override
    public String nextLine() {
        return scanner.nextLine();
    }

    /***
     * Muestra un mensaje y lee la entrada del usuario.
     * @param mensaje Mensaje a mostrar al usuario.
     * @return La entrada del usuario como una cadena.
     */
    @Override
    public String prompt(String mensaje) {
        ConsoleUtils.imprimirInfo(mensaje);
        return scanner.nextLine().trim();
    }

    /***
     * Lee una cadena obligatoria desde la consola.
     * @param mensaje Mensaje a mostrar al usuario.
     * @param param Nombre del parámetro para mensajes de error.
     * @return La cadena ingresada por el usuario.
     */
    @Override
    public String leerStringObligatorio(String mensaje, String param) {
        while (true) {
            ConsoleUtils.imprimirInfo(mensaje);
            String valorIngresado = scanner.nextLine().trim();
            if (!valorIngresado.isEmpty()) {
                return valorIngresado;
            }
            ConsoleUtils.imprimirError("Error: " + param + " no puede estar vacío.");
        }
    }

    /***
     * Lee un valor double desde la consola.
     * @param mensaje Mensaje a mostrar al usuario.
     * @return El valor double ingresado por el usuario.
     */
    @Override
    public double leerDouble(String mensaje) {
        while (true) {
            try {
                ConsoleUtils.imprimirInfo(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException _) {
                ConsoleUtils.imprimirError("Error: Ingrese un monto válido.");
            }
        }
    }

    /***
     * Lee un valor Long desde la consola.
     * @param mensaje Mensaje a mostrar al usuario.
     * @return El valor Long ingresado por el usuario.
     */
    @Override
    public Long leerLong(String mensaje) {
        while (true) {
            try {
                ConsoleUtils.imprimirInfo(mensaje);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException _) {
                ConsoleUtils.imprimirError("Error: Ingrese un número válido.");
            }
        }
    }

    /***
     * Lee un número de pedido con formato específico desde la consola.
     * @param mensaje Mensaje a mostrar al usuario.
     * @return El número de pedido ingresado por el usuario.
     */
    @Override
    public String leerNumeroPedido(String mensaje) {
        while (true) {
            ConsoleUtils.imprimirInfo(mensaje);
            String entrada = scanner.nextLine().trim();
            if (entrada.matches("PED-\\d{8}") || entrada.equalsIgnoreCase("q")) return entrada;
            ConsoleUtils.imprimirError("Error: Formato inválido. Use PED-XXXXXXXX.");
        }
    }

    /***
     * Muestra las opciones de un enum al usuario.
     * @param valores Array de valores del enum.
     */
    @Override
    public void mostrarOpcionesEnum(Object[] valores) {
        for (int i = 0; i < valores.length; i++) {
            ConsoleUtils.imprimirInfo((i + 1) + ". " + valores[i]);
        }
    }

    /***
     * Lee una opción de un enum desde la consola.
     * @param maxOpcion Número máximo de opciones disponibles.
     * @return La opción seleccionada por el usuario.
     */
    @Override
    public int leerOpcionEnum(int maxOpcion) {
        while (true) {
            try {
                ConsoleUtils.imprimirInfo("Opción: ");
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                if (opcion >= 1 && opcion <= maxOpcion) {
                    return opcion;
                }
                ConsoleUtils.imprimirError("Opción fuera de rango.");
            } catch (NumberFormatException _) {
                ConsoleUtils.imprimirError("Error: Ingrese un número válido.");
            }
        }
    }

    /***
     * Cierra el Scanner asociado.
     */
    @Override
    public void close() {
        this.scanner.close();
    }
}
