package pe.servitec.crm.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.servitec.crm.model.Usuario;
import pe.servitec.crm.repository.RolRepository;
import pe.servitec.crm.repository.UsuarioRepository;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

	private final UsuarioRepository usuarioRepository;
	private final RolRepository rolRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UsuarioController(UsuarioRepository usuarioRepository, RolRepository rolRepository,
			BCryptPasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.rolRepository = rolRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("usuarios", usuarioRepository.findAll());
		return "usuarios/lista";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("roles", rolRepository.findAll());
		return "usuarios/form";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model, RedirectAttributes flash) {
		return usuarioRepository.findById(id).map(usuario -> {
			model.addAttribute("usuario", usuario);
			model.addAttribute("roles", rolRepository.findAll());
			return "usuarios/form";
		}).orElseGet(() -> {
			flash.addFlashAttribute("error", "El usuario no existe");
			return "redirect:/usuarios";
		});
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute Usuario usuario, @RequestParam Integer idRol,
			@RequestParam(required = false) String clave, Model model, RedirectAttributes flash) {
		boolean nuevo = usuario.getIdUsuario() == null;
		usuario.setRol(rolRepository.findById(idRol).orElseThrow());
		if (nuevo) {
			if (usuarioRepository.existsByUsername(usuario.getUsername())) {
				model.addAttribute("error", "El nombre de usuario ya está en uso");
				model.addAttribute("roles", rolRepository.findAll());
				return "usuarios/form";
			}
			if (clave == null || clave.length() < 6) {
				model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
				model.addAttribute("roles", rolRepository.findAll());
				return "usuarios/form";
			}
			usuario.setActivo(true);
			usuario.setPassword(passwordEncoder.encode(clave));
		} else {
			Usuario actual = usuarioRepository.findById(usuario.getIdUsuario()).orElseThrow();
			usuario.setUsername(actual.getUsername());
			usuario.setActivo(actual.isActivo());
			usuario.setPassword(clave == null || clave.isBlank() ? actual.getPassword() : passwordEncoder.encode(clave));
		}
		usuarioRepository.save(usuario);
		flash.addFlashAttribute("mensaje", nuevo ? "Usuario registrado correctamente" : "Usuario actualizado correctamente");
		return "redirect:/usuarios";
	}

	@GetMapping("/estado/{id}")
	public String cambiarEstado(@PathVariable Integer id, HttpSession session, RedirectAttributes flash) {
		Usuario sesion = (Usuario) session.getAttribute("usuario");
		if (sesion.getIdUsuario().equals(id)) {
			flash.addFlashAttribute("error", "No puede desactivar su propio usuario");
			return "redirect:/usuarios";
		}
		usuarioRepository.findById(id).ifPresent(usuario -> {
			usuario.setActivo(!usuario.isActivo());
			usuarioRepository.save(usuario);
			flash.addFlashAttribute("mensaje", usuario.isActivo() ? "Usuario activado" : "Usuario desactivado");
		});
		return "redirect:/usuarios";
	}

}
