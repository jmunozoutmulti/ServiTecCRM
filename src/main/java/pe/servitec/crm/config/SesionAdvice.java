package pe.servitec.crm.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;
import pe.servitec.crm.model.Usuario;

@ControllerAdvice
public class SesionAdvice {

	@ModelAttribute("usuarioSesion")
	public Usuario usuarioSesion(HttpSession session) {
		return (Usuario) session.getAttribute("usuario");
	}

}
