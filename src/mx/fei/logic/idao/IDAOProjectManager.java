package mx.fei.logic.idao;

import mx.fei.logic.dto.ProjectManager;

import java.util.List;

public interface IDAOProjectManager {
    boolean registerProjectManager(ProjectManager projectManager);

    ProjectManager getProjectManagerById(int idProjectManager);

    List<ProjectManager> getProjectManagers();
}
