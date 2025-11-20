package gestorenvios.ui.console.controllers;

import gestorenvios.entities.*;
import gestorenvios.services.GenericEnviosService;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.ui.console.input.InputReader;
import gestorenvios.ui.console.output.EnvioPrinter;
import gestorenvios.ui.console.utils.ConsoleUtils;
import gestorenvios.ui.console.utils.Paginador;

import java.time.LocalDate;
import java.util.List;

/***
 * Controlador de consola para gestionar envíos.
 */
public class EnvioConsoleController {

    /***
     * Mensaje de operación cancelada por el usuario.
     */
    public static final String OPERACION_CANCELADA_POR_EL_USUARIO = "❌ Operación cancelada por el usuario.";

    /***
     * Mensaje de envío actualizado correctamente.
     */
    public static final String ENVIO_ACTUALIZADO_CORRECTAMENTE = "✅ Envío actualizado de Tracking %s actualizado correctamente.";

    private final GenericEnviosService<Envio, Pedido> envioService;
    private final GenericPedidosService<Pedido> pedidoService;
    private final InputReader input;

    /***
     * Constructor del controlador de consola para envíos.
     *
     * @param envioService Servicio genérico de envíos.
     * @param pedidoService Servicio genérico de pedidos.
     * @param input Lector de entrada de consola.
     */
    public EnvioConsoleController(GenericEnviosService<Envio, Pedido> envioService,
                                  GenericPedidosService<Pedido> pedidoService,
                                  InputReader input) {
        this.envioService = envioService;
        this.pedidoService = pedidoService;
        this.input = input;
    }

    /***
     * Crea un nuevo envío y lo asigna a un pedido existente.
     */
    public void crear() {
        ConsoleUtils.imprimirDivisores("CREAR NUEVO ENVÍO POR NUMERO PEDIDO");
        try {
            Pedido pedido = getPedido();
            if (pedido == null) {
                return;
            }

            Envio envio = crearEnvioEnMemoria();

            //transaccional para crear envio y actualizar pedido
            String numeroEnvio = envioService.crearEnvioYActualizarPedido(envio, pedido);

            ConsoleUtils.imprimirMensaje("✅ Envío Tracking Nro: " + numeroEnvio + " creado exitosamente.");
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al crear envío: " + e.getMessage());
        }
    }

    /***
     * Obtiene un pedido válido para asignar un envío.
     *
     * @return Pedido válido o null si la operación fue cancelada.
     */
    private Pedido getPedido() {
        Pedido pedido;
        while (true) {
            String numeroPedido = input.leerNumeroPedido(
                    "Ingrese el Numero del Pedido (PED-XXXXXXXX) al que asignar el envío o 'q' para salir: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                ConsoleUtils.imprimirError(OPERACION_CANCELADA_POR_EL_USUARIO);
                return null;
            }
            pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);
            if (pedido == null) {
                ConsoleUtils.imprimirError("❌ El pedido con número " + numeroPedido + " no existe. Intente nuevamente.");
            } else if (pedido.getEnvio() != null) {
                ConsoleUtils.imprimirError("❌ El pedido con número " + numeroPedido + " ya tiene un envío asignado. Intente nuevamente.");
            } else {
                break;
            }
        }
        return pedido;
    }

    /***
     * Lista todos los envíos con paginación.
     */
    public void listar() {
        ConsoleUtils.imprimirDivisores("LISTA DE ENVÍOS");
        try {
            Long total = envioService.obtenerCantidadTotalDeEnvios();
            ConsoleUtils.imprimirInfo("Total de envíos registrados: " + total);

            Paginador<Envio> paginador = new Paginador<>(50L, input);
            paginador.paginar(
                    (pageSize, page) -> {
                        try {
                            return envioService.buscarTodos(pageSize, page);
                        } catch (Exception e) {
                            ConsoleUtils.imprimirError("❌ Error al obtener envíos: " + e.getMessage());
                            return List.of();
                        }
                    },
                    lista -> {
                        ConsoleUtils.imprimirLineaVacia();
                        EnvioPrinter.mostrarCabecera();
                        lista.forEach(EnvioPrinter::mostrarDetalle);
                    },
                    total
            );
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al listar envíos: " + e.getMessage());
        }
    }

    /***
     * Busca un envío por su número de tracking.
     */
    public void buscarPorTracking() {
        ConsoleUtils.imprimirDivisores("BUSCAR ENVIO POR TRACKING");
        try {
            Envio envio = envioService.buscarPorTracking(input.leerStringObligatorio("Ingrese Tracking de pedido: ", "Tracking"));
            EnvioPrinter.mostrarCabecera();
            EnvioPrinter.mostrarDetalle(envio);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar por Tracking: " + e.getMessage());
        }
    }

    /***
     * Busca un envío por su número de pedido.
     */
    public void buscarPorNumeroPedido() {
        ConsoleUtils.imprimirDivisores("BUSCAR ENVIO POR NUMERO PEDIDO");
        try {
            String numeroPedido = input.leerNumeroPedido("Ingrese Numero de pedido (PED-XXXXXXXX) o 'q' para salir: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                ConsoleUtils.imprimirError(OPERACION_CANCELADA_POR_EL_USUARIO);
                return;
            }
            Envio envio = envioService.buscarPorNumeroPedido(numeroPedido);

            EnvioPrinter.mostrarCabecera();
            EnvioPrinter.mostrarDetalle(envio);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar por Numero Pedido: " + e.getMessage());
        }
    }

    /***
     * Busca un envío por su ID.
     */
    public void buscarPorId() {
        ConsoleUtils.imprimirDivisores("BUSCAR ENVIO POR ID");
        try {
            Envio envio = envioService.buscarPorId(input.leerLong("Ingrese ID de pedido: "));
            EnvioPrinter.mostrarCabecera();
            EnvioPrinter.mostrarDetalle(envio);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar por ID: " + e.getMessage());
        }
    }

    /***
     * Actualiza el estado de un envío buscado por su número de tracking.
     */
    public void actualizarEstadoPorTracking() {
        ConsoleUtils.imprimirDivisores("ACTUALIZAR ESTADO DE ENVÍO POR TRACKING");
        Envio envio;
        try {
            envio = envioService.buscarPorTracking(input.leerStringObligatorio("Ingrese Tracking del envío a modificar: ", "Tracking"));
            actualizarEstadoEnvio(envio);
            if (envio == null) {
                return;
            }
            ConsoleUtils.imprimirMensaje(String.format(ENVIO_ACTUALIZADO_CORRECTAMENTE, envio.getTracking()));
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar envío por Tracking: " + e.getMessage());
        }
    }

    /***
     * Actualiza el estado de un envío buscado por su ID.
     */
    public void actualizarEstadoEnvioPorId() {
        ConsoleUtils.imprimirDivisores("ACTUALIZAR ESTADO DE ENVÍO POR ID");
        Envio envio;
        try {
            envio = envioService.buscarPorId(input.leerLong("Ingrese ID del envío a modificar: "));
            actualizarEstadoEnvio(envio);
            if (envio == null) {
                return;
            }
            ConsoleUtils.imprimirMensaje(String.format(ENVIO_ACTUALIZADO_CORRECTAMENTE, envio.getTracking()));
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar envío por ID: " + e.getMessage());
        }
    }

    /***
     * Actualiza el estado de un envío buscado por el número de pedido asociado.
     */
    public void actualizarEstadoEnvioPorNumeroPedido() {
        ConsoleUtils.imprimirDivisores("ACTUALIZAR ESTADO DE ENVÍO POR NÚMERO DE PEDIDO");
        Envio envio;
        try {
            String numeroPedido = input.leerNumeroPedido("Ingrese el NÚMERO de PEDIDO: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                ConsoleUtils.imprimirError(OPERACION_CANCELADA_POR_EL_USUARIO);
                return;
            }
            Pedido pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);

            if (pedido == null) {
                ConsoleUtils.imprimirError("❌ Pedido no encontrado.");
                return;
            } else if (pedido.getEnvio() == null) {
                ConsoleUtils.imprimirInfo("⚠ Este pedido (" + pedido.getNumero() + ") NO tiene envío asociado.");
                ConsoleUtils.imprimirInfo("Use la opción 'Actualizar Pedido' para asignarle uno nuevo.");
                return;
            }

            envio = pedido.getEnvio();
            actualizarEstadoEnvio(envio);
            if (envio == null) {
                return;
            }
            ConsoleUtils.imprimirMensaje(String.format(ENVIO_ACTUALIZADO_CORRECTAMENTE, envio.getTracking()));
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al buscar envío: " + e.getMessage());
        }
    }

    /***
     * Actualiza el estado de un envío.
     *
     * @param envio Envío a actualizar.
     */
    private void actualizarEstadoEnvio(Envio envio) {
        if (envio == null) {
            ConsoleUtils.imprimirError("❌ Envío no encontrado.");
            return;
        } else if (envio.getEstado().equals(EstadoEnvio.ENTREGADO)) {
            ConsoleUtils.imprimirError("❌ El envío ya está en estado ENTREGADO y no puede ser modificado.");
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

    /***
     * Elimina un envío buscado por su número de tracking.
     */
    public void eliminarEnvioPorTracking() {
        ConsoleUtils.imprimirDivisores("ELIMINAR ENVÍO POR TRACKING");
        try {
            Envio envio = envioService.buscarPorTracking(input.leerStringObligatorio("Ingrese Tracking del envío a modificar: ", "Tracking"));
            eliminar(envio);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    /***
     * Elimina un envío buscado por el número de pedido asociado.
     */
    public void eliminarEnvioPorNumeroPedido() {
        ConsoleUtils.imprimirDivisores("ELIMINAR ENVÍO POR NÚMERO DE PEDIDO");
        try {
            String numeroPedido = input.leerNumeroPedido("Ingrese el NÚMERO de PEDIDO: ");
            if (numeroPedido.equalsIgnoreCase("q")) {
                ConsoleUtils.imprimirError(OPERACION_CANCELADA_POR_EL_USUARIO);
                return;
            }
            Envio envio = envioService.buscarPorNumeroPedido(numeroPedido);

            eliminar(envio);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al actualizar envío: " + e.getMessage());
        }
    }

    /***
     * Elimina un envío buscado por su ID.
     */
    public void eliminarEnvioPorId() {
        ConsoleUtils.imprimirDivisores("ELIMINAR ENVÍO POR ID");
        try {
            Envio envio = envioService.buscarPorId(input.leerLong("Ingrese ID del envío a eliminar: "));
            eliminar(envio);
        } catch (Exception e) {
            ConsoleUtils.imprimirError("❌ Error al eliminar: " + e.getMessage());
        }
    }

    /***
     * Elimina un envío si no ha sido entregado.
     *
     * @param envio Envío a eliminar.
     */
    private void eliminar(Envio envio) {
        if (envio == null) {
            ConsoleUtils.imprimirError("❌ El envío no existe.");
            return;
        } else if (envio.getEstado() == EstadoEnvio.ENTREGADO) {
            ConsoleUtils.imprimirError("❌ No se puede eliminar un envío que ya ha sido entregado.");
            return;
        }

        ConsoleUtils.imprimirMensaje("¿Está seguro que desea eliminar el envío " + envio.getTracking() + "? (s/n): ");
        if (input.nextLine().trim().equalsIgnoreCase("s")) {

            try {
                // Llamada real al Service
                envioService.eliminar(envio);
                ConsoleUtils.imprimirMensaje("✅ Envío eliminado.");
            } catch (Exception e) {
                ConsoleUtils.imprimirError("❌ Error al eliminar envío: " + e.getMessage());
            }
        } else {
            ConsoleUtils.imprimirError("❌ Operación cancelada.");
        }
    }

    /***
     * Crea un nuevo envío en memoria solicitando los datos al usuario.
     *
     * @return Envío creado en memoria.
     */
    private Envio crearEnvioEnMemoria() {
        ConsoleUtils.imprimirAdvertencia("\n... Configurando datos del Envío ...");
        Envio envio = new Envio();

        envio.setCosto(input.leerDouble("Costo del envío: "));
        envio.setFechaDespacho(LocalDate.now());
        envio.setFechaEstimada(envio.getFechaDespacho().plusDays(5));
        envio.setEmpresa(elegirEmpresaEnvio());
        envio.setTipo(elegirTipoEnvio());
        envio.setEstado(EstadoEnvio.EN_PREPARACION);
        envio.setEliminado(false);

        return envio;
    }

    /***
     * Solicita al usuario que elija una empresa de envío.
     *
     * @return Empresa de envío seleccionada.
     */
    private EmpresaEnvio elegirEmpresaEnvio() {
        ConsoleUtils.imprimirMensaje("Seleccione Empresa de Envío:");
        EmpresaEnvio[] valores = EmpresaEnvio.values();
        input.mostrarOpcionesEnum(valores);
        return valores[input.leerOpcionEnum(valores.length) - 1];
    }

    /***
     * Solicita al usuario que elija un tipo de envío.
     *
     * @return Tipo de envío seleccionado.
     */
    private TipoEnvio elegirTipoEnvio() {
        ConsoleUtils.imprimirMensaje("Seleccione Tipo de Envío:");
        TipoEnvio[] valores = TipoEnvio.values();
        input.mostrarOpcionesEnum(valores);
        return valores[input.leerOpcionEnum(valores.length) - 1];
    }

    /***
     * Solicita al usuario que elija un estado de envío.
     *
     * @return Estado de envío seleccionado.
     */
    private EstadoEnvio elegirEstadoEnvio() {
        ConsoleUtils.imprimirMensaje("Seleccione Estado del Envío:");
        EstadoEnvio[] valores = EstadoEnvio.values();
        input.mostrarOpcionesEnum(valores);
        return valores[input.leerOpcionEnum(valores.length) - 1];
    }
}