package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Period;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOPeriod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeriodDAO implements IDAOPeriod {

    private static final Logger logger = Logger.getLogger(PeriodDAO.class.getName());

    @Override
    public boolean activatePeriod(int year, int number) throws DataOperationException {
        boolean success = false;
        String name = year + "-" + number;
        String queryDeactivate = "UPDATE periodo SET activo = FALSE WHERE activo = TRUE";
        String queryInsertOrActivate = "INSERT INTO periodo (anio, numero, nombre, activo) VALUES (?, ?, ?, TRUE) ON DUPLICATE KEY UPDATE activo = TRUE";
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryDeactivate)) {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryInsertOrActivate)) {
                preparedStatement.setInt(1, year);
                preparedStatement.setInt(2, number);
                preparedStatement.setString(3, name);
                preparedStatement.executeUpdate();
            }
            connection.commit();
            success = true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al activar el periodo", e);
            throw new DataOperationException("Error al activar el periodo.");
        }
        return success;
    }

    @Override
    public Period getActivePeriod() throws DataOperationException {
        String query = "SELECT id_periodo, anio, numero, nombre, activo FROM periodo WHERE activo = TRUE";
        Period period = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                period = new Period(
                        resultSet.getInt("id_periodo"),
                        resultSet.getInt("anio"),
                        resultSet.getInt("numero"),
                        resultSet.getString("nombre"),
                        resultSet.getBoolean("activo")
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el periodo activo", e);
            throw new DataOperationException("Error al obtener el periodo activo.");
        }
        return period;
    }

    @Override
    public List<Period> getAllPeriods() throws DataOperationException {
        String query = "SELECT id_periodo, anio, numero, nombre, activo FROM periodo ORDER BY anio DESC, numero DESC";
        List<Period> periods = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                periods.add(new Period(
                        resultSet.getInt("id_periodo"),
                        resultSet.getInt("anio"),
                        resultSet.getInt("numero"),
                        resultSet.getString("nombre"),
                        resultSet.getBoolean("activo")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener todos los periodos", e);
            throw new DataOperationException("Error al obtener los periodos.");
        }
        return periods;
    }
}