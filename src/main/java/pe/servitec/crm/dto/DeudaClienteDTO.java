package pe.servitec.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DeudaClienteDTO {

	private String numeroDocumento;
	private String razonSocial;
	private String telefono;
	private Long cuotasPendientes;
	private BigDecimal montoPendiente;
	private String proximoVencimiento;

	public DeudaClienteDTO(String numeroDocumento, String razonSocial, String telefono, Long cuotasPendientes,
			BigDecimal montoPendiente, LocalDate proximoVencimiento) {
		this.numeroDocumento = numeroDocumento;
		this.razonSocial = razonSocial;
		this.telefono = telefono;
		this.cuotasPendientes = cuotasPendientes;
		this.montoPendiente = montoPendiente;
		this.proximoVencimiento = proximoVencimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public String getTelefono() {
		return telefono;
	}

	public Long getCuotasPendientes() {
		return cuotasPendientes;
	}

	public BigDecimal getMontoPendiente() {
		return montoPendiente;
	}

	public String getProximoVencimiento() {
		return proximoVencimiento;
	}

}
