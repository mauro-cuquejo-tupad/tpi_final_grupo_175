package gestorenvios.ui.console.controllers;

import gestorenvios.entities.*;
import gestorenvios.services.GenericEnviosService;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.ui.console.input.InputReader;
import gestorenvios.ui.console.output.EnvioPrinter;
import gestorenvios.ui.console.utils.Paginador;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EnvioConsoleController {

    private final GenericEnviosService<Envio, Pedido> envioService;
    private final GenericPedidosService<Pedido> pedidoService;
    private final InputReader input;

    public EnvioConsoleController(GenericEnviosService<Envio, Pedido> envioService,
                                  GenericPedidosService<Pedido> pedidoService,
                                  InputReader input) {
        this.envioService = envioService;
        this.pedidoService = pedidoService;
        this.input = input;
    }

    public void crear() {
        System.out.println("\n--- CREAR ENVÍO POR NUMERO PEDIDO ---");
        try {
            Pedido pedido = pedidoService.buscarPorNumeroPedido(
                    input.leerNumeroPedido("Ingrese el NÚMERO de PEDIDO al que asignar el envío: "));

            Envio envio = crearEnvioEnMemoria();

            //transaccional para crear envio y actualizar pedido
            envioService.crearEnvioYActualizarPedido(envio, pedido);

            System.out.println("✅ Envío creado exitosamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al crear envío: " + e.getMessage());
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE ENVÍOS ---");
        try {
            Long total = envioService.obtenerCantidadTotalDeEnvios();
            System.out.println("Total de envíos registrados: " + total);

            Paginador<Envio> paginador = new Paginador<>(50L, input);
            paginador.paginar(
                    (pageSize, page) -> {
                        try {
                            return envioService.buscarTodos(pageSize, page);
                        } catch (Exception e) {
                            System.out.println("❌ Error al obtener envíos: " + e.getMessage());
                            return List.of();
                        }
                    },
                    lista -> lista.forEach(EnvioPrinter::mostrarResumen),
                    total
            );
        } catch (Exception e) {
            System.out.println("❌ Error al listar envíos: " + e.getMessage());
        }
    }

    public Envio buscarPorTracking() {
        System.out.println("\n--- BUSCAR ENVIO POR TRACKING ---");
        try {
            Envio envio = envioService.buscarPorTracking(input.prompt("Ingrese Tracking de pedido: "));
            EnvioPrinter.mostrarResumen(envio);
            return envio;
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por Tracking: " + e.getMessage());
            return null;
        }
    }

    public void buscarPorNumeroPedido() {
        System.out.println("\n--- BUSCAR ENVIO POR NUMERO PEDIDO ---");
        try {
            Pedido p = pedidoService.buscarPorNumeroPedido(
                    input.leerNumeroPedido("Ingrese Numero de pedido (PED-XXXXXXXX): "));

            Envio envio = p.getEnvio();
            EnvioPrinter.mostrarResumen(envio);
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por Numero Pedido: " + e.getMessage());
        }
    }

    public void buscarPorId() {
        System.out.println("\n--- BUSCAR ENVIO POR ID ---");
        try {
            Envio envio = envioService.buscarPorId(input.leerLong("Ingrese ID de pedido: "));
            EnvioPrinter.mostrarResumen(envio);
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por ID: " + e.getMessage());
        }
    }

    public void actualizarPorTracking() {
        System.out.println("\n--- ACTUALIZAR ENVÍO (POR TRACKING) ---");
        Envio envio;
        try {
            envio = envioService.buscarPorTracking(input.prompt("Ingrese Tracking del envío a modificar: "));
        } catch (Exception e) {
            System.out.println("❌ Error al buscar envío: " + e.getMessage());
            return;
        }
        actualizar(envio);
    }

    public void actualizarEnvioPorId() {
        System.out.println("\n--- ACTUALIZAR ENVÍO (POR ID) ---");
        Envio envio;
        try {
            envio = envioService.buscarPorId(input.leerLong("Ingrese ID del envío a modificar: "));
        } catch (Exception e) {
            System.out.println("❌ Error al buscar envío: " + e.getMessage());
            return;
        }
        actualizar(envio);
    }

    public void actualizarEnvioPorNumeroPedido() {
        System.out.println("\n--- ACTUALIZAR ENVÍO DE UN PEDIDO ---");
        Envio envio;
        try {
            Pedido pedido = pedidoService.buscarPorNumeroPedido(
                    input.leerNumeroPedido("Ingrese el NÚMERO de PEDIDO: "));

            if (pedido == null) {
                System.out.println("❌ Pedido no encontrado.");
                return;
            }

            // Verificar si tiene envío
            if (pedido.getEnvio() == null) {
                System.out.println("⚠ Este pedido (" + pedido.getNumero() + ") NO tiene envío asociado.");
                System.out.println("Use la opción 'Actualizar Pedido' para asignarle uno nuevo.");
                return;
            }
            envio = pedido.getEnvio();
        } catch (Exception e) {
            System.out.println("❌ Error al buscar envío: " + e.getMessage());
            return;
        }
        actualizar(envio);
    }

    private void actualizar(Envio envio) {
        if (envio == null) {
            System.out.println("❌ Envio no encontrado.");
            return;
        }

        System.out.println("--- Editando Envío: " + envio.getTracking() + " ---");
        System.out.println("(Presione Enter para mantener el valor actual)");

        actualizarTracking(envio);
        actualizarCostoEnvio(envio);
        actualizarFechaDespacho(envio);
        actualizarFechaEstimada(envio);
        actualizarEstado(envio);

        try {
            envioService.actualizar(envio);
            System.out.println("✅ Envío actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    private void actualizarTracking(Envio envio) {
        String tracking = input.prompt("Nuevo Tracking (" + envio.getTracking() + "): ").trim();
        if (!tracking.isEmpty()) envio.setTracking(tracking);
    }

    private void actualizarEstado(Envio envio) {
        System.out.println("Estado actual: " + envio.getEstado());
        if (input.prompt("¿Desea cambiar el estado? (s/n): ").trim().equalsIgnoreCase("s")) {
            envio.setEstado(elegirEstadoEnvio());
        }
    }

    private void actualizarFechaDespacho(Envio envio) {
        String fDespacho = input.prompt("Nueva Fecha Despacho (" + envio.getFechaDespacho() + "): ").trim();
        if (!fDespacho.isEmpty()) {
            try {
                envio.setFechaDespacho(LocalDate.parse(fDespacho));
            } catch (DateTimeParseException _) {
                System.out.println("⚠ Fecha inválida. Se mantiene la anterior.");
            }
        }
    }

    private void actualizarFechaEstimada(Envio envio) {
        String fEstimada = input.prompt("Nueva Fecha Estimada (" + envio.getFechaEstimada() + "): ").trim();
        if (!fEstimada.isEmpty()) {
            try {
                envio.setFechaEstimada(LocalDate.parse(fEstimada));
            } catch (DateTimeParseException _) {
                System.out.println("⚠ Fecha inválida. Se mantiene la anterior.");
            }
        }
    }

    private void actualizarCostoEnvio(Envio envio) {
        String costoStr = input.prompt("Nuevo Costo (" + envio.getCosto() + "): ").trim();
        if (!costoStr.isEmpty()) {
            try {
                double costo = Double.parseDouble(costoStr);
                if (costo <= 0) {
                    System.out.println("⚠ El costo debe ser un valor positivo. Se mantiene el costo anterior.");
                } else {
                    envio.setCosto(costo);
                }
            } catch (NumberFormatException _) {
                System.out.println("⚠ Formato incorrecto. Se mantiene el costo anterior.");
            }
        }
    }

    public void eliminarEnvioPorTracking() {
        System.out.println("\n--- ELIMINAR ENVÍO POR TRACKING ---");
        System.out.println("⚠ PRECAUCIÓN: Esto eliminará el envío aunque tenga un pedido asociado.");
        try {
            Envio envio = envioService.buscarPorTracking(input.prompt("Ingrese Tracking del envío a modificar: "));
            eliminar(envio);
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    public void eliminarEnvioPorNumeroPedido() {
        System.out.println("\n--- ELIMINAR ENVÍO POR NUMERO PEDIDO ---");
        System.out.println("⚠ PRECAUCIÓN: Esto eliminará el envío aunque tenga un pedido asociado.");
        try {
            Envio envio = envioService.buscarPorNumeroPedido(
                    input.leerNumeroPedido("Ingrese el NÚMERO de PEDIDO: "));

            eliminar(envio);
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    public void eliminarEnvioPorId() {
        System.out.println("\n--- ELIMINAR ENVÍO POR ID ---");
        System.out.println("⚠ PRECAUCIÓN: Esto eliminará el envío aunque tenga un pedido asociado.");
        try {
            Envio envio = envioService.buscarPorId(input.leerLong("Ingrese ID del envío a eliminar: "));
            eliminar(envio);
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    private void eliminar(Envio envio) {
        if (envio == null) {
            System.out.println("❌ El envío no existe.");
            return;
        }

        System.out.print("¿Está seguro que desea eliminar el envío " + envio.getTracking() + "? (s/n): ");
        if (input.nextLine().trim().equalsIgnoreCase("s")) {

            try {
                // Llamada real al Service
                envioService.eliminar(envio.getId());
                System.out.println("✅ Envío eliminado.");
            } catch (Exception e) {
                System.out.println("❌ Error al eliminar envío: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private Envio crearEnvioEnMemoria() {
        System.out.println("\n... Configurando datos del Envío ...");
        Envio envio = new Envio();
        envio.setTracking(input.prompt("Número de Tracking: "));

        envio.setCosto(input.leerDouble("Costo del envío: "));

        envio.setFechaDespacho(input.leerFecha("Fecha de Despacho (AAAA-MM-DD): "));
        envio.setFechaEstimada(input.leerFecha("Fecha Estimada de Entrega (AAAA-MM-DD): "));

        envio.setEmpresa(elegirEmpresaEnvio());
        envio.setTipo(elegirTipoEnvio());
        envio.setEstado(elegirEstadoEnvio());

        envio.setEliminado(false);

        return envio;
    }

    private EmpresaEnvio elegirEmpresaEnvio() {
        System.out.println("Seleccione Empresa de Envío:");
        EmpresaEnvio[] valores = EmpresaEnvio.values();
        input.mostrarOpcionesEnum(valores);
        return valores[input.leerOpcionEnum(valores.length) - 1];
    }

    private TipoEnvio elegirTipoEnvio() {
        System.out.println("Seleccione Tipo de Envío:");
        TipoEnvio[] valores = TipoEnvio.values();
        input.mostrarOpcionesEnum(valores);
        return valores[input.leerOpcionEnum(valores.length) - 1];
    }

    private EstadoEnvio elegirEstadoEnvio() {
        System.out.println("Seleccione Estado del Envío:");
        EstadoEnvio[] valores = EstadoEnvio.values();
        input.mostrarOpcionesEnum(valores);
        return valores[input.leerOpcionEnum(valores.length) - 1];
    }
}