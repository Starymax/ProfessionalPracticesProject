package mx.fei.logic.idao;

import mx.fei.logic.dto.Project;

import java.util.List;

public interface IDAOProject {
    Project getProjectById(int idProject);

    int registerProject(Project project);

    List<Project> getActiveProjects();

    List<Project> getAvailableProjects();

    boolean modifyProject(Project project);
}
