package mx.fei.logic.idao;

import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IDAOStudent {
    Student getStudentById(Integer studentId) throws DataOperationException;

    Student buildStudentFromResultSet(ResultSet resultSet) throws SQLException;

    List<Student> resolveProjectsOfStudents(List<Student> students);

    Student getStudentByEnrollment(String enrollment) throws DataOperationException;

    boolean registerStudent(Student student) throws DataOperationException;

    boolean modifyStudent(Student student) throws DataOperationException;

    List<Student> getStudents() throws DataOperationException;

    List<Student> getStudentsWithoutProject() throws DataOperationException;

    List<Student> getActiveStudents() throws DataOperationException;

    void saveSelectedProjects(List<Project> selectedProjects, Student student) throws DataOperationException;

    List<Project> getSelectedProjects(Student student) throws DataOperationException;

    boolean assignProject(Student student,Project project) throws DataOperationException;

    boolean assignEducationalExperience(Practice practice) throws DataOperationException;
}
