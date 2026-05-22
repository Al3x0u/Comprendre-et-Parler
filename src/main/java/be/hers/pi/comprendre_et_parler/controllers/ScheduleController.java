package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.BeneficiaryService;
import be.hers.pi.comprendre_et_parler.services.InterpreterService;
import be.hers.pi.comprendre_et_parler.services.JobSkillService;
import be.hers.pi.comprendre_et_parler.services.MissionService;
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
public class ScheduleController {

    private final MissionService missionService = new MissionService();
    private final InterpreterService interpreterService  = new InterpreterService();
    private final BeneficiaryService beneficiaryService  = new BeneficiaryService();
    private final JobSkillService jobSkillService = new JobSkillService();


    /**
     * Display the schedule page
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the schedule view or redirect to the connection if not authenticated
     */
    @GetMapping("/horaire")
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
            Set<Beneficiary> beneficiaries= beneficiaryService.findAll();
            List<Interpreter> interpreters = interpreterService.getAllInterpreters();


            ObjectMapper mapper = new ObjectMapper();
            model.addAttribute("events", mapper.writeValueAsString(events));
            model.addAttribute("beneficiaries", beneficiaries);
            model.addAttribute("interpreters", interpreters);

        }catch(Exception e){
            e.printStackTrace();

        }
        return "schedule";
    }

    @PostMapping("/horairerequests")
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
                postalCode = 0;
            }
            String street = payload.get("street");
            String streetNumber = payload.get("streetNumber");
            int box = 0;
            try{
                box = Integer.parseInt(payload.get("box"));
            }catch(Exception e){
                box = 0;
            }

            String professor = payload.get("professor");
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
            mission.setRoom(professor);
            mission.setImportance(importance);
            mission.setBeneficiary(beneficiary);
            mission.setInterpreters(Set.of());

            missionService.createRequest(mission);

            return ResponseEntity.ok("Demande créée.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la création de la demande : " + e.getMessage());
        }
    }

    @PostMapping("/horaire/missions/{id}/accept")
    @ResponseBody
    public ResponseEntity<?> acceptMission(@PathVariable int id, @RequestBody Map<String, String> body, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            String interpreterIdStr = body.get("interpreterId");
            if (interpreterIdStr == null || interpreterIdStr.isBlank()) {
                return ResponseEntity.badRequest().body("Aucun interprète sélectionné");
            }

            Mission mission = missionService.getMissionById(id);
            Interpreter interpreter = interpreterService.getInterpreterById(Integer.parseInt(interpreterIdStr));

            Set<Interpreter> interpreters = new HashSet<>();
            interpreters.add(interpreter);
            mission.setInterpreters(interpreters);

            missionService.acceptRequest(mission);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/horaire/missions/{id}/refuse")
    @ResponseBody
    public ResponseEntity<?> refuseMission(@PathVariable int id, HttpSession session) {
        try {

            Mission mission = missionService.getMissionById(id);
            missionService.refuseRequest(mission);

            return ResponseEntity.ok().build();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @PostMapping("/horaire/missions")
    @ResponseBody
    public ResponseEntity<?> createMission(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            Mission mission = buildMissionFromBody(body, true);
            missionService.createMission(mission);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/horaire/missions/{id}/delay")
    @ResponseBody
    public ResponseEntity<?> reportDelay(@PathVariable int id, @RequestBody Map<String, String> body, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Interpreter) && !(user instanceof Beneficiary)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
            }

            Mission mission = missionService.getMissionById(id);

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }

    @GetMapping("/horaire/events")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getEvents(@RequestParam(required = false) String weekDate, @RequestParam(required = false) String status, @RequestParam(required = false) String interpreter, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            LocalDate date =  LocalDate.now();
            if(weekDate != null && !weekDate.isBlank()){
                try{
                    date = LocalDate.parse(weekDate);
                } catch (Exception e) {
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

        return null;

    }

    private List<Map<String, String>> convertMissionsToEvents(List<Mission> missions) {

        List<Map<String, String>> events = new ArrayList<>();

        for (Mission mission : missions) {
            if (!(mission.getTimeSlot() instanceof PunctualTimeSlot)) {
                continue;
            }
            PunctualTimeSlot pts = (PunctualTimeSlot) mission.getTimeSlot();
            Map<String, String> event = new HashMap<>();
            event.put("title", mission.getSubject());

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
                event.put("address", mission.getLocation().toString());
            } else {
                event.put("address", "");
            }

            event.put("importance", String.valueOf(mission.getImportance()));
            event.put("status", getDisplayStatus(mission.getStateOfMission()));
            events.add(event);
        }

        return events;
    }
    private String getDisplayStatus(MissionState state) {
        if (state == null) {
            return "";
        }
        return switch (state) {
            case ACCEPTED -> "Accepte";
            case PENDING -> "En attente";
            case DENIED -> "Refuse";
            case CANCELED -> "Refuse";
            default -> "";
        };
    }

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

    private Mission buildMissionFromBody(Map<String, String> body, boolean withInterpreter) throws Exception {
        Mission mission = new Mission();

        mission.setSubject(body.get("title"));
        mission.setCommentary(body.get("comment"));
        mission.setImportance(Integer.parseInt(body.getOrDefault("importance", "0")));

        String designation = body.get("locationDesignation");
        String cityName = body.get("city");
        String postalCode = body.get("postalCode");
        String street = body.get("street");
        String streetNumber = body.get("streetNumber");

        int box = 0;
        String boxStr = body.get("box");
        if (boxStr != null && !boxStr.isBlank()) {
            try{
                box = Integer.parseInt(boxStr);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        boolean hasLocation =
                (designation != null && !designation.isBlank()) ||
                        (cityName != null && !cityName.isBlank()) ||
                        (postalCode != null && !postalCode.isBlank()) ||
                        (street != null && !street.isBlank()) ||
                        (streetNumber != null && !streetNumber.isBlank()) ||
                        box > 0;

        if (hasLocation) {
            int parsedPostalCode = 0;
            try {
                parsedPostalCode = Integer.parseInt(postalCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            City city = new City(cityName, parsedPostalCode);

            Location location = new Location(
                   designation,
                    city,
                    street,
                    streetNumber,
                    box
            );
            mission.setLocation(location);
        }


        String professor = body.get("professor");
        if (professor != null && !professor.isBlank()) {
            mission.setRoom(professor);
        }

        LocalDate date = LocalDate.parse(body.get("date"));
        java.time.LocalTime start = java.time.LocalTime.parse(body.get("startTime"));
        java.time.LocalTime end = java.time.LocalTime.parse(body.get("endTime"));

        PunctualTimeSlot slot = new PunctualTimeSlot(
                java.time.LocalDateTime.of(date, start),
                java.time.LocalDateTime.of(date, end)
        );
        mission.setTimeSlot(slot);

        String type = body.get("type");
        if (type != null && !type.isBlank()) {
            Set<JobSkill> allJobSkills = jobSkillService.findAll();

            for (JobSkill jobSkill : allJobSkills) {
                if (jobSkill.getDesignation() != null
                        && jobSkill.getDesignation().trim().equalsIgnoreCase(type.trim())) {
                    mission.setJobSkill(jobSkill);
                    break;
                }
            }
        }

        if (withInterpreter) {
            String interpreterId = body.get("interpreterId");
            if (interpreterId != null && !interpreterId.isBlank()) {
                Interpreter interpreter = interpreterService.getInterpreterById(Integer.parseInt(interpreterId));
                Set<Interpreter> interpreters = new HashSet<>();
                interpreters.add(interpreter);
                mission.setInterpreters(interpreters);
            }

            String beneficiaryId = body.get("beneficiaryId");
            if (beneficiaryId != null && !beneficiaryId.isBlank()) {
                Beneficiary beneficiary = beneficiaryService.getBeneficiaryById(Integer.parseInt(beneficiaryId));
                mission.setBeneficiary(beneficiary);
            }
        }

        return mission;
    }

}
