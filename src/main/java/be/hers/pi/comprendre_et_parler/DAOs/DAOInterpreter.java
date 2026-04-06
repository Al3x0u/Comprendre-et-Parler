package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOInterpreter implements DAO<Interpreter> {
    protected static final String TABLE = "Interpreter";
    protected static final String TABLE_APPLIUSER = "AppliUser";
    protected static final String TABLE_ACADEMIC_SKILL_INTERPRETER = "AcademicSkillInterpreter";
    protected static final String TABLE_JOB_SKILL_INTERPRETER = "JobSkillInterpreter";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_LOGIN = "login";
    protected static final String FIELD_INTERPRETER = "interpreter";
    protected static final String FIELD_FIRST_NAME = "firstName";
    protected static final String FIELD_LAST_NAME = "lastName";
    protected static final String FIELD_BIRTH_DATE = "birthDate";
    protected static final String FIELD_HASHED_PASSWORD = "hashedPassword";
    protected static final String FIELD_EMAIL = "email";
    protected static final String FIELD_PHONE_NUMBER = "phoneNumber";
    protected static final String FIELD_WEEK_QUOTA = "weekHourlyQuota";
    protected static final String FIELD_YEAR_QUOTA = "yearHourlyQuota";
    protected static final String FIELD_TRANSPORT_MODE = "transportMode";
    protected static final String FIELD_LOCATION = "location";
    protected static final String FIELD_MISSION = "mission";
    protected static final String TABLE_INTERPRETER_MISSION = "InterpreterMission";

    /*
    * constructor for DAOInterpreter object*/
    public DAOInterpreter() {
        try{
            DatabaseConnector.initialize();
        }catch (SQLException e){
            e.printStackTrace();//most useful than an Exception
        }
    }


    /**
     * Search for an Interpreter in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Interpreter find(int id) throws SQLException {
        return null;
    }

    
    public Interpreter find(String login) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        Interpreter interpreter = null;

        String query = "SELECT a.*, i.%s, i.%s, i.%s, i.%s FROM %s a JOIN %s i ON a.%s = i.%s WHERE a.%s = ?";
        query = String.format(query, FIELD_WEEK_QUOTA, FIELD_YEAR_QUOTA, FIELD_TRANSPORT_MODE, FIELD_LOCATION, TABLE_APPLIUSER, FIELD_LOGIN, FIELD_LOGIN, FIELD_LOGIN);

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            if(rs.next()){
                int idTransportation = rs.getInt(FIELD_TRANSPORT_MODE);
                String interpreterId = rs.getString(FIELD_ID);
                interpreter = new Interpreter(
                        login,
                        rs.getString(FIELD_FIRST_NAME),
                        rs.getString(FIELD_LAST_NAME),
                        rs.getDate(FIELD_BIRTH_DATE).toLocalDate(),
                        rs.getString(FIELD_HASHED_PASSWORD),
                        rs.getString(FIELD_EMAIL),
                        rs.getString(FIELD_PHONE_NUMBER),
                        rs.getInt(FIELD_WEEK_QUOTA),
                        rs.getInt(FIELD_YEAR_QUOTA),
                        DAOTransportation.findById(idTransportation),
                        DAOAcademicSkill.findAllByInterpreterLogin(interpreterId),
                        DAOJobSkill.findAllByInterpreterLogin(login),
                        new DAOBeneficiary().findReferencedBeneficiaries(interpreterId),
                        DAOMission.findAllByInterpreterLogin(login),
                        DAOLocation.findById(rs.getInt(FIELD_LOCATION)),
                        DAOPunctualTimeSlot.findAllByInterpreterLogin(login),
                        DAOExceptionalUnavailability.findByInterpreterLogin(login)
                );
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return interpreter;
    }

    /**
     * Insert an Interpreter object in the database
     * @param objectToInsert an object of type Interpreter to add to the database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Interpreter objectToInsert)
            throws AlreadyExistsException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String queryUser = "INSERT INTO " + TABLE_APPLIUSER + " (" + FIELD_LOGIN + ", "
                            + FIELD_LAST_NAME + ", " + FIELD_FIRST_NAME + ", "
                            + FIELD_BIRTH_DATE + ", " + FIELD_HASHED_PASSWORD + ", "
                            + FIELD_EMAIL + ", " + FIELD_PHONE_NUMBER + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        String queryInterpreter = "INSERT INTO " + TABLE + " (" + FIELD_LOGIN + ", "
                            + FIELD_WEEK_QUOTA + ", " + FIELD_YEAR_QUOTA + ", "
                            + FIELD_TRANSPORT_MODE + ") VALUES (?, ?, ?, ?)";
        String queryAcademicSkill = "INSERT INTO " +  TABLE_ACADEMIC_SKILL_INTERPRETER +" ("
                            + FIELD_ID + ", " + DAOAcademicSkill.FIELD_ID + ") VALUES(?, ?)";
        String queryJobSkill = "INSERT INTO " + TABLE_JOB_SKILL_INTERPRETER + " ("+ FIELD_LOGIN
                            + ", " + DAOJobSkill.FIELD_ID + ") VALUES(?, ?)";
        int rowsAffectedUser = 0;
        int rowsAffectedBeneficiary = 0;
        int rowsAffectedAcademicSkill = 0;
        int rowsAffectedJobSkill = 0;
        PreparedStatement stmt = null;

        try{
            try {
                find(objectToInsert.getLogin());
                throw new AlreadyExistsException();
            } catch (NoSuchElementException e) {
                //only to continue
            }
            stmt = connection.prepareStatement(queryUser);
            stmt.setString(1, objectToInsert.getLogin());
            stmt.setString(2, objectToInsert.getLastName());
            stmt.setString(3, objectToInsert.getFirstName());
            stmt.setDate(4, Date.valueOf(objectToInsert.getBirthDate()));
            stmt.setString(5, objectToInsert.getHashedPassword());
            stmt.setString(6, objectToInsert.getEmail());
            stmt.setString(7, objectToInsert.getPhoneNumber());
            rowsAffectedUser = stmt.executeUpdate();

            stmt = connection.prepareStatement(queryInterpreter);
            stmt.setString(1, objectToInsert.getLogin());
            stmt.setInt(2, objectToInsert.getHourQuotaWeek());
            stmt.setInt(3, objectToInsert.getHourQuotayear());
            stmt.setInt(4, objectToInsert.getTransportation().getId());
            rowsAffectedBeneficiary = stmt.executeUpdate();

            stmt = connection.prepareStatement(queryAcademicSkill);
            stmt.setString(1,objectToInsert.getLogin());
            for(AcademicSkill element : objectToInsert.getAcademicSkills()){
                stmt.setInt(2, element.getId());
                rowsAffectedAcademicSkill = stmt.executeUpdate();
            }


            stmt = connection.prepareStatement(queryJobSkill);
            stmt.setString(1,objectToInsert.getLogin());
            for(JobSkill element : objectToInsert.getJobSkills()){
                stmt.setInt(2, element.getId());
                rowsAffectedJobSkill = stmt.executeUpdate();
            }


            if(rowsAffectedUser > 0 && rowsAffectedBeneficiary > 0 &&  rowsAffectedAcademicSkill > 0 &&   rowsAffectedJobSkill > 0){
                System.out.println("Interprete inséré avec succès");
            }else{
                System.out.println("Un problème est survenu lors de l'insertion. Veuillez réessayer");
            }
        }finally {
            if(stmt != null){
                stmt.close();
            }
        }
    }

    /**
     * Update an Interpreter line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Interpreter objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();

        String queryUser = "UPDATE " + TABLE_APPLIUSER + " SET "+ FIELD_LAST_NAME + " = ?, "
                            + FIELD_FIRST_NAME + " = ?, "+ FIELD_BIRTH_DATE + " = ?, "
                            + FIELD_HASHED_PASSWORD + " = ?, " + FIELD_EMAIL + " = ?, "
                            + FIELD_PHONE_NUMBER + " = ? WHERE "+ FIELD_ID + " = ?";
        String queryInterpreter = "UPDATE " + TABLE + " SET " + FIELD_ID + " = ?, " + FIELD_WEEK_QUOTA + " = ?, " + FIELD_YEAR_QUOTA + " = ?, " + FIELD_TRANSPORT_MODE + " = ? WHERE " + FIELD_ID + " = ?";
        String queryDeleteAcademicSkill = "DELETE FROM " + TABLE_ACADEMIC_SKILL_INTERPRETER + "  WHERE interpreter = ?";
        String queryInsertAcademicSkill = "INSERT INTO " + TABLE_ACADEMIC_SKILL_INTERPRETER + " (" + FIELD_INTERPRETER + ", " + DAOAcademicSkill.FIELD_SKILL + ") VALUES(?, ?)";
        String queryDeleteJobSkill = "DELETE FROM " + TABLE_JOB_SKILL_INTERPRETER + "  WHERE " + FIELD_INTERPRETER + " = ?";
        String queryInsertJobSkill = "INSERT INTO " + TABLE_JOB_SKILL_INTERPRETER + " (" + FIELD_INTERPRETER + ", " + DAOJobSkill.FIELD_SKILL + ") VALUES(?, ?)";

        PreparedStatement stmt = null;

        int rowsAffectedUser = 0;
        int rowsAffectedBeneficiary = 0;
        int rowsAffectedDeleteAcademicSkill = 0;
        int rowsAffectedInsertAcademicSkill = 0;
        int rowsAffectedDeleteJobSkill = 0;
        int rowsAffectedInsertJobSkill = 0;

        try{
            try {
                find(objectToUpdate.getLogin());
                throw new AlreadyExistsException();
            } catch (NoSuchElementException e) {
                //only to continue
            }
            stmt = connection.prepareStatement(queryUser);
            stmt.setString(1, objectToUpdate.getLastName());
            stmt.setString(2, objectToUpdate.getFirstName());
            stmt.setDate(3, Date.valueOf(objectToUpdate.getBirthDate()));
            stmt.setString(4, objectToUpdate.getHashedPassword());
            stmt.setString(5, objectToUpdate.getEmail());
            stmt.setString(6, objectToUpdate.getPhoneNumber());
            stmt.setString(7, objectToUpdate.getLogin());
            rowsAffectedUser = stmt.executeUpdate();

            stmt =  connection.prepareStatement(queryInterpreter);
            stmt.setString(1, objectToUpdate.getLogin());
            stmt.setInt(2, objectToUpdate.getHourQuotaWeek());
            stmt.setInt(3, objectToUpdate.getHourQuotayear());
            stmt.setInt(4, objectToUpdate.getTransportation().getId());
            stmt.setString(5, objectToUpdate.getLogin());
            rowsAffectedBeneficiary = stmt.executeUpdate();

            stmt = connection.prepareStatement(queryDeleteAcademicSkill);
            stmt.setString(1, objectToUpdate.getLogin());
            rowsAffectedDeleteAcademicSkill = stmt.executeUpdate();

            stmt = connection.prepareStatement(queryInsertAcademicSkill);
            stmt.setString(1, objectToUpdate.getLogin());
            for(AcademicSkill element : objectToUpdate.getAcademicSkills()){
                stmt.setInt(2, element.getId());
                rowsAffectedInsertAcademicSkill = stmt.executeUpdate();
            }

            stmt = connection.prepareStatement(queryDeleteJobSkill);
            stmt.setString(1, objectToUpdate.getLogin());
            rowsAffectedDeleteJobSkill = stmt.executeUpdate();

            stmt = connection.prepareStatement(queryInsertJobSkill);
            stmt.setString(1, objectToUpdate.getLogin());
            for(JobSkill element : objectToUpdate.getJobSkills()){
                stmt.setInt(2, element.getId());
                rowsAffectedInsertJobSkill = stmt.executeUpdate();
            }
        }finally {
            if(stmt != null){
                stmt.close();
            }
        }

        if(rowsAffectedUser > 0 && rowsAffectedBeneficiary > 0 &&
                rowsAffectedInsertJobSkill > 0 && rowsAffectedDeleteJobSkill > 0 &&
                rowsAffectedDeleteAcademicSkill > 0 && rowsAffectedInsertAcademicSkill > 0){
            System.out.println("Interprète mis à jour avec succès");
        }else{
            System.out.println("Erreur lors de la mis à jour de l'interprète");
        }
    }

    /**
     * Delete an Interpreter line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Interpreter objectToDelete)
            throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String queryUser = "DELETE FROM " + TABLE_APPLIUSER + "  WHERE " + FIELD_ID + " = ?";
        String queryInterpreter = "DELETE FROM " + TABLE + "  WHERE " + FIELD_ID + " = ?";
        String queryJobSkill = "DELETE FROM " + TABLE_JOB_SKILL_INTERPRETER + "  WHERE " + FIELD_INTERPRETER + " = ?";
        String queryAcademicSkill = "DELETE FROM " + TABLE_ACADEMIC_SKILL_INTERPRETER + "  WHERE " + FIELD_INTERPRETER + " = ?";

        PreparedStatement stmt = null;

        int rowsAffectedUser = 0;
        int rowsAffectedBeneficiary = 0;
        int rowsAffectedJobSkill = 0;
        int rowsAffectedAcademicSkill = 0;

        try{
            stmt = connection.prepareStatement(queryAcademicSkill);
            stmt.setString(1, objectToDelete.getLogin());
            rowsAffectedAcademicSkill = stmt.executeUpdate();

            stmt = connection.prepareStatement(queryJobSkill);
            stmt.setString(1, objectToDelete.getLogin());
            rowsAffectedJobSkill = stmt.executeUpdate();

            stmt = connection.prepareStatement(queryInterpreter);
            stmt.setString(1, objectToDelete.getLogin());
            rowsAffectedBeneficiary = stmt.executeUpdate();

            stmt = connection.prepareStatement(queryUser);
            stmt.setString(1, objectToDelete.getLogin());
            rowsAffectedUser = stmt.executeUpdate();

            if(rowsAffectedUser > 0 && rowsAffectedBeneficiary > 0 &&
                    rowsAffectedAcademicSkill > 0 && rowsAffectedJobSkill > 0){
                System.out.println("Suppression de l'interprète avec succès.");
            }else{
                System.out.println("Erreur lors de la suppression de l'interprète");
            }
        }finally {
            if(stmt != null){
                stmt.close();
            }
        }
    }

    /**
     * Return all line of Interpreter table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Interpreter> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> interpreters = new ArrayList<>();
        String query = "SELECT " + FIELD_ID + " i FROM " + TABLE + " JOIN " + TABLE_APPLIUSER + " a ON i." + FIELD_ID + " = a." + FIELD_ID;

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            while(rs.next()){
                interpreters.add(find(rs.getString("login")));
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return interpreters;
    }

    public List<Interpreter> findAllByMissionId(int id) throws SQLException{
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> list = new ArrayList<>();
        String query = "SELECT i." + FIELD_ID + " FROM " + TABLE + " i JOIN " + TABLE_INTERPRETER_MISSION + " im ON i." + FIELD_ID + " = im." + FIELD_INTERPRETER + " WHERE im." + FIELD_MISSION + " = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            while(rs.next()){
                list.add(find(rs.getString(FIELD_LOGIN)));
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }

        return list;
    }

    /**
     * Return all Interpreter who are available in the given time and date
     * @param start represent the start of the time that we want the availability
     * @param end represent the end of the time that we want the availability
     * @param date represent the date
     * @return a List of Interpreter who are available in the given time and date or null
     * @return a List of Interpreter who are available in the given time and date, or an empty List if no Interpreter is available
     */
    public List<Interpreter> findAvailable(LocalTime start, LocalTime end, LocalDate date)throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> interpreters = new ArrayList<>();
        String query = "SELECT i." + FIELD_LOGIN + " FROM " + TABLE + " i JOIN "
                + DAOPunctualTimeSlot.TABLE_AVAILABILITY + " av ON i." + FIELD_LOGIN
                + " = av." + DAOPunctualTimeSlot.FIELD_INTERPRETER + " JOIN "
                + DAOPunctualTimeSlot.TABLE_TIMESLOT + " t ON av." + DAOPunctualTimeSlot.TABLE_TIMESLOT
                + " = t." + DAOPunctualTimeSlot.TABLE_TIMESLOT_ID + " WHERE t."
                + DAOPunctualTimeSlot.FIELD_START_TIME + " = ? AND t."
                + DAOPunctualTimeSlot.FIELD_END_TIME + " = ? AND TRUNC(t."
                + DAOPunctualTimeSlot.FIELD_START_TIME + ") = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(date, start)));
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(date, end)));
            stmt.setDate(3, Date.valueOf(date));
            rs = stmt.executeQuery();

            while(rs.next()){
                interpreters.add(find(rs.getString("login")));
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return interpreters;
    }

    /**
     * Return all Interpreter who have the AcademicSkill having the given id
     * @param idAcademicSkills the id of the AcademicSkill
     * @return a List of Interpreter who have the AcademicSkill having the idAcademicSkills, or an empty List if no Interpreter have this AcademicSkill
     * @throws NoSuchElementException if idAcademicSkills doesn't correspond to the id of any AcademicSkill
     */
    public List<Interpreter> findByAcademicSkills(int idAcademicSkills)
            throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> list = new ArrayList<>();
        String query = "SELECT i." + FIELD_LOGIN + " FROM " + TABLE + " i ON JOIN " + TABLE_ACADEMIC_SKILL_INTERPRETER + " ai ON i." + FIELD_LOGIN + " = ai." + FIELD_INTERPRETER + " WHERE ai." + DAOAcademicSkill.FIELD_SKILL + " = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, idAcademicSkills);
            rs = stmt.executeQuery();

            while(rs.next()){
                list.add(find(rs.getString("login")));
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return list;
    }

    /**
     * Return all Interpreter who have the JobSkill having the given id
     * @param idJobSkills the id of the JobSkill
     * @return a List of Interpreter who have the JobSkill having the idJobSkills, or an empty List if no Interpreter have this JobSkill
     * @throws NoSuchElementException if idJobSkills doesn't correspond to the id of any JobSkill
     */
    public List<Interpreter> findByJobSkills(int idJobSkills)
            throws NoSuchElementException {
        return null;
    }
}