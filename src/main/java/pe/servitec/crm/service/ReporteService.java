package pe.servitec.crm.service;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReporteService {

	private final Map<String, JasperReport> compilados = new ConcurrentHashMap<>();

	public byte[] generarPdf(String nombre, Map<String, Object> parametros, Collection<?> datos) throws Exception {
		JasperReport reporte = compilar(nombre);
		JasperPrint print = JasperFillManager.fillReport(reporte, parametros, new JRBeanCollectionDataSource(datos));
		return JasperExportManager.exportReportToPdf(print);
	}

	private JasperReport compilar(String nombre) throws Exception {
		JasperReport reporte = compilados.get(nombre);
		if (reporte == null) {
			try (InputStream jrxml = new ClassPathResource("reportes/" + nombre + ".jrxml").getInputStream()) {
				reporte = JasperCompileManager.compileReport(jrxml);
			}
			compilados.put(nombre, reporte);
		}
		return reporte;
	}

}
