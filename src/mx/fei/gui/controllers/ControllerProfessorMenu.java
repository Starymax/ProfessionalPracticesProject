package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import mx.fei.gui.views.GUIProfessor;

public class ControllerProfessorMenu implements EventHandler<ActionEvent> {
    private GUIProfessor guiProfessor;
    public ControllerProfessorMenu(GUIProfessor guiProfessor) {
        this.guiProfessor = guiProfessor;
    }

    @Override
    public void handle(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Gestionar actividades" -> { /* TODO: abrir ventana */ }
            case "Gestionar reportes" -> { /* TODO: abrir ventana */ }
            case "Regresar" -> {guiProfessor.closeWindow();}
        }
    }
}
