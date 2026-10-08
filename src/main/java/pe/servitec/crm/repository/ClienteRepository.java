package pe.servitec.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.servitec.crm.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

	@Query("SELECT c FROM Cliente c WHERE LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :texto, '%')) OR c.numeroDocumento LIKE CONCAT('%', :texto, '%') ORDER BY c.razonSocial")
	List<Cliente> buscar(@Param("texto") String texto);

	List<Cliente> findByEstadoOrderByRazonSocial(String estado);

	boolean existsByNumeroDocumento(String numeroDocumento);

}
