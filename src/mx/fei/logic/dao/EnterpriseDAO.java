package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOEnterprise;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Enterprise (linked organizations).
 * Provides persistence and retrieval operations on the
 * organizacion_vinculada table.
 */
public class EnterpriseDAO implements IDAOEnterprise {
    private static final Logger LOGGER = Logger.getLogger(EnterpriseDAO.class.getName());

    /**
     * Retrieves an enterprise by its identifier.
     *
     * @param idEnterprise the identifier of the enterprise to retrieve
     * @return the matching Enterprise
     * @throws NoSuchElementException if no enterprise exists with the given id
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public Enterprise getEnterpriseById(int idEnterprise) throws DataOperationException {
        Enterprise enterprise = null;
        String query = "SELECT * FROM organizacion_vinculada WHERE id_empresa = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, idEnterprise);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("nombre_empresa");
                    String sector = resultSet.getString("sector");
                    String phone = resultSet.getString("telefono");
                    String mail = resultSet.getString("correo");
                    String city = resultSet.getString("ciudad");
                    long directUsers = resultSet.getLong("usuarios_directos");
                    long indirectUsers = resultSet.getLong("usuarios_indirectos");
                    boolean activeStatus = resultSet.getBoolean("estado_activo");
                    String country = resultSet.getString("pais");
                    enterprise = new Enterprise(idEnterprise, name, sector, phone, mail, city, directUsers, indirectUsers, activeStatus, country);
                }
            }
            if (enterprise == null) {
                LOGGER.log(Level.WARNING, "No se encontro a la empresa con el id: " + idEnterprise);
                throw new NoSuchElementException("No se encontro a la empresa");
            }
            return enterprise;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo la organizacion vinculada",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener la organizacion vinculada");
        }
    }

    /**
     * Registers a new enterprise.
     *
     * @param enterprise the enterprise to register, must not be null
     * @return the generated identifier of the new enterprise, or -1 if none was generated
     * @throws IllegalArgumentException if enterprise is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public int registerEnterprise(Enterprise enterprise) throws DataOperationException {
        if (enterprise == null) {
            LOGGER.log(Level.WARNING,"La empresa es nula");
            throw new IllegalArgumentException("La empresa no puede ser nula");
        }
        int generatedId = -1;
        String query = "INSERT INTO organizacion_vinculada (nombre_empresa, sector, telefono, correo, ciudad, usuarios_directos, usuarios_indirectos, estado_activo, pais) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1,enterprise.getName());
            preparedStatement.setString(2,enterprise.getSector());
            preparedStatement.setString(3,enterprise.getPhoneNumber());
            preparedStatement.setString(4,enterprise.getContactEmail());
            preparedStatement.setString(5,enterprise.getCity());
            preparedStatement.setLong(6,enterprise.getDirectUsers());
            preparedStatement.setLong(7,enterprise.getIndirectUsers());
            preparedStatement.setBoolean(8,enterprise.isActiveStatus());
            preparedStatement.setString(9,enterprise.getCountry());
            preparedStatement.executeUpdate();
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    generatedId = keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registrando la organizacion vinculada",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al registrar la organizacion vinculada");
        }
        return generatedId;
    }

    /**
     * Builds an Enterprise from the current row of the given result set.
     *
     * @param resultSet a result set positioned on a valid row
     * @return the Enterprise represented by the current row
     * @throws SQLException if a column cannot be read from the result set
     */
    private Enterprise buildEnterpriseFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id_empresa");
        String name = resultSet.getString("nombre_empresa");
        String sector = resultSet.getString("sector");
        String phone = resultSet.getString("telefono");
        String mail = resultSet.getString("correo");
        String city = resultSet.getString("ciudad");
        long directUsers = resultSet.getLong("usuarios_directos");
        long indirectUsers = resultSet.getLong("usuarios_indirectos");
        boolean activeStatus = resultSet.getBoolean("estado_activo");
        String country = resultSet.getString("pais");
        return new Enterprise(id, name, sector, phone, mail, city, directUsers, indirectUsers, activeStatus, country);
    }

    /**
     * Retrieves all enterprises whose status is active.
     *
     * @return a list of active enterprises, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Enterprise> getActiveEnterprises() throws DataOperationException {
        List<Enterprise> enterprises = new ArrayList<>();
        String query = "SELECT * FROM organizacion_vinculada WHERE estado_activo = true;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                enterprises.add(buildEnterpriseFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo las organizaciones vinculadas activas", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener todas las organizaciones activas");
        }
        return enterprises;
    }

    /**
     * Retrieves all enterprises regardless of their status.
     *
     * @return a list of all enterprises, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    public List<Enterprise> getEnterprises() throws DataOperationException {
        List<Enterprise> enterprises = new ArrayList<>();
        String query = "SELECT * FROM organizacion_vinculada;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                enterprises.add(buildEnterpriseFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo las organizaciones activas", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error obteniendo todas las organizaciones");
        }
        return enterprises;
    }

    /**
     * Updates the data of an existing enterprise.
     *
     * @param enterprise the enterprise carrying the updated data, must not be null
     * @return true if at least one row was updated
     * @throws IllegalArgumentException if enterprise is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean modifyEnterprise(Enterprise enterprise) throws DataOperationException {
        if (enterprise == null) {
            LOGGER.log(Level.WARNING, "La empresa es nula");
            throw new IllegalArgumentException("La empresa no puede ser nula");
        }
        boolean enterpriseUpdated = false;
        String query = "UPDATE organizacion_vinculada SET nombre_empresa=?, sector=?, telefono=?, correo=?, ciudad=?, usuarios_directos=?, usuarios_indirectos=?, estado_activo=?, pais=? WHERE id_empresa=?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enterprise.getName());
            preparedStatement.setString(2, enterprise.getSector());
            preparedStatement.setString(3, enterprise.getPhoneNumber());
            preparedStatement.setString(4, enterprise.getContactEmail());
            preparedStatement.setString(5, enterprise.getCity());
            preparedStatement.setLong(6, enterprise.getDirectUsers());
            preparedStatement.setLong(7, enterprise.getIndirectUsers());
            preparedStatement.setBoolean(8, enterprise.isActiveStatus());
            preparedStatement.setString(9, enterprise.getCountry());
            preparedStatement.setInt(10, enterprise.getEnterpriseId());
            enterpriseUpdated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al modificar la organizacion vinculada", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al modificar la organizacion vinculada");
        }
        return enterpriseUpdated;
    }
}
