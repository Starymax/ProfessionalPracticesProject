package mx.fei.gui.utils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonthlyReportGenerator {

    private static final Logger LOGGER = Logger.getLogger(MonthlyReportGenerator.class.getName());
    private static final String TEMPLATE_PATH = "/templates/MonthlyReport.jasper";

    public MonthlyReportGenerator() {
    }

    public boolean generate(Map<String, Object> parameters, String outputPath) {
        boolean result = false;
        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                LOGGER.log(Level.SEVERE, "No se encontró la plantilla");
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            result = true;
        } catch (JRException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error crítico al rellenar o exportar el reporte mensual", e);
        }
        return result;
    }
}