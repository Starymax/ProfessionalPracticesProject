package mx.fei.gui.views;

import javafx.collections.ObservableList;
import mx.fei.gui.controllers.ControllerChooseProfessor;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Professor;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class GUIChooseProfessor extends Application {
    private ListView<String> listViewProfessors;
    private Button buttonSelect;
    private Button buttonBack;
    private List<Professor> professors;

    public GUIChooseProfessor() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Seleccionar profesor");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 25, 25, 25));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label labelTitle = new Label("Seleccione un profesor:");
        labelTitle.setFont(new Font("SansSerif", 14));
        listViewProfessors = new ListView<>();
        listViewProfessors.setPrefWidth(430);
        listViewProfessors.setPrefHeight(300);
        listViewProfessors.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        listViewProfessors.setItems(FXCollections.observableArrayList());
        buttonSelect = new Button("Seleccionar");
        buttonBack = new Button("Regresar");
        buttonSelect.setPrefWidth(130);
        buttonBack.setPrefWidth(130);
        buttonSelect.setPrefHeight(35);
        buttonBack.setPrefHeight(35);
        buttonSelect.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 8;");
        buttonBack.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 8;");
        VBox buttonsBox = new VBox(20, buttonSelect, buttonBack);
        buttonsBox.setAlignment(Pos.TOP_CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        HBox contentBox = new HBox(20, listViewProfessors, buttonsBox);
        contentBox.setAlignment(Pos.TOP_LEFT);
        formPanel.getChildren().addAll(labelTitle, contentBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerChooseProfessor controllerChooseProfessor = new ControllerChooseProfessor(this);
        buttonSelect.setOnAction(controllerChooseProfessor::handleSelectReturnButtons);
        buttonBack.setOnAction(controllerChooseProfessor::handleSelectReturnButtons);
        Scene scene = new Scene(mainPanel, 660, 430);
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

    public ListView<String> getListViewProfessors() { return listViewProfessors; }
    public Button getButtonSelect() { return buttonSelect; }
    public Button getButtonBack() { return buttonBack; }
    public List<Professor> getProfessors() { return professors; }
}