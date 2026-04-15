package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.exceptions.DataBaseConnectionException;
import mx.fei.logic.idao.IDAOProfessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfessorDAO implements IDAOProfessor {
    private final Logger logger = Logger.getLogger(ProfessorDAO.class.getName());
    @Override
    public Professor getProfessorByPersonalNumber(int personalNumber) throws DataBaseConnectionException {
        Professor professor = null;
        String queryProfessor = "SELECT * FROM vw_profesor WHERE numero_de_personal = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryProfessor)) {
            preparedStatement.setInt(1, personalNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idUser = resultSet.getInt("id_usuario");
                String name = resultSet.getString("nombre");
                String lastName = resultSet.getString("apellidos");
                String mail = resultSet.getString("correo");
                String password = resultSet.getString("contrasena");
                boolean activeStatus = resultSet.getBoolean("estado_activo");
                String gender = resultSet.getString("genero");
                boolean isCoordinator = resultSet.getBoolean("es_coordinador");
                boolean isAdmin = resultSet.getBoolean("es_administrador");
                String shift = resultSet.getString("turno");
                professor = new Professor(idUser, name, lastName, mail, password, gender, activeStatus, personalNumber, isCoordinator, isAdmin, shift);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el profesor");
            throw new DataBaseConnectionException("Error al obtener el profesor");
        }
        return professor;
    }

    @Override
    public boolean registerProfessor(Professor professor) throws DataBaseConnectionException {
        boolean registered = false;
        if (professor != null && getProfessorByPersonalNumber(professor.getPersonalNumber()) == null) {
            try {
                UserDAO userDAO = new UserDAO();
                int idUser = userDAO.registerUser(professor);
                if (idUser != RegistrationStatus.FAILURE.getValue()) {
                    String queryRegisterProfessor = "INSERT INTO profesor (id_usuario, numero_de_personal, es_coordinador, es_administrador, turno) " + "VALUES (?, ?, ?, ?, ?);";
                    try (Connection connection = DatabaseConnectionManager.getConnection();
                         PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterProfessor)) {
                        preparedStatement.setInt(1, idUser);
                        preparedStatement.setInt(2, professor.getPersonalNumber());
                        preparedStatement.setBoolean(3, professor.isCoordinator());
                        preparedStatement.setBoolean(4, professor.isAdmin());
                        preparedStatement.setString(5, professor.getShift());
                        registered = preparedStatement.executeUpdate() > 0;
                    }
                } else {
                    logger.log(Level.WARNING, "No se logro registrar el usuario en la base de datos");
                    throw new DataBaseConnectionException("No se logro registrar el profesor");
                }
            }catch (SQLException e) {
                logger.log(Level.SEVERE,"Error registrando el profesor");
                throw new DataBaseConnectionException("Error al registrar el profesor");
            }
        } else if (professor == null) {
            logger.log(Level.WARNING, "El profesor es nulo");
            throw new IllegalArgumentException("El profesor es nulo");
        } else {
            logger.log(Level.WARNING, "El profesor con el numero de personal ya existe");
            throw new IllegalStateException("El profesor con el numero de personal ya existe");
        }
        return registered;
    }

    @Override
    public List<Professor> getProfessors() throws DataBaseConnectionException {
        List<Professor> professors = new ArrayList<>();
        String queryRegisterProfessor = "SELECT numero_de_personal FROM profesor;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterProfessor)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> personalNumbers = new ArrayList<>();
            while (resultSet.next()) {
                personalNumbers.add(resultSet.getInt("numero_de_personal"));
            }
            resultSet.close();
            for (Integer personalNumer : personalNumbers) {
                professors.add(getProfessorByPersonalNumber(personalNumer));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo todos los profesores");
            throw new DataBaseConnectionException("Error al obtener a los profesores");
        }
        return professors;
    }

    @Override
    public boolean modifyProfessor(Professor professor) throws DataBaseConnectionException {
        boolean updated = false;
        String queryModifyProfessor = "UPDATE profesor set es_coordinador=?, es_administrador=?, turno=? WHERE numero_de_personal=?;";
        if (professor != null) {
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(queryModifyProfessor)) {
                preparedStatement.setBoolean(1, professor.isCoordinator());
                preparedStatement.setBoolean(2, professor.isAdmin());
                preparedStatement.setString(3, professor.getShift());
                updated = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error modificando los datos del profesor");
                throw new DataBaseConnectionException("Error al modificar los datos del profesor");
            }
        }
        return updated;
    }

}
