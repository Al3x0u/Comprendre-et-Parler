package be.hers.pi.comprendre_et_parler.DAOs;


import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.Manager;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOManager extends DAO<Manager> {

    protected static final String TABLE = "Manager";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_LOGIN = "login";
    protected static final String FIELD_FIRSTNAME = "firstName";
    protected static final String FIELD_LASTNAME = "lastName";
    protected static final String FIELD_BIRTHDATE = "birthDate";
    protected static final String FIELD_PASSWORD = "hashedPassword";
    protected static final String FIELD_EMAIL = "email";
    protected static final String FIELD_PHONE = "phoneNumber";
    protected static final String FIELD_HOURQUOTAWEEK = "weekHourlyQuota";
    protected static final String FIELD_HOURQUOTAYEAR = "yearHourlyQuota";
    protected static final String FIELD_TRANSPORTATION = "transportMode";
    protected static final String FIELD_LOCATION = "location";

    /**
     * Creates and populates a Manager object from the current row of the given ResultSet.
     * Retrieves all related data (academic skills, job skills, location, time slots,
     * and exceptional unavailabilities) using their respective DAOs.
     *
     * @param result the ResultSet positioned on the row to read, must not be null
     * @return a fully populated Manager object based on the current row of the ResultSet
     * @throws SQLException if a database access error occurs while reading the ResultSet
     */
    protected Manager getResult(ResultSet result) throws SQLException {
        return new Manager(
                result.getInt(FIELD_ID),
                result.getString(FIELD_LOGIN),
                result.getString(FIELD_FIRSTNAME),
                result.getString(FIELD_LASTNAME),
                result.getDate(FIELD_BIRTHDATE).toLocalDate(),
                result.getString(FIELD_PASSWORD),
                result.getString(FIELD_EMAIL),
                result.getString(FIELD_PHONE),
                result.getInt(FIELD_HOURQUOTAWEEK),
                result.getInt(FIELD_HOURQUOTAYEAR),
                result.getString(FIELD_TRANSPORTATION),
                new DAOAcademicSkill().getAcademicSkillOfAnInterpreter(result.getInt(FIELD_ID)),
                new DAOJobSkill().getJobSkillOfAnInterpreter(result.getInt(FIELD_ID)),
                new DAOLocation().find(result.getInt(FIELD_LOCATION)),
                new DAOBaseTimeSlot().findForInterpreter(result.getInt(FIELD_ID))
        );
    }

    /**
     * Search for a Manager in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Manager find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE,
                FIELD_ID);
        PreparedStatement statement = null;
        ResultSet result = null;
        Manager ret = null;

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();

            if (result.next())
                ret = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }

    /**
     * Search for a Manager in the database with the String parameter
     * @param login the login of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Manager find(String login) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE,
                FIELD_LOGIN
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Manager ret = null;

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, login);
            result = statement.executeQuery();

            if (result.next())
                ret = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }

    /**
     * Insert a Manager object in the database
     * @param objectToInsert an object of type Manager to add to the database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Manager objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert))
            throw new AlreadyExistsException("Manager with same data already exists");

        String query = String.format(
                "INSERT INTO %s VALUES (NULL, NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                TABLE
        );

        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToInsert.getFirstName());
            statement.setString(2, objectToInsert.getLastName());
            statement.setDate(3, Date.valueOf(objectToInsert.getBirthDate()));
            statement.setString(4, objectToInsert.getHashedPassword());
            statement.setString(5, objectToInsert.getEmail());
            statement.setString(6, objectToInsert.getPhoneNumber());
            statement.setInt(7, objectToInsert.getHourQuotaWeek());
            statement.setInt(8, objectToInsert.getHourQuotaYear());
            statement.setString(9, objectToInsert.getTransportMode());
            statement.setInt(10, objectToInsert.getLocation().getId());
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
    private void getNewAttributes(Manager newObject) throws SQLException {
        String query = String.format(
                "SELECT %s, %s FROM AppliUser WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?",
                FIELD_ID, FIELD_LOGIN,
                FIELD_FIRSTNAME, FIELD_LASTNAME, FIELD_BIRTHDATE,
                FIELD_PASSWORD, FIELD_EMAIL, FIELD_PHONE
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
     * Update a Manager line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws AlreadyExistsException if another object with the same attributes already exists in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Manager objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate))
            throw new AlreadyExistsException("Manager with same data already exists");

        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE,
                FIELD_FIRSTNAME,
                FIELD_LASTNAME,
                FIELD_BIRTHDATE,
                FIELD_PASSWORD,
                FIELD_EMAIL,
                FIELD_PHONE,
                FIELD_HOURQUOTAWEEK,
                FIELD_HOURQUOTAYEAR,
                FIELD_TRANSPORTATION,
                FIELD_ID
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
            statement.setInt(7, objectToUpdate.getHourQuotaWeek());
            statement.setInt(8, objectToUpdate.getHourQuotaYear());
            statement.setString(9, objectToUpdate.getTransportMode());
            statement.setInt(10, objectToUpdate.getId());

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("Manager not found in database");
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    protected boolean checkAlreadyExists(Manager objectToCheck) throws SQLException {
        String query = String.format(
                "SELECT 1 FROM %s WHERE " +
                        "%s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?",
                TABLE,
                FIELD_FIRSTNAME,
                FIELD_LASTNAME,
                FIELD_BIRTHDATE,
                FIELD_PASSWORD,
                FIELD_EMAIL,
                FIELD_PHONE,
                FIELD_HOURQUOTAWEEK,
                FIELD_HOURQUOTAYEAR,
                FIELD_TRANSPORTATION,
                FIELD_LOCATION
        );

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);

            statement.setString(1, objectToCheck.getFirstName());
            statement.setString(2, objectToCheck.getLastName());
            statement.setDate(3, Date.valueOf(objectToCheck.getBirthDate()));
            statement.setString(4, objectToCheck.getHashedPassword());
            statement.setString(5, objectToCheck.getEmail());
            statement.setString(6, objectToCheck.getPhoneNumber());
            statement.setInt(7, objectToCheck.getHourQuotaWeek());
            statement.setInt(8, objectToCheck.getHourQuotaYear());
            statement.setString(9, objectToCheck.getTransportMode());
            statement.setInt(10, objectToCheck.getLocation().getId());

            result = statement.executeQuery();
            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    /**
     * Delete a Manager line in the table in the database
     * @param idObjectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object ID matching objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object ID matching objectToDelete has been deleted from the database, and the change was commited
     */
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

            if(statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no manager with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * Return all line of Manager table in the database in a Set
     * @return every object of the corresponding type present in database (possibly an empty Set)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Set<Manager> findAll() throws SQLException {
        Set<Manager> managers = new HashSet<Manager>();
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();

            while (result.next())
                managers.add(getResult(result));
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return managers;
    }

    /**
     * Promote an Interpreter to a Manager by inserting it into the Manager table.
     * @param idInterpreter the id of the Interpreter to promote
     * @throws AlreadyExistsException if a Manager with this id already exists in database
     * @throws NoSuchElementException if no Interpreter with this id exists in database
     * @throws SQLException if the database could not be reached
     * @post the Interpreter has been added to the Manager table with the same id,
     *       and the change was committed
     */
    public void create(int idInterpreter) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (find(idInterpreter) != null)
            throw new AlreadyExistsException("Manager already exists");

        DAOInterpreter daoInterpreter = new DAOInterpreter();
        Interpreter interpreter = daoInterpreter.find(idInterpreter);
        if (interpreter == null)
            throw new NoSuchElementException("Interpreter not found");

        String query = "INSERT INTO ManagerT (id) VALUES (?)";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);
            statement.executeUpdate();
        } finally {
            closeStatement(statement);
        }
    }
}