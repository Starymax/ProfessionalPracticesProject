package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;

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
    private Button buttonSave;
    private Button buttonActivityPlan;
    private Button buttonCancel;
    private Stage stage;
    private GridPane form;

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

        textAreaDescription.setPrefRowCount(3);
        textAreaDescription.setWrapText(true);
        textAreaImmediateObjective.setPrefRowCount(2);
        textAreaImmediateObjective.setWrapText(true);
        textAreaMediateObjectives.setPrefRowCount(2);
        textAreaMediateObjectives.setWrapText(true);
        textAreaResources.setPrefRowCount(2);
        textAreaResources.setWrapText(true);
        textAreaResponsibilities.setPrefRowCount(2);
        textAreaResponsibilities.setWrapText(true);

        datePickerStartDate.setPromptText("dd/mm/aaaa");
        datePickerFinalDate.setPromptText("dd/mm/aaaa");
        GUIUtils.applyPeriodDateRestrictions(datePickerStartDate, datePickerFinalDate);

        comboBoxEnterprise.setMaxWidth(Double.MAX_VALUE);
        comboBoxEnterprise.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Enterprise enterprise, boolean empty) {
                super.updateItem(enterprise, empty);
                setText(empty || enterprise == null ? null : enterprise.getName());
            }
        });
        comboBoxEnterprise.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Enterprise enterprise, boolean empty) {
                super.updateItem(enterprise, empty);
                setText(empty || enterprise == null ? null : enterprise.getName());
            }
        });

        comboBoxProjectManager.setMaxWidth(Double.MAX_VALUE);
        comboBoxProjectManager.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ProjectManager projectManager, boolean empty) {
                super.updateItem(projectManager, empty);
                setText(empty || projectManager == null ? null : projectManager.getName());
            }
        });
        comboBoxProjectManager.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProjectManager projectManager, boolean empty) {
                super.updateItem(projectManager, empty);
                setText(empty || projectManager == null ? null : projectManager.getName());
            }
        });

        form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.getColumnConstraints().addAll(columnConstraint(160), columnConstraint(Double.MAX_VALUE));

        int row = 0;
        addNameRow(row++);
        addDescriptionRow(row++);
        addGeneralObjectiveRow(row++);
        addImmediateObjectivesRow(row++);
        addMediateObjectivesRow(row++);
        addMethodologyRow(row++);
        addResourcesRow(row++);

        HBox dateRow = new HBox(16);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        dateRow.getChildren().addAll(new Label("Fecha Inicio:"), datePickerStartDate, new Label("Fecha Fin:"), datePickerFinalDate);
        form.add(dateRow, 0, row++, 2, 1);

        addResponsibilitiesRow(row++);
        addAvailablePlacesRow(row++);
        addEnterpriseRow(row++);
        addProjectManagerRow(row);

        buttonAddProjectManager = new Button("Añadir Responsable");
        buttonSave = new Button("Guardar");
        buttonActivityPlan = new Button("Plan de Actividades");
        buttonCancel = new Button("Cancelar");


        ControllerRegisterProject controllerRegisterProject = new ControllerRegisterProject(this);
        buttonAddProjectManager.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueActivityPlanButtonsAndEnterpriseComboBox);
        buttonSave.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueActivityPlanButtonsAndEnterpriseComboBox);
        buttonActivityPlan.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueActivityPlanButtonsAndEnterpriseComboBox);
        buttonCancel.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueActivityPlanButtonsAndEnterpriseComboBox);
        comboBoxEnterprise.setOnAction(controllerRegisterProject::handleAddProjectManagerContinueActivityPlanButtonsAndEnterpriseComboBox);

        buttonActivityPlan.setDisable(true);

        HBox buttonRow = new HBox(12, buttonAddProjectManager, buttonSave, buttonActivityPlan, buttonCancel);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox mainPanel = new VBox(16, title, form, buttonRow);
        mainPanel.setPadding(new Insets(24, 32, 24, 32));

        ScrollPane scrollPane = new ScrollPane(mainPanel);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 680, 710);
        GUIStyle.apply(scene);
        stage.setTitle("Registrar Proyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void addNameRow(int rowIndex) {
        addRow(rowIndex, "Nombre:", textFieldName);
    }

    private void addDescriptionRow(int rowIndex) {
        addRow(rowIndex, "Descripción:", textAreaDescription);
    }

    private void addGeneralObjectiveRow(int rowIndex) {
        addRow(rowIndex, "Objetivo General:", textFieldGeneralObjective);
    }

    private void addImmediateObjectivesRow(int rowIndex) {
        addRow(rowIndex, "Objetivos Inmediatos:", textAreaImmediateObjective);
    }

    private void addMediateObjectivesRow(int rowIndex) {
        addRow(rowIndex, "Objetivos Mediatos:", textAreaMediateObjectives);
    }

    private void addMethodologyRow(int rowIndex) {
        addRow(rowIndex, "Metodología:", textFieldMethodology);
    }

    private void addResourcesRow(int rowIndex) {
        addRow(rowIndex, "Recursos humanos,\neconómicos y materiales:", textAreaResources);
    }

    private void addResponsibilitiesRow(int rowIndex) {
        addRow(rowIndex, "Responsabilidades:", textAreaResponsibilities);
    }

    private void addAvailablePlacesRow(int rowIndex) {
        addRow(rowIndex, "Lugares disponibles:", textFieldAvailablePlaces);
    }

    private void addEnterpriseRow(int rowIndex) {
        addRow(rowIndex, "Organizacion:", comboBoxEnterprise);
    }

    private void addProjectManagerRow(int rowIndex) {
        addRow(rowIndex, "Responsable:", comboBoxProjectManager);
    }

    private void addRow(int row, String labelText, Node field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("SansSerif", 13));
        label.setWrapText(true);
        GridPane.setFillWidth(field, true);
        if (field instanceof TextField textField) {
            textField.setMaxWidth(Double.MAX_VALUE);
        }
        form.add(label, 0, row);
        form.add(field, 1, row);
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

    public void enableActivityPlanButton() {
        buttonActivityPlan.setDisable(false);
    }

    public void disableActivityPlanButton() {
        buttonActivityPlan.setDisable(true);
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

    public Button getButtonSave() {
        return buttonSave;
    }

    public Button getButtonActivityPlan() {
        return buttonActivityPlan;
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