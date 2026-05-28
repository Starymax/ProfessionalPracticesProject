package mx.fei.gui.utils;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PartialReportGenerator {
    private static final Logger logger = Logger.getLogger(PartialReportGenerator.class.getName());
    private static final String TEMPLATE_PATH = "/templates/partialReport.jasper";

    public boolean generate(Map<String, Object> parameters, String outputPath) {
        boolean result = false;
        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                logger.log(Level.SEVERE, "No se encontró la plantilla: " + TEMPLATE_PATH);
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            result = true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al generar el reporte parcial", e);
        }
        return result;
    }
}