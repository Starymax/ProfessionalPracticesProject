package mx.fei.logic.idao;

import mx.fei.logic.dto.Professor;

public interface IDAOProfessor {
    public Professor getProfessorByPersonalNumber();
    public void registerProfessor();
    public void modifyProfessor();
    public void convertToCordinator();
}
