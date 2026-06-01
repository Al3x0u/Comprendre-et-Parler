package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOCity;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.services.wrappers.FunctionWithSQLException;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CityService {
    private final DAOCity daoCity;

    public CityService() {
        daoCity = new DAOCity();
    }

    /**
     * Search for a city in the database.
     * @return one city present in database
     * @param id the id of the city to find
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public City getOneCity(int id) throws ConnectionException, SQLException {
        return SQLWrap.call((FunctionWithSQLException<Integer, City>) daoCity::find, id);
    }

    /**
     * Get all cities from the database.
     * @return all cities present in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public List<City> getAllCities() throws ConnectionException, SQLException {
        return new ArrayList<>(SQLWrap.call(daoCity::findAll));
    }
}
