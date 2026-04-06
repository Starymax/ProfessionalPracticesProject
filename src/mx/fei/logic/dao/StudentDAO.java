package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.idao.IDAOStudent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDAO implements IDAOStudent {
    private  Logger logger = Logger.getLogger(StudentDAO.class.getName());

    @Override
    public Student getStudentByEnrollment(String enrollment) {
        Student student = null;
        String querygetStudentByEnrollment = "SELECT * FROM vw_alumnos where matricula=?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querygetStudentByEnrollment);) {
            preparedStatement.setString(1,enrollment);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int idUser = resultSet.getInt("id_usuario");
                String name = resultSet.getString("nombre");
                String lastName = resultSet.getString("apellidos");
                String period = resultSet.getString("periodo");
                String mail = resultSet.getString("correo");
                String password = resultSet.getString("contrasena");
                boolean activeStatus = resultSet.getBoolean("activo");
                String gender = resultSet.getString("genero");
                Boolean indigenousLanguage = resultSet.getBoolean("lengua_indigena");
                Float grade = resultSet.getFloat("calificacion");
                int studentProjectId = resultSet.getInt("proyecto");
                String nrc = resultSet.getString("nrc");
                ProjectDAO projectDAO = new ProjectDAO();
                Project project = projectDAO.getProjectById(studentProjectId);
                EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
                EducationalExperience educationalExperience=educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
                student = new Student(idUser,name,lastName,mail,password,gender,activeStatus,enrollment,period,indigenousLanguage,grade,project,educationalExperience);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return student;
    }

    @Override
    public boolean registerStudent(Student student) {
        boolean registered = false;
        if (student == null) {
            return false;
        }
        if (this.getStudentByEnrollment(student.getEnrollment())!=null) {
            logger.log(Level.WARNING,"El estudiante con la matricula ingresada ya existe");
            return false;
        }
        try {
            UserDAO userDAO = new UserDAO();
            int idUser = userDAO.registerUser(student);
            if (idUser == -1) {
                logger.log(Level.SEVERE, "No se logro registrar el usuario en la base");
                return false;
            }
            String queryRegisterStudent = "INSERT INTO alumno (id_usuario,matricula,periodo,lengua_indigena) VALUES (?,?,?,?);";
            try (Connection connection=DatabaseConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterStudent)) {
                preparedStatement.setInt(1,idUser);
                preparedStatement.setString(2,student.getEnrollment());
                preparedStatement.setString(3,student.getPeriod());
                preparedStatement.setBoolean(4,student.isIndigenousLanguage());
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    registered = true;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return registered;
    }

    @Override
    public boolean modifyStudent(Student student) {
        boolean updated = false;
        String queryModifyStudent = "UPDATE alumno SET periodo=?, lengua_indigena=?, calificacion=?, proyecto_asignado=?, nrc=? where id_usuario=?;";
        if (student == null) {
            return false;
        }
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryModifyStudent)) {
            preparedStatement.setString(1, student.getPeriod());
            preparedStatement.setBoolean(2, student.isIndigenousLanguage());
            preparedStatement.setFloat(3, student.getGrade());
            preparedStatement.setInt(4, student.getAssignedProject().getProjectId());
            preparedStatement.setString(5, student.getEducationalExperience().getNrc());
            preparedStatement.setInt(6, student.getUserId());
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return updated;
    }

    @Override
    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        String queryConsultStudent = "SELECT matricula FROM alumno";
        try (Connection connection =DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryConsultStudent)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                students.add(getStudentByEnrollment(resultSet.getString("matricula")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return students;
    }

    @Override
    public List<Student> getStudentsWithoutProject() {
        List<Student> students = new ArrayList<>();
        String queryConsultStudents = "SELECT matricula FROM alumno WHERE proyecto_asignado = NULL";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryConsultStudents)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                students.add(getStudentByEnrollment(resultSet.getString("matricula")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return students;
    }

    @Override
    public List<Student> getActiveStudents() {
        List<Student> students = new ArrayList<>();
        String queryConsultActiveStudent = "SELECT matricula FROM alumno join usuario USING(id_usuario) WHERE estado_activo = true";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryConsultActiveStudent)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                students.add(getStudentByEnrollment(resultSet.getString("matricula")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return students;
    }

    @Override
    public void saveSelectedProjects(List<Project> selectedProjects) {

    }

    @Override
    public List<Project> getSelectedProjects() {
        return List.of();
    }

    @Override
    public boolean assignProject(Project project) {
        return false;
    }
}
