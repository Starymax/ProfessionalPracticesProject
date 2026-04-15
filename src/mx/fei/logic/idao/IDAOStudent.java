package mx.fei.logic.idao;

import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.util.List;

public interface IDAOStudent {
    Student getStudentByEnrollment(String enrollment) throws DataBaseConnectionException;

    boolean registerStudent(Student student) throws DataBaseConnectionException;

    boolean modifyStudent(Student student) throws DataBaseConnectionException;

    List<Student> getStudents() throws DataBaseConnectionException;

    List<Student> getStudentsWithoutProject() throws DataBaseConnectionException;

    List<Student> getActiveStudents() throws DataBaseConnectionException;

    void saveSelectedProjects(List<Project> selectedProjects, Student student) throws DataBaseConnectionException;

    List<Project> getSelectedProjects(Student student) throws DataBaseConnectionException;

    boolean assignProject(Student student,Project project) throws DataBaseConnectionException;

    boolean assignEducationalExperience(Student student, EducationalExperience experience) throws DataBaseConnectionException;
}
