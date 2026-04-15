package mx.fei.logic.idao;

import mx.fei.logic.dto.Project;
import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.util.List;

public interface IDAOProject {
    Project getProjectById(int idProject) throws DataBaseConnectionException;

    int registerProject(Project project) throws DataBaseConnectionException;

    List<Project> getActiveProjects() throws DataBaseConnectionException;

    List<Project> getAvailableProjects() throws DataBaseConnectionException;

    boolean modifyProject(Project project) throws DataBaseConnectionException;
}
