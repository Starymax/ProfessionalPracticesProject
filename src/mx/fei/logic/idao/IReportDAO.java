package mx.fei.logic.idao;

import mx.fei.logic.dto.Report;

import java.util.List;

public interface IReportDAO {
    public boolean createReport(Report report);

    public List<Report> consultReports();

    public Report getReportById(int reportId);
}
