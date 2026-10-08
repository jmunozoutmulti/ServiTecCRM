package pe.servitec.crm.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import pe.servitec.crm.dto.DeudaClienteDTO;
import pe.servitec.crm.dto.EquipoAsignadoDTO;
import pe.servitec.crm.model.Usuario;
import pe.servitec.crm.repository.ContratoDetalleRepository;
import pe.servitec.crm.repository.CuotaRepository;
import pe.servitec.crm.service.ReporteService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

	private final ReporteService reporteService;
	private final CuotaRepository cuotaRepository;
	private final ContratoDetalleRepository detalleRepository;

	public ReporteController(ReporteService reporteService, CuotaRepository cuotaRepository,
			ContratoDetalleRepository detalleRepository) {
		this.reporteService = reporteService;
		this.cuotaRepository = cuotaRepository;
		this.detalleRepository = detalleRepository;
	}

	@GetMapping
	public String index() {
		return "reportes/index";
	}

	@GetMapping("/deudas")
	public ResponseEntity<byte[]> deudas(HttpSession session) throws Exception {
		List<DeudaClienteDTO> datos = cuotaRepository.listarDeudaPorCliente();
		Map<String, Object> parametros = parametrosBase(session);
		parametros.put("TOTAL_DEUDA", datos.stream().map(DeudaClienteDTO::getMontoPendiente).reduce(BigDecimal.ZERO, BigDecimal::add));
		return pdf(reporteService.generarPdf("deudas_clientes", parametros, datos), "deudas_clientes.pdf");
	}

	@GetMapping("/equipos")
	public ResponseEntity<byte[]> equipos(HttpSession session) throws Exception {
		List<EquipoAsignadoDTO> datos = detalleRepository.listarEquiposAsignados();
		Map<String, Object> parametros = parametrosBase(session);
		parametros.put("TOTAL_MENSUAL", datos.stream().map(EquipoAsignadoDTO::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
		return pdf(reporteService.generarPdf("equipos_asignados", parametros, datos), "equipos_asignados.pdf");
	}

	private Map<String, Object> parametrosBase(HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("EMPRESA", "ServiTec Lima S.A.C.");
		parametros.put("USUARIO", usuario.getNombreCompleto());
		parametros.put("FECHA", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
		return parametros;
	}

	private ResponseEntity<byte[]> pdf(byte[] contenido, String nombreArchivo) {
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + nombreArchivo)
				.contentType(MediaType.APPLICATION_PDF)
				.body(contenido);
	}

}
