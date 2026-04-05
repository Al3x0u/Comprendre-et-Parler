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
                    rs.getString("subject"),
                    MissionState.toMissionState(rs.getString("status")),
                    rs.getString("commentary"),
                    rs.getInt("importance"),
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
        String queryMission = "INSERT INTO Mission (subject, stateOfMission, commentary, timeSlot, jobSkill, academicSkill) VALUE(?, ?, ?, ?, ?, ?)";
        String queryTimeSlot = "INSERT INTO TimeSlot(startTime, endTime, day) VALUE (?, ?, ?)";
        String queryMissionLoc = "INSERT INTO MissionLocation(mission, location, room) VALUE (?, ?, ?)";
        String queryBeneficaryMission = "INSERT INTO BeneficiaryMission (mission, beneficiary, importance) VALUE (?, ?, ?)";
        String queryInterpreterMission = "INSERT INTO InterpreterMission (mission, interpreter) VALUE (?, ?)";

        int rowsAffectedMission = 0;
        int rowsAffectionTimeSlot = 0;
        int rowsAffectedMissionLoc = 0;
        int rowsAffectedBenefMission = 0;
        int rowsAffectedInterpreterMission = 0;

        PreparedStatement stmt = null;

        try {
            stmt = connection.prepareStatement(queryTimeSlot);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(objectToInsert.getPunctualTime().getDate(), objectToInsert.getPunctualTime().getStartTime())));
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(objectToInsert.getPunctualTime().getDate(), objectToInsert.getPunctualTime().getEndTime())));
            stmt.setNull(3, Types.INTEGER);
            rowsAffectionTimeSlot = stmt.executeUpdate();

            int timeSlotId;
            ResultSet rs = null;
            stmt = connection.prepareStatement("SELECT MAX(id) FROM TimeSlot");
            rs = stmt.executeQuery();
            rs.next();
            timeSlotId = rs.getInt(1);

            stmt = connection.prepareStatement(queryMission);
            stmt.setString(1, objectToInsert.getSubjet());
            stmt.setString(2, objectToInsert.getStateOfMission().toString());
            stmt.setString(3, objectToInsert.getCommentary());
            stmt.setInt(4, timeSlotId);
            stmt.setInt(5, objectToInsert.getJobSkill().getId());
            stmt.setInt(6, objectToInsert.getAcademicSkill().getId());
            rowsAffectedMission = stmt.executeUpdate();

            stmt = connection.prepareStatement("SELECT MAX(id) FROM Mission");
            rs = stmt.executeQuery();
            rs.next();
            int missionId = rs.getInt(1);

            stmt = connection.prepareStatement(queryBeneficaryMission);
            for (Beneficiary b : objectToInsert.getBeneficiaries()){
                stmt.setInt(1,missionId);
                stmt.setString(2, b.getLogin());
                stmt.setInt(3, objectToInsert.getImportance());
                rowsAffectedBenefMission += stmt.executeUpdate();
            }

            stmt = connection.prepareStatement(queryInterpreterMission);
            for(Interpreter interpreter : objectToInsert.getInterpreters()){
                stmt.setInt(1, missionId);
                stmt.setString(2, interpreter.getLogin());
                rowsAffectedInterpreterMission += stmt.executeUpdate();
                stmt.getM
            }

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
    }

    /**
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Mission objectToDelete)
            throws NoSuchElementException, SQLException {
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
    public Mission findById(int id)throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getConnection();
        Mission mission;
        String query = "SELECT m.*, i.interpreter, l.location, b.importance FROM Mission m JOIN MissionLocation l ON m.id = l.mission JOIN InterpreterMission i ON m.id = i.mission JOIN BeneficiaryMission b ON m.id = b.mission WHERE m.id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                int jobSkillId = rs.getInt("jobSkill");
                JobSkill jobSkill = rs.wasNull() ? null : DAOJobSkill.findById(jobSkillId);
                int academicSkillId = rs.getInt("academicSkill");
                AcademicSkill academicSkill = rs.wasNull() ? null : DAOAcademicSkill.findById(academicSkillId);
                mission = new Mission(
                        rs.getString("subject"),
                        MissionState.toMissionState(rs.getString("status")),
                        rs.getString("commentary"),
                        rs.getInt("importance"),
                        DAOBeneficiary.findByIdBeneficiariesMission(id),
                        DAOInterpreter.findAllByMissionId(rs.getInt(id)),
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