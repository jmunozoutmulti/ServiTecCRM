package pe.servitec.crm.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuota")
public class Cuota {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cuota")
	private Integer idCuota;

	@ManyToOne
	@JoinColumn(name = "id_contrato", nullable = false)
	private Contrato contrato;

	@Column(nullable = false)
	private Integer numero;

	@Column(name = "fecha_vencimiento", nullable = false)
	private LocalDate fechaVencimiento;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal monto;

	@Column(nullable = false, length = 10)
	private String estado = "PENDIENTE";

	@Column(name = "fecha_pago")
	private LocalDate fechaPago;

	public boolean isVencida() {
		return "PENDIENTE".equals(estado) && fechaVencimiento.isBefore(LocalDate.now());
	}

	public Integer getIdCuota() {
		return idCuota;
	}

	public void setIdCuota(Integer idCuota) {
		this.idCuota = idCuota;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public LocalDate getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(LocalDate fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public LocalDate getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(LocalDate fechaPago) {
		this.fechaPago = fechaPago;
	}

}
