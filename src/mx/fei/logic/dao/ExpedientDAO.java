package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.idao.IDAOExpedient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpedientDAO implements IDAOExpedient {
    private Logger logger = Logger.getLogger(ExpedientDAO.class.getName());
    @Override
    public boolean loadDocument(String enrollment, String documentType, boolean loadState) {
        boolean loaded = false;
        String queryLoad = "UPDATE expediente_practicas set" + documentType + "= ? where enrollment=?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryLoad)) {
            preparedStatement.setBoolean(1,loadState);
            preparedStatement.setString(2,enrollment);
            loaded = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
        }
        return loaded;
    }

    @Override
    public boolean isLoaded(String enrollment, String documentType) {
        boolean isLoaded = false;
        String queryIsLoaded = "SELECT " + documentType + " FROM expediente_practicas WHERE matricula = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryIsLoaded)) {
            preparedStatement.setString(1,enrollment);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isLoaded = resultSet.getBoolean(documentType);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return isLoaded;
    }
}
