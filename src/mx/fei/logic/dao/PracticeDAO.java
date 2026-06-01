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
    import java.util.NoSuchElementException;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    public class PracticeDAO implements IDAOPractice {
        private static final Logger logger = Logger.getLogger(PracticeDAO.class.getName());

        @Override
        public Practice getPracticeById(int practiceId) throws DataOperationException {
            Practice practice = null;
            String query = "SELECT * FROM practicas WHERE id_practica=?;";
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, practiceId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int studentId = resultSet.getInt("id_alumno");
                        String nrc = resultSet.getString("nrc");
                        String period = resultSet.getString("periodo");
                        float grade = resultSet.getFloat("calificacion");
                        StudentDAO studentDAO = new StudentDAO();
                        Student student = studentDAO.getStudentById(studentId);
                        EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
                        EducationalExperience educationalExperience = educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
                        practice = new Practice(student, educationalExperience, period, grade);
                    }
                }
                if (practice == null) {
                    logger.log(Level.WARNING, "Error al buscar la práctica por id");
                    throw new NoSuchElementException("No se encontró la práctica");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al buscar la práctica por id", e);
                throw new DataOperationException("Error al obtener los datos de la práctica");
            }
            return practice;
        }

        @Override
        public boolean createPractice(Practice practice) throws DataOperationException {
            if (practice == null) {
                logger.log(Level.WARNING, "La practica es nula");
                throw new IllegalArgumentException("La practica no puede ser nula");
            }
            if (practice.getStudent() == null) {
                logger.log(Level.WARNING, "El estudiante de la practica es nulo");
                throw new IllegalArgumentException("El estudiante de la practica no puede ser nulo");
            }
            String period = practice.getPeriod();
            if (period == null || period.isBlank()) {
                logger.log(Level.WARNING, "El periodo de la practica esta vacio");
                throw new IllegalArgumentException("El periodo de la practica no puede estar vacio");
            }
            boolean result = false;
            String sql = "INSERT INTO practicas (id_alumno, nrc, periodo, calificacion) VALUES (?, ?, ?, ?)";
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, practice.getStudent().getUserId());
                preparedStatement.setString(2, practice.getEducationalExperience().getNrc());
                preparedStatement.setString(3, period);
                preparedStatement.setFloat(4, practice.getGrade());
                result = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al crear la practica", e);
                throw new DataOperationException("Error al crear la practica");
            }
            return result;
        }

        @Override
        public Practice getPracticeByEnrollment(String enrollment) throws DataOperationException {
            if (enrollment == null || enrollment.isBlank()) {
                logger.log(Level.WARNING, "La matricula esta vacia");
                throw new IllegalArgumentException("La matricula no puede estar vacia");
            }
            String query = "SELECT p.id_practica, p.periodo, p.nrc, p.calificacion FROM practicas p INNER JOIN alumno a ON p.id_alumno = a.id_usuario WHERE a.matricula = ? ORDER BY p.id_practica DESC LIMIT 1";
            Practice practice = null;
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, enrollment);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int practiceId = resultSet.getInt("id_practica");
                        String period = resultSet.getString("periodo");
                        String nrc = resultSet.getString("nrc");
                        float grade = resultSet.getFloat("calificacion");
                        if (nrc != null && !nrc.isBlank()) {
                            EducationalExperienceDAO experienceDAO = new EducationalExperienceDAO();
                            EducationalExperience educationalExperience = experienceDAO.getEducationalExperienceByNrc(nrc);
                            StudentDAO studentDAO = new StudentDAO();
                            Student student = studentDAO.getStudentByEnrollment(enrollment);
                            practice = new Practice(practiceId,student, educationalExperience, period != null ? period : "", grade);
                        }
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al obtener la practica por matricula", e);
                throw new DataOperationException("Error al obtener la practica");
            }
            return practice;
        }

        @Override
        public String getCurrentPeriod() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            return LocalDate.now().format(formatter);
        }
    }
