package gestorenvios.ui.console.controllers;

import gestorenvios.entities.Pedido;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.ui.console.input.InputReader;
import gestorenvios.ui.console.output.PedidoPrinter;
import gestorenvios.ui.console.utils.Paginador;

import java.time.LocalDate;
import java.util.List;

public class PedidoConsoleController {

    private final GenericPedidosService<Pedido> pedidoService;
    private final InputReader input;

    public PedidoConsoleController(GenericPedidosService<Pedido> pedidoService, InputReader input) {
        this.pedidoService = pedidoService;
        this.input = input;
    }

    public void crear() {
        try {
            System.out.println("\n--- CREAR NUEVO PEDIDO ---");
            String cliente = input.prompt("Nombre del Cliente: ");
            double total = input.leerDouble("Total del pedido: ");

            Pedido pedido = new Pedido();
            pedido.setClienteNombre(cliente);
            pedido.setTotal(total);
            pedido.setFecha(LocalDate.now());
            pedido.setEstado(gestorenvios.entities.EstadoPedido.NUEVO);

            String numeroPedido = pedidoService.crear(pedido);
            System.out.println("✅ Pedido Tracking Nro: " + numeroPedido + " creado exitosamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al crear el pedido: " + e.getMessage());
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE PEDIDOS ---");
        try {
            Long total = pedidoService.obtenerCantidadTotalDePedidos();
            System.out.println("Total de pedidos registrados: " + total);

            Paginador<Pedido> paginador = new Paginador<>(50L, input);
            paginador.paginar(
                    (cantidad, pagina) -> {
                        try {
                            return pedidoService.buscarTodos(cantidad, pagina);
                        } catch (Exception e) {
                            System.out.println("❌ Error al obtener pedidos: " + e.getMessage());
                            return List.of();
                        }
                    },
                    lista -> lista.forEach(PedidoPrinter::mostrarResumen),
                    total
            );
        } catch (Exception e) {
            System.out.println("❌ Error al listar pedidos: " + e.getMessage());
        }
    }

    public void buscarPorNumero() {
        System.out.println("\n--- BUSCAR PEDIDO POR NUMERO ---");
        try {
            String numero = input.leerNumeroPedido("Ingrese Numero de pedido (PED-XXXXXXXX): ");
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numero);
            PedidoPrinter.mostrarResumen(pedido);
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por Numero: " + e.getMessage());
        }
    }

    public Pedido buscarPorTracking() {
        System.out.println("\n--- BUSCAR PEDIDO POR TRACKING ---");
        try {
            String tracking = input.prompt("Ingrese código de tracking: ");
            Pedido p = pedidoService.buscarPorNumeroTracking(tracking);
            PedidoPrinter.mostrarResumen(p);
            return p;
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por Tracking: " + e.getMessage());
            return null;
        }
    }

    public Pedido buscarPorId() {
        System.out.println("\n--- BUSCAR PEDIDO POR ID ---");
        try {
            Long id = input.leerLong("Ingrese ID de pedido: ");
            Pedido p = pedidoService.buscarPorId(id);
            PedidoPrinter.mostrarResumen(p);
            return p;
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por ID: " + e.getMessage());
            return null;
        }
    }

    public void buscarPorCliente() {
        System.out.println("\n--- BUSCAR PEDIDO POR CLIENTE ---");
        try {
            String clienteNombre = input.prompt("Ingrese Cliente de pedido: ");
            Long total = pedidoService.obtenerCantidadTotalDePedidosPorCliente(clienteNombre);
            System.out.println("Total de pedidos registrados: " + total);

            Paginador<Pedido> paginador = new Paginador<>(50L, input);
            paginador.paginar(
                    (cantidad, pagina) -> {
                        try {
                            return pedidoService.buscarPorCliente(clienteNombre, cantidad, pagina);
                        } catch (Exception e) {
                            System.out.println("❌ Error al obtener pedidos: " + e.getMessage());
                            return List.of();
                        }
                    },
                    lista -> lista.forEach(PedidoPrinter::mostrarResumen),
                    total
            );
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por Cliente: " + e.getMessage());
        }
    }

    //ELIMINAR PEDIDOS
    public void eliminarPedidoPorNumero() {
        System.out.println("\n--- ELIMINAR PEDIDO ---");
        try {
            String numero = input.leerNumeroPedido("Ingrese Numero del pedido a eliminar (PED-XXXXXXXX): ");

            // Llamada al Service (Soft Delete)
            pedidoService.eliminarPorNumero(numero);

            System.out.println("✅ Pedido eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    public void eliminarEnvioDePedido() {
        System.out.println("\n--- ELIMINAR ENVIO DE PEDIDO ---");
        try {
            String numero = input.leerNumeroPedido("Ingrese Numero del pedido para eliminar su envío (PED-XXXXXXXX): ");

            // Llamada al Service (Soft Delete)
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numero);
            if (pedido == null) {
                System.out.println("❌ Pedido no encontrado.");
                return;
            }
            pedidoService.eliminarEnvioDePedido(pedido.getId());

            System.out.println("✅ Envío eliminado del pedido correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar el envío del pedido: " + e.getMessage());
        }
    }

    public void eliminarPedidoPorId() {
        System.out.println("\n--- ELIMINAR PEDIDO ---");
        try {
            Long id = input.leerLong("Ingrese ID del pedido a eliminar: ");

            // Llamada al Service (Soft Delete)
            pedidoService.eliminar(id);

            System.out.println("✅ Pedido eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }
}
