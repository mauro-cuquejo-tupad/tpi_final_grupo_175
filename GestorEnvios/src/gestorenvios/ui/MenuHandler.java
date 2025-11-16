package gestorenvios.ui;

import gestorenvios.controllers.EnvioController;
import gestorenvios.controllers.PedidoController;
import gestorenvios.entities.Envio;
import gestorenvios.entities.Pedido;
import gestorenvios.services.GenericEnviosService;
import gestorenvios.services.GenericPedidosService;

public class MenuHandler {

    private final PedidoController pedidoController;
    private final EnvioController envioController;

    public MenuHandler(GenericPedidosService<Pedido> pedidoService,
                       GenericEnviosService<Envio, Pedido> envioService,
                       InputReader input) {
        this.pedidoController = new PedidoController(pedidoService, input);
        this.envioController = new EnvioController(envioService, pedidoService, input);
    }

    public void crearPedido() {
        pedidoController.crear();
    }

    public void listarPedidos() {
        pedidoController.listar();
    }

    public void buscarPedidoPorNumero() {
        pedidoController.buscarPorNumero();
    }

    public void buscarPedidoPorTracking() {
        pedidoController.buscarPorTracking();
    }

    public void buscarPedidoPorId() {
        pedidoController.buscarPorId();
    }

    public void actualizarPedidoPorNumero() {
        pedidoController.actualizarPedidoPorNumero();
    }

    public void actualizarPedidoPorId() {
        pedidoController.actualizarPedidoPorId();
    }

    public void eliminarPedidoPorNumero() {
        pedidoController.eliminarPedidoPorNumero();
    }

    public void eliminarEnvioDePedido() {
        pedidoController.eliminarEnvioDePedido();
    }

    public void eliminarPedidoPorId() {
        pedidoController.eliminarPedidoPorId();
    }

    public void crearEnvio() {
        envioController.crear();
    }

    public void listarEnvios() {
        envioController.listar();
    }

    public void buscarEnvioPorTracking() {
        envioController.buscarPorTracking();
    }

    public void buscarEnvioPorNumeroPedido() {
        envioController.buscarPorNumeroPedido();
    }

    public void buscarEnvioPorId() {
        envioController.buscarPorId();
    }

    public void actualizarEnvioPorTracking() {
        envioController.actualizarPorTracking();
    }

    public void actualizarEnvioPorNumeroPedido() {
        envioController.actualizarEnvioPorNumeroPedido();
    }

    public void actualizarEnvioPorId() {
        envioController.actualizarEnvioPorId();
    }

    public void eliminarEnvioPorTracking() {
        envioController.eliminarEnvioPorTracking();
    }

    public void eliminarEnvioPorNumeroPedido() {
        envioController.eliminarEnvioPorNumeroPedido();
    }

    public void eliminarEnvioPorId() {
        envioController.eliminarEnvioPorId();
    }
}

