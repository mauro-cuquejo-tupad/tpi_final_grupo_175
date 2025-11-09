package gestorenvios.entities;

/**
 * Estado del proceso de envío.
 */
public enum EstadoEnvio {
    EN_PREPARACION(1),
    EN_TRANSITO(2),
    ENTREGADO(3);

    //Agrego campo para guardar el ID
    private final int id;

    //constructor
    EstadoEnvio(int id) {
        this.id = id;
    }

    // Agrego getter para ID para guardar en la BD
    public int getId() {
        return id;
    }

    // Agrego estático para leer de la BD
    public static EstadoEnvio fromId(int id) {
        for (EstadoEnvio estado : values()) {
            if (estado.id == id) {
                return estado;
            }
        }
        throw new IllegalArgumentException("ID de estado de envío inválido: " + id);
    }
}
