package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOEducationalExperience;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Types;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EducationalExperienceDAO implements IDAOEducationalExperience {
    private static final Logger logger = Logger.getLogger(EducationalExperienceDAO.class.getName());

    @Override
    public boolean registerEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException {
        if (educationalExperience == null) {
            logger.log(Level.WARNING, "La experiencia es nula");
            throw new IllegalArgumentException("La experiencia educativa no puede ser nula");
        }
        try {
            getEducationalExperienceByNrc(educationalExperience.getNrc());
            logger.log(Level.WARNING,"Ya existe una experiencia educativa con el nrc: "+educationalExperience.getNrc());
            throw new IllegalStateException("Ya existe una experiencia educativa con ese nrc");
        } catch (NoSuchElementException e) {
            logger.log(Level.INFO,"Nrc disponible para el registro");
        }
        String period = educationalExperience.getPeriod();
        if (period == null || period.isBlank()) {
            logger.log(Level.WARNING, "El periodo de la experiencia educativa esta vacio");
            throw new IllegalArgumentException("El periodo de la experiencia educativa no puede estar vacio");
        }
        String queryRegisterEE = "INSERT INTO experiencia_educativa (NRC, nombre_experiencia, programa_educativo, id_profesor, periodo) values (?,?,?,?,?);";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterEE)) {
            preparedStatement.setString(1, educationalExperience.getNrc());
            preparedStatement.setString(2, educationalExperience.getName());
            preparedStatement.setString(3, educationalExperience.getEducationalProgram());
            if (educationalExperience.getProfessor() != null) {
                preparedStatement.setInt(4, educationalExperience.getProfessor().getUserId());
            } else {
                preparedStatement.setNull(4, Types.NULL);
            }
            preparedStatement.setString(5, period);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar una experiencia educativa",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al registrar la experiencia educativa");
        }
    }

    @Override
    public boolean modifyEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException {
        if (educationalExperience == null) {
            logger.log(Level.WARNING, "La experiencia es nula");
            throw new IllegalArgumentException("La experiencia educativa no puede ser nula");
        }
        String period = educationalExperience.getPeriod();
        if (period == null || period.isBlank()) {
            logger.log(Level.WARNING, "El periodo de la experiencia educativa está vacío");
            throw new IllegalArgumentException("El periodo de la experiencia educativa no puede estar vacío");
        }
        boolean updated = false;
        String queryModifyExperience = "UPDATE experiencia_educativa SET nombre_experiencia=?, programa_educativo=?, id_profesor=?, periodo=? WHERE nrc=?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryModifyExperience)) {
            preparedStatement.setString(1, educationalExperience.getName());
            preparedStatement.setString(2, educationalExperience.getEducationalProgram());
            if (educationalExperience.getProfessor() != null) {
                preparedStatement.setInt(3, educationalExperience.getProfessor().getUserId());
            } else {
                preparedStatement.setNull(3, Types.NULL);
            }
            preparedStatement.setString(4, period);
            preparedStatement.setString(5, educationalExperience.getNrc());
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al modificar una experiencia: " + e.getMessage());
            throw new DataOperationException("Error modificando los datos de una experiencia");
        }
        return updated;
    }

    @Override
    public EducationalExperience getEducationalExperienceByNrc(String nrc) throws DataOperationException {
        if (nrc == null || nrc.isBlank()) {
            logger.log(Level.WARNING, "El nrc es nulo");
            throw new IllegalArgumentException("El nrc no puede ser nulo");
        }
        EducationalExperience experience = null;
        String queryEEByNrc = "SELECT * FROM experiencia_educativa WHERE NRC=?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryEEByNrc)) {
            preparedStatement.setString(1,nrc);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String nrcEE = resultSet.getString("NRC");
                    String name = resultSet.getString("nombre_experiencia");
                    String career = resultSet.getString("programa_educativo");
                    String period = resultSet.getString("periodo");
                    if (period == null) {
                        period = "";
                    }
                    int idProfessor = resultSet.getInt("id_profesor");
                    Professor professor = null;
                    if (idProfessor > 0) {
                        ProfessorDAO professorDAO = new ProfessorDAO();
                        professor = professorDAO.getProfessorById(idProfessor);
                    }
                    experience = new EducationalExperience(nrcEE, name, career, professor, period);
                }
            }
            if (experience == null) {
                logger.log(Level.WARNING, "No se encontro el experiencia con el nrc: "+ nrc);
                throw new NoSuchElementException("No se encontro la experiencia educativa");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al obtener la experiencia educativa por NRC",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los datos de la experiencia");
        }
        return experience;
    }

    @Override
    public List<EducationalExperience> getEducationalExperiences() throws DataOperationException {
        ArrayList<EducationalExperience> educationalExperiences = new ArrayList<>();
        String queryGetEducationalExperiences = "SELECT nrc FROM experiencia_educativa;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryGetEducationalExperiences)) {
            List<String> nrcs = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    nrcs.add(resultSet.getString("NRC"));
                }
            }
            for (String nrc : nrcs) {
                educationalExperiences.add(getEducationalExperienceByNrc(nrc));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los datos de las experiencias",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw  new DataOperationException("Error al obtener las experiencias educativas");
        }
        return educationalExperiences;
    }

    public List<EducationalExperience> getEducationalExperiencesByProfessor(int professorId) throws DataOperationException {
        ArrayList<EducationalExperience> educationalExperiences = new ArrayList<>();
        String queryGetExperiencesByProfessor = "SELECT NRC FROM experiencia_educativa WHERE id_profesor = ?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryGetExperiencesByProfessor)) {
            preparedStatement.setInt(1, professorId);
            List<String> nrcs = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    nrcs.add(resultSet.getString("NRC"));
                }
            }
            for (String nrc : nrcs) {
                educationalExperiences.add(getEducationalExperienceByNrc(nrc));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener las experiencias del profesor", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener las experiencias educativas del profesor");
        }
        return educationalExperiences;
    }
}