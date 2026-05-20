package mx.fei.logic.idao;

import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOEducationalExperience {
    boolean registerEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException;

    boolean modifyEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException;

    EducationalExperience getEducationalExperienceByNrc(String nrc) throws DataOperationException;

    List<EducationalExperience> getEducationalExperiences() throws DataOperationException;
}
