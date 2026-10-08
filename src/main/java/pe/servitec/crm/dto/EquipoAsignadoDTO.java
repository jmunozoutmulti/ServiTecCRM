package pe.servitec.crm.dto;

import java.math.BigDecimal;

public class EquipoAsignadoDTO {

	private String razonSocial;
	private Integer idContrato;
	private String codigo;
	private String producto;
	private String tipo;
	private Integer cantidad;
	private BigDecimal precioMensual;
	private BigDecimal subtotal;

	public EquipoAsignadoDTO(String razonSocial, Integer idContrato, String codigo, String producto, String tipo,
			Integer cantidad, BigDecimal precioMensual, BigDecimal subtotal) {
		this.razonSocial = razonSocial;
		this.idContrato = idContrato;
		this.codigo = codigo;
		this.producto = producto;
		this.tipo = tipo;
		this.cantidad = cantidad;
		this.precioMensual = precioMensual;
		this.subtotal = subtotal;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public Integer getIdContrato() {
		return idContrato;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getProducto() {
		return producto;
	}

	public String getTipo() {
		return tipo;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public BigDecimal getPrecioMensual() {
		return precioMensual;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

}
