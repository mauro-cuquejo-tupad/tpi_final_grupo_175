package gestorenvios.ui;

import java.time.LocalDate;

public interface InputReader {
    String nextLine();
    String prompt(String mensaje);
    double readDouble(String mensaje);
    Long readLong(String mensaje);
    LocalDate readDate(String mensaje);
    String readPedidoNumero(String mensaje);

    void mostrarOpcionesEnum(Object[] valores);

    int leerOpcionEnum(int maxOpcion);

    void close();
}
