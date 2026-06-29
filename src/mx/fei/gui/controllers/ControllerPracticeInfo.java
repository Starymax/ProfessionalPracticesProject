package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIPracticeInfo;

import javafx.event.ActionEvent;

public class ControllerPracticeInfo {

    private final GUIPracticeInfo guiPracticeInfo;

    public ControllerPracticeInfo(GUIPracticeInfo guiPracticeInfo) {
        this.guiPracticeInfo = guiPracticeInfo;
    }

    public void handleBackButton(ActionEvent event) {
        guiPracticeInfo.closeWindow();
    }
}