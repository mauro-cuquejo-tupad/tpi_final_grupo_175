package gestorenvios.ui.console.controllers;

import gestorenvios.entities.*;
import gestorenvios.services.GenericEnviosService;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.ui.console.input.InputReader;
import gestorenvios.ui.console.output.EnvioPrinter;
import gestorenvios.ui.console.utils.Paginador;

import java.time.LocalDate;
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
            Pedido pedido = getPedido();
            if (pedido == null) {
                return;
            }
            String tracking = obtenerTrackingValido();
            if (tracking == null) {
                return;
            }

            Envio envio = crearEnvioEnMemoria(tracking);

            //transaccional para crear envio y actualizar pedido
            String numeroEnvio = envioService.crearEnvioYActualizarPedido(envio, pedido);

            System.out.println("✅ Envío Tracking Nro: " + numeroEnvio + " creado exitosamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al crear envío: " + e.getMessage());
        }
    }

    private String obtenerTrackingValido() {
        String tracking;
        while (true) {
            tracking = input.prompt("Ingrese el Tracking envío o 'q' para salir: ");
            if (tracking.equalsIgnoreCase("q")) {
                System.out.println("❌ Operación cancelada por el usuario.");
                return null;
            }
            Envio envio = envioService.buscarPorTracking(tracking);
            if (envio != null) {
                System.out.println("❌ Ya existe un envío con ese Tracking. Intente nuevamente.");
            } else {
                break;
            }
        }
        return tracking;
    }

    private Pedido getPedido() {
        Pedido pedido;
        while (true) {
            String numeroPedido = input.leerNumeroPedido(
                    "Ingrese el Numero del Pedido (PED-XXXXXXXX) al que asignar el envío o 'q' para salir: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                System.out.println("❌ Operación cancelada por el usuario.");
                return null;
            }
            pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);
            if (pedido == null) {
                System.out.println("❌ El pedido con número " + numeroPedido + " no existe. Intente nuevamente.");
            } else if (pedido.getEnvio() != null) {
                System.out.println("❌ El pedido con número " + numeroPedido + " ya tiene un envío asignado. Intente nuevamente.");
            } else {
                break;
            }
        }
        return pedido;
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

    public void buscarPorTracking() {
        System.out.println("\n--- BUSCAR ENVIO POR TRACKING ---");
        try {
            Envio envio = envioService.buscarPorTracking(input.prompt("Ingrese Tracking de pedido: "));
            EnvioPrinter.mostrarResumen(envio);
        } catch (Exception e) {
            System.out.println("❌ Error al buscar por Tracking: " + e.getMessage());
        }
    }

    public void buscarPorNumeroPedido() {
        System.out.println("\n--- BUSCAR ENVIO POR NUMERO PEDIDO ---");
        try {
            String numeroPedido = input.leerNumeroPedido("Ingrese Numero de pedido (PED-XXXXXXXX) o 'q' para salir: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                System.out.println("❌ Operación cancelada por el usuario.");
                return;
            }
            Pedido p = pedidoService.buscarPorNumeroPedido(numeroPedido);

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

    public void actualizarEstadoPorTracking() {
        System.out.println("\n--- ACTUALIZAR ENVÍO (POR TRACKING) ---");
        Envio envio;
        try {
            envio = envioService.buscarPorTracking(input.prompt("Ingrese Tracking del envío a modificar: "));
            actualizarEstadoEnvio(envio);
            if (envio == null) {
                return;
            }
            System.out.println("✅ Envío actualizado de Tracking " + envio.getTracking() + " actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al buscar envío por Tracking: " + e.getMessage());
        }
    }

    public void actualizarEstadoEnvioPorId() {
        System.out.println("\n--- ACTUALIZAR ENVÍO (POR ID) ---");
        Envio envio;
        try {
            envio = envioService.buscarPorId(input.leerLong("Ingrese ID del envío a modificar: "));
            actualizarEstadoEnvio(envio);
            if (envio == null) {
                return;
            }
            System.out.println("✅ Envío actualizado de Tracking " + envio.getTracking() + " actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al buscar envío por ID: " + e.getMessage());
        }
    }

    public void actualizarEstadoEnvioPorNumeroPedido() {
        System.out.println("\n--- ACTUALIZAR ENVÍO DE UN PEDIDO ---");
        Envio envio;
        try {
            String numeroPedido = input.leerNumeroPedido("Ingrese el NÚMERO de PEDIDO: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                System.out.println("❌ Operación cancelada por el usuario.");
                return;
            }
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);

            if (pedido == null) {
                System.out.println("❌ Pedido no encontrado.");
                return;
            } else if (pedido.getEnvio() == null) {
                System.out.println("⚠ Este pedido (" + pedido.getNumero() + ") NO tiene envío asociado.");
                System.out.println("Use la opción 'Actualizar Pedido' para asignarle uno nuevo.");
                return;
            }

            envio = pedido.getEnvio();
            actualizarEstadoEnvio(envio);
            if (envio == null) {
                return;
            }
            System.out.println("✅ Envío actualizado de Tracking " + envio.getTracking() + " actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al buscar envío: " + e.getMessage());
        }
    }

    private void actualizarEstadoEnvio(Envio envio) {
        if (envio == null) {
            System.out.println("❌ Envío no encontrado.");
            return;
        } else if (envio.getEstado().equals(EstadoEnvio.ENTREGADO)) {
            System.out.println("⚠ El envío ya está en estado ENTREGADO y no puede ser modificado.");
            return;
        }

        EstadoEnvio nuevoEstado = elegirEstadoEnvio();
        if (nuevoEstado.equals(EstadoEnvio.ENTREGADO)) {
            envio.setEstado(nuevoEstado);
            Pedido pedido = pedidoService.buscarPorNumeroTracking(envio.getTracking());
            envioService.actualizarEstado(envio, pedido);
        } else {
            envio.setEstado(nuevoEstado);
            envioService.actualizar(envio);
        }
    }

    //Eliminar envíos

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
            String numeroPedido = input.leerNumeroPedido("Ingrese el NÚMERO de PEDIDO: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                System.out.println("❌ Operación cancelada por el usuario.");
                return;
            }
            Envio envio = envioService.buscarPorNumeroPedido(numeroPedido);

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
        } else if (envio.getEstado() == EstadoEnvio.ENTREGADO) {
            System.out.println("❌ No se puede eliminar un envío que ya ha sido entregado.");
            return;
        }

        System.out.print("¿Está seguro que desea eliminar el envío " + envio.getTracking() + "? (s/n): ");
        if (input.nextLine().trim().equalsIgnoreCase("s")) {

            try {
                // Llamada real al Service
                envioService.eliminar(envio);
                System.out.println("✅ Envío eliminado.");
            } catch (Exception e) {
                System.out.println("❌ Error al eliminar envío: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private Envio crearEnvioEnMemoria(String tracking) {
        System.out.println("\n... Configurando datos del Envío ...");
        Envio envio = new Envio();
        envio.setTracking(tracking);

        envio.setCosto(input.leerDouble("Costo del envío: "));

        envio.setFechaDespacho(LocalDate.now());
        envio.setFechaEstimada(envio.getFechaDespacho().plusDays(5));

        envio.setEmpresa(elegirEmpresaEnvio());
        envio.setTipo(elegirTipoEnvio());
        envio.setEstado(EstadoEnvio.EN_PREPARACION);

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