package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Beneficiary;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.Interpreter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOBeneficiary implements DAO<Beneficiary> {

    /**
     *
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Beneficiary objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {

    }

    /**
     *
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Beneficiary objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Beneficiary objectToDelete)
            throws NoSuchElementException, SQLException {

    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Beneficiary> findAll() throws SQLException {
        return List.of();
    }

    /**
     *
     * @param idInterpreter represent the id of the interpreter which we want the beneficiaries
     * @return a List of Beneficiary which are referenced by the interpreter who have the idInterpreter
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to a existent interpreter
     */
    public List<Beneficiary> getReferenced(String idInterpreter) throws NoSuchElementException {
        return null;
    }

    /**
     *
     * @param idStatus represent the id of the status
     * @return a List of Beneficiary who have the id having the given idStatus
     * @throws NoSuchElementException if the idStatus doesn't correspond to a existent Status
     */
    public List<Beneficiary> getByStatus(int idStatus) throws NoSuchElementException {
        return null;
    }

    /**
     * @param login the login of the reference interpreter
     * @return the list of beneficiaries whose reference interpreter has this login, empty if none
     * @throws SQLException if the database could not be reached
     */
    public static List<Beneficiary> findAllReferenceInterpreter(String login) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT a.*, b.status, b.referenceInterpreter FROM AppliUser a JOIN Beneficiary b ON a.login = b.login WHERE b.referenceInterpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();
            Interpreter interpreter = DAOInterpreter.findByLogin(login);
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
                        DAOStatus.findById(idStatus),
                        interpreter
                );
                beneficiaries.add(b);
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return beneficiaries;
    }

    public static List<Beneficiary> findByIdBeneficiariesMission(int id)throws SQLException{
        Connection connection = DatabaseConnector.getConnection();
        String query = "SELECT a.*, b.status, b.referenceInterpreter FROM AppliUser a JOIN Beneficiary b ON a.login = b.login JOIN BeneficiaryMission bm ON  a.login = bm.login JOIN Mission m ON bm.mission = m.id WHERE m.id = ?";
        List<Beneficiary> list = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            while(rs.next()){
                Beneficiary b = new Beneficiary(
                        rs.getString("login"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthday").toLocalDate(),//pas sur que getString sois adapté pou un localDate
                        rs.getString("hashPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        DAOStatus.findById(rs.getInt("status")),
                        DAOInterpreter.findByLogin(rs.getString("referenceInterpreter"))
                );
                list.add(b);
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return list;
    }
}