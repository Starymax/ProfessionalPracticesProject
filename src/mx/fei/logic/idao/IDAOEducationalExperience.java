package mx.fei.logic.idao;

import mx.fei.logic.dto.EducationalExperience;

import java.util.List;

public interface IDAOEducationalExperience {
    boolean registerEducationalExperience(EducationalExperience educationalExperience);

    EducationalExperience getEducationalExperienceByNrc(String nrc);

    List<EducationalExperience> getEducationalExperiences();

    boolean addStudentByNrc(String nrc);
}
