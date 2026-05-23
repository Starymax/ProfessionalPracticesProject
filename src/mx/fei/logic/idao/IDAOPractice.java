package mx.fei.logic.idao;

import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

public interface IDAOPractice {
    Practice getPracticeById(int practiceId) throws DataOperationException;

    boolean createPractice(Practice practice) throws DataOperationException;

    Practice getPracticeByEnrollment(String enrollment) throws DataOperationException;

    String getCurrentPeriod();
}
