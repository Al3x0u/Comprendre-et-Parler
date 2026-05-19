package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    @GetMapping("/hash")
    @ResponseBody
    public String hash(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode("DemoUserPI");
    }

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
