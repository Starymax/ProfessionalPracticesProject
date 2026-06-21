package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import mx.fei.gui.views.GUIReportPreview;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerReportPreview {

    private final GUIReportPreview guiReportPreview;
    private static final Logger logger = Logger.getLogger(ControllerReportPreview.class.getName());

    public ControllerReportPreview(GUIReportPreview guiReportPreview) {
        this.guiReportPreview = guiReportPreview;
    }

    public void handleAcceptCloseButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Aceptar reporte" -> {
                acceptReport();
            }
            case "Cerrar" -> {
                guiReportPreview.getStage().close();
            }
        }
    }

    private void acceptReport() {
        Document report = guiReportPreview.getReport();
        try {
            DocumentDAO documentDAO = new DocumentDAO();
            if (documentDAO.acceptReport(report.getId())) {
                report.setAccepted(true);
                guiReportPreview.markAsAccepted();
                guiReportPreview.getParent().reloadReports();
                guiReportPreview.showSuccess("El reporte fue marcado como aceptado.");
                guiReportPreview.getStage().close();
            } else {
                guiReportPreview.showError("No se pudo marcar el reporte como aceptado.");
            }
        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error al aceptar el reporte", e);
            guiReportPreview.showError(e.getMessage());
        }
    }
}
