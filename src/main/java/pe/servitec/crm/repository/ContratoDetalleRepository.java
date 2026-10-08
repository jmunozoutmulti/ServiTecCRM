package pe.servitec.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.servitec.crm.dto.EquipoAsignadoDTO;
import pe.servitec.crm.model.ContratoDetalle;

public interface ContratoDetalleRepository extends JpaRepository<ContratoDetalle, Integer> {

	@Query("SELECT new pe.servitec.crm.dto.EquipoAsignadoDTO(c.cliente.razonSocial, c.idContrato, p.codigo, p.nombre, p.tipo, d.cantidad, d.precioMensual, d.subtotal) "
			+ "FROM ContratoDetalle d JOIN d.contrato c JOIN d.producto p WHERE c.estado = 'VIGENTE' ORDER BY c.cliente.razonSocial, c.idContrato")
	List<EquipoAsignadoDTO> listarEquiposAsignados();

	boolean existsByProductoIdProducto(Integer idProducto);

}
