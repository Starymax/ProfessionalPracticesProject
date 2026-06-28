package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerLogin;
import mx.fei.gui.utils.GUIUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class GUILogin extends Application {
    private static final int INSTANCE_LOCK_PORT = 54321;
    private static ServerSocket instanceLock;
    private TextField textFieldMail;
    private PasswordField textFieldPassword;
    private Button buttonLogin;
    private Button buttonCancel;

    private static final Logger LOGGER = Logger.getLogger(GUILogin.class.getName());

    static {
        try {
            InputStream configLogger = GUILogin.class.getResourceAsStream("/logging.properties");
            if (configLogger != null) {
                LogManager.getLogManager().readConfiguration(configLogger);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error cargando configuración de logging", e);
        }
    }

    @Override
    public void start(Stage stage) {
        if (isAnotherInstanceRunning()) {
            GUIUtils.showError("La aplicación ya está abierta. Cierra la instancia actual antes de abrir una nueva.");
            Platform.exit();
            return;
        }
        stage.setTitle("Inicio de sesión");
        stage.setResizable(false);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20, 30, 20, 30));
        formGrid.getStyleClass().add("form-panel");

        Label labelTitle = new Label("Iniciar sesión");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        GridPane.setColumnSpan(labelTitle, 2);
        GridPane.setHalignment(labelTitle, HPos.CENTER);
        formGrid.add(labelTitle, 0, 0);

        formGrid.add(new Label("Correo:"), 0, 1);
        textFieldMail = new TextField();
        textFieldMail.setPrefWidth(220);
        formGrid.add(textFieldMail, 1, 1);

        formGrid.add(new Label("Contraseña:"), 0, 2);
        textFieldPassword = new PasswordField();
        formGrid.add(textFieldPassword, 1, 2);

        buttonLogin = new Button("Ingresar");
        buttonCancel = new Button("Cancelar");
        buttonLogin.setPrefWidth(110);
        buttonCancel.setPrefWidth(110);
        ControllerLogin controllerLogin = new ControllerLogin(this);
        buttonLogin.setOnAction(controllerLogin::handleLoginCancelButtons);
        buttonCancel.setOnAction(controllerLogin::handleLoginCancelButtons);

        HBox buttonsBox = new HBox(30, buttonLogin, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        GridPane.setColumnSpan(buttonsBox, 2);
        GridPane.setHalignment(buttonsBox, HPos.CENTER);
        formGrid.add(buttonsBox, 0, 3);

        StackPane mainPanel = new StackPane(formGrid);
        mainPanel.setPadding(new Insets(20));
        Scene scene = new Scene(mainPanel);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    private boolean isAnotherInstanceRunning() {
        return instanceLock == null;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void closeWindow() {
        ((Stage) buttonCancel.getScene().getWindow()).close();
    }

    @Override
    public void stop() {
        if (instanceLock != null && !instanceLock.isClosed()) {
            try {
                instanceLock.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al liberar el bloqueo de instancia única", e);
            }
        }
    }

    public static void main(String[] args) {
        try {
            instanceLock = new ServerSocket(INSTANCE_LOCK_PORT, 1, InetAddress.getByName("localhost"));
        } catch (BindException e) {
            LOGGER.log(Level.WARNING, "Se intentó abrir una segunda instancia de la aplicación", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar el bloqueo de instancia única", e);
        }
        launch(args);
    }

    public TextField getTextFieldMail() {
        return textFieldMail;
    }

    public PasswordField getTextFieldPassword() {
        return textFieldPassword;
    }

    public Button getButtonLogin() {
        return buttonLogin;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }
}