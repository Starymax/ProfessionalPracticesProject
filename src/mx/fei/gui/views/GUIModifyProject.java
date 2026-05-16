package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerModifyProject;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.ProjectManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

import java.util.ArrayList;
import java.util.List;

public class GUIModifyProject extends Application {

    private TextField textFieldName;
    private TextArea textAreaDescription;
    private TextField textFieldGeneralObjective;
    private TextField textFieldImmediateObjectives;
    private TextField textFieldMediateObjectives;
    private TextField textFieldMethodology;
    private TextField textFieldResources;
    private DatePicker datePickerStartDate;
    private DatePicker datePickerEndDate;
    private TextField textFieldResponsibilities;
    private TextField textFieldAvailableSpots;
    private ComboBox<Enterprise> comboBoxEnterprise;
    private ComboBox<ProjectManager> comboBoxProjectManager;
    private RadioButton radioButtonActive;
    private RadioButton radioButtonInactive;
    private ToggleGroup toggleGroupStatus;
    private Button buttonAddProjectManager;
    private Button buttonContinue;
    private Button buttonCancel;
    private Stage stage;
    private Project project;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label labelTitle = new Label("Datos del proyecto:");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        textFieldName = new TextField();
        textAreaDescription = new TextArea();
        textFieldGeneralObjective = new TextField();
        textFieldImmediateObjectives = new TextField();
        textFieldMediateObjectives = new TextField();
        textFieldMethodology = new TextField();
        textFieldResources = new TextField();
        datePickerStartDate = new DatePicker();
        datePickerEndDate = new DatePicker();
        textFieldResponsibilities = new TextField();
        textFieldAvailableSpots = new TextField();
        comboBoxEnterprise = new ComboBox<>();
        comboBoxProjectManager = new ComboBox<>();

        textAreaDescription.setPrefRowCount(4);
        textAreaDescription.setWrapText(true);
        textAreaDescription.setMaxWidth(Double.MAX_VALUE);
        datePickerStartDate.setPromptText("dd/mm/aaaa");
        datePickerStartDate.getEditor().setDisable(true);
        datePickerEndDate.setPromptText("dd/mm/aaaa");
        datePickerEndDate.getEditor().setDisable(true);
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

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(180);
        labelColumn.setPrefWidth(180);

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);

        formGrid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        int row = 0;
        addFormRow(formGrid, row++, "Nombre:", textFieldName);
        addFormRow(formGrid, row++, "Descripción:", textAreaDescription);
        addFormRow(formGrid, row++, "Objetivo General:", textFieldGeneralObjective);
        addFormRow(formGrid, row++, "Objetivos Inmediatos:", textFieldImmediateObjectives);
        addFormRow(formGrid, row++, "Objetivos Mediatos:", textFieldMediateObjectives);
        addFormRow(formGrid, row++, "Metodología:", textFieldMethodology);
        addFormRow(formGrid, row++, "Recursos humanos,\neconómicos y materiales:", textFieldResources);

        Label labelStartDate = new Label("Fecha Inicio:");
        labelStartDate.setFont(Font.font("SansSerif", 13));
        Label labelEndDate = new Label("Fecha Fin:");
        labelEndDate.setFont(Font.font("SansSerif", 13));
        HBox dateRow = new HBox(16, labelStartDate, datePickerStartDate, labelEndDate, datePickerEndDate);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(dateRow, 0, row++, 2, 1);

        addFormRow(formGrid, row++, "Responsabilidades:", textFieldResponsibilities);
        addFormRow(formGrid, row++, "Lugares disponibles:", textFieldAvailableSpots);
        addFormRow(formGrid, row++, "Organizacion:", comboBoxEnterprise);
        addFormRow(formGrid, row++, "Responsable:", comboBoxProjectManager);

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
        buttonContinue = new Button("Continuar");
        buttonCancel = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand;";
        buttonAddProjectManager.setStyle(buttonStyle);
        buttonContinue.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);

        ControllerModifyProject controllerModifyProject = new ControllerModifyProject(this);
        buttonAddProjectManager.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);
        buttonContinue.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);
        buttonCancel.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);
        comboBoxEnterprise.setOnAction(controllerModifyProject::handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox);

        HBox buttonRightPanel = new HBox(12, buttonContinue, buttonCancel);
        buttonRightPanel.setAlignment(Pos.CENTER_RIGHT);

        BorderPane buttonPanel = new BorderPane();
        buttonPanel.setLeft(buttonAddProjectManager);
        buttonPanel.setRight(buttonRightPanel);

        VBox mainPanel = new VBox(16, labelTitle, formGrid, buttonPanel);
        mainPanel.setPadding(new Insets(24, 32, 24, 32));

        ScrollPane scrollPane = new ScrollPane(mainPanel);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 620, 680);
        stage.setTitle("Modificar Proyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void addFormRow(GridPane grid, int rowIndex, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("SansSerif", 13));
        label.setWrapText(true);
        if (field instanceof TextField textField) textField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setFillWidth(field, true);
        grid.add(label, 0, rowIndex);
        grid.add(field, 1, rowIndex);
    }

    public void loadProject(Project project) {
        this.project = project;
        textFieldName.setText(project.getNameProject());
        textAreaDescription.setText(project.getDescriptionProject());
        textFieldGeneralObjective.setText(project.getGeneralObjective());
        textFieldImmediateObjectives.setText(project.getImmediateObjectives());
        textFieldMediateObjectives.setText(project.getMediatesObjectives());
        textFieldMethodology.setText(project.getMethodology());
        datePickerStartDate.setValue(project.getStartDate().toLocalDate());
        datePickerEndDate.setValue(project.getFinalDate().toLocalDate());
        textFieldResponsibilities.setText(project.getResponsibilities());
        textFieldResources.setText(project.getResources());
        textFieldAvailableSpots.setText(String.valueOf(project.getAvailablePlaces()));
        if (project.getActiveStatus()) {
            radioButtonActive.setSelected(true);
        } else {
            radioButtonInactive.setSelected(true);
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
        GUIUtils.validateLongText(textFieldMediateObjectives.getText(), "Objetivos Mediatos", errors);
        GUIUtils.validateLongText(textFieldImmediateObjectives.getText(), "Objetivos Inmediatos", errors);
        GUIUtils.validateLongText(textFieldMethodology.getText(), "Metodología", errors);
        GUIUtils.validateLongText(textFieldResources.getText(), "Recursos", errors);
        GUIUtils.validateLongText(textFieldResponsibilities.getText(), "Responsabilidades", errors);
        GUIUtils.validateShortInt(textFieldAvailableSpots.getText(), "Lugares Disponibles", errors);
        if (comboBoxEnterprise.getValue() == null) {
            errors.add("El campo Organizacion es obligatorio");
        }
        if (comboBoxProjectManager.getValue() == null) {
            errors.add("El campo Responsable es obligatorio");
        }
        if (datePickerStartDate.getValue() == null) {
            errors.add("El campo Fecha Inicial es obligatorio.");
        }
        if (datePickerEndDate.getValue() == null) {
            errors.add("El campo Fecha Final es obligatorio.");
        }
        if (datePickerStartDate.getValue().isAfter(datePickerEndDate.getValue()) || datePickerStartDate.getValue().isEqual(datePickerEndDate.getValue())) {
            errors.add("La fecha final no puede ser anterior a la inicial");
        }
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            validated = false;
        }
        return validated;
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    public TextField getTextFieldImmediateObjectives() {
        return textFieldImmediateObjectives;
    }

    public TextField getTextFieldMediateObjectives() {
        return textFieldMediateObjectives;
    }

    public TextField getTextFieldMethodology() {
        return textFieldMethodology;
    }

    public TextField getTextFieldResources() {
        return textFieldResources;
    }

    public DatePicker getDatePickerStartDate() {
        return datePickerStartDate;
    }

    public DatePicker getDatePickerEndDate() {
        return datePickerEndDate;
    }

    public TextField getTextFieldResponsibilities() {
        return textFieldResponsibilities;
    }

    public TextField getTextFieldAvailableSpots() {
        return textFieldAvailableSpots;
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