package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOInterpreter implements DAO<Interpreter> {


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
     * @param login the login of the interpreter
     * @return the Interpreter identified by login, or null if none was found
     * @throws SQLException if the database could not be reached
     */
    public static Interpreter findByLogin(String login) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        Interpreter interpreter = null;
        String query = "SELECT a.*, i.weekHourlyQuota, i.yearHourlyQuota, i.transportMode, i.location FROM AppliUser a JOIN interpreter i ON a.login = i.login WHERE a.login = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            if(rs.next()){
                int idTransportation = rs.getInt("transportation");
                String interpreterId = rs.getString("id");
                interpreter = new Interpreter(
                        login,
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthday").toLocalDate(),
                        rs.getString("hashedPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getInt("hourQuotaWeek"),
                        rs.getInt("hourQuotaYear"),
                        DAOTransportation.findById(idTransportation),
                        DAOAcademicSkill.findAllByInterpreterLogin(interpreterId),
                        DAOJobSkill.findAllByInterpreterLogin(login),
                        DAOBeneficiary.findAllReferenceInterpreter(interpreterId),
                        DAOMission.findAllByInterpreterLogin(login),
                        DAOLocation.findById(rs.getInt("location")),
                        DAOPunctualTimeSlot.findAllByInterpreterLogin(login),
                        DAOExceptionalUnavailability.findByInterpreterLogin(login)
                );
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return interpreter;
    }

    /**
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Interpreter objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String queryUser = "INSERT INTO AppliUser(login, lastName, firstName, birthday, hashPassword, mail, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String queryInterpreter = "INSERT INTO Interpreter (login, hourQuotaWeek, hourQuotaYear, transportation) VALUES (?, ?, ?, ?)";
        String queryAcademicSkill = "INSERT INTO AcademicSkillInterpreter (interpreter, skill) VALUES(?, ?)";
        String queryJobSkill = "INSERT INTO JobSkillInterpreter (interpreter, skill) VALUES(?, ?)";
        int rowsAffectedUser = 0;
        int rowsAffectedBeneficiary = 0;
        int rowsAffectedAcademicSkill = 0;
        int rowsAffectedJobSkill = 0;
        PreparedStatement stmt = null;

        try{
            try {
                findByLogin(objectToInsert.getLogin());
                throw new AlreadyExistsException();
            } catch (NoSuchElementException e) {
                //only to continue
            }
            stmt = connection.prepareStatement(queryUser);
            stmt.setString(1, objectToInsert.getLogin());
            stmt.setString(2, objectToInsert.getLastName());
            stmt.setString(3, objectToInsert.getFirstName());
            stmt.setDate(4, Date.valueOf(objectToInsert.getBirthday()));
            stmt.setString(5, objectToInsert.getPassword());
            stmt.setString(6, objectToInsert.getMail());
            stmt.setString(7, objectToInsert.getPhone());
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
            DatabaseConnector.closeStmt(null, stmt);
        }

    }

    /**
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Interpreter objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();

        String queryUser = "UPDATE AppliUser SET lastName = ?, firstName = ?, birthday = ?, hashPassword = ?, mail = ?, phone = ? WHERE id = ?";
        String queryInterpreter = "UPDATE Interpreter SET id = ?, hourQuotaWeek = ?, hourQuotaYear = ?, transportation = ? WHERE id = ?";
        String queryDeleteAcademicSkill = "DELETE FROM AcademicSkillInterpreter  WHERE interpreter = ?";
        String queryInsertAcademicSkill = "INSERT INTO AcademicSkillInterpreter (interpreter, skill) VALUES(?, ?)";
        String queryDeleteJobSkill = "DELETE FROM JobSkillInterpreter  WHERE interpreter = ?";
        String queryInsertJobSkill = "INSERT INTO JobSkillInterpreter (interpreter, skill) VALUES(?, ?)";

        PreparedStatement stmt = null;

        int rowsAffectedUser = 0;
        int rowsAffectedBeneficiary = 0;
        int rowsAffectedDeleteAcademicSkill = 0;
        int rowsAffectedInsertAcademicSkill = 0;
        int rowsAffectedDeleteJobSkill = 0;
        int rowsAffectedInsertJobSkill = 0;

        try{
            try {
                findByLogin(objectToUpdate.getLogin());
                throw new AlreadyExistsException();
            } catch (NoSuchElementException e) {
                //only to continue
            }
            stmt = connection.prepareStatement(queryUser);
            stmt.setString(1, objectToUpdate.getLastName());
            stmt.setString(2, objectToUpdate.getFirstName());
            stmt.setDate(3, Date.valueOf(objectToUpdate.getBirthday()));
            stmt.setString(4, objectToUpdate.getPassword());
            stmt.setString(5, objectToUpdate.getMail());
            stmt.setString(6, objectToUpdate.getPhone());
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
            DatabaseConnector.closeStmt(null, stmt);
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
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Interpreter objectToDelete)
            throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String queryUser = "DELETE FROM AppliUser  WHERE id = ?";
        String queryInterpreter = "DELETE FROM Interpreter  WHERE id = ?";
        String queryJobSkill = "DELETE FROM JobSkillInterpreter  WHERE interpreter = ?";
        String queryAcademicSkill = "DELETE FROM AcademicSkillInterpreter  WHERE interpreter = ?";

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
            DatabaseConnector.closeStmt(null, stmt);
        }
    }

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Interpreter> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Interpreter> interpreters = new ArrayList<>();
        String query = "SELECT a.*, i.* FROM AppliUser a JOIN Interpreter i ON a.id = i.id";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            while(rs.next()){
                int idTransportation = rs.getInt("transportation");
                String interpreterLogin = rs.getString("id");
                Interpreter interpreter = new Interpreter(
                        interpreterLogin,
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthday").toLocalDate(),
                        rs.getString("hashPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getInt("hourQuotaWeek"),
                        rs.getInt("hourQuotaYear"),
                        DAOTransportation.findById(idTransportation),
                        DAOAcademicSkill.findAllByInterpreterLogin(interpreterLogin),
                        DAOJobSkill.findAllByInterpreterLogin(interpreterLogin),
                        DAOBeneficiary.findAllReferenceInterpreter(interpreterLogin),
                        DAOMission.findAllByInterpreterLogin(interpreterLogin),
                        DAOLocation.findById(rs.getInt("location")),
                        DAOPunctualTimeSlot.findAllByInterpreterLogin(interpreterLogin),
                        DAOExceptionalUnavailability.findByInterpreterLogin(interpreterLogin)
                );
                interpreters.add(interpreter);
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return interpreters;
    }

    public static List<Interpreter> findAllByMissionId(int id) throws SQLException{
        Connection connection = DatabaseConnector.getConnection();
        List<Interpreter> list = new ArrayList<>();
        String query = "SELECT a.*, i.weekHourlyQuota, i.yearHourlyQuota, i.transportMode, i.location FROM AppliUser a JOIN Interpreter i ON a.login = i.login JOIN InterpreterMission im ON a.login = im.interpreter WHERE im.mission = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            while(rs.next()){
                String interpreterLogin = rs.getString("login");
                int idTransportation = rs.getInt("transportMode");
                Interpreter interpreter = new Interpreter(
                        interpreterLogin,
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthday").toLocalDate(),
                        rs.getString("hashPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getInt("hourQuotaWeek"),
                        rs.getInt("hourQuotaYear"),
                        DAOTransportation.findById(idTransportation),
                        DAOAcademicSkill.findAllByInterpreterLogin(interpreterLogin),
                        DAOJobSkill.findAllByInterpreterLogin(interpreterLogin),
                        DAOBeneficiary.findAllReferenceInterpreter(interpreterLogin),
                        DAOMission.findAllByInterpreterLogin(interpreterLogin),
                        DAOLocation.findById(rs.getInt("location")),
                        DAOPunctualTimeSlot.findAllByInterpreterLogin(interpreterLogin),
                        DAOExceptionalUnavailability.findByInterpreterLogin(interpreterLogin)
                );
                list.add(interpreter);
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }

        return list;
    }

    /**
     * @param start represent the start of the time that we want the availability
     * @param end represent the end of the time that we want the availability
     * @param date represent the date
     * @return a List of Interpreter who are available in the given time and date or null
     */
    public List<Interpreter> findAvailable(LocalTime start, LocalTime end, LocalDate date) {
        List<Interpreter> interpreters = new ArrayList<>();
        String query;
        return null;
    }

    /**
     * @param idAcademicSkills the id of the AcademicSkill
     * @return a List of Interpreter who have the AcademicSkill having the idAcademicSkills or null
     */
    public List<Interpreter> findByAcademicSkills(int idAcademicSkills)
            throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Interpreter> list = new ArrayList<>();
        String query = "SELECT a.*, i.weekHourlyQuota, i.yearHourlyQuota, i.transportMode, i.location FROM AppliUser a JOIN Interpreter i ON a.login = i.login JOIN AcademicSkillInterpreter ai ON a.login = ai.interpreter WHERE ai.skill = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, idAcademicSkills);
            rs = stmt.executeQuery();

            while(rs.next()){
                String interpreterLogin = rs.getString("login");
                int idTransportation = rs.getInt("transportMode");
                Interpreter interpreter = new Interpreter(
                        interpreterLogin,
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthday").toLocalDate(),
                        rs.getString("hashPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getInt("hourQuotaWeek"),
                        rs.getInt("hourQuotaYear"),
                        DAOTransportation.findById(idTransportation),
                        DAOAcademicSkill.findAllByInterpreterLogin(interpreterLogin),
                        DAOJobSkill.findAllByInterpreterLogin(interpreterLogin),
                        DAOBeneficiary.findAllReferenceInterpreter(interpreterLogin),
                        DAOMission.findAllByInterpreterLogin(interpreterLogin),
                        DAOLocation.findById(rs.getInt("location")),
                        DAOPunctualTimeSlot.findAllByInterpreterLogin(interpreterLogin),
                        DAOExceptionalUnavailability.findByInterpreterLogin(interpreterLogin)
                );
                list.add(interpreter);
            }
        }finally {
            DatabaseConnector.closeStmt(rs,stmt);
        }
        return list;
    }
}