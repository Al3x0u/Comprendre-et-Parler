package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOPunctualTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.PunctualTimeSlot;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;

public class PunctualTimeSlotService {

    private final DAOPunctualTimeSlot daoPunctualTimeSlot;

    public PunctualTimeSlotService() {
        this.daoPunctualTimeSlot = new DAOPunctualTimeSlot();
    }

    /**
     * Finds an existing PunctualTimeSlot in the database with the same dates,
     * or creates a new one if it doesn't exist. Sets the ID on the slot in both cases.
     * @param slot the time slot to find or create
     * @throws SQLException if the database could not be reached
     */
    public void findOrCreate(PunctualTimeSlot slot) throws SQLException {
        try {
            SQLWrap.callTransaction(daoPunctualTimeSlot::create, slot);
        } catch (AlreadyExistsException e) {
            // Le créneau existe déjà — l'ID a été set par DAOPunctualTimeSlot.create
        }
    }
}