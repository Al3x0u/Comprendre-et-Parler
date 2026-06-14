package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConflictException;
import be.hers.pi.comprendre_et_parler.exceptions.QuotaExceededException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;


import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/horaire")
public class ScheduleController {
    private final static MissionService missionService = new MissionService();
    private final static InterpreterService interpreterService  = new InterpreterService();
    private final static BeneficiaryService beneficiaryService  = new BeneficiaryService();
    private final static JobSkillService jobSkillService = new JobSkillService();
    private final static AcademicSkillService academicSkillService = new AcademicSkillService();
    private final static CityService cityService = new CityService();
    private final static LocationService locationService = new LocationService();
    private final static PunctualTimeSlotService punctualTimeSlotService = new PunctualTimeSlotService();

    private static final String COLOR_ACCEPTED = "#40c057";
    private static final String COLOR_PENDING = "#fab005";
    private static final String COLOR_REFUSED = "#fa5252";
    private static final String COLOR_DEFAULT = "#adb5bd";

    /**
     * Display the schedule page
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the schedule view or redirect to the connection if not authenticated
     */
    @GetMapping
    public String showSchedule(HttpSession session, Model model)  {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");

            if (user instanceof Manager) {
                model.addAttribute("userRole", "MANAGER");
            } else if (user instanceof Interpreter) {
                model.addAttribute("userRole", "INTERPRETER");
            } else if (user instanceof Beneficiary) {
                model.addAttribute("userRole", "BENEFICIARY");
            }

            LocalDate today = LocalDate.now();

            List<Mission> missions = missionService.getMissionsForWeek(user, today);

            List<Map<String, String>> events = convertMissionsToEvents(missions);
            Set<Beneficiary> beneficiaries = new HashSet<>(beneficiaryService.getAllBeneficiaries());
            List<Interpreter> interpreters = interpreterService.getAllInterpreters();


            if (user instanceof Manager) {
                List<Map<String, String>> filterUsers = new ArrayList<>();
                for (Interpreter interp : interpreters) {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("id", String.valueOf(interp.getId()));
                    entry.put("fullName", interp.getFirstName() + " " + interp.getLastName());
                    entry.put("role", interp instanceof Manager ? "MANAGER" : "INTERPRETER");
                    filterUsers.add(entry);
                }
                for (Beneficiary bene : beneficiaries) {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("id", String.valueOf(bene.getId()));
                    entry.put("fullName", bene.getFirstName() + " " + bene.getLastName());
                    entry.put("role", "BENEFICIARY");
                    filterUsers.add(entry);
                }
                filterUsers.sort(Comparator.comparing(e -> e.get("fullName")));
                model.addAttribute("filterUsers", filterUsers);



            } else if (user instanceof Interpreter interpreter) {
                Set<Beneficiary> refBeneficiaries = beneficiaryService.getBeneficiariesOf(interpreter.getId());
                model.addAttribute("beneficiaries", refBeneficiaries);

            }

            String userFullName = user.getFirstName() + " " + user.getLastName();
            model.addAttribute("userFullName", userFullName);
            ObjectMapper mapper = new ObjectMapper();
            model.addAttribute("events", mapper.writeValueAsString(events));
            model.addAttribute("interpreters", interpreters);

            if (user instanceof Manager) {
                model.addAttribute("allBeneficiaries", new ArrayList<>(beneficiaries));
            }
            model.addAttribute("professionalSkills", jobSkillService.getAllJobSkills());
            model.addAttribute("academicSkills", academicSkillService.getAllAcademicSkills());

            List<String> timeSlots = new ArrayList<>();
            for (int h = 8; h <= 22; h++) {
                timeSlots.add(String.format("%02d:00", h));
                if (h < 22) timeSlots.add(String.format("%02d:30", h));
            }
            model.addAttribute("timeSlots", timeSlots);

            List<City> allCities = new ArrayList<>(cityService.getAllCities());
            allCities.sort(City::compareTo);
            model.addAttribute("allCities", allCities);

        }catch(Exception e){
            e.printStackTrace();
            model.addAttribute("loadError", true);
        }
        return "schedule";
    }

    /**
     * Create a new interpretation request from a beneficiary
     * @param payload the request body containing mission details (title, date, times, location, etc.)
     * @param session the current HTTP session
     * @return 200 if created, 400 if missing fields or invalid times, 403 if not a beneficiary, 500 on error
     */
    @PostMapping("/requetes")
    @ResponseBody
    public ResponseEntity<String> createRequest(@RequestBody Map<String, String> payload, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Beneficiary beneficiary)) {
                return ResponseEntity.status(403).body("Accès refusé.");
            }

            String title = payload.get("title");
            String date = payload.get("date");
            String startTime = payload.get("startTime");
            String endTime = payload.get("endTime");
            String locationDesignation = payload.get("locationDesignation");
            String cityName = payload.get("city");
            int postalCode = 0;
            try{
                postalCode =  Integer.parseInt(payload.get("postalCode"));
            }catch(Exception e){
                e.printStackTrace();
                postalCode = 0;
            }
            String street = payload.get("street");
            String streetNumber = payload.get("streetNumber");
            String boxStr = payload.get("box");
            int box = 0;
            if (boxStr != null && !boxStr.isBlank()) {
                try { box = Integer.parseInt(boxStr); } catch (Exception ignored) {}
            }

            String comment = payload.get("comment");
            String importanceRaw = payload.get("importance");

            if (title == null || title.isBlank() || date == null || startTime == null || endTime == null || cityName == null || cityName.isBlank() ||  street == null || street.isBlank()) {
                return ResponseEntity.badRequest().body("Champs requis manquants.");
            }

            int importance = 0;
            if (importanceRaw != null && !importanceRaw.isBlank()) {
                importance = Integer.parseInt(importanceRaw);
            } else {
                importance = 0;
            }

            LocalDate localDate = LocalDate.parse(date);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            if (!end.isAfter(start)) {
                return ResponseEntity.badRequest().body("L'heure de fin doit être après l'heure de début.");
            }

            City city = new City(cityName, postalCode);
            Location location = new Location(locationDesignation, city, street, streetNumber, box);

            PunctualTimeSlot slot = new PunctualTimeSlot(
                    LocalDateTime.of(localDate, start),
                    LocalDateTime.of(localDate, end)
            );

            Mission mission = new Mission();
            mission.setSubject(title);
            mission.setCommentary(comment);
            mission.setTimeSlot(slot);
            mission.setLocation(location);
            mission.setImportance(importance);
            mission.setBeneficiary(beneficiary);
            mission.setInterpreters(Set.of());

            String room = payload.get("room");
            if (room != null && !room.isBlank()) {
                mission.setRoom(room);
            }

            String academicSkillIdStr = payload.get("academicSkillId");
            if (academicSkillIdStr != null && !academicSkillIdStr.isBlank()) {
                try {
                    int skillId = Integer.parseInt(academicSkillIdStr);
                    mission.setAcademicSkill(academicSkillService.getAllAcademicSkills()
                            .stream()
                            .filter(s -> s.getId() == skillId)
                            .findFirst()
                            .orElse(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String jobSkillIdStr = payload.get("jobSkillId");
            if (jobSkillIdStr != null && !jobSkillIdStr.isBlank()) {
                try {
                    int skillId = Integer.parseInt(jobSkillIdStr);
                    mission.setJobSkill(jobSkillService.getAllJobSkills().stream()
                            .filter(s -> s.getId() == skillId)
                            .findFirst()
                            .orElse(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            missionService.createRequest(mission);

            return ResponseEntity.ok("Demande créée.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de la création de la demande. Veuillez réessayer.");
        }
    }

    /**
     * Checks whether accepting a mission would exceed the interpreter's hour quota.
     * @param id the mission ID
     * @param body the request body containing the interpreter ID
     * @param session the current HTTP session
     * @return 200 with a warning message if quota exceeded, empty string if ok, 400 if no interpreter, 500 on error
     */
    @PostMapping("/missions/{id}/verifier-quota")
    @ResponseBody
    public ResponseEntity<?> checkQuota(@PathVariable int id, @RequestBody Map<String, String> body, HttpSession session) {
        try {
            String interpreterIdStr = body.get("interpreterId");
            if (interpreterIdStr == null || interpreterIdStr.isBlank())
                return ResponseEntity.badRequest().body("Aucun interprète sélectionné");

            Mission mission = missionService.getOneMission(id);
            Interpreter interpreter = interpreterService.getOneInterpreter(Integer.parseInt(interpreterIdStr));
            Set<Interpreter> interpreters = new HashSet<>();
            interpreters.add(interpreter);
            mission.setInterpreters(interpreters);

            String warning = missionService.checkQuotaWarning(mission);
            return ResponseEntity.ok().body(warning);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur");
        }
    }

    /**
     * Returns the list of interpreters available for a given mission's time slot.
     * @param id the mission ID
     * @param session the current HTTP session
     * @return 200 with the list of available interpreters, 403 if not a manager, 500 on error
     */
    @GetMapping("/missions/{id}/interpretes-disponibles")
    @ResponseBody
    public ResponseEntity<?> getAvailableInterpreters(@PathVariable int id, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");

            Mission mission = missionService.getOneMission(id);
            List<Interpreter> available = interpreterService.getAvailableInterpreters(mission.getTimeSlot());

            List<Map<String, String>> result = new ArrayList<>();
            for (Interpreter i : available) {
                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(i.getId()));
                map.put("name", i.getFirstName() + " " + i.getLastName());
                result.add(map);
            }
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur");
        }
    }

    /**
     * Accept a pending mission and assign an interpreter to it
     * @param id the mission ID
     * @param body the request body containing the interpreter ID
     * @param session the current HTTP session
     * @return 200 if accepted, 400 if no interpreter selected, 403 if not a manager, 500 on error
     */
    @PostMapping("/missions/{id}/accepter")
    @ResponseBody
    public ResponseEntity<?> acceptMission(@PathVariable int id, @RequestBody Map<String, String> body, HttpSession session) {
        Mission mission = null;
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            String interpreterIdStr = body.get("interpreterId");
            if (interpreterIdStr == null || interpreterIdStr.isBlank()) {
                return ResponseEntity.badRequest().body("Aucun interprète sélectionné");
            }

            mission = missionService.getOneMission(id);
            Interpreter interpreter = interpreterService.getOneInterpreter(Integer.parseInt(interpreterIdStr));

            Set<Interpreter> interpreters = new HashSet<>();
            interpreters.add(interpreter);
            mission.setInterpreters(interpreters);

            missionService.acceptRequest(mission);
            return ResponseEntity.ok().body("ok");

        } catch (QuotaExceededException e) {
            try {
                missionService.acceptRequestDespiteQuota(mission);
                return ResponseEntity.ok().body("warning:" + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'acceptation malgré le quota.");
            }
        }catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("conflict:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'acceptation de la mission.");
        }
    }

    /**
     * Refuse a pending mission
     * @param id the mission ID
     * @param session the current HTTP session
     * @return 200 if refused, 500 on error
     */
    @PostMapping("/missions/{id}/refuser")
    @ResponseBody
    public ResponseEntity<?> refuseMission(@PathVariable int id, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }
            Mission mission = missionService.getOneMission(id);
            missionService.refuseRequest(mission);

            return ResponseEntity.ok().build();

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du refus de la mission. Veuillez réessayer.");
        }

    }

    /**
     * Cancel a mission. A manager can cancel any mission; a beneficiary can only cancel
     * their own request while it is still pending. Concerned parties are notified.
     * @param id the mission ID
     * @param session the current HTTP session
     * @return 200 if cancelled, 403 if not allowed, 500 on error
     */
    @PostMapping("/missions/{id}/annuler")
    @ResponseBody
    public ResponseEntity<?> cancelMission(@PathVariable int id, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager) && !(user instanceof Beneficiary)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            Mission mission = missionService.getOneMission(id);

            if (user instanceof Beneficiary) {
                boolean isOwner = mission.getBeneficiary() != null && mission.getBeneficiary().getId() == user.getId();
                boolean isPending = mission.getStateOfMission() == MissionState.PENDING;
                if (!isOwner || !isPending) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
                }
            }

            missionService.cancelMission(mission);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'annulation de la mission. Veuillez réessayer.");
        }
    }

    /**
     * Create a new mission directly (manager only)
     * @param body the request body containing mission details (title, date, times, location, interpreter, beneficiary, etc.)
     * @param session the current HTTP session
     * @return 200 if created, 403 if not a manager, 500 on error
     */
    @PostMapping("/missions")
    @ResponseBody
    public ResponseEntity<?> createMission(@RequestBody Map<String, Object> body, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            Mission mission = buildMissionFromBody(body);
            missionService.createMission(mission);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de la mission. Veuillez réessayer.");
        }
    }

    /**
     * Report a delay or absence for a mission
     * @param id the mission ID
     * @param body the request body containing delay minutes and absence flag
     * @param session the current HTTP session
     * @return 200 if reported, 403 if not an interpreter or beneficiary, 500 on error
     */
    @PostMapping("/missions/{id}/retard")
    @ResponseBody
    public ResponseEntity<?> reportDelay(@PathVariable int id, @RequestBody Map<String, String> body, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");

            Mission mission = missionService.getOneMission(id);

            boolean concerned;
            if (user instanceof Beneficiary) {
                concerned = mission.getBeneficiary() != null
                        && mission.getBeneficiary().getId() == user.getId();
            } else {
                interpreterService.loadInterpreters(mission);
                concerned = mission.getInterpreters() != null
                        && mission.getInterpreters().stream().anyMatch(i -> i.getId() == user.getId());
            }
            if (!concerned) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            String minutesStr = body.get("minutes");
            String absentStr  = body.get("absent");

            int minutes = 0;
            if (minutesStr != null && !minutesStr.isBlank()) {
                minutes = Integer.parseInt(minutesStr);
            }
            boolean absent = Boolean.parseBoolean(absentStr);

            String delayInfo;
            if (absent) {
                delayInfo = "L'utilisateur ne pourra pas être présent.";
            } else {
                delayInfo = "Retard de " + minutes + " minutes.";
            }

            missionService.reportDelay(mission, delayInfo);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du signalement du retard. Veuillez réessayer.");
        }
    }

    /**
     * Fetch events for a given week with optional filters
     * @param weekDate the start date of the week to fetch (optional, defaults to current week)
     * @param status the status filter to apply (optional)
     * @param session the current HTTP session
     * @return the filtered list of events, or 500 on error
     */
    @GetMapping("/evenements")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getEvents(@RequestParam(required = false) String weekDate, @RequestParam(required = false) String status, @RequestParam(required = false) String user, HttpSession session) {
        try {
            AppliUser currentUser = (AppliUser) session.getAttribute("user");
            LocalDate date = LocalDate.now();
            if (weekDate != null && !weekDate.isBlank()) {
                try {
                    date = LocalDate.parse(weekDate);
                } catch (Exception e) {
                    e.printStackTrace();
                    date = LocalDate.now();
                }
            }

            List<Mission> missions = missionService.getMissionsForWeek(currentUser, date);
            List<Map<String, String>> allEvents = convertMissionsToEvents(missions);

            List<Map<String, String>> filtered = new ArrayList<>();
            for (Map<String, String> event : allEvents) {

                if (status != null && !status.isBlank()) {
                    if (!event.getOrDefault("status", "").equalsIgnoreCase(status)) continue;
                }

                if (user != null && !user.isBlank()) {
                    boolean matchInterp = event.getOrDefault("interpreter", "").contains(user);
                    boolean matchBene   = event.getOrDefault("beneficiary", "").contains(user);
                    if (!matchInterp && !matchBene) continue;
                }

                filtered.add(event);
            }
            return ResponseEntity.ok(filtered);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Convert a list of missions to a list of FullCalendar-compatible event maps
     * @param missions the list of missions to convert
     * @return the list of event maps with title, start, end, color, status, and other display fields
     */
    private List<Map<String, String>> convertMissionsToEvents(List<Mission> missions) {

        List<Map<String, String>> events = new ArrayList<>();

        for (Mission mission : missions) {
            if (!(mission.getTimeSlot() instanceof PunctualTimeSlot)) {
                continue;
            }

            interpreterService.loadInterpreters(mission);
            if (mission.getInterpreters() == null) {
                mission.setInterpreters(new java.util.HashSet<>());
            }

            PunctualTimeSlot pts = (PunctualTimeSlot) mission.getTimeSlot();
            Map<String, String> event = new HashMap<>();
            event.put("id", String.valueOf(mission.getId()));
            event.put("title",mission.getSubject());
            event.put("start",pts.getStartDate().toString());
            event.put("end", pts.getEndDate().toString());
            event.put("color",getColor(mission.getStateOfMission()));
            event.put("importance", String.valueOf(mission.getImportance()));
            event.put("status", getDisplayStatus(mission.getStateOfMission()));

            if (mission.getJobSkill() != null) {
                event.put("type", mission.getJobSkill().getDesignation());
                event.put("jobSkillId", String.valueOf(mission.getJobSkill().getId()));
            } else {
                event.put("type", "");
                event.put("jobSkillId", "");
            }

            if (mission.getAcademicSkill() != null) {
                event.put("academicSkill", mission.getAcademicSkill().getDesignation());
                event.put("academicSkillId", String.valueOf(mission.getAcademicSkill().getId()));
            } else {
                event.put("academicSkill",   "");
                event.put("academicSkillId", "");
            }

            if (mission.getRoom() != null) {
                event.put("room", mission.getRoom());
            } else {
                event.put("room", "");
            }

            if (mission.getCommentary() != null) {
                event.put("comment", mission.getCommentary());
            } else {
                event.put("comment", "");
            }

            String names = "";
            String ids   = "";
            for (Interpreter interpreter : mission.getInterpreters()) {
                if (!names.isEmpty()) {
                    names += ", ";
                    ids   += ",";
                }
                names += interpreter.getFirstName() + " " + interpreter.getLastName();
                ids   += interpreter.getId();
            }
            event.put("interpreter",    names);
            event.put("interpreterIds", ids);

            if (mission.getBeneficiary() != null) {
                event.put("beneficiary",   mission.getBeneficiary().getFirstName() + " " + mission.getBeneficiary().getLastName());
                event.put("beneficiaryId", String.valueOf(mission.getBeneficiary().getId()));
            } else {
                event.put("beneficiary",   "");
                event.put("beneficiaryId", "");
            }

            if (mission.getLocation() != null) {
                Location loc = mission.getLocation();

                if (loc.getDesignation() != null) {
                    event.put("locationDesignation", loc.getDesignation());
                } else {
                    event.put("locationDesignation", "");
                }

                if (loc.getStreet() != null) {
                    event.put("street", loc.getStreet());
                } else {
                    event.put("street", "");
                }

                if (loc.getStreetNumber() != null) {
                    event.put("streetNumber", loc.getStreetNumber());
                } else {
                    event.put("streetNumber", "");
                }

                if (loc.getBox() > 0) {
                    event.put("box", String.valueOf(loc.getBox()));
                } else {
                    event.put("box", "");
                }

                if (loc.getCity() != null) {
                    if (loc.getCity().getDesignation() != null) {
                        event.put("city", loc.getCity().getDesignation());
                    } else {
                        event.put("city", "");
                    }
                    event.put("postalCode", String.valueOf(loc.getCity().getPostalCode()));
                } else {
                    event.put("city",       "");
                    event.put("postalCode", "");
                }

                StringBuilder address = new StringBuilder();
                if (loc.getStreet() != null && !loc.getStreet().isBlank()) {
                    address.append(loc.getStreet());
                    if (loc.getStreetNumber() != null && !loc.getStreetNumber().isBlank()) {
                        address.append(" ").append(loc.getStreetNumber());
                    }
                    if (loc.getBox() > 0) {
                        address.append(", bte ").append(loc.getBox());
                    }
                }
                if (loc.getCity() != null) {
                    if (!address.isEmpty()) {
                        address.append(", ");
                    }
                    address.append(loc.getCity().getPostalCode())
                            .append(" ")
                            .append(loc.getCity().getDesignation());
                }
                if (loc.getDesignation() != null && !loc.getDesignation().isBlank()) {
                    address.insert(0, loc.getDesignation() + " — ");
                }
                event.put("address", address.toString());

            } else {
                event.put("locationDesignation", "");
                event.put("street",              "");
                event.put("streetNumber",        "");
                event.put("box",                 "");
                event.put("city",                "");
                event.put("postalCode",          "");
                event.put("address",             "");
            }

            events.add(event);
        }

        return events;
    }

    /**
     * Get the display label for a mission state
     * @param state the mission state
     * @return the French label corresponding to the state
     */
    private String getDisplayStatus(MissionState state) {
        if (state == null) {
            return "";
        }
        return switch (state) {
            case ACCEPTED -> "Acceptée";
            case PENDING  -> "En attente";
            case DENIED   -> "Refusée";
            case CANCELED -> "Annulée";
            default       -> "";
        };
    }

    /**
     * Get the hex color code for a mission state
     * @param state the mission state
     * @return the hex color string corresponding to the state
     */
    private String getColor(MissionState state) {
        if (state == null) {
            return COLOR_DEFAULT;
        }
        return switch (state) {
            case ACCEPTED -> COLOR_ACCEPTED;
            case PENDING -> COLOR_PENDING;
            case DENIED, CANCELED -> COLOR_REFUSED;
            default -> COLOR_DEFAULT;
        };
    }

    /**
     * Build a Mission object from a request body map
     * @param body the request body map containing all mission fields
     * @return the constructed Mission object
     * @throws Exception if date/time parsing fails or a required field is missing
     */
    private Mission buildMissionFromBody(Map<String, Object> body) throws Exception {
        Mission mission = new Mission();

        mission.setSubject((String) body.get("title"));
        mission.setCommentary((String) body.get("comment"));

        String importanceStr = body.getOrDefault("importance", "0").toString();
        mission.setImportance(Integer.parseInt(importanceStr));

        String designation  = (String) body.get("locationDesignation");
        String cityName     = (String) body.get("city");
        String postalCode   = (String) body.get("postalCode");
        String street       = (String) body.get("street");
        String streetNumber = (String) body.get("streetNumber");

        int box = 0;
        Object boxObj = body.get("box");
        if (boxObj != null && !boxObj.toString().isBlank()) {
            try { box = Integer.parseInt(boxObj.toString()); } catch (Exception ignored) {}
        }

        boolean hasLocation = (designation != null && !designation.isBlank()) ||
                (cityName    != null && !cityName.isBlank())    ||
                (postalCode  != null && !postalCode.isBlank())  ||
                (street      != null && !street.isBlank())      ||
                (streetNumber!= null && !streetNumber.isBlank())||
                box > 0;

        if (hasLocation) {
            int parsedPostalCode = 0;
            try { parsedPostalCode = Integer.parseInt(postalCode); } catch (Exception ignored) {}
            mission.setLocation(new Location(designation, new City(cityName, parsedPostalCode), street, streetNumber, box));
        }

        String room = (String) body.get("room");
        if (room != null && !room.isBlank()) {
            mission.setRoom(room);
        }

        Object academicSkillIdObj = body.get("academicSkillId");
        if (academicSkillIdObj != null && !academicSkillIdObj.toString().isBlank()) {
            try {
                int skillId = Integer.parseInt(academicSkillIdObj.toString());
                mission.setAcademicSkill(academicSkillService.getAllAcademicSkills()
                        .stream()
                        .filter(s -> s.getId() == skillId)
                        .findFirst()
                        .orElse(null));
            } catch (Exception ignored) {}
        }

        LocalDate date = LocalDate.parse((String) body.get("date"));
        LocalTime start = LocalTime.parse((String) body.get("startTime"));
        LocalTime end   = LocalTime.parse((String) body.get("endTime"));
        mission.setTimeSlot(new PunctualTimeSlot(LocalDateTime.of(date, start), LocalDateTime.of(date, end)));

        Object jobSkillIdObj = body.get("jobSkillId");
        if (jobSkillIdObj != null && !jobSkillIdObj.toString().isBlank()) {
            try {
                int skillId = Integer.parseInt(jobSkillIdObj.toString());
                mission.setJobSkill(jobSkillService.getAllJobSkills().stream()
                        .filter(s -> s.getId() == skillId)
                        .findFirst()
                        .orElse(null));
            } catch (Exception ignored) {}
        }

        Object interpreterIdsObj = body.get("interpreterIds");
        if (interpreterIdsObj instanceof List<?> interpreterIdsList && !interpreterIdsList.isEmpty()) {
            Set<Interpreter> interpreters = new HashSet<>();
            for (Object idObj : interpreterIdsList) {
                interpreters.add(interpreterService.getOneInterpreter(Integer.parseInt(idObj.toString())));
            }
            mission.setInterpreters(interpreters);
        }

        Object beneficiaryIdObj = body.get("beneficiaryId");
        if (beneficiaryIdObj != null && !beneficiaryIdObj.toString().isBlank()) {
            mission.setBeneficiary(beneficiaryService.getOneBeneficiary(Integer.parseInt(beneficiaryIdObj.toString())));
        }


        return mission;
    }

    /**
     * Applies shared post-processing to a mission update: persists the time slot if needed,
     * updates or carries over the location, and carries over existing interpreters if none were provided.
     * @param mission the existing mission as currently stored in the database
     * @param newMission the new mission information to apply, modified in place
     * @throws AlreadyExistsException if the updated location already exists with a different id
     * @throws NoSuchElementException if the existing location does not exist in the database
     * @throws Exception if a database error occurs
     */
    private void prepareMissionUpdate(Mission mission, Mission newMission) throws Exception {
        if (newMission.getTimeSlot() != null) {
            punctualTimeSlotService.findOrCreate((PunctualTimeSlot) newMission.getTimeSlot());
        }

        if (newMission.getLocation() != null && mission.getLocation() != null) {
            locationService.updateLocation(mission.getLocation(), newMission.getLocation());
        } else if (mission.getLocation() != null) {
            newMission.setLocation(mission.getLocation());
        }

        if (newMission.getInterpreters() == null) {
            if (mission.getInterpreters() != null) {
                newMission.setInterpreters(mission.getInterpreters());
            } else {
                newMission.setInterpreters(new HashSet<>());
            }
        }
    }

    /**
     * Update an existing mission (manager only).
     * @param id the id of the mission to update
     * @param body the request body containing the updated mission details
     * @param session the current HTTP session, used to check the user's rights
     * @return 200 if updated, 404 if the mission does not exist, 409 on schedule conflict,
     *         403 if the user is not a manager, 500 on error
     */
    @PostMapping("/missions/{id}/modifier")
    @ResponseBody
    public ResponseEntity<?> updateMission(@PathVariable int id, @RequestBody Map<String, Object> body, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            Mission mission    = missionService.getOneMission(id);
            Mission newMission = buildMissionFromBody(body);

            newMission.setId(mission.getId());

            prepareMissionUpdate(mission, newMission);

            missionService.updateMission(mission, newMission);
            return ResponseEntity.ok().build();

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (AlreadyExistsException e) {
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification de la mission.");
        }
    }

    /**
     * Update a pending request (beneficiary owner only).
     * @param id the id of the request to update
     * @param body the request body containing the updated request details
     * @param session the current HTTP session, used to check the user's rights
     * @return 200 if updated, 404 if the request does not exist, 409 on schedule conflict,
     *         403 if the user is not the owning beneficiary or the request is no longer pending, 500 on error
     */
    @PostMapping("/requetes/{id}/modifier")
    @ResponseBody
    public ResponseEntity<?> updateRequest(@PathVariable int id, @RequestBody Map<String, Object> body, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Beneficiary) && !(user instanceof Manager)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            Mission mission = missionService.getOneMission(id);
            if (mission.getStateOfMission() != MissionState.PENDING) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La demande n'est plus modifiable.");
            }

            if (user instanceof Beneficiary) {
                boolean isOwner = mission.getBeneficiary() != null && mission.getBeneficiary().getId() == user.getId();
                if (!isOwner) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
                }
            }

            Mission newMission = buildMissionFromBody(new HashMap<>(body));
            newMission.setBeneficiary(mission.getBeneficiary());
            newMission.setId(mission.getId());

            prepareMissionUpdate(mission, newMission);

            missionService.updateMission(mission, newMission);
            return ResponseEntity.ok().build();

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (AlreadyExistsException e) {
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification de la demande.");
        }
    }



}