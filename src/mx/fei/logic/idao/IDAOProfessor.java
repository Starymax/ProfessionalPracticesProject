package mx.fei.logic.idao;

import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.util.List;

public interface IDAOProfessor {
    Professor getProfessorByPersonalNumber(int personalNumber) throws DataBaseConnectionException;

    boolean registerProfessor(Professor professor) throws DataBaseConnectionException;

    List<Professor> getProfessors() throws DataBaseConnectionException;

    boolean modifyProfessor(Professor professor) throws DataBaseConnectionException;
}
