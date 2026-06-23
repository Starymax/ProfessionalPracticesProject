package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Enterprise;
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

/**
 * Data Access Object for ProjectManager (project representatives).
 * Provides persistence and retrieval operations on the
 * responsable_proyecto table.
 */
public class ProjectManagerDAO implements IDAOProjectManager {
    private static final Logger LOGGER = Logger.getLogger(ProjectManagerDAO.class.getName());

    /**
     * Registers a new project manager, unless one with the same id already exists.
     *
     * @param projectManager the project manager to register, ignored if null
     * @return true if the project manager was registered successfully
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean registerProjectManager(ProjectManager projectManager) throws DataOperationException {
        boolean sucess = false;
        if (projectManager != null) {
            if (this.getProjectManagerById(projectManager.getProjectManagerId()) != null) {
                LOGGER.log(Level.WARNING, "El Representante de Proyecto ingresado ya existe");
            } else {
                try {
                    String query = "INSERT INTO responsable_proyecto (nombre_responsable, correo_responsable, telefono_responsable, cargo, id_empresa) VALUES (?, ?, ?, ?, ?);";
                    try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, projectManager.getName());
                        preparedStatement.setString(2, projectManager.getEmailProjectManager());
                        preparedStatement.setString(3, projectManager.getPhoneNumberProjectManager());
                        preparedStatement.setString(4, projectManager.getRol());
                        preparedStatement.setInt(5, projectManager.getEnterpriseId());
                        preparedStatement.executeUpdate();
                        sucess = true;
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error al registrar el responsable",e);
                    if (DAOUtils.isConnectionError(e)) {
                        throw new DataOperationException("Error de conexión. Intente más tarde.");
                    }
                    throw new DataOperationException("Error al registrar el responsable");
                }
            }
        }
        return sucess;
    }

    /**
     * Retrieves a project manager by its identifier.
     *
     * @param idProjectManager the identifier of the project manager to retrieve
     * @return the matching ProjectManager, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public ProjectManager getProjectManagerById(int idProjectManager) throws DataOperationException {
        ProjectManager projectManager =  null;
        String query = "SELECT * FROM responsable_proyecto WHERE id_responsable = ?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, idProjectManager);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("nombre_responsable");
                    String email = resultSet.getString("correo_responsable");
                    String phoneNumber = resultSet.getString("telefono_responsable");
                    String rol = resultSet.getString("cargo");
                    int enterpriseId = resultSet.getInt("id_empresa");
                    projectManager = new ProjectManager(idProjectManager, name, email, phoneNumber, rol, enterpriseId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener el Responsable",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el Responsable");
        }
        return projectManager;
    }

    /**
     * Retrieves all project managers belonging to a given enterprise.
     *
     * @param enterprise the enterprise whose project managers are requested
     * @return a list of project managers for the enterprise, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<ProjectManager> getProjectManagersByEnterprise(Enterprise enterprise) throws DataOperationException {
        List<ProjectManager> projectManagers = new ArrayList<>();
        int enterpriseId = enterprise.getEnterpriseId();
        String query = "SELECT id_responsable, nombre_responsable, correo_responsable, telefono_responsable, cargo, id_empresa FROM responsable_proyecto WHERE id_empresa = ?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, enterpriseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id_responsable");
                    String name = resultSet.getString("nombre_responsable");
                    String email = resultSet.getString("correo_responsable");
                    String phoneNumber = resultSet.getString("telefono_responsable");
                    String rol = resultSet.getString("cargo");
                    projectManagers.add(new ProjectManager(id, name, email, phoneNumber, rol, enterpriseId));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los responsables",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los responsables");
        }
        return projectManagers;
    }
}
