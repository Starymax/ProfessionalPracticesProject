package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerRegisterEducationalExperience;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mx.fei.gui.utils.GUIUtils;

import java.util.ArrayList;
import java.util.List;

public class GUIRegisterEducationalExperience extends Application {

    private TextField textFieldNrc;
    private TextField textFieldName;
    private TextField textFieldCareer;
    private TextField textFieldPeriod;
    private Button buttonRegister;
    private Button buttonCancel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("RegistrarExperienciaEducativa");
        stage.setResizable(false);
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20, 30, 20, 30));
        formGrid.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));formGrid.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label labelTitle = new Label("Registrar experiencia educativa");
        labelTitle.setFont(new Font("SansSerif", 14));
        GridPane.setColumnSpan(labelTitle, 2);
        GridPane.setHalignment(labelTitle, HPos.CENTER);
        formGrid.add(labelTitle, 0, 0);
        formGrid.add(new Label("NRC:"), 0, 1);
        textFieldNrc = new TextField();
        textFieldNrc.setPrefWidth(220);
        formGrid.add(textFieldNrc, 1, 1);
        formGrid.add(new Label("Nombre:"), 0, 2);
        textFieldName = new TextField();
        formGrid.add(textFieldName, 1, 2);
        formGrid.add(new Label("Carrera:"), 0, 3);
        textFieldCareer = new TextField();
        formGrid.add(textFieldCareer, 1, 3);
        formGrid.add(new Label("Periodo:"), 0, 4);
        textFieldPeriod = new TextField();
        formGrid.add(textFieldPeriod, 1, 4);
        ControllerRegisterEducationalExperience controllerRegisterEducationalExperience = new ControllerRegisterEducationalExperience(this);
        buttonRegister = new Button("Registrar");
        buttonCancel = new Button("Cancelar");
        buttonRegister.setPrefWidth(110);
        buttonCancel.setPrefWidth(110);
        buttonRegister.setStyle("-fx-background-color: #323232; -fx-text-fill: white;");
        buttonCancel.setStyle("-fx-background-color: #323232; -fx-text-fill: white;");
        buttonRegister.setOnAction(controllerRegisterEducationalExperience::handleButtonAction);
        buttonCancel.setOnAction(controllerRegisterEducationalExperience::handleButtonAction);
        HBox buttonsBox = new HBox(30, buttonRegister, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        GridPane.setColumnSpan(buttonsBox, 2);
        GridPane.setHalignment(buttonsBox, HPos.CENTER);
        formGrid.add(buttonsBox, 0, 5);
        StackPane mainPanel = new StackPane(formGrid);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateNRC(textFieldNrc.getText().trim(), "NRC:", errors);
        GUIUtils.validateShortText(textFieldName.getText().trim(), "Nombre", errors);
        GUIUtils.validateShortText(textFieldCareer.getText().trim(), "Carrera", errors);
        GUIUtils.validatePeriod(textFieldPeriod.getText().trim(), errors);
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            validated = false;
        }
        return validated;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void closeWindow() {
        ((Stage) buttonCancel.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public TextField getTextFieldNrc() {
        return textFieldNrc;
    }

    public TextField getTextFieldName() {
        return textFieldName;
    }

    public TextField getTextFieldCareer() {
        return textFieldCareer;
    }

    public TextField getTextFieldPeriod() {
        return textFieldPeriod;
    }

    public Button getButtonRegister() {
        return buttonRegister;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }
}