package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;

import mx.fei.gui.controllers.ControllerNotifications;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.dto.Student;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.List;

public class GUINotifications extends Application {

    private ListView<Notification> listViewNotifications;
    private Label labelTitle;
    private Label labelDate;
    private TextArea textAreaMessage;
    private Button buttonClose;
    private Stage stage;
    private Student student;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label labelHeader = new Label("Notificaciones");
        labelHeader.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        listViewNotifications = new ListView<>();
        listViewNotifications.setPrefWidth(280);
        listViewNotifications.setCellFactory(listView -> createNotificationCell());
        listViewNotifications.getSelectionModel().selectedItemProperty().addListener((observable, oldNotification, newNotification) -> {
                if (newNotification != null) {
                    loadNotificationDetail(newNotification);
                    if (!newNotification.isRead()) {
                        markAsReadAutomatically(newNotification);
                    }
                }
            }
        );

        labelTitle = new Label("");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelTitle.setWrapText(true);

        labelDate = new Label("");
        labelDate.setFont(Font.font("SansSerif", 11));
        labelDate.setTextFill(Color.GRAY);

        textAreaMessage = new TextArea();
        textAreaMessage.setEditable(false);
        textAreaMessage.setWrapText(true);
        textAreaMessage.setFont(Font.font("SansSerif", 13));
        VBox.setVgrow(textAreaMessage, Priority.ALWAYS);

        VBox detailPanel = new VBox(10, labelTitle, labelDate, textAreaMessage);
        detailPanel.setPadding(new Insets(0, 0, 0, 16));
        VBox.setVgrow(detailPanel, Priority.ALWAYS);

        buttonClose = new Button("Cerrar");
        buttonClose.setStyle("-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 8;");
        buttonClose.setPrefWidth(100);

        HBox bottomRow = new HBox(buttonClose);
        bottomRow.setAlignment(Pos.BOTTOM_RIGHT);
        bottomRow.setPadding(new Insets(12, 0, 0, 0));

        ControllerNotifications controller = new ControllerNotifications(this);
        buttonClose.setOnAction(controller::handleClose);
        HBox contentRow = new HBox(listViewNotifications, detailPanel);
        HBox.setHgrow(detailPanel, Priority.ALWAYS);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(24, 32, 24, 32));
        mainPanel.setTop(labelHeader);
        mainPanel.setCenter(contentRow);
        mainPanel.setBottom(bottomRow);
        BorderPane.setMargin(labelHeader, new Insets(0, 0, 16, 0));

        Scene scene = new Scene(mainPanel, 700, 460);
        GUIStyle.apply(scene);
        stage.setTitle("Notificaciones");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void loadNotificationDetail(Notification notification) {
        labelTitle.setText(notification.getTitle());
        labelDate.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(notification.getEmissionDate()));
        textAreaMessage.setText(notification.getMessage());
    }

    public void loadNotifications(List<Notification> notifications) {
        listViewNotifications.getItems().clear();
        listViewNotifications.getItems().addAll(notifications);
        if (!notifications.isEmpty()) {
            listViewNotifications.getSelectionModel().selectFirst();
        }
    }

    public void refreshList() {
        listViewNotifications.refresh();
    }

    private ListCell<Notification> createNotificationCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label labelCellTitle = new Label(notification.getTitle());
                    labelCellTitle.setFont(Font.font("SansSerif", notification.isRead() ? FontWeight.NORMAL : FontWeight.BOLD, 13));
                    Label labelCellDate = new Label(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(notification.getEmissionDate()));
                    labelCellDate.setFont(Font.font("SansSerif", 11));
                    labelCellDate.setTextFill(Color.GRAY);
                    Circle unreadDot = new Circle(4, Color.web("#e74c3c"));
                    unreadDot.setVisible(!notification.isRead());
                    HBox dotRow = new HBox(6, unreadDot, labelCellTitle);
                    dotRow.setAlignment(Pos.CENTER_LEFT);
                    VBox cellBox = new VBox(2, dotRow, labelCellDate);
                    setGraphic(cellBox);
                }
            }
        };
    }

    public void markAsReadAutomatically(Notification notification) {
        ControllerNotifications controller = new ControllerNotifications(this);
        controller.markNotificationAsRead(notification);
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public Notification getSelectedNotification() {
        return listViewNotifications.getSelectionModel().getSelectedItem();
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    public Button getButtonClose() {
        return buttonClose;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}