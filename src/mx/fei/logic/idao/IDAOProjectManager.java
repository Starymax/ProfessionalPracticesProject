package mx.fei.logic.idao;

import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.util.List;

public interface IDAOProjectManager {
    boolean registerProjectManager(ProjectManager projectManager) throws DataBaseConnectionException;

    ProjectManager getProjectManagerById(int idProjectManager) throws DataBaseConnectionException;

    List<ProjectManager> getProjectManagers() throws DataBaseConnectionException;
}
