package gestorenvios.entities;

import java.time.LocalDate;

/***
 * Entidad que representa un envío asociado a un pedido.
 * <p>
 * Campos según consigna:
 * <ul>
 *   <li><b>id</b>: Long (PK)</li>
 *   <li><b>eliminado</b>: Boolean (baja lógica)</li>
 *   <li><b>tracking</b>: String (UNIQUE, máx. 40)</li>
 *   <li><b>empresa</b>: EmpresaEnvio (ANDREANI, OCA, CORREO_ARG)</li>
 *   <li><b>tipo</b>: TipoEnvio (ESTANDAR, EXPRES)</li>
 *   <li><b>costo</b>: double (10,2)</li>
 *   <li><b>fechaDespacho</b>: LocalDate</li>
 *   <li><b>fechaEstimada</b>: LocalDate</li>
 *   <li><b>estado</b>: EstadoEnvio (EN_PREPARACION, EN_TRANSITO, ENTREGADO)</li>
 * </ul>
 */

public class Envio {

    private Long id;
    private Boolean eliminado;

    private String tracking;
    private EmpresaEnvio empresa;
    private TipoEnvio tipo;
    private double costo;
    private LocalDate fechaDespacho;
    private LocalDate fechaEstimada;
    private EstadoEnvio estado;

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

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public EmpresaEnvio getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaEnvio empresa) {
        this.empresa = empresa;
    }

    public TipoEnvio getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnvio tipo) {
        this.tipo = tipo;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public LocalDate getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDate fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public LocalDate getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(LocalDate fechaEstimada) {
        this.fechaEstimada = fechaEstimada;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Envios{" +
                "id=" + id +
                ", tracking='" + tracking + '\'' +
                ", empresa=" + empresa +
                ", tipo=" + tipo +
                ", costo=" + costo +
                ", fechaDespacho=" + fechaDespacho +
                ", fechaEstimada=" + fechaEstimada +
                ", estado=" + estado +
                '}';
    }
}
