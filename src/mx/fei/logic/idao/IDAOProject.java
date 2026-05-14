package mx.fei.logic.idao;

import mx.fei.logic.dto.Project;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOProject {
    Project getProjectById(Integer idProject) throws DataOperationException;

    int registerProject(Project project) throws DataOperationException;

    List<Project> getActiveProjects() throws DataOperationException;

    List<Project> getAllProjects() throws DataOperationException;

    List<Project> getAvailableProjects() throws DataOperationException;

    boolean modifyProject(Project project) throws DataOperationException;
}
