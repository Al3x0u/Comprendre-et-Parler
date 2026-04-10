package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Importance;
import be.hers.pi.comprendre_et_parler.models.Beneficiary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOImportance {

    protected static final String TABLE_BENEFICIARY_MISSION = "BeneficiaryMission";
    protected static final String FIELD_MISSION = "mission";
    protected static final String FIELD_BENEFICIARY = "beneficiary";
    protected static final String FIELD_IMPORTANCE = "importance";

    /**
     * Return all Importance objects linked to the given mission id
     * @param missionId the id of the mission
     * @return a List of Importance linked to the mission, empty if none
     * @throws SQLException if the database could not be reached
     */
    public static List<Importance> findByMissionId(int missionId) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Importance> list = new ArrayList<>();
        String query = "SELECT " + FIELD_BENEFICIARY + ", " + FIELD_IMPORTANCE
                + " FROM " + TABLE_BENEFICIARY_MISSION
                + " WHERE " + FIELD_MISSION + " = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, missionId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Beneficiary beneficiary = new DAOBeneficiary().find(rs.getString(FIELD_BENEFICIARY));
                list.add(new Importance(beneficiary, rs.getInt(FIELD_IMPORTANCE)));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
        return list;
    }
}
