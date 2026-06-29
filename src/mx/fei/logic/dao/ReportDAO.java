package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.ReportActivityProgress;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOReport;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Report and its activity/weekly progress detail.
 * Provides persistence and retrieval operations on the reporte,
 * reporte_actividad and reporte_actividad_semana tables.
 */
public class ReportDAO implements IDAOReport {

    private static final Logger LOGGER = Logger.getLogger(ReportDAO.class.getName());

    /**
     * Builds a Report from the current row of the given result set.
     *
     * @param resultSet a result set positioned on a valid row
     * @param student the student the report belongs to
     * @return the Report represented by the current row
     * @throws SQLException if a column cannot be read from the result set
     */
    @Override
    public Report buildReportFromResultSet(ResultSet resultSet, Student student) throws SQLException {
        int reportId = resultSet.getInt("id_reporte");
        String reportType = resultSet.getString("tipo_reporte");
        Date reportDate = resultSet.getDate("fecha_reporte");
        String observations = resultSet.getString("observaciones");
        String resultsObtained = resultSet.getString("resultados_obtenidos");
        String nrc = resultSet.getString("nrc");
        return new Report(reportId, reportType, reportDate, observations, resultsObtained, student, nrc);
    }

    /**
     * Inserts the base report record and assigns the generated id back to the report.
     *
     * @param report the report to create, must not be null
     * @return the generated identifier of the new report, or 0 if none was generated
     * @throws IllegalArgumentException if report is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public int createReport(Report report) throws DataOperationException {
        int idGenerated = 0;
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
        String query = "INSERT INTO reporte (tipo_reporte, fecha_reporte, observaciones, resultados_obtenidos, id_alumno, nrc) VALUES (?,?,?,?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, report.getReportType());
            preparedStatement.setDate(2, new Date(report.getReportDate().getTime()));
            preparedStatement.setString(3, report.getObservations());
            preparedStatement.setString(4, report.getResultsObtained());
            preparedStatement.setInt(5, report.getStudent().getUserId());
            preparedStatement.setString(6, report.getNrc());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerated = generatedKeys.getInt(1);
                    report.setReportId(idGenerated);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear el reporte", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al crear el reporte.");
        }
        return idGenerated;
    }

    /**
     * Creates a monthly report together with its activity progress and weekly progress detail,
     * with a single transaction.
     *
     * @param report the monthly report to create, must not be null
     * @return true if the report and all its detail were persisted successfully
     * @throws IllegalArgumentException if report is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean createMonthlyReport(Report report) throws DataOperationException {
        requireReport(report);
        return persistReportWithWeeklyDetail(report, "Error al crear el reporte mensual");
    }

    /**
     * Validates that the given report is present.
     *
     * @param report the report to validate
     * @throws IllegalArgumentException if report is null
     */
    @Override
    public void requireReport(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
    }

    /**
     * Persists a report together with its activity progress and weekly progress detail
     * with a single transaction, rolling back if the base report could not be created.
     *
     * @param report the report to persist
     * @param errorMessage the base message used for logging and the thrown exception
     * @return true if the report and all its detail were persisted successfully
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean persistReportWithWeeklyDetail(Report report, String errorMessage) throws DataOperationException {
        boolean reportPersisted = false;
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                int reportId = insertReport(connection, report);
                if (reportId == 0) {
                    connection.rollback();
                } else {
                    insertActivitiesWithWeeklyDetail(connection, report, reportId);
                    connection.commit();
                    reportPersisted = true;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, errorMessage, e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException(errorMessage);
        }
        return reportPersisted;
    }

    /**
     * Inserts the base report record and returns its generated identifier.
     *
     * @param connection the active connection participating in the transaction
     * @param report the report to insert
     * @return the generated report identifier, or 0 if none was generated
     * @throws SQLException if a database error occurs
     */
    private int insertReport(Connection connection, Report report) throws SQLException {
        int idGenerated = 0;
        String query = "INSERT INTO reporte (tipo_reporte, fecha_reporte, observaciones, resultados_obtenidos, id_alumno, nrc) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, report.getReportType());
            preparedStatement.setDate(2, new Date(report.getReportDate().getTime()));
            preparedStatement.setString(3, report.getObservations());
            preparedStatement.setString(4, report.getResultsObtained());
            preparedStatement.setInt(5, report.getStudent().getUserId());
            preparedStatement.setString(6, report.getNrc());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerated = generatedKeys.getInt(1);
                    report.setReportId(idGenerated);
                }
            }
        }
        return idGenerated;
    }

    /**
     * Inserts every activity progress of a report along with its weekly progress detail.
     *
     * @param connection the active transactional connection
     * @param report the report whose activity progress is inserted
     * @param reportId the generated identifier of the base report
     * @throws SQLException if a database error occurs
     */
    @Override
    public void insertActivitiesWithWeeklyDetail(Connection connection, Report report, int reportId) throws SQLException {
        String queryActivities = "INSERT INTO reporte_actividad (porcentaje_avance, observaciones, id_reporte, id_actividad) VALUES (?,?,?,?)";
        for (ReportActivityProgress activityProgress : report.getActivityProgressList()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryActivities, Statement.RETURN_GENERATED_KEYS)) {
                bindActivityProgress(preparedStatement, activityProgress, reportId);
                preparedStatement.executeUpdate();
                insertWeeklyDetail(connection, preparedStatement, activityProgress);
            }
        }
    }

    /**
     * Binds the activity progress parameters to the given statement.
     *
     * @param preparedStatement the statement to bind
     * @param activityProgress the activity progress to bind
     * @param reportId the generated identifier of the base report
     * @throws SQLException if a parameter cannot be set
     */
    @Override
    public void bindActivityProgress(PreparedStatement preparedStatement, ReportActivityProgress activityProgress, int reportId) throws SQLException {
        preparedStatement.setFloat(1, activityProgress.getProgressPercentage());
        preparedStatement.setString(2, activityProgress.getObservations());
        preparedStatement.setInt(3, reportId);
        preparedStatement.setInt(4, activityProgress.getActivity().getActivityId());
    }

    /**
     * Inserts the weekly progress detail for the activity progress just persisted by the given statement.
     *
     * @param connection the active transactional connection
     * @param activityStatement the statement that inserted the activity progress
     * @param activityProgress the activity progress whose weekly detail is inserted
     * @throws SQLException if a database error occurs
     */
    @Override
    public void insertWeeklyDetail(Connection connection, PreparedStatement activityStatement, ReportActivityProgress activityProgress) throws SQLException {
        String queryWeeklyProgress = "INSERT INTO reporte_actividad_semana (semana, horas_plan, horas_real, id_reporte_actividad) VALUES (?,?,?,?)";
        try (ResultSet generatedKeys = activityStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int reportActivityId = generatedKeys.getInt(1);
                try (PreparedStatement weeklyStatement = connection.prepareStatement(queryWeeklyProgress)) {
                    for (WeeklyLog weeklyProgress : activityProgress.getWeeklyProgressList()) {
                        weeklyStatement.setInt(1, weeklyProgress.getWeek());
                        weeklyStatement.setFloat(2, weeklyProgress.getPlannedHours());
                        weeklyStatement.setFloat(3, weeklyProgress.getWorkedHours());
                        weeklyStatement.setInt(4, reportActivityId);
                        weeklyStatement.addBatch();
                    }
                    weeklyStatement.executeBatch();
                }
            }
        }
    }

    /**
     * Creates a partial report together with its activity progress and weekly progress detail,
     * within a single transaction.
     *
     * @param report the partial report to create, must not be null
     * @return true if the report and all its detail were persisted successfully
     * @throws IllegalArgumentException if report is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean createPartialReport(Report report) throws DataOperationException {
        requireReport(report);
        return persistReportWithWeeklyDetail(report, "Error al crear el reporte parcial");
    }

    /**
     * Creates a final report together with its activity progress, within a single transaction.
     *
     * @param report the final report to create, must not be null
     * @return true if the report and its detail were persisted successfully
     * @throws IllegalArgumentException if report is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean createFinalReport(Report report) throws DataOperationException {
        boolean finalReportCreated = false;
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
        String queryActivities = "INSERT INTO reporte_actividad (porcentaje_avance, observaciones, id_reporte, id_actividad) VALUES (?,?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                int reportId = insertReport(connection, report);
                if (reportId == 0) {
                    connection.rollback();
                } else {
                    try (PreparedStatement preparedStatementActivity = connection.prepareStatement(queryActivities)) {
                        for (ReportActivityProgress activityProgress : report.getActivityProgressList()) {
                            preparedStatementActivity.setFloat(1, activityProgress.getProgressPercentage());
                            preparedStatementActivity.setString(2, activityProgress.getObservations());
                            preparedStatementActivity.setInt(3, reportId);
                            preparedStatementActivity.setInt(4, activityProgress.getActivity().getActivityId());
                            preparedStatementActivity.addBatch();
                        }
                        preparedStatementActivity.executeBatch();
                    }
                    connection.commit();
                    finalReportCreated = true;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear el reporte final", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al crear el reporte final.");
        }
        return finalReportCreated;
    }

    /**
     * Retrieves a report by its identifier, resolving its student.
     *
     * @param reportId the identifier of the report to retrieve
     * @return the matching Report, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public Report getReportById(int reportId) throws DataOperationException {
        Report report = null;
        String query = "SELECT id_reporte, tipo_reporte, fecha_reporte, observaciones, resultados_obtenidos, id_alumno, nrc FROM reporte WHERE id_reporte = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reportId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int studentId = resultSet.getInt("id_alumno");
                    StudentDAO studentDAO = new StudentDAO();
                    Student student = studentDAO.getStudentById(studentId);
                    report = buildReportFromResultSet(resultSet, student);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener el reporte por id", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el reporte.");
        }
        return report;
    }

    /**
     * Retrieves all reports of a student identified by enrollment, ordered by date descending.
     *
     * @param enrollment the student's enrollment
     * @return a list of the student's reports, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Report> getReportsByStudentEnrollment(String enrollment) throws DataOperationException {
        List<Report> reports = new ArrayList<>();
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByEnrollment(enrollment);
        String query = "SELECT r.id_reporte, r.tipo_reporte, r.fecha_reporte, r.observaciones, r.resultados_obtenidos, r.nrc " +
                "FROM reporte r JOIN alumno a ON r.id_alumno = a.id_usuario " +
                "WHERE a.matricula = ? ORDER BY r.fecha_reporte DESC";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enrollment);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(buildReportFromResultSet(resultSet, student));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los reportes del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los reportes del alumno.");
        }
        return reports;
    }

    /**
     * Counts how many reports of a given type a student has.
     *
     * @param reportType the report type to count
     * @param studentId the student's identifier
     * @return the number of matching reports
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public int countReportsByTypeAndStudent(String reportType, int studentId) throws DataOperationException {
        int count = 0;
        String query = "SELECT COUNT(*) FROM reporte WHERE tipo_reporte = ? AND id_alumno = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, reportType);
            preparedStatement.setInt(2, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al contar reportes por tipo", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al contar reportes por tipo.");
        }
        return count;
    }

    /**
     * Updates the observations of a report.
     *
     * @param reportId the identifier of the report to update
     * @param observations the new observations text
     * @return true if at least one row was updated
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean setObservations(int reportId, String observations) throws DataOperationException {
        boolean observationsUpdated = false;
        String query = "UPDATE reporte SET observaciones = ? WHERE id_reporte = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, observations);
            preparedStatement.setInt(2, reportId);
            observationsUpdated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar observaciones del reporte", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al actualizar observaciones del reporte.");
        }
        return observationsUpdated;
    }

    /**
     * Updates the observations of each activity associated with a report in a single batch.
     *
     * @param reportId the identifier of the report whose activity observations are updated
     * @param activityProgressList the activity progress entries carrying the new observations
     * @return true if the batch update completed successfully
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean updateActivityObservations(int reportId, List<ReportActivityProgress> activityProgressList) throws DataOperationException {
        boolean activityObservationsUpdated = false;
        String query = "UPDATE reporte_actividad SET observaciones = ? WHERE id_reporte = ? AND id_actividad = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (ReportActivityProgress activityProgress : activityProgressList) {
                preparedStatement.setString(1, activityProgress.getObservations());
                preparedStatement.setInt(2, reportId);
                preparedStatement.setInt(3, activityProgress.getActivity().getActivityId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            activityObservationsUpdated = true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar observaciones de actividades del reporte", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al actualizar observaciones de actividades del reporte.");
        }
        return activityObservationsUpdated;
    }
}
