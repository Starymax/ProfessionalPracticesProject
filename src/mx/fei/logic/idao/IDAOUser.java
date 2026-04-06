package mx.fei.logic.idao;

import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.User;

public interface IDAOUser {
    boolean userExist(int idUser);

    int registerUser(User user);

    boolean updateUser(User user);

}
