package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;
import be.hers.pi.comprendre_et_parler.DAOs.DAOMission;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConflictException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.exceptions.QuotaExceededException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MissionService {
    private final static DAOMission daoMission = new DAOMission();
    private final static DAOInterpreter daoInterpreter = new DAOInterpreter();
    private final static NotificationService notificationService = new NotificationService();

    /**
     * Returns a mission according to the given id.
     * @param id the id of the mission which we want
     * @return a Mission matching the id
     * @throws SQLException if the database could not be reached
     * @throws ConnectionException  if the connection to the database could not be established
     */
    public Mission getOneMission(int id)  throws SQLException, ConnectionException{
        Mission mission = SQLWrap.call(daoMission::find, id);
        return mission;
    }

    /**
     * Returns a list of missions filtered according to the given filter.
     * @param filter the filter to apply, each criterion is optional (null means no filter)
     * @return a List of Mission matching the filter
     * @throws SQLException if the database could not be reached
     * @throws ConnectionException if the connection to the database could not be established
     */
    public List<Mission> getByFilter(MissionFilter filter, LocalDateTime start, LocalDateTime end) throws SQLException, ConnectionException {
        return new ArrayList<>(SQLWrap.call(daoMission::getByFilter, filter, start, end));
    }

    /**
     * Return the list of missions for a given week, filtered according to the user's role.
     * @param user the user requesting the schedule (Manager, Interpreter or Beneficiary)
     * @param weekStart the date of any day within the target week;
     * @throws SQLException if the database could not be reached
     */
    public ArrayList<Mission> getMissionsForWeek(AppliUser user, LocalDate weekStart) throws SQLException {
        int yearNumber = weekStart.getYear();
        int weekNumber = weekStart.get(WeekFields.ISO.weekOfWeekBasedYear());
        Set<Mission> missions;

        if (user instanceof Manager)
            missions = SQLWrap.call(daoMission::getAllMissionsForWeek, yearNumber, weekNumber);
        else if(user instanceof Interpreter)
            missions = SQLWrap.call(daoMission::getScheduleForWeek, user.getId(), yearNumber, weekNumber);
        else
            missions = SQLWrap.call(daoMission::getScheduleForWeek, user.getId(), yearNumber, weekNumber);

        return new ArrayList<>(missions);
    }

    /**
     * Creates a mission with the status ACCEPTED
     * @param mission the mission to create, with interpreters and time slot already set
     * @throws ConflictException if an assigned interpreter or beneficiary has a schedule conflict
     * @throws SQLException if any database error occurs
     */
    public void createMission(Mission mission) throws ConflictException, SQLException {
        mission.setStateOfMission(MissionState.ACCEPTED);
        try {
            SQLWrap.callTransaction(daoMission::create, mission);
        }
        catch (AlreadyExistsException e) {
            throw new ConflictException("Conflit d'horaire avec " + describeConflict(e));
        }
    }

    /**
     * Creates a mission with the status PENDING.
     * @param mission the mission to create, with beneficiary and time slot already set
     * @throws ConflictException if the new request overlaps with an existing mission
     * @throws SQLException if the database could not be reached
     */
    public void createRequest(Mission mission) throws ConflictException, SQLException {
        mission.setStateOfMission(MissionState.PENDING);
        try {
            SQLWrap.callTransaction(daoMission::create, mission);
        }
        catch (AlreadyExistsException e) {
            throw new ConflictException("Conflit d'horaire avec " + describeConflict(e));
        }
    }

    /**
     * Checks that an interpreter has no schedule conflict with the given time slot.
     * @param interpreter the interpreter to check
     * @param slot the time slot of the mission
     * @throws ConflictException if the interpreter already has a mission at the same time
     * @throws SQLException if the database could not be reached
     */
    private void checkInterpreterConflict(Interpreter interpreter, TimeSlot slot) throws ConflictException, SQLException {
        for (LocalDate date : getDates(slot)) {
            for (Mission existing : SQLWrap.call(daoMission::getScheduleForDay, interpreter.getId(), date)) {
                if (hasConflict(slot, existing.getTimeSlot())) {
                    throw new ConflictException("Conflit d'horaire pour " + interpreter.getId());
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

            LocalDate cursorStart = baseTimeSlot.getStartDate();
            while (!cursorStart.getDayOfWeek().equals(baseTimeSlot.getDay()))
                cursorStart = cursorStart.plusDays(1);

            while (!cursorStart.isAfter(baseTimeSlot.getEndDate())) {
                dates.add(cursorStart);
                cursorStart = cursorStart.plusWeeks(1);
            }
        } else {
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
        SQLWrap.callTransaction(daoMission::update, mission);
        notificationService.notifyCancellation(mission);
    }

    /**
     * Reports a delay for a mission by sending an email notification to all concerned parties.
     * @param mission the mission for which to report a delay
     * @param delayInfo the delay information to send
     * @throws SQLException if the database could not be reached
     */
    public void reportDelay(Mission mission, String delayInfo) throws SQLException {
        notificationService.notifyDelay(mission, delayInfo);
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
                checkQuota(interpreter, mission.getTimeSlot());
                checkInterpreterConflict(interpreter, mission.getTimeSlot());
            }

        mission.setStateOfMission(MissionState.ACCEPTED);
        SQLWrap.callTransaction(daoMission::update,mission);
    }

    /**
     * Refuses a pending request by setting its status to DENIED and notifying the concerned parties.
     * @param mission the mission to refuse
     * @throws NoSuchElementException if the mission does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void refuseRequest(Mission mission) throws NoSuchElementException, SQLException {
        mission.setStateOfMission(MissionState.DENIED);
        SQLWrap.callTransaction(daoMission::update, mission);
        notificationService.notifyRefusal(mission);
    }

    /**
     * Updates an existing mission with new information.
     * If the time slot has changed, checks for schedule conflicts with assigned interpreters.
     * @param mission the existing mission to update
     * @param newMission the new mission information to apply
     * @throws ConflictException if an assigned interpreter or beneficiary has a schedule conflict
     * @throws NoSuchElementException if the mission does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void updateMission(Mission mission, Mission newMission) throws ConflictException, NoSuchElementException, SQLException {
        try {
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

            SQLWrap.callTransaction(daoMission::update, mission);
        }
        catch (AlreadyExistsException e) {
            throw new ConflictException("Conflit d'horaire avec " + describeConflict(e));
        }
    }

    /**
     * Checks that an interpreter's hour quota is not exceeded by the given time slot.
     * @param interpreter the interpreter to check
     * @param slot the time slot of the mission to add
     * @throws QuotaExceededException if the weekly or yearly quota would be exceeded
     * @throws SQLException if the database could not be reached
     * @throws IllegalArgumentException if ts is an unknown TimeSlot subtype
     */
    private void checkQuota(Interpreter interpreter, TimeSlot slot) throws QuotaExceededException, IllegalArgumentException, SQLException {
        double newMissionHours = calculateHours(slot);
        LocalDate date;
        if (slot instanceof PunctualTimeSlot)
            date = ((PunctualTimeSlot) slot).getStartDate().toLocalDate();
        else
            date = ((BaseTimeSlot) slot).getStartDate();

        LocalDate weekStart = date.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = date.with(DayOfWeek.SUNDAY);
        LocalDate yearStart = LocalDate.of(date.getYear(), 1, 1);
        LocalDate yearEnd = LocalDate.of(date.getYear(), 12, 31);

        double hoursThisWeek = SQLWrap.call(daoInterpreter::getWorkedHours, interpreter.getId(), weekStart, weekEnd);
        double hoursThisYear = SQLWrap.call(daoInterpreter::getWorkedHours, interpreter.getId(), yearStart, yearEnd);

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
    private double calculateHours(TimeSlot ts) throws IllegalArgumentException{
        if (ts instanceof PunctualTimeSlot) {
            PunctualTimeSlot pts = (PunctualTimeSlot) ts;
            return Duration.between(pts.getStartDate(), pts.getEndDate()).toMinutes() /60.0;
        }
        if (ts instanceof BaseTimeSlot) {
            BaseTimeSlot bts = (BaseTimeSlot) ts;
            return Duration.between(bts.getStartTime(), bts.getEndTime()).toMinutes() / 60.0;
        }
        throw new IllegalArgumentException("Unknown TimeSlot subtype : " + ts.getClass().getSimpleName());
    }


    /**
     * Accepts a pending request by setting its status to ACCEPTED,
     * bypassing the hour quota check but still verifying schedule conflicts.
     * Use this when the interpreter's quota is exceeded but the manager
     * explicitly chooses to accept the mission anyway.
     * @param mission the mission to accept, with interpreters already set
     * @throws ConflictException if an assigned interpreter or beneficiary has a schedule conflict
     * @throws SQLException if the database could not be reached
     * @pre mission.getInterpreters() is not null and not empty
     * @post mission.getStateOfMission() == MissionState.ACCEPTED
     */
    public void acceptRequestDespiteQuota(Mission mission) throws ConflictException, SQLException {
        mission.setStateOfMission(MissionState.ACCEPTED);
        try {
            SQLWrap.callTransaction(daoMission::update, mission);
        }
        catch (AlreadyExistsException e) {
            throw new ConflictException("Conflit d'horaire avec " + describeConflict(e));
        }
    }

    /**
     * Checks quota for all interpreters of a mission without throwing.
     * @param mission the mission to check
     * @return a warning message if quota is exceeded, empty string otherwise
     * @throws SQLException if the database could not be reached
     */
    public String checkQuotaWarning(Mission mission) throws SQLException {
        if (mission.getInterpreters() == null) return "";
        StringBuilder warnings = new StringBuilder();
        for (Interpreter interpreter : mission.getInterpreters()) {
            try {
                checkQuota(interpreter, mission.getTimeSlot());
            } catch (QuotaExceededException e) {
                if (!warnings.isEmpty()) warnings.append("\n");
                warnings.append("⚠ ")
                        .append(interpreter.getFirstName())
                        .append(" ")
                        .append(interpreter.getLastName())
                        .append(" dépasse son quota d'heures !");
            }
        }
        return warnings.toString();
    }

    /**
     * Builds a human-readable description of a mission for use in conflict messages.
     * @param mission the mission to describe
     * @return a String describing the mission's subject, date/time and beneficiary (if any)
     */
    private String describeMission(Mission mission) {
        StringBuilder sb = new StringBuilder("\"").append(mission.getSubject()).append("\"");

        TimeSlot ts = mission.getTimeSlot();
        if (ts instanceof PunctualTimeSlot pts) {
            sb.append(" le ").append(pts.getStartDate().toLocalDate())
                    .append(" de ").append(pts.getStartDate().toLocalTime())
                    .append(" à ").append(pts.getEndDate().toLocalTime());
        } else if (ts instanceof BaseTimeSlot bts) {
            sb.append(" le ").append(bts.getDay())
                    .append(" de ").append(bts.getStartTime())
                    .append(" à ").append(bts.getEndTime());
        }

        if (mission.getBeneficiary() != null) {
            sb.append(" (bénéficiaire : ")
                    .append(mission.getBeneficiary().getFirstName()).append(" ").append(mission.getBeneficiary().getLastName())
                    .append(")");
        }

        return sb.toString();
    }

    /**
     * Fetches and describes the mission that caused an AlreadyExistsException raised by the DAO,
     * whose message contains the conflicting mission's id.
     * @param e the AlreadyExistsException raised by the DAO
     * @return a description of the conflicting mission, or a generic fallback if it could not be retrieved
     */
    private String describeConflict(AlreadyExistsException e) {
        try {
            int conflictId = Integer.parseInt(e.getMessage());
            Mission conflict = daoMission.find(conflictId);
            return conflict != null ? describeMission(conflict) : "une autre mission";
        } catch (Exception ex) {
            return "une autre mission";
        }
    }
}
