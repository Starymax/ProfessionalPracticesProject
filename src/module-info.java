module practicas.profesionales {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop; //TODO: Borrar esta linea cuando se quite java swing
    opens mx.fei.gui.views to javafx.fxml;
    exports mx.fei.gui.views;
}