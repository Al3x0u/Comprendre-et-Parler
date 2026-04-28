package be.hers.pi.comprendre_et_parler.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    /**
     * Redirect root to login page
     * @return redirect to login page
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * Display the dashboard page (only for managers)
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the dashboard view or redirect to login if not authenticated
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        // AppliUser user = (AppliUser) session.getAttribute("user");
        // if (user == null) return "redirect:/login";
        // if (!(user instanceof Manager)) return "redirect:/schedule";
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("managerFirstName", "Isabelle");
        model.addAttribute("interpreterCount", 25);
        model.addAttribute("beneficiaryCount", 50);
        return "dashboard";
    }

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

    /**
     * Display the interpreters page
     * @param session the current HTTP session
     * @return the interpreters view or redirect to login if not authenticated
     */
    @GetMapping("/interpreters")
    public String showInterpreters(HttpSession session, Model model) {
        // AppliUser user = (AppliUser) session.getAttribute("user");
        // if (user == null) return "redirect:/login";
        model.addAttribute("currentPage", "interpreters");
        return "interpreters";
    }

    /**
     * Display the beneficiaries page
     * @param session the current HTTP session
     * @return the beneficiaries view or redirect to login if not authenticated
     */
    @GetMapping("/beneficiaries")
    public String showBeneficiaries(HttpSession session, Model model) {
        // AppliUser user = (AppliUser) session.getAttribute("user");
        // if (user == null) return "redirect:/login";
        model.addAttribute("currentPage", "beneficiaries");
        return "beneficiaries";
    }

    /**
     * Display the profile page
     * @param session the current HTTP session
     * @return the profile view or redirect to login if not authenticated
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session) {
        // AppliUser user = (AppliUser) session.getAttribute("user");
        // if (user == null) return "redirect:/login";
        return "profile";
    }
}