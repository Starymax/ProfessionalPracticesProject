package mx.fei.gui.controllers;

import javafx.stage.Stage;
import mx.fei.gui.views.GUIManageStudent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import mx.fei.gui.views.GUIRegisterStudent;

public class ControllerManageStudent implements EventHandler<ActionEvent> {
    private GUIManageStudent guiManageStudent;

    public ControllerManageStudent(GUIManageStudent guiManageStudent) {
        this.guiManageStudent = guiManageStudent;
    }

    @Override
    public void handle(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar estudiante" -> {
                GUIRegisterStudent guiRegisterStudent = new GUIRegisterStudent();
                Stage stage = new Stage();
                guiRegisterStudent.start(stage);
                guiManageStudent.closeWindow();
            }
            case "Modificar estudiante" -> { /* TODO: abrir ventana modificar */ }
            case "Asignar proyecto"     -> { /* TODO: abrir ventana asignar */ }
            case "Regresar" -> {
                guiManageStudent.closeWindow();
            }
        }
    }
}