package pe.servitec.crm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.servitec.crm.model.Cliente;
import pe.servitec.crm.model.Contrato;
import pe.servitec.crm.model.ContratoDetalle;
import pe.servitec.crm.model.Cuota;
import pe.servitec.crm.model.Producto;
import pe.servitec.crm.model.Usuario;
import pe.servitec.crm.repository.ClienteRepository;
import pe.servitec.crm.repository.ContratoRepository;
import pe.servitec.crm.repository.CuotaRepository;
import pe.servitec.crm.repository.ProductoRepository;

@Service
public class ContratoService {

	private final ContratoRepository contratoRepository;
	private final ClienteRepository clienteRepository;
	private final ProductoRepository productoRepository;
	private final CuotaRepository cuotaRepository;

	public ContratoService(ContratoRepository contratoRepository, ClienteRepository clienteRepository,
			ProductoRepository productoRepository, CuotaRepository cuotaRepository) {
		this.contratoRepository = contratoRepository;
		this.clienteRepository = clienteRepository;
		this.productoRepository = productoRepository;
		this.cuotaRepository = cuotaRepository;
	}

	@Transactional
	public Contrato registrar(Integer idCliente, LocalDate fechaInicio, Integer meses, String observacion,
			List<Integer> productos, List<Integer> cantidades, Usuario usuario) {
		Cliente cliente = clienteRepository.findById(idCliente)
				.orElseThrow(() -> new IllegalArgumentException("El cliente seleccionado no existe"));
		if (!"ACTIVO".equals(cliente.getEstado())) {
			throw new IllegalArgumentException("El cliente se encuentra inactivo");
		}
		if (fechaInicio == null) {
			throw new IllegalArgumentException("Debe indicar la fecha de inicio");
		}
		if (meses == null || meses < 1 || meses > 60) {
			throw new IllegalArgumentException("La duración debe estar entre 1 y 60 meses");
		}

		Contrato contrato = new Contrato();
		contrato.setCliente(cliente);
		contrato.setUsuario(usuario);
		contrato.setFechaRegistro(LocalDate.now());
		contrato.setFechaInicio(fechaInicio);
		contrato.setMeses(meses);
		contrato.setFechaFin(fechaInicio.plusMonths(meses).minusDays(1));
		contrato.setObservacion(observacion);
		contrato.setEstado("VIGENTE");

		BigDecimal montoMensual = BigDecimal.ZERO;
		for (int i = 0; i < productos.size(); i++) {
			Integer cantidad = i < cantidades.size() ? cantidades.get(i) : null;
			if (cantidad == null || cantidad <= 0) {
				continue;
			}
			Producto producto = productoRepository.findById(productos.get(i))
					.orElseThrow(() -> new IllegalArgumentException("Uno de los productos no existe"));
			if (producto.esEquipo()) {
				if (producto.getStock() < cantidad) {
					throw new IllegalStateException("Stock insuficiente para " + producto.getNombre()
							+ ". Disponible: " + producto.getStock());
				}
				producto.setStock(producto.getStock() - cantidad);
			}
			ContratoDetalle detalle = new ContratoDetalle();
			detalle.setProducto(producto);
			detalle.setCantidad(cantidad);
			detalle.setPrecioMensual(producto.getPrecioMensual());
			detalle.setSubtotal(producto.getPrecioMensual().multiply(BigDecimal.valueOf(cantidad)));
			contrato.agregarDetalle(detalle);
			montoMensual = montoMensual.add(detalle.getSubtotal());
		}

		if (contrato.getDetalles().isEmpty()) {
			throw new IllegalArgumentException("Debe asignar al menos un equipo o servicio");
		}

		contrato.setMontoMensual(montoMensual);
		for (Cuota cuota : generarCuotas(fechaInicio, meses, montoMensual)) {
			contrato.agregarCuota(cuota);
		}
		return contratoRepository.save(contrato);
	}

	public static List<Cuota> generarCuotas(LocalDate fechaInicio, int meses, BigDecimal montoMensual) {
		List<Cuota> cuotas = new ArrayList<>();
		for (int i = 1; i <= meses; i++) {
			Cuota cuota = new Cuota();
			cuota.setNumero(i);
			cuota.setFechaVencimiento(fechaInicio.plusMonths(i));
			cuota.setMonto(montoMensual);
			cuota.setEstado("PENDIENTE");
			cuotas.add(cuota);
		}
		return cuotas;
	}

	@Transactional
	public void anular(Integer idContrato) {
		Contrato contrato = contratoRepository.findById(idContrato)
				.orElseThrow(() -> new IllegalArgumentException("El contrato no existe"));
		if (!"VIGENTE".equals(contrato.getEstado())) {
			throw new IllegalStateException("Solo se pueden anular contratos vigentes");
		}
		for (ContratoDetalle detalle : contrato.getDetalles()) {
			Producto producto = detalle.getProducto();
			if (producto.esEquipo()) {
				producto.setStock(producto.getStock() + detalle.getCantidad());
			}
		}
		for (Cuota cuota : contrato.getCuotas()) {
			if ("PENDIENTE".equals(cuota.getEstado())) {
				cuota.setEstado("ANULADA");
			}
		}
		contrato.setEstado("ANULADO");
	}

	@Transactional
	public Cuota pagarCuota(Integer idCuota) {
		Cuota cuota = cuotaRepository.findById(idCuota)
				.orElseThrow(() -> new IllegalArgumentException("La cuota no existe"));
		if (!"PENDIENTE".equals(cuota.getEstado())) {
			throw new IllegalStateException("La cuota ya no se encuentra pendiente");
		}
		cuota.setEstado("PAGADA");
		cuota.setFechaPago(LocalDate.now());
		Contrato contrato = cuota.getContrato();
		boolean sinPendientes = contrato.getCuotas().stream().noneMatch(c -> "PENDIENTE".equals(c.getEstado()));
		if (sinPendientes) {
			contrato.setEstado("FINALIZADO");
		}
		return cuota;
	}

}
