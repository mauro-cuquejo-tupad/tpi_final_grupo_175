package gestorenvios.ui.console.utils;

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
}
