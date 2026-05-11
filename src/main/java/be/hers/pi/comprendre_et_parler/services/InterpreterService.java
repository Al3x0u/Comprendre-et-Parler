package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.DTO.InterpreterCreationForm;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.ConsumerWithSQLException;
import be.hers.pi.comprendre_et_parler.services.wrappers.FunctionWithSQLException;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.util.*;

public class InterpreterService {

    private final DAOInterpreter daoInterpreter;
    private final DAOBeneficiary daoBeneficiary;
    private final DAOMission daoMission;
    private final MissionService missionService;


    public InterpreterService() {
        this.daoInterpreter = new DAOInterpreter();
        this.daoBeneficiary = new DAOBeneficiary();
        this.daoMission = new DAOMission();
        this.missionService = new MissionService();
    }

    /**
     * Creates a new interpreter in the system.
     * @throws AlreadyExistsException if the interpreter already exists in the database
     * @throws SQLException if the database could not be reached
     */
    public void createInterpreter(InterpreterCreationForm form) throws AlreadyExistsException, SQLException, ConnectionException {
        City city = new City(form.getCityDesignation(), form.getPostalCode());
        SQLWrap.callTransaction(new DAOCity()::create, city);

        Location location = new Location(
                form.getLocationDesignation(),
                city,
                form.getStreet(),
                form.getStreetNumber(),
                form.getBox() != null ? form.getBox() : 0
        );
        SQLWrap.callTransaction(new DAOLocation()::create, location);

        String plainPassword = form.getPassword();
        String hashedPassword = new BCryptPasswordEncoder().encode(plainPassword);

        Interpreter interpreter = new Interpreter(
                null,
                form.getFirstName(),
                form.getLastName(),
                form.getBirthDate(),
                hashedPassword,
                form.getEmail(),
                form.getPhoneNumber(),
                form.getHourQuotaWeek(),
                form.getHourQuotaYear(),
                form.getTransportMode(),
                new HashSet<>(),
                new HashSet<>(),
                location,
                new HashSet<>()
        );

        SQLWrap.callTransaction(daoInterpreter::create, interpreter);
    }

    /**
     * Promote an interpreter to Manager
     * @param id the interpreter's id
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void promoteInterpreter(int id) throws SQLException, ConnectionException {
        SQLWrap.callTransaction((ConsumerWithSQLException<Integer>) new DAOManager()::create, id);
    }

    /**
     * Deletes an interpreter from the system.
     * If the interpreter is the only one assigned to a mission, the mission is set to CANCELED.
     * Otherwise, the interpreter is automatically removed from the mission via ON DELETE CASCADE.
     * @param interpreter the interpreter to delete
     * @throws NoSuchElementException if the interpreter does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void deleteInterpreter(Interpreter interpreter) throws NoSuchElementException, SQLException {
        for (Mission mission : SQLWrap.call(daoMission::findAll)) {
            if (mission.getInterpreters() != null && mission.getInterpreters().contains(interpreter)) {
                if (mission.getInterpreters().size() == 1){
                    missionService.cancelMission(mission);
                }
            }
        }
        SQLWrap.callTransaction(daoInterpreter::delete, interpreter.getId());
    }

    /**
     * Updates an interpreter's information.
     * @param interpreter the interpreter to update
     * @param newInterpreter the new information to apply
     * @throws AlreadyExistsException if the updated interpreter already exists in the database
     * @throws NoSuchElementException if the interpreter does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void updateInterpreter(Interpreter interpreter, Interpreter newInterpreter) throws AlreadyExistsException, NoSuchElementException, SQLException {
        interpreter.setFirstName(newInterpreter.getFirstName());
        interpreter.setLastName(newInterpreter.getLastName());
        interpreter.setBirthDate(newInterpreter.getBirthDate());
        interpreter.setEmail(newInterpreter.getEmail());
        interpreter.setPhoneNumber(newInterpreter.getPhoneNumber());
        interpreter.setHourQuotaWeek(newInterpreter.getHourQuotaWeek());
        interpreter.setHourQuotaYear(newInterpreter.getHourQuotaYear());
        interpreter.setTransportMode(newInterpreter.getTransportMode());
        interpreter.setLocation(newInterpreter.getLocation());
        interpreter.setAcademicSkills(newInterpreter.getAcademicSkills());
        interpreter.setJobSkills(newInterpreter.getJobSkills());
        interpreter.setAvailability(newInterpreter.getAvailability());

        SQLWrap.callTransaction(daoInterpreter::update, interpreter);
    }

    /**
     * Creates an exceptional unavailability for an interpreter.
     * @param interpreter the interpreter for whom to create the unavailability
     * @param unavailability the unavailability to create
     * @throws AlreadyExistsException if the unavailability already exists in the database
     * @throws SQLException if the database could not be reached
     */
    public void createUnavailability(Interpreter interpreter, ExceptionalUnavailability unavailability) throws AlreadyExistsException, IllegalArgumentException, SQLException {
        SQLWrap.callTransaction(new DAOExceptionalUnavailability()::create, unavailability, interpreter);
    }

    /**
     * @return all interpreters present in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public List<Interpreter> getAllInterpreters() throws SQLException, ConnectionException {
        return new ArrayList<>(SQLWrap.call(new DAOInterpreter()::findAll));
    }

    /**
     * @param beneficiaryId a beneficiary's id
     * @return the interpreter the beneficiary refers to
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public Interpreter getAssignedInterpreter(int beneficiaryId) throws SQLException, ConnectionException {
        Beneficiary b = SQLWrap.call((FunctionWithSQLException<Integer, Beneficiary>) daoBeneficiary::find, beneficiaryId);
        if (b == null)
            return null;
        Interpreter ref = b.getInterpreterRef();
        if (ref == null)
            return null;
        return SQLWrap.call((FunctionWithSQLException<Integer, Interpreter>) daoInterpreter::find, ref.getId());
    }

    /**
     * Returns the list of interpreters available for a given time slot.
     * An interpreter is considered available if:
     * - they have no mission during that time slot
     * - they have no exceptional unavailability overlapping that time slot
     * - they have a base availability covering that time slot
     * @param timeSlot the time slot to check availability for, must be a PunctualTimeSlot
     * @return a List of available Interpreter for the given time slot
     * @throws SQLException if the database could not be reached
     * @throws IllegalArgumentException if the given TimeSlot is not a PunctualTimeSlot
     */
    public List<Interpreter> getAvailableInterpreters(TimeSlot timeSlot) throws SQLException {
        if (!(timeSlot instanceof PunctualTimeSlot)){
            throw new IllegalArgumentException("getAvailableInterpreters requiert un PunctualTimeSlot");
        }


        PunctualTimeSlot slot = (PunctualTimeSlot) timeSlot;

        Set<Interpreter> candidates = SQLWrap.call(daoInterpreter::findAvailable, slot.getStartDate().toLocalTime(), slot.getEndDate().toLocalTime(), slot.getStartDate().toLocalDate());

        List<Interpreter> available = new ArrayList<>();
        for (Interpreter interpreter : candidates) {
            if (!hasUnavailabilityConflict(interpreter, slot) && !hasMissionConflict(interpreter, slot)){
                available.add(interpreter);
            }
        }

        return available;
    }


    /**
     * Checks if an interpreter has an exceptional unavailability overlapping the given time slot.
     * @param interpreter the interpreter to check
     * @param slot the time slot to check
     * @return true if there is an overlapping unavailability
     */
    private boolean hasUnavailabilityConflict(Interpreter interpreter, PunctualTimeSlot slot) {
        if (interpreter.getUnavailability() == null){
            return false;
        }

        for (ExceptionalUnavailability unavailability : interpreter.getUnavailability()) {
            if (unavailability.getTimeSlot().overlaps(slot)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an interpreter has a mission conflicting with the given time slot.
     * @param interpreter the interpreter to check
     * @param slot the time slot to check
     * @return true if there is a conflicting mission
     * @throws SQLException if the database could not be reached
     */
    private boolean hasMissionConflict(Interpreter interpreter, PunctualTimeSlot slot) throws SQLException {
        Set<Mission> missions = SQLWrap.call(daoMission::getScheduleForDay, interpreter.getId(), slot.getStartDate().toLocalDate());
        for (Mission mission : missions) {
            if (mission.getTimeSlot() instanceof PunctualTimeSlot) {
                PunctualTimeSlot missionSlot = (PunctualTimeSlot) mission.getTimeSlot();
                if (missionSlot.overlaps(slot)){
                    return true;
                }

            } else if (mission.getTimeSlot() instanceof BaseTimeSlot) {
                BaseTimeSlot missionSlot = (BaseTimeSlot) mission.getTimeSlot();
                if (missionSlot.getStartTime().isBefore(slot.getEndDate().toLocalTime()) && missionSlot.getEndTime().isAfter(slot.getStartDate().toLocalTime())){
                    return true;
                }
            }
        }
        return false;
    }
}