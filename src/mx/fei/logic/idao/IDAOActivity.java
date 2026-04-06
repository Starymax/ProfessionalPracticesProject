package mx.fei.logic.idao;

import mx.fei.logic.dto.Activity;

import java.util.List;

public interface IDAOActivity {
    boolean registerActivity(Activity activity, int idProject);

    List<Activity> getActivitiesByProjectId(int idActivity);
}