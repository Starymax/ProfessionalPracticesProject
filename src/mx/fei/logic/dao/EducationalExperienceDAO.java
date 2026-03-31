package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.idao.IDAOEducationalExperience;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EducationalExperienceDAO implements IDAOEducationalExperience {
    private Logger logger = Logger.getLogger(EducationalExperienceDAO.class.getName());

    @Override
    public EducationalExperience getEducationalExperienceByNrc(String nrc) {
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
            logger.log(Level.SEVERE,e.getMessage());
        }
        return experience;
    }
}