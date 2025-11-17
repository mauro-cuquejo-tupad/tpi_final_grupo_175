package gestorenvios.ui.console.controllers;

import gestorenvios.entities.Envio;
import gestorenvios.entities.Pedido;
import gestorenvios.services.GenericEnviosService;
import gestorenvios.services.GenericPedidosService;
import gestorenvios.ui.console.input.InputReader;

public class MenuHandler {

    private final PedidoConsoleController pedidoConsoleController;
    private final EnvioConsoleController envioConsoleController;

    public MenuHandler(GenericPedidosService<Pedido> pedidoService,
                       GenericEnviosService<Envio, Pedido> envioService,
                       InputReader input) {
        this.pedidoConsoleController = new PedidoConsoleController(pedidoService, input);
        this.envioConsoleController = new EnvioConsoleController(envioService, pedidoService, input);
    }

    public void crearPedido() {
        pedidoConsoleController.crear();
    }

    public void listarPedidos() {
        pedidoConsoleController.listar();
    }

    public void buscarPedidoPorNumero() {
        pedidoConsoleController.buscarPorNumero();
    }

    public void buscarPedidoPorTracking() {
        pedidoConsoleController.buscarPorTracking();
    }

    public void buscarPedidoPorId() {
        pedidoConsoleController.buscarPorId();
    }

    public void actualizarPedidoPorNumero() {
        pedidoConsoleController.actualizarPedido();
    }

    public void eliminarPedidoPorNumero() {
        pedidoConsoleController.eliminarPedidoPorNumero();
    }
    public void eliminarPedidoPorId() {
        pedidoConsoleController.eliminarPedidoPorId();
    }

    public void crearEnvio() {
        envioConsoleController.crear();
    }

    public void listarEnvios() {
        envioConsoleController.listar();
    }

    public void buscarEnvioPorTracking() {
        envioConsoleController.buscarPorTracking();
    }

    public void buscarEnvioPorNumeroPedido() {
        envioConsoleController.buscarPorNumeroPedido();
    }

    public void buscarEnvioPorId() {
        envioConsoleController.buscarPorId();
    }

    public void actualizarEstadoEnvioPorTracking() {
        envioConsoleController.actualizarEstadoPorTracking();
    }

    public void actualizarEstadoEnvioPorNumeroPedido() {
        envioConsoleController.actualizarEstadoEnvioPorNumeroPedido();
    }

    public void actualizarEstadoEnvioPorId() {
        envioConsoleController.actualizarEstadoEnvioPorId();
    }

    public void eliminarEnvioPorTracking() {
        envioConsoleController.eliminarEnvioPorTracking();
    }

    public void eliminarEnvioPorNumeroPedido() {
        envioConsoleController.eliminarEnvioPorNumeroPedido();
    }

    public void eliminarEnvioPorId() {
        envioConsoleController.eliminarEnvioPorId();
    }

    public void buscarPedidoPorCliente() {
        pedidoConsoleController.buscarPorCliente();
    }
}

