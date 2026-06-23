package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOStudent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Student.
 * Provides persistence and retrieval operations on the alumno table and the
 * vw_alumnos view, as well as project selection and assignment operations.
 */
public class StudentDAO implements IDAOStudent {
    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    /**
     * Builds a Student from the current row of the given result set.
     * The assigned project is left unresolved and must be loaded separately if needed.
     *
     * @param resultSet a result set positioned on a valid row
     * @return the Student represented by the current row
     * @throws SQLException if a column cannot be read from the result set
     */
    @Override
    public Student buildStudentFromResultSet(ResultSet resultSet) throws SQLException {
        int idUser = resultSet.getInt("id_usuario");
        String name = resultSet.getString("nombre");
        String lastName = resultSet.getString("apellidos");
        String mail = resultSet.getString("correo");
        String password = resultSet.getString("contrasena");
        boolean activeStatus = resultSet.getBoolean("activo");
        String gender = resultSet.getString("genero");
        String enrollment = resultSet.getString("matricula");
        boolean indigenousLanguage = resultSet.getBoolean("lengua_indigena");
        int studentProjectId = resultSet.getInt("proyecto");
        float grade = resultSet.getFloat("calificacion");
        return new Student(idUser, name, lastName, mail, password, gender, activeStatus, enrollment, indigenousLanguage, null, grade, studentProjectId);
    }

    /**
     * Resolves and attaches the assigned project to each student that has a pending project id.
     * Students whose project cannot be found are still kept in the returned list without a project.
     *
     * @param students the students whose assigned projects should be resolved
     * @return the list of students with their assigned projects resolved when possible
     */
    @Override
    public List<Student> resolveProjectsOfStudents(List<Student> students) {
        List<Integer> projectIds = new ArrayList<>();
        for (Student student : students) {
            if (student.getPendingProjectId() > 0) {
                projectIds.add(student.getPendingProjectId());
            }
        }
        List<Project> resolvedProjects = new ArrayList<>();
        if (!projectIds.isEmpty()) {
            try {
                resolvedProjects = new ProjectDAO().getProjectsByIds(projectIds);
            } catch (DataOperationException e) {
                LOGGER.log(Level.WARNING, "Error al resolver los proyectos de los estudiantes");
            }
        }
        List<Student> validStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.getPendingProjectId() > 0) {
                Project found = null;
                for (Project project : resolvedProjects) {
                    if (project.getProjectId() == student.getPendingProjectId()) {
                        found = project;
                        break;
                    }
                }
                if (found != null) {
                    student.setAssignedProject(found);
                    validStudents.add(student);
                } else {
                    LOGGER.log(Level.WARNING, "No se encontró el proyecto para el alumno " + student.getUserId());
                }
            } else {
                validStudents.add(student);
            }
        }
        return validStudents;
    }

    /**
     * Retrieves a student by enrollment, resolving their assigned project.
     *
     * @param enrollment the student's enrollment, must not be null or blank
     * @return the matching Student
     * @throws IllegalArgumentException if enrollment is null or blank
     * @throws NoSuchElementException if no student exists with the given enrollment
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public Student getStudentByEnrollment(String enrollment) throws DataOperationException, NoSuchElementException {
        requireEnrollment(enrollment);
        Student student = searchStudentByEnrollment(enrollment);
        if (student == null) {
            LOGGER.log(Level.WARNING, "Error al buscar el estudiante por matrícula");
            throw new NoSuchElementException("No se encontró el estudiante");
        }
        resolveProjectsOfStudents(List.of(student));
        return student;
    }

    /**
     * Validates that the given enrollment is present.
     *
     * @param enrollment the enrollment to validate
     * @throws IllegalArgumentException if enrollment is null or blank
     */
    @Override
    public void requireEnrollment(String enrollment) {
        if (enrollment == null || enrollment.isBlank()) {
            LOGGER.log(Level.WARNING, "La matrícula está vacía");
            throw new IllegalArgumentException("La matrícula no puede estar vacía");
        }
    }

    /**
     * Searches the student matching the given enrollment, without resolving the assigned project.
     *
     * @param enrollment the enrollment to search for
     * @return the matching Student, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public Student searchStudentByEnrollment(String enrollment) throws DataOperationException {
        Student student = null;
        String query = "SELECT * FROM vw_alumnos WHERE matricula=?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enrollment);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    student = buildStudentFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los datos del estudiante", e);
            throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al obtener los datos del estudiante");
        }
        return student;
    }

    /**
     * Searches the student matching the given identifier, without resolving the assigned project.
     *
     * @param idStudent the user identifier to search for
     * @return the matching Student, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public Student searchStudentById(Integer idStudent) throws DataOperationException {
        Student student = null;
        String query = "SELECT * FROM vw_alumnos WHERE id_usuario=?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idStudent);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    student = buildStudentFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los datos del estudiante", e);
            throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al obtener los datos del estudiante");
        }
        return student;
    }

    /**
     * Retrieves a student by their user identifier, resolving their assigned project.
     *
     * @param idStudent the student's user identifier, must not be null or 0
     * @return the matching Student
     * @throws IllegalArgumentException if idStudent is null or 0
     * @throws DataOperationException if no student is found or a database error occurs
     */
    @Override
    public Student getStudentById(Integer idStudent) throws DataOperationException {
        requireStudentId(idStudent);
        Student student = searchStudentById(idStudent);
        if (student == null) {
            LOGGER.log(Level.WARNING, "No se encontro el estudiante con el id: " + idStudent);
            throw new DataOperationException("No se encontro el estudiante");
        }
        resolveProjectsOfStudents(List.of(student));
        return student;
    }

    /**
     * Validates that the given student identifier is present.
     *
     * @param idStudent the identifier to validate
     * @throws IllegalArgumentException if idStudent is null or 0
     */
    @Override
    public void requireStudentId(Integer idStudent) {
        if (idStudent == null || idStudent == 0) {
            LOGGER.log(Level.WARNING, "El id esta vacio");
            throw new IllegalArgumentException("El Id no puede estar vacio");
        }
    }

    /**
     * Registers a new student, creating the underlying user record first.
     *
     * @param student the student to register, must not be null
     * @return true if the student was registered successfully
     * @throws IllegalArgumentException if student is null
     * @throws IllegalStateException if a student with the same enrollment already exists
     * @throws DataOperationException if the user could not be created or a database error occurs
     */
    @Override
    public boolean registerStudent(Student student) throws DataOperationException {
        requireStudent(student);
        ensureEnrollmentAvailable(student.getEnrollment());
        int idUser = createUserForStudent(student);
        return insertStudentRecord(student, idUser);
    }

    /**
     * Validates that the given student is present.
     *
     * @param student the student to validate
     * @throws IllegalArgumentException if student is null
     */
    @Override
    public void requireStudent(Student student) {
        if (student == null) {
            LOGGER.log(Level.WARNING, "El estudiante es nulo");
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        }
    }

    /**
     * Ensures no student is already registered with the given enrollment.
     *
     * @param enrollment the enrollment to check
     * @throws IllegalStateException if a student with the same enrollment already exists
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public void ensureEnrollmentAvailable(String enrollment) throws DataOperationException {
        boolean exists = true;
        try {
            getStudentByEnrollment(enrollment);
        } catch (NoSuchElementException e) {
            LOGGER.log(Level.INFO, "Matricula disponible para el registro");
            exists = false;
        }
        if (exists) {
            LOGGER.log(Level.WARNING, "Ya existe un estudiante con la matricula: " + enrollment);
            throw new IllegalStateException("Ya existe un estudiante con esa matricula");
        }
    }

    /**
     * Creates the underlying user record for a student.
     *
     * @param student the student whose user record is created
     * @return the generated user identifier
     * @throws DataOperationException if the user could not be created
     */
    @Override
    public int createUserForStudent(Student student) throws DataOperationException {
        UserDAO userDAO = new UserDAO();
        int idUser = userDAO.registerUser(student);
        if (idUser == RegistrationStatus.FAILURE.getValue()) {
            LOGGER.log(Level.SEVERE, "No se logro registrar el usuario");
            throw new DataOperationException("No se logro registrar el usuario");
        }
        return idUser;
    }

    /**
     * Inserts the student-specific record linked to an existing user.
     *
     * @param student the student to insert
     * @param idUser the identifier of the previously created user
     * @return true if the student record was inserted
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean insertStudentRecord(Student student, int idUser) throws DataOperationException {
        String query = "INSERT INTO alumno (id_usuario, matricula, lengua_indigena, calificacion) VALUES (?,?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatementStudent = connection.prepareStatement(query)) {
            preparedStatementStudent.setInt(1, idUser);
            preparedStatementStudent.setString(2, student.getEnrollment());
            preparedStatementStudent.setBoolean(3, student.isIndigenousLanguage());
            preparedStatementStudent.setFloat(4, student.getGrade());
            return preparedStatementStudent.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registrando al estudiante", e);
            throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al registrar el alumno");
        }
    }

    /**
     * Updates the student-specific data (indigenous language flag and grade).
     *
     * @param student the student carrying the updated data, must not be null
     * @return true if at least one row was updated
     * @throws IllegalArgumentException if student is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean modifyStudent(Student student) throws DataOperationException {
        if (student == null) {
            LOGGER.log(Level.WARNING, "El estudiante es nulo");
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        }
        boolean updated = false;
        String query = "UPDATE alumno SET lengua_indigena=?, calificacion=? WHERE id_usuario=?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, student.isIndigenousLanguage());
            preparedStatement.setFloat(2, student.getGrade());
            preparedStatement.setInt(3, student.getUserId());
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al modificar el alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al modificar el alumno");
        }
        return updated;
    }

    /**
     * Retrieves all students with their assigned projects resolved.
     *
     * @return a list of all students, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Student> getStudents() throws DataOperationException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM vw_alumnos";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                students.add(buildStudentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los estudiantes", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los estudiantes");
        }
        return resolveProjectsOfStudents(students);
    }

    /**
     * Retrieves the students that have completed their three project selections but have not yet
     * been assigned a project.
     *
     * @return a list of students awaiting project assignment, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Student> getStudentsWithoutProject() throws DataOperationException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM vw_alumnos WHERE matricula IN (" +
                "SELECT a.matricula FROM alumno a " +
                "JOIN seleccion s ON a.matricula = s.matricula " +
                "WHERE a.proyecto_asignado IS NULL " +
                "GROUP BY a.matricula " +
                "HAVING COUNT(DISTINCT s.proyecto_seleccionado) = 3)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                students.add(buildStudentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los estudiantes sin proyecto asignado", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los estudiantes sin proyecto");
        }
        return resolveProjectsOfStudents(students);
    }

    /**
     * Retrieves all active students with their assigned projects resolved.
     *
     * @return a list of active students, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Student> getActiveStudents() throws DataOperationException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM vw_alumnos WHERE activo = true";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                students.add(buildStudentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los estudiantes activos", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los estudiantes activos");
        }
        return resolveProjectsOfStudents(students);
    }

    /**
     * Retrieves the students enrolled in the practice associated with a given educational experience.
     *
     * @param nrc the NRC of the educational experience, must not be null or blank
     * @return a list of students for the educational experience, empty if there are none
     * @throws IllegalArgumentException if nrc is null or blank
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Student> getStudentsByEducationalExperience(String nrc) throws DataOperationException {
        if (nrc == null || nrc.isBlank()) {
            LOGGER.log(Level.WARNING, "El nrc esta vacio");
            throw new IllegalArgumentException("El nrc no puede estar vacio");
        }
        List<Student> students = new ArrayList<>();
        String query = "SELECT v.* FROM vw_alumnos v INNER JOIN practicas p ON v.id_usuario = p.id_alumno WHERE p.nrc = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nrc);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(buildStudentFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los estudiantes de la experiencia educativa", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los estudiantes de la experiencia educativa");
        }
        return resolveProjectsOfStudents(students);
    }

    /**
     * Stores the projects a student has selected as preferences.
     *
     * @param selectedProjects the projects selected by the student
     * @param student the student making the selection
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public void saveSelectedProjects(List<Project> selectedProjects, Student student) throws DataOperationException {
        String query = "INSERT INTO seleccion (matricula, proyecto_seleccionado) VALUES (?,?);";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Project project : selectedProjects) {
                preparedStatement.setString(1, student.getEnrollment());
                preparedStatement.setInt(2, project.getProjectId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar los proyectos seleccionados", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al guardar los proyectos seleccionados");
        }
    }

    /**
     * Retrieves the projects a student selected as preferences.
     *
     * @param student the student whose selected projects are requested
     * @return a list of the student's selected projects, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Project> getSelectedProjects(Student student) throws DataOperationException {
        ArrayList<Project> selectedProjects = new ArrayList<>();
        String query = "SELECT proyecto_seleccionado FROM seleccion WHERE matricula = ?;";
        List<Integer> projectIds = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, student.getEnrollment());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    projectIds.add(resultSet.getInt("proyecto_seleccionado"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los proyectos seleccionados", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los proyectos seleccionados");
        }
        ProjectDAO projectDAO = new ProjectDAO();
        for (Integer projectId : projectIds) {
            selectedProjects.add(projectDAO.getProjectById(projectId));
        }
        return selectedProjects;
    }

    /**
     * Assigns a project to a student and decrements the project's available places.
     *
     * @param student the student to assign the project to
     * @param project the project being assigned
     * @return true if the project was assigned successfully
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean assignProject(Student student, Project project) throws DataOperationException {
        boolean assigned = false;
        String query = "UPDATE alumno SET proyecto_asignado = ? WHERE matricula = ?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, project.getProjectId());
            preparedStatement.setString(2, student.getEnrollment());
            assigned = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al asignar un proyecto", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al asignar el proyecto");
        }
        if (assigned) {
            project.setAvailablePlaces(project.getAvailablePlaces() - 1);
            ProjectDAO projectDAO = new ProjectDAO();
            projectDAO.modifyProject(project);
        }
        return assigned;
    }

    /**
     * Assigns an educational experience to a student by creating the corresponding practice record.
     *
     * @param practice the practice linking the student to the educational experience
     * @return true if the educational experience was assigned successfully
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean assignEducationalExperience(Practice practice) throws DataOperationException {
        boolean assigned = false;
        String query = "INSERT INTO practicas (id_alumno, nrc, periodo) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, practice.getStudent().getUserId());
            preparedStatement.setString(2, practice.getEducationalExperience().getNrc());
            preparedStatement.setString(3, practice.getEducationalExperience().getPeriod());
            assigned = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al asignar una experiencia educativa", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al asignar la experiencia educativa");
        }
        return assigned;
    }

    /**
     * Retrieves the active students who have not yet been assigned any educational experience.
     *
     * @return a list of students without an educational experience, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Student> getStudentsWithoutEducationalExperience() throws DataOperationException {
        String query = "SELECT u.id_usuario, u.nombre, u.apellidos, a.matricula " +
                "FROM usuario u " +
                "INNER JOIN alumno a ON u.id_usuario = a.id_usuario " +
                "LEFT JOIN practicas p ON a.id_usuario = p.id_alumno " +
                "WHERE p.id_alumno IS NULL AND u.estado_activo = 1";
        List<Student> students = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Student student = null;
                int id = resultSet.getInt("id_usuario");
                String names = resultSet.getString("nombre");
                String lastNames = resultSet.getString("apellidos");
                String enrollment = resultSet.getString("matricula");
                student = new Student(id, names, lastNames, enrollment);
                students.add(student);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo los estudiantes sin experiencia educativa ", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener alumnos sin experiencia: ");
        }
        return students;
    }
}
