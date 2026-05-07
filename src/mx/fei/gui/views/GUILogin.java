package mx.fei.gui.views;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerLogin;

public class GUILogin extends Application {
    private     TextField textFieldMail;
    private PasswordField textFieldPassword;
    private Button buttonLogin;
    private Button buttonCancel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Inicio de sesión");
        stage.setResizable(false);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20, 30, 20, 30));
        formGrid.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formGrid.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

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
        buttonLogin.setStyle("-fx-background-color: #323232; -fx-text-fill: white;");
        buttonCancel.setStyle("-fx-background-color: #323232; -fx-text-fill: white;");
        ControllerLogin controllerLogin = new ControllerLogin(this);
        buttonLogin.setOnAction(event -> controllerLogin.handleButtonAction(event));
        buttonCancel.setOnAction(event -> controllerLogin.handleButtonAction(event));

        HBox buttonsBox = new HBox(30, buttonLogin, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        GridPane.setColumnSpan(buttonsBox, 2);
        GridPane.setHalignment(buttonsBox, HPos.CENTER);
        formGrid.add(buttonsBox, 0, 3);

        StackPane mainPanel = new StackPane(formGrid);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
        stage.show();
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Campo requerido");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void closeWindow() {
        ((Stage) buttonCancel.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
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