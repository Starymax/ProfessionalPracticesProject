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
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeriodDAO implements IDAOPeriod {

    private static final Logger logger = Logger.getLogger(PeriodDAO.class.getName());

    @Override
    public boolean createPeriodIfNotExists(int year, int number) throws DataOperationException {
        boolean inserted = false;
        String name = year + "-" + number;
        String query = "INSERT INTO periodo (anio, numero, nombre, activo) VALUES (?, ?, ?, FALSE) ON DUPLICATE KEY UPDATE id_periodo = id_periodo";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, year);
            preparedStatement.setInt(2, number);
            preparedStatement.setString(3, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al asignar el periodo", e);
            throw new DataOperationException("Error al asignar el periodo");
        }
        return inserted;
    }

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
                int idPeriod = resultSet.getInt("id_periodo");
                int year = resultSet.getInt("anio");
                int periodNumber = resultSet.getInt("numero");
                String periodName = resultSet.getString("nombre");
                boolean activeState = resultSet.getBoolean("activo");
                period = new Period(idPeriod, year, periodNumber, periodName, activeState);
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
                int idPeriod = resultSet.getInt("id_periodo");
                int year = resultSet.getInt("anio");
                int periodNumber = resultSet.getInt("numero");
                String periodName = resultSet.getString("nombre");
                boolean activeState = resultSet.getBoolean("activo");
                periods.add(new Period(idPeriod, year, periodNumber, periodName, activeState));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener todos los periodos", e);
            throw new DataOperationException("Error al obtener los periodos.");
        }
        return periods;
    }

    public Period getPeriodById(int periodId) throws DataOperationException {
        if (periodId <= 0) {
            logger.log(Level.WARNING, "El id de periodo es invalido");
            throw new IllegalArgumentException("El id de periodo no puede ser menor o igual a 0");
        }
        Period period = null;
        String query = "SELECT * FROM periodo WHERE id_periodo = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, periodId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id_periodo");
                int year = resultSet.getInt("anio");
                int number = resultSet.getInt("numero");
                String name = resultSet.getString("nombre");
                boolean active = resultSet.getBoolean("activo");
                period = new Period(id, year, number, name, active);
            }
            if (period == null) {
                logger.log(Level.WARNING, "No se encontro el periodo con id: " + periodId);
                throw new NoSuchElementException("No se encontro el periodo");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el periodo por id", e);
            throw new DataOperationException("Error al obtener el periodo");
        }
        return period;
    }

    public Period getPeriodByYearAndNumber(int year, int number) throws DataOperationException {
        String query = "SELECT * FROM periodo WHERE anio = ? AND numero = ?";
        Period period = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, year);
            preparedStatement.setInt(2, number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idPeriod = resultSet.getInt("id_periodo");
                String periodName = resultSet.getString("nombre");
                boolean activeState = resultSet.getBoolean("activo");
                period = new Period(idPeriod, year, number, periodName, activeState);
            }
            if (period == null) {
                throw new NoSuchElementException("No se encontró el periodo");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar periodo", e);
            throw new DataOperationException("Error al obtener el periodo");
        }
        return period;
    }
}