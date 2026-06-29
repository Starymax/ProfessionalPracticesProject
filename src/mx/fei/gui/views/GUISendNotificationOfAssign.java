package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerSendNotificationOfAssign;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;


public class GUISendNotificationOfAssign {

    private final Stage stage;
    private final Student student;
    private final Project project;
    private final TextField textFieldTitle;
    private final TextArea textAreaMessage;
    private final Button buttonSend;
    private final Button buttonCancel;
    private boolean wasSent = false;

    public GUISendNotificationOfAssign(Stage previousStage, Student student, Project project) {
        this.student = student;
        this.project = project;

        stage = new Stage();
        stage.initOwner(previousStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        Label labelHeader = new Label("Enviar notificación");
        labelHeader.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));

        textFieldTitle = new TextField();
        textFieldTitle.setPromptText("Título");
        textAreaMessage = new TextArea();
        textAreaMessage.setPromptText("Mensaje");
        textAreaMessage.setWrapText(true);

        if (project != null) {
            textFieldTitle.setText("Asignación de proyecto " + project.getNameProject());
            textAreaMessage.setText("Felicitaciones! Fue asignado al proyecto " + project.getNameProject() + "\n Ya puede subir sus documentos iniciales (Carta de aceptacionl, horario, identificación oficial, acta de nacimiento, constancia de estudios y CURP).");
        }
        buttonSend = new Button("Enviar");
        buttonCancel = new Button("Cancelar");

        buttonSend.setPrefWidth(100);
        buttonCancel.setPrefWidth(100);

        ControllerSendNotificationOfAssign controllerSendNotificationOfAssign = new ControllerSendNotificationOfAssign(this);
        buttonSend.setOnAction(controllerSendNotificationOfAssign::handleSendCancelButtons);
        buttonCancel.setOnAction(controllerSendNotificationOfAssign::handleSendCancelButtons);
        HBox buttonRow = new HBox(12, buttonCancel, buttonSend);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        VBox centerPanel = new VBox(8, textFieldTitle, textAreaMessage);
        centerPanel.setPadding(new Insets(8));
        BorderPane mainPanel = new BorderPane();
        mainPanel.setTop(labelHeader);
        BorderPane.setMargin(labelHeader, new Insets(0, 0, 8, 0));
        mainPanel.setCenter(centerPanel);
        mainPanel.setBottom(buttonRow);
        mainPanel.setPadding(new Insets(12));

        Scene scene = new Scene(mainPanel, 520, 300);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.setTitle("Enviar notificación");
        stage.setResizable(false);
    }

    public void setWasSent(boolean wasSent) {
        this.wasSent = wasSent;
    }

    public String getTitleText() {
        return textFieldTitle.getText();
    }

    public String getMessageText() {
        return textAreaMessage.getText();
    }

    public Stage getStage() {
        return stage;
    }

    public Student getStudent() {
        return student;
    }

    public Project getProject() {
        return project;
    }

    public boolean wasSent() {
        return wasSent;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showErrors(List<String> errors) {
        GUIUtils.showErrors(errors);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void close() {
        stage.close();
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}