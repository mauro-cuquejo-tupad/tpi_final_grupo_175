package gestorenvios.entities;

/***
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

    /***
     * Obtiene el estado de pedido correspondiente al ID proporcionado.
     *
     * @param id El ID del estado de pedido.
     * @return El estado de pedido correspondiente.
     * @throws IllegalArgumentException Si el ID no corresponde a ningún estado válido.
     */
    public static EstadoPedido fromId(int id) {
        for (EstadoPedido estado : values()) {
            if (estado.id == id) {
                return estado;
            }
        }
        throw new IllegalArgumentException("ID de estado de pedido inválido: " + id);
    }
}