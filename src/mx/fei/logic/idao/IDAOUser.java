package mx.fei.logic.idao;

import mx.fei.logic.dto.User;
import mx.fei.logic.dto.UserRole;
import mx.fei.logic.exceptions.DataOperationException;

public interface IDAOUser {
    boolean userExist(int idUser) throws DataOperationException;

    int registerUser(User user) throws DataOperationException;

    boolean updateUser(User user) throws DataOperationException;

    User getUserByEmail(String email) throws DataOperationException;

    void requireEmail(String email);

    int findUserIdByEmail(String email) throws DataOperationException;

    User loadUserByRole(int idUser) throws DataOperationException;

    boolean isStudent(int idUser) throws DataOperationException;

    void logInByRole(UserRole role) throws DataOperationException;

    void logout();
}
