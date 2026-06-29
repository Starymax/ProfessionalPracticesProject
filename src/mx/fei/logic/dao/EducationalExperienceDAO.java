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

/**
 * Data Access Object for EducationalExperience.
 * Provides persistence and retrieval operations on the
 * experiencia_educativa table.
 */
public class EducationalExperienceDAO implements IDAOEducationalExperience {
    private static final Logger LOGGER = Logger.getLogger(EducationalExperienceDAO.class.getName());

    /**
     * Registers a new educational experience.
     *
     * @param educationalExperience the educational experience to register, must not be null and must have a non-blank period
     * @return true if the educational experience was registered successfully
     * @throws IllegalArgumentException if the experience is null or its period is blank
     * @throws IllegalStateException if an experience with the same NRC already exists
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean registerEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException {
        requireEducationalExperience(educationalExperience);
        ensureSectionAvailable(educationalExperience.getNrc(), educationalExperience.getSection());
        String period = requirePeriod(educationalExperience.getPeriod());
        return insertEducationalExperience(educationalExperience, period);
    }

    /**
     * Validates that the given educational experience is present.
     *
     * @param educationalExperience the experience to validate
     * @throws IllegalArgumentException if the experience is null
     */
    @Override
    public void requireEducationalExperience(EducationalExperience educationalExperience) {
        if (educationalExperience == null) {
            LOGGER.log(Level.WARNING, "La experiencia es nula");
            throw new IllegalArgumentException("La experiencia educativa no puede ser nula");
        }
    }

    /**
     * Ensures no educational experience is already registered with the given NRC and section.
     *
     * @param nrc the NRC to check
     * @param section the section to check
     * @throws IllegalStateException if an experience with the same NRC and section already exists
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public void ensureSectionAvailable(String nrc, int section) throws DataOperationException {
        boolean exists = true;
        try {
            getEducationalExperienceByNrcAndSection(nrc, section);
        } catch (NoSuchElementException e) {
            LOGGER.log(Level.INFO, "Seccion disponible para el registro");
            exists = false;
        }
        if (exists) {
            LOGGER.log(Level.WARNING, "Ya existe una experiencia educativa con el nrc " + nrc + " y la seccion " + section);
            throw new IllegalStateException("Ya existe una experiencia educativa con ese nrc y esa seccion");
        }
    }

    /**
     * Retrieves the sections already registered for a given NRC.
     *
     * @param nrc the NRC to check, must not be null or blank
     * @return a list of the registered sections for the NRC, empty if there are none
     * @throws IllegalArgumentException if nrc is null or blank
     * @throws DataOperationException if a database error occurs
     */
    public List<Integer> getUsedSectionsByNrc(String nrc) throws DataOperationException {
        requireNrc(nrc);
        List<Integer> sections = new ArrayList<>();
        String query = "SELECT seccion FROM experiencia_educativa WHERE NRC=? ORDER BY seccion;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nrc);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    sections.add(resultSet.getInt("seccion"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener las secciones del NRC", e);
            throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al obtener las secciones del NRC");
        }
        return sections;
    }

    /**
     * Validates that the given period is present.
     *
     * @param period the period to validate
     * @return the validated period
     * @throws IllegalArgumentException if period is null or blank
     */
    @Override
    public String requirePeriod(String period) {
        if (period == null || period.isBlank()) {
            LOGGER.log(Level.WARNING, "El periodo de la experiencia educativa esta vacio");
            throw new IllegalArgumentException("El periodo de la experiencia educativa no puede estar vacio");
        }
        return period;
    }

    /**
     * Inserts a new educational experience record.
     *
     * @param educationalExperience the experience to insert
     * @param period the validated period
     * @return true if the experience was inserted
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean insertEducationalExperience(EducationalExperience educationalExperience, String period) throws DataOperationException {
        String query = "INSERT INTO experiencia_educativa (NRC, seccion, nombre_experiencia, programa_educativo, id_profesor, periodo, estado_activo) values (?,?,?,?,?,?,?);";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, educationalExperience.getNrc());
            preparedStatement.setInt(2, educationalExperience.getSection());
            preparedStatement.setString(3, educationalExperience.getName());
            preparedStatement.setString(4, educationalExperience.getEducationalProgram());
            setProfessorParameter(preparedStatement, 5, educationalExperience.getProfessor());
            preparedStatement.setString(6, period);
            preparedStatement.setBoolean(7, educationalExperience.isActiveStatus());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar una experiencia educativa", e);
            throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al registrar la experiencia educativa");
        }
    }

    /**
     * Binds a professor's identifier to a statement parameter, or NULL when the professor is absent.
     *
     * @param preparedStatement the statement to bind
     * @param index the parameter index
     * @param professor the professor to bind, may be null
     * @throws SQLException if the parameter cannot be set
     */
    @Override
    public void setProfessorParameter(PreparedStatement preparedStatement, int index, Professor professor) throws SQLException {
        if (professor != null) {
            preparedStatement.setInt(index, professor.getUserId());
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    /**
     * Updates an existing educational experience identified by its NRC.
     *
     * @param educationalExperience the experience carrying the updated data, must not be null and must have a non-blank period
     * @return true if at least one row was updated
     * @throws IllegalArgumentException if the experience is null or its period is blank
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean modifyEducationalExperience(EducationalExperience educationalExperience) throws DataOperationException {
        if (educationalExperience == null) {
            LOGGER.log(Level.WARNING, "La experiencia es nula");
            throw new IllegalArgumentException("La experiencia educativa no puede ser nula");
        }
        String period = educationalExperience.getPeriod();
        if (period == null || period.isBlank()) {
            LOGGER.log(Level.WARNING, "El periodo de la experiencia educativa está vacío");
            throw new IllegalArgumentException("El periodo de la experiencia educativa no puede estar vacío");
        }
        boolean experienceUpdated = false;
        String queryModifyExperience = "UPDATE experiencia_educativa SET nombre_experiencia=?, programa_educativo=?, id_profesor=?, periodo=?, estado_activo=? WHERE nrc=? AND seccion=?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryModifyExperience)) {
            preparedStatement.setString(1, educationalExperience.getName());
            preparedStatement.setString(2, educationalExperience.getEducationalProgram());
            setProfessorParameter(preparedStatement, 3, educationalExperience.getProfessor());
            preparedStatement.setString(4, period);
            preparedStatement.setBoolean(5, educationalExperience.isActiveStatus());
            preparedStatement.setString(6, educationalExperience.getNrc());
            preparedStatement.setInt(7, educationalExperience.getSection());
            experienceUpdated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al modificar una experiencia: " + e.getMessage());
            throw new DataOperationException("Error modificando los datos de una experiencia");
        }
        return experienceUpdated;
    }

    /**
     * Retrieves an educational experience by its NRC and section, resolving its professor when present.
     *
     * @param nrc the NRC to search for, must not be null or blank
     * @param section the section to search for
     * @return the matching EducationalExperience
     * @throws IllegalArgumentException if nrc is null or blank
     * @throws NoSuchElementException if no experience exists with the given NRC and section
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public EducationalExperience getEducationalExperienceByNrcAndSection(String nrc, int section) throws DataOperationException {
        requireNrc(nrc);
        EducationalExperience experience = searchExperienceByNrcAndSection(nrc, section);
        if (experience == null) {
            LOGGER.log(Level.WARNING, "No se encontro la experiencia con el nrc " + nrc + " y la seccion " + section);
            throw new NoSuchElementException("No se encontro la experiencia educativa");
        }
        return experience;
    }

    /**
     * Validates that the given NRC is present.
     *
     * @param nrc the NRC to validate
     * @throws IllegalArgumentException if nrc is null or blank
     */
    private void requireNrc(String nrc) {
        if (nrc == null || nrc.isBlank()) {
            LOGGER.log(Level.WARNING, "El nrc es nulo");
            throw new IllegalArgumentException("El nrc no puede ser nulo");
        }
    }

    /**
     * Searches the educational experience matching the given NRC and section, if any.
     *
     * @param nrc the NRC to search for
     * @param section the section to search for
     * @return the matching EducationalExperience, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public EducationalExperience searchExperienceByNrcAndSection(String nrc, int section) throws DataOperationException {
        EducationalExperience experience = null;
        String query = "SELECT * FROM experiencia_educativa WHERE NRC=? AND seccion=?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nrc);
            preparedStatement.setInt(2, section);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    experience = buildExperienceFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener la experiencia educativa por NRC y seccion", e);
            throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al obtener los datos de la experiencia");
        }
        return experience;
    }

    /**
     * Builds an educational experience from the current row, resolving its professor when present.
     *
     * @param resultSet a result set positioned on a valid row
     * @return the EducationalExperience represented by the current row
     * @throws SQLException if a column cannot be read from the result set
     * @throws DataOperationException if the professor cannot be resolved
     */
    @Override
    public EducationalExperience buildExperienceFromResultSet(ResultSet resultSet) throws SQLException, DataOperationException {
        String nrcEE = resultSet.getString("NRC");
        int section = resultSet.getInt("seccion");
        String name = resultSet.getString("nombre_experiencia");
        String career = resultSet.getString("programa_educativo");
        String period = resultSet.getString("periodo");
        boolean activeStatus = resultSet.getBoolean("estado_activo");
        if (period == null) {
            period = "";
        }
        Professor professor = resolveProfessor(resultSet.getInt("id_profesor"));
        return new EducationalExperience(nrcEE, section, name, career, professor, period, activeStatus);
    }

    /**
     * Resolves the professor with the given identifier, if it refers to an existing professor.
     *
     * @param idProfessor the professor identifier, non-positive when absent
     * @return the matching Professor, or null when no professor is referenced
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public Professor resolveProfessor(int idProfessor) throws DataOperationException {
        Professor professor = null;
        if (idProfessor > 0) {
            professor = new ProfessorDAO().getProfessorById(idProfessor);
        }
        return professor;
    }

    /**
     * Retrieves all educational experiences.
     *
     * @return a list of all educational experiences, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<EducationalExperience> getEducationalExperiences() throws DataOperationException {
        ArrayList<EducationalExperience> educationalExperiences = new ArrayList<>();
        String queryGetEducationalExperiences =
                "SELECT e.NRC, e.seccion, e.nombre_experiencia, e.programa_educativo, e.periodo, e.estado_activo, " +
                "p.id_usuario, p.numero_de_personal, p.nombre, p.apellidos, p.correo, " +
                "p.contrasena, p.estado_activo AS estado_activo_profesor, p.genero, p.es_coordinador, p.es_administrador, p.turno " +
                "FROM experiencia_educativa e LEFT JOIN vw_profesor p ON e.id_profesor = p.id_usuario;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryGetEducationalExperiences);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            ProfessorDAO professorDAO = new ProfessorDAO();
            while (resultSet.next()) {
                String nrc = resultSet.getString("NRC");
                int section = resultSet.getInt("seccion");
                String name = resultSet.getString("nombre_experiencia");
                String educationalProgram = resultSet.getString("programa_educativo");
                String period = resultSet.getString("periodo");
                boolean activeStatus = resultSet.getBoolean("estado_activo");
                if (period == null) {
                    period = "";
                }
                Professor professor = null;
                if (resultSet.getObject("id_usuario") != null) {
                    professor = professorDAO.buildProfessorFromResultSet(resultSet);
                }
                educationalExperiences.add(new EducationalExperience(nrc, section, name, educationalProgram, professor, period, activeStatus));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los datos de las experiencias", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener las experiencias educativas");
        }
        return educationalExperiences;
    }

    /**
     * Retrieves all educational experiences taught by a given professor.
     *
     * @param professorId the professor's identifier
     * @return a list of the professor's educational experiences, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<EducationalExperience> getEducationalExperiencesByProfessor(int professorId) throws DataOperationException {
        ArrayList<EducationalExperience> educationalExperiences = new ArrayList<>();
        Professor professor = new ProfessorDAO().getProfessorById(professorId);
        String queryGetExperiencesByProfessor = "SELECT NRC, seccion, nombre_experiencia, programa_educativo, periodo, estado_activo FROM experiencia_educativa WHERE id_profesor = ?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryGetExperiencesByProfessor)) {
            preparedStatement.setInt(1, professorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String nrc = resultSet.getString("NRC");
                    int section = resultSet.getInt("seccion");
                    String name = resultSet.getString("nombre_experiencia");
                    String educationalProgram = resultSet.getString("programa_educativo");
                    String period = resultSet.getString("periodo");
                    boolean activeStatus = resultSet.getBoolean("estado_activo");
                    if (period == null) {
                        period = "";
                    }
                    educationalExperiences.add(new EducationalExperience(nrc, section, name, educationalProgram, professor, period, activeStatus));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener las experiencias del profesor", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener las experiencias educativas del profesor");
        }
        return educationalExperiences;
    }

    /**
     * Retrieves all active educational experiences.
     *
     * @return a list of all active educational experiences, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<EducationalExperience> getActiveEducationalExperiences() throws DataOperationException {
        List<EducationalExperience> educationalExperiences = new ArrayList<>();
        String query =
                "SELECT e.NRC, e.seccion, e.nombre_experiencia, e.programa_educativo, e.periodo, e.estado_activo, " +
                        "p.id_usuario, p.numero_de_personal, p.nombre, p.apellidos, p.correo, " +
                        "p.contrasena, p.estado_activo AS estado_activo_profesor, p.genero, p.es_coordinador, p.es_administrador, p.turno " +
                        "FROM experiencia_educativa e LEFT JOIN vw_profesor p ON e.id_profesor = p.id_usuario " +
                        "WHERE e.estado_activo = true;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            ProfessorDAO professorDAO = new ProfessorDAO();
            while (resultSet.next()) {
                String nrc = resultSet.getString("NRC");
                int section = resultSet.getInt("seccion");
                String name = resultSet.getString("nombre_experiencia");
                String educationalProgram = resultSet.getString("programa_educativo");
                String period = resultSet.getString("periodo");
                boolean activeStatus = resultSet.getBoolean("estado_activo");
                if (period == null) {
                    period = "";
                }
                Professor professor = null;
                if (resultSet.getObject("id_usuario") != null) {
                    professor = professorDAO.buildProfessorFromResultSet(resultSet);
                }
                educationalExperiences.add(new EducationalExperience(nrc, section, name, educationalProgram, professor, period, activeStatus));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo las experiencias activas: " + e.getMessage());
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener las experiencias educativas activas");
        }
        return educationalExperiences;
    }
}