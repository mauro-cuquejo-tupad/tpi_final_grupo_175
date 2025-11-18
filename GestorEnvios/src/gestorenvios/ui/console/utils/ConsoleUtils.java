package gestorenvios.ui.console.utils;

import gestorenvios.ui.console.input.ConsoleInputReader;

import java.util.Scanner;

public class ConsoleUtils {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
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

    public static void enterParaContinuar() {
        imprimirInfo("\nPresione Enter para continuar...");
        new ConsoleInputReader(new Scanner(System.in)).nextLine();
    }

    public static void formatearOpcion(Integer numero, String descripcion) {
        String num = numero.toString().length() == 1 ? " " + numero : numero.toString();
        System.out.printf(BLUE + " %s  -" + RESET + " %s\n", num, descripcion);
    }

    public static void imprimirError(String mensaje) {
        System.out.println(RED + mensaje + RESET);
    }

    public static void imprimirAdvertencia(String mensaje) {
        System.out.println(YELLOW + mensaje + RESET);
    }

    public static void imprimirInfo(String mensaje) {
        System.out.println(CYAN + mensaje + RESET);
    }

    public static void imprimirDivisores(String mensaje) {
        System.out.println(PURPLE + mensaje + " " + "=".repeat(50 - mensaje.length()) + RESET);
    }

    public static void imprimirMensaje(String mensaje) {
        System.out.println(GREEN + mensaje + RESET);
    }

    public static void imprimirLineaVacia() {
        System.out.println();
    }
}
