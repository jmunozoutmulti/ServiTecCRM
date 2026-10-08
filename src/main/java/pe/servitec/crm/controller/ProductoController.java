package pe.servitec.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.servitec.crm.model.Producto;
import pe.servitec.crm.repository.ContratoDetalleRepository;
import pe.servitec.crm.repository.ProductoRepository;

@Controller
@RequestMapping("/productos")
public class ProductoController {

	private final ProductoRepository productoRepository;
	private final ContratoDetalleRepository detalleRepository;

	public ProductoController(ProductoRepository productoRepository, ContratoDetalleRepository detalleRepository) {
		this.productoRepository = productoRepository;
		this.detalleRepository = detalleRepository;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("productos", productoRepository.findAllByOrderByCodigo());
		return "productos/lista";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("producto", new Producto());
		return "productos/form";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model, RedirectAttributes flash) {
		return productoRepository.findById(id).map(producto -> {
			model.addAttribute("producto", producto);
			return "productos/form";
		}).orElseGet(() -> {
			flash.addFlashAttribute("error", "El producto no existe");
			return "redirect:/productos";
		});
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute Producto producto, Model model, RedirectAttributes flash) {
		boolean nuevo = producto.getIdProducto() == null;
		if (nuevo && productoRepository.existsByCodigo(producto.getCodigo())) {
			model.addAttribute("error", "Ya existe un producto con el código " + producto.getCodigo());
			return "productos/form";
		}
		if (!producto.esEquipo() || producto.getStock() == null) {
			producto.setStock(0);
		}
		if (nuevo) {
			producto.setEstado("ACTIVO");
		} else {
			producto.setEstado(productoRepository.findById(producto.getIdProducto()).orElseThrow().getEstado());
		}
		productoRepository.save(producto);
		flash.addFlashAttribute("mensaje", nuevo ? "Producto registrado correctamente" : "Producto actualizado correctamente");
		return "redirect:/productos";
	}

	@GetMapping("/estado/{id}")
	public String cambiarEstado(@PathVariable Integer id, RedirectAttributes flash) {
		productoRepository.findById(id).ifPresent(producto -> {
			producto.setEstado("ACTIVO".equals(producto.getEstado()) ? "INACTIVO" : "ACTIVO");
			productoRepository.save(producto);
			flash.addFlashAttribute("mensaje", "El producto ahora se encuentra " + producto.getEstado());
		});
		return "redirect:/productos";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
		if (detalleRepository.existsByProductoIdProducto(id)) {
			flash.addFlashAttribute("error", "No se puede eliminar un producto asignado a contratos, puede desactivarlo");
			return "redirect:/productos";
		}
		productoRepository.deleteById(id);
		flash.addFlashAttribute("mensaje", "Producto eliminado correctamente");
		return "redirect:/productos";
	}

}
