package mx.fei.logic.idao;

import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOStudent {
    Student getStudentByEnrollment(String enrollment) throws DataOperationException;

    boolean registerStudent(Student student) throws DataOperationException;

    boolean modifyStudent(Student student) throws DataOperationException;

    List<Student> getStudents() throws DataOperationException;

    List<Student> getStudentsWithoutProject() throws DataOperationException;

    List<Student> getActiveStudents() throws DataOperationException;

    void saveSelectedProjects(List<Project> selectedProjects, Student student) throws DataOperationException;

    List<Project> getSelectedProjects(Student student) throws DataOperationException;

    boolean assignProject(Student student,Project project) throws DataOperationException;

    boolean assignEducationalExperience(Student student, EducationalExperience experience) throws DataOperationException;
}
