package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

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
     * Search for a Mission in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Mission find(int id) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + fieldID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Mission mission = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                mission = new Mission(
                        id,
                        result.getString(fieldSubject),
                        MissionState.valueOf(result.getString(fieldState)),
                        result.getString(fieldCommentary),
                        new DAOTimeSlot().find(result.getInt(fieldTimeSlot)),
                        new DAOBeneficiary().getMissionBeneficiaries(id),
                        new DAOInterpreter().getMissionInterpreters(id),
                        new DAOLocation().getMissionLocation(id),
                        new DAOJobSkill().find(result.getInt(fieldJobSkill)),
                        new DAOAcademicSkill().find(result.getInt(fieldAcademicSkill)),
                        new DAOLocation().getMissionRoom(id)
                );
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return mission;
    }

    /**
     * Insert a Mission object in the database
     * @param objectToInsert an object of type Mission to add to the database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Mission objectToInsert) throws AlreadyExistsException, SQLException {
        List<Mission> missions = findAll();
        for (Mission line : missions) {
            if (line.getSubject().equals(objectToInsert.getSubject())
                    && line.getStateOfMission().equals(objectToInsert.getStateOfMission())
                    && line.getCommentary().equals(objectToInsert.getCommentary())
                    && line.getTimeSlot().equals(objectToInsert.getTimeSlot())
                    && line.getJobSkill().equals(objectToInsert.getJobSkill())
                    && line.getAcademicSkill().equals(objectToInsert.getAcademicSkill()))
                throw new AlreadyExistsException("Mission " + objectToInsert.getSubject() + " already exists at id " + line.getId());
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
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Update a Mission line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Mission objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        List<Mission> missions = findAll();
        for (Mission line : missions) {
            if (line.getSubject().equals(objectToUpdate.getSubject())
                    && line.getStateOfMission().equals(objectToUpdate.getStateOfMission())
                    && line.getCommentary().equals(objectToUpdate.getCommentary())
                    && line.getTimeSlot().equals(objectToUpdate.getTimeSlot())
                    && line.getJobSkill().equals(objectToUpdate.getJobSkill())
                    && line.getAcademicSkill().equals(objectToUpdate.getAcademicSkill()))
                throw new AlreadyExistsException("Mission " + objectToUpdate.getSubject() + " already exists at id " + line.getId());
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
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("Mission " + objectToUpdate.getSubject() + " of id " + objectToUpdate.getId() + " could not be found in database");
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Delete a Mission line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Mission objectToDelete) throws NoSuchElementException, SQLException {
        String query = "DELETE FROM %s WHERE %s = ?";
        query = String.format(query, table, fieldID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("Mission " + objectToDelete.getSubject() + " was not found in database");
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Return all line of Mission table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
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
                        new DAOTimeSlot().find(result.getInt(fieldTimeSlot)),
                        new DAOBeneficiary().getMissionBeneficiaries(missionId),
                        new DAOInterpreter().getMissionInterpreters(missionId),
                        new DAOLocation().getMissionLocation(missionId),
                        new DAOJobSkill().find(result.getInt(fieldJobSkill)),
                        new DAOAcademicSkill().find(result.getInt(fieldAcademicSkill)),
                        new DAOLocation().getMissionRoom(missionId)
                ));
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return missions;
    }

    /**
     * Return the schedule of the user with the given id for a specific week
     * @param idUser represent the id of the user which we want the schedule
     * @param week represent the week number (0-6)
     * @return a list of Mission which compose the schedule of the idUser for the given week, or an empty List if none was found
     * @throws SQLException if the database could not be reached
     */
    public List<Mission> getScheduleForWeek(int idUser, int week) throws SQLException {
        List<Mission> missions = new ArrayList<>();
        String query = "SELECT m.id FROM " + table + " m " +
                "JOIN TimeSlot ts ON m." + fieldTimeSlot + " = ts.id " +
                "WHERE ts.day IS NOT NULL " +
                "AND (m.id IN (SELECT mission FROM InterpreterMission WHERE interpreter = ?) " +
                "OR m.id IN (SELECT mission FROM BeneficiaryMission WHERE beneficiary = ?))";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idUser);
            statement.setInt(2, idUser);
            result = statement.executeQuery();
            while (result.next()) {
                Mission mission = find(result.getInt("id"));
                if (mission != null)
                    missions.add(mission);
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return missions;
    }

    /**
     * Return the schedule of the user with the given id for a specific day
     * @param idUser represent the id of the user which we want the schedule
     * @param day represent the day number (0-6)
     * @return a list of Mission which compose the schedule of the idUser for the given day, or an empty List if none was found
     * @throws SQLException if the database could not be reached
     */
    public List<Mission> getScheduleForDay(int idUser, int day) throws SQLException {
        List<Mission> missions = new ArrayList<>();
        String query = "SELECT m.id FROM " + table + " m " +
                "JOIN TimeSlot ts ON m." + fieldTimeSlot + " = ts.id " +
                "WHERE ts.day = ? " +
                "AND (m.id IN (SELECT mission FROM InterpreterMission WHERE interpreter = ?) " +
                "OR m.id IN (SELECT mission FROM BeneficiaryMission WHERE beneficiary = ?))";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, day);
            statement.setInt(2, idUser);
            statement.setInt(3, idUser);
            result = statement.executeQuery();
            while (result.next()) {
                Mission mission = find(result.getInt("id"));
                if (mission != null)
                    missions.add(mission);
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return missions;
    }

    /**
     * Return the importance of a Mission for a Beneficiary
     * @param missionId : id of the mission
     * @param beneficiaryId : id of the beneficiary
     * @throws NoSuchElementException if the given idMission or idBeneficiary doesn't correspond to an existent id
     * @throws SQLException if the database could not be reached
     * @return the importance of the Mission
     */
    public int getImportanceForBeneficiary(int missionId, int beneficiaryId) throws NoSuchElementException, SQLException {
        String query = "SELECT importance FROM BeneficiaryMission WHERE mission = ? AND beneficiary = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        int importance = 0;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, missionId);
            statement.setInt(2, beneficiaryId);
            result = statement.executeQuery();

            if (result.next())
                importance = result.getInt("importance");
            else
                throw new NoSuchElementException("No mission " + missionId + " found for beneficiary " + beneficiaryId);
        }finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return importance;
    }

    /**
     * Add a beneficiary to a mission in the BeneficiaryMission table
     * @param missionId : id of the mission
     * @param beneficiaryId : id of the beneficiary
     * @param importance : importance of the beneficiary in the mission
     * @throws AlreadyExistsException if the beneficiary is already linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the beneficiary is linked to the mission in the database
     */
    public void addBeneficiaryToMission(int missionId, int beneficiaryId, int importance) throws SQLException, AlreadyExistsException {
        String checkQuery = "SELECT * FROM BeneficiaryMission WHERE mission = ? AND beneficiary = ?";
        String insertQuery = "INSERT INTO BeneficiaryMission(mission, beneficiary, importance) VALUES(?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(checkQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, beneficiaryId);
            result = statement.executeQuery();
            if (result.next()) throw new AlreadyExistsException("This beneficiary is already linked to the mission");

            statement = DatabaseConnector.getInstance().prepareStatement(insertQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, beneficiaryId);
            statement.setInt(3, importance);
            statement.executeUpdate();
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Add an interpreter to a mission in the InterpreterMission table
     * @param missionId : id of the mission
     * @param interpreterId : id of the interpreter
     * @throws AlreadyExistsException if the interpreter is already linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the interpreter is linked to the mission in the database
     */
    public void addInterpreterToMission(int missionId, int interpreterId) throws SQLException, AlreadyExistsException {
        String checkQuery = "SELECT * FROM InterpreterMission WHERE mission = ? AND interpreter = ?";
        String insertQuery = "INSERT INTO InterpreterMission(mission, interpreter) VALUES(?, ?)";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(checkQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            result = statement.executeQuery();
            if (result.next()) throw new AlreadyExistsException("This interpreter is already linked to the mission");

            statement = DatabaseConnector.getInstance().prepareStatement(insertQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            statement.executeUpdate();
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Remove a beneficiary from a mission in the BeneficiaryMission table
     * @param missionId : id of the mission
     * @param beneficiaryId : id of the beneficiary
     * @throws NoSuchElementException if the beneficiary is not linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the beneficiary is no longer linked to the mission in the database
     */
    public void removeBeneficiaryFromMission(int missionId, int beneficiaryId) throws SQLException, NoSuchElementException {
        String checkQuery = "SELECT * FROM BeneficiaryMission WHERE mission = ? AND beneficiary = ?";
        String deleteQuery = "DELETE FROM BeneficiaryMission WHERE mission = ? AND beneficiary = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(checkQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, beneficiaryId);
            result = statement.executeQuery();
            if (!result.next()) throw new NoSuchElementException("This beneficiary is not linked to the mission");

            statement = DatabaseConnector.getInstance().prepareStatement(deleteQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, beneficiaryId);
            statement.executeUpdate();
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Remove an interpreter from a mission in the InterpreterMission table
     * @param missionId : id of the mission
     * @param interpreterId : id of the interpreter
     * @throws NoSuchElementException if the interpreter is not linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the interpreter is no longer linked to the mission in the database
     */
    public void removeInterpreterFromMission(int missionId, int interpreterId) throws SQLException, NoSuchElementException {
        String checkQuery = "SELECT * FROM InterpreterMission WHERE mission = ? AND interpreter = ?";
        String deleteQuery = "DELETE FROM InterpreterMission WHERE mission = ? AND interpreter = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(checkQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            result = statement.executeQuery();
            if (!result.next()) throw new NoSuchElementException("This interpreter is not linked to the mission");

            statement = DatabaseConnector.getInstance().prepareStatement(deleteQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            statement.executeUpdate();
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}