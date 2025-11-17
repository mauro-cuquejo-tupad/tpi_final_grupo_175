package gestorenvios.ui.console.controllers;

import gestorenvios.entities.EstadoPedido;
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

    public void buscarPorTracking() {
        System.out.println("\n--- BUSCAR PEDIDO POR TRACKING ---");
        try {
            String tracking = input.prompt("Ingrese código de tracking: ");
            Pedido p = pedidoService.buscarPorNumeroTracking(tracking);
            PedidoPrinter.mostrarResumen(p);
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por Tracking: " + e.getMessage());
        }
    }

    public void buscarPorId() {
        System.out.println("\n--- BUSCAR PEDIDO POR ID ---");
        try {
            Long id = input.leerLong("Ingrese ID de pedido: ");
            Pedido p = pedidoService.buscarPorId(id);
            PedidoPrinter.mostrarResumen(p);
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por ID: " + e.getMessage());
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

            Pedido pedido = pedidoService.buscarPorNumeroPedido(numero);
            eliminar(pedido);
            if (pedido == null) {
                return;
            }

            System.out.println("✅ Pedido eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    public void eliminarPedidoPorId() {
        System.out.println("\n--- ELIMINAR PEDIDO ---");
        try {
            Long id = input.leerLong("Ingrese ID del pedido a eliminar: ");

            Pedido pedido = pedidoService.buscarPorId(id);
            eliminar(pedido);
            if (pedido == null) {
                return;
            }
            System.out.println("✅ Pedido eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    private void eliminar(Pedido pedido) {
        if (pedido == null) {
            System.out.println("❌ El pedido no existe.");
            return;
        } else if(pedido.getEstado() == EstadoPedido.ENVIADO) {
            System.out.println("❌ No se puede eliminar un pedido que ya ha sido enviado.");
            return;
        } else if (pedido.getEnvio() != null) {
            System.out.println("❌ El pedido tiene un envío asociado. Elimine primero el pedido.");
            return;
        }

        System.out.print("¿Está seguro que desea eliminar el pedido " + pedido.getNumero() + "? (s/n): ");
        if (input.nextLine().trim().equalsIgnoreCase("s")) {

            try {
                // Llamada real al Service
                pedidoService.eliminar(pedido);
                System.out.println("✅ Pedido eliminado.");
            } catch (Exception e) {
                System.out.println("❌ Error al eliminar pedido: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }
}
