package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOMission;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConflictException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.exceptions.QuotaExceededException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class MissionService {

    private final DAOMission daoMission;

    public MissionService() {
        this.daoMission = new DAOMission();
    }

    /**
     * Returns a list of missions filtered according to the given filter.
     * @param filter the filter to apply, each criterion is optional (null means no filter)
     * @return a List of Mission matching the filter
     * @throws SQLException if the database could not be reached
     * @throws ConnectionException if the connection to the database could not be established
     */
    public List<Mission> getByFilter(MissionFilter filter) throws SQLException, ConnectionException {
        Set<Mission> all = SQLWrap.call(daoMission::findAll);
        return all.stream()
                .filter(m -> filter.getBeneficiary() == null ||
                        m.getBeneficiary().equals(filter.getBeneficiary()))
                .filter(m -> filter.getInterpreter() == null ||
                        m.getInterpreters().contains(filter.getInterpreter()))
                .filter(m -> filter.getJobSkill() == null ||
                        m.getJobSkill().equals(filter.getJobSkill()))
                .filter(m -> filter.getAcademicSkill() == null ||
                        m.getAcademicSkill().equals(filter.getAcademicSkill()))
                .filter(m -> filter.getLocation() == null ||
                        m.getLocation().equals(filter.getLocation()))
                .filter(m -> filter.getMinImportance() == null ||
                        m.getImportance() >= filter.getMinImportance())
                .filter(m -> filter.getStateOfMission() == null ||
                        m.getStateOfMission().equals(filter.getStateOfMission()))
                .collect(Collectors.toList());
    }


    /**
     * Return the list of missions for a given week, filtered according to the user's role.
     * @param user the user requesting the schedule (Manager, Interpreter or Beneficiary)
     * @param weekStart the date of any day within the target week;
     * @throws SQLException if the database could not be reached
     */
    public ArrayList<Mission> getMissionsForWeek(AppliUser user, LocalDate weekStart) throws SQLException {

        int yearNumber = weekStart.getYear();
        int weekNumber = weekStart.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());

        Set<Mission> missions;

        if (user instanceof Manager) {
            missions = daoMission.getAllMissionsForWeek(yearNumber, weekNumber);
        }
        else {
            missions = daoMission.getScheduleForWeek(user.getId(), yearNumber, weekNumber);
        }

        return new ArrayList<>(missions);
    }

    /**
     * Creates a mission with the status ACCEPTED
     * @param mission the mission to create, with interpreters and time slot already set
     * @throws ConflictException if an assigned interpreter has a schedule conflict
     * @throws AlreadyExistsException if the mission already exists in the database
     * @throws SQLException if the database could not be reached
     */
    public void createMission(Mission mission) throws ConflictException, AlreadyExistsException, SQLException {
        if (mission.getInterpreters() != null){
            for (Interpreter interpreter : mission.getInterpreters()){
                checkInterpreterConflict(interpreter, mission.getTimeSlot());
            }
        }

        mission.setStateOfMission(MissionState.ACCEPTED);
        daoMission.create(mission);
    }


    /**
     * Creates a mission with the status PENDING.
     * @param mission the mission to create, with beneficiary and time slot already set
     * @throws AlreadyExistsException if the mission already exists in the database
     * @throws SQLException if the database could not be reached
     */
    public void createRequest(Mission mission) throws AlreadyExistsException, SQLException {
        mission.setStateOfMission(MissionState.PENDING);
        daoMission.create(mission);
    }


    /**
     * Checks that an interpreter has no schedule conflict with the given time slot.
     * @param interpreter the interpreter to check
     * @param slot the time slot of the mission
     * @throws ConflictException if the interpreter already has a mission at the same time
     * @throws SQLException if the database could not be reached
     */
    private void checkInterpreterConflict(Interpreter interpreter, TimeSlot slot) throws ConflictException, SQLException {
        for (LocalDate date : getDates(slot)){
            for (Mission existing : daoMission.getScheduleForDay(interpreter.getId(), date)){
                if (hasConflict(slot, existing.getTimeSlot())){
                    throw new ConflictException("Conflit d'hiraire pour " + interpreter.getId());
                }

            }


        }

    }

    /**
     * Returns all dates covered by a TimeSlot.
     * @param ts the TimeSlot to extract dates from
     * @return a List of LocalDate covered by the TimeSlot
     * @throws IllegalArgumentException if ts is an unknown TimeSlot subtype, or if startDate is after endDate for a BaseTimeSlot
     */
    private List<LocalDate> getDates(TimeSlot ts) {
        List<LocalDate> dates = new ArrayList<>();

        if (ts instanceof PunctualTimeSlot) {
            PunctualTimeSlot punctualTimeSlot = (PunctualTimeSlot) ts;
            dates.add(punctualTimeSlot.getStartDate().toLocalDate());

        } else if (ts instanceof BaseTimeSlot) {
            BaseTimeSlot baseTimeSlot = (BaseTimeSlot) ts;

            if (baseTimeSlot.getStartDate().isAfter(baseTimeSlot.getEndDate()))
                throw new IllegalArgumentException("startDate est après endDate");

            LocalDate cursorStart = baseTimeSlot.getStartDate();
            while (!cursorStart.getDayOfWeek().equals(baseTimeSlot.getDay()))
                cursorStart = cursorStart.plusDays(1);

            while (!cursorStart.isAfter(baseTimeSlot.getEndDate())) {
                dates.add(cursorStart);
                cursorStart = cursorStart.plusWeeks(1);
            }
        }
        else {
            throw new IllegalArgumentException("Sous type Inconnu");
        }

        return dates;
    }

    /**
     * Checks if two TimeSlots are in conflict.
     * @param firstTS the first TimeSlot
     * @param secondTS the second TimeSlot
     * @return true if the two TimeSlots overlap, false otherwise
     * @throws IllegalArgumentException if the two TimeSlots are not of the same subtype
     */
    private boolean hasConflict(TimeSlot firstTS, TimeSlot secondTS) throws IllegalArgumentException {
        if (firstTS instanceof PunctualTimeSlot && secondTS instanceof PunctualTimeSlot)
            return ((PunctualTimeSlot) firstTS).overlaps((PunctualTimeSlot) secondTS);
        if (firstTS instanceof BaseTimeSlot && secondTS instanceof BaseTimeSlot)
            return ((BaseTimeSlot) firstTS).overlaps((BaseTimeSlot) secondTS);
        throw new IllegalArgumentException("Les deux TimeSlots ne sont pas du même sous-type");
    }

    /**
     * Cancels a mission by setting its status to CANCELED and notifying the interpreter and beneficiary.
     * @param mission the mission to cancel
     * @throws NoSuchElementException if the mission does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void cancelMission(Mission mission) throws NoSuchElementException, SQLException {
        mission.setStateOfMission(MissionState.CANCELED);
        daoMission.update(mission);
        // TODO : notifier l'interprète et le bénéficiaire via NotificationService
    }

    /**
     * Reports a delay for a mission by sending an email notification to all concerned parties.
     * @param mission the mission for which to report a delay
     * @param delayInfo the delay information to send
     * @throws SQLException if the database could not be reached
     */
    public void reportDelay(Mission mission, String delayInfo) throws SQLException {
        // TODO : notifier l'interprète et le bénéficiaire via NotificationService
    }

    /**
     * Accepts a pending request by setting its status to ACCEPTED
     * after checking the interpreter's availability and hour quota.
     * @param mission the mission to accept
     * @throws ConflictException if the interpreter has a schedule conflict
     * @throws QuotaExceededException if the interpreter's hour quota is exceeded
     * @throws AlreadyExistsException if the mission already exists in the database
     * @throws SQLException if the database could not be reached
     */
    public void acceptRequest(Mission mission) throws ConflictException, QuotaExceededException, AlreadyExistsException, SQLException {
        if (mission.getInterpreters() != null)
            for (Interpreter interpreter : mission.getInterpreters()) {
                checkInterpreterConflict(interpreter, mission.getTimeSlot());
                checkQuota(interpreter, mission.getTimeSlot());
            }

        mission.setStateOfMission(MissionState.ACCEPTED);
        daoMission.update(mission);
    }

    /**
     * Refuses a pending request by setting its status to DENIED and notifying the concerned parties.
     * @param mission the mission to refuse
     * @throws NoSuchElementException if the mission does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void refuseRequest(Mission mission) throws NoSuchElementException, SQLException {
        mission.setStateOfMission(MissionState.DENIED);
        daoMission.update(mission);
        // TODO : notifier l'interprète et le bénéficiaire via NotificationService
    }

    /**
     * Updates an existing mission with new information.
     * If the time slot has changed, checks for schedule conflicts with assigned interpreters.
     * @param mission the existing mission to update
     * @param newMission the new mission information to apply
     * @throws ConflictException if an interpreter has a schedule conflict with the new time slot
     * @throws AlreadyExistsException if the updated mission already exists in the database
     * @throws NoSuchElementException if the mission does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void updateMission(Mission mission, Mission newMission) throws ConflictException, AlreadyExistsException, NoSuchElementException, SQLException {
        if (!mission.getTimeSlot().equals(newMission.getTimeSlot()))
            if (newMission.getInterpreters() != null)
                for (Interpreter interpreter : newMission.getInterpreters())
                    checkInterpreterConflict(interpreter, newMission.getTimeSlot());

        mission.setSubject(newMission.getSubject());
        mission.setCommentary(newMission.getCommentary());
        mission.setTimeSlot(newMission.getTimeSlot());
        mission.setLocation(newMission.getLocation());
        mission.setRoom(newMission.getRoom());
        mission.setImportance(newMission.getImportance());
        mission.setInterpreters(newMission.getInterpreters());
        mission.setJobSkill(newMission.getJobSkill());
        mission.setAcademicSkill(newMission.getAcademicSkill());
        mission.setBeneficiary(newMission.getBeneficiary());

        daoMission.update(mission);
    }

    /**
     * Checks that an interpreter's hour quota is not exceeded by the given time slot.
     * @param interpreter the interpreter to check
     * @param slot the time slot of the mission to add
     * @throws QuotaExceededException if the weekly or yearly quota would be exceeded
     * @throws SQLException if the database could not be reached
     */
    private void checkQuota(Interpreter interpreter, TimeSlot slot) throws QuotaExceededException, SQLException {
        double newMissionHours = calculateHours(slot);
        double hoursThisWeek = calculateAssignedHoursForWeek(interpreter, slot);
        double hoursThisYear = calculateAssignedHoursForYear(interpreter, slot);

        if (hoursThisWeek + newMissionHours > interpreter.getHourQuotaWeek())
            throw new QuotaExceededException("Le quota hebdomadaire de l'interprète " + interpreter.getId() + " est dépassé");
        if (hoursThisYear + newMissionHours > interpreter.getHourQuotaYear())
            throw new QuotaExceededException("Le quota annuel de l'interprète " + interpreter.getId() + " est dépassé");
    }

    /**
     * Calculates the total duration in hours of a TimeSlot.
     * For a PunctualTimeSlot, returns the duration of the single slot.
     * For a BaseTimeSlot, returns the duration of one occurrence.
     * @param ts the TimeSlot to calculate the duration of
     * @return the duration in hours
     * @throws IllegalArgumentException if ts is an unknown TimeSlot subtype
     */
    private double calculateHours(TimeSlot ts) {
        if (ts instanceof PunctualTimeSlot) {
            PunctualTimeSlot pts = (PunctualTimeSlot) ts;
            return java.time.Duration.between(pts.getStartDate(), pts.getEndDate()).toMinutes();
        }
        if (ts instanceof BaseTimeSlot) {
            BaseTimeSlot bts = (BaseTimeSlot) ts;
            return java.time.Duration.between(bts.getStartTime(), bts.getEndTime()).toMinutes();
        }
        throw new IllegalArgumentException("Unknown TimeSlot subtype : " + ts.getClass().getSimpleName());
    }

    /**
     * Calculates the total hours already assigned to an interpreter for the week of the given time slot.
     * @param interpreter the interpreter to check
     * @param slot the time slot used to determine the week
     * @return the total hours assigned for that week
     * @throws SQLException if the database could not be reached
     */
    private double calculateAssignedHoursForWeek(Interpreter interpreter, TimeSlot slot) throws SQLException {
        LocalDate date;

        if (slot instanceof PunctualTimeSlot) {
            PunctualTimeSlot punctualTimeSlot = (PunctualTimeSlot) slot;
            date = punctualTimeSlot.getStartDate().toLocalDate();
        } else {
            BaseTimeSlot baseTimeSlot = (BaseTimeSlot) slot;
            date = baseTimeSlot.getStartDate();
        }

        int year = date.getYear();
        int week = date.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());

        double total = 0;
        for (Mission m : daoMission.getScheduleForWeek(interpreter.getId(), year, week))
            total += calculateHours(m.getTimeSlot());
        return total;
    }

    /**
     * Calculates the total hours already assigned to an interpreter for the year of the given time slot.
     * @param interpreter the interpreter to check
     * @param slot the time slot used to determine the year
     * @return the total hours assigned for that year
     * @throws SQLException if the database could not be reached
     */
    private double calculateAssignedHoursForYear(Interpreter interpreter, TimeSlot slot) throws SQLException {

        LocalDate date;

        if (slot instanceof PunctualTimeSlot) {
            PunctualTimeSlot punctualTimeSlot = (PunctualTimeSlot) slot;
            date = punctualTimeSlot.getStartDate().toLocalDate();
        } else {
            BaseTimeSlot baseTimeSlot = (BaseTimeSlot) slot;
            date = baseTimeSlot.getStartDate();
        }

        int year = date.getYear();

        double total = 0;

        for (int week = 1; week <= 52; week++) {

            for (Mission mission : daoMission.getScheduleForWeek(interpreter.getId(), year, week)) {
                total += calculateHours(mission.getTimeSlot());
            }
        }

        return total;
    }

}
