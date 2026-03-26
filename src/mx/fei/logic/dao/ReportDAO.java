package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.Student;
import mx.fei.logic.idao.IReportDAO;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportDAO implements IReportDAO {
    private static final Logger logger = Logger.getLogger(ReportDAO.class.getName());

    @Override
    public boolean createReport(Report report) {
        if (report == null) {
            return false;
        }
        String queryReport = "INSERT INTO reporte (horas_realizadas, tipo_reporte, fecha, observaciones_reporte, id_alumno) VALUES (?,?,?,?,?)";
        try {
            DatabaseConnectionManager databaseConnectionManager = DatabaseConnectionManager.buildConnection();
            PreparedStatement preparedStatement = databaseConnectionManager.preparedStatement(queryReport);
            preparedStatement.setFloat(1, report.getWorkedHours());
            preparedStatement.setString(2, report.getReportType());
            preparedStatement.setDate(3, (Date) report.getDate());
            preparedStatement.setString(4, report.getObservationsReport());
            preparedStatement.setInt(5, report.getStudent().getUserId());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al crear reporte", e);
        }
        return false;
    }

    @Override
    public List<Report> consultReports() {
        String queryViewReport = "SELECT * FROM vw_reportes";
        List<Report> reports = new ArrayList<>();
        try {
            DatabaseConnectionManager databaseConnectionManager = DatabaseConnectionManager.buildConnection();
            PreparedStatement preparedStatement = databaseConnectionManager.preparedStatement(queryViewReport);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Student student = StudentDAO.getStudentByEnrollment(resultSet.getString("matricula"));
                int reportId = resultSet.getInt("id_reporte");
                float workedHours = resultSet.getFloat("horas_realizadas");
                String reportType = resultSet.getString("tipo_reporte");
                Date reportDate = resultSet.getDate("fecha");
                String observations = resultSet.getString("observaciones_reporte");
                Report report = new Report(reportId, workedHours, reportType, reportDate, observations, student);
                reports.add(report);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar reportes", e);
        }
        return reports;
    }

    @Override
    public Report getReportById(int reportId) {
        String queryViewReport = "SELECT * FROM vw_reportes";
        Report report = null;
        try {
            DatabaseConnectionManager databaseConnectionManager = DatabaseConnectionManager.buildConnection();
            PreparedStatement preparedStatement = databaseConnectionManager.preparedStatement(queryViewReport);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Student student = StudentDAO.getStudentByEnrollment(resultSet.getString("matricula"));
                float workedHours = resultSet.getFloat("horas_realizadas");
                String reportType = resultSet.getString("tipo_reporte");
                Date reportDate = resultSet.getDate("fecha");
                String observations = resultSet.getString("observaciones_reporte");
                report = new Report(reportId, workedHours, reportType, reportDate, observations, student);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar reportes", e);
        }
        return report;
    }
}
