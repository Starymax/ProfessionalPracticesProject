package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.idao.IDAOStudent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDAO implements IDAOStudent {
    private  Logger logger = Logger.getLogger(StudentDAO.class.getName());
    @Override
    public Student getStudentByEnrollment(String enrollment) {
        Student student = null;
        try {
            DatabaseConnectionManager connection = DatabaseConnectionManager.buildConnection();
            String querygetStudentByEnrollment = "SELECT * FROM vw_alumnos where matricula=?;";
            PreparedStatement preparedStatement = connection.preparedStatement(querygetStudentByEnrollment);
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
                int studentProjectId = resultSet.getInt("proyecto_asignado");
                ProjectDAO projectDAO = new ProjectDAO();
                Project project = projectDAO.getProjectById(studentProjectId);
                student = new Student(idUser,name,lastName,mail,password,gender,activeStatus,enrollment,period,indigenousLanguage,grade,project);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return student;
    }

    @Override
    public boolean registerStudent(Student student) {
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
            DatabaseConnectionManager connectionManager = DatabaseConnectionManager.buildConnection();
            String queryRegisterStudent = "INSERT INTO alumno (id_usuario,matricula,periodo,lengua_indigena) VALUES (?,?,?,?);";
            PreparedStatement preparedStatement = connectionManager.preparedStatement(queryRegisterStudent);
            preparedStatement.setInt(1,idUser);
            preparedStatement.setString(2,student.getEnrollment());
            preparedStatement.setString(3,student.getPeriod());
            preparedStatement.setBoolean(4,student.isIndigenousLanguage());
            preparedStatement.executeUpdate();
            connectionManager.close();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return false;
    }

    @Override
    public boolean modifyStudent(Student student) {
        boolean updated = false;
        if (student == null) {
            return false;
        }
        try {
            DatabaseConnectionManager connection = DatabaseConnectionManager.buildConnection();
            String queryModifyStudent = "UPDATE alumno SET periodo=?, lengua_indigena=?, calificacion=?, proyecto_asignado=?, nrc=? where id_usuario=?;";
            PreparedStatement preparedStatement= connection.preparedStatement(queryModifyStudent);
            preparedStatement.setString(1, student.getPeriod());
            preparedStatement.setBoolean(2, student.isIndigenousLanguage());
            preparedStatement.setFloat(3, student.getGrade());
            preparedStatement.setInt(4, student.getAssignedProject().getProjectId());
            preparedStatement.setString(5, student.getEducationalExperience().getNrc());
            preparedStatement.setInt(6, student.getUserId());
            updated = preparedStatement.executeUpdate()>0;
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return updated;
    }
}
