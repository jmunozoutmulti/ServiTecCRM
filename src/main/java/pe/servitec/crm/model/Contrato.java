package pe.servitec.crm.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "contrato")
public class Contrato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_contrato")
	private Integer idContrato;

	@ManyToOne
	@JoinColumn(name = "id_cliente", nullable = false)
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;

	@Column(name = "fecha_registro", nullable = false)
	private LocalDate fechaRegistro;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@Column(nullable = false)
	private Integer meses;

	@Column(name = "fecha_fin", nullable = false)
	private LocalDate fechaFin;

	@Column(name = "monto_mensual", nullable = false, precision = 10, scale = 2)
	private BigDecimal montoMensual;

	@Column(nullable = false, length = 12)
	private String estado = "VIGENTE";

	@Column(length = 200)
	private String observacion;

	@OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
	private List<ContratoDetalle> detalles = new ArrayList<>();

	@OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
	@OrderBy("numero")
	private List<Cuota> cuotas = new ArrayList<>();

	public void agregarDetalle(ContratoDetalle detalle) {
		detalle.setContrato(this);
		detalles.add(detalle);
	}

	public void agregarCuota(Cuota cuota) {
		cuota.setContrato(this);
		cuotas.add(cuota);
	}

	public Integer getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(Integer idContrato) {
		this.idContrato = idContrato;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Integer getMeses() {
		return meses;
	}

	public void setMeses(Integer meses) {
		this.meses = meses;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public BigDecimal getMontoMensual() {
		return montoMensual;
	}

	public void setMontoMensual(BigDecimal montoMensual) {
		this.montoMensual = montoMensual;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public List<ContratoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<ContratoDetalle> detalles) {
		this.detalles = detalles;
	}

	public List<Cuota> getCuotas() {
		return cuotas;
	}

	public void setCuotas(List<Cuota> cuotas) {
		this.cuotas = cuotas;
	}

}
