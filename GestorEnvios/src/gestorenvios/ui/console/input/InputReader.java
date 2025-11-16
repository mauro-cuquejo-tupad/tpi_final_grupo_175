package gestorenvios.ui.console.input;

public interface InputReader {
    String nextLine();

    String prompt(String mensaje);

    double leerDouble(String mensaje);

    Long leerLong(String mensaje);

    String leerNumeroPedido(String mensaje);

    void mostrarOpcionesEnum(Object[] valores);

    int leerOpcionEnum(int maxOpcion);

    void close();
}
