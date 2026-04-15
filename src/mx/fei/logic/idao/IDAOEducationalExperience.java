package mx.fei.logic.idao;

import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.util.List;

public interface IDAOEducationalExperience {
    boolean registerEducationalExperience(EducationalExperience educationalExperience) throws DataBaseConnectionException;

    EducationalExperience getEducationalExperienceByNrc(String nrc) throws DataBaseConnectionException;

    List<EducationalExperience> getEducationalExperiences() throws DataBaseConnectionException;
}
