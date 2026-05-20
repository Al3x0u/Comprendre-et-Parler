package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.BeneficiaryService;
import be.hers.pi.comprendre_et_parler.services.InterpreterService;
import be.hers.pi.comprendre_et_parler.services.MissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
            // beneficiaries = beneficiaryService.getAllBeneficiaries();

            ObjectMapper mapper = new ObjectMapper();
            model.addAttribute("events", mapper.writeValueAsString(missions));
            model.addAttribute("beneficiaries", beneficiaries);

        }catch(Exception e){
            e.printStackTrace();

        }
        return "schedule";
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
