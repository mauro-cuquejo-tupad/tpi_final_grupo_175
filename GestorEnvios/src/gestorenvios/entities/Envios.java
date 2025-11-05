package gestorenvios.entities;

import java.time.LocalDate;

/**
 * Entidad que representa un Envío asociado a un Pedido.
 *
 * Campos (según consigna):
 * - id: Long (PK)
 * - eliminado: Boolean (baja lógica)
 * - tracking: String (UNIQUE, máx. 40)
 * - empresa: EmpresaEnvio (ANDREANI, OCA, CORREO_ARG)
 * - tipo: TipoEnvio (ESTANDAR, EXPRES)
 * - costo: double (10,2)
 * - fechaDespacho: LocalDate
 * - fechaEstimada: LocalDate
 * - estado: EstadoEnvio (EN_PREPARACION, EN_TRANSITO, ENTREGADO)
 */
public class Envios {

    private Long id;
    private Boolean eliminado;

    private String tracking;
    private EmpresaEnvio empresa;
    private TipoEnvio tipo;
    private double costo;
    private LocalDate fechaDespacho;
    private LocalDate fechaEstimada;
    private EstadoEnvio estado;

    public Envios() {
    }

    public Envios(Long id,
                  Boolean eliminado,
                  String tracking,
                  EmpresaEnvio empresa,
                  TipoEnvio tipo,
                  double costo,
                  LocalDate fechaDespacho,
                  LocalDate fechaEstimada,
                  EstadoEnvio estado) {
        this.id = id;
        this.eliminado = eliminado;
        this.tracking = tracking;
        this.empresa = empresa;
        this.tipo = tipo;
        this.costo = costo;
        this.fechaDespacho = fechaDespacho;
        this.fechaEstimada = fechaEstimada;
        this.estado = estado;
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
