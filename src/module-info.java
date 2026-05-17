module practicas.profesionales {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    opens mx.fei.gui.views to javafx.fxml;
    exports mx.fei.gui.views;
    exports mx.fei.logic.dto;
}