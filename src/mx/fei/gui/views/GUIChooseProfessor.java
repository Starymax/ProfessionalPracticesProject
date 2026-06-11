package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerChooseProfessor;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Professor;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class GUIChooseProfessor extends Application {
    private ListView<String> listViewProfessors;
    private Button buttonSelect;
    private Button buttonBack;
    private List<Professor> professors;
    private final int noProfessorSelected = 0;

    public GUIChooseProfessor() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Seleccionar profesor");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 25, 25, 25));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Seleccione un profesor:");
        labelTitle.setFont(new Font("SansSerif", 14));
        listViewProfessors = new ListView<>();
        listViewProfessors.setPrefWidth(430);
        listViewProfessors.setPrefHeight(300);
        listViewProfessors.setItems(FXCollections.observableArrayList());
        buttonSelect = new Button("Seleccionar");
        buttonBack = new Button("Regresar");
        buttonSelect.setPrefWidth(130);
        buttonBack.setPrefWidth(130);
        buttonSelect.setPrefHeight(35);
        buttonBack.setPrefHeight(35);
        VBox buttonsBox = new VBox(20, buttonSelect, buttonBack);
        buttonsBox.setAlignment(Pos.TOP_CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        HBox contentBox = new HBox(20, listViewProfessors, buttonsBox);
        contentBox.setAlignment(Pos.TOP_LEFT);
        formPanel.getChildren().addAll(labelTitle, contentBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerChooseProfessor controllerChooseProfessor = new ControllerChooseProfessor(this);
        buttonSelect.setOnAction(controllerChooseProfessor::handleSelectReturnButtons);
        buttonBack.setOnAction(controllerChooseProfessor::handleSelectReturnButtons);
        Scene scene = new Scene(mainPanel, 660, 430);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void setProfessors(List<Professor> professors) {
        this.professors = professors;
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Professor professor : professors) {
            items.add(professor.getPersonalNumber() + " - " + professor.getName() + " " + professor.getLastName());
        }
        listViewProfessors.setItems(items);
    }

    public Professor getSelectedProfessor() {
        int selectedIndex = listViewProfessors.getSelectionModel().getSelectedIndex();
        if (selectedIndex < noProfessorSelected || selectedIndex >= professors.size()) {
            throw new IllegalStateException("No hay professor seleccionado.");
        }
        return professors.get(selectedIndex);
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void closeWindow() {
        GUIUtils.closeWindow((Stage) buttonBack.getScene().getWindow());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ListView<String> getListViewProfessors() {
        return listViewProfessors;
    }

    public Button getButtonSelect() {
        return buttonSelect;
    }

    public Button getButtonBack() {
        return buttonBack;
    }

    public List<Professor> getProfessors() {
        return professors;
    }
}