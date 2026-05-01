package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;

public class DAOBeneficiary extends DAO<Beneficiary> {
    protected static final String TABLE_VIEW = "Beneficiary";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_LOGIN = "login";
    protected static final String FIELD_FIRST_NAME = "firstName";
    protected static final String FIELD_LAST_NAME = "lastName";
    protected static final String FIELD_BIRTH_DATE = "birthDate";
    protected static final String FIELD_HASHED_PASSWORD = "hashedPassword";
    protected static final String FIELD_EMAIL = "email";
    protected static final String FIELD_PHONE_NUMBER = "phoneNumber";
    protected static final String FIELD_INTERPRETER_REFERENCE = "referenceInterpreter";
    protected static final String FIELD_STATUS = "status";

    /**
     * Check if an object already exists in the database
     * @param objectToCheck the object to check
     * @return true if the object already exists, else false
     * @throws SQLException if the database could not be reached
     */
    protected boolean checkAlreadyExists(Beneficiary objectToCheck) throws SQLException{
        String query = "SELECT 1 FROM " + TABLE_VIEW + " WHERE " + FIELD_FIRST_NAME + " = ? AND "
                + FIELD_LAST_NAME + " = ? AND " + FIELD_BIRTH_DATE + " = ? AND "
                + FIELD_HASHED_PASSWORD + " = ? AND " + FIELD_EMAIL + " = ? AND "
                + FIELD_PHONE_NUMBER + " = ? AND " + FIELD_STATUS + " = ? AND "
                + FIELD_INTERPRETER_REFERENCE + " = ?";

        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToCheck.getFirstName());
            statement.setString(2, objectToCheck.getLastName());
            statement.setDate(3, Date.valueOf(objectToCheck.getBirthDate()));
            statement.setString(4, objectToCheck.getHashedPassword());
            statement.setString(5, objectToCheck.getEmail());
            statement.setString(6, objectToCheck.getPhoneNumber());
            statement.setInt(7, objectToCheck.getStatus().getId());
            statement.setInt(8, objectToCheck.getInterpreterRef().getId());
            result = statement.executeQuery();

            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Beneficiary find(int id) throws SQLException {
        String query = "SELECT * FROM %s WHERE %s = ?";
        query = String.format(query, TABLE_VIEW, FIELD_ID);

        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary = null;
        try{
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();

            if(result.next())
                beneficiary = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiary;
    }

    /**
     * Populates a Beneficiary object from the current row of the given ResultSet.
     * Fetches the associated Status and reference Interpreter from the database
     * using their respective DAOs.
     * @param result      the ResultSet positioned on the row to read, must not be null
     * @throws SQLException if a database access error occurs while reading the ResultSet
     */
    public Beneficiary getResult(ResultSet result) throws SQLException {
        return new Beneficiary(
                result.getInt(FIELD_ID),
                result.getString(FIELD_LOGIN),
                result.getString(FIELD_FIRST_NAME),
                result.getString(FIELD_LAST_NAME),
                result.getDate(FIELD_BIRTH_DATE).toLocalDate(),
                result.getString(FIELD_HASHED_PASSWORD),
                result.getString(FIELD_EMAIL),
                result.getString(FIELD_PHONE_NUMBER),
                new DAOStatus().find(result.getInt(FIELD_STATUS)),
                new DAOInterpreter().find(result.getInt(FIELD_INTERPRETER_REFERENCE))
        );
    }

    /**
     * Search for a Beneficiary in the database with the String parameter
     * @param login the login of the object to find in database
     * @return the object identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Beneficiary find(String login) throws SQLException {
        String query = "SELECT * FROM " + TABLE_VIEW + " WHERE " + FIELD_LOGIN + " = ?";

        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, login);
            result = statement.executeQuery();
            if(result.next())
                beneficiary = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiary;
    }

    /**
     * @param objectToInsert an object of type T to add to the database
     * @post objectToInsert has been added to the database, and the change was commited
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the insertion failed for any other reason
     */
    @Override
    public void create(Beneficiary objectToInsert) throws AlreadyExistsException, SQLException {
        if(find(objectToInsert.getLogin()) != null)
            throw new AlreadyExistsException("Object already exists in database");

        String query = "INSERT INTO " + TABLE_VIEW + " VALUES (NULL, NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToInsert.getFirstName());
            statement.setString(2, objectToInsert.getLastName());
            statement.setDate(3, Date.valueOf(objectToInsert.getBirthDate()));
            statement.setString(4, objectToInsert.getHashedPassword());
            statement.setString(5, objectToInsert.getEmail());
            statement.setString(6, objectToInsert.getPhoneNumber());
            statement.setInt(7, objectToInsert.getStatus().getId());
            statement.setInt(8, objectToInsert.getInterpreterRef().getId());
            statement.executeUpdate();

            getNewAttributes(objectToInsert);
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * Update the login and the id of the new object inserted in the database
     * @param newObject the new object inserted in the database
     * @throws SQLException if the database could not be reached
     */
    private void getNewAttributes(Beneficiary newObject) throws SQLException {
        String query = "SELECT " + FIELD_ID + ", " + FIELD_LOGIN + " FROM AppliUser WHERE " +
                FIELD_FIRST_NAME + " = ? AND " + FIELD_LAST_NAME + " = ? AND " + FIELD_BIRTH_DATE + " = ? AND " +
                FIELD_HASHED_PASSWORD + " = ? AND " + FIELD_EMAIL + " = ? AND " + FIELD_PHONE_NUMBER + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, newObject.getFirstName());
            statement.setString(2, newObject.getLastName());
            statement.setDate(3, Date.valueOf(newObject.getBirthDate()));
            statement.setString(4, newObject.getHashedPassword());
            statement.setString(5, newObject.getEmail());
            statement.setString(6, newObject.getPhoneNumber());

            result = statement.executeQuery();
            if(result.next()) {
                newObject.setId(result.getInt(FIELD_ID));
                newObject.setLogin(result.getString(FIELD_LOGIN));
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    /**
     * @param objectToUpdate the object to edit in the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws AlreadyExistsException if an object with a different id but otherwise identical fields already exists in database
     * @throws SQLException if the update failed for any other reason
     */
    @Override
    public void update(Beneficiary objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if(checkAlreadyExists(objectToUpdate))
            throw new AlreadyExistsException("The beneficiary already exists in database.");

        String query = "UPDATE " + TABLE_VIEW + " SET " +
                FIELD_FIRST_NAME + " = ?, " + FIELD_LAST_NAME + " = ?, " + FIELD_BIRTH_DATE + " = ?, " +
                FIELD_HASHED_PASSWORD + " = ?, " + FIELD_EMAIL + " = ?, " + FIELD_PHONE_NUMBER + " = ?, " +
                FIELD_STATUS + " = ?, " + FIELD_INTERPRETER_REFERENCE + " = ? WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);

            statement.setString(1, objectToUpdate.getFirstName());
            statement.setString(2, objectToUpdate.getLastName());
            statement.setDate(3, Date.valueOf(objectToUpdate.getBirthDate()));
            statement.setString(4, objectToUpdate.getHashedPassword());
            statement.setString(5, objectToUpdate.getEmail());
            statement.setString(6, objectToUpdate.getPhoneNumber());
            statement.setInt(7, objectToUpdate.getStatus().getId());
            statement.setInt(8, objectToUpdate.getInterpreterRef().getId());
            statement.setInt(9, objectToUpdate.getId());

            if(statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no user with the id " + objectToUpdate.getId() + ".");
        }finally {
            closeStatement(statement);
        }
    }

    /**
     * @param idObjectToDelete the ID of the object to delete in the database
     * @post the object ID matching objectToDelete has been deleted from the database, and the change was commited
     * @throws NoSuchElementException if no object ID matching objectToDelete was present in the database
     * @throws SQLException if the deletion failed for any other reason
     */
    @Override
    public void delete(int idObjectToDelete) throws NoSuchElementException, SQLException {
        String query = "DELETE FROM " + TABLE_VIEW + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
         try {
             statement = DatabaseConnector.getInstance().prepareStatement(query);
             statement.setInt(1, idObjectToDelete);

             if(statement.executeUpdate()  == 0)
                 throw new NoSuchElementException("[ERROR] There is no beneficiary with the id " + idObjectToDelete);
         } finally {
             closeStatement(statement);
         }
    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty Set)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Set<Beneficiary> findAll() throws SQLException {
        String query = "SELECT *  FROM " + TABLE_VIEW;

        Set<Beneficiary> beneficiaries = new HashSet<Beneficiary>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();

            while(result.next())
                beneficiaries.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiaries;
    }

    /**
     * Return all Beneficiary referenced by the interpreter with the given id
     * @param idInterpreter represent the id of the interpreter which we want the beneficiary
     * @return a set of Beneficiary which are referenced by the interpreter who have the idInterpreter, or an empty set if no beneficiaries
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to an existent interpreter
     * @throws SQLException if the database could not be reached
     */
    public Set<Beneficiary> findReferencedBeneficiaries(int idInterpreter) throws SQLException, NoSuchElementException {
        if(new DAOInterpreter().find(idInterpreter) == null )
            throw new NoSuchElementException("[ERROR] There is no interpreter with the id " + idInterpreter);
        Set<Beneficiary> beneficiaries = new HashSet<Beneficiary>();
        String query = "SELECT * FROM " + TABLE_VIEW + " WHERE " + FIELD_INTERPRETER_REFERENCE + " = ?";

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);
            result = statement.executeQuery();

            while(result.next())
                beneficiaries.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiaries;
    }

    /**
     * Return all Beneficiary having the given status
     * @param idStatus represent the id of the status
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if the idStatus doesn't correspond to a existent Status
     * @return a Set of Beneficiary who have the id having the given idStatus,or an empty Set if no beneficiaries having this Status
     */
    public Set<Beneficiary> getByStatus(int idStatus) throws SQLException, NoSuchElementException {
        if(new DAOInterpreter().find(idStatus) == null )
            throw new NoSuchElementException("[ERROR] There is no status with the id " + idStatus);

        Set<Beneficiary> beneficiaries = new HashSet<>();
        String query = "SELECT * FROM " + TABLE_VIEW + " WHERE " + FIELD_STATUS + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idStatus);
            result = statement.executeQuery();

            while(result.next())
                beneficiaries.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiaries;
    }
}