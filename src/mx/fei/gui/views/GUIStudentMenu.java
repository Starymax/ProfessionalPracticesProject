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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private Button buttonRegisterAdvance;
    private Button buttonDocuments;
    private Button buttonNotifications;
    private Button buttonLogout;
    private Label labelUnreadCount;
    private Stage stage;
    private Student student;
    private StackPane badgePane;
    private ControllerStudentMenu controllerStudentMenu;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        HBox welcomeRow = buildInfoRow("Bienvenido Alumno:", "Nombre del Alumno");
        HBox projectRow = buildInfoRow("Proyecto asignado:", "Nombre del proyecto");
        labelStudentName = (Label) welcomeRow.getChildren().get(1);
        labelProjectName = (Label) projectRow.getChildren().get(1);

        VBox infoPanel = new VBox(12, welcomeRow, projectRow);

        buttonSelectProjects = buildMenuButton("Seleccionar Proyectos");
        buttonReports = buildMenuButton("Generar Reportes");
        buttonRegisterAdvance = buildMenuButton("Registro de Avances");
        buttonDocuments = buildMenuButton("Subir Documentos");

        VBox centerButtons = new VBox(20, buttonSelectProjects, buttonReports, buttonRegisterAdvance, buttonDocuments);
        centerButtons.setAlignment(Pos.CENTER);

        buttonNotifications = new Button("🔔 Notificaciones");
        buttonNotifications.setPrefHeight(40);
        buttonNotifications.setFont(Font.font("SansSerif", 13));
        buttonNotifications.setStyle("-fx-background-color: transparent; -fx-text-fill: #1e1e23; -fx-cursor: hand; -fx-border-color: #1e1e23; -fx-border-radius: 8; -fx-background-radius: 8;");

        labelUnreadCount = new Label("0");
        labelUnreadCount.setFont(Font.font("SansSerif", FontWeight.BOLD, 10));
        labelUnreadCount.setTextFill(Color.WHITE);
        labelUnreadCount.setVisible(false);

        Circle badge = new Circle(9, Color.web("#e74c3c"));
        badgePane = new StackPane(badge, labelUnreadCount);
        badgePane.setTranslateX(14);
        badgePane.setTranslateY(-14);
        badgePane.setMouseTransparent(true);

        StackPane notificationStack = new StackPane(buttonNotifications, badgePane);
        StackPane.setAlignment(badgePane, Pos.TOP_RIGHT);

        buttonLogout = buildMenuButton("Cerrar Sesión");
        buttonLogout.setPrefWidth(160);

        HBox bottomRow = new HBox(12, notificationStack, buttonLogout);
        bottomRow.setAlignment(Pos.BOTTOM_RIGHT);

        ControllerStudentMenu controllerStudentMenu = new ControllerStudentMenu(this);
        buttonSelectProjects.setOnAction(controllerStudentMenu::handleButtonsMenu);
        buttonReports.setOnAction(controllerStudentMenu::handleButtonsMenu);
        buttonRegisterAdvance.setOnAction(controllerStudentMenu::handleButtonsMenu);
        buttonDocuments.setOnAction(controllerStudentMenu::handleButtonsMenu);
        buttonNotifications.setOnAction(controllerStudentMenu::handleButtonsMenu);
        buttonLogout.setOnAction(controllerStudentMenu::handleButtonsMenu);
        this.controllerStudentMenu = controllerStudentMenu;

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(32, 40, 32, 40));
        mainPanel.setTop(infoPanel);
        mainPanel.setCenter(centerButtons);
        mainPanel.setBottom(bottomRow);
        BorderPane.setMargin(centerButtons, new Insets(20, 0, 20, 0));

        Scene scene = new Scene(mainPanel, 680, 520);
        stage.setTitle("Estudiante");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void updateUnreadCount(int count) {
        if (count > 0) {
            labelUnreadCount.setText(count > 99 ? "99+" : String.valueOf(count));
            if (badgePane != null) {
                badgePane.setVisible(true);
            }
        } else {
            if (badgePane != null) {
                badgePane.setVisible(false);
            }
        }
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
        if (controllerStudentMenu != null) {
            controllerStudentMenu.loadUnreadCount();
        }
    }

    public Button getButtonSelectProjects() {
        return buttonSelectProjects;
    }

    public Button getButtonReports() {
        return buttonReports;
    }

    public Button getButtonRegisterAdvance() {
        return buttonRegisterAdvance;
    }

    public Button getButtonDocuments() {
        return buttonDocuments;
    }

    public Button getButtonNotifications() {
        return buttonNotifications;
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