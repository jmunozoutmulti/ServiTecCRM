package pe.servitec.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.servitec.crm.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

	List<Contrato> findAllByOrderByIdContratoDesc();

	List<Contrato> findByClienteIdClienteOrderByIdContratoDesc(Integer idCliente);

	long countByEstado(String estado);

}
