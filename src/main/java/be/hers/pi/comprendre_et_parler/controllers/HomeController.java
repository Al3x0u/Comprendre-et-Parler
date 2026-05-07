package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    /**
     * Generate a BCrypt hash of a hardcoded password for manual database insertion purposes.
     * This endpoint is temporary and should be removed once no longer needed.
     * @return the BCrypt hash of the hardcoded password as a plain string
     */
    @GetMapping("/hash")
    @ResponseBody
    public String hash(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode("DemoAdminPI");
    }

    /**
     * Display the dashboard page, accessible to managers only
     * @return the dashboard template
     */
    @GetMapping("/dashboard")
    public String dashboardPage(){
        return "dashboard";
    }

    /**
     * Display the schedule page
     * @return the schedule template
     */
    @GetMapping("/horaire")
    public String schedulePage(){
        return "schedule";
    }

    /**
     * Display the profile page
     * @return the profile template
     */
    @GetMapping("/profile")
    public String profilePage(){
        return "profile";
    }

    /**
     * Display the interpreters management page
     * @return the interpreters template
     */
    @GetMapping("/interpreters")
    public String interpretersPage(){
        return "interpreters";
    }

    /**
     * Display the beneficiaries management page
     * @return the beneficiaries template
     */
    @GetMapping("/beneficiaries")
    public String beneficiariesPage(){
        return "beneficiaries";
    }

    /**
     * Log out the current user by invalidating the HTTP session and redirect to the login page
     * @param session the current HTTP session to invalidate
     * @return a redirect to /login
     * @post the session has been invalidated and the user is no longer authenticated
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}