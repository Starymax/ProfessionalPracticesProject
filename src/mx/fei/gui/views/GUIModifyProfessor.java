package mx.fei.gui.views;

import javafx.scene.Node;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.controllers.ControllerModifyProfessor;
import mx.fei.logic.dto.Professor;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUIModifyProfessor extends Application {

    private Professor professor;
    private TextField textFieldName;
    private TextField textFieldLastName;
    private ComboBox<String> comboBoxGender;
    private ComboBox<String> comboBoxShift;
    private TextField textFieldEmail;
    private CheckBox checkBoxIsCoordinator;
    private CheckBox checkBoxIsAdministrator;
    private ToggleButton toggleState;
    private Button buttonUpdate;
    private Button buttonCancel;
    private Stage stage;

    public GUIModifyProfessor(Professor professor) {
        this.professor = professor;
    }

    public GUIModifyProfessor() {}

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Modificar datos del Profesor:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        textFieldName = new TextField();
        textFieldLastName = new TextField();
        comboBoxGender = new ComboBox<>();
        comboBoxShift = new ComboBox<>();
        textFieldEmail = new TextField();
        comboBoxGender.getItems().addAll("Masculino", "Femenino");
        comboBoxShift.getItems().addAll("Matutino", "Vespertino", "Mixto");
        comboBoxGender.setMaxWidth(Double.MAX_VALUE);
        comboBoxShift.setMaxWidth(Double.MAX_VALUE);
        String[] labels = {"Nombre:", "Apellidos:", "Género:", "Turno:", "Correo:"};
        Node[] fields = {textFieldName, textFieldLastName, comboBoxGender, comboBoxShift, textFieldEmail};

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(12);
        for (int i = 0; i < labels.length; i++) {
            Label label = new Label(labels[i]);
            label.setFont(Font.font("SansSerif", 14));
            if (fields[i] instanceof TextField textField) {
                textField.setPrefWidth(320);
            }
            formGrid.add(label, 0, i);
            formGrid.add(fields[i], 1, i);
        }
        checkBoxIsCoordinator = new CheckBox("Coordinador");
        checkBoxIsAdministrator = new CheckBox("Administrador");
        checkBoxIsCoordinator.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) checkBoxIsAdministrator.setSelected(false);
        });
        checkBoxIsAdministrator.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) checkBoxIsCoordinator.setSelected(false);
        });

        HBox checkBoxPanel = new HBox(20, checkBoxIsCoordinator, checkBoxIsAdministrator);
        checkBoxPanel.setAlignment(Pos.CENTER_LEFT);
        toggleState = new ToggleButton("Inactivo");
        toggleState.setPrefWidth(110);
        toggleState.setOnAction(e -> toggleState.setText(toggleState.isSelected() ? "Activo" : "Inactivo"));
        if (professor != null) {
            textFieldName.setText(professor.getName());
            textFieldLastName.setText(professor.getLastName());
            textFieldEmail.setText(professor.getEmail());
            comboBoxGender.setValue(professor.getGender());
            comboBoxShift.setValue(professor.getShift());
            checkBoxIsCoordinator.setSelected(professor.isCoordinator());
            checkBoxIsAdministrator.setSelected(professor.isAdmin());
            toggleState.setSelected(professor.isActive());
            toggleState.setText(professor.isActive() ? "Activo" : "Inactivo");
        }

        buttonUpdate = new Button("Actualizar");
        buttonCancel = new Button("Cancelar");
        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; " + "-fx-font-size: 14px; -fx-cursor: hand;";
        buttonUpdate.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);
        ControllerModifyProfessor controllerModifyProfessor = new ControllerModifyProfessor(this);
        buttonUpdate.setOnAction(controllerModifyProfessor::handleUpdateCancelButtons);
        buttonCancel.setOnAction(controllerModifyProfessor::handleUpdateCancelButtons);
        HBox buttonPanel = new HBox(10, buttonUpdate, buttonCancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        HBox leftControls = new HBox(20, checkBoxPanel, toggleState);
        leftControls.setAlignment(Pos.CENTER_LEFT);
        BorderPane bottomRow = new BorderPane();
        bottomRow.setLeft(leftControls);
        bottomRow.setRight(buttonPanel);
        VBox mainPanel = new VBox(20, title, formGrid, bottomRow);
        mainPanel.setPadding(new Insets(24, 32, 24, 32));
        Scene scene = new Scene(mainPanel, 570, 420);
        stage.setTitle("Modificar Profesor");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateNames(textFieldName.getText().trim(), "Nombre", errors);
        GUIUtils.validateNames(textFieldLastName.getText().trim(), "Apellidos", errors);
        GUIUtils.validateEmail(textFieldEmail.getText().trim(), errors);
        GUIUtils.validateComboBoxSelection(comboBoxGender.getValue(), "Género", errors);
        GUIUtils.validateComboBoxSelection(comboBoxShift.getValue(), "Turno", errors);
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
        GUIUtils.closeWindow((Stage) buttonCancel.getScene().getWindow());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Professor getProfessor() {
        return professor;
    }

    public TextField getTextFieldName() {
        return textFieldName;
    }

    public TextField getTextFieldLastName() {
        return textFieldLastName;
    }

    public ComboBox<String> getComboBoxGender() {
        return comboBoxGender;
    }

    public ComboBox<String> getComboBoxShift() {
        return comboBoxShift;
    }

    public TextField getTextFieldEmail() {
        return textFieldEmail;
    }

    public CheckBox getCheckBoxIsCoordinator() {
        return checkBoxIsCoordinator;
    }

    public CheckBox getCheckBoxIsAdministrator() {
        return checkBoxIsAdministrator;
    }

    public ToggleButton getToggleState() {
        return toggleState;
    }

    public Button getButtonUpdate() {
        return buttonUpdate;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public Stage getStage() {
        return stage;
    }
}