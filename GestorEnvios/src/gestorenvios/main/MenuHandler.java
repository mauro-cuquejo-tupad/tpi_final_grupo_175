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
    private final GenericPedidosService<Pedido> pedidoService;
    private final GenericEnviosService<Envio> envioService;

    /**
     * Constructor con Inyección de Dependencias.
     *
     * @param scanner       El scanner único de la app.
     * @param pedidoService El servicio que contiene la lógica de pedidos.
     * @param envioService  El servicio que contiene la lógica de envíos.
     */
    public MenuHandler(Scanner scanner,
                       GenericPedidosService<Pedido> pedidoService,
                       GenericEnviosService<Envio> envioService) {

        this.scanner = scanner;
        this.pedidoService = pedidoService;
        this.envioService = envioService;
    }

    // MÉTODOS DE GESTIÓN DE PEDIDOS
    public void crearPedido() {
        try {
            System.out.println("\n--- CREAR NUEVO PEDIDO ---");

            System.out.print("Nombre del Cliente: ");
            String cliente = scanner.nextLine().trim();

            double total = leerDouble("Total del pedido: ");

            // Construcción
            Pedido pedido = new Pedido();
            pedido.setClienteNombre(cliente);
            pedido.setTotal(total);
            pedido.setFecha(LocalDate.now());
            pedido.setEstado(EstadoPedido.NUEVO); //NUEVO

            // Llamada al Service
            pedidoService.crear(pedido);

            System.out.println("✅ Pedido creado exitosamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al crear el pedido: " + e.getMessage());
        }
    }

    public void listarPedidos() {
        System.out.println("\n--- LISTA DE PEDIDOS ---");
        try {
            Long cantidadPedidos = pedidoService.obtenerCantidadTotalDePedidos();
            System.out.println("Total de pedidos registrados: " + cantidadPedidos);

            if (cantidadPedidos == 0) {
                System.out.println("No hay pedidos registrados.");
                return;
            }

            Long pedidosPorPagina = 50L;
            Long pagina = 1L;

            do {
                List<Pedido> lista = pedidoService.buscarTodos(pedidosPorPagina, pagina);
                if (lista.isEmpty()) break;

                lista.forEach(p -> {
                    System.out.println(p);
                    if (p.getEnvio() != null) {
                        System.out.println("   ↳ Con Envío: " + p.getEnvio().getTracking());
                    }
                });

                if (cantidadPedidos <= pedidosPorPagina * pagina) break;

                System.out.print("Presione Enter para ver más o 'q' para salir: ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("q")) break;

                pagina++;
            } while (true);

        } catch (Exception e) {
            System.out.println("❌ Error al listar pedidos: " + e.getMessage());
        }
    }

    public void actualizarPedido() {
        System.out.println("\n--- ACTUALIZAR PEDIDO ---");
        try {
            System.out.print("Ingrese Numero de pedido a modificar (PED-XXXXXXXX): ");
            String numero = leerNumeroPedido();

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
            System.out.print("Ingrese Numero del pedido a eliminar (PED-XXXXXXXX): ");
            String numero = leerNumeroPedido();

            // Llamada al Service (Soft Delete)
            pedidoService.eliminarPorNumero(numero);

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
            Pedido pedido = pedidoService.buscarPorNumeroTracking(tracking);

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
            Envio envio = crearEnvioEnMemoria();

            envioService.crear(envio);

            System.out.println("✅ Envío creado exitosamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al crear envío: " + e.getMessage());
        }
    }

    public void listarEnvios() {
        System.out.println("\n--- LISTA DE ENVÍOS ---");
        try {
            Long cantidadTotalDeEnvios = envioService.obtenerCantidadTotalDeEnvios();
            System.out.println("Total de envíos registrados: " + cantidadTotalDeEnvios);

            if (cantidadTotalDeEnvios == 0) {
                System.out.println("No hay envíos registrados.");
                return;
            }

            Long enviosPorPagina = 50L;
            Long pagina = 1L;

            do {
                List<Envio> lista = envioService.buscarTodos(enviosPorPagina, pagina);
                if (lista.isEmpty()) break;

                lista.forEach(p -> {
                    System.out.println(p);
                    for (Envio e : lista) {
                        System.out.println(e); // asume toString() en Envios
                    }
                });

                if (cantidadTotalDeEnvios <= enviosPorPagina * pagina) break;

                System.out.print("Presione Enter para ver más o 'q' para salir: ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("q")) break;

                pagina++;
            } while (true);

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
            Envio envio = envioService.buscarPorId(id);

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
            Envio envio = envioService.buscarPorId(id);
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
            Pedido pedido = pedidoService.buscarPorId(idPedido);

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
            Pedido pedido = pedidoService.buscarPorId(idPedido);
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
    private Envio crearEnvioEnMemoria() {
        System.out.println("\n... Configurando datos del Envío ...");
        Envio envio = new Envio();

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
                System.out.println("Error: Ingrese un monto válido.");
            }
        }
    }

    private String leerNumeroPedido() {
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada.matches("PED-\\d{8}")) {
                return entrada;
            } else {
                System.out.print("Error: Formato inválido. Use PED-XXXXXXXX. Intente nuevamente: ");
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
            } catch (DateTimeParseException _) {
                System.out.println("Error: Formato inválido. Use AAAA-MM-DD.");
            }
        }
    }

    public void actualizarEnvioPorNumero() {
        System.out.println("\n--- ACTUALIZAR ENVÍO POR NÚMERO DE TRACKING ---");
    }

    public void eliminarEnvioPorNumero() {
        System.out.println("\n--- ELIMINAR ENVÍO POR NÚMERO DE TRACKING ---");
    }
}
