package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScheduleController {
    /**
     * Display the schedule page
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the schedule view or redirect to the connection if not authenticated
     */
    @GetMapping("/horaire")
    public String showSchedule(HttpSession session, Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (user instanceof Manager) {
            model.addAttribute("userRole", "MANAGER");
        } else if (user instanceof Interpreter) {
            model.addAttribute("userRole", "INTERPRETER");
        } else if (user instanceof Beneficiary) {
            model.addAttribute("userRole", "BENEFICIARY");
        }

        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "schedule");
        model.addAttribute("isManager", user instanceof Manager);
        return "schedule";
    }
}
