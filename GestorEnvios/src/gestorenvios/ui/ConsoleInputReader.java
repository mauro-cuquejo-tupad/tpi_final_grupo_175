// java
package gestorenvios.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    @Override
    public double readDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un monto válido.");
            }
        }
    }

    @Override
    public Long readLong(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número válido.");
            }
        }
    }

    @Override
    public LocalDate readDate(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String fechaStr = scanner.nextLine().trim();
                if (fechaStr.isEmpty()) {
                    return null;
                }
                return LocalDate.parse(fechaStr);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato inválido. Use AAAA-MM-DD.");
            }
        }
    }

    @Override
    public String readPedidoNumero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (entrada.matches("PED-\\d{8}")) return entrada;
            System.out.println("Error: Formato inválido. Use PED-XXXXXXXX.");
        }
    }

    @Override
    public void mostrarOpcionesEnum(Object[] valores) {
        for (int i = 0; i < valores.length; i++) {
            System.out.println((i + 1) + ". " + valores[i]);
        }
    }

    @Override
    public int leerOpcionEnum(int maxOpcion) {
        while (true) {
            try {
                System.out.print("Opción: ");
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                if (opcion >= 1 && opcion <= maxOpcion) {
                    return opcion;
                }
                System.out.println("Opción fuera de rango.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número válido.");
            }
        }
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
