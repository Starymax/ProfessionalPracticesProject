package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Period;
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
    private Logger logger = Logger.getLogger(EducationalExperienceDAO.class.getName());

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
        String queryRegisterEE = "INSERT INTO experiencia_educativa (NRC, nombre_experiencia, programa_educativo, id_profesor, id_periodo) values (?,?,?,?,?);";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterEE)) {
            preparedStatement.setString(1,educationalExperience.getNrc());
            preparedStatement.setString(2,educationalExperience.getName());
            preparedStatement.setString(3,educationalExperience.getEducationalProgram());
            if (educationalExperience.getProfessor() != null) {
                preparedStatement.setInt(4,educationalExperience.getProfessor().getUserId());
            } else {
                preparedStatement.setNull(4, Types.NULL);
            }
            if (educationalExperience.getPeriod() != null) {
                preparedStatement.setInt(5, educationalExperience.getPeriod().getPeriodId());
            } else {
                preparedStatement.setNull(5, Types.NULL);
            }
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar una experiencia educativa",e);
            throw new DataOperationException("Error al registrar la experiencia educativa");
        }
    }

    @Override
    public boolean modifyEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException {
        boolean updated = false;
        String queryModifyExperience = "UPDATE experiencia_educativa set nombre_experiencia=?,programa_educativo=?,id_profesor=?, id_periodo=? where nrc=?;";
        if (educationalExperience != null) {
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(queryModifyExperience)) {
                preparedStatement.setString(1,educationalExperience.getName());
                preparedStatement.setString(2,educationalExperience.getEducationalProgram());
                preparedStatement.setInt(3,educationalExperience.getProfessor().getUserId());
                preparedStatement.setInt(4,educationalExperience.getPeriod().getPeriodId());
                preparedStatement.setString(5, educationalExperience.getNrc());
                updated = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al modificar una experiencia",e);
                throw new DataOperationException("Error modificando los datos de una experiencia");
            }
        } else {
            logger.log(Level.WARNING,"La experiencia es nula");
            throw new IllegalArgumentException("La experiencia educativa  no puede ser nula");
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
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryEEByNrc)) {
            preparedStatement.setString(1,nrc);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String nrcEE = resultSet.getString("NRC");
                String name = resultSet.getString("nombre_experiencia");
                String career = resultSet.getString("programa_educativo");
                int idProfessor = resultSet.getInt("id_profesor");
                int idPeriod = resultSet.getInt("id_periodo");
                resultSet.close();
                Professor professor = null;
                Period period = null;
                if (idProfessor > 0) {
                    ProfessorDAO professorDAO = new ProfessorDAO();
                    professor = professorDAO.getProfessorById(idProfessor);
                }
                if (idPeriod > 0) {
                    PeriodDAO periodDAO = new PeriodDAO();
                    period = periodDAO.getPeriodById(idPeriod);
                }
                experience = new EducationalExperience(nrcEE,name,career,professor,period);
            }
            if (experience == null) {
                logger.log(Level.WARNING, "No se encontro el experiencia con el nrc: "+ nrc);
                throw  new NoSuchElementException("No se encontro la experiencia educativa");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al obtener la experiencia educativa por NRC",e);
            throw new DataOperationException("Error al obtener los datos de la experiencia");
        }
        return experience;
    }

    @Override
    public List<EducationalExperience> getEducationalExperiences() throws DataOperationException {
        ArrayList<EducationalExperience> educationalExperiences = new ArrayList<>();
        String queryGetEducationalExperiences = "SELECT nrc FROM experiencia_educativa;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryGetEducationalExperiences)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> nrcs = new ArrayList<>();
            while (resultSet.next()) {
                nrcs.add(resultSet.getString("NRC"));
            }
            for (String nrc : nrcs) {
                educationalExperiences.add(getEducationalExperienceByNrc(nrc));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los datos de las experiencias",e);
            throw  new DataOperationException("Error al obtener las experiencias educativas");
        }
        return educationalExperiences;
    }
}