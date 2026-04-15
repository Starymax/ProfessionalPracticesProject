package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataBaseConnectionException;
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
    public Student getStudentByEnrollment(String enrollment) throws DataBaseConnectionException {
        Student student = null;
        String querygetStudentByEnrollment = "SELECT * FROM vw_alumnos where matricula=?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querygetStudentByEnrollment);) {
            preparedStatement.setString(1,enrollment);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
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
                resultSet.close();
                ProjectDAO projectDAO = new ProjectDAO();
                Project project = projectDAO.getProjectById(studentProjectId);
                EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
                EducationalExperience educationalExperience=educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
                student = new Student(idUser,name,lastName,mail,password,gender,activeStatus,enrollment,period,indigenousLanguage,grade,project,educationalExperience);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al buscar el estudiante por matricula");
            throw new DataBaseConnectionException("Error al obtener los datos del estudiante");
        }
        return student;
    }

    @Override
    public boolean registerStudent(Student student) throws DataBaseConnectionException {
        boolean registered = false;
        if (student != null && getStudentByEnrollment(student.getEnrollment()) == null) {
            try {
                UserDAO userDAO = new UserDAO();
                int idUser = userDAO.registerUser(student);
                if (idUser != RegistrationStatus.FAILURE.getValue()) {
                    String queryRegisterStudent = "INSERT INTO alumno (id_usuario,matricula,periodo,lengua_indigena) VALUES (?,?,?,?);";
                    try (Connection connection = DatabaseConnectionManager.getConnection();
                         PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterStudent)) {
                        preparedStatement.setInt(1, idUser);
                        preparedStatement.setString(2, student.getEnrollment());
                        preparedStatement.setString(3, student.getPeriod());
                        preparedStatement.setBoolean(4, student.isIndigenousLanguage());
                        registered = preparedStatement.executeUpdate() > 0;
                    }
                } else {
                    logger.log(Level.SEVERE, "No se logro registrar el usuario en la base");
                    throw  new DataBaseConnectionException("No se logro registrar el usuario en la base");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw  new DataBaseConnectionException("Error al registrar el alumno");
            }
        } else if (student == null) {
            logger.log(Level.WARNING, "El estudiante es nulo");
            throw new IllegalArgumentException("El estudiante es nulo");
        } else {
            logger.log(Level.WARNING, "El estudiante con la matricula ingresada ya existe");
            throw new IllegalStateException("Ya existe un estudiante con la matricula ingresada");
        }
        return registered;
    }

    @Override
    public boolean modifyStudent(Student student) throws DataBaseConnectionException {
        boolean updated = false;
        String queryModifyStudent = "UPDATE alumno SET periodo=?, lengua_indigena=?, calificacion=? where id_usuario=?;";
        if (student != null) {
            try (Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(queryModifyStudent)) {
                preparedStatement.setString(1, student.getPeriod());
                preparedStatement.setBoolean(2, student.isIndigenousLanguage());
                preparedStatement.setFloat(3, student.getGrade());
                preparedStatement.setInt(4, student.getUserId());
                updated = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al modificar el alumno");
                throw new DataBaseConnectionException("Error al cambiar los datos en la base de datos");
            }
        }
        return updated;
    }

    @Override
    public List<Student> getStudents() throws DataBaseConnectionException {
        List<Student> students = new ArrayList<>();
        String queryConsultStudent = "SELECT matricula FROM alumno";
        try (Connection connection =DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryConsultStudent)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> enrollments = new ArrayList<>();
            while (resultSet.next()) {
                enrollments.add((resultSet.getString("matricula")));
            }
            resultSet.close();
            for(String enrollment : enrollments) {
                students.add(getStudentByEnrollment(enrollment));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al obtener todos los estudiantes");
            throw new DataBaseConnectionException("Error al obtener los estudiantes en la base de datos");
        }
        return students;
    }

    @Override
    public List<Student> getStudentsWithoutProject() throws DataBaseConnectionException {
        List<Student> students = new ArrayList<>();
        String queryConsultStudents = "SELECT matricula FROM alumno WHERE proyecto_asignado IS NULL";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryConsultStudents)) {
            List<String> enrollmetns = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                enrollmetns.add(resultSet.getString("matricula"));
            }
            resultSet.close();
            for (String enrollment : enrollmetns) {
                students.add(getStudentByEnrollment(enrollment));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al obtener todos los estudiantes sin proyecto asignado");
            throw new DataBaseConnectionException("Error al obtener los estudiantes sin proyecto");
        }
        return students;
    }

    @Override
    public List<Student> getActiveStudents() throws DataBaseConnectionException {
        List<Student> students = new ArrayList<>();
        String queryConsultActiveStudent = "SELECT matricula FROM alumno join usuario USING(id_usuario) WHERE estado_activo = true";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryConsultActiveStudent)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> enrollmetns = new ArrayList<>();
            while (resultSet.next()) {
                enrollmetns.add(resultSet.getString("matricula"));
            }
            resultSet.close();
            for(String enrollment : enrollmetns) {
                students.add(getStudentByEnrollment(enrollment));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al obtener todos los estudiantes activos");
            throw new DataBaseConnectionException("Error al obtener los estudiantes activos");
        }
        return students;
    }

    @Override
    public void saveSelectedProjects(List<Project> selectedProjects, Student student) throws DataBaseConnectionException {
        String querySaveSelectedProjects = "INSERT INTO seleccion (matricula, proyecto_seleccionado) values (?,?);";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(querySaveSelectedProjects)) {
            for (Project project : selectedProjects) {
                preparedStatement.setString(1,student.getEnrollment());
                preparedStatement.setInt(2, project.getProjectId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al guardar los proyectos seleccionados");
            throw new DataBaseConnectionException("Error al guardar los proyectos seleccionados");
        }
    }

    @Override
    public List<Project> getSelectedProjects(Student student) throws DataBaseConnectionException {
        ArrayList<Project> selectedProjects = new ArrayList<>();
        String queryGetSelectedProjects = "SELECT proyecto_seleccionado FROM seleccion WHERE matricula = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryGetSelectedProjects)){
            preparedStatement.setString(1,student.getEnrollment());
            ResultSet resultSet = preparedStatement.executeQuery();
            ProjectDAO projectDAO = new ProjectDAO();
            while (resultSet.next()) {
                int idProject = resultSet.getInt("proyecto_seleccionado");
                Project project = projectDAO.getProjectById(idProject);
                selectedProjects.add(project);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al obtener los proyectos seleccionados");
            throw new DataBaseConnectionException("Error al obtener los proyectos seleccionados");
        }
        return selectedProjects;
    }

    @Override
    public boolean assignProject(Student student, Project project) throws DataBaseConnectionException {
        boolean assigned = false;
        String queryAssignProject = "UPDATE alumno set proyecto_asignado = ? where matricula = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryAssignProject)) {
            preparedStatement.setInt(1,project.getProjectId());
            preparedStatement.setString(2,student.getEnrollment());
            assigned = preparedStatement.executeUpdate() > 0;
            if(assigned) {
                project.setAvailablePlaces(project.getAvailablePlaces()-1);
                ProjectDAO projectDAO = new ProjectDAO();
                projectDAO.modifyProject(project);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al asignar un proyecto");
            throw new DataBaseConnectionException("Error al asignar el proyecto");
        }
        return assigned;
    }

    @Override
    public boolean assignEducationalExperience(Student student, EducationalExperience experience) throws DataBaseConnectionException {
        boolean assigned = false;
        String queryAssignEE = "UPDATE alumno SET nrc = ? where matricula = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryAssignEE)) {
            preparedStatement.setString(1,experience.getNrc());
            preparedStatement.setString(2,student.getEnrollment());
            assigned = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al asignar una experiencia educativa");
            throw new DataBaseConnectionException("Error al asignar la experiencia educativa");
        }
        return assigned;
    }
}
