package mx.fei.logic.idao;

import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Student;

import java.util.List;

public interface IDAOEducationalExperience {
    boolean registerEducationalExperience(EducationalExperience educationalExperience);

    EducationalExperience getEducationalExperienceByNrc(String nrc);

    List<EducationalExperience> getEducationalExperiences();
}
