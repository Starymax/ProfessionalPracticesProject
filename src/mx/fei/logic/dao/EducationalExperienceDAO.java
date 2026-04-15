package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataBaseConnectionException;
import mx.fei.logic.idao.IDAOEducationalExperience;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EducationalExperienceDAO implements IDAOEducationalExperience {
    private Logger logger = Logger.getLogger(EducationalExperienceDAO.class.getName());

    @Override
    public boolean registerEducationalExperience(EducationalExperience educationalExperience) throws DataBaseConnectionException {
        boolean registered = false;
        String queryRegisterEE = "INSERT INTO experiencia_educativa (NRC, nombre_experiencia, programa_educativo, periodo_escolar) values (?,?,?,?);";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterEE)) {
            preparedStatement.setString(1,educationalExperience.getNrc());
            preparedStatement.setString(2,educationalExperience.getName());
            preparedStatement.setString(3,educationalExperience.getEducationalProgram());
            preparedStatement.setString(4,educationalExperience.getEscolarPeriod());
            registered = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar una experiencia educativa");
            throw new DataBaseConnectionException("Error al registrar la experiencia educativa");
        }
        return registered;
    }

    @Override
    public EducationalExperience getEducationalExperienceByNrc(String nrc) throws DataBaseConnectionException {
        EducationalExperience experience = null;
        String queryEEByNrc = "SELECT * FROM experiencia_educativa WHERE NRC=?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryEEByNrc);) {
            preparedStatement.setString(1,nrc);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String nrcEE = resultSet.getString("NRC");
                String name = resultSet.getString("nombre_experiencia");
                String career = resultSet.getString("programa_educativo");
                String period = resultSet.getString("periodo_escolar");
                int idProfessor = resultSet.getInt("id_profesor");
                ProfessorDAO professorDAO = new ProfessorDAO();
                Professor professor = professorDAO.getProfessorByPersonalNumber(idProfessor);
                experience = new EducationalExperience(nrcEE,name,career,period,professor);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al obtener la experiencia educativa por NRC");
            throw new DataBaseConnectionException("Error al obtener los datos de la experiencia");
        }
        return experience;
    }

    @Override
    public List<EducationalExperience> getEducationalExperiences() throws DataBaseConnectionException {
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
            logger.log(Level.SEVERE, "Error al obtener los datos de las experiencias");
            throw  new DataBaseConnectionException("Error al obtener las experiencias educativas");
        }
        return educationalExperiences;
    }
}