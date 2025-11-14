package gestorenvios.entities;

/**
 * Estado posible de un Pedido.
 */
public enum EstadoPedido {
    NUEVO(1),
    FACTURADO(2),
    ENVIADO(3);

    private final int id;

    EstadoPedido(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // Método estático para convertir el INT de la BD a un ENUM
    public static EstadoPedido fromId(int id) {
        for (EstadoPedido estado : values()) {
            if (estado.id == id) {
                return estado;
            }
        }

        throw new IllegalArgumentException("ID de estado de pedido inválido: " + id);
    }


}

