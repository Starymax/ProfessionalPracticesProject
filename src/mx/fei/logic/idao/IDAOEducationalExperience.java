package mx.fei.logic.idao;

import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IDAOEducationalExperience {
    boolean registerEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException;

    void requireEducationalExperience(EducationalExperience educationalExperience);

    void ensureSectionAvailable(String nrc, int section) throws DataOperationException;

    String requirePeriod(String period);

    boolean insertEducationalExperience(EducationalExperience educationalExperience, String period) throws DataOperationException;

    void setProfessorParameter(PreparedStatement preparedStatement, int index, Professor professor) throws SQLException;

    boolean modifyEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException;

    EducationalExperience getEducationalExperienceByNrcAndSection(String nrc, int section) throws DataOperationException;

    EducationalExperience searchExperienceByNrcAndSection(String nrc, int section) throws DataOperationException;

    EducationalExperience buildExperienceFromResultSet(ResultSet resultSet) throws SQLException, DataOperationException;

    Professor resolveProfessor(int idProfessor) throws DataOperationException;

    List<EducationalExperience> getEducationalExperiences() throws DataOperationException;

    List<EducationalExperience> getEducationalExperiencesByProfessor(int professorId) throws DataOperationException;

    List<EducationalExperience> getActiveEducationalExperiences() throws DataOperationException;
}
