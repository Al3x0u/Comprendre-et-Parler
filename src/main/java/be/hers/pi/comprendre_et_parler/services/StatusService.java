package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOStatus;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatusService {

    private final DAOStatus daoStatus = new DAOStatus();

    /***
     * get the a status by it id
     * @param id the id of the status to get
     * @return the status with the id id
     */
    public Status getStatus(int id)throws SQLException{
        return SQLWrap.call(daoStatus::find, id);
    }

    /***
     *
     * @return
     * @throws SQLException
     */
    public List<Status> getAllStatus()throws SQLException{
        return new ArrayList<>(SQLWrap.call(daoStatus::findAll));
    }
}
