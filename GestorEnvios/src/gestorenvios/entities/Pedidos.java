package gestorenvios.entities;

import java.time.LocalDate;

/**
 * Entidad que representa un Pedido realizado por un cliente.
 * <p>
 * Campos (según consigna):
 * - id: Long (PK)
 * - eliminado: Boolean (baja lógica)
 * - numero: String (NOT NULL, UNIQUE, máx. 20)
 * - fecha: LocalDate (NOT NULL)
 * - clienteNombre: String (NOT NULL, máx. 120)
 * - total: double (12,2) NOT NULL
 * - estado: EstadoPedido (NUEVO, FACTURADO, ENVIADO)
 * - envio: referencia 1→1 a Envios
 */
public class Pedidos {

    private Long id;
    private Boolean eliminado;

    private String numero;
    private LocalDate fecha;
    private String clienteNombre;
    private double total;
    private EstadoPedido estado;
    private Envios envio; // Relación 1→1

    public Pedidos() {
    }

    public Pedidos(Long id,
                   Boolean eliminado,
                   String numero,
                   LocalDate fecha,
                   String clienteNombre,
                   double total,
                   EstadoPedido estado,
                   Envios envio) {
        this.id = id;
        this.eliminado = eliminado;
        this.numero = numero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.total = total;
        this.estado = estado;
        this.envio = envio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Envios getEnvio() {
        return envio;
    }

    public void setEnvio(Envios envio) {
        this.envio = envio;
    }

    @Override
    public String toString() {
        return "Pedidos{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", fecha=" + fecha +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", total=" + total +
                ", estado=" + estado +
                ", envio=" + (envio != null ? envio.getTracking() : "SIN_ENVIO") +
                '}';
    }
}
