package pe.servitec.crm.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.servitec.crm.dto.DeudaClienteDTO;
import pe.servitec.crm.model.Cuota;

public interface CuotaRepository extends JpaRepository<Cuota, Integer> {

	@Query("SELECT q FROM Cuota q WHERE q.contrato.cliente.idCliente = :idCliente AND q.estado = 'PENDIENTE' ORDER BY q.fechaVencimiento")
	List<Cuota> listarPendientesPorCliente(@Param("idCliente") Integer idCliente);

	@Query("SELECT q FROM Cuota q WHERE q.fechaVencimiento BETWEEN :desde AND :hasta AND (:estado = 'TODOS' OR q.estado = :estado) ORDER BY q.fechaVencimiento, q.contrato.cliente.razonSocial")
	List<Cuota> listarPorRangoFechas(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta,
			@Param("estado") String estado);

	@Query("SELECT new pe.servitec.crm.dto.DeudaClienteDTO(c.numeroDocumento, c.razonSocial, c.telefono, COUNT(q), SUM(q.monto), MIN(q.fechaVencimiento)) "
			+ "FROM Cuota q JOIN q.contrato k JOIN k.cliente c WHERE q.estado = 'PENDIENTE' AND k.estado = 'VIGENTE' "
			+ "GROUP BY c.idCliente, c.numeroDocumento, c.razonSocial, c.telefono ORDER BY SUM(q.monto) DESC")
	List<DeudaClienteDTO> listarDeudaPorCliente();

	@Query("SELECT COALESCE(SUM(q.monto), 0) FROM Cuota q WHERE q.estado = 'PENDIENTE' AND q.fechaVencimiento < :hoy")
	BigDecimal totalVencido(@Param("hoy") LocalDate hoy);

	@Query("SELECT COALESCE(SUM(q.monto), 0) FROM Cuota q WHERE q.estado = 'PENDIENTE'")
	BigDecimal totalPendiente();

	@Query("SELECT COUNT(q) FROM Cuota q WHERE q.estado = 'PENDIENTE' AND q.fechaVencimiento < :hoy")
	long contarVencidas(@Param("hoy") LocalDate hoy);

}
