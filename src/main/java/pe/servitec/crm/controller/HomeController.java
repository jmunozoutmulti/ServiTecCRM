package pe.servitec.crm.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.servitec.crm.repository.ClienteRepository;
import pe.servitec.crm.repository.ContratoRepository;
import pe.servitec.crm.repository.CuotaRepository;

@Controller
public class HomeController {

	private final ClienteRepository clienteRepository;
	private final ContratoRepository contratoRepository;
	private final CuotaRepository cuotaRepository;

	public HomeController(ClienteRepository clienteRepository, ContratoRepository contratoRepository,
			CuotaRepository cuotaRepository) {
		this.clienteRepository = clienteRepository;
		this.contratoRepository = contratoRepository;
		this.cuotaRepository = cuotaRepository;
	}

	@GetMapping("/")
	public String inicio(Model model) {
		LocalDate hoy = LocalDate.now();
		model.addAttribute("totalClientes", clienteRepository.findByEstadoOrderByRazonSocial("ACTIVO").size());
		model.addAttribute("contratosVigentes", contratoRepository.countByEstado("VIGENTE"));
		model.addAttribute("totalPendiente", cuotaRepository.totalPendiente());
		model.addAttribute("totalVencido", cuotaRepository.totalVencido(hoy));
		model.addAttribute("cuotasVencidas", cuotaRepository.contarVencidas(hoy));
		model.addAttribute("ultimosContratos", contratoRepository.findAllByOrderByIdContratoDesc().stream().limit(5).toList());
		return "index";
	}

}
