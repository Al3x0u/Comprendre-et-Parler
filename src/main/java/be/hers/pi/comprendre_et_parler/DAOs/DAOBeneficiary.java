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
    protected static final String TABLE = "Beneficiary";
    protected static final String TABLE_APPLIUSER = "AppliUser";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_PASSWORD_UPDATED = "passwordUpdated";
    protected static final String FIELD_LOGIN = "login";
    protected static final String FIELD_FIRST_NAME = "firstName";
    protected static final String FIELD_LAST_NAME = "lastName";
    protected static final String FIELD_BIRTH_DATE = "birthDate";
    protected static final String FIELD_HASHED_PASSWORD = "hashedPassword";
    protected static final String FIELD_EMAIL = "email";
    protected static final String FIELD_PHONE_NUMBER = "phoneNumber";
    protected static final String FIELD_INTERPRETER_REFERENCE = "referenceInterpreter";
    protected static final String FIELD_STATUS = "status";

    @Override
    public Beneficiary find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);

            result = statement.executeQuery();
            if (result.next())
                beneficiary = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiary;
    }

    /**
     * Search for a Beneficiary in the database with the String parameter
     * @param login the login of the Beneficiary to find in database
     * @return the Beneficiary identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Beneficiary find(String login) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_LOGIN
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, login);

            result = statement.executeQuery();
            if (result.next())
                beneficiary = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiary;
    }

    @Override
    public void create(Beneficiary objectToInsert) throws AlreadyExistsException, SQLException {
        int idInDB = checkAlreadyExists(objectToInsert);
        if (idInDB >= 0) {
            objectToInsert.setId(idInDB);
            throw new AlreadyExistsException("Object already exists in database");
        }

        String query = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                TABLE,
                FIELD_FIRST_NAME, FIELD_LAST_NAME, FIELD_BIRTH_DATE, FIELD_HASHED_PASSWORD,
                FIELD_EMAIL, FIELD_PHONE_NUMBER, FIELD_STATUS, FIELD_INTERPRETER_REFERENCE
        );        PreparedStatement statement = null;
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
        String phoneNumber = newObject.getPhoneNumber();
        String query = String.format(
                "SELECT %s, %s FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s",
                FIELD_ID, FIELD_LOGIN, TABLE, FIELD_FIRST_NAME, FIELD_LAST_NAME,FIELD_BIRTH_DATE,
                FIELD_HASHED_PASSWORD, FIELD_EMAIL,
                phoneNumber == null || phoneNumber.isEmpty() ? FIELD_PHONE_NUMBER + " IS NULL" : FIELD_PHONE_NUMBER + " = ?"

        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, newObject.getFirstName());
            statement.setString(2, newObject.getLastName());
            statement.setDate(3, Date.valueOf(newObject.getBirthDate()));
            statement.setString(4, newObject.getHashedPassword());
            statement.setString(5, newObject.getEmail());
            if (phoneNumber != null && !phoneNumber.isEmpty()) statement.setString(6, phoneNumber);


            result = statement.executeQuery();
            if (result.next()) {
                newObject.setId(result.getInt(FIELD_ID));
                newObject.setLogin(result.getString(FIELD_LOGIN));
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    @Override
    public void update(Beneficiary objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (find(objectToUpdate.getId()) == null)
            throw new NoSuchElementException("[ERROR] There is no Beneficiary with the id " + objectToUpdate.getId());

        int idInDB = checkAlreadyExists(objectToUpdate);
        if (idInDB != objectToUpdate.getId() && idInDB >= 0)
            throw new AlreadyExistsException("The beneficiary already exists in database.");

        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE, FIELD_FIRST_NAME, FIELD_LAST_NAME,FIELD_BIRTH_DATE, FIELD_HASHED_PASSWORD,
                FIELD_EMAIL, FIELD_PHONE_NUMBER, FIELD_STATUS, FIELD_INTERPRETER_REFERENCE, FIELD_ID
        );
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

            statement.executeUpdate();
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public void delete(int idObjectToDelete) throws NoSuchElementException, SQLException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
         try {
             statement = DatabaseConnector.getInstance().prepareStatement(query);
             statement.setInt(1, idObjectToDelete);

             if (statement.executeUpdate()  == 0)
                 throw new NoSuchElementException("[ERROR] There is no Beneficiary with the id " + idObjectToDelete);
         } finally {
             closeStatement(statement);
         }
    }

    @Override
    public Set<Beneficiary> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        Set<Beneficiary> beneficiaries = new HashSet<Beneficiary>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();

            while (result.next())
                beneficiaries.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiaries;
    }

    @Override
    protected int checkAlreadyExists(Beneficiary objectToCheck) throws SQLException {
        String phoneNumber = objectToCheck.getPhoneNumber();
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s",
                FIELD_ID, TABLE, FIELD_FIRST_NAME, FIELD_LAST_NAME, FIELD_BIRTH_DATE, FIELD_EMAIL,
                phoneNumber == null || phoneNumber.isEmpty() ? FIELD_PHONE_NUMBER + " IS NULL" : FIELD_PHONE_NUMBER + " = ?"

        );
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToCheck.getFirstName());
            statement.setString(2, objectToCheck.getLastName());
            statement.setDate(3, Date.valueOf(objectToCheck.getBirthDate()));
            statement.setString(4, objectToCheck.getEmail());
            if (phoneNumber != null && !phoneNumber.isEmpty()) statement.setString(5, objectToCheck.getPhoneNumber());

            result = statement.executeQuery();
            if(result.next())
                return result.getInt(FIELD_ID);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return -1;
    }

    @Override
    protected Beneficiary getResult(ResultSet result) throws SQLException {
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
     * Return all Beneficiary referenced by the interpreter with the given id
     * @param idInterpreter represent the id of the interpreter which we want the beneficiary
     * @return a set of Beneficiary which are referenced by the interpreter who have the idInterpreter,
     * or an empty set if no beneficiaries
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to an existent interpreter
     * @throws SQLException if the database could not be reached
     */
    public Set<Beneficiary> findReferencedBeneficiaries(int idInterpreter) throws SQLException, NoSuchElementException {
        if (new DAOInterpreter().find(idInterpreter) == null)
            throw new NoSuchElementException("[ERROR] There is no Interpreter with the id " + idInterpreter);

        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_INTERPRETER_REFERENCE
        );
        Set<Beneficiary> beneficiaries = new HashSet<Beneficiary>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);

            result = statement.executeQuery();
            while (result.next())
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
     * @throws NoSuchElementException if the idStatus doesn't correspond to an existent Status
     * @return a Set of Beneficiary who have the id having the given idStatus,
     * or an empty Set if no beneficiaries having this Status
     */
    public Set<Beneficiary> getByStatus(int idStatus) throws SQLException, NoSuchElementException {
        if (new DAOInterpreter().find(idStatus) == null)
            throw new NoSuchElementException("[ERROR] There is no Status with the id " + idStatus);

        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_STATUS
        );
        Set<Beneficiary> beneficiaries = new HashSet<>();
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

    /**
     * Update the passwordUpdated flag of an AppliUser in the database
     * @param id the id of the AppliUser to update
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if no AppliUser with this id exists in the database
     * @post the passwordUpdated flag of the AppliUser has been set to true in the database
     */
    public void updatePasswordUpdated(int id) throws SQLException {
        String query = "UPDATE " + TABLE_APPLIUSER + " SET " + FIELD_PASSWORD_UPDATED + " = 1 WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            if(statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no AppliUser with the id " + id);
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * Retrieve the passwordUpdated flag of a user from the database
     * @param id the id of the user
     * @return true if the password has been updated, false otherwise
     * @throws SQLException if the database could not be reached
     */
    public boolean getPasswordUpdated(int id) throws SQLException {
        String query = "SELECT " + FIELD_PASSWORD_UPDATED + " FROM " + TABLE_APPLIUSER + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if(!result.next())
                throw new NoSuchElementException("[ERROR] There is no user with the id " + id);
            return result.getInt(FIELD_PASSWORD_UPDATED) == 1;
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }
}