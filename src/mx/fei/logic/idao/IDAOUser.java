package mx.fei.logic.idao;

import mx.fei.logic.dto.User;
import mx.fei.logic.exceptions.DataOperationException;

public interface IDAOUser {
    boolean userExist(int idUser) throws DataOperationException;

    int registerUser(User user) throws DataOperationException;

    boolean updateUser(User user) throws DataOperationException;

}
