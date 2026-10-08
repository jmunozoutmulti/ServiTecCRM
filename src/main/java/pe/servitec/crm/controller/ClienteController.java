package pe.servitec.crm.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.servitec.crm.model.Cliente;
import pe.servitec.crm.repository.ClienteRepository;
import pe.servitec.crm.repository.ContratoRepository;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

	private final ClienteRepository clienteRepository;
	private final ContratoRepository contratoRepository;

	public ClienteController(ClienteRepository clienteRepository, ContratoRepository contratoRepository) {
		this.clienteRepository = clienteRepository;
		this.contratoRepository = contratoRepository;
	}

	@GetMapping
	public String listar(@RequestParam(required = false) String q, Model model) {
		model.addAttribute("clientes", q == null || q.isBlank() ? clienteRepository.buscar("") : clienteRepository.buscar(q.trim()));
		model.addAttribute("q", q);
		return "clientes/lista";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("cliente", new Cliente());
		return "clientes/form";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model, RedirectAttributes flash) {
		return clienteRepository.findById(id).map(cliente -> {
			model.addAttribute("cliente", cliente);
			return "clientes/form";
		}).orElseGet(() -> {
			flash.addFlashAttribute("error", "El cliente no existe");
			return "redirect:/clientes";
		});
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute Cliente cliente, Model model, RedirectAttributes flash) {
		boolean nuevo = cliente.getIdCliente() == null;
		if (nuevo && clienteRepository.existsByNumeroDocumento(cliente.getNumeroDocumento())) {
			model.addAttribute("error", "Ya existe un cliente con el documento " + cliente.getNumeroDocumento());
			return "clientes/form";
		}
		if (nuevo) {
			cliente.setFechaRegistro(LocalDate.now());
			cliente.setEstado("ACTIVO");
		} else {
			Cliente actual = clienteRepository.findById(cliente.getIdCliente()).orElseThrow();
			cliente.setFechaRegistro(actual.getFechaRegistro());
			cliente.setEstado(actual.getEstado());
		}
		clienteRepository.save(cliente);
		flash.addFlashAttribute("mensaje", nuevo ? "Cliente registrado correctamente" : "Cliente actualizado correctamente");
		return "redirect:/clientes";
	}

	@GetMapping("/estado/{id}")
	public String cambiarEstado(@PathVariable Integer id, RedirectAttributes flash) {
		clienteRepository.findById(id).ifPresent(cliente -> {
			cliente.setEstado("ACTIVO".equals(cliente.getEstado()) ? "INACTIVO" : "ACTIVO");
			clienteRepository.save(cliente);
			flash.addFlashAttribute("mensaje", "El cliente ahora se encuentra " + cliente.getEstado());
		});
		return "redirect:/clientes";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
		if (!contratoRepository.findByClienteIdClienteOrderByIdContratoDesc(id).isEmpty()) {
			flash.addFlashAttribute("error", "No se puede eliminar un cliente con contratos registrados, puede desactivarlo");
			return "redirect:/clientes";
		}
		clienteRepository.deleteById(id);
		flash.addFlashAttribute("mensaje", "Cliente eliminado correctamente");
		return "redirect:/clientes";
	}

}
