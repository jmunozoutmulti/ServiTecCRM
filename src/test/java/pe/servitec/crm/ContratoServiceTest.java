package pe.servitec.crm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import pe.servitec.crm.model.Cuota;
import pe.servitec.crm.service.ContratoService;

class ContratoServiceTest {

	@Test
	void generaUnaCuotaPorCadaMes() {
		List<Cuota> cuotas = ContratoService.generarCuotas(LocalDate.of(2026, 1, 15), 12, new BigDecimal("350.00"));
		assertEquals(12, cuotas.size());
		assertEquals(LocalDate.of(2026, 2, 15), cuotas.get(0).getFechaVencimiento());
		assertEquals(LocalDate.of(2027, 1, 15), cuotas.get(11).getFechaVencimiento());
		assertEquals("PENDIENTE", cuotas.get(5).getEstado());
	}

	@Test
	void montoDeCuotaIgualAlMontoMensual() {
		List<Cuota> cuotas = ContratoService.generarCuotas(LocalDate.of(2026, 3, 1), 3, new BigDecimal("610.00"));
		assertEquals(new BigDecimal("610.00"), cuotas.get(2).getMonto());
	}

}
