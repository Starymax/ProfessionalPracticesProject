module practicas.profesionales {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires java.xml.crypto;
    requires java.desktop;
    requires java.logging;
    requires net.sf.jasperreports.core;
    requires org.apache.pdfbox;
    requires mysql.connector.j;

    opens mx.fei.gui.views to javafx.fxml;
    opens mx.fei.gui.controllers to javafx.fxml;
    opens mx.fei.logic.dao to org.junit.platform.commons, org.mockito;
    opens mx.fei.dataaccess to org.mockito;
    opens mx.fei.logic.exceptions to org.mockito;

    exports mx.fei.dataaccess;
    exports mx.fei.logic.dto;
    exports mx.fei.logic.exceptions;
    exports mx.fei.logic.dao;
    exports mx.fei.gui.views;
    exports mx.fei.gui.controllers;
}