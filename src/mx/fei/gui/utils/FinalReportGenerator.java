package mx.fei.gui.utils;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinalReportGenerator {

    private static final Logger logger = Logger.getLogger(FinalReportGenerator.class.getName());
    private static final String TEMPLATE_PATH = "/templates/FinalReport.jasper";

    public FinalReportGenerator() {
    }

    public boolean generate(Map<String, Object> parameters, String outputPath) {
        boolean result = false;
        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                logger.log(Level.SEVERE, "No se encontró la plantilla FinalReport.jasper en el classpath");
            }

            JasperPrint combinedPrint = null;
            try (InputStream subreport1Stream = getClass().getResourceAsStream("/templates/subFinalReport1.jasper");
                 InputStream subreport2Stream = getClass().getResourceAsStream("/templates/subFinalReport2.jasper")) {
                if (subreport1Stream != null) {
                    try {
                        JasperReport subreport1 = (JasperReport) JRLoader.loadObject(subreport1Stream);
                        JasperPrint subreport1Print = JasperFillManager.fillReport(subreport1, parameters, new JREmptyDataSource());
                        combinedPrint = subreport1Print;
                    } catch (JRException e) {
                        logger.log(Level.WARNING, "Error al rellenar subFinalReport1.jasper", e);
                    }
                } else {
                    logger.log(Level.WARNING, "No se encontró subFinalReport1.jasper en el classpath");
                }

                if (subreport2Stream != null) {
                    try {
                        if (!parameters.containsKey("Observations")) {
                            Object observationsValue = parameters.getOrDefault("Methodology", "");
                            parameters.put("Observations", observationsValue);
                        }
                        JasperReport subreport2 = (JasperReport) JRLoader.loadObject(subreport2Stream);
                        JasperPrint subreport2Print = JasperFillManager.fillReport(subreport2, parameters, new JREmptyDataSource());
                        if (combinedPrint == null) {
                            combinedPrint = subreport2Print;
                        } else {
                            for (JRPrintPage page : subreport2Print.getPages()) {
                                combinedPrint.addPage(page);
                            }
                        }
                    } catch (JRException e) {
                        logger.log(Level.WARNING, "Error al rellenar subFinalReport2.jasper", e);
                    }
                } else {
                    logger.log(Level.WARNING, "No se encontró subFinalReport2.jasper en el classpath");
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error al cargar subreportes", e);
            }

            if (combinedPrint != null) {
                JasperExportManager.exportReportToPdfFile(combinedPrint, outputPath);
                result = true;
            } else if (templateStream != null) {
                JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
                result = true;
            }
        } catch (JRException | IOException e) {
            logger.log(Level.SEVERE, "Error crítico al rellenar o exportar el reporte final", e);
        }
        return result;
    }
}
