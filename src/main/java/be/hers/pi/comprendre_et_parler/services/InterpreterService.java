package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.DTO.CreateInterpreterForm;
import be.hers.pi.comprendre_et_parler.DTO.CreateUnavailability;
import be.hers.pi.comprendre_et_parler.DTO.UpdateInterpreterForm;
import be.hers.pi.comprendre_et_parler.DTO.UserCredentials;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.ConsumerWithSQLException;
import be.hers.pi.comprendre_et_parler.services.wrappers.FunctionWithSQLException;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class InterpreterService {
    private final DAOInterpreter daoInterpreter = new DAOInterpreter();
    private final DAOBeneficiary daoBeneficiary = new DAOBeneficiary();
    private final DAOMission daoMission = new DAOMission();
    private final DAOExceptionalUnavailability daoUnavailability = new DAOExceptionalUnavailability();
    private final MissionService missionService = new MissionService();

    /**
     * Creates a new interpreter in the system.
     * @throws AlreadyExistsException if the interpreter already exists in the database
     * @throws SQLException if the database could not be reached
     */
    public UserCredentials createInterpreter(CreateInterpreterForm form) throws AlreadyExistsException, SQLException, ConnectionException {
        Location location = new Location(
                form.getLocationDesignation(),
                new CityService().getOneCity(form.getCityId()),
                form.getStreet(),
                form.getStreetNumber(),
                form.getBox() != null ? form.getBox() : 0
        );

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
                new HashSet<>(form.getAcademicSkillList()),
                new HashSet<>(form.getJobSkillList()),
                location,
                new HashSet<>()
        );

        SQLWrap.callTransaction(daoInterpreter::create, interpreter);
        return new UserCredentials(interpreter.getFirstName(), interpreter.getLogin(), plainPassword, interpreter.getEmail());
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
     * Demotes a manager to interpreter
     * @param id the manager's id
     * @throws NoSuchElementException if the manager does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if a database error occurs
     */
    public void demoteManager(int id) throws SQLException, ConnectionException, NoSuchElementException {
        SQLWrap.callTransaction(new DAOManager()::delete, id);
    }

    /**
     * Disables an interpreter account by setting its end date to today.
     * The account remains in the database but the user can no longer log in.
     * @param id the id of the interpreter to disable
     * @throws NoSuchElementException if the interpreter does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void disableInterpreter(int id) throws SQLException, NoSuchElementException {
        SQLWrap.callTransaction(new DAOAppliUser()::disableAccount, id);
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
        for (Mission mission : SQLWrap.call(daoMission::findByInterpreter, interpreter.getId())) {
            if (mission.getInterpreters().size() == 1) {
                missionService.cancelMission(mission);
            }
        }
        SQLWrap.callTransaction(daoInterpreter::delete, interpreter.getId());
    }

    /**
     * Updates an interpreter's information.
     * @param id the id of the Interpreter to update
     * @param interpreterForm the new information to apply
     * @throws AlreadyExistsException if the updated interpreter already exists in the database
     * @throws NoSuchElementException if the interpreter does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void updateInterpreter(int id, UpdateInterpreterForm interpreterForm) throws AlreadyExistsException, NoSuchElementException, SQLException {
        Interpreter interpreter = getOneInterpreter(id);
        interpreter.setFirstName(interpreterForm.getFirstName());
        interpreter.setLastName(interpreterForm.getLastName());
        interpreter.setEmail(interpreterForm.getEmail());
        interpreter.setBirthDate(interpreterForm.getBirthDate());
        interpreter.setPhoneNumber(interpreterForm.getPhoneNumber());
        interpreter.setTransportMode(interpreterForm.getTransportMode());
        Location location = new Location(
                interpreterForm.getLocationDesignation(),
                new CityService().getOneCity(interpreterForm.getCityId()),
                interpreterForm.getStreet(),
                interpreterForm.getStreetNumber(),
                interpreterForm.getBox() != null ? interpreterForm.getBox() : 0
        );
        interpreter.setLocation(location);

        SQLWrap.callTransaction((ConsumerWithSQLException<Interpreter>) daoInterpreter::update, interpreter);
    }

    /**
     * Creates an exceptional unavailability for an interpreter.
     * @param interpreter the interpreter for whom to create the unavailability
     * @param unavailability the unavailability to create
     * @throws AlreadyExistsException if the unavailability already exists in the database
     * @throws SQLException if the database could not be reached
     */
    public void createUnavailability(Interpreter interpreter, CreateUnavailability unavailability) throws AlreadyExistsException, IllegalArgumentException, SQLException {
        ExceptionalUnavailability newUnavailability = new ExceptionalUnavailability(unavailability.getReason(),
                new PunctualTimeSlot(unavailability.getStartDate(), unavailability.getEndDate()));
        SQLWrap.callTransaction(daoUnavailability::create, newUnavailability, interpreter);
        interpreter.addUnavailability(newUnavailability);
    }

    /**
     * Retrieve all Interpreters from the database.
     * @return every Interpreters present in database, sorted by their compareTo()
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public List<Interpreter> getAllInterpreters() throws SQLException, ConnectionException {
        List<Interpreter> allInterpreters = new ArrayList<>(SQLWrap.call(daoInterpreter::findAll));
        allInterpreters.sort(Interpreter::compareTo);
        return allInterpreters;
    }

    /**
     * Search for an interpreter in the database.
     * @return one interpreter present in database
     * @param id the id of the interpreter to find
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public Interpreter getOneInterpreter(int id) throws SQLException, ConnectionException {
        return SQLWrap.call(
                (FunctionWithSQLException<Integer, Interpreter>) daoInterpreter::find, id);
    }

    /**
     * Counts interpreters
     * @return the number of interpreters in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public int countInterpreters() throws SQLException, ConnectionException {
        return SQLWrap.call(daoInterpreter::count);
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

        List<Interpreter> allInterpreters = getAllInterpreters();

        List<Interpreter> available = new ArrayList<>();
        for (Interpreter interpreter : allInterpreters) {
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

    /**
     * Modifies an interpreter's unavailability slot
     * @param interpreter the interpreter to modify
     * @param idOldTimeSlot the ID of the unavailability's timeSlot to update
     * @param unavailability the object to replace it with
     * @throws NoSuchElementException if interpreter does not exist or does not possess oldUn in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if a database error occurs
     */
    public void updateUnavailability(Interpreter interpreter, int idOldTimeSlot, CreateUnavailability unavailability) throws SQLException, ConnectionException, NoSuchElementException {
        ExceptionalUnavailability newUn = new ExceptionalUnavailability(unavailability.getReason(),
                new PunctualTimeSlot(unavailability.getStartDate(), unavailability.getEndDate()));
        ExceptionalUnavailability oldUn = SQLWrap.callTransaction(daoUnavailability::find, interpreter.getId(), idOldTimeSlot);

        if (Objects.equals(oldUn, newUn)) return;

        if (oldUn.getTimeSlot().equals(newUn.getTimeSlot())) {
            SQLWrap.callTransaction(daoUnavailability::update, newUn, interpreter);
        }
        else {
            SQLWrap.callTransaction(
                    (Interpreter i, ExceptionalUnavailability oldEU, ExceptionalUnavailability newEU) -> {
                        daoUnavailability.delete(i.getId(), oldEU.getTimeSlot().getId());
                        daoUnavailability.create(newEU, i);
                    }, interpreter, oldUn, newUn
            );
        }
        interpreter.getUnavailability().remove(oldUn);
        interpreter.addUnavailability(newUn);
    }

    /**
     * Delete an interpreter's unavailability slot
     * @param interpreter the interpreter of the unavailability to delete
     * @param timeSlotId the ID of the time slot of the unavailability to delete
     * @throws NoSuchElementException if the interpreter does not exist or does not possess unavailability in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if a database error occurs
     */
    public void deleteUnavailability(Interpreter interpreter, int timeSlotId) throws SQLException, ConnectionException, NoSuchElementException {
        SQLWrap.callTransaction(
                (Interpreter i, Integer id) -> {
                    daoUnavailability.delete(i.getId(), id);
                    i.setUnavailability(daoUnavailability.findForInterpreter(i.getId()));
                }, interpreter, timeSlotId
        );
    }

    /**
     * Adds a job skill to an interpreter.
     * If the skill does not exist in the database, it is created first.
     * @param interpreter the interpreter to whom to add the skill
     * @param skill the job skill to add
     * @throws SQLException if the database could not be reached
     * @throws ConnectionException if the database could not be reached
     */
    public void addJobSkill(Interpreter interpreter, JobSkill skill) throws SQLException, ConnectionException {
        SQLWrap.callTransaction(daoInterpreter::createJobSkillLink, interpreter, skill);
    }

    /**
     * Adds a job skill to an interpreter.
     * If the skill does not exist in the database, it is created first.
     * @param interpreter the interpreter to whom to add the skill
     * @param skill the Academic skill to add
     * @throws SQLException if a database error occurs
     * @throws ConnectionException if the database could not be reached
     */
    public void addAcademicSkill(Interpreter interpreter, AcademicSkill skill) throws SQLException, ConnectionException {
        SQLWrap.callTransaction(daoInterpreter::createAcademicSkillLink, interpreter, skill);
    }

    /**
     * Modifies an interpreter's weekly and yearly quotas
     * @param interpreter the interpreter to modify
     * @param weekQuota the new weekly quota
     * @param yearQuota the new yearly quota
     * @throws NoSuchElementException if the interpreter does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if a database error occurs
     */
    public void updateQuota(Interpreter interpreter, int weekQuota, int yearQuota) throws SQLException, ConnectionException, NoSuchElementException {
        interpreter.setHourQuotaWeek(weekQuota);
        interpreter.setHourQuotaYear(yearQuota);
        SQLWrap.callTransaction(daoInterpreter::update, interpreter);
    }

    /**
     * Loads and sets the interpreters for a given mission.
     * Call this explicitly only when interpreters are needed,
     * to avoid unnecessary cascade loading on every getResult() call.
     * @param mission the mission to load interpreters for, must not be null
     * @post mission.getInterpreters() is populated with the interpreters linked to this mission, can be null if SQLException
     */
    public void loadInterpreters(Mission mission) {
        try {
            SQLWrap.callTransaction(
                    (ConsumerWithSQLException<Mission>) m -> m.setInterpreters(new DAOInterpreter().findByMission(m.getId())),
                    mission
            );
        }catch(SQLException e){
            mission.setInterpreters((null));
        }

    }
}