package gestorenvios.entities;

/**
 * Tipo de envío según el servicio.
 */
public enum TipoEnvio {
    ESTANDAR(1),
    EXPRES(2);

    private final int id;

    TipoEnvio(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TipoEnvio fromId(int id) {
        for (TipoEnvio tipo : values()) {
            if (tipo.id == id) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("ID de tipo de envío inválido: " + id);
    }
}
