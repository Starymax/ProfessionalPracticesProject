package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseProfessor;
import mx.fei.gui.views.GUIModifyProfessor;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerChooseProfessor {
    private GUIChooseProfessor guiChooseProfessor;
    private ProfessorDAO professorDAO;
    private static final Logger logger = Logger.getLogger(ControllerChooseProfessor.class.getName());

    public ControllerChooseProfessor(GUIChooseProfessor guiChooseProfessor) {
        this.guiChooseProfessor = guiChooseProfessor;
        this.professorDAO = new ProfessorDAO();
        loadProfessors();
    }
    private void loadProfessors() {
        try {
            List<Professor> professors = professorDAO.getProfessors();
            guiChooseProfessor.setProfessors(professors);
        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error al cargar a los profesores", e);
            guiChooseProfessor.showError(e.getMessage());
        }
    }

    public void handleSelectReturnButtons(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        switch (button.getText()) {
            case "Seleccionar" -> {
                handleSelectProfessor();
            }
            case "Regresar" -> {
                guiChooseProfessor.closeWindow();
            }
        }
    }

    private void handleSelectProfessor() {
        Professor professorSelected;
        try {
            professorSelected = guiChooseProfessor.getSelectedProfessor();
            GUIModifyProfessor guiModifyProfessor = new GUIModifyProfessor(professorSelected);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiModifyProfessor.start(stage);
            guiChooseProfessor.closeWindow();
        } catch (IllegalStateException e) {
            guiChooseProfessor.showError("Seleccione un profesor.");
        }
    }
}
