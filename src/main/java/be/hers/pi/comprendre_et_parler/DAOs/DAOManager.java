package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Manager;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

import static be.hers.pi.comprendre_et_parler.DAOs.DAOBeneficiary.FIELD_PASSWORD_UPDATED;
import static be.hers.pi.comprendre_et_parler.DAOs.DAOBeneficiary.TABLE_APPLIUSER;

public class DAOManager extends DAO<Manager> {
    protected static final String TABLE = "ManagerT";
    protected static final String FIELD_ID = "id";

    @Override
    public Manager find(int id) throws SQLException {
        String query = String.format(
                "SELECT interpreter.* FROM %s interpreter, %s manager WHERE manager.%s = interpreter.%s AND manager.%s = ?",
                DAOInterpreter.TABLE, TABLE, FIELD_ID, DAOInterpreter.FIELD_ID, FIELD_ID
        );
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
     * @param login the login of the Manager to find in database
     * @return the Manager identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Manager find(String login) throws SQLException {
        String query = String.format(
                "SELECT interpreter.* FROM %s interpreter, %s manager WHERE manager.%s = interpreter.%s AND interpreter.%s = ?",
                DAOInterpreter.TABLE, TABLE, FIELD_ID, DAOInterpreter.FIELD_ID, DAOInterpreter.FIELD_LOGIN
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

    @Override
    public void create(Manager objectToInsert) throws AlreadyExistsException, SQLException {
        new DAOInterpreter().create(objectToInsert);
        String query = String.format("INSERT INTO %s VALUES (?)", TABLE);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToInsert.getId());
            statement.executeUpdate();
        } finally {
            closeStatement(statement);
        }
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

        if (new DAOInterpreter().find(idInterpreter) == null)
            throw new NoSuchElementException("[ERROR] There is no Interpreter with the id " + idInterpreter);

        String query = String.format("INSERT INTO %s VALUES (?)", TABLE);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);
            statement.executeUpdate();
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public void update(Manager objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        new DAOInterpreter().update(objectToUpdate);
    }

    @Override
    public void delete(int idObjectToDelete) throws NoSuchElementException, SQLException {
        String query = String.format("DELETE FROM %s WHERE %s = ?", TABLE, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idObjectToDelete);
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no Manager with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<Manager> findAll() throws SQLException {
        String query = String.format(
                "SELECT interpreter.* FROM %s interpreter, %s manager WHERE manager.%s = interpreter.%s",
                DAOInterpreter.TABLE, TABLE, FIELD_ID, DAOInterpreter.FIELD_ID
        );
        Set<Manager> managers = new HashSet<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next())
                managers.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return managers;
    }

    @Override
    protected int checkAlreadyExists(Manager objectToCheck) throws SQLException {
        String query = String.format("SELECT %s FROM %s WHERE %s = ?", FIELD_ID, TABLE, FIELD_ID);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToCheck.getId());
            result = statement.executeQuery();
            if (result.next())
                return result.getInt(FIELD_ID);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return -1;
    }

    @Override
    protected Manager getResult(ResultSet result) throws SQLException {
        int id = result.getInt(DAOInterpreter.FIELD_ID);
        Manager manager = new Manager(
                id,
                result.getString(DAOInterpreter.FIELD_LOGIN),
                result.getString(DAOInterpreter.FIELD_FIRST_NAME),
                result.getString(DAOInterpreter.FIELD_LAST_NAME),
                result.getDate(DAOInterpreter.FIELD_BIRTH_DATE).toLocalDate(),
                result.getString(DAOInterpreter.FIELD_HASHED_PASSWORD),
                result.getString(DAOInterpreter.FIELD_EMAIL),
                result.getString(DAOInterpreter.FIELD_PHONE_NUMBER),
                result.getInt(DAOInterpreter.FIELD_WEEK_QUOTA),
                result.getInt(DAOInterpreter.FIELD_YEAR_QUOTA),
                result.getString(DAOInterpreter.FIELD_TRANSPORT_MODE),
                new DAOAcademicSkill().getAcademicSkillOfAnInterpreter(id),
                new DAOJobSkill().getJobSkillOfAnInterpreter(id),
                new DAOLocation().find(result.getInt(DAOInterpreter.FIELD_LOCATION)),
                new DAOBaseTimeSlot().findAvailabilities(id)
        );
        manager.setUnavailability(new DAOExceptionalUnavailability().findForInterpreter(id));
        return manager;
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