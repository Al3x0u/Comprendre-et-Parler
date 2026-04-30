package be.hers.pi.comprendre_et_parler.controllers;

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
     * @return the schedule view or redirect to login if not authenticated
     */
    @GetMapping("/schedule")
    public String showSchedule(HttpSession session, Model model) {
        // AppliUser user = (AppliUser) session.getAttribute("user");
        // if (user == null) return "redirect:/login";
        // model.addAttribute("user", user);
        model.addAttribute("currentPage", "schedule");
        return "schedule";
    }
}
