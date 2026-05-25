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

public class ReportDAO implements IDAOReport {

    private static final Logger LOGGER = Logger.getLogger(ReportDAO.class.getName());

    @Override
    public int createReport(Report report) throws DataOperationException {
        int idGenerated = 0;
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
        String query = "INSERT INTO reporte (tipo_reporte, fecha_reporte, observaciones, resultados_obtenidos, id_alumno, nrc) VALUES (?,?,?,?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, report.getReportType());
            preparedStatement.setDate(2, new Date(report.getReportDate().getTime()));
            preparedStatement.setString(3, report.getObservations());
            preparedStatement.setString(4, report.getResultsObtained());
            preparedStatement.setInt(5, report.getStudent().getUserId());
            preparedStatement.setString(6, report.getNrc());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                idGenerated = generatedKeys.getInt(1);
                report.setReportId(idGenerated);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear el reporte", e);
            throw new DataOperationException("Error al crear el reporte.");
        }
        return idGenerated;
    }

    @Override
    public boolean createMonthlyReport(Report report) throws DataOperationException {
        boolean success = false;
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
        String query = "INSERT INTO reporte_mensual (id_reporte, numero_reporte, mes, horas_reportadas, horas_acumuladas) VALUES (?,?,?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            int reportId = createReport(report);
            if (reportId == 0) {
                connection.rollback();
            } else {
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, reportId);
                    preparedStatement.setInt(2, report.getReportNumber());
                    preparedStatement.setString(3, report.getMonth());
                    preparedStatement.setFloat(4, report.getWorkedHours());
                    preparedStatement.setFloat(5, report.getAccumulatedHours());
                    preparedStatement.executeUpdate();
                }
                connection.commit();
                success = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear el reporte mensual", e);
            throw new DataOperationException("Error al crear el reporte mensual.");
        }
        return success;
    }

    @Override
    public boolean createPartialReport(Report report) throws DataOperationException {
        boolean success = false;
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
        String queryActivities = "INSERT INTO reporte_actividad (porcentaje_avance, observaciones, id_reporte, id_actividad) VALUES (?,?,?,?)";
        String queryWeeklyProgress = "INSERT INTO reporte_actividad_semana (semana, horas_plan, horas_real, id_reporte_actividad) VALUES (?,?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            int reportId = createReport(report);
            if (reportId == 0) {
                connection.rollback();
            } else {
                for (ReportActivityProgress activityProgress : report.getActivityProgressList()) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(queryActivities, Statement.RETURN_GENERATED_KEYS)) {
                        preparedStatement.setFloat(1, activityProgress.getProgressPercentage());
                        preparedStatement.setString(2, activityProgress.getObservations());
                        preparedStatement.setInt(3, reportId);
                        preparedStatement.setInt(4, activityProgress.getActivity().getActivityId());
                        preparedStatement.executeUpdate();
                        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int reportActivityId = generatedKeys.getInt(1);
                            try (PreparedStatement prepareStatement = connection.prepareStatement(queryWeeklyProgress)) {
                                for (WeeklyLog weeklyProgress : activityProgress.getWeeklyProgressList()) {
                                    prepareStatement.setInt(1, weeklyProgress.getWeek());
                                    prepareStatement.setFloat(2, weeklyProgress.getPlannedHours());
                                    prepareStatement.setFloat(3, weeklyProgress.getWorkedHours());
                                    prepareStatement.setInt(4, reportActivityId);
                                    prepareStatement.addBatch();
                                }
                                prepareStatement.executeBatch();
                            }
                        }
                    }
                }
                connection.commit();
                success = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear el reporte parcial", e);
            throw new DataOperationException("Error al crear el reporte parcial.");
        }
        return success;
    }

    @Override
    public boolean createFinalReport(Report report) throws DataOperationException {
        boolean success = false;
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
        String queryActivities = "INSERT INTO reporte_actividad (porcentaje_avance, observaciones, id_reporte, id_actividad) VALUES (?,?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            int reportId = createReport(report);
            if (reportId == 0) {
                connection.rollback();
            } else {
                for (ReportActivityProgress activityProgress : report.getActivityProgressList()) {
                    try (PreparedStatement psActivity = connection.prepareStatement(queryActivities)) {
                        psActivity.setFloat(1, activityProgress.getProgressPercentage());
                        psActivity.setString(2, activityProgress.getObservations());
                        psActivity.setInt(3, reportId);
                        psActivity.setInt(4, activityProgress.getActivity().getActivityId());
                        psActivity.addBatch();
                    }
                }
                connection.commit();
                success = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear el reporte final", e);
            throw new DataOperationException("Error al crear el reporte final.");
        }
        return success;
    }

    @Override
    public Report getReportById(int reportId) throws DataOperationException {
        Report report = null;
        String query = "SELECT id_reporte, tipo_reporte, fecha_reporte, observaciones, resultados_obtenidos, id_alumno, nrc FROM reporte WHERE id_reporte = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reportId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String reportType = resultSet.getString("tipo_reporte");
                Date reportDate = resultSet.getDate("fecha_reporte");
                String observations = resultSet.getString("observaciones");
                String resultsObtained = resultSet.getString("resultados_obtenidos");
                int studentId = resultSet.getInt("id_alumno");
                String nrc = resultSet.getString("nrc");
                StudentDAO studentDAO = new StudentDAO();
                Student student = studentDAO.getStudentById(studentId);
                report = new Report(reportId, reportType, reportDate, observations, resultsObtained, student, nrc);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener el reporte por id", e);
            throw new DataOperationException("Error al obtener el reporte.");
        }
        return report;
    }

    @Override
    public List<Report> getReportsByStudentEnrollment(String enrollment) throws DataOperationException {
        String query = "SELECT r.id_reporte FROM reporte r JOIN alumno a ON r.id_alumno = a.id_usuario WHERE a.matricula = ? ORDER BY r.fecha_reporte DESC";
        List<Report> reports = new ArrayList<>();
        List<Integer> reportIds = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enrollment);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reportIds.add(resultSet.getInt("id_reporte"));
            }
            for (int reportId : reportIds) {
                reports.add(getReportById(reportId));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los reportes del alumno", e);
            throw new DataOperationException("Error al obtener los reportes del alumno.");
        }
        return reports;
    }

    @Override
    public int countReportsByTypeAndStudent(String reportType, int studentId) throws DataOperationException {
        int count = 0;
        String query = "SELECT COUNT(*) FROM reporte WHERE tipo_reporte = ? AND id_alumno = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, reportType);
            preparedStatement.setInt(2, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al contar reportes por tipo", e);
            throw new DataOperationException("Error al contar reportes por tipo.");
        }
        return count;
    }

    @Override
    public boolean setObservations(int reportId, String observations) throws DataOperationException {
        boolean success = false;
        String query = "UPDATE reporte SET observaciones = ? WHERE id_reporte = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, observations);
            preparedStatement.setInt(2, reportId);
            success = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar observaciones del reporte", e);
            throw new DataOperationException("Error al actualizar observaciones del reporte.");
        }
        return success;
    }

    public boolean updateActivityObservations(int reportId, List<ReportActivityProgress> activityProgressList) throws DataOperationException {
        boolean success = false;
        String query = "UPDATE reporte_actividad SET observaciones = ? WHERE id_reporte = ? AND id_actividad = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (ReportActivityProgress activityProgress : activityProgressList) {
                preparedStatement.setString(1, activityProgress.getObservations());
                preparedStatement.setInt(2, reportId);
                preparedStatement.setInt(3, activityProgress.getActivity().getActivityId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            success = true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar observaciones de actividades del reporte", e);
            throw new DataOperationException("Error al actualizar observaciones de actividades del reporte.");
        }
        return success;
    }
}