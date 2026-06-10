package be.hers.pi.comprendre_et_parler.controllers;

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


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/horaire")
public class ScheduleController {

    private final MissionService missionService = new MissionService();
    private final InterpreterService interpreterService  = new InterpreterService();
    private final BeneficiaryService beneficiaryService  = new BeneficiaryService();
    private final JobSkillService jobSkillService = new JobSkillService();
    private final AcademicSkillService academicSkillService = new AcademicSkillService();
    private final CityService cityService = new CityService();


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


            ObjectMapper mapper = new ObjectMapper();
            model.addAttribute("events", mapper.writeValueAsString(events));
            model.addAttribute("beneficiaries", beneficiaries);
            model.addAttribute("interpreters", interpreters);
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
            String type = payload.get("type");
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

            missionService.createRequest(mission);

            return ResponseEntity.ok("Demande créée.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de la création de la demande. Veuillez réessayer.");
        }
    }

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
            return ResponseEntity.ok().body(warning); // "" si ok, message si dépassement

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur");
        }
    }

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

            Mission mission = missionService.getOneMission(id);
            missionService.refuseRequest(mission);

            return ResponseEntity.ok().build();

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du refus de la mission. Veuillez réessayer.");
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
            if (!(user instanceof Interpreter) && !(user instanceof Beneficiary)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            Mission mission = missionService.getOneMission(id);

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
     * @param interpreter the interpreter name filter to apply (optional)
     * @param session the current HTTP session
     * @return the filtered list of events, or 500 on error
     */
    @GetMapping("/evenements")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getEvents(@RequestParam(required = false) String weekDate, @RequestParam(required = false) String status, @RequestParam(required = false) String interpreter, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            LocalDate date =  LocalDate.now();
            if(weekDate != null && !weekDate.isBlank()){
                try{
                    date = LocalDate.parse(weekDate);
                } catch (Exception e) {
                    e.printStackTrace();
                    date = LocalDate.now();
                }
            }

            List<Mission> missions = missionService.getMissionsForWeek(user, date);
            List<Map<String, String>> allEvents =  convertMissionsToEvents(missions);

            List<Map<String, String>> filtered = new ArrayList<>();
            for (Map<String, String> event : allEvents) {

                if (status != null && !status.isBlank()) {
                    if (!event.getOrDefault("status", "").equalsIgnoreCase(status)) continue;
                }

                if (interpreter != null && !interpreter.isBlank()) {
                    if (!event.getOrDefault("interpreter", "").contains(interpreter)) continue;
                }

                filtered.add(event);

            }
            return ResponseEntity.ok(filtered);
        }catch(Exception e){
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

            PunctualTimeSlot pts = (PunctualTimeSlot) mission.getTimeSlot();
            Map<String, String> event = new HashMap<>();
            event.put("title", mission.getSubject());
            event.put("id", String.valueOf(mission.getId()));


            event.put("start", pts.getStartDate().toString());
            event.put("end", pts.getEndDate().toString());

            event.put("color", getColor(mission.getStateOfMission()));

            if (mission.getJobSkill() != null) {
                event.put("type", mission.getJobSkill().getDesignation());
            } else {
                event.put("type", "");
            }
            if (mission.getRoom() != null) {
                event.put("room", mission.getRoom());
            } else {
                event.put("room", "");
            }

            String interpreters = "";
            if (mission.getInterpreters() != null) {

                for (Interpreter interpreter : mission.getInterpreters()) {

                    if (!interpreters.isEmpty()) {
                        interpreters += ", ";
                    }
                    interpreters += interpreter.getFirstName() + " " + interpreter.getLastName();
                }
            }

            event.put("interpreter", interpreters);
            if (mission.getBeneficiary() != null) {

                String beneficiaryName =  mission.getBeneficiary().getFirstName() + " " + mission.getBeneficiary().getLastName();
                event.put("beneficiary", beneficiaryName);

            } else {
                event.put("beneficiary", "");
            }


            if (mission.getCommentary() != null) {
                event.put("comment", mission.getCommentary());
            } else {
                event.put("comment", "");
            }

            if (mission.getLocation() != null) {
                Location loc = mission.getLocation();
                StringBuilder address = new StringBuilder();
                if (loc.getStreet() != null && !loc.getStreet().isBlank()) {
                    address.append(loc.getStreet());
                    if (loc.getStreetNumber() != null && !loc.getStreetNumber().isBlank())
                        address.append(" ").append(loc.getStreetNumber());
                    if (loc.getBox() > 0)
                        address.append(", bte ").append(loc.getBox());
                }
                if (loc.getCity() != null) {
                    if (!address.isEmpty()) address.append(", ");
                    address.append(loc.getCity().getPostalCode())
                            .append(" ")
                            .append(loc.getCity().getDesignation());
                }
                if (loc.getDesignation() != null && !loc.getDesignation().isBlank()) {
                    address.insert(0, loc.getDesignation() + " — ");
                }
                event.put("address", address.toString());
            } else {
                event.put("address", "");
            }

            event.put("importance", String.valueOf(mission.getImportance()));
            event.put("status", getDisplayStatus(mission.getStateOfMission()));
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
        if (state == null) {
            return "#adb5bd";
        }
        return switch (state) {
            case ACCEPTED -> "#40c057";
            case PENDING -> "#fab005";
            case DENIED  -> "#fa5252";
            case CANCELED -> "#fa5252";
            default -> "#adb5bd";
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

        boolean hasLocation =
                (designation != null && !designation.isBlank()) ||
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

        String type = (String) body.get("type");
        if (type != null && !type.isBlank()) {
            jobSkillService.getAllJobSkills().stream()
                    .filter(js -> js.getDesignation() != null && js.getDesignation().trim().equalsIgnoreCase(type.trim()))
                    .findFirst()
                    .ifPresent(mission::setJobSkill);
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
     * Cancel an accepted or pending mission
     * @param id the mission ID
     * @param session the current HTTP session
     * @return 200 if cancelled, 403 if not a manager, 500 on error
     */
    @PostMapping("/missions/{id}/annuler")
    @ResponseBody
    public ResponseEntity<?> cancelMission(@PathVariable int id, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }
            Mission mission = missionService.getOneMission(id);
            missionService.cancelMission(mission);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'annulation.");
        }
    }

}