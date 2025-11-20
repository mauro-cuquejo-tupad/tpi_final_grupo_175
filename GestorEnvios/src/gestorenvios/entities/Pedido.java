package gestorenvios.entities;

import java.time.LocalDate;

/***
 * Entidad que representa un pedido realizado por un cliente.
 * <p>
 * Campos según consigna:
 * <ul>
 *   <li><b>id</b>: Long (PK)</li>
 *   <li><b>eliminado</b>: Boolean (baja lógica)</li>
 *   <li><b>numero</b>: String (NOT NULL, UNIQUE, máx. 20)</li>
 *   <li><b>fecha</b>: LocalDate (NOT NULL)</li>
 *   <li><b>clienteNombre</b>: String (NOT NULL, máx. 120)</li>
 *   <li><b>total</b>: double (12,2) NOT NULL</li>
 *   <li><b>estado</b>: EstadoPedido (NUEVO, FACTURADO, ENVIADO)</li>
 *   <li><b>envio</b>: referencia 1→1 a Envios</li>
 * </ul>
 */
public class Pedido {

    private Long id;
    private Boolean eliminado;

    private String numero;
    private LocalDate fecha;
    private String clienteNombre;
    private Double total;
    private EstadoPedido estado;
    private Envio envio; // Relación 1 a 1 con Envio

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

    public Double getTotal() {
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

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
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
