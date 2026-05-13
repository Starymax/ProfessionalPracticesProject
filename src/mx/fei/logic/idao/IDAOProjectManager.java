package mx.fei.logic.idao;

import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOProjectManager {
    boolean registerProjectManager(ProjectManager projectManager) throws DataOperationException;

    ProjectManager getProjectManagerById(int idProjectManager) throws DataOperationException;

    List<ProjectManager> getProjectManagersByEnterprise(Enterprise enterprise) throws DataOperationException;
}
