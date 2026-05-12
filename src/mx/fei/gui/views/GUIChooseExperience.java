package mx.fei.gui.views;

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
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label labelTitle = new Label("Seleccione una experiencia:");
        labelTitle.setFont(new Font("SansSerif", 14));
        listViewExperiences = new ListView<>();
        listViewExperiences.setPrefWidth(450);
        listViewExperiences.setPrefHeight(280);
        listViewExperiences.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        listViewExperiences.setItems(FXCollections.observableArrayList());
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
        HBox contentBox = new HBox(20, listViewExperiences, buttonsBox);
        contentBox.setAlignment(Pos.TOP_LEFT);
        formPanel.getChildren().addAll(labelTitle, contentBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerChooseExperience controllerChooseExperience = new ControllerChooseExperience(this);
        buttonSelect.setOnAction(event -> controllerChooseExperience.handleButtonsSelectReturn(event));
        buttonBack.setOnAction(event -> controllerChooseExperience.handleButtonsSelectReturn(event));
        Scene scene = new Scene(mainPanel, 680, 420);
        stage.setScene(scene);
        stage.show();
    }

    public EducationalExperience getSelectedExperience() {
        int selectedIndex = listViewExperiences.getSelectionModel().getSelectedIndex();
        if (experiences == null || experiences.isEmpty()) {
            throw new IllegalStateException("No hay experiencias disponibles");
        }
        if (selectedIndex < 0 || selectedIndex >= experiences.size()) {
            throw new IllegalStateException("Ninguna experiencia seleccionada");
        }
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
    //TODO: Validar que no se de clic en seleccionar sin seleccionar una experiencia primero

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