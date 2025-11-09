package gestorenvios.entities;

/**
 * Empresas habilitadas para realizar el envío.
 */
public enum EmpresaEnvio {
    CORREO_ARGENTINO(1),
    ANDREANI(2),
    OCA(3);

    private final int id;

    // Constructor
    EmpresaEnvio(int id) {
        this.id = id;
    }

    // getter
    public int getId() {
        return id;
    }

    /**
     * método para convertir el INT de la BD a un ENUM.
     * @param id El ID de la base de datos.
     * @return El enum correspondiente.
     */
    public static EmpresaEnvio fromId(int id) {
        for (EmpresaEnvio empresa : values()) {
            if (empresa.id == id) {
                return empresa;
            }
        }
        throw new IllegalArgumentException("ID de empresa de envío inválido: " + id);
    }
}

