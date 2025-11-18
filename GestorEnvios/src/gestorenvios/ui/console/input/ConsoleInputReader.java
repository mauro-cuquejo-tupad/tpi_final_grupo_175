package gestorenvios.ui.console.input;

import gestorenvios.ui.console.utils.ConsoleUtils;

import java.util.Scanner;

public class ConsoleInputReader implements InputReader {
    private final Scanner scanner;

    public ConsoleInputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }

    @Override
    public String prompt(String mensaje) {
        ConsoleUtils.imprimirInfo(mensaje);
        return scanner.nextLine().trim();
    }

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

    @Override
    public String leerNumeroPedido(String mensaje) {
        while (true) {
            ConsoleUtils.imprimirInfo(mensaje);
            String entrada = scanner.nextLine().trim();
            if (entrada.matches("PED-\\d{8}") || entrada.equalsIgnoreCase("q")) return entrada;
            ConsoleUtils.imprimirError("Error: Formato inválido. Use PED-XXXXXXXX.");
        }
    }

    @Override
    public void mostrarOpcionesEnum(Object[] valores) {
        for (int i = 0; i < valores.length; i++) {
            ConsoleUtils.imprimirInfo((i + 1) + ". " + valores[i]);
        }
    }

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

    @Override
    public void close() {
        this.scanner.close();
    }
}
