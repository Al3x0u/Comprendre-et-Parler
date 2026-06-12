package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOPunctualTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.PunctualTimeSlot;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;
import java.util.NoSuchElementException;

public class TimeSlotService {

    private final DAOPunctualTimeSlot daoPunctualTimeSlot;

    public TimeSlotService() {
        this.daoPunctualTimeSlot = new DAOPunctualTimeSlot();
    }

    /**
     * Updates an existing PunctualTimeSlot in the database.
     * If the time slot already exists with the same dates, keeps the old ID.
     * @param oldSlot the existing time slot used to transfer the ID
     * @param newSlot the new time slot to apply
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if the time slot does not exist in the database
     */
    public void updateTimeSlot(PunctualTimeSlot oldSlot, PunctualTimeSlot newSlot) throws SQLException, NoSuchElementException {
        newSlot.setId(oldSlot.getId());
        try {
            SQLWrap.callTransaction(daoPunctualTimeSlot::update, newSlot);
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
        }
    }
}