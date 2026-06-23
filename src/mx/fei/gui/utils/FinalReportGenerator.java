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

    private static final Logger LOGGER = Logger.getLogger(FinalReportGenerator.class.getName());
    private static final String TEMPLATE_PATH = "/templates/FinalReport.jasper";

    public FinalReportGenerator() {
    }

    public boolean generate(Map<String, Object> parameters, String outputPath) {
        boolean result = false;
        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                LOGGER.log(Level.SEVERE, "No se encontró la plantilla FinalReport.jasper en el classpath");
            }
            JasperPrint combinedPrint = buildCombinedPrint(parameters);
            result = exportReport(combinedPrint, templateStream, parameters, outputPath);
        } catch (JRException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error crítico al rellenar o exportar el reporte final", e);
        }
        return result;
    }

    private JasperPrint buildCombinedPrint(Map<String, Object> parameters) {
        JasperPrint combinedPrint = null;
        try (InputStream subreport1Stream = getClass().getResourceAsStream("/templates/subFinalReport1.jasper");
             InputStream subreport2Stream = getClass().getResourceAsStream("/templates/subFinalReport2.jasper")) {
            combinedPrint = fillFirstSubreport(subreport1Stream, parameters);
            combinedPrint = appendSecondSubreport(combinedPrint, subreport2Stream, parameters);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al cargar subreportes", e);
        }
        return combinedPrint;
    }

    private JasperPrint fillFirstSubreport(InputStream subreportStream, Map<String, Object> parameters) {
        JasperPrint subreportPrint = null;
        if (subreportStream == null) {
            LOGGER.log(Level.WARNING, "No se encontró subFinalReport1.jasper en el classpath");
        } else {
            try {
                JasperReport subreport = (JasperReport) JRLoader.loadObject(subreportStream);
                subreportPrint = JasperFillManager.fillReport(subreport, parameters, new JREmptyDataSource());
            } catch (JRException e) {
                LOGGER.log(Level.WARNING, "Error al rellenar subFinalReport1.jasper", e);
            }
        }
        return subreportPrint;
    }

    private JasperPrint appendSecondSubreport(JasperPrint combinedPrint, InputStream subreportStream, Map<String, Object> parameters) {
        JasperPrint result = combinedPrint;
        if (subreportStream == null) {
            LOGGER.log(Level.WARNING, "No se encontró subFinalReport2.jasper en el classpath");
        } else {
            try {
                ensureObservationsParameter(parameters);
                JasperReport subreport = (JasperReport) JRLoader.loadObject(subreportStream);
                JasperPrint subreportPrint = JasperFillManager.fillReport(subreport, parameters, new JREmptyDataSource());
                if (combinedPrint == null) {
                    result = subreportPrint;
                } else {
                    for (JRPrintPage page : subreportPrint.getPages()) {
                        combinedPrint.addPage(page);
                    }
                }
            } catch (JRException e) {
                LOGGER.log(Level.WARNING, "Error al rellenar subFinalReport2.jasper", e);
            }
        }
        return result;
    }

    private void ensureObservationsParameter(Map<String, Object> parameters) {
        if (!parameters.containsKey("Observations")) {
            Object observationsValue = parameters.getOrDefault("Methodology", "");
            parameters.put("Observations", observationsValue);
        }
    }

    private boolean exportReport(JasperPrint combinedPrint, InputStream templateStream, Map<String, Object> parameters, String outputPath) throws JRException {
        boolean exported = false;
        if (combinedPrint != null) {
            JasperExportManager.exportReportToPdfFile(combinedPrint, outputPath);
            exported = true;
        } else if (templateStream != null) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            exported = true;
        }
        return exported;
    }
}
