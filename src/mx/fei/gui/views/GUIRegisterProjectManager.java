package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerRegisterProjectManager;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Enterprise;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUIRegisterProjectManager extends Application {

    private TextField textFieldName;
    private TextField textFieldPosition;
    private TextField textFieldPhoneNumber;
    private TextField textFieldEmail;
    private Button buttonRegister;
    private Button buttonCancel;
    private Stage stage;
    private Enterprise enterprise;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label labelTitle = new Label("Datos del Responsable del Proyecto:");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));

        textFieldName = new TextField();
        textFieldPosition = new TextField();
        textFieldPhoneNumber = new TextField();
        textFieldEmail = new TextField();

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(18);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(100);
        labelColumn.setPrefWidth(100);

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);

        formGrid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        String[] labelTexts = {"Nombre:", "Cargo:", "Telefono:", "Correo:"};
        TextField[] fields  = {textFieldName, textFieldPosition, textFieldPhoneNumber, textFieldEmail};

        for (int i = 0; i < labelTexts.length; i++) {
            Label label = new Label(labelTexts[i]);
            label.setFont(Font.font("SansSerif", 14));
            fields[i].setMaxWidth(Double.MAX_VALUE);
            formGrid.add(label, 0, i);
            formGrid.add(fields[i], 1, i);
        }

        buttonRegister = new Button("Registrar");
        buttonCancel = new Button("Cancelar");

        buttonRegister.setPrefWidth(120);
        buttonCancel.setPrefWidth(120);

        ControllerRegisterProjectManager controllerRegisterProjectManager = new ControllerRegisterProjectManager(this);
        buttonRegister.setOnAction(controllerRegisterProjectManager::handleRegisterCancelButtons);
        buttonCancel.setOnAction(controllerRegisterProjectManager::handleRegisterCancelButtons);

        FlowPane buttonPanel = new FlowPane(10, 0, buttonRegister, buttonCancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);

        VBox centerPanel = new VBox(24, labelTitle, formGrid);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(32, 40, 32, 40));
        mainPanel.setTop(centerPanel);
        mainPanel.setBottom(buttonPanel);
        BorderPane.setMargin(buttonPanel, new Insets(24, 0, 0, 0));

        Scene scene = new Scene(mainPanel, 580, 380);
        GUIStyle.apply(scene);
        stage.setTitle("Registrar Responsable de Proyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean fieldsValidated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateNames(textFieldName.getText(), "Nombre", errors);
        GUIUtils.validateShortText(textFieldPosition.getText().trim(), "Cargo", errors);
        GUIUtils.validatePhone(textFieldPhoneNumber.getText().trim(), "Teléfono", errors);
        GUIUtils.validateEmail(textFieldEmail.getText().trim(), errors);
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            fieldsValidated = false;
        }
        return fieldsValidated;
    }

    public void loadEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public TextField getTextFieldName() {
        return textFieldName;
    }

    public TextField getTextFieldPosition() {
        return textFieldPosition;
    }

    public TextField getTextFieldPhoneNumber() {
        return textFieldPhoneNumber;
    }

    public TextField getTextFieldEmail() {
        return textFieldEmail;
    }

    public Button getButtonRegister() {
        return buttonRegister;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public Stage getStage() {
        return stage;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public static void main(String[] args) {
        launch(args);
    }
}