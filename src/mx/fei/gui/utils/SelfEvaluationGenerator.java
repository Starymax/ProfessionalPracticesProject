package mx.fei.gui.utils;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelfEvaluationGenerator {

    private static final Logger LOGGER = Logger.getLogger(SelfEvaluationGenerator.class.getName());
    private static final String TEMPLATE_PATH = "/templates/selfEvaluation.jasper";

    public boolean generate(Map<String, Object> parameters, String outputPath) {
        boolean result = false;
        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                LOGGER.severe("No se encontró la plantilla selfEvaluation.jasper");
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            result = true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al generar autoevaluación", e);
        }
        return result;
    }
}