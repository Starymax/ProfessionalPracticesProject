package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOStudent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDAO implements IDAOStudent {
    private  Logger logger = Logger.getLogger(StudentDAO.class.getName());

    @Override
    public Student getStudentByEnrollment(String enrollment) throws DataOperationException, NoSuchElementException {
        Student student = null;
        String querygetStudentByEnrollment = "SELECT * FROM vw_alumnos where matricula=?;";
        if (enrollment == null || enrollment.isBlank()) {
            logger.log(Level.WARNING, "La matricula esta vacia");
            throw new IllegalArgumentException("La matricula no puede estar vacia");
        } else {
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(querygetStudentByEnrollment);) {
                preparedStatement.setString(1,enrollment);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int idUser = resultSet.getInt("id_usuario");
                    String name = resultSet.getString("nombre");
                    String lastName = resultSet.getString("apellidos");
                    String mail = resultSet.getString("correo");
                    String password = resultSet.getString("contrasena");
                    boolean activeStatus = resultSet.getBoolean("activo");
                    String gender = resultSet.getString("genero");
                    Boolean indigenousLanguage = resultSet.getBoolean("lengua_indigena");
                    Float grade = resultSet.getFloat("calificacion");
                    int studentProjectId = resultSet.getInt("proyecto");
                    String nrc = resultSet.getString("nrc");
                    resultSet.close();
                    Project project = null;
                    if (studentProjectId > 0) {
                        ProjectDAO projectDAO = new ProjectDAO();
                        project = projectDAO.getProjectById(studentProjectId);
                    }
                    EducationalExperience educationalExperience = null;
                    if (nrc != null && !nrc.isBlank()) {
                        EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
                        educationalExperience = educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
                    }
                    student = new Student(idUser, name, lastName, mail, password, gender, activeStatus, enrollment, indigenousLanguage, grade, project, educationalExperience);
                }
                if (student == null) {
                    logger.log(Level.WARNING, "No se encontro el estudiante con la matricula: " + enrollment);
                    throw new NoSuchElementException("No se encontro el estudiante");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE,"Error al buscar el estudiante por matricula",e);
                throw new DataOperationException("Error al obtener los datos del estudiante");
            }
        }
        return student;
    }

    @Override
    public Student getStudentById(Integer idStudent) throws DataOperationException, NoSuchElementException {
        Student student = null;
        String querygetStudentById = "SELECT * FROM vw_alumnos where id_usuario=?;";
        if (idStudent == null || idStudent.intValue() == 0) {
            logger.log(Level.WARNING, "El id esta vacio");
            throw new IllegalArgumentException("El Id no puede estar vacio");
        } else {
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(querygetStudentById)) {
                preparedStatement.setInt(1,idStudent);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("nombre");
                    String lastName = resultSet.getString("apellidos");
                    String mail = resultSet.getString("correo");
                    String enrollment = resultSet.getString("matricula");
                    String password = resultSet.getString("contrasena");
                    boolean activeStatus = resultSet.getBoolean("activo");
                    String gender = resultSet.getString("genero");
                    boolean indigenousLanguage = resultSet.getBoolean("lengua_indigena");
                    float grade = resultSet.getFloat("calificacion");
                    int studentProjectId = resultSet.getInt("proyecto");
                    String nrc = resultSet.getString("nrc");
                    resultSet.close();
                    Project project = null;
                    if (studentProjectId > 0) {
                        ProjectDAO projectDAO = new ProjectDAO();
                        project = projectDAO.getProjectById(studentProjectId);
                    }
                    EducationalExperience educationalExperience = null;
                    if (nrc != null && !nrc.isBlank()) {
                        EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
                        educationalExperience = educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
                    }
                    student = new Student(idStudent, name, lastName, mail, password, gender, activeStatus, enrollment, indigenousLanguage, grade, project, educationalExperience);
                }
                if (student == null) {
                    logger.log(Level.WARNING, "No se encontro el estudiante con el id: " + idStudent);
                    throw new NoSuchElementException("No se encontro el estudiante");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE,"Error al buscar el estudiante por ID",e);
                throw new DataOperationException("Error al obtener los datos del estudiante");
            }
        }
        return student;
    }

    @Override
    public boolean registerStudent(Student student) throws DataOperationException {
        boolean result = false;
        if (student == null) {
            logger.log(Level.WARNING, "El estudiante es nulo");
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        } else {
            try {
                getStudentByEnrollment(student.getEnrollment());
                logger.log(Level.WARNING, "Ya existe un estudiante con la matricula: " + student.getEnrollment());
                throw new IllegalStateException("Ya existe un estudiante con esa matricula");
            } catch (NoSuchElementException e) {
                logger.log(Level.INFO, "Matricula disponible para el registro");
            }
            try {
                UserDAO userDAO = new UserDAO();
                int idUser = userDAO.registerUser(student);
                if (idUser == RegistrationStatus.FAILURE.getValue()) {
                    logger.log(Level.SEVERE, "No se logro registrar el usuario");
                    throw new DataOperationException("No se logro registrar el usuario");
                }
                String queryRegisterStudent = "INSERT INTO alumno (id_usuario, matricula, lengua_indigena) VALUES (?,?,?)";
                try (Connection connection = DatabaseConnectionManager.getConnection();
                     PreparedStatement preparedStatementStudent = connection.prepareStatement(queryRegisterStudent)) {
                    preparedStatementStudent.setInt(1, idUser);
                    preparedStatementStudent.setString(2, student.getEnrollment());
                    preparedStatementStudent.setBoolean(3, student.isIndigenousLanguage());
                    result = preparedStatementStudent.executeUpdate() > 0;
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error registrando al estudiante", e);
                throw new DataOperationException("Error al registrar el alumno");
            }
        }
        return result;
    }

    @Override
    public boolean modifyStudent(Student student) throws DataOperationException {
        boolean updated = false;
        String queryModifyStudent = "UPDATE alumno SET lengua_indigena=?, calificacion=? where id_usuario=?;";
        if (student != null) {
            try (Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(queryModifyStudent)) {
                preparedStatement.setBoolean(1, student.isIndigenousLanguage());
                preparedStatement.setFloat(2, student.getGrade());
                preparedStatement.setInt(3, student.getUserId());
                updated = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al modificar el alumno",e);
                throw new DataOperationException("Error al modificar el alumno");
            }
        } else {
            logger.log(Level.WARNING, "El estudiante es nulo");
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        }
        return updated;
    }

    @Override
    public List<Student> getStudents() throws DataOperationException {
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
            logger.log(Level.SEVERE,"Error al obtener todos los estudiantes",e);
            throw new DataOperationException("Error al obtener los estudiantes");
        }
        return students;
    }

    @Override
    public List<Student> getStudentsWithoutProject() throws DataOperationException {
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
            logger.log(Level.SEVERE,"Error al obtener todos los estudiantes sin proyecto asignado",e);
            throw new DataOperationException("Error al obtener los estudiantes sin proyecto");
        }
        return students;
    }

    @Override
    public List<Student> getActiveStudents() throws DataOperationException {
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
            logger.log(Level.SEVERE,"Error al obtener todos los estudiantes activos",e);
            throw new DataOperationException("Error al obtener los estudiantes activos");
        }
        return students;
    }

    @Override
    public void saveSelectedProjects(List<Project> selectedProjects, Student student) throws DataOperationException {
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
            logger.log(Level.SEVERE,"Error al guardar los proyectos seleccionados",e);
            throw new DataOperationException("Error al guardar los proyectos seleccionados");
        }
    }

    @Override
    public List<Project> getSelectedProjects(Student student) throws DataOperationException {
        ArrayList<Project> selectedProjects = new ArrayList<>();
        String queryGetSelectedProjects = "SELECT proyecto_seleccionado FROM seleccion WHERE matricula = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryGetSelectedProjects)){
            preparedStatement.setString(1,student.getEnrollment());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> projectIds = new ArrayList<>();
            while (resultSet.next()) {
                projectIds.add(resultSet.getInt("proyecto_seleccionado"));
            }
            ProjectDAO projectDAO = new ProjectDAO();
            for(Integer projectId : projectIds) {
                selectedProjects.add(projectDAO.getProjectById(projectId));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al obtener los proyectos seleccionados",e);
            throw new DataOperationException("Error al obtener los proyectos seleccionados");
        }
        return selectedProjects;
    }

    @Override
    public boolean assignProject(Student student, Project project) throws DataOperationException {
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
            logger.log(Level.SEVERE,"Error al asignar un proyecto",e);
            throw new DataOperationException("Error al asignar el proyecto");
        }
        return assigned;
    }

    @Override
    public boolean assignEducationalExperience(Student student, EducationalExperience experience) throws DataOperationException {
        boolean assigned = false;
        String queryAssignEE = "UPDATE alumno SET nrc = ? where matricula = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryAssignEE)) {
            preparedStatement.setString(1,experience.getNrc());
            preparedStatement.setString(2,student.getEnrollment());
            assigned = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al asignar una experiencia educativa",e);
            throw new DataOperationException("Error al asignar la experiencia educativa");
        }
        return assigned;
    }
}
