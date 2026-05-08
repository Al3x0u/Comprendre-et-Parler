package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.AppliUser;
import be.hers.pi.comprendre_et_parler.models.Manager;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    /**
     * Display the dashboard page (only for managers)
     * @param model the model to pass data to the view
     * @return the dashboard view
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("interpreterCount", 25);
        model.addAttribute("beneficiaryCount", 50);
        return "dashboard";
    }
}
