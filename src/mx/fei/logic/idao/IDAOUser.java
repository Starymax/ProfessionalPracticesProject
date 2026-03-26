package mx.fei.logic.idao;

import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.User;

public interface IDAOUser {
    public boolean userExist(int idUser);
    public int registerUser(User user);
    public boolean updateUser(Student user);

}
