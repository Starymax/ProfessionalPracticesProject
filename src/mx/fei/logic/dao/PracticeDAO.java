    package mx.fei.logic.dao;

    import mx.fei.dataaccess.DatabaseConnectionManager;
    import mx.fei.logic.dto.EducationalExperience;
    import mx.fei.logic.dto.Practice;
    import mx.fei.logic.dto.Student;
    import mx.fei.logic.exceptions.DataOperationException;
    import mx.fei.logic.idao.IDAOPractice;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.NoSuchElementException;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * Data Access Object for Practice.
     * Provides persistence and retrieval operations on the practicas table.
     */
    public class PracticeDAO implements IDAOPractice {
        private static final Logger LOGGER = Logger.getLogger(PracticeDAO.class.getName());

        /**
         * Retrieves a practice by its identifier, resolving its student and educational experience.
         *
         * @param practiceId the identifier of the practice to retrieve
         * @return the matching Practice
         * @throws NoSuchElementException if no practice exists with the given id
         * @throws DataOperationException if a database error occurs
         */
        @Override
        public Practice getPracticeById(int practiceId) throws DataOperationException {
            String query = "SELECT * FROM practicas WHERE id_practica = ?;";
            int studentId = 0;
            String nrc = null;
            String period = null;
            float grade = 0;
            boolean practiceFound = false;
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, practiceId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        studentId = resultSet.getInt("id_alumno");
                        nrc = resultSet.getString("nrc");
                        period = resultSet.getString("periodo");
                        grade = resultSet.getFloat("calificacion");
                        practiceFound = true;
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al buscar la práctica por id", e);
                if (DAOUtils.isConnectionError(e)) {
                    throw new DataOperationException("Error de conexión. Intente más tarde.");
                }
                throw new DataOperationException("Error al obtener los datos de la práctica");
            }
            if (!practiceFound) {
                LOGGER.log(Level.WARNING, "Error al buscar la práctica por id");
                throw new NoSuchElementException("No se encontró la práctica");
            }
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.getStudentById(studentId);
            EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
            EducationalExperience educationalExperience = educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
            return new Practice(student, educationalExperience, period, grade);
        }

        /**
         * Creates a new practice record.
         *
         * @param practice the practice to create, must not be null and must have a non-null student and a non-blank period
         * @return true if the practice was created successfully
         * @throws IllegalArgumentException if the practice, its student or its period are invalid
         * @throws DataOperationException if a database error occurs
         */
        @Override
        public boolean createPractice(Practice practice) throws DataOperationException {
            requirePractice(practice);
            String period = requirePracticePeriod(practice.getPeriod());
            return insertPractice(practice, period);
        }

        /**
         * Validates that the practice and its student are present.
         *
         * @param practice the practice to validate
         * @throws IllegalArgumentException if the practice or its student are null
         */
        @Override
        public void requirePractice(Practice practice) {
            if (practice == null) {
                LOGGER.log(Level.WARNING, "La practica es nula");
                throw new IllegalArgumentException("La practica no puede ser nula");
            }
            if (practice.getStudent() == null) {
                LOGGER.log(Level.WARNING, "El estudiante de la practica es nulo");
                throw new IllegalArgumentException("El estudiante de la practica no puede ser nulo");
            }
        }

        /**
         * Validates that the given practice period is present.
         *
         * @param period the period to validate
         * @return the validated period
         * @throws IllegalArgumentException if period is null or blank
         */
        @Override
        public String requirePracticePeriod(String period) {
            if (period == null || period.isBlank()) {
                LOGGER.log(Level.WARNING, "El periodo de la practica esta vacio");
                throw new IllegalArgumentException("El periodo de la practica no puede estar vacio");
            }
            return period;
        }

        /**
         * Inserts a new practice record.
         *
         * @param practice the practice to insert
         * @param period the validated period
         * @return true if the practice was inserted
         * @throws DataOperationException if a database error occurs
         */
        @Override
        public boolean insertPractice(Practice practice, String period) throws DataOperationException {
            String query = "INSERT INTO practicas (id_alumno, nrc, periodo, calificacion) VALUES (?, ?, ?, ?)";
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, practice.getStudent().getUserId());
                preparedStatement.setString(2, practice.getEducationalExperience().getNrc());
                preparedStatement.setString(3, period);
                preparedStatement.setFloat(4, practice.getGrade());
                return preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al crear la practica", e);
                throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al crear la practica");
            }
        }

        /**
         * Retrieves the most recent practice associated with a student's enrollment.
         *
         * @param enrollment the student's enrollment, must not be null or blank
         * @return the most recent matching Practice, or null if none was found
         * @throws IllegalArgumentException if enrollment is null or blank
         * @throws DataOperationException if a database error occurs
         */
        @Override
        public Practice getPracticeByEnrollment(String enrollment) throws DataOperationException {
            requireEnrollment(enrollment);
            String query = "SELECT p.id_practica, p.periodo, p.nrc, p.calificacion FROM practicas p INNER JOIN alumno a ON p.id_alumno = a.id_usuario WHERE a.matricula = ? ORDER BY p.id_practica DESC LIMIT 1";
            int practiceId = 0;
            String period = null;
            String nrc = null;
            float grade = 0;
            boolean practiceFound = false;
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, enrollment);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        practiceId = resultSet.getInt("id_practica");
                        period = resultSet.getString("periodo");
                        nrc = resultSet.getString("nrc");
                        grade = resultSet.getFloat("calificacion");
                        practiceFound = true;
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al obtener la practica por matricula", e);
                throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al obtener la practica");
            }
            if (!practiceFound || nrc == null || nrc.isBlank()) {
                return null;
            }
            EducationalExperience educationalExperience = new EducationalExperienceDAO().getEducationalExperienceByNrc(nrc);
            Student student = new StudentDAO().getStudentByEnrollment(enrollment);
            return new Practice(practiceId, student, educationalExperience, period != null ? period : "", grade);
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
                LOGGER.log(Level.WARNING, "La matricula esta vacia");
                throw new IllegalArgumentException("La matricula no puede estar vacia");
            }
        }

        /**
         * Builds the most recent practice from the current row, resolving its experience and student.
         *
         * @param resultSet a result set positioned on a valid row
         * @param enrollment the enrollment of the student the practice belongs to
         * @return the resolved Practice, or null when the row has no associated NRC
         * @throws SQLException if a column cannot be read from the result set
         * @throws DataOperationException if a database error occurs while resolving related data
         */
        @Override
        public Practice buildPracticeFromRow(ResultSet resultSet, String enrollment) throws SQLException, DataOperationException {
            int practiceId = resultSet.getInt("id_practica");
            String period = resultSet.getString("periodo");
            String nrc = resultSet.getString("nrc");
            float grade = resultSet.getFloat("calificacion");
            Practice practice = null;
            if (nrc != null && !nrc.isBlank()) {
                EducationalExperience educationalExperience = new EducationalExperienceDAO().getEducationalExperienceByNrc(nrc);
                Student student = new StudentDAO().getStudentByEnrollment(enrollment);
                practice = new Practice(practiceId, student, educationalExperience, period != null ? period : "", grade);
            }
            return practice;
        }

        /**
         * Retrieves all active students who have an assigned practice.
         *
         * @return a list of active students with a registered practice, empty if there are none
         * @throws DataOperationException if a database error occurs
         */
        @Override
        public List<Student> getStudentsWithPractice() throws DataOperationException {
            List<Integer> studentIds = new ArrayList<>();
            String query = "SELECT DISTINCT a.id_usuario FROM alumno a " +
                            "INNER JOIN practicas p ON p.id_alumno = a.id_usuario " +
                            "INNER JOIN usuario u ON u.id_usuario = a.id_usuario " +
                            "WHERE u.estado_activo = true";
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    studentIds.add(resultSet.getInt("id_usuario"));
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al obtener los estudiantes con práctica: " + e.getMessage());
                throw new DataOperationException("Error al obtener los estudiantes con práctica");
            }
            List<Student> students = new ArrayList<>();
            StudentDAO studentDAO = new StudentDAO();
            for (int studentId : studentIds) {
                try {
                    students.add(studentDAO.getStudentById(studentId));
                } catch (DataOperationException e) {
                    LOGGER.log(Level.WARNING, "No se pudo cargar el estudiante con id: " + studentId);
                }
            }
            return students;
        }

        /**
         * Returns the current period formatted as yyyy-MM.
         *
         * @return the current period based on the system date
         */
        @Override
        public String getCurrentPeriod() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            return LocalDate.now().format(formatter);
        }

        /**
         * Retrieves all active students enrolled in the educational experience identified by the given NRC.
         *
         * @param nrc the NRC of the educational experience, must not be null or blank
         * @return a list of active students enrolled in the educational experience, empty if there are none
         * @throws IllegalArgumentException if the NRC is null or blank
         * @throws DataOperationException if a database error occurs
         */
        @Override
        public List<Student> getStudentsByNrc(String nrc) throws DataOperationException {
            if (nrc == null || nrc.isBlank()) {
                LOGGER.log(Level.WARNING, "El NRC está vacío");
                throw new IllegalArgumentException("El NRC no puede estar vacío");
            }
            List<Integer> studentIds = new ArrayList<>();
            String query = "SELECT a.id_usuario FROM alumno a " +
                    "INNER JOIN practicas p ON p.id_alumno = a.id_usuario " +
                    "INNER JOIN usuario u ON u.id_usuario = a.id_usuario " +
                    "WHERE p.nrc = ? AND u.estado_activo = true";
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nrc);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        studentIds.add(resultSet.getInt("id_usuario"));
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al obtener los estudiantes por NRC: " + e.getMessage());
                throw new DataOperationException("Error al obtener los estudiantes de la experiencia");
            }
            List<Student> students = new ArrayList<>();
            StudentDAO studentDAO = new StudentDAO();
            for (int studentId : studentIds) {
                try {
                    students.add(studentDAO.getStudentById(studentId));
                } catch (DataOperationException e) {
                    LOGGER.log(Level.WARNING, "No se pudo cargar el estudiante con id: " + studentId);
                }
            }
            return students;
        }
    }