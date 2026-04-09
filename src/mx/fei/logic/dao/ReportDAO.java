package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.Student;
import mx.fei.logic.idao.IDAOReport;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportDAO implements IDAOReport {
    private static final Logger logger = Logger.getLogger(ReportDAO.class.getName());

    @Override
    public boolean createReport(mx.fei.logic.dto.Report report) {
        boolean sucess = false;
        if (report != null) {
            String queryReport = "INSERT INTO reporte (horas_realizadas, tipo_reporte, fecha, observaciones_reporte, id_alumno) VALUES (?,?,?,?,?)";
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(queryReport);) {
                preparedStatement.setFloat(1, report.getWorkedHours());
                preparedStatement.setString(2, report.getReportType());
                preparedStatement.setDate(3, (Date) report.getDate());
                preparedStatement.setString(4, report.getObservationsReport());
                preparedStatement.setInt(5, report.getStudent().getUserId());
                preparedStatement.executeUpdate();
                connection.close();
                sucess = true;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al crear reporte", e);
            }
        }
        return sucess;
    }

    @Override
    public List<Report> getReportsByStudentEnrollment(String enrollment) {
        String queryViewReport = "SELECT id_reporte FROM vw_reportes WHERE matricula = ?";
        List<Report> reports = new ArrayList<>();
        List<Integer> idReports = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryViewReport);) {
            preparedStatement.setString(1, enrollment);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                idReports.add(resultSet.getInt("id_reporte"));
            }
            resultSet.close();
            for (int idReport : idReports) {
                reports.add(getReportById(idReport));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar reportes", e);
        }
        return reports;
    }

    @Override
    public Report getReportById(int reportId) {
        String queryViewReport = "SELECT * FROM vw_reportes where id_reporte = ?";
        Report report = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryViewReport);) {
            preparedStatement.setInt(1, reportId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String enrollment = resultSet.getString("matricula");
                float workedHours = resultSet.getFloat("horas_realizadas");
                String reportType = resultSet.getString("tipo_reporte");
                Date reportDate = resultSet.getDate("fecha");
                String observations = resultSet.getString("observaciones_reporte");
                resultSet.close();
                StudentDAO studentDao = new StudentDAO();
                Student student = studentDao.getStudentByEnrollment(enrollment);
                report = new Report(reportId, workedHours, reportType, reportDate, observations, student);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar reportes", e);
        }
        return report;
    }

    @Override
    public void setObservations(int reportId, String Observations) {

    }
}
