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



import java.time.LocalDate;
import java.util.*;

@Controller
public class ScheduleController {

    private final MissionService missionService;
    private final InterpreterService interpreterService;
    private final BeneficiaryService beneficiaryService;

    public ScheduleController() {
        this.missionService = new MissionService();
        this.interpreterService = new InterpreterService();
        this.beneficiaryService = new BeneficiaryService();
    }

    /**
     * Display the schedule page
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the schedule view or redirect to the connection if not authenticated
     */
    @GetMapping("/horaire")
    public String showSchedule(HttpSession session, Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");

        if (user instanceof Manager) {
            model.addAttribute("userRole", "MANAGER");
        } else if (user instanceof Interpreter) {
            model.addAttribute("userRole", "INTERPRETER");
        } else if (user instanceof Beneficiary) {
            model.addAttribute("userRole", "BENEFICIARY");
        }

        List<Map<String, String>> missions = getHardcodedMissions();
        List<Beneficiary> beneficiaries = getHardcodedBeneficiaries();

        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute("events", mapper.writeValueAsString(missions));
        model.addAttribute("beneficiaries", beneficiaries);
        return "schedule";
    }

    // Temporary - user hardcoded
    private AppliUser getHardcodedUser() {
        Manager m1 = new Manager(
                "M001",
                "Isabelle",
                "Hulin",
                LocalDate.of(1980, 1, 1),
                "hashed",
                "isabelle@hers.be",
                "0470000000",
                0,
                0,
                null,
                new HashSet<>(),
                new HashSet<>(),
                null,
                new HashSet<>()
        );

        Interpreter i1 = new Interpreter(
                "I001",
                "Jessica",
                "DuBuisson",
                LocalDate.of(1980, 1, 1),
                "hashed",
                "jessica@hers.be",
                "0470000001",
                0, 0,
                null,
                null, null,
                null,
                null
        );

        Interpreter i2 = new Interpreter(
                "I002",
                "Alice",
                "Charpentier",
                LocalDate.of(1985, 6, 12),
                "hashed",
                "alice@hers.be",
                "0470000002",
                30,
                1000,
                "Train",
                new HashSet<>(),
                new HashSet<>(),
                null,
                new HashSet<>()
        );

        Beneficiary b1 = new Beneficiary(
                "B001",
                "Lucas",
                "Martin",
                LocalDate.of(2005, 3, 15),
                "hashed",
                "lucas@hers.be",
                "0470000003",
                null,
                i1
        );

        Beneficiary b2 = new Beneficiary(
                "B002",
                "Emma",
                "Dupont",
                LocalDate.of(2006, 5, 20),
                "hashed",
                "emma@hers.be",
                "0470000004",
                null,
                i2
        );

        return m1;
    }

    private List<Interpreter> getHardcodedInterpreters() {
        List<Interpreter> interpreters = new ArrayList<>();

        Interpreter i1 = new Interpreter(
                "I001",
                "Jessica",
                "DuBuisson",
                LocalDate.of(1980, 1, 1),
                "hashed",
                "jessica@hers.be",
                "0470000001",
                0, 0,
                null,
                null, null,
                null,
                null
        );

        Interpreter i2 = new Interpreter(
                "I002",
                "Alice",
                "Charpentier",
                LocalDate.of(1985, 6, 12),
                "hashed",
                "alice@hers.be",
                "0470000002",
                30,
                1000,
                "Train",
                new HashSet<>(),
                new HashSet<>(),
                null,
                new HashSet<>()
        );

        interpreters.add(i1);
        interpreters.add(i2);

        return interpreters;
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
