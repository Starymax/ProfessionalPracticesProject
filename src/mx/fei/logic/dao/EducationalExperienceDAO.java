/*package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.idao.IDAOEducationalExperience;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EducationalExperienceDAO implements IDAOEducationalExperience {
    private Logger logger = Logger.getLogger(EducationalExperienceDAO.class.getName());

    @Override
    public EducationalExperience getEEByNrc(String nrc) {
        EducationalExperience experience = null;
        try {
            DatabaseConnectionManager connection = DatabaseConnectionManager.buildConnection();
            String queryEEByNrc = "SELECT * FROM experiencia_educativa WHERE NRC=?;";
            PreparedStatement preparedStatement = connection.preparedStatement(queryEEByNrc);
            preparedStatement.setString(1,nrc);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String nrcEE = resultSet.getString("NRC");
                String name = resultSet.getString("nombre_experiencia");
                String career = resultSet.getString("programa_educativo");
                String period = resultSet.getString("periodo_escolar");
                int idProfessor = resultSet.getInt("id_profesor");
                PrfessorDAO professorDAO = new ProfessorDAO();
                Professor professor = professorDAO.getProfessorByPersonalNumber(int id_professor);

                experience = new EducationalExperience(nrcEE,name,career,period,professor);
            }
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return experience;
    }
}*/
