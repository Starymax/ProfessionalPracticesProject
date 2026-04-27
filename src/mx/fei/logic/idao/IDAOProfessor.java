package mx.fei.logic.idao;

import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOProfessor {
    Professor getProfessorById(int idProfessor) throws DataOperationException;

    Professor getProfessorByPersonalNumber(int personalNumber) throws DataOperationException;

    boolean registerProfessor(Professor professor) throws DataOperationException;

    List<Professor> getProfessors() throws DataOperationException;

    boolean modifyProfessor(Professor professor) throws DataOperationException;

    boolean existsCoordinator() throws DataOperationException;
}
