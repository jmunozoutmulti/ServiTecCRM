package pe.servitec.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.servitec.crm.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

	List<Producto> findAllByOrderByCodigo();

	List<Producto> findByEstadoOrderByCodigo(String estado);

	boolean existsByCodigo(String codigo);

}
