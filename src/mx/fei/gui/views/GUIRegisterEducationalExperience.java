package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;

import mx.fei.gui.controllers.ControllerRegisterEducationalExperience;
import mx.fei.gui.utils.GUIUtils;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GUIRegisterEducationalExperience extends Application {

    private TextField textFieldNrc;
    private TextField textFieldName;
    private TextField textFieldCareer;
    private ComboBox<String> comboBoxSemester;
    private ComboBox<Integer> comboBoxYear;
    private Button buttonRegister;
    private Button buttonCancel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Registrar Experiencia Educativa");
        stage.setResizable(false);
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20, 30, 20, 30));
        formGrid.getStyleClass().add("form-panel");
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
        formGrid.add(new Label("Semestre:"), 0, 4);
        comboBoxSemester = new ComboBox<>(FXCollections.observableArrayList("Febrero - Julio", "Agosto - Enero"));
        comboBoxSemester.setPrefWidth(220);
        comboBoxSemester.setPromptText("Seleccione semestre");
        formGrid.add(comboBoxSemester, 1, 4);
        formGrid.add(new Label("Año:"), 0, 5);
        int currentYear = LocalDate.now().getYear();
        comboBoxYear = new ComboBox<>(FXCollections.observableArrayList(currentYear, currentYear + 1));
        comboBoxYear.setPrefWidth(220);
        comboBoxYear.setPromptText("Seleccione año");
        formGrid.add(comboBoxYear, 1, 5);
        ControllerRegisterEducationalExperience controller = new ControllerRegisterEducationalExperience(this);
        buttonRegister = new Button("Registrar");
        buttonCancel = new Button("Cancelar");
        buttonRegister.setPrefWidth(110);
        buttonCancel.setPrefWidth(110);
        buttonRegister.setOnAction(controller::handleRegisterCancelButtons);
        buttonCancel.setOnAction(controller::handleRegisterCancelButtons);
        HBox buttonsBox = new HBox(30, buttonRegister, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        GridPane.setColumnSpan(buttonsBox, 2);
        GridPane.setHalignment(buttonsBox, HPos.CENTER);
        formGrid.add(buttonsBox, 0, 6);
        StackPane mainPanel = new StackPane(formGrid);
        mainPanel.setPadding(new Insets(20));
        Scene scene = new Scene(mainPanel);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public String getSelectedPeriod() {
        if (comboBoxSemester == null || comboBoxYear == null) {
            return "";
        }
        String semester = comboBoxSemester.getValue();
        Integer year = comboBoxYear.getValue();
        if (semester == null || year == null) {
            return "";
        }
        String month;
        if ("Febrero - Julio".equals(semester)) {
            month = "02";
        } else if ("Agosto - Enero".equals(semester)) {
            month = "08";
        } else {
            return "";
        }
        return String.format("%d-%s", year, month);
    }

    public boolean validateFields() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateNRC(textFieldNrc.getText().trim(), "NRC:", errors);
        GUIUtils.validateShortText(textFieldName.getText().trim(), "Nombre", errors);
        GUIUtils.validateShortText(textFieldCareer.getText().trim(), "Carrera", errors);
        if (comboBoxSemester.getValue() == null || comboBoxSemester.getValue().isBlank()) {
            errors.add("Seleccione el semestre.");
        }
        if (comboBoxYear.getValue() == null) {
            errors.add("Seleccione el año.");
        }
        String selectedPeriod = getSelectedPeriod();
        if (selectedPeriod.isBlank()) {
            errors.add("El periodo seleccionado no es valido.");
        } else {
            GUIUtils.validatePeriod(selectedPeriod, errors);
        }
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

    public Button getButtonRegister() {
        return buttonRegister;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }
}