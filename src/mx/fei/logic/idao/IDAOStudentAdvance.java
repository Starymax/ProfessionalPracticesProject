package mx.fei.logic.idao;

import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOStudentAdvance {
    boolean createAdvance(StudentAdvance advance) throws DataOperationException;

    StudentAdvance getAdvanceById(int advanceId) throws DataOperationException;

    List<StudentAdvance> getAdvancesByStudentId(int studentId) throws DataOperationException;

    List<StudentAdvance> getAdvancesByWeeklyLogId(int weeklyLogId) throws DataOperationException;

    boolean updateRealizedHours(int advanceId, float realizedHours) throws DataOperationException;

    List<StudentAdvance> getAdvancesByStudentAndWeeklyLog(int studentId, int weeklyLogId) throws DataOperationException;
}
