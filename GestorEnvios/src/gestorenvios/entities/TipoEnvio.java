package gestorenvios.entities;

/**
 * Tipo de envío según el servicio.
 */
public enum TipoEnvio {
    ESTANDAR(1),
    EXPRES(2);

    //Agrego el campo para guardar el ID
    private final int id;

    // constructor
    TipoEnvio(int id) {
        this.id = id;
    }

    // Agrego el getter para el ID para guardar en la BD
    public int getId() {
        return id;
    }

    // Agrego estático para leer de la BD
    public static TipoEnvio fromId(int id) {
        for (TipoEnvio tipo : values()) {
            if (tipo.id == id) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("ID de tipo de envío inválido: " + id);
    }
}
