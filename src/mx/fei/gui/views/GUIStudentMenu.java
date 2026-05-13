package mx.fei.gui.views;

import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.controllers.ControllerStudentMenu;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.Project;

public class GUIStudentMenu extends Application {

        private Label labelStudentName;
        private Label labelProjectName;
        private Button buttonSelectProjects;
        private Button buttonReports;
        private Button buttonDocuments;
        private Button buttonLogout;
        private Stage stage;
        private Student student;

        @Override
        public void start(Stage stage) {
            this.stage = stage;

            HBox welcomeRow = buildInfoRow("Bienvenido Alumno:", "Nombre del Alumno");
            HBox projectRow = buildInfoRow("Proyecto seleccionado:", "Nombre del proyecto");
            labelStudentName = (Label) welcomeRow.getChildren().get(1);
            labelProjectName = (Label) projectRow.getChildren().get(1);

            VBox infoPanel = new VBox(12, welcomeRow, projectRow);

            buttonSelectProjects = buildMenuButton("Seleccionar Proyectos");
            buttonReports = buildMenuButton("Gestión de Reportes");
            buttonDocuments = buildMenuButton("Subir Documentos");

            VBox centerButtons = new VBox(20, buttonSelectProjects, buttonReports, buttonDocuments);
            centerButtons.setAlignment(Pos.CENTER);

            buttonLogout = buildMenuButton("Cerrar Sesión");
            buttonLogout.setPrefWidth(160);

            HBox logoutRow = new HBox(buttonLogout);
            logoutRow.setAlignment(Pos.BOTTOM_RIGHT);

            ControllerStudentMenu controllerStudentMenu = new ControllerStudentMenu(this);
            buttonSelectProjects.setOnAction(event -> controllerStudentMenu.handleButtonAction(event));
            buttonReports.setOnAction(event -> controllerStudentMenu.handleButtonAction(event));
            buttonDocuments.setOnAction(event -> controllerStudentMenu.handleButtonAction(event));
            buttonLogout.setOnAction(event -> controllerStudentMenu.handleButtonAction(event));

            BorderPane mainPanel = new BorderPane();
            mainPanel.setPadding(new Insets(32, 40, 32, 40));
            mainPanel.setTop(infoPanel);
            mainPanel.setCenter(centerButtons);
            mainPanel.setBottom(logoutRow);
            BorderPane.setMargin(centerButtons, new Insets(20, 0, 20, 0));

            Scene scene = new Scene(mainPanel, 680, 520);
            stage.setTitle("GUIStudentMenu");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }

        private HBox buildInfoRow(String boldText, String normalText) {
            Label bold = new Label(boldText);
            bold.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
            Label normal = new Label(normalText);
            normal.setFont(Font.font("SansSerif", 15));
            HBox row = new HBox(8, bold, normal);
            row.setAlignment(Pos.CENTER_LEFT);
            return row;
        }

        private Button buildMenuButton(String text) {
            Button button = new Button(text);
            button.setPrefWidth(380);
            button.setPrefHeight(52);
            button.setFont(Font.font("SansSerif", 15));
            button.setStyle("-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 10;");
            return button;
        }

        public void showError(String message) {
            GUIUtils.showError(message);
        }

        public void setStudentInfo(Student student) {
            this.student = student;
            if (labelStudentName != null) {
                labelStudentName.setText(student.getName());
            }
            Project project = student.getAssignedProject();
            if (project != null) {
                labelProjectName.setText(project.getNameProject());
            } else {
                labelProjectName.setText("Proyecto sin asignar");
            }
        }

        public Button getButtonSelectProjects() {
            return buttonSelectProjects;
        }

        public Button getButtonReports() {
            return buttonReports;
        }

        public Button getButtonDocuments() {
            return buttonDocuments;
        }

        public Button getButtonLogout() {
            return buttonLogout;
        }

        public Student getStudent() {
            return student;
        }

        public Stage getStage() {
            return stage;
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
