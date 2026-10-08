package pe.servitec.crm.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.servitec.crm.model.Usuario;

@Component
public class SesionInterceptor implements HandlerInterceptor {

	private static final String[] RUTAS_ADMIN = { "/usuarios", "/productos", "/reportes" };

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);
		Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuario");
		if (usuario == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		}
		String ruta = request.getRequestURI().substring(request.getContextPath().length());
		for (String rutaAdmin : RUTAS_ADMIN) {
			if (ruta.startsWith(rutaAdmin) && !usuario.esAdministrador()) {
				response.sendRedirect(request.getContextPath() + "/acceso-denegado");
				return false;
			}
		}
		return true;
	}

}
