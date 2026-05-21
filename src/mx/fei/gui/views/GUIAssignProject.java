package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerAssignProject;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUIAssignProject extends Application {

    private Button buttonAssign;
    private Button buttonCancel;
    private Label labelStudentName;
    private Label labelEnrollment;
    private ListView<Project> projectListView;
    private Stage stage;
    private Student student;
    private ArrayList<Integer> selectedProjectIds = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        labelStudentName = new Label("Alumno Ejemplo");
        labelEnrollment = new Label("Matricula Ejemplo");

        HBox studentRow = buildInfoRow("Alumno:", labelStudentName);
        HBox enrollmentRow = buildInfoRow("Matrícula:", labelEnrollment);

        Label projectsTitle = new Label("Proyectos activos");
        projectsTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        projectListView = new ListView<>();
        projectListView.setCellFactory(listView -> {
            ListCell<Project> cell = new ListCell<>() {
                @Override
                protected void updateItem(Project project, boolean empty) {
                    super.updateItem(project, empty);
                    if (empty || project == null) {
                        setText(null);
                        setStyle(null);
                    } else {
                        boolean selectedByStudent = selectedProjectIds.contains(project.getProjectId());
                        String displayText = project.getNameProject();
                        if (selectedByStudent) {
                            displayText += " — Seleccionado";
                            if (isSelected()) {
                                setStyle("-fx-background-color: #a8d5a2; -fx-text-fill: #155724;");
                            } else {
                                setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
                            }
                        } else if (isSelected()) {
                            setStyle("-fx-background-color: -fx-selection-bar; -fx-text-fill: -fx-selection-bar-text;");
                        } else {
                            setStyle(null);
                        }
                        setText(displayText);
                    }
                }
            };
            cell.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (!cell.isEmpty()) {
                    cell.requestLayout();
                }
            });
            return cell;
        });
        projectListView.setPrefHeight(200);
        projectListView.setStyle("-fx-font-size: 14px;");

        buttonAssign = new Button("Asignar");
        buttonCancel = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 10;";
        buttonAssign.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);
        buttonAssign.setPrefWidth(160);
        buttonCancel.setPrefWidth(160);

        ControllerAssignProject controllerAssignProject = new ControllerAssignProject(this);
        buttonAssign.setOnAction(controllerAssignProject::handleAssignCancelButtons);
        buttonCancel.setOnAction(controllerAssignProject::handleAssignCancelButtons);

        VBox buttonPanel = new VBox(12, buttonAssign, buttonCancel);
        buttonPanel.setAlignment(Pos.BOTTOM_RIGHT);

        VBox infoPanel = new VBox(16, studentRow, enrollmentRow, projectsTitle, projectListView);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(32, 40, 32, 40));
        mainPanel.setCenter(infoPanel);
        mainPanel.setBottom(buttonPanel);
        BorderPane.setMargin(buttonPanel, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(mainPanel, 720, 560);
        stage.setTitle("Asignar Proyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private HBox buildInfoRow(String boldText, Label valueLabel) {
        Label bold = new Label(boldText);
        bold.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        bold.setMinWidth(100);
        valueLabel.setFont(Font.font("SansSerif", 15));
        HBox row = new HBox(10, bold, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    public void setStudent(Student student) {
        this.student = student;
        labelStudentName.setText(student.getName() + " " + student.getLastName());
        labelEnrollment.setText(student.getEnrollment());
    }

    public void loadProjects(List<Project> projects, List<Project> selectedProjects) {
        selectedProjectIds.clear();
        if (selectedProjects != null) {
            for (Project selectedProject : selectedProjects) {
                selectedProjectIds.add(selectedProject.getProjectId());
            }
        }
        projectListView.getItems().clear();
        List<Project> orderedProjects = new ArrayList<>();
        for (Project project : projects) {
            if (selectedProjectIds.contains(project.getProjectId())) {
                orderedProjects.add(project);
            }
        }
        for (Project project : projects) {
            if (!selectedProjectIds.contains(project.getProjectId())) {
                orderedProjects.add(project);
            }
        }
        projectListView.getItems().addAll(orderedProjects);
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public Project getSelectedProject() {
        return projectListView.getSelectionModel().getSelectedItem();
    }

    public Student getStudent() {
        return student;
    }

    public Button getButtonAssign() {
        return buttonAssign;
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