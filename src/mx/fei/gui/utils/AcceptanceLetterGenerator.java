package mx.fei.gui.utils;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcceptanceLetterGenerator {

    private static final Logger logger = Logger.getLogger(AcceptanceLetterGenerator.class.getName());
    private static final String TEMPLATE_PATH = "/templates/aceptation_card.jasper";

    public boolean generate(Map<String, Object> parameters, String outputPath) {
        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                logger.severe("No se encontró la plantilla: " + TEMPLATE_PATH);
                return false;
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            return true;
        } catch (JRException | IOException e) {
            logger.log(Level.SEVERE, "Error al generar la carta de aceptación", e);
            return false;
        }
    }
}