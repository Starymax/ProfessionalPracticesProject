package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIRegisterActivity;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.WeeklyLog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;

public class ControllerRegisterActivity {

    private final GUIRegisterActivity guiRegisterActivity;

    public ControllerRegisterActivity(GUIRegisterActivity guiRegisterActivity) {
        this.guiRegisterActivity = guiRegisterActivity;
    }

    public void handleSaveCancelButtons(ActionEvent event) {
        if (event.getSource() == guiRegisterActivity.getButtonSave()) {
            save();
        } else if (event.getSource() == guiRegisterActivity.getButtonCancel()) {
            cancel();
        }
    }

    private void save() {
        if (guiRegisterActivity.validateFields()) {
            String name = guiRegisterActivity.getTextFieldActivityName().getText();
            String description = guiRegisterActivity.getTextAreaDescription().getText();
            Activity activity = new Activity(0, name, description, guiRegisterActivity.getProject());
            ArrayList<WeeklyLog> weeklyLogs = guiRegisterActivity.buildWeeklyLogs(activity);
            guiRegisterActivity.updateActivitiesList(activity, weeklyLogs);
            guiRegisterActivity.getStage().close();
        }
    }

    private void cancel() {
        guiRegisterActivity.getStage().close();
    }
}