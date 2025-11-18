package gestorenvios.ui.console.controllers;

import gestorenvios.entities.EstadoPedido;
import gestorenvios.entities.Pedido;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.ui.console.input.InputReader;
import gestorenvios.ui.console.output.PedidoPrinter;
import gestorenvios.ui.console.utils.ConsoleUtils;
import gestorenvios.ui.console.utils.Paginador;

import java.time.LocalDate;
import java.util.List;

public class PedidoConsoleController {

    public static final String OPERACION_CANCELADA_POR_EL_USUARIO = "❌ Operación cancelada por el usuario.";
    private final GenericPedidosService<Pedido> pedidoService;
    private final InputReader input;

    public PedidoConsoleController(GenericPedidosService<Pedido> pedidoService, InputReader input) {
        this.pedidoService = pedidoService;
        this.input = input;
    }

    public void crear() {
        try {
            ConsoleUtils.imprimirDivisores("CREAR NUEVO PEDIDO");
            String cliente = input.leerStringObligatorio("Nombre del Cliente: ", "nombre de cliente");
            double total = input.leerDouble("Total del pedido: ");

            Pedido pedido = new Pedido();
            pedido.setClienteNombre(cliente);
            pedido.setTotal(total);
            pedido.setFecha(LocalDate.now());
            pedido.setEstado(gestorenvios.entities.EstadoPedido.NUEVO);

            String numeroPedido = pedidoService.crear(pedido);
            ConsoleUtils.imprimirMensaje("✅ Pedido Tracking Nro: " + numeroPedido + " creado exitosamente.");
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al crear el pedido: " + e.getMessage());
        }
    }

    public void listar() {
        ConsoleUtils.imprimirDivisores("LISTA DE PEDIDOS");
        try {
            Long total = pedidoService.obtenerCantidadTotalDePedidos();
            ConsoleUtils.imprimirMensaje("Total de pedidos registrados: " + total);

            Paginador<Pedido> paginador = new Paginador<>(50L, input);
            paginador.paginar(
                    (cantidad, pagina) -> {
                        try {
                            return pedidoService.buscarTodos(cantidad, pagina);
                        } catch (Exception e) {
                            ConsoleUtils.imprimirError("❌ Error al obtener pedidos: " + e.getMessage());
                            return List.of();
                        }
                    },
                    lista -> {
                        ConsoleUtils.imprimirLineaVacia();
                        PedidoPrinter.mostrarCabecera();
                        lista.forEach(PedidoPrinter::mostrarDetalle);
                    },
                    total
            );
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al listar pedidos: " + e.getMessage());
        }
    }

    public void buscarPorNumero() {
        ConsoleUtils.imprimirDivisores("BUSCAR PEDIDO POR NÚMERO");
        try {
            String numeroPedido = input.leerNumeroPedido("Ingrese Numero de pedido (PED-XXXXXXXX) o 'q' para salir: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                ConsoleUtils.imprimirError(OPERACION_CANCELADA_POR_EL_USUARIO);
                return;
            }
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);
            PedidoPrinter.mostrarCabecera();
            PedidoPrinter.mostrarDetalle(pedido);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar por Numero: " + e.getMessage());
        }
    }

    public void buscarPorTracking() {
        ConsoleUtils.imprimirDivisores("BUSCAR PEDIDO POR TRACKING");
        try {
            String tracking = input.leerStringObligatorio("Ingrese código de tracking: ", "código de tracking");
            Pedido pedido = pedidoService.buscarPorNumeroTracking(tracking);
            PedidoPrinter.mostrarCabecera();
            PedidoPrinter.mostrarDetalle(pedido);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar por Tracking: " + e.getMessage());
        }
    }

    public void buscarPorId() {
        ConsoleUtils.imprimirDivisores("BUSCAR PEDIDO POR ID");
        try {
            Long id = input.leerLong("Ingrese ID de pedido: ");
            Pedido pedido = pedidoService.buscarPorId(id);
            PedidoPrinter.mostrarCabecera();
            PedidoPrinter.mostrarDetalle(pedido);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar por ID: " + e.getMessage());
        }
    }

    public void buscarPorCliente() {
        ConsoleUtils.imprimirDivisores("BUSCAR PEDIDO POR CLIENTE");
        try {
            String clienteNombre = input.leerStringObligatorio("Ingrese Cliente de pedido: ", "nombre de cliente");
            Long total = pedidoService.obtenerCantidadTotalDePedidosPorCliente(clienteNombre);
            ConsoleUtils.imprimirMensaje("Total de pedidos registrados: " + total);

            Paginador<Pedido> paginador = new Paginador<>(50L, input);
            paginador.paginar(
                    (cantidad, pagina) -> {
                        try {
                            return pedidoService.buscarPorCliente(clienteNombre, cantidad, pagina);
                        } catch (Exception e) {
                            ConsoleUtils.imprimirError("❌ Error al obtener pedidos: " + e.getMessage());
                            return List.of();
                        }
                    },
                    lista -> {
                        ConsoleUtils.imprimirLineaVacia();
                        PedidoPrinter.mostrarCabecera();
                        lista.forEach(PedidoPrinter::mostrarDetalle);
                    },
                    total
            );
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar por Cliente: " + e.getMessage());
        }
    }

    public void actualizarPedido() {
        ConsoleUtils.imprimirDivisores("ACTUALIZAR PEDIDO POR NÚMERO");
        try {
            String numeroPedido = input.leerNumeroPedido(
                    "Ingrese Numero del pedido a actualizar (PED-XXXXXXXX) o 'q' para salir: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                ConsoleUtils.imprimirError(OPERACION_CANCELADA_POR_EL_USUARIO);
                return;
            }
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);
            if (pedido == null) {
                ConsoleUtils.imprimirError("❌ El pedido no existe.");
                return;
            } else if (pedido.getEstado() == EstadoPedido.ENVIADO) {
                ConsoleUtils.imprimirError("❌ No se puede actualizar un pedido que ya ha sido enviado.");
                return;
            }

            ConsoleUtils.imprimirMensaje("Datos actuales del pedido:");
            PedidoPrinter.mostrarCabecera();
            PedidoPrinter.mostrarDetalle(pedido);

            String nuevoCliente = input.prompt(
                    "Nuevo nombre del Cliente (dejar vacío para no cambiar): ");
            if (!nuevoCliente.isBlank()) {
                pedido.setClienteNombre(nuevoCliente);
            }

            String totalInput = input.prompt(
                    "Nuevo total del pedido (dejar vacío para no cambiar): ");
            if (!totalInput.isBlank()) {
                double nuevoTotal = Double.parseDouble(totalInput);
                pedido.setTotal(nuevoTotal);
            }

            pedidoService.actualizar(pedido);
            ConsoleUtils.imprimirMensaje("✅ Pedido actualizado correctamente.");
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al actualizar el pedido: " + e.getMessage());
        }
    }

    //ELIMINAR PEDIDOS
    public void eliminarPedidoPorNumero() {
        ConsoleUtils.imprimirDivisores("ELIMINAR PEDIDO POR NÚMERO");
        try {
            String numeroPedido = input.leerNumeroPedido(
                    "Ingrese Numero del pedido a eliminar (PED-XXXXXXXX) o 'q' para salir: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                ConsoleUtils.imprimirError(OPERACION_CANCELADA_POR_EL_USUARIO);
                return;
            }
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);
            eliminar(pedido);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al eliminar por Número: " + e.getMessage());
        }
    }

    public void eliminarPedidoPorId() {
        ConsoleUtils.imprimirDivisores("ELIMINAR PEDIDO POR ID");
        try {
            Pedido pedido = pedidoService.buscarPorId(input.leerLong("Ingrese ID del pedido a eliminar: "));
            eliminar(pedido);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al eliminar por ID: " + e.getMessage());
        }
    }

    private void eliminar(Pedido pedido) {
        if (pedido == null) {
            ConsoleUtils.imprimirError("❌ El pedido no existe.");
            return;
        } else if (pedido.getEstado() == EstadoPedido.ENVIADO) {
            ConsoleUtils.imprimirError("❌ No se puede eliminar un pedido que ya ha sido enviado.");
            return;
        } else if (pedido.getEnvio() != null && !pedido.getEnvio().getEliminado()) {
            ConsoleUtils.imprimirError("❌ El pedido tiene un envío asociado. Elimine primero el envío: "
                    + pedido.getEnvio().getTracking());
            return;
        }

        ConsoleUtils.imprimirMensaje("¿Está seguro que desea eliminar el pedido " + pedido.getNumero() + "? (s/n): ");
        if (input.nextLine().trim().equalsIgnoreCase("s")) {

            try {
                pedidoService.eliminar(pedido);
                ConsoleUtils.imprimirMensaje("✅ Pedido eliminado correctamente.");
            } catch (Exception e) {
                ConsoleUtils.imprimirError("❌ Error al eliminar pedido: " + e.getMessage());
            }
        } else {
            ConsoleUtils.imprimirError("❌ Operación cancelada.");
        }
    }
}
