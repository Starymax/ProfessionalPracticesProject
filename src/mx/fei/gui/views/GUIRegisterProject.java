package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerRegisterProject;
import mx.fei.logic.dto.Enterprise;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.logic.dto.ProjectManager;

import java.util.List;
import java.util.Map;

public class GUIRegisterProject extends Application {

    private TextField nameTextField;
    private TextArea descriptionTextArea;
    private TextField generalObjectiveTextField;
    private TextField immediateObjectivesTextField;
    private TextField mediateObjectivesTextField;
    private TextField methodologyTextField;
    private TextField resourcesTextField;
    private DatePicker initialDatePicker;
    private DatePicker finalDatePicker;
    private TextField responsabilitiesTextField;
    private TextField availablePlacesTextField;
    private ComboBox<Enterprise> enterpriseComboBox;
    private ComboBox<ProjectManager> projectManagerComboBox;
    private Button addProjectManagerButton;
    private Button continueButton;
    private Button cancelButton;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label title = new Label("Datos del proyecto:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        nameTextField = new TextField();
        descriptionTextArea = new TextArea();
        generalObjectiveTextField = new TextField();
        immediateObjectivesTextField = new TextField();
        mediateObjectivesTextField = new TextField();
        methodologyTextField = new TextField();
        resourcesTextField = new TextField();
        initialDatePicker = new DatePicker();
        finalDatePicker = new DatePicker();
        responsabilitiesTextField = new TextField();
        availablePlacesTextField = new TextField();
        enterpriseComboBox = new ComboBox<>();
        projectManagerComboBox = new ComboBox<>();

        descriptionTextArea.setPrefRowCount(4);
        descriptionTextArea.setWrapText(true);

        initialDatePicker.setPromptText("dd/mm/aaaa");
        finalDatePicker.setPromptText("dd/mm/aaaa");

        enterpriseComboBox.setMaxWidth(Double.MAX_VALUE);
        enterpriseComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override protected void updateItem(Enterprise enterprise, boolean empty) {
                super.updateItem(enterprise, empty);
                setText(empty || enterprise == null ? null : enterprise.getName());
            }
        });

        enterpriseComboBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Enterprise enterprise, boolean empty) {
                super.updateItem(enterprise, empty);
                setText(empty || enterprise == null ? null : enterprise.getName());
            }
        });

        projectManagerComboBox.setMaxWidth(Double.MAX_VALUE);
        projectManagerComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(ProjectManager projectManager, boolean empty) {
                super.updateItem(projectManager, empty);
                setText(empty || projectManager == null ? null : projectManager.getName());
            }
        });
        projectManagerComboBox.setButtonCell(new ListCell<>() {
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
        addRow(form, row++, "Nombre:", nameTextField);
        addRow(form, row++, "Descripción:", descriptionTextArea);
        addRow(form, row++, "Objetivo General:", generalObjectiveTextField);
        addRow(form, row++, "Objetivos Inmediatos:", immediateObjectivesTextField);
        addRow(form, row++, "Objetivos Mediatos:", mediateObjectivesTextField);
        addRow(form, row++, "Metodología:", methodologyTextField);
        addRow(form, row++, "Recursos humanos,\neconómicos y materiales:", resourcesTextField);

        HBox dateRow = new HBox(16);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        dateRow.getChildren().addAll(new Label("Fecha Inicio:"), initialDatePicker, new Label("Fecha Fin:"), finalDatePicker);
        form.add(dateRow, 0, row++, 2, 1);

        addRow(form, row++, "Responsabilidades:", responsabilitiesTextField);
        addRow(form, row++, "Lugares disponibles:", availablePlacesTextField);
        addRow(form, row++, "Organizacion:", enterpriseComboBox);
        addRow(form, row, "Responsable:", projectManagerComboBox);

        addProjectManagerButton = new Button("Añadir Responsable");
        continueButton = new Button("Continuar");
        cancelButton = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand;";
        addProjectManagerButton.setStyle(buttonStyle);
        continueButton.setStyle(buttonStyle);
        cancelButton.setStyle(buttonStyle);

        ControllerRegisterProject controllerRegisterProject = new ControllerRegisterProject(this);
        addProjectManagerButton.setOnAction(event ->  controllerRegisterProject.handleButtonAction(event));
        continueButton.setOnAction(event ->  controllerRegisterProject.handleButtonAction(event));
        cancelButton.setOnAction(event ->  controllerRegisterProject.handleButtonAction(event));

        HBox buttonRow = new HBox(12, addProjectManagerButton, continueButton, cancelButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox mainPanel = new VBox(16, title, form, buttonRow);
        mainPanel.setPadding(new Insets(24, 32, 24, 32));

        ScrollPane scrollPane = new ScrollPane(mainPanel);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 680, 620);
        stage.setTitle("GUIRegistrarProyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void addRow(GridPane grid, int row, String labelText, javafx.scene.Node field) {
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
        List<Map.Entry<Boolean, String>> validations = List.of(
                Map.entry(nameTextField.getText().isEmpty(), "El campo nombre es obligatorio."),
                Map.entry(descriptionTextArea.getText().isEmpty(), "El campo descripcion es obligatorio."),
                Map.entry(generalObjectiveTextField.getText().isEmpty(), "El campo Objetivo General es obligatorio."),
                Map.entry(mediateObjectivesTextField.getText().isEmpty(), "El campo Objetivos Mediatos es obligatorio."),
                Map.entry(immediateObjectivesTextField.getText().isEmpty(), "El campo Objetivos Inmediatos es obligatorio."),
                Map.entry(methodologyTextField.getText().isEmpty(), "El campo Metodología es obligatorio."),
                Map.entry(resourcesTextField.getText().isEmpty(), "El campo Recursos es obligatorio."),
                Map.entry(initialDatePicker.getValue() == null, "El campo Fecha Inicial es obligatorio."),
                Map.entry(finalDatePicker.getValue() == null, "El campo Fecha Final es obligatorio."),
                Map.entry(responsabilitiesTextField.getText().isEmpty(), "El campo Responsabilidades es obligatorio."),
                Map.entry(availablePlacesTextField.getText().isEmpty(), "El campo Lugares Disponibles es obligatorio."),
                Map.entry(enterpriseComboBox.getSelectionModel().isEmpty(), "El campo Organización es obligatorio."),
                Map.entry(projectManagerComboBox.getSelectionModel().isEmpty(), "El campo Responsable es obligatorio.")
        );
        for (var validation : validations) {
            if (validation.getKey()) {
                showError(validation.getValue());
                validated = false;
                break;
            }
        }
        return validated;
    }

    public boolean validateFieldInt() {
        boolean intValidated = true;
        if (!availablePlacesTextField.getText().trim().matches("^\\d+$")) {
            showError("El campo Lugares Disponibles debe incluir solo números.");
            intValidated = false;
        }
        return intValidated;
    }

    public void loadEnterprises(List<Enterprise> enterprises) {
        enterpriseComboBox.getItems().clear();
        enterpriseComboBox.getItems().addAll(enterprises);
    }

    public void loadProjectManagers(List<ProjectManager> projectManagers) {
        projectManagerComboBox.getItems().clear();
        projectManagerComboBox.getItems().addAll(projectManagers);
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Campo requerido");
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

    public TextField getNameTextField() {
        return nameTextField;
    }

    public TextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    public TextField getGeneralObjectiveTextField() {
        return generalObjectiveTextField;
    }

    public TextField getImmediateObjectivesTextField() {
        return immediateObjectivesTextField;
    }

    public TextField getMediateObjectivesTextField() {
        return mediateObjectivesTextField;
    }

    public TextField getMethodologyTextField() {
        return methodologyTextField;
    }

    public TextField getResourcesTextField() {
        return resourcesTextField;
    }

    public DatePicker getInitialDatePicker() {
        return initialDatePicker;
    }

    public DatePicker getFinalDatePicker() {
        return finalDatePicker;
    }

    public TextField getResponsabilitiesTextField() {
        return responsabilitiesTextField;
    }

    public TextField getAvailablePlacesTextField() {
        return availablePlacesTextField;
    }

    public ComboBox<Enterprise> getEnterpriseComboBox() {
        return enterpriseComboBox;
    }

    public ComboBox<ProjectManager> getProjectManagerComboBox() {
        return projectManagerComboBox;
    }

    public Button getAddProjectManagerButton() {
        return addProjectManagerButton;
    }

    public Button getContinueButton() {
        return continueButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}