package mx.fei.logic.idao;

import mx.fei.logic.dto.Practice;
import mx.fei.logic.exceptions.DataOperationException;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDAOPractice {
    Practice getPracticeById(int practiceId) throws DataOperationException;

    boolean createPractice(Practice practice) throws DataOperationException;

    void requirePractice(Practice practice);

    String requirePracticePeriod(String period);

    boolean insertPractice(Practice practice, String period) throws DataOperationException;

    Practice getPracticeByEnrollment(String enrollment) throws DataOperationException;

    void requireEnrollment(String enrollment);

    Practice buildPracticeFromRow(ResultSet resultSet, String enrollment) throws SQLException, DataOperationException;

    String getCurrentPeriod();
}
