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

    private void fermer(ResultSet rs, Statement stmt) {
        if(rs != null){
            try{
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        if(stmt != null){
            try{
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    private Transportation getTransportation(int id, Connection connection)throws SQLException {
        Transportation transportation = null;
        String query = "SELECT * FROM transportation WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                transportation = new Transportation(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
            }
        }finally {
            fermer(rs, stmt);
        }
        return transportation;
    }

    private List<AcademicSkill> getAcademicSkills(String id, Connection connection) throws SQLException {
        List<AcademicSkill> academicSkils = new ArrayList<>();
        String query = "SELECT a.id, a.designation FROM AcademicSkillInterpreter asi JOIN AcademicSkill a ON a.id = asi.skill WHERE asi.interpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            while(rs.next()){
                AcademicSkill academicSkil = new AcademicSkill(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
                academicSkils.add(academicSkil);
            }
        }finally {
            fermer(rs, stmt);
        }
        return academicSkils;
    }

    private List<JobSkill> getJobSkills(String login, Connection connection) throws SQLException {
        List<JobSkill> jobSkills = new ArrayList<>();
        String query = "SELECT j.id, j.designation FROM JobSkillInterpreter jsi JOIN JobSkill j ON j.id = jsi.skill WHERE jsi.interpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            while(rs.next()){
                JobSkill jobSkill = new JobSkill(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
                jobSkills.add(jobSkill);
            }
        }finally {
            fermer(rs, stmt);
        }
        return jobSkills;
    }

    private Status getStatus(int idStatus, Connection connection) throws SQLException {
        Status status = null;
        String query = "SELECT * FROM Status WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, idStatus);
            rs = stmt.executeQuery();

            if(rs.next()){
                status = new Status(
                        rs.getInt("id"),
                        rs.getString("designation"),
                        rs.getInt("hourQuota")
                );
            }
        }finally {
            fermer(rs, stmt);
        }
        return status;
    }

    private Interpreter getInterpreter(String login, Connection connection) throws SQLException {
        Interpreter interpreter = null;
        String query = "SELECT * FROM interpreter WHERE login = ?";

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
                        rs.getString("hashPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getInt("hourQuotaWeek"),
                        rs.getInt("hourQuotaYear"),

                        getTransportation(idTransportation, connection),
                        getAcademicSkills(interpreterId, connection),
                        getJobSkills(interpreterId, connection),
                        getBeneficiaries(interpreterId, connection)

                );
            }
        }finally {
            fermer(rs, stmt);
        }
        return interpreter;
    }

    private List<Beneficiary> getBeneficiaries(String login, Connection connection) throws SQLException {
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT a.*, b.beneficiaryStatus, b.referenceInterpreter FROM AppliUser a JOIN Beneficiary b ON a.login = b.login WHERE b.referenceInterpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            while(rs.next()){
                int idStatus = rs.getInt("beneficiaryStatus");
                Beneficiary b = new Beneficiary(
                        rs.getString("login"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthday").toLocalDate(),//pas sur que getString sois adapté pou un localDate
                        rs.getString("hashPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        getStatus(idStatus, connection),
                        getInterpreter(login, connection)
                );
                beneficiaries.add(b);
            }
        }finally {
            fermer(rs, stmt);
        }
        return beneficiaries;
    }

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if there is no interpreter with that login in the database
     */
    @Override
    public Interpreter find(String login) throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getConnection();

        Interpreter interpreter = null;
        String query = "SELECT * FROM interpreter i JOIN AppliUser a ON a.login = i.login WHERE i.login = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            if(rs.next()){
                int idTransportation = rs.getInt("transportation");
                interpreter = new Interpreter(
                        rs.getString("id"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthday").toLocalDate(),
                        rs.getString("hashPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getInt("hourQuotaWeek"),
                        rs.getInt("hourQuotaYear"),
                        getTransportation(idTransportation, connection),
                        getAcademicSkills(login, connection),
                        getJobSkills(login, connection),
                        getBeneficiaries(login, connection)
                );
            }else{
                throw new NoSuchElementException();
            }
        }finally {
            fermer(rs, stmt);
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
                find(objectToInsert.getLogin());
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
            fermer(null, stmt);
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
                find(objectToUpdate.getLogin());
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
            fermer(null, stmt);
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
    }

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Interpreter> findAll() throws SQLException {
        return List.of();
    }

    /**
     * @param start represent the start of the time that we want the availability
     * @param end represent the end of the time that we want the availability
     * @param date represent the date
     * @return a List of Interpreter who are available in the given time and date
     */
    public List<Interpreter> findAvailable(LocalTime start, LocalTime end, LocalDate date) {
        return null;
    }

    /**
     * @param idAcademicSkills the id of the AcademicSkill
     * @return a List of Interpreter who have the AcademicSkill having the idAcademicSkills
     * @throws NoSuchElementException if idAcademicSkills doesn't correspond to the id of any AcademicSkill
     */
    public List<Interpreter> findByAcademicSkills(int idAcademicSkills)
            throws NoSuchElementException {
        return null;
    }
}