package mx.fei.logic.idao;

import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IDAOProfessor {
    Professor getProfessorById(int idProfessor) throws DataOperationException;

    Professor buildProfessorFromResultSet(ResultSet resultSet) throws SQLException;

    Professor getProfessorByPersonalNumber(int personalNumber) throws DataOperationException;

    boolean registerProfessor(Professor professor) throws DataOperationException;

    List<Professor> getProfessors() throws DataOperationException;

    boolean modifyProfessor(Professor professor) throws DataOperationException;

    boolean existsCoordinator() throws DataOperationException;
}
