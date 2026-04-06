package mx.fei.logic.idao;

import mx.fei.logic.dto.Professor;

import java.util.List;

public interface IDAOProfessor {
    Professor getProfessorByPersonalNumber(int personalNumber);

    boolean registerProfessor(Professor professor);

    List<Professor> getProfessors();

    boolean modifyProfessor(Professor professor);
}
