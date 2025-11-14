package gestorenvios.main;

import gestorenvios.entities.*;
import gestorenvios.services.GenericEnviosService;
import gestorenvios.services.GenericPedidosService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Controlador de Menú (MenuHandler). Actúa como intermediario entre el Usuario
 * y la capa de Servicios. No contiene lógica de negocio ni acceso a datos, solo
 * lógica de presentación.
 */
public class MenuHandler {

    private final Scanner scanner;

    // Dependencias de la capa Service
    private final GenericPedidosService<Pedidos> pedidoService;
    private final GenericEnviosService<Envios> envioService;

    /**
     * Constructor con Inyección de Dependencias.
     *
     * @param scanner       El scanner único de la app.
     * @param pedidoService El servicio que contiene la lógica de pedidos.
     * @param envioService  El servicio que contiene la lógica de envíos.
     */
    public MenuHandler(Scanner scanner,
                       GenericPedidosService<Pedidos> pedidoService,
                       GenericEnviosService<Envios> envioService) {

        this.scanner = scanner;
        this.pedidoService = pedidoService;
        this.envioService = envioService;
    }

    // MÉTODOS DE GESTIÓN DE PEDIDOS
    public void crearPedido() {
        try {
            System.out.println("\n--- CREAR NUEVO PEDIDO ---");

            // Captura de datos
            System.out.print("Número de Pedido (ej. PED-001): ");
            String numero = scanner.nextLine().trim();

            System.out.print("Nombre del Cliente: ");
            String cliente = scanner.nextLine().trim();

            double total = leerDouble("Total del pedido: ");
            LocalDate fecha = leerFecha("Fecha del pedido (AAAA-MM-DD): ");

            EstadoPedido estado = elegirEstadoPedido();

            // Construcción
            Pedidos pedido = new Pedidos();
            pedido.setNumero(numero);
            pedido.setClienteNombre(cliente);
            pedido.setTotal(total);
            pedido.setFecha(fecha);
            pedido.setEstado(estado);

            // Gestión del Envío opcional
            System.out.print("¿Desea agregar un envío a este pedido? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                Envios envio = crearEnvioEnMemoria();
                pedido.setEnvio(envio);
            }

            // Llamada al Service
            ///////// El Service decidirá si llamar a 'insertar' o 'insertTx' según si tiene envío
            pedidoService.crear(pedido);

            System.out.println("✅ Pedido creado exitosamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al crear el pedido: " + e.getMessage());
        }
    }

    public void listarPedidos() {
        System.out.println("\n--- LISTA DE PEDIDOS ---");
        try {
            List<Pedidos> lista = pedidoService.buscarTodos();

            if (lista.isEmpty()) {
                System.out.println("No hay pedidos registrados.");
            } else {
                for (Pedidos p : lista) {
                    System.out.println(p);
                    if (p.getEnvio() != null) {
                        System.out.println("   ↳ Con Envío: " + p.getEnvio().getTracking());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error al listar: " + e.getMessage());
        }
    }

    public void actualizarPedido() {
        System.out.println("\n--- ACTUALIZAR PEDIDO ---");
        try {
            System.out.print("Ingrese ID del pedido a modificar: ");
            Long id = leerLong();

            // buscamos si existe
            Pedidos pedido = pedidoService.buscarPorId(id);
            if (pedido == null) {
                System.out.println("Pedido no encontrado.");
                return;
            }

            System.out.println("Editando pedido: " + pedido.getNumero());
            System.out.println("(Presione Enter para mantener el valor actual)");

            // Lógica para editar solo si el usuario escribe algo
            System.out.print("Nuevo Cliente (" + pedido.getClienteNombre() + "): ");
            String nuevoCliente = scanner.nextLine().trim();
            if (!nuevoCliente.isEmpty()) {
                pedido.setClienteNombre(nuevoCliente);
            }

            // Actualizar TOTAL
            System.out.print("Nuevo Total (" + pedido.getTotal() + "): ");
            String totalStr = scanner.nextLine().trim();
            if (!totalStr.isEmpty()) {
                try {
                    pedido.setTotal(Double.parseDouble(totalStr));
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Formato de número incorrecto. Se mantiene el valor anterior.");
                }
            }

            // Actualizar FECHA
            System.out.print("Nueva Fecha (" + pedido.getFecha() + ") [AAAA-MM-DD]: ");
            String fechaStr = scanner.nextLine().trim();
            if (!fechaStr.isEmpty()) {
                try {
                    pedido.setFecha(LocalDate.parse(fechaStr));
                } catch (DateTimeParseException e) {
                    System.out.println("⚠ Formato de fecha incorrecto. Se mantiene la fecha anterior.");
                }
            }

            // Actualizar ESTADO (Enum)
            System.out.println("Estado actual: " + pedido.getEstado());
            System.out.print("¿Desea cambiar el estado? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                pedido.setEstado(elegirEstadoPedido()); // Reutilizamos método selector
            }

            // Llamada al Service para guardar cambios
            pedidoService.actualizar(pedido);
            System.out.println("✅ Pedido actualizado.");

        } catch (Exception e) {
            System.out.println("❌ Error al actualizar: " + e.getMessage());
        }
    }

    public void eliminarPedido() {
        System.out.println("\n--- ELIMINAR PEDIDO ---");
        try {
            System.out.print("Ingrese ID del pedido a eliminar: ");
            Long id = leerLong();

            // Llamada al Service (Soft Delete)
            pedidoService.eliminar(id);

            System.out.println("✅ Pedido eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    public void buscarPedidoPorTracking() {
        System.out.println("\n--- BUSCAR PEDIDO POR TRACKING ---");
        try {
            System.out.print("Ingrese código de tracking: ");
            String tracking = scanner.nextLine().trim();

            // BÚSQUEDA REAL TODO: ver si se puede hacer desde la implementación del service.
            Pedidos pedido = pedidoService.buscarPorTracking(tracking);

            if (pedido != null) {
                System.out.println("✅ ¡Pedido Encontrado!");
                System.out.println("ID: " + pedido.getId() + " | Cliente: " + pedido.getClienteNombre());
                System.out.println("Envío asociado: " + pedido.getEnvio().getTracking() + " | Estado: " + pedido.getEnvio().getEstado());
            } else {
                System.out.println("❌ No se encontró ningún pedido con ese tracking.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al buscar: " + e.getMessage());
        }
    }

    // MÉTODOS DE GESTIÓN DE ENVÍOS

    public void crearEnvioIndependiente() {
        System.out.println("\n--- CREAR ENVÍO INDEPENDIENTE ---");
        try {
            Envios envio = crearEnvioEnMemoria();

            envioService.crear(envio);

            System.out.println("✅ Envío creado exitosamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al crear envío: " + e.getMessage());
        }
    }

    public void listarEnvios() {
        System.out.println("\n--- LISTA DE ENVÍOS ---");
        try {
            List<Envios> lista = envioService.buscarTodos();

            if (lista.isEmpty()) {
                System.out.println("No hay envíos registrados.");
            } else {
                for (Envios e : lista) {
                    System.out.println(e); // asume toString() en Envios
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error al listar envíos: " + e.getMessage());
        }
    }

    public void actualizarEnvioPorId() {
        System.out.println("\n--- ACTUALIZAR ENVÍO (POR ID) ---");
        try {
            System.out.print("Ingrese ID del envío a modificar: ");
            Long id = leerLong();

            // Llamada al Service
            Envios envio = envioService.buscarPorId(id);

            if (envio == null) {
                System.out.println("❌ No se encontró ningún envío con ese ID.");
                return;
            }

            System.out.println("--- Editando Envío ID: " + envio.getId() + " ---");
            System.out.println("(Presione Enter para mantener el valor actual)");

            // Actualizar Tracking
            System.out.print("Nuevo Tracking (" + envio.getTracking() + "): ");
            String tracking = scanner.nextLine().trim();
            if (!tracking.isEmpty()) {
                envio.setTracking(tracking);
            }

            // Actualizar Costo
            System.out.print("Nuevo Costo (" + envio.getCosto() + "): ");
            String costoStr = scanner.nextLine().trim();
            if (!costoStr.isEmpty()) {
                try {
                    envio.setCosto(Double.parseDouble(costoStr));
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Formato incorrecto. Se mantiene el costo anterior.");
                }
            }

            // Actualizar Fechas
            System.out.print("Nueva Fecha Despacho (" + envio.getFechaDespacho() + "): ");
            String fDespacho = scanner.nextLine().trim();
            if (!fDespacho.isEmpty()) {
                try {
                    envio.setFechaDespacho(LocalDate.parse(fDespacho));
                } catch (DateTimeParseException e) {
                    System.out.println("⚠ Fecha inválida. Se mantiene la anterior.");
                }
            }

            System.out.print("Nueva Fecha Estimada (" + envio.getFechaEstimada() + "): ");
            String fEstimada = scanner.nextLine().trim();
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
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                envio.setEstado(elegirEstadoEnvio());
            }

            // Llamada al Service para guardar cambios
            envioService.actualizar(envio);
            System.out.println("✅ Envío actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    public void eliminarEnvioPorId() {
        System.out.println("\n--- ELIMINAR ENVÍO POR ID ---");
        System.out.println("⚠ PRECAUCIÓN: Esto eliminará el envío aunque tenga un pedido asociado.");

        try {
            System.out.print("Ingrese ID del envío a eliminar: ");
            Long id = leerLong();

            // buscar si existe antes de intentar borrar
            Envios envio = envioService.buscarPorId(id);
            if (envio == null) {
                System.out.println("❌ El envío no existe.");
                return;
            }

            System.out.print("¿Está seguro que desea eliminar el envío " + envio.getTracking() + "? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {

                // Llamada real al Service
                envioService.eliminar(id);
                System.out.println("✅ Envío eliminado.");
            } else {
                System.out.println("Operación cancelada.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    public void actualizarEnvioPorPedido() {
        System.out.println("\n--- ACTUALIZAR ENVÍO DE UN PEDIDO ---");
        try {
            System.out.print("Ingrese el ID del PEDIDO: ");
            Long idPedido = leerLong();

            // Buscar Pedido 
            Pedidos pedido = pedidoService.buscarPorId(idPedido);

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

            Envios envio = pedido.getEnvio();
            System.out.println("-> Envío encontrado: " + envio.getTracking());

            // Lógica de actualización
            System.out.println("(Presione Enter para mantener el valor actual)");

            System.out.print("Nuevo Costo (" + envio.getCosto() + "): ");
            String costoStr = scanner.nextLine().trim();
            if (!costoStr.isEmpty()) {
                try {
                    envio.setCosto(Double.parseDouble(costoStr));
                } catch (NumberFormatException e) {
                    System.out.println("Valor incorrecto, se mantiene.");
                }
            }

            System.out.println("Estado actual: " + envio.getEstado());
            System.out.print("¿Cambiar estado? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                envio.setEstado(elegirEstadoEnvio());
            }

            // Llamada al Service de Envios
            envioService.actualizar(envio);
            System.out.println("✅ Envío del pedido actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public void eliminarEnvioPorPedido() {
        System.out.println("\n--- ELIMINAR ENVÍO DE UN PEDIDO ---");
        try {
            System.out.print("Ingrese el ID del PEDIDO: ");
            Long idPedido = leerLong();

            // Verificación de existencia
            Pedidos pedido = pedidoService.buscarPorId(idPedido);
            if (pedido == null) {
                System.out.println("❌ Pedido no encontrado.");
                return;
            }
            if (pedido.getEnvio() == null) {
                System.out.println("⚠ El pedido no tiene envío para eliminar.");
                return;
            }

            System.out.print("¿Seguro que desea desvincular y eliminar el envío del pedido " + pedido.getNumero() + "? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {

                // Llamada al Service (Método Transaccional)
                pedidoService.eliminarEnvioDePedido(idPedido);

                System.out.println("✅ Envío desvinculado y eliminado correctamente.");
            } else {
                System.out.println("Operación cancelada.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // AUXILIARES
    private Envios crearEnvioEnMemoria() {
        System.out.println("\n... Configurando datos del Envío ...");
        Envios envio = new Envios();

        System.out.print("Número de Tracking: ");
        envio.setTracking(scanner.nextLine().trim());

        envio.setCosto(leerDouble("Costo del envío: "));

        envio.setFechaDespacho(leerFecha("Fecha de Despacho (AAAA-MM-DD): "));
        envio.setFechaEstimada(leerFecha("Fecha Estimada de Entrega (AAAA-MM-DD): "));

        envio.setEmpresa(elegirEmpresaEnvio());
        envio.setTipo(elegirTipoEnvio());
        envio.setEstado(elegirEstadoEnvio());

        envio.setEliminado(false);

        return envio;
    }

    // SELECTORES 
    private EstadoPedido elegirEstadoPedido() {
        System.out.println("Seleccione Estado del Pedido:");
        EstadoPedido[] valores = EstadoPedido.values();
        mostrarOpcionesEnum(valores);
        return valores[leerOpcionEnum(valores.length) - 1];
    }

    private EmpresaEnvio elegirEmpresaEnvio() {
        System.out.println("Seleccione Empresa de Envío:");
        EmpresaEnvio[] valores = EmpresaEnvio.values();
        mostrarOpcionesEnum(valores);
        return valores[leerOpcionEnum(valores.length) - 1];
    }

    private TipoEnvio elegirTipoEnvio() {
        System.out.println("Seleccione Tipo de Envío:");
        TipoEnvio[] valores = TipoEnvio.values();
        mostrarOpcionesEnum(valores);
        return valores[leerOpcionEnum(valores.length) - 1];
    }

    private EstadoEnvio elegirEstadoEnvio() {
        System.out.println("Seleccione Estado del Envío:");
        EstadoEnvio[] valores = EstadoEnvio.values();
        mostrarOpcionesEnum(valores);
        return valores[leerOpcionEnum(valores.length) - 1];
    }

    private void mostrarOpcionesEnum(Object[] valores) {
        for (int i = 0; i < valores.length; i++) {
            System.out.println((i + 1) + ". " + valores[i]);
        }
    }

    // --- LECTURA SEGURA ---
    private int leerOpcionEnum(int maxOpcion) {
        while (true) {
            try {
                System.out.print("Opción: ");
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                if (opcion >= 1 && opcion <= maxOpcion) {
                    return opcion;
                }
                System.out.println("Opción fuera de rango.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número válido.");
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número válido.");
            }
        }
    }

    private Long leerLong() {
        while (true) {
            try {
                String entrada = scanner.nextLine();
                return Long.parseLong(entrada);
            } catch (NumberFormatException e) {
                System.out.print("Error: Ingrese un número válido (ID): ");
            }
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato inválido. Use AAAA-MM-DD.");
            }
        }
    }
}
