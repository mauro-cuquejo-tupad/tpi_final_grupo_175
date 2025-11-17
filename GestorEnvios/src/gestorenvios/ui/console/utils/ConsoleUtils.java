package gestorenvios.ui.console.utils;

import gestorenvios.ui.console.input.ConsoleInputReader;

import java.util.Scanner;

public class ConsoleUtils {
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
        System.out.println("\nPresione Enter para continuar...");
        new ConsoleInputReader(new Scanner(System.in)).nextLine();
    }
}
