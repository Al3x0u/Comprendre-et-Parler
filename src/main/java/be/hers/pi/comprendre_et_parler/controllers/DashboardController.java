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
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the dashboard view or redirect to login if not authenticated
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager)) return "redirect:/horaire";

        int interpreterCount = 25;
        int beneficiaryCount = 50;
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("managerFirstName", user.getFirstName());
        model.addAttribute("interpreterCount", interpreterCount);
        model.addAttribute("beneficiaryCount", beneficiaryCount);
        model.addAttribute("isManager", true);
        return "dashboard";
    }
}
