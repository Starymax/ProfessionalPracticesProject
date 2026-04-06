package mx.fei.logic.idao;

import mx.fei.logic.dto.EducationalExperience;
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

    void saveSelectedProjects(List<Project> selectedProjects, Student student);

    List<Project> getSelectedProjects(Student student);

    boolean assignProject(Student student,Project project);

    boolean assignEducationalExperience(Student student, EducationalExperience experience);
}
