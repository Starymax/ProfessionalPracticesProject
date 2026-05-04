package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOProjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectManagerDAO implements IDAOProjectManager {
    private Logger logger = Logger.getLogger(ProjectManagerDAO.class.getName());
    @Override
    public boolean registerProjectManager(ProjectManager projectManager) throws DataOperationException {
        boolean sucess = false;
        if (projectManager != null) {
            if (this.getProjectManagerById(projectManager.getProjectManagerId()) != null) {
                logger.log(Level.WARNING, "El Representante de Proyecto ingresado ya existe");
            } else {
                try {
                    String queryRegisterProfessor = "INSERT INTO responsable_proyecto (nombre_responsable, correo_responsable, telefono_responsable, cargo) VALUES (?, ?, ?, ?);";
                    try (Connection connection = DatabaseConnectionManager.getConnection();
                         PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterProfessor)) {
                        preparedStatement.setString(1, projectManager.getName());
                        preparedStatement.setString(2, projectManager.getEmailProjectManager());
                        preparedStatement.setString(3, projectManager.getPhoneNumberProjectManager());
                        preparedStatement.setString(4, projectManager.getRol());
                        preparedStatement.executeUpdate();
                        sucess = true;
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error al registrar el responsable en la base de datos",e);
                    throw new DataOperationException("Error al registrar el responsable en la base de datos");
                }
            }
        }
        return sucess;
    }

    @Override
    public ProjectManager getProjectManagerById(int idProjectManager) throws DataOperationException {
        ProjectManager projectManager =  null;
        String query = "SELECT * FROM responsable_proyecto WHERE id_responsable = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, idProjectManager);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("nombre_responsable");
                String email = resultSet.getString("correo_responsable");
                String phoneNumber = resultSet.getString("telefono_responsable");
                String rol = resultSet.getString("cargo");
                projectManager = new ProjectManager(idProjectManager, name, email, phoneNumber, rol);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el Responsable de la base de datos",e);
            throw new DataOperationException("Error al obtener el Responsable de la base de datos");
        }
        return projectManager;
    }

    @Override
    public List<ProjectManager> getProjectManagers() throws DataOperationException {
        List<ProjectManager> projectManagers = new ArrayList<>();
        String queryProjectManagers = "SELECT id_responsable FROM responsable_proyecto;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryProjectManagers)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                projectManagers.add(getProjectManagerById(resultSet.getInt("id_responsable")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error en la conexión a la base de datos",e);
            throw new DataOperationException("Error en la conexión a la base de datos");
        }
        return projectManagers;
    }
}
