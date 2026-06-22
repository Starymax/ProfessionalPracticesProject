package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerModifyProject;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.utils.SchoolPeriod;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class GUIModifyProject extends Application {

    private TextField textFieldName;
    private TextArea textAreaDescription;
    private TextField textFieldGeneralObjective;
    private TextArea textAreaImmediateObjectives;
    private TextArea textAreaMediateObjectives;
    private TextField textFieldMethodology;
    private TextArea textAreaResources;
    private ComboBox<SchoolPeriod> comboBoxPeriod;
    private DatePicker datePickerStartDate;
    private DatePicker datePickerFinalDate;
    private TextArea textAreaResponsibilities;
    private TextField textFieldAvailablePlaces;
    private ComboBox<Enterprise> comboBoxEnterprise;
    private ComboBox<ProjectManager> comboBoxProjectManager;
    private RadioButton radioButtonActive;
    private RadioButton radioButtonInactive;
    private ToggleGroup toggleGroupStatus;
    private Button buttonAddProjectManager;
    private Button buttonActivityPlan;
    private Button buttonContinue;
    private Button buttonCancel;
    private Stage stage;
    private Project project;
    private GridPane formGrid;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label labelTitle = new Label("Datos del proyecto:");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        textFieldName = new TextField();
        textAreaDescription = new TextArea();
        textFieldGeneralObjective = new TextField();
        textAreaImmediateObjectives = new TextArea();
        textAreaMediateObjectives = new TextArea();
        textFieldMethodology = new TextField();
        textAreaResources = new TextArea();
        comboBoxPeriod = new ComboBox<>();
        comboBoxPeriod.setPromptText("Seleccione periodo");
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
        textAreaImmediateObjectives.setPrefRowCount(2);
        textAreaImmediateObjectives.setWrapText(true);
        textAreaMediateObjectives.setPrefRowCount(2);
        textAreaMediateObjectives.setWrapText(true);
        textAreaResources.setPrefRowCount(2);
        textAreaResources.setWrapText(true);
        textAreaResponsibilities.setPrefRowCount(2);
        textAreaResponsibilities.setWrapText(true);
        textAreaDescription.setMaxWidth(Double.MAX_VALUE);
        datePickerStartDate.setPromptText("dd/mm/aaaa");
        datePickerStartDate.getEditor().setDisable(true);
        datePickerFinalDate.setPromptText("dd/mm/aaaa");
        datePickerFinalDate.getEditor().setDisable(true);
        GUIUtils.bindDatePickersToPeriodSelection(comboBoxPeriod, datePickerStartDate, datePickerFinalDate);
        comboBoxEnterprise.setMaxWidth(Double.MAX_VALUE);
        comboBoxProjectManager.setMaxWidth(Double.MAX_VALUE);

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

        comboBoxProjectManager.setCellFactory(listView -> new ListCell<>() {
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

        formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(180);
        labelColumn.setPrefWidth(180);

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);

        formGrid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        int row = 0;
        addNameRow(row++);
        addDescriptionRow(row++);
        addGeneralObjectiveRow(row++);
        addImmediateObjectivesRow(row++);
        addMediateObjectivesRow(row++);
        addMethodologyRow(row++);
        addResourcesRow(row++);

        Label labelPeriod = new Label("Periodo:");
        labelPeriod.setFont(Font.font("SansSerif", 13));
        HBox periodRow = new HBox(16, labelPeriod, comboBoxPeriod);
        periodRow.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(periodRow, 0, row++, 2, 1);

        Label labelStartDate = new Label("Fecha Inicio:");
        labelStartDate.setFont(Font.font("SansSerif", 13));
        Label labelEndDate = new Label("Fecha Fin:");
        labelEndDate.setFont(Font.font("SansSerif", 13));
        HBox dateRow = new HBox(16, labelStartDate, datePickerStartDate, labelEndDate, datePickerFinalDate);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(dateRow, 0, row++, 2, 1);

        addResponsibilitiesRow(row++);
        addAvailablePlacesRow(row++);
        addEnterpriseRow(row++);
        addProjectManagerRow(row++);

        toggleGroupStatus = new ToggleGroup();
        radioButtonActive = new RadioButton("Proyecto Activo");
        radioButtonInactive = new RadioButton("Proyecto Inactivo");
        radioButtonActive.setFont(Font.font("SansSerif", 13));
        radioButtonInactive.setFont(Font.font("SansSerif", 13));
        radioButtonActive.setToggleGroup(toggleGroupStatus);
        radioButtonInactive.setToggleGroup(toggleGroupStatus);
        radioButtonActive.setSelected(true);

        HBox statusRow = new HBox(32, radioButtonActive, radioButtonInactive);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(statusRow, 0, row++, 2, 1);

        buttonAddProjectManager = new Button("Añadir Responsable");
        buttonActivityPlan = new Button("Plan de Actividades");
        buttonContinue = new Button("Continuar");
        buttonCancel = new Button("Cancelar");


        ControllerModifyProject controllerModifyProject = new ControllerModifyProject(this);
        buttonAddProjectManager.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);
        buttonActivityPlan.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);
        buttonContinue.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);
        buttonCancel.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);
        comboBoxEnterprise.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);

        HBox buttonLeftPanel = new HBox(12, buttonAddProjectManager, buttonActivityPlan);
        buttonLeftPanel.setAlignment(Pos.CENTER_LEFT);
        HBox buttonRightPanel = new HBox(12, buttonContinue, buttonCancel);
        buttonRightPanel.setAlignment(Pos.CENTER_RIGHT);

        BorderPane buttonPanel = new BorderPane();
        buttonPanel.setLeft(buttonLeftPanel);
        buttonPanel.setRight(buttonRightPanel);

        VBox mainPanel = new VBox(16, labelTitle, formGrid, buttonPanel);
        mainPanel.setPadding(new Insets(24, 32, 24, 32));

        ScrollPane scrollPane = new ScrollPane(mainPanel);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 720, 790);
        GUIStyle.apply(scene);
        stage.setTitle("Modificar Proyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void addNameRow(int rowIndex) {
        addFormRow(rowIndex, "Nombre:", textFieldName);
    }

    private void addDescriptionRow(int rowIndex) {
        addFormRow(rowIndex, "Descripción:", textAreaDescription);
    }

    private void addGeneralObjectiveRow(int rowIndex) {
        addFormRow(rowIndex, "Objetivo General:", textFieldGeneralObjective);
    }

    private void addImmediateObjectivesRow(int rowIndex) {
        addFormRow(rowIndex, "Objetivos Inmediatos:", textAreaImmediateObjectives);
    }

    private void addMediateObjectivesRow(int rowIndex) {
        addFormRow(rowIndex, "Objetivos Mediatos:", textAreaMediateObjectives);
    }

    private void addMethodologyRow(int rowIndex) {
        addFormRow(rowIndex, "Metodología:", textFieldMethodology);
    }

    private void addResourcesRow(int rowIndex) {
        addFormRow(rowIndex, "Recursos humanos,\neconómicos y materiales:", textAreaResources);
    }

    private void addResponsibilitiesRow(int rowIndex) {
        addFormRow(rowIndex, "Responsabilidades:", textAreaResponsibilities);
    }

    private void addAvailablePlacesRow(int rowIndex) {
        addFormRow(rowIndex, "Lugares disponibles:", textFieldAvailablePlaces);
    }

    private void addEnterpriseRow(int rowIndex) {
        addFormRow(rowIndex, "Organizacion:", comboBoxEnterprise);
    }

    private void addProjectManagerRow(int rowIndex) {
        addFormRow(rowIndex, "Responsable:", comboBoxProjectManager);
    }

    private void addFormRow(int rowIndex, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("SansSerif", 13));
        label.setWrapText(true);
        if (field instanceof TextField textField) textField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setFillWidth(field, true);
        formGrid.add(label, 0, rowIndex);
        formGrid.add(field, 1, rowIndex);
    }

    public void loadProject(Project project) {
        this.project = project;
        textFieldName.setText(project.getNameProject());
        textAreaDescription.setText(project.getDescriptionProject());
        textFieldGeneralObjective.setText(project.getGeneralObjective());
        textAreaImmediateObjectives.setText(project.getImmediateObjectives());
        textAreaMediateObjectives.setText(project.getMediatesObjectives());
        textFieldMethodology.setText(project.getMethodology());
        loadPeriodAndDates(project);
        textAreaResponsibilities.setText(project.getResponsibilities());
        textAreaResources.setText(project.getResources());
        textFieldAvailablePlaces.setText(String.valueOf(project.getAvailablePlaces()));
        if (project.getActiveStatus()) {
            radioButtonActive.setSelected(true);
        } else {
            radioButtonInactive.setSelected(true);
        }
        verifyActivityPlan();
    }

    private void loadPeriodAndDates(Project project) {
        LocalDate startDate = project.getStartDate().toLocalDate();
        LocalDate finalDate = project.getFinalDate().toLocalDate();
        SchoolPeriod projectSchoolPeriod = SchoolPeriod.getPeriodByDate(startDate);
        SchoolPeriod currentSchoolPeriod = SchoolPeriod.currentPeriod(LocalDate.now());
        LinkedHashSet<SchoolPeriod> schoolPeriods = new LinkedHashSet<>();
        schoolPeriods.add(projectSchoolPeriod);
        schoolPeriods.add(currentSchoolPeriod);
        schoolPeriods.add(currentSchoolPeriod.getNextPeriod());
        comboBoxPeriod.getItems().setAll(schoolPeriods);
        comboBoxPeriod.setValue(projectSchoolPeriod);
        datePickerStartDate.setValue(startDate);
        datePickerFinalDate.setValue(finalDate);
    }

    private void verifyActivityPlan() {
        if (project != null) {
            try {
                ActivityDAO activityDAO = new ActivityDAO();
                boolean hasActivities = !activityDAO.getActivitiesByProjectId(project.getProjectId()).isEmpty();
                if (hasActivities) {
                    buttonActivityPlan.setDisable(true);
                }
            } catch (DataOperationException e) {
                showError("Error al verificar plan de actividades: " + e.getMessage());
            }
        }
    }

    public void loadEnterprises(List<Enterprise> enterprises) {
        comboBoxEnterprise.getItems().clear();
        comboBoxEnterprise.getItems().addAll(enterprises);
    }

    public void loadProjectManagers(List<ProjectManager> projectManagers) {
        comboBoxProjectManager.getItems().clear();
        comboBoxProjectManager.getItems().addAll(projectManagers);
    }

    public boolean validateFields() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateShortText(textFieldName.getText(), "Nombre", errors);
        GUIUtils.validateLongText(textAreaDescription.getText(), "Descripción", errors);
        GUIUtils.validateLongText(textFieldGeneralObjective.getText(), "Objetivo General", errors);
        GUIUtils.validateLongText(textAreaMediateObjectives.getText(), "Objetivos Mediatos", errors);
        GUIUtils.validateLongText(textAreaImmediateObjectives.getText(), "Objetivos Inmediatos", errors);
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
        if (comboBoxPeriod.getValue() == null) {
            errors.add("Debe seleccionar el periodo.");
        }
        if (datePickerStartDate.getValue() == null) {
            errors.add("El campo Fecha Inicial es obligatorio.");
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

    public void enableActivityPlanButton() {
        buttonActivityPlan.setDisable(false);
    }

    public void disableActivityPlanButton() {
        buttonActivityPlan.setDisable(true);
    }

    public void showError(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Project getProject() {
        return project;
    }

    public boolean isActiveSelected() {
        return radioButtonActive.isSelected();
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

    public TextArea getTextAreaImmediateObjectives() {
        return textAreaImmediateObjectives;
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

    public Button getButtonActivityPlan() {
        return buttonActivityPlan;
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