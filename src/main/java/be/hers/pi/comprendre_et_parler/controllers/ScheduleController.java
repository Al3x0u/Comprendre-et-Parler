package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/horaire")
public class ScheduleController {

    private final MissionService missionService = new MissionService();
    private final InterpreterService interpreterService = new InterpreterService();
    private final BeneficiaryService beneficiaryService = new BeneficiaryService();
    private final JobSkillService jobSkillService = new JobSkillService();
    private final AcademicSkillService academicSkillService = new AcademicSkillService();
    private final CityService cityService = new CityService();

    /**
     * Display the schedule page
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the schedule view
     */
    @GetMapping
    public String showSchedule(HttpSession session, Model model) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");

            if (user instanceof Manager) {
                model.addAttribute("userRole", "MANAGER");
            } else if (user instanceof Interpreter) {
                model.addAttribute("userRole", "INTERPRETER");
            } else if (user instanceof Beneficiary) {
                model.addAttribute("userRole", "BENEFICIARY");
            }

            List<Mission> missions = missionService.getMissionsForWeek(user, LocalDate.now());
            List<Map<String, String>> events = convertMissionsToEvents(missions);

            ObjectMapper mapper = new ObjectMapper();
            model.addAttribute("events", mapper.writeValueAsString(events));
            model.addAttribute("beneficiaries", new HashSet<>(beneficiaryService.getAllBeneficiaries()));
            model.addAttribute("interpreters", interpreterService.getAllInterpreters());
            model.addAttribute("allJobSkills", jobSkillService.getAllJobSkills());
            model.addAttribute("allAcademicSkills", academicSkillService.getAllAcademicSkills());

            List<String> timeSlots = new ArrayList<>();
            for (int h = 8; h <= 22; h++) {
                timeSlots.add(String.format("%02d:00", h));
                if (h < 22) timeSlots.add(String.format("%02d:30", h));
            }
            model.addAttribute("timeSlots", timeSlots);

            List<City> allCities = new ArrayList<>(cityService.getAllCities());
            allCities.sort(City::compareTo);
            model.addAttribute("allCities", allCities);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "schedule";
    }

    /**
     * Create a new interpretation request from a beneficiary
     * @param payload the request body containing mission details
     * @param session the current HTTP session
     * @return 200 if created, 400 if missing fields or invalid times, 403 if not a beneficiary, 500 on error
     */
    @PostMapping("/requetes")
    @ResponseBody
    public ResponseEntity<String> createRequest(@RequestBody Map<String, Object> payload, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Beneficiary beneficiary))
                return ResponseEntity.status(403).body("Accès refusé.");

            String title     = (String) payload.get("title");
            String date      = (String) payload.get("date");
            String startTime = (String) payload.get("startTime");
            String endTime   = (String) payload.get("endTime");
            String cityName  = (String) payload.get("city");
            String street    = (String) payload.get("street");

            if (title == null || title.isBlank() || date == null || startTime == null || endTime == null
                    || cityName == null || cityName.isBlank() || street == null || street.isBlank())
                return ResponseEntity.badRequest().body("Champs requis manquants.");

            LocalTime start = LocalTime.parse(startTime);
            LocalTime end   = LocalTime.parse(endTime);
            if (!end.isAfter(start))
                return ResponseEntity.badRequest().body("L'heure de fin doit être après l'heure de début.");

            Mission mission = buildMissionFromBody(payload);
            mission.setBeneficiary(beneficiary);
            mission.setInterpreters(Set.of());
            missionService.createRequest(mission);

            return ResponseEntity.ok("Demande créée.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de la création de la demande. Veuillez réessayer.");
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
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");

            String interpreterIdStr = body.get("interpreterId");
            if (interpreterIdStr == null || interpreterIdStr.isBlank())
                return ResponseEntity.badRequest().body("Aucun interprète sélectionné");

            Mission mission = missionService.getOneMission(id);
            Interpreter interpreter = interpreterService.getOneInterpreter(Integer.parseInt(interpreterIdStr));

            Set<Interpreter> interpreters = new HashSet<>();
            interpreters.add(interpreter);
            mission.setInterpreters(interpreters);

            missionService.acceptRequest(mission);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'acceptation de la mission. Veuillez réessayer.");
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
            Mission mission = missionService.getOneMission(id);
            missionService.refuseRequest(mission);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du refus de la mission. Veuillez réessayer.");
        }
    }

    /**
     * Create a new mission directly (manager only)
     * @param body the request body containing mission details
     * @param session the current HTTP session
     * @return 200 if created, 403 if not a manager, 500 on error
     */
    @PostMapping("/missions")
    @ResponseBody
    public ResponseEntity<?> createMission(@RequestBody Map<String, Object> body, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");

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
            if (!(user instanceof Interpreter) && !(user instanceof Beneficiary))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");

            Mission mission = missionService.getOneMission(id);

            int minutes = 0;
            String minutesStr = body.get("minutes");
            if (minutesStr != null && !minutesStr.isBlank())
                minutes = Integer.parseInt(minutesStr);

            boolean absent = Boolean.parseBoolean(body.get("absent"));

            String delayInfo = absent
                    ? "L'utilisateur ne pourra pas être présent."
                    : "Retard de " + minutes + " minutes.";

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
     * @param interpreter the interpreter name filter to apply (optional)
     * @param session the current HTTP session
     * @return the filtered list of events, or 500 on error
     */
    @GetMapping("/evenements")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getEvents(
            @RequestParam(required = false) String weekDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String interpreter,
            HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");

            LocalDate date = LocalDate.now();
            if (weekDate != null && !weekDate.isBlank()) {
                try {
                    date = LocalDate.parse(weekDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<Mission> missions = missionService.getMissionsForWeek(user, date);
            List<Map<String, String>> allEvents = convertMissionsToEvents(missions);

            List<Map<String, String>> filtered = new ArrayList<>();
            for (Map<String, String> event : allEvents) {
                if (status != null && !status.isBlank()
                        && !event.getOrDefault("status", "").equalsIgnoreCase(status)) continue;
                if (interpreter != null && !interpreter.isBlank()
                        && !event.getOrDefault("interpreter", "").contains(interpreter)) continue;
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
            if (!(mission.getTimeSlot() instanceof PunctualTimeSlot pts)) continue;

            Map<String, String> event = new HashMap<>();
            event.put("id",         String.valueOf(mission.getId()));
            event.put("title",      mission.getSubject());
            event.put("start",      pts.getStartDate().toString());
            event.put("end",        pts.getEndDate().toString());
            event.put("color",      getColor(mission.getStateOfMission()));
            event.put("type",       mission.getJobSkill() != null ? mission.getJobSkill().getDesignation() : "");
            event.put("room",       mission.getRoom() != null ? mission.getRoom() : "");
            event.put("comment",    mission.getCommentary() != null ? mission.getCommentary() : "");
            event.put("address",    mission.getLocation() != null ? mission.getLocation().toString() : "");
            event.put("importance", String.valueOf(mission.getImportance()));
            event.put("status",     getDisplayStatus(mission.getStateOfMission()));

            String interpreterNames = "";
            if (mission.getInterpreters() != null) {
                for (Interpreter interpreter : mission.getInterpreters()) {
                    if (!interpreterNames.isEmpty()) interpreterNames += ", ";
                    interpreterNames += interpreter.getFirstName() + " " + interpreter.getLastName();
                }
            }
            event.put("interpreter", interpreterNames);
            event.put("beneficiary", mission.getBeneficiary() != null
                    ? mission.getBeneficiary().getFirstName() + " " + mission.getBeneficiary().getLastName()
                    : "");

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
        if (state == null) return "";
        return switch (state) {
            case ACCEPTED -> "Acceptée";
            case PENDING  -> "En attente";
            case DENIED   -> "Refusée";
            case CANCELED -> "Refusée";
            default       -> "";
        };
    }

    /**
     * Get the hex color code for a mission state
     * @param state the mission state
     * @return the hex color string corresponding to the state
     */
    private String getColor(MissionState state) {
        if (state == null) return "#adb5bd";
        return switch (state) {
            case ACCEPTED -> "#40c057";
            case PENDING  -> "#fab005";
            case DENIED   -> "#fa5252";
            case CANCELED -> "#fa5252";
            default       -> "#adb5bd";
        };
    }

    /**
     * Build a Mission object from a request body map.
     * Handles both request creation (beneficiary) and mission creation (manager).
     * Beneficiary and interpreters must be set by the caller if needed.
     * @param body the request body map containing all mission fields
     * @return the constructed Mission object
     * @throws Exception if date/time parsing fails or a required field is missing
     */
    private Mission buildMissionFromBody(Map<String, Object> body) throws Exception {
        Mission mission = new Mission();

        mission.setSubject((String) body.get("title"));
        mission.setCommentary((String) body.get("comment"));
        mission.setImportance(Integer.parseInt(body.getOrDefault("importance", "0").toString()));

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

        boolean hasLocation =
                (designation  != null && !designation.isBlank())  ||
                        (cityName     != null && !cityName.isBlank())     ||
                        (postalCode   != null && !postalCode.isBlank())   ||
                        (street       != null && !street.isBlank())       ||
                        (streetNumber != null && !streetNumber.isBlank()) ||
                        box > 0;

        if (hasLocation) {
            int parsedPostalCode = 0;
            try { parsedPostalCode = Integer.parseInt(postalCode); } catch (Exception ignored) {}
            mission.setLocation(new Location(designation, new City(cityName, parsedPostalCode), street, streetNumber, box));
        }

        String room = (String) body.get("room");
        if (room != null && !room.isBlank())
            mission.setRoom(room);

        LocalDate date  = LocalDate.parse((String) body.get("date"));
        LocalTime start = LocalTime.parse((String) body.get("startTime"));
        LocalTime end   = LocalTime.parse((String) body.get("endTime"));
        mission.setTimeSlot(new PunctualTimeSlot(LocalDateTime.of(date, start), LocalDateTime.of(date, end)));

        String type = (String) body.get("type");
        if (type != null && !type.isBlank()) {
            jobSkillService.getAllJobSkills().stream()
                    .filter(js -> js.getDesignation() != null && js.getDesignation().trim().equalsIgnoreCase(type.trim()))
                    .findFirst()
                    .ifPresent(mission::setJobSkill);
        }

        Object academicSkillIdObj = body.get("academicSkillId");
        if (academicSkillIdObj != null && !academicSkillIdObj.toString().isBlank()) {
            try {
                int skillId = Integer.parseInt(academicSkillIdObj.toString());
                academicSkillService.getAllAcademicSkills().stream()
                        .filter(s -> s.getId() == skillId)
                        .findFirst()
                        .ifPresent(mission::setAcademicSkill);
            } catch (Exception ignored) {}
        }

        Object interpreterIdsObj = body.get("interpreterIds");
        if (interpreterIdsObj instanceof List<?> interpreterIdsList && !interpreterIdsList.isEmpty()) {
            Set<Interpreter> interpreters = new HashSet<>();
            for (Object idObj : interpreterIdsList)
                interpreters.add(interpreterService.getOneInterpreter(Integer.parseInt(idObj.toString())));
            mission.setInterpreters(interpreters);
        }

        Object beneficiaryIdObj = body.get("beneficiaryId");
        if (beneficiaryIdObj != null && !beneficiaryIdObj.toString().isBlank())
            mission.setBeneficiary(beneficiaryService.getOneBeneficiary(Integer.parseInt(beneficiaryIdObj.toString())));

        return mission;
    }
}