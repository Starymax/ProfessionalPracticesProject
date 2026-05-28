package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIGenerateMonthlyReport;
import mx.fei.gui.views.GUIGeneratePartialReport;
import mx.fei.gui.views.GUIGenerateReport;

public class ControllerGenerateReport {
    GUIGenerateReport guiGenerateReport;
    public ControllerGenerateReport(GUIGenerateReport guiGenerateReport) {
        this.guiGenerateReport = guiGenerateReport;
    }

    public void handleReportOptions(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch(source.getText()) {
            case "Mensual" -> {
                openMonthlyReport();
            }
            case "Parcial" -> {
                openPartialReport();
            }
            case "Regresar" -> guiGenerateReport.closeWindow();
        }
    }

    private void openMonthlyReport() {
        if (guiGenerateReport.getPractice().getStudent() == null) {
            guiGenerateReport.showError("No hay estudiante seleccionado.");
        } else {
            try {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                GUIGenerateMonthlyReport generateMonthlyReport = new GUIGenerateMonthlyReport(guiGenerateReport.getPractice().getStudent());
                generateMonthlyReport.start(stage);
            } catch (Exception e) {
                guiGenerateReport.showError("No se pudo abrir la generación de reportes: " + e);
            }
        }
    }

    private void openPartialReport() {
        if (guiGenerateReport.getPractice().getStudent() == null) {
            guiGenerateReport.showError("No hay estudiante seleccionado.");
        } else {
            try {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                GUIGeneratePartialReport generatePartialReport = new GUIGeneratePartialReport(guiGenerateReport.getPractice().getStudent());
                generatePartialReport.start(stage);
            } catch (Exception e) {
                guiGenerateReport.showError("No se pudo abrir la generación de reportes: " + e);
            }
        }
    }
}
