package pe.servitec.crm.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.servitec.crm.model.Cuota;
import pe.servitec.crm.repository.ClienteRepository;
import pe.servitec.crm.repository.ContratoRepository;
import pe.servitec.crm.repository.CuotaRepository;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

	private final ClienteRepository clienteRepository;
	private final ContratoRepository contratoRepository;
	private final CuotaRepository cuotaRepository;

	public ConsultaController(ClienteRepository clienteRepository, ContratoRepository contratoRepository,
			CuotaRepository cuotaRepository) {
		this.clienteRepository = clienteRepository;
		this.contratoRepository = contratoRepository;
		this.cuotaRepository = cuotaRepository;
	}

	@GetMapping("/estado-cuenta")
	public String estadoCuenta(@RequestParam(required = false) Integer idCliente, Model model) {
		model.addAttribute("clientes", clienteRepository.buscar(""));
		if (idCliente != null) {
			clienteRepository.findById(idCliente).ifPresent(cliente -> {
				List<Cuota> pendientes = cuotaRepository.listarPendientesPorCliente(idCliente);
				BigDecimal deuda = pendientes.stream().map(Cuota::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
				BigDecimal vencido = pendientes.stream().filter(Cuota::isVencida).map(Cuota::getMonto)
						.reduce(BigDecimal.ZERO, BigDecimal::add);
				model.addAttribute("cliente", cliente);
				model.addAttribute("contratos", contratoRepository.findByClienteIdClienteOrderByIdContratoDesc(idCliente));
				model.addAttribute("pendientes", pendientes);
				model.addAttribute("deudaTotal", deuda);
				model.addAttribute("deudaVencida", vencido);
			});
		}
		model.addAttribute("idCliente", idCliente);
		return "consultas/estado-cuenta";
	}

	@GetMapping("/cobranzas")
	public String cobranzas(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
			@RequestParam(defaultValue = "PENDIENTE") String estado, Model model) {
		LocalDate hoy = LocalDate.now();
		if (desde == null) {
			desde = hoy.withDayOfMonth(1);
		}
		if (hasta == null) {
			hasta = hoy.withDayOfMonth(hoy.lengthOfMonth());
		}
		List<Cuota> cuotas = cuotaRepository.listarPorRangoFechas(desde, hasta, estado);
		model.addAttribute("cuotas", cuotas);
		model.addAttribute("total", cuotas.stream().map(Cuota::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add));
		model.addAttribute("desde", desde);
		model.addAttribute("hasta", hasta);
		model.addAttribute("estado", estado);
		return "consultas/cobranzas";
	}

}
