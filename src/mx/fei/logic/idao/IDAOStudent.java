package mx.fei.logic.idao;

import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import java.util.List;

public interface IDAOStudent {
    Student getStudentByEnrollment(String enrollment);

    boolean registerStudent(Student student);

    boolean modifyStudent(Student student);

    List<Student> getStudents();

    List<Student> getStudentsWithoutProject();

    List<Student> getActiveStudents();

    void saveSelectedProjects(List<Project> selectedProjects);

    List<Project> getSelectedProjects();

    boolean assignProject(Project project);
}
