package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOLocation;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.Location;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@Service
public class LocationService {

    /**
     * Returns a location by its ID.
     * @param id the ID of the location to find
     * @return the Location with the given ID, or null if not found
     * @throws SQLException if the database could not be reached
     */
    public Location getOneLocation(int id) throws SQLException {
        return SQLWrap.call(new DAOLocation()::find, id);
    }

    /**
     * Creates a new location in the database.
     * If the location already exists, sets its ID from the database and returns.
     * @param location the location to create
     * @throws SQLException if the database could not be reached
     */
    public void createLocation(Location location) throws SQLException {
        try {
            SQLWrap.callTransaction(new DAOLocation()::create, location);
        } catch (AlreadyExistsException e) {
            // ID already set by DAOLocation.create when AlreadyExistsException is thrown
        }
    }

    /**
     * Updates an existing location in the database.
     * @param oldLocation the location as it exists in database, used to get the ID
     * @param newLocation the new version of the location to apply
     * @throws NoSuchElementException if the location does not exist in database
     * @throws AlreadyExistsException if the updated location already exists
     * @throws SQLException if the database could not be reached
     */
    public void updateLocation(Location oldLocation, Location newLocation) throws NoSuchElementException, AlreadyExistsException, SQLException {
        if (oldLocation.equals(newLocation)) {
            return;
        }
        newLocation.setId(oldLocation.getId());
        SQLWrap.callTransaction(new DAOLocation()::update, newLocation);
    }

    /**
     * Deletes a location from the database.
     * @param location the location to delete
     * @throws NoSuchElementException if the location does not exist in database
     * @throws SQLException if the database could not be reached
     */
    public void deleteLocation(Location location) throws NoSuchElementException, SQLException {
        SQLWrap.callTransaction(new DAOLocation()::delete, location.getId());
    }
}