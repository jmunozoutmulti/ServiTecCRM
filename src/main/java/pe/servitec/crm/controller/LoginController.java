package pe.servitec.crm.controller;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import pe.servitec.crm.model.Usuario;
import pe.servitec.crm.repository.UsuarioRepository;

@Controller
public class LoginController {

	private final UsuarioRepository usuarioRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public LoginController(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/login")
	public String login(HttpSession session) {
		if (session.getAttribute("usuario") != null) {
			return "redirect:/";
		}
		return "login";
	}

	@PostMapping("/login")
	public String ingresar(@RequestParam String username, @RequestParam String password, HttpSession session,
			Model model) {
		Optional<Usuario> usuario = usuarioRepository.findByUsernameAndActivoTrue(username.trim());
		if (usuario.isEmpty() || !passwordEncoder.matches(password, usuario.get().getPassword())) {
			model.addAttribute("error", "Usuario o contraseña incorrectos");
			model.addAttribute("username", username);
			return "login";
		}
		session.setAttribute("usuario", usuario.get());
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String salir(HttpSession session) {
		session.invalidate();
		return "redirect:/login?salida";
	}

	@GetMapping("/acceso-denegado")
	public String accesoDenegado() {
		return "acceso-denegado";
	}

}
