package pe.servitec.crm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.servitec.crm.model.Contrato;
import pe.servitec.crm.model.Cuota;
import pe.servitec.crm.model.Usuario;
import pe.servitec.crm.repository.ClienteRepository;
import pe.servitec.crm.repository.ContratoRepository;
import pe.servitec.crm.repository.ProductoRepository;
import pe.servitec.crm.service.ContratoService;

@Controller
@RequestMapping("/contratos")
public class ContratoController {

	private final ContratoRepository contratoRepository;
	private final ClienteRepository clienteRepository;
	private final ProductoRepository productoRepository;
	private final ContratoService contratoService;

	public ContratoController(ContratoRepository contratoRepository, ClienteRepository clienteRepository,
			ProductoRepository productoRepository, ContratoService contratoService) {
		this.contratoRepository = contratoRepository;
		this.clienteRepository = clienteRepository;
		this.productoRepository = productoRepository;
		this.contratoService = contratoService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("contratos", contratoRepository.findAllByOrderByIdContratoDesc());
		return "contratos/lista";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("clientes", clienteRepository.findByEstadoOrderByRazonSocial("ACTIVO"));
		model.addAttribute("productos", productoRepository.findByEstadoOrderByCodigo("ACTIVO"));
		model.addAttribute("hoy", LocalDate.now());
		return "contratos/form";
	}

	@PostMapping("/guardar")
	public String guardar(@RequestParam Integer idCliente,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam Integer meses, @RequestParam(required = false) String observacion,
			@RequestParam List<Integer> productoId, @RequestParam List<Integer> cantidad, HttpSession session,
			RedirectAttributes flash) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		try {
			Contrato contrato = contratoService.registrar(idCliente, fechaInicio, meses, observacion, productoId,
					cantidad, usuario);
			flash.addFlashAttribute("mensaje", "Contrato N° " + contrato.getIdContrato()
					+ " registrado con " + contrato.getCuotas().size() + " cuotas mensuales");
			return "redirect:/contratos/ver/" + contrato.getIdContrato();
		} catch (RuntimeException e) {
			flash.addFlashAttribute("error", "No se registró el contrato: " + e.getMessage());
			return "redirect:/contratos/nuevo";
		}
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model, RedirectAttributes flash) {
		return contratoRepository.findById(id).map(contrato -> {
			model.addAttribute("contrato", contrato);
			return "contratos/detalle";
		}).orElseGet(() -> {
			flash.addFlashAttribute("error", "El contrato no existe");
			return "redirect:/contratos";
		});
	}

	@PostMapping("/cuotas/{id}/pagar")
	public String pagar(@PathVariable Integer id, @RequestParam(required = false) String origen,
			RedirectAttributes flash) {
		try {
			Cuota cuota = contratoService.pagarCuota(id);
			flash.addFlashAttribute("mensaje", "Se registró el pago de la cuota N° " + cuota.getNumero());
			if ("estado-cuenta".equals(origen)) {
				return "redirect:/consultas/estado-cuenta?idCliente=" + cuota.getContrato().getCliente().getIdCliente();
			}
			return "redirect:/contratos/ver/" + cuota.getContrato().getIdContrato();
		} catch (RuntimeException e) {
			flash.addFlashAttribute("error", e.getMessage());
			return "redirect:/contratos";
		}
	}

	@PostMapping("/anular/{id}")
	public String anular(@PathVariable Integer id, HttpSession session, RedirectAttributes flash) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if (!usuario.esAdministrador()) {
			return "redirect:/acceso-denegado";
		}
		try {
			contratoService.anular(id);
			flash.addFlashAttribute("mensaje", "Contrato anulado y stock devuelto al inventario");
		} catch (RuntimeException e) {
			flash.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/contratos/ver/" + id;
	}

}
