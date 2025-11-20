package gestorenvios.ui.console.utils;

import gestorenvios.ui.console.input.ConsoleInputReader;

import java.util.Scanner;

/***
 * Clase de utilidades para la consola.
 * Proporciona métodos estáticos para imprimir mensajes con formato,
 * limpiar la pantalla y manejar entradas de usuario.
 */
public class ConsoleUtils {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String PURPLE = "\u001B[35m";

    private ConsoleUtils() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Limpia la pantalla de la consola.
     * Utiliza secuencias de escape ANSI para mover el cursor a la esquina superior
     * izquierda y borrar la pantalla.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /***
     * Pausa la ejecución y espera a que el usuario presione Enter para continuar.
     */
    public static void enterParaContinuar() {
        imprimirInfo("\nPresione Enter para continuar...");
        new ConsoleInputReader(new Scanner(System.in)).nextLine();
    }

    /***
     * Formatea y muestra una opción de menú en la consola.
     *
     * @param numero      El número de la opción.
     * @param descripcion La descripción de la opción.
     */
    public static void formatearOpcion(Integer numero, String descripcion) {
        String num = numero.toString().length() == 1 ? " " + numero : numero.toString();
        System.out.printf(BLUE + " %s  -" + RESET + " %s\n", num, descripcion);
    }

    /***
     * Imprime un mensaje de error en rojo.
     *
     * @param mensaje El mensaje de error a imprimir.
     */
    public static void imprimirError(String mensaje) {
        System.out.println(RED + mensaje + RESET);
    }

    /***
     * Imprime un mensaje de advertencia en amarillo.
     *
     * @param mensaje El mensaje de advertencia a imprimir.
     */
    public static void imprimirAdvertencia(String mensaje) {
        System.out.println(YELLOW + mensaje + RESET);
    }

    /***
     * Imprime un mensaje informativo en cian.
     *
     * @param mensaje El mensaje informativo a imprimir.
     */
    public static void imprimirInfo(String mensaje) {
        System.out.println(CYAN + mensaje + RESET);
    }

    /***
     * Imprime un encabezado de divisores en violeta.
     *
     * @param mensaje El mensaje del encabezado a imprimir.
     */
    public static void imprimirDivisores(String mensaje) {
        System.out.println(PURPLE + mensaje + " " + "=".repeat(50 - mensaje.length()) + RESET);
    }

    /***
     * Imprime un mensaje de éxito en verde.
     *
     * @param mensaje El mensaje de éxito a imprimir.
     */
    public static void imprimirMensaje(String mensaje) {
        System.out.println(GREEN + mensaje + RESET);
    }

    /***
     * Imprime una línea vacía en la consola.
     */
    public static void imprimirLineaVacia() {
        System.out.println();
    }

    /***
     * Rellena una cadena a la derecha con espacios hasta alcanzar una longitud específica.
     *
     * @param s La cadena original.
     * @param n La longitud deseada.
     * @return La cadena rellenada a la derecha.
     */
    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
