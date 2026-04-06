package mx.fei.logic.idao;

import mx.fei.logic.dto.SelfAssessment;

public interface IDAOSelfAssessment {
    boolean saveSelfAssessment();

    boolean loadSelfAssessment(SelfAssessment selfAssessment);

    boolean getSelfAssessment();
}
