package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerSelectProjects;
import mx.fei.logic.dto.Project;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;

import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Priority;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUISelectProjects extends Application {

    private static final String ALL_ENTERPRISES = "Todas las empresas";

    private Button buttonSelect;
    private Button buttonCancel;
    private TextField searchField;
    private ComboBox<String> enterpriseFilter;
    private VBox vBoxProjectList;
    private Stage stage;
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final List<HBox> projectRows = new ArrayList<>();
    private Student student;
    private boolean isModify;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Proyectos disponibles");
        title.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));

        searchField = new TextField();
        searchField.setPromptText("Buscar por nombre de proyecto...");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        enterpriseFilter = new ComboBox<>();
        enterpriseFilter.setPrefWidth(220);
        enterpriseFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        HBox filterRow = new HBox(10, searchField, enterpriseFilter);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        vBoxProjectList = new VBox(6);
        vBoxProjectList.setPadding(new Insets(10));
        vBoxProjectList.getStyleClass().add("bg-white");

        isModify = false;

        ScrollPane scrollPane = new ScrollPane(vBoxProjectList);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("card-scroll");
        scrollPane.setPrefHeight(280);

        VBox listPanel = new VBox(10, title, filterRow, scrollPane);
        listPanel.setPadding(new Insets(16));
        listPanel.getStyleClass().add("card-panel");

        buttonSelect = new Button("Seleccionar");
        buttonCancel = new Button("Cancelar");

        buttonSelect.setPrefWidth(140);
        buttonCancel.setPrefWidth(140);

        ControllerSelectProjects controllerSelectProjects = new ControllerSelectProjects(this);
        buttonSelect.setOnAction(event -> controllerSelectProjects.handleSelectButtonAction());
        buttonCancel.setOnAction(event -> controllerSelectProjects.cancel());

        HBox buttonRow = new HBox(40, buttonSelect, buttonCancel);
        buttonRow.setAlignment(Pos.CENTER);

        VBox mainPanel = new VBox(20, listPanel, buttonRow);
        mainPanel.setPadding(new Insets(24));

        Scene scene = new Scene(mainPanel, 680, 460);
        GUIStyle.apply(scene);
        stage.setTitle("SeleccionarProyectos");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void loadProjects(List<Project> projectsToLoad) {
        checkBoxes.clear();
        projects.clear();
        projectRows.clear();
        projects.addAll(projectsToLoad);
        for (Project project : projectsToLoad) {
            projectRows.add(buildProjectRow(project));
        }
        populateEnterpriseFilter();
        applyFilters();
    }

    public List<Project> getSelectedProjects() {
        List<Project> selected = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selected.add((Project) checkBox.getUserData());
            }
        }
        return selected;
    }

    public int getSelectedCount() {
        return (int) checkBoxes.stream().filter(CheckBox::isSelected).count();
    }

    public Button getButtonSelect() {
        return buttonSelect;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public Stage getStage() {
        return stage;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public boolean isModify() {
        return isModify;
    }

    public void setModify(boolean modify) {
        isModify = modify;
    }

    private HBox buildProjectRow(Project project) {
        Label label = new Label(project.getNameProject());
        label.setFont(Font.font("SansSerif", 13));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(8, 12, 8, 12));
        label.getStyleClass().add("list-item-label");
        HBox.setHgrow(label, Priority.ALWAYS);

        CheckBox checkBox = new CheckBox();
        checkBox.setUserData(project);
        checkBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            enforceSelectionLimit(checkBox, newValue);
        });
        checkBoxes.add(checkBox);
        HBox row = new HBox(6, label, checkBox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void enforceSelectionLimit(CheckBox checkBox, boolean isSelected) {
        long selected = checkBoxes.stream().filter(CheckBox::isSelected).count();
        if (isSelected && selected > 3) {
            checkBox.setSelected(false);
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Límite alcanzado");
            alert.setHeaderText(null);
            alert.setContentText("Solo puedes seleccionar 3 proyectos.");
            alert.showAndWait();
        }
    }

    private void populateEnterpriseFilter() {
        List<String> options = new ArrayList<>();
        options.add(ALL_ENTERPRISES);
        for (Project project : projects) {
            String enterpriseName = getEnterpriseName(project);
            if (!enterpriseName.isEmpty() && !options.contains(enterpriseName)) {
                options.add(enterpriseName);
            }
        }
        enterpriseFilter.setItems(FXCollections.observableArrayList(options));
        enterpriseFilter.setValue(ALL_ENTERPRISES);
    }

    private void applyFilters() {
        String search = GUIUtils.sanitizeSearch(searchField.getText());
        String enterprise = enterpriseFilter.getValue();
        vBoxProjectList.getChildren().clear();
        for (int i = 0; i < projects.size(); i++) {
            if (matchesFilters(projects.get(i), search, enterprise)) {
                vBoxProjectList.getChildren().add(projectRows.get(i));
            }
        }
    }

    private boolean matchesFilters(Project project, String search, String enterprise) {
        boolean matchesName = GUIUtils.matchesSearch(project.getNameProject(), search);
        boolean matchesEnterprise = enterprise == null || enterprise.equals(ALL_ENTERPRISES) || enterprise.equals(getEnterpriseName(project));
        return matchesName && matchesEnterprise;
    }

    private String getEnterpriseName(Project project) {
        String enterpriseName = "";
        if (project.getEnterprise() != null && project.getEnterprise().getName() != null) {
            enterpriseName = project.getEnterprise().getName();
        }
        return enterpriseName;
    }
}
