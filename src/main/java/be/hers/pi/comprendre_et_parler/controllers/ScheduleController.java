package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.BeneficiaryService;
import be.hers.pi.comprendre_et_parler.services.InterpreterService;
import be.hers.pi.comprendre_et_parler.services.MissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Controller
public class ScheduleController {

    private final MissionService missionService = new MissionService();
    private final InterpreterService interpreterService  = new InterpreterService();
    private final BeneficiaryService beneficiaryService  = new BeneficiaryService();


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
            List<Beneficiary> beneficiaries = new ArrayList<>();
            // beneficiaries = beneficiaryService.getAllBeneficiaries();
            List<Interpreter> interpreters = interpreterService.getAllInterpreters();


            ObjectMapper mapper = new ObjectMapper();
            model.addAttribute("events", mapper.writeValueAsString(missions));
            model.addAttribute("beneficiaries", beneficiaries);
            model.addAttribute("interpreters", interpreters);

        }catch(Exception e){
            e.printStackTrace();

        }
        return "schedule";
    }

    @GetMapping("/horaire/events")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getEvents(@RequestParam(required = false) String weekDate, @RequestParam(required = false) String status, @RequestParam(required = false) String interpreter, HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            LocalDate date = null;
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

            if (mission.getStateOfMission() != null) {
                event.put("status", mission.getStateOfMission().toString());
            } else {
                event.put("status", "");
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
            events.add(event);
        }

        return events;
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

}
