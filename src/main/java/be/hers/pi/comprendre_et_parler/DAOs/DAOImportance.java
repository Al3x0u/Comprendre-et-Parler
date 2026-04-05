package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.*;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOImportance implements DAO<Importance> {

    /**
     * Search for a City in the database with the String parameter
     * @param id : identification of the city
     * @return City object who correspond to the given id else null
     * @throws SQLException if the database couldn't be reached
     */
    public City find(String id) throws SQLException {
        return null;
    }

    /**
     * Insert a City Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws SQLException if the database could not be reached
     */
    @Override
    public void create(Importance objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {

    }

    /**
     * Update a City line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     */
    @Override
    public void update(Importance objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     * Delete a line in the City table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Location object in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void delete(Importance objectToDelete)
            throws NoSuchElementException, SQLException {

    }

    /**
     * Return all line of City table in the database in City Object in a List
     * @return a List who contains City Object
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Importance> findAll() throws SQLException {
        return List.of();
    }

    public static List<Importance> findByMissionId(int missionId) throws SQLException{
        Connection connection = DatabaseConnector.getConnection();
        List<Importance> list = new ArrayList<>();
        String query = "SELECT b.*, bm.* FROM BeneficiaryMission bm JOIN Beneficiary b ON bm.beneficiary = b.login WHERE bm.mission = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, missionId);
            rs = stmt.executeQuery();

            while(rs.next()){
                list.add(new Importance(
                        DAOBeneficiary.findByLogin(rs.getString("beneficiary")),
                        rs.getInt("importance")
                ));
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }

        return list;
    }

}
