package gestorenvios.entities;

/***
 * Estado del proceso de envío.
 */
public enum EstadoEnvio {
    EN_PREPARACION(1),
    EN_TRANSITO(2),
    ENTREGADO(3);

    private final int id;

    EstadoEnvio(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /***
     * Obtiene el estado de envío a partir de su ID.
     *
     * @param id ID del estado de envío.
     * @return EstadoEnvio correspondiente al ID.
     * @throws IllegalArgumentException si el ID no corresponde a ningún estado.
     */
    public static EstadoEnvio fromId(int id) {
        for (EstadoEnvio estado : values()) {
            if (estado.id == id) {
                return estado;
            }
        }
        throw new IllegalArgumentException("ID de estado de envío inválido: " + id);
    }
}