package mx.fei.logic.idao;

import mx.fei.logic.dto.Project;

public interface IDAOProject {
    public Project getProjectById(int idProject);

    public int registerProject(Project project);
}
