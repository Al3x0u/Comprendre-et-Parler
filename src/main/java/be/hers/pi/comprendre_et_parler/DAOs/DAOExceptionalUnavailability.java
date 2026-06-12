package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.ExceptionalUnavailability;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.Interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOExceptionalUnavailability {
    protected static final String TABLE = "Unavailability";
    protected static final String FIELD_ID_INTERPRETER = "interpreter";
    protected static final String FIELD_ID_TIMESLOT = "timeSlot";
    protected static final String FIELD_REASON = "reason";

    /**
     * Search for a ExceptionalUnavailability in the database with the int parameter
     * @param idInterpreter the primary key of the Interpreter for which one finds the unavailability in the database
     * @param idTimeSlot the primary key of the PunctualTimeSlot for which one finds the unavailability in the database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public ExceptionalUnavailability find(int idInterpreter, int idTimeSlot) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ? AND %s = ?",
                TABLE, FIELD_ID_INTERPRETER, FIELD_ID_TIMESLOT);
        PreparedStatement statement = null;
        ResultSet result = null;
        ExceptionalUnavailability unavailability = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);
            statement.setInt(2, idTimeSlot);

            result = statement.executeQuery();
            if (result.next())
                unavailability = getResult(result);
        } finally {
            if(result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return unavailability;
    }

    /**
     * Insert a ExceptionalUnavailability object in the database
     * @param objectToInsert an object of type ExceptionalUnavailability to add to the database
     * @param interpreter the interpreter who is unavailable
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    public void create(ExceptionalUnavailability objectToInsert, Interpreter interpreter) throws AlreadyExistsException, IllegalArgumentException, SQLException {
        if (checkAlreadyExists(objectToInsert, interpreter))
            throw new AlreadyExistsException("An unavailability for interpreter " + interpreter.getId()
                    + " at time slot " + objectToInsert.getTimeSlot().getId()+ " already exists in the database");

        int interpreterRef = new DAOInterpreter().checkAlreadyExists(interpreter);
        if (interpreterRef < 0)
            throw new IllegalArgumentException("Interpreter " + interpreter.getFullName()
                    + " (id " + interpreter.getId() + ") does not exist in database");

        try {
            new DAOPunctualTimeSlot().create(objectToInsert.getTimeSlot());
        }
        catch (AlreadyExistsException e) {}

        String query = String.format("INSERT INTO %s(%s, %s, %s) VALUES (?, ?, ?)",
                TABLE, FIELD_ID_INTERPRETER, FIELD_ID_TIMESLOT, FIELD_REASON);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, interpreterRef);
            statement.setInt(2, objectToInsert.getTimeSlot().getId());
            statement.setString(3, objectToInsert.getReason());

            statement.executeUpdate();

        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Update a ExceptionalUnavailability line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @param interpreter the interpreter who is unavailable
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    public void update(ExceptionalUnavailability objectToUpdate, Interpreter interpreter) throws NoSuchElementException, SQLException {
        try {
            new DAOPunctualTimeSlot().create(objectToUpdate.getTimeSlot());
        } catch (AlreadyExistsException e) {}

        String query = String.format(
                "UPDATE %s SET %s = ? WHERE %s = ? AND %s = ?",
                TABLE, FIELD_REASON, FIELD_ID_INTERPRETER, FIELD_ID_TIMESLOT
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getReason());
            statement.setInt(2, interpreter.getId());
            statement.setInt(3, objectToUpdate.getTimeSlot().getId());

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] The Interpreter " + interpreter.getId()
                        + " is not linked to the TimeSLot " + objectToUpdate.getTimeSlot().getId());
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Delete a ExceptionalUnavailability line in the table in the database
     * @param idInterpreter the primary key of the Interpreter for which one delete the unavailability in the database
     * @param idTimeSlot the primary key of the PunctualTimeSlot for which one delete the unavailability in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    public void delete(int idInterpreter, int idTimeSlot) throws NoSuchElementException, SQLException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ? AND %s = ?",
                TABLE, FIELD_ID_INTERPRETER, FIELD_ID_TIMESLOT
        );
        PreparedStatement statement = null;

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);
            statement.setInt(2, idTimeSlot);

            if(statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] The Interpreter " + idInterpreter + " is not linked to the TimeSLot " + idTimeSlot);

        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Return all line of ExceptionalUnavailability table in the database in a Set
     * @return every object of the corresponding type present in database (possibly an empty Set)
     * @throws SQLException if the database could not be reached
     */
    public Set<ExceptionalUnavailability> findAll() throws SQLException {
        Set<ExceptionalUnavailability> unavailability = new HashSet<ExceptionalUnavailability>();
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);

            result = statement.executeQuery();
            while (result.next())
                unavailability.add(getResult(result));
        } finally {
            if(result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return unavailability;
    }

    /**
     * Check if an ExceptionalUnavailability already exists in the database
     * @param objectToCheck the ExceptionalUnavailability to check
     * @param interpreter the interpreter who is unavailable
     * @return true if the ExceptionalUnavailability already exists, else false
     * @throws SQLException if the database could not be reached
     */
    protected boolean checkAlreadyExists(ExceptionalUnavailability objectToCheck, Interpreter interpreter) throws SQLException {
        int idTimeSlot = new DAOPunctualTimeSlot().checkAlreadyExists(objectToCheck.getTimeSlot());

        if (idTimeSlot == -1)
            return false;

        String query = String.format(
                "SELECT 1 FROM %s WHERE %s = ? AND %s = ?",
                TABLE,
                FIELD_ID_INTERPRETER,
                FIELD_ID_TIMESLOT
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, interpreter.getId());
            statement.setInt(2, idTimeSlot);

            result = statement.executeQuery();
            return result.next();
        } finally {
            if(result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Build an ExceptionalUnavailability from a ResultSet
     * @param result the ResultSet to read from
     * @return an ExceptionalUnavailability built from the ResultSet
     * @throws SQLException if the database could not be reached
     */
    protected ExceptionalUnavailability getResult(ResultSet result) throws SQLException {
        return new ExceptionalUnavailability(
                result.getString(FIELD_REASON),
                new DAOPunctualTimeSlot().find(result.getInt(FIELD_ID_TIMESLOT))
        );
    }

    /**
     * Return all ExceptionalUnavailability of an Interpreter with the given id
     * @param idInterpreter he primary key of the interpreter for which one finds all unavailability in the database
     * @return a Set of ExceptionalUnavailability instances representing the interpreter’s exceptional unavailability, or an empty Set if none exist
     * @throws IllegalArgumentException if id is < 0
     * @throws SQLException if the database could not be reached
     */
    public Set<ExceptionalUnavailability> findForInterpreter(int idInterpreter) throws IllegalArgumentException, SQLException {
        if (idInterpreter < 0)
            throw new IllegalArgumentException("Invalid id : " + idInterpreter);

        Set<ExceptionalUnavailability> unavailability = new HashSet<ExceptionalUnavailability>();
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID_INTERPRETER
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);

            result = statement.executeQuery();
            while (result.next())
                unavailability.add(getResult(result));
        } finally {
            if(result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return unavailability;
    }
}