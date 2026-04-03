package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOMission implements DAO<Mission> {
    public final String table = "mission";
    public final String fieldID = "id";
    public final String fieldSubject = "subject";
    public final String fieldState = "stateOfMission";
    public final String fieldCommentary = "commentary";
    public final String fieldTimeSlot = "timeSlot";
    public final String fieldJobSkill = "jobSkill";
    public final String fieldAcademicSkill = "academicSkill";

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Mission find(String id) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + fieldID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Mission mission = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, Integer.parseInt(id));
            result = statement.executeQuery();
            if (result.next()) {
                int missionId = result.getInt(fieldID);
                mission = new Mission(
                        missionId,
                        result.getString(fieldSubject),
                        MissionState.valueOf(result.getString(fieldState)),
                        result.getString(fieldCommentary),
                        new DAOTimeSlot().find(String.valueOf(result.getInt(fieldTimeSlot))),
                        getMissionBeneficiaries(missionId),
                        getMissionInterpreters(missionId),
                        getMissionLocation(missionId),
                        new DAOJobSkill().find(String.valueOf(result.getInt(fieldJobSkill))),
                        new DAOAcademicSkill().find(String.valueOf(result.getInt(fieldAcademicSkill)))
                );
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return mission;
    }

    /**
     * Insert a Mission Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if the given id is already used in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void create(Mission objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        Mission alreadyPresent = find(String.valueOf(objectToInsert.getId()));
        if (alreadyPresent != null) {
            if (alreadyPresent.equals(objectToInsert))
                throw new AlreadyExistsException("Object already exists in database");
            else
                throw new DuplicatePrimaryKeyException("Object is already present in database under a different primary key");
        }

        String query = "INSERT INTO %s(%s, %s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?, ?)";
        query = String.format(query, table, fieldSubject, fieldState, fieldCommentary, fieldTimeSlot, fieldJobSkill, fieldAcademicSkill);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToInsert.getSubject());
            statement.setString(2, objectToInsert.getStateOfMission().toString());
            statement.setString(3, objectToInsert.getCommentary());
            statement.setInt(4, objectToInsert.getTimeSlot().getId());
            statement.setInt(5, objectToInsert.getJobSkill().getId());
            statement.setInt(6, objectToInsert.getAcademicSkill().getId());
            statement.executeUpdate();
        }
        finally {
            if (statement != null)
                statement.close();
        }
    }

    /**
     * Update a Mission line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     */
    @Override
    public void update(Mission objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        List<Mission> allLines = findAll();
        if (allLines.contains(objectToUpdate))
            return;
        allLines.forEach((Mission line) -> {
            if (line.getSubject().equals(objectToUpdate.getSubject())
                    && line.getStateOfMission().equals(objectToUpdate.getStateOfMission())
                    && line.getCommentary().equals(objectToUpdate.getCommentary())
                    && line.getTimeSlot().equals(objectToUpdate.getTimeSlot())
                    && line.getBeneficiaries().equals(objectToUpdate.getBeneficiaries())
                    && line.getInterpreters().equals(objectToUpdate.getInterpreters())
                    && line.getLocation().equals(objectToUpdate.getLocation())
                    && line.getJobSkill().equals(objectToUpdate.getJobSkill())
                    && line.getAcademicSkill().equals(objectToUpdate.getAcademicSkill())
                    && line.getId() != objectToUpdate.getId())
                throw new AlreadyExistsException("Object " + objectToUpdate.getSubject() + " already exists at id " + line.getId());
        });
        if (allLines.stream().noneMatch((Mission line) -> line.getId() == objectToUpdate.getId())) {
            throw new NoSuchElementException("Object " + objectToUpdate.getSubject() + " of id " + objectToUpdate.getId() + " could not be found in database");
        }

        String query = "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?";
        query = String.format(query, table, fieldSubject, fieldState, fieldCommentary, fieldTimeSlot, fieldJobSkill, fieldAcademicSkill, fieldID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getSubject());
            statement.setString(2, objectToUpdate.getStateOfMission().toString());
            statement.setString(3, objectToUpdate.getCommentary());
            statement.setInt(4, objectToUpdate.getTimeSlot().getId());
            statement.setInt(5, objectToUpdate.getJobSkill().getId());
            statement.setInt(6, objectToUpdate.getAcademicSkill().getId());
            statement.setInt(7, objectToUpdate.getId());
            statement.executeUpdate();
        }
        finally {
            if (statement != null)
                statement.close();
        }
    }

    /**
     * Delete a line in the Mission table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Mission object in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void delete(Mission objectToDelete) throws NoSuchElementException, SQLException {
        if (find(String.valueOf(objectToDelete.getId())) == null)
            throw new NoSuchElementException("Object " + objectToDelete.getSubject() + " was not found in database");

        String query = "DELETE FROM %s WHERE %s = ?";
        query = String.format(query, table, fieldID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.executeUpdate();
        }
        finally {
            if (statement != null)
                statement.close();
        }
    }

    /**
     * Return all lines of Mission table in the database as Mission Objects in a List
     * @return a List which contains Mission Objects
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Mission> findAll() throws SQLException {
        String query = "SELECT * FROM " + table;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Mission> missions = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                int missionId = result.getInt(fieldID);
                missions.add(new Mission(
                        missionId,
                        result.getString(fieldSubject),
                        MissionState.valueOf(result.getString(fieldState)),
                        result.getString(fieldCommentary),
                        new DAOTimeSlot().find(String.valueOf(result.getInt(fieldTimeSlot))),
                        getMissionBeneficiaries(missionId),
                        getMissionInterpreters(missionId),
                        getMissionLocation(missionId),
                        new DAOJobSkill().find(String.valueOf(result.getInt(fieldJobSkill))),
                        new DAOAcademicSkill().find(String.valueOf(result.getInt(fieldAcademicSkill)))
                ));
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return missions;
    }

    /**
     * @param idUser represent the id of the user which we want the schedule
     * @return a list of Mission which compose the schedule of the idUser
     * @throws SQLException if the database could not be reached
     */
    public List<Mission> getSchedule(String idUser) throws SQLException {
        List<Mission> missions = new ArrayList<>();
        String query = "SELECT mission FROM InterpreterMission WHERE interpreter = ? " +
                       "UNION " +
                       "SELECT mission FROM BeneficiaryMission WHERE beneficiary = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, idUser);
            statement.setString(2, idUser);
            result = statement.executeQuery();
            while (result.next()) {
                Mission mission = find(String.valueOf(result.getInt("mission")));
                if (mission != null)
                    missions.add(mission);
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return missions;
    }

    /**
     * Get the location of a mission via MissionLocation table
     * @param missionId : id of the mission
     * @return Location object or null
     * @throws SQLException if the database could not be reached
     */
    private Location getMissionLocation(int missionId) throws SQLException {
        String query = "SELECT location FROM MissionLocation WHERE mission = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, missionId);
            result = statement.executeQuery();
            if (result.next())
                return new DAOLocation().find(String.valueOf(result.getInt("location")));
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return null;
    }

    /**
     * Get the beneficiaries of a mission via BeneficiaryMission table
     * @param missionId : id of the mission
     * @return List of Beneficiary objects
     * @throws SQLException if the database could not be reached
     */
    private List<Beneficiary> getMissionBeneficiaries(int missionId) throws SQLException {
        String query = "SELECT beneficiary FROM BeneficiaryMission WHERE mission = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Beneficiary> beneficiaries = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, missionId);
            result = statement.executeQuery();
            while (result.next())
                beneficiaries.add(new DAOBeneficiary().find(result.getString("beneficiary")));
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return beneficiaries;
    }

    /**
     * Get the interpreters of a mission via InterpreterMission table
     * @param missionId : id of the mission
     * @return List of Interpreter objects
     * @throws SQLException if the database could not be reached
     */
    private List<Interpreter> getMissionInterpreters(int missionId) throws SQLException {
        String query = "SELECT interpreter FROM InterpreterMission WHERE mission = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Interpreter> interpreters = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, missionId);
            result = statement.executeQuery();
            while (result.next())
                interpreters.add(new DAOInterpreter().find(result.getString("interpreter")));
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return interpreters;
    }
}