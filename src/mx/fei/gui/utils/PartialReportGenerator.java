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

public class PartialReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(PartialReportGenerator.class.getName());
    private static final String TEMPLATE_PATH = "/templates/partialReport2.jasper";

    public boolean generate(Map<String, Object> parameters, String outputPath) {
        boolean result = false;
        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                LOGGER.log(Level.SEVERE, "No se encontró la plantilla: " + TEMPLATE_PATH);
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            result = true;
        } catch (JRException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error al generar el reporte parcial", e);
        }
        return result;
    }
}