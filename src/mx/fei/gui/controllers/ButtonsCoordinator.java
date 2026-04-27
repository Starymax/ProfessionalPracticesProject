package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import mx.fei.gui.views.GUICoordinator;

public class ButtonsCoordinator implements EventHandler<ActionEvent> {
    private GUICoordinator guiCoordinator;

    public ButtonsCoordinator(GUICoordinator guiCoordinator) {
        this.guiCoordinator = guiCoordinator;
    }

    @Override
    public void handle(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Gestionar alumnos" -> { /* TODO: abrir ventana */ }
            case "Gestionar proyectos" -> { /* TODO: abrir ventana */ }
            case "Gestionar organizaciones" -> { /* TODO: abrir ventana */ }
            case "Gestionar experiencia educativa" -> { /* TODO: abrir ventana */ }
            case "Consultar profesor" -> { /* TODO: abrir ventana */ }
            case "Regresar" -> guiCoordinator.closeWindow();
        }
    }
}