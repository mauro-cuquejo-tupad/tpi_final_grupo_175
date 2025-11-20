package gestorenvios.ui.console.utils;

import gestorenvios.ui.console.input.InputReader;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/***
 * Clase genérica para paginar resultados en la consola.
 * @param <T> Tipo de los elementos a paginar.
 */
public class Paginador<T> {

    private final long pageSize;
    private final InputReader input;

    /***
     * Constructor del paginador.
     * @param pageSize Tamaño de cada página.
     * @param input Lector de entrada para capturar la interacción del usuario.
     */
    public Paginador(long pageSize, InputReader input) {
        this.pageSize = pageSize;
        this.input = input;
    }

    /***
     * Método para paginar resultados.
     * @param fetcher Función que recibe el tamaño de página y el número de página, y devuelve una lista de elementos.
     * @param consumer Consumidor que procesa la lista de elementos obtenida.
     * @param totalCount Cantidad total de elementos disponibles.
     */
    public void paginar(BiFunction<Long, Long, List<T>> fetcher,
                        Consumer<List<T>> consumer,
                        long totalCount) {
        if (totalCount == 0) {
            ConsoleUtils.imprimirInfo("No hay registros.");
            return;
        }

        long page = 1L;

        boolean continuar = true;
        while (continuar) {
            List<T> lista = fetcher.apply(pageSize, page);

            if (lista != null && !lista.isEmpty()) {
                consumer.accept(lista);

                if (totalCount > pageSize * page) {
                    ConsoleUtils.imprimirInfo("Presione Enter para ver más o 'q' para salir: ");
                    String in = input.nextLine().trim();
                    continuar = !"q".equalsIgnoreCase(in);
                    page++;
                } else {
                    continuar = false;
                }
            } else {
                continuar = false;
            }
        }
    }
}
