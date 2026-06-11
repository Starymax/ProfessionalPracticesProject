package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerChooseExperience;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.EducationalExperience;
import java.util.List;

public class GUIChooseExperience extends Application {
    private ListView<String> listViewExperiences;
    private Button buttonSelect;
    private Button buttonBack;
    private List<EducationalExperience> experiences;
    private boolean toModify = false;

    public GUIChooseExperience() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Escoger experiencia");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 25, 25, 25));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Seleccione una experiencia:");
        labelTitle.setFont(new Font("SansSerif", 14));
        listViewExperiences = new ListView<>();
        listViewExperiences.setPrefWidth(450);
        listViewExperiences.setPrefHeight(280);
        listViewExperiences.setItems(FXCollections.observableArrayList());
        buttonSelect = new Button("Seleccionar");
        buttonBack = new Button("Regresar");
        buttonSelect.setPrefWidth(130);
        buttonBack.setPrefWidth(130);
        buttonSelect.setPrefHeight(35);
        buttonBack.setPrefHeight(35);
        VBox buttonsBox = new VBox(20, buttonSelect, buttonBack);
        buttonsBox.setAlignment(Pos.TOP_CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        HBox contentBox = new HBox(20, listViewExperiences, buttonsBox);
        contentBox.setAlignment(Pos.TOP_LEFT);
        formPanel.getChildren().addAll(labelTitle, contentBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerChooseExperience controllerChooseExperience = new ControllerChooseExperience(this);
        buttonSelect.setOnAction(controllerChooseExperience::handleSelectReturnButtons);
        buttonBack.setOnAction(controllerChooseExperience::handleSelectReturnButtons);
        Scene scene = new Scene(mainPanel, 680, 420);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public EducationalExperience getSelectedExperience() {
        int selectedIndex = listViewExperiences.getSelectionModel().getSelectedIndex();
        return experiences.get(selectedIndex);
    }

    public void setExperiences(List<EducationalExperience> experiences) {
        this.experiences = experiences;
        ObservableList<String> items = FXCollections.observableArrayList();
        for (EducationalExperience educationalExperience : experiences) {
            items.add(educationalExperience.getNrc() + " - " + educationalExperience.getName());
        }
        listViewExperiences.setItems(items);
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void closeWindow() {
        ((Stage) buttonBack.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ListView<String> getListViewExperiences() { return listViewExperiences; }
    public Button getButtonSelect() { return buttonSelect; }
    public Button getButtonBack() { return buttonBack; }
    public boolean isToModify() {return toModify; }
    public void setToModify(boolean toModify) {this.toModify = toModify; }
    public List<EducationalExperience> getExperiences() { return experiences; }
}