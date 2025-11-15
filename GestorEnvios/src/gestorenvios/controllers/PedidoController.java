package gestorenvios.controllers;

import gestorenvios.entities.EstadoPedido;
import gestorenvios.entities.Pedido;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.ui.InputReader;
import gestorenvios.ui.Paginador;
import gestorenvios.ui.PedidoPrinter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PedidoController {

    private final GenericPedidosService<Pedido> pedidoService;
    private final InputReader input;

    public PedidoController(GenericPedidosService<Pedido> pedidoService, InputReader input) {
        this.pedidoService = pedidoService;
        this.input = input;
    }

    public void crear() {
        try {
            System.out.println("\n--- CREAR NUEVO PEDIDO ---");
            String cliente = input.prompt("Nombre del Cliente: ");
            double total = input.readDouble("Total del pedido: ");

            Pedido pedido = new Pedido();
            pedido.setClienteNombre(cliente);
            pedido.setTotal(total);
            pedido.setFecha(LocalDate.now());
            pedido.setEstado(gestorenvios.entities.EstadoPedido.NUEVO);

            pedidoService.crear(pedido);
            System.out.println("✅ Pedido creado exitosamente.");
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
                    (pageSize, page) -> {
                        try {
                            return pedidoService.buscarTodos(pageSize, page);
                        } catch (Exception e) {
                            System.out.println("❌ Error al obtener envíos: " + e.getMessage());
                            return List.of();
                        }
                    },
                    lista -> lista.forEach(PedidoPrinter::printSummary),
                    total
            );
        } catch (Exception e) {
            System.out.println("❌ Error al listar pedidos: " + e.getMessage());
        }
    }

    public Pedido buscarPorNumero() {
        System.out.println("\n--- BUSCAR PEDIDO POR NUMERO ---");
        try {
            String numero = input.readPedidoNumero("Ingrese Numero de pedido (PED-XXXXXXXX): ");
            Pedido p = pedidoService.buscarPorNumeroPedido(numero);
            PedidoPrinter.printSummary(p);
            return p;
        } catch (Exception e) {
            System.out.println("❌ Error al buscar: " + e.getMessage());
            return null;
        }
    }

    public Pedido buscarPorTracking() {
        System.out.println("\n--- BUSCAR PEDIDO POR TRACKING ---");
        try {
            String tracking = input.prompt("Ingrese código de tracking: ");
            Pedido p = pedidoService.buscarPorNumeroTracking(tracking);
            PedidoPrinter.printSummary(p);
            return p;
        } catch (Exception e) {
            System.out.println("❌ Error al buscar: " + e.getMessage());
            return null;
        }
    }

    public Pedido buscarPorId() {
        System.out.println("\n--- BUSCAR PEDIDO POR ID ---");
        try {
            Long id = input.readLong("Ingrese ID de pedido: ");
            Pedido p = pedidoService.buscarPorId(id);
            PedidoPrinter.printSummary(p);
            return p;
        } catch (Exception e) {
            System.out.println("❌ Error al buscar: " + e.getMessage());
            return null;
        }
    }



    //ACTUALIZAR PEDIDOS
    public void actualizarPedidoPorNumero() {
        System.out.println("\n--- ACTUALIZAR PEDIDO ---");
        try {
            String numero = input.readPedidoNumero("Ingrese Numero de pedido a modificar (PED-XXXXXXXX): ");

            // buscamos si existe
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numero);
            if (pedido == null) {
                System.out.println("Pedido no encontrado.");
                return;
            }

            System.out.println("Editando pedido: " + pedido.getNumero());
            System.out.println("(Presione Enter para mantener el valor actual)");

            // Lógica para editar solo si el usuario escribe algo
            System.out.print("Nuevo Cliente (" + pedido.getClienteNombre() + "): ");
            String nuevoCliente = input.nextLine().trim();
            if (!nuevoCliente.isEmpty()) {
                pedido.setClienteNombre(nuevoCliente);
            }

            // Actualizar TOTAL
            System.out.print("Nuevo Total (" + pedido.getTotal() + "): ");
            String totalStr = input.nextLine().trim();
            if (!totalStr.isEmpty()) {
                try {
                    pedido.setTotal(Double.parseDouble(totalStr));
                } catch (NumberFormatException _) {
                    System.out.println("⚠ Formato de número incorrecto. Se mantiene el valor anterior.");
                }
            }

            // Actualizar FECHA
            System.out.print("Nueva Fecha (" + pedido.getFecha() + ") [AAAA-MM-DD]: ");
            String fechaStr = input.nextLine().trim();
            if (!fechaStr.isEmpty()) {
                try {
                    pedido.setFecha(LocalDate.parse(fechaStr));
                } catch (DateTimeParseException _) {
                    System.out.println("⚠ Formato de fecha incorrecto. Se mantiene la fecha anterior.");
                }
            }

            // Actualizar ESTADO (Enum)
            System.out.println("Estado actual: " + pedido.getEstado());
            System.out.print("¿Desea cambiar el estado? (s/n): ");
            if (input.nextLine().trim().equalsIgnoreCase("s")) {
                pedido.setEstado(elegirEstadoPedido()); // Reutilizamos método selector
            }

            // Llamada al Service para guardar cambios
            pedidoService.actualizar(pedido);
            System.out.println("✅ Pedido actualizado.");

        } catch (Exception e) {
            System.out.println("❌ Error al actualizar: " + e.getMessage());
        }
    }

    public void actualizarPedidoPorId() {
        System.out.println("\n--- ACTUALIZAR PEDIDO ---");
        try {
            Long id = input.readLong("Ingrese Id de pedido a modificar: ");

            // buscamos si existe
            Pedido pedido = pedidoService.buscarPorId(id);
            if (pedido == null) {
                System.out.println("Pedido no encontrado.");
                return;
            }

            System.out.println("Editando pedido: " + pedido.getNumero());
            System.out.println("(Presione Enter para mantener el valor actual)");

            // Lógica para editar solo si el usuario escribe algo
            System.out.print("Nuevo Cliente (" + pedido.getClienteNombre() + "): ");
            String nuevoCliente = input.nextLine().trim();
            if (!nuevoCliente.isEmpty()) {
                pedido.setClienteNombre(nuevoCliente);
            }

            // Actualizar TOTAL
            System.out.print("Nuevo Total (" + pedido.getTotal() + "): ");
            String totalStr = input.nextLine().trim();
            if (!totalStr.isEmpty()) {
                try {
                    pedido.setTotal(Double.parseDouble(totalStr));
                } catch (NumberFormatException _) {
                    System.out.println("⚠ Formato de número incorrecto. Se mantiene el valor anterior.");
                }
            }

            // Actualizar FECHA
            System.out.print("Nueva Fecha (" + pedido.getFecha() + ") [AAAA-MM-DD]: ");
            String fechaStr = input.nextLine().trim();
            if (!fechaStr.isEmpty()) {
                try {
                    pedido.setFecha(LocalDate.parse(fechaStr));
                } catch (DateTimeParseException _) {
                    System.out.println("⚠ Formato de fecha incorrecto. Se mantiene la fecha anterior.");
                }
            }

            // Actualizar ESTADO (Enum)
            System.out.println("Estado actual: " + pedido.getEstado());
            System.out.print("¿Desea cambiar el estado? (s/n): ");
            if (input.nextLine().trim().equalsIgnoreCase("s")) {
                pedido.setEstado(elegirEstadoPedido()); // Reutilizamos método selector
            }

            // Llamada al Service para guardar cambios
            pedidoService.actualizar(pedido);
            System.out.println("✅ Pedido actualizado.");

        } catch (Exception e) {
            System.out.println("❌ Error al actualizar: " + e.getMessage());
        }
    }

    //ELIMINAR PEDIDOS
    public void eliminarPedidoPorNumero() {
        System.out.println("\n--- ELIMINAR PEDIDO ---");
        try {
            String numero = input.readPedidoNumero("Ingrese Numero del pedido a eliminar (PED-XXXXXXXX): ");

            // Llamada al Service (Soft Delete)
            pedidoService.eliminarPorNumero(numero);

            System.out.println("✅ Pedido eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    public void eliminarPedidoPorId() {
        System.out.println("\n--- ELIMINAR PEDIDO ---");
        try {
            Long id = input.readLong("Ingrese ID del pedido a eliminar: ");

            // Llamada al Service (Soft Delete)
            pedidoService.eliminar(id);

            System.out.println("✅ Pedido eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    private EstadoPedido elegirEstadoPedido() {
        System.out.println("Seleccione Estado del Pedido:");
        EstadoPedido[] valores = EstadoPedido.values();
        input.mostrarOpcionesEnum(valores);
        return valores[input.leerOpcionEnum(valores.length) - 1];
    }
}
