package gestorenvios.controllers;

import gestorenvios.entities.*;
import gestorenvios.models.exceptions.envios.ActualizacionEnvioException;
import gestorenvios.models.exceptions.envios.EliminacionEnvioException;
import gestorenvios.services.GenericEnviosService;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.ui.EnvioPrinter;
import gestorenvios.ui.InputReader;
import gestorenvios.ui.Paginador;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EnvioController {

    private final GenericEnviosService<Envio> envioService;
    private final GenericPedidosService<Pedido> pedidoService;
    private final InputReader input;

    public EnvioController(GenericEnviosService<Envio> envioService,
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
                    input.readPedidoNumero("Ingrese el NÚMERO de PEDIDO al que asignar el envío: "));

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
            System.out.println("❌ Error al buscar: " + e.getMessage());
            return null;
        }
    }

    public Envio buscarPorNumeroPedido() {
        System.out.println("\n--- BUSCAR ENVIO POR TRACKING ---");
        try {
            String numero = input.readPedidoNumero("Ingrese Numero de pedido (PED-XXXXXXXX): ");
            Pedido p = pedidoService.buscarPorNumeroPedido(numero);
            Envio envio = p.getEnvio();
            EnvioPrinter.mostrarResumen(envio);
            return envio;
        } catch (Exception e) {
            System.out.println("❌ Error al buscar: " + e.getMessage());
            return null;
        }
    }

    public Envio buscarPorId() {
        System.out.println("\n--- BUSCAR ENVIO POR TRACKING ---");
        try {
            Envio envio = envioService.buscarPorId(input.readLong("Ingrese ID de pedido: "));
            EnvioPrinter.mostrarResumen(envio);
            return envio;
        } catch (Exception e) {
            System.out.println("❌ Error al buscar: " + e.getMessage());
            return null;
        }
    }

    public void actualizarPorTracking() {
        System.out.println("\n--- ACTUALIZAR ENVÍO (POR TRACKING) ---");
        try {
            Envio envio = envioService.buscarPorTracking(input.prompt("Ingrese Tracking del envío a modificar: "));
            actualizar(envio);
            System.out.println("✅ Envío actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    public void actualizarEnvioPorId() {
        System.out.println("\n--- ACTUALIZAR ENVÍO (POR ID) ---");
        try {
            Envio envio = envioService.buscarPorId(input.readLong("Ingrese ID del envío a modificar: "));
            actualizar(envio);
            System.out.println("✅ Envío actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    public void actualizarEnvioPorNumeroPedido() {
        System.out.println("\n--- ACTUALIZAR ENVÍO DE UN PEDIDO ---");
        try {
            String numeroPedido = input.readPedidoNumero("Ingrese el NÚMERO de PEDIDO: ");
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);

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

            Envio envio = pedido.getEnvio();
            actualizar(envio);
            System.out.println("✅ Envío del pedido actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void actualizar(Envio envio) throws Exception {
        if (envio == null) {
            throw new ActualizacionEnvioException("❌ No se encontró ningún envío con ese ID.");
        }

        System.out.println("--- Editando Envío ID: " + envio.getId() + " ---");
        System.out.println("(Presione Enter para mantener el valor actual)");

        // Actualizar Tracking
        System.out.print("Nuevo Tracking (" + envio.getTracking() + "): ");
        String tracking = input.nextLine().trim();

        if (!tracking.isEmpty()) {
            envio.setTracking(tracking);
        }

        // Actualizar Costo
        System.out.print("Nuevo Costo (" + envio.getCosto() + "): ");
        String costoStr = input.nextLine().trim();
        if (!costoStr.isEmpty()) {
            try {
                envio.setCosto(Double.parseDouble(costoStr));
            } catch (NumberFormatException _) {
                System.out.println("⚠ Formato incorrecto. Se mantiene el costo anterior.");
            }
        }

        // Actualizar Fechas
        System.out.print("Nueva Fecha Despacho (" + envio.getFechaDespacho() + "): ");
        String fDespacho = input.nextLine().trim();
        if (!fDespacho.isEmpty()) {
            try {
                envio.setFechaDespacho(LocalDate.parse(fDespacho));
            } catch (DateTimeParseException e) {
                System.out.println("⚠ Fecha inválida. Se mantiene la anterior.");
            }
        }

        System.out.print("Nueva Fecha Estimada (" + envio.getFechaEstimada() + "): ");
        String fEstimada = input.nextLine().trim();
        if (!fEstimada.isEmpty()) {
            try {
                envio.setFechaEstimada(LocalDate.parse(fEstimada));
            } catch (DateTimeParseException e) {
                System.out.println("⚠ Fecha inválida. Se mantiene la anterior.");
            }
        }

        // Actualizar Estado (Enum)
        System.out.println("Estado actual: " + envio.getEstado());
        System.out.print("¿Desea cambiar el estado? (s/n): ");
        if (input.nextLine().trim().equalsIgnoreCase("s")) {
            envio.setEstado(elegirEstadoEnvio());
        }

        // Llamada al Service para guardar cambios
        envioService.actualizar(envio);
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
            Envio envio = envioService.buscarPorNumeroPedido(input.readPedidoNumero("Ingrese el NÚMERO de PEDIDO: "));
            eliminar(envio);
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    public void eliminarEnvioPorId() {
        System.out.println("\n--- ELIMINAR ENVÍO POR ID ---");
        System.out.println("⚠ PRECAUCIÓN: Esto eliminará el envío aunque tenga un pedido asociado.");
        try {
            Envio envio = envioService.buscarPorId(input.readLong("Ingrese ID del envío a eliminar: "));
            eliminar(envio);
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    private void eliminar(Envio envio) throws Exception {
        if (envio == null) {
            throw new EliminacionEnvioException("❌ El envío no existe.");
        }

        System.out.print("¿Está seguro que desea eliminar el envío " + envio.getTracking() + "? (s/n): ");
        if (input.nextLine().trim().equalsIgnoreCase("s")) {

            // Llamada real al Service
            envioService.eliminar(envio.getId());
            System.out.println("✅ Envío eliminado.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private Envio crearEnvioEnMemoria() {
        System.out.println("\n... Configurando datos del Envío ...");
        Envio envio = new Envio();
        envio.setTracking(input.prompt("Número de Tracking: "));

        envio.setCosto(input.readDouble("Costo del envío: "));

        envio.setFechaDespacho(input.readDate("Fecha de Despacho (AAAA-MM-DD): "));
        envio.setFechaEstimada(input.readDate("Fecha Estimada de Entrega (AAAA-MM-DD): "));

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