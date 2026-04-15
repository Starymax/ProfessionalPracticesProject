package mx.fei.logic.idao;

import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.User;
import mx.fei.logic.exceptions.DataBaseConnectionException;

public interface IDAOUser {
    boolean userExist(int idUser) throws DataBaseConnectionException;

    int registerUser(User user) throws DataBaseConnectionException;

    boolean updateUser(User user) throws DataBaseConnectionException;

}
