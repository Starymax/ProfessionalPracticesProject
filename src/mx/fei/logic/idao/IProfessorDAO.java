package mx.fei.logic.idao;

import mx.fei.logic.dto.Professor;

import java.util.List;

public interface IProfessorDAO {
    public Professor getProfessorByPersonalNumber(int personalNumber);

    public boolean registerProfessor(Professor professor);

    public List<Professor> consultProfessors();
}
