package mx.fei.gui.views;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.controllers.ControllerRegisterProject;
import mx.fei.logic.dto.Enterprise;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.logic.dto.ProjectManager;

import java.util.ArrayList;
import java.util.List;

public class GUIRegisterProject extends Application {

    private TextField textFieldName;
    private TextArea textAreaDescription;
    private TextField textFieldGeneralObjective;
    private TextArea textAreaImmediateObjective;
    private TextArea textAreaMediateObjectives;
    private TextField textFieldMethodology;
    private TextArea textAreaResources;
    private DatePicker datePickerStartDate;
    private DatePicker datePickerFinalDate;
    private TextArea textAreaResponsibilities;
    private TextField textFieldAvailablePlaces;
    private ComboBox<Enterprise> comboBoxEnterprise;
    private ComboBox<ProjectManager> comboBoxProjectManager;
    private Button buttonAddProjectManager;
    private Button buttonContinue;
    private Button buttonCancel;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label title = new Label("Datos del proyecto:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        textFieldName = new TextField();
        textAreaDescription = new TextArea();
        textFieldGeneralObjective = new TextField();
        textAreaImmediateObjective = new TextArea();
        textAreaMediateObjectives = new TextArea();
        textFieldMethodology = new TextField();
        textAreaResources = new TextArea();
        datePickerStartDate = new DatePicker();
        datePickerStartDate.getEditor().setDisable(true);
        datePickerFinalDate = new DatePicker();
        datePickerFinalDate.getEditor().setDisable(true);
        textAreaResponsibilities = new TextArea();
        textFieldAvailablePlaces = new TextField();
        comboBoxEnterprise = new ComboBox<>();
        comboBoxProjectManager = new ComboBox<>();

        textAreaDescription.setPrefRowCount(4);
        textAreaDescription.setWrapText(true);
        textAreaImmediateObjective.setPrefRowCount(4);
        textAreaImmediateObjective.setWrapText(true);
        textAreaMediateObjectives.setPrefRowCount(4);
        textAreaMediateObjectives.setWrapText(true);
        textAreaResources.setPrefRowCount(4);
        textAreaResources.setWrapText(true);
        textAreaResponsibilities.setPrefRowCount(4);
        textAreaResponsibilities.setWrapText(true);

        datePickerStartDate.setPromptText("dd/mm/aaaa");
        datePickerFinalDate.setPromptText("dd/mm/aaaa");

        comboBoxEnterprise.setMaxWidth(Double.MAX_VALUE);
        comboBoxEnterprise.setCellFactory(listView -> new ListCell<>() {
            @Override protected void updateItem(Enterprise enterprise, boolean empty) {
                super.updateItem(enterprise, empty);
                setText(empty || enterprise == null ? null : enterprise.getName());
            }
        });

        comboBoxEnterprise.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Enterprise enterprise, boolean empty) {
                super.updateItem(enterprise, empty);
                setText(empty || enterprise == null ? null : enterprise.getName());
            }
        });

        comboBoxProjectManager.setMaxWidth(Double.MAX_VALUE);
        comboBoxProjectManager.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(ProjectManager projectManager, boolean empty) {
                super.updateItem(projectManager, empty);
                setText(empty || projectManager == null ? null : projectManager.getName());
            }
        });
        comboBoxProjectManager.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(ProjectManager projectManager, boolean empty) {
                super.updateItem(projectManager, empty);
                setText(empty || projectManager == null ? null : projectManager.getName());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.getColumnConstraints().addAll(columnConstraint(160), columnConstraint(Double.MAX_VALUE));

        int row = 0;
        addRow(form, row++, "Nombre:", textFieldName);
        addRow(form, row++, "Descripción:", textAreaDescription);
        addRow(form, row++, "Objetivo General:", textFieldGeneralObjective);
        addRow(form, row++, "Objetivos Inmediatos:", textAreaImmediateObjective);
        addRow(form, row++, "Objetivos Mediatos:", textAreaMediateObjectives);
        addRow(form, row++, "Metodología:", textFieldMethodology);
        addRow(form, row++, "Recursos humanos,\neconómicos y materiales:", textAreaResources);

        HBox dateRow = new HBox(16);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        dateRow.getChildren().addAll(new Label("Fecha Inicio:"), datePickerStartDate, new Label("Fecha Fin:"), datePickerFinalDate);
        form.add(dateRow, 0, row++, 2, 1);

        addRow(form, row++, "Responsabilidades:", textAreaResponsibilities);
        addRow(form, row++, "Lugares disponibles:", textFieldAvailablePlaces);
        addRow(form, row++, "Organizacion:", comboBoxEnterprise);
        addRow(form, row, "Responsable:", comboBoxProjectManager);

        buttonAddProjectManager = new Button("Añadir Responsable");
        buttonContinue = new Button("Continuar");
        buttonCancel = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand;";
        buttonAddProjectManager.setStyle(buttonStyle);
        buttonContinue.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);

        ControllerRegisterProject controllerRegisterProject = new ControllerRegisterProject(this);
        buttonAddProjectManager.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueButtonsAndEnterpriseComboBox);
        buttonContinue.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueButtonsAndEnterpriseComboBox);
        buttonCancel.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueButtonsAndEnterpriseComboBox);
        comboBoxEnterprise.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueButtonsAndEnterpriseComboBox);

        HBox buttonRow = new HBox(12, buttonAddProjectManager, buttonContinue, buttonCancel);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox mainPanel = new VBox(16, title, form, buttonRow);
        mainPanel.setPadding(new Insets(24, 32, 24, 32));

        ScrollPane scrollPane = new ScrollPane(mainPanel);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 680, 850);
        stage.setTitle("Registrar Proyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void addRow(GridPane grid, int row, String labelText, Node field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("SansSerif", 13));
        label.setWrapText(true);
        GridPane.setFillWidth(field, true);
        if (field instanceof TextField textField) {
            textField.setMaxWidth(Double.MAX_VALUE);
        }
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private ColumnConstraints columnConstraint(double width) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        if (width == Double.MAX_VALUE) {
            columnConstraints.setHgrow(Priority.ALWAYS);
        } else {
            columnConstraints.setMinWidth(width);
            columnConstraints.setPrefWidth(width);
        }
        return columnConstraints;
    }

    public boolean validateFields() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateShortText(textFieldName.getText(), "Nombre", errors);
        GUIUtils.validateLongText(textAreaDescription.getText(), "Descripción", errors);
        GUIUtils.validateLongText(textFieldGeneralObjective.getText(), "Objetivo General", errors);
        GUIUtils.validateLongText(textAreaMediateObjectives.getText(), "Objetivos Mediatos", errors);
        GUIUtils.validateLongText(textAreaImmediateObjective.getText(), "Objetivos Inmediatos", errors);
        GUIUtils.validateLongText(textFieldMethodology.getText(), "Metodología", errors);
        GUIUtils.validateLongText(textAreaResources.getText(), "Recursos", errors);
        GUIUtils.validateLongText(textAreaResponsibilities.getText(), "Responsabilidades", errors);
        GUIUtils.validateShortInt(textFieldAvailablePlaces.getText(), "Lugares Disponibles", errors);
        if (comboBoxEnterprise.getValue() == null) {
            errors.add("El campo Organizacion es obligatorio");
        }
        if (comboBoxProjectManager.getValue() == null) {
            errors.add("El campo Responsable es obligatorio");
        }
        if (datePickerStartDate.getValue() == null) {
            errors.add("Los campos de Fecha son obligatorios.");
        } else if (datePickerFinalDate.getValue() == null) {
            errors.add("El campo Fecha Final es obligatorio.");
        } else if (datePickerStartDate.getValue().isAfter(datePickerFinalDate.getValue()) || datePickerStartDate.getValue().isEqual(datePickerFinalDate.getValue())) {
            errors.add("La fecha final no puede ser anterior a la inicial");
        }
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            validated = false;
        }
        return validated;
    }

    public void loadEnterprises(List<Enterprise> enterprises) {
        comboBoxEnterprise.getItems().clear();
        comboBoxEnterprise.getItems().addAll(enterprises);
    }

    public void loadProjectManagers(List<ProjectManager> projectManagers) {
        comboBoxProjectManager.getItems().clear();
        comboBoxProjectManager.getItems().addAll(projectManagers);
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

    public TextArea getTextAreaDescription() {
        return textAreaDescription;
    }

    public TextField getTextFieldGeneralObjective() {
        return textFieldGeneralObjective;
    }

    public TextArea getTextAreaImmediateObjective() {
        return textAreaImmediateObjective;
    }

    public TextArea getTextAreaMediateObjectives() {
        return textAreaMediateObjectives;
    }

    public TextField getTextFieldMethodology() {
        return textFieldMethodology;
    }

    public TextArea getTextAreaResources() {
        return textAreaResources;
    }

    public DatePicker getDatePickerStartDate() {
        return datePickerStartDate;
    }

    public DatePicker getDatePickerFinalDate() {
        return datePickerFinalDate;
    }

    public TextArea getTextAreaResponsibilities() {
        return textAreaResponsibilities;
    }

    public TextField getTextFieldAvailablePlaces() {
        return textFieldAvailablePlaces;
    }

    public ComboBox<Enterprise> getComboBoxEnterprise() {
        return comboBoxEnterprise;
    }

    public ComboBox<ProjectManager> getComboBoxProjectManager() {
        return comboBoxProjectManager;
    }

    public Button getButtonAddProjectManager() {
        return buttonAddProjectManager;
    }

    public Button getButtonContinue() {
        return buttonContinue;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}