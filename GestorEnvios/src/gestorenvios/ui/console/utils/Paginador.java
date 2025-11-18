package gestorenvios.ui.console.utils;

import gestorenvios.ui.console.input.InputReader;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Paginador<T> {

    private final long pageSize;
    private final InputReader input;

    public Paginador(long pageSize, InputReader input) {
        this.pageSize = pageSize;
        this.input = input;
    }

    /**
     * fetcher.apply(pageSize, page) debe devolver la lista de resultados.
     * consumer.accept(lista) procesará/mostrará cada página.
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
