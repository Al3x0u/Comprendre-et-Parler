package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Mission;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.*;

import javax.sound.midi.MidiChannel;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOMission implements DAO<Mission> {

    private static void getMissionList(ResultSet rs, List<Mission> list)throws SQLException{
        while(rs.next()) {
            int missionId = rs.getInt("id");
            int jobSkillId = rs.getInt("jobSkill");
            JobSkill jobSkill = rs.wasNull() ? null : DAOJobSkill.findById(jobSkillId);
            int academicSkillId = rs.getInt("academicSkill");
            AcademicSkill academicSkill = rs.wasNull() ? null : DAOAcademicSkill.findById(academicSkillId);

            list.add(new Mission(
                    rs.getInt("id"),
                    rs.getString("subject"),
                    MissionState.toMissionState(rs.getString("status")),
                    rs.getString("commentary"),
                    DAOImportance.findByMissionId(missionId),
                    DAOBeneficiary.findByIdBeneficiariesMission(missionId),
                    DAOInterpreter.findAllByMissionId(missionId),
                    DAOPunctualTimeSlot.findById(rs.getInt("timeSlot")),
                    DAOLocation.findById(rs.getInt("location")),
                    jobSkill,
                    academicSkill
            ));
        }
    }

    /**
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Mission objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String queryMission = "INSERT INTO Mission (subject, stateOfMission, commentary, timeSlot, jobSkill, academicSkill) VALUES(?, ?, ?, ?, ?, ?)";
        String queryMissionLoc = "INSERT INTO MissionLocation(mission, location, room) VALUES (?, ?, ?)";
        String queryBeneficaryMission = "INSERT INTO BeneficiaryMission (mission, beneficiary, importance) VALUES (?, ?, ?)";
        String queryInterpreterMission = "INSERT INTO InterpreterMission (mission, interpreter) VALUES (?, ?)";

        int rowsAffectedMission = 0;
        int rowsAffectedMissionLoc = 0;
        int rowsAffectedBenefMission = 0;
        int rowsAffectedInterpreterMission = 0;

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            DAOPunctualTimeSlot daoPunctualTimeSlot = new DAOPunctualTimeSlot();
            daoPunctualTimeSlot.create(objectToInsert.getPunctualTime());

            int timeSlotId;
            stmt = connection.prepareStatement("SELECT MAX(id) FROM TimeSlot");
            rs = stmt.executeQuery();
            rs.next();
            timeSlotId = rs.getInt(1);

            stmt = connection.prepareStatement(queryMission);
            stmt.setString(1, objectToInsert.getSubjet());
            stmt.setString(2, objectToInsert.getStateOfMission().toString());
            stmt.setString(3, objectToInsert.getCommentary());
            stmt.setInt(4, timeSlotId);
            if (objectToInsert.getJobSkill() != null) {
                stmt.setInt(5, objectToInsert.getJobSkill().getId());
            }else {
                stmt.setNull(5, Types.INTEGER);
            }
            if (objectToInsert.getAcademicSkill() != null){
                stmt.setInt(6, objectToInsert.getAcademicSkill().getId());
            }else {
                stmt.setNull(6, Types.INTEGER);
            }
            rowsAffectedMission = stmt.executeUpdate();
            if(rowsAffectedMission < 1 ){
                throw new NoSuchElementException();
            }

            stmt = connection.prepareStatement("SELECT MAX(id) FROM Mission");
            rs = stmt.executeQuery();
            rs.next();
            int missionId = rs.getInt(1);

            stmt = connection.prepareStatement(queryBeneficaryMission);
            for (Importance importance : objectToInsert.getImportance()){
                stmt.setInt(1,missionId);
                stmt.setString(2, importance.getBeneficiary().getLogin());
                stmt.setInt(3, importance.getImportance());
                rowsAffectedBenefMission += stmt.executeUpdate();
            }
            if(rowsAffectedBenefMission < 1 ){
                throw new NoSuchElementException();
            }

            stmt = connection.prepareStatement(queryInterpreterMission);
            for(Interpreter interpreter : objectToInsert.getInterpreters()){
                stmt.setInt(1, missionId);
                stmt.setString(2, interpreter.getLogin());
                rowsAffectedInterpreterMission += stmt.executeUpdate();
            }
            if(rowsAffectedInterpreterMission < 1 ){
                throw new NoSuchElementException();
            }

            stmt = connection.prepareStatement(queryMissionLoc);
            stmt.setInt(1, missionId);
            stmt.setInt(2, objectToInsert.getLocation().getId());
            if(objectToInsert.getLocation().getRoom() != null){
                stmt.setString(3, objectToInsert.getLocation().getRoom());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            rowsAffectedMissionLoc = stmt.executeUpdate();
            if(rowsAffectedMissionLoc < 1 ){
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
    }

    /**
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Mission objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String queryMission = "UPDATE Mission SET subject = ?, stateOfMission = ?, commentary = ?, jobSkill = ?, academicSkill = ? WHERE id = ?";
        int rowsAffected = 0;
        String queryDeleteBeneficiary = "DELETE FROM BeneficiaryMission WHERE mission = ?";
        //can be null
        String queryDeleteInterpreter = "DELETE FROM InterpreterMission WHERE mission = ?";
        //can be null
        String queryBeneficaryMission = "INSERT INTO BeneficiaryMission (mission, beneficiary, importance) VALUES (?, ?, ?)";
        String queryInterpreterMission = "INSERT INTO InterpreterMission (mission, interpreter) VALUES (?, ?)";
        String queryDeleteMissionLocation = "DELETE FROM MissionLocation WHERE mission = ?";
        int rowsAffectedDelMissionLocation = 0;
        String queryMissionLoc = "INSERT INTO MissionLocation(mission, location, room) VALUES (?, ?, ?)";
        int rowsAffectedInsertMissionLocation = 0;
        try{
            DAOPunctualTimeSlot daoPunctualTimeSlot = new DAOPunctualTimeSlot();
            daoPunctualTimeSlot.update(objectToUpdate.getPunctualTime());

            stmt = connection.prepareStatement(queryMission);
            stmt.setString(1, objectToUpdate.getSubjet());
            stmt.setString(2, objectToUpdate.getStateOfMission().toString());
            stmt.setString(3, objectToUpdate.getCommentary());
            if(objectToUpdate.getJobSkill() != null) {
                stmt.setInt(4, objectToUpdate.getJobSkill().getId());
            }else{
                stmt.setNull(4, Types.INTEGER);
            }
            if(objectToUpdate.getAcademicSkill() != null) {
                stmt.setInt(5, objectToUpdate.getAcademicSkill().getId());
            }else{
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, objectToUpdate.getId());
            rowsAffected = stmt.executeUpdate();
            if(rowsAffected < 1) {
                throw new NoSuchElementException();
            }

            stmt = connection.prepareStatement(queryDeleteBeneficiary);
            stmt.setInt(1, objectToUpdate.getId());
            stmt.executeUpdate();

            stmt = connection.prepareStatement(queryDeleteInterpreter);
            stmt.setInt(1, objectToUpdate.getId());
            stmt.executeUpdate();

            stmt = connection.prepareStatement(queryBeneficaryMission);
            for (Importance importance : objectToUpdate.getImportance()){
                stmt.setInt(1,objectToUpdate.getId());
                stmt.setString(2, importance.getBeneficiary().getLogin());
                stmt.setInt(3, importance.getImportance());
                stmt.executeUpdate();
            }


            stmt = connection.prepareStatement(queryInterpreterMission);
            for(Interpreter interpreter : objectToUpdate.getInterpreters()){
                stmt.setInt(1, objectToUpdate.getId());
                stmt.setString(2, interpreter.getLogin());
                stmt.executeUpdate();
            }

            stmt = connection.prepareStatement(queryDeleteMissionLocation);
            stmt.setInt(1, objectToUpdate.getId());
            rowsAffectedDelMissionLocation = stmt.executeUpdate();
            if(rowsAffectedDelMissionLocation < 1 ){
                throw new NoSuchElementException();
            }

            stmt = connection.prepareStatement(queryMissionLoc);
            stmt.setInt(1, objectToUpdate.getId());
            stmt.setInt(2, objectToUpdate.getLocation().getId());
            if(objectToUpdate.getLocation().getRoom() != null){
                stmt.setString(3, objectToUpdate.getLocation().getRoom());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            rowsAffectedInsertMissionLocation = stmt.executeUpdate();
            if(rowsAffectedInsertMissionLocation < 1 ){
                throw new NoSuchElementException();
            }

        }finally {
            DatabaseConnector.closeStmt(null, stmt);
        }
    }

    /**
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Mission objectToDelete) throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        PreparedStatement stmt = null;
        int rowsAffected = 0;

        String query = "DELETE FROM Mission WHERE id = ?";

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, objectToDelete.getId());
            rowsAffected = stmt.executeUpdate();

            if (rowsAffected < 1){
                throw new NoSuchElementException();
            }

            DAOPunctualTimeSlot daoPunctualTimeSlot = new DAOPunctualTimeSlot();
            daoPunctualTimeSlot.delete(objectToDelete.getPunctualTime());
        }finally {
            DatabaseConnector.closeStmt(null, stmt);
        }
    }

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Mission> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Mission> list = new ArrayList<>();
        String query = "SELECT m.*, bm.*, ml.location FROM Mission m JOIN BeneficiaryMission ON m.id = bm.mission JOIN MissionLocation ml ON m.id = ml.mission";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            getMissionList(rs, list);
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }

        return list;
    }

    /**
     * @param login represent the id of the user which we want the schedule
     * @return a list of Mission which compose the schedule of the idUser
     */
    public List<Mission> getSchedule(String login) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Mission> list = new ArrayList<>();
        String query = "SELECT * FROM AppliUser WHERE login = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            getMissionList(rs, list);
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }

        return list;
    }

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Mission findById(int missionId)throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getConnection();
        Mission mission;
        String query = "SELECT m.*, i.interpreter, l.location, b.importance FROM Mission m JOIN MissionLocation l ON m.id = l.mission JOIN InterpreterMission i ON m.id = i.mission JOIN BeneficiaryMission b ON m.id = b.mission WHERE m.id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, missionId);
            rs = stmt.executeQuery();

            if(rs.next()){
                int jobSkillId = rs.getInt("jobSkill");
                JobSkill jobSkill = rs.wasNull() ? null : DAOJobSkill.findById(jobSkillId);
                int academicSkillId = rs.getInt("academicSkill");
                AcademicSkill academicSkill = rs.wasNull() ? null : DAOAcademicSkill.findById(academicSkillId);
                mission = new Mission(
                        rs.getInt("id"),
                        rs.getString("subject"),
                        MissionState.toMissionState(rs.getString("status")),
                        rs.getString("commentary"),
                        DAOImportance.findByMissionId(missionId),
                        DAOBeneficiary.findByIdBeneficiariesMission(missionId),
                        DAOInterpreter.findAllByMissionId(rs.getInt(missionId)),
                        DAOPunctualTimeSlot.findById(rs.getInt("timeSlot")),
                        DAOLocation.findById(rs.getInt("location")),
                        jobSkill,
                        academicSkill

                );
            }else{
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(rs,stmt);
        }
        return mission;
    }

    public static List<Mission> findAllByInterpreterLogin(String login) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Mission> list = new ArrayList<>();
        String query = "SELECT m.*, l.location FROM Mission m " +
                "JOIN MissionLocation l ON m.id = l.mission " +
                "JOIN InterpreterMission i ON m.id = i.mission " +
                "WHERE i.interpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            getMissionList(rs, list);
        } finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return list;
    }
}