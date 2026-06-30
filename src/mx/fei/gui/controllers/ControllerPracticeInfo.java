package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIPracticeInfo;


public class ControllerPracticeInfo {

    private final GUIPracticeInfo guiPracticeInfo;

    public ControllerPracticeInfo(GUIPracticeInfo guiPracticeInfo) {
        this.guiPracticeInfo = guiPracticeInfo;
    }

    public void handleBackButton() {
        guiPracticeInfo.closeWindow();
    }
}