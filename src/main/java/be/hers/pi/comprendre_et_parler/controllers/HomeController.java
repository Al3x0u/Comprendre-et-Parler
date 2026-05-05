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

    @GetMapping("/hash")
    @ResponseBody
    public String hash(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode("DemoAdminPI");
    }

    @GetMapping("/dashboard")
    public String dashboardPage(){
        return "dashboard";
    }

    @GetMapping("/horaire")
    public String schedulePage(){
        return "schedule";
    }

    @GetMapping("/profile")
    public String profilePage(){
        return "profile";
    }

    @GetMapping("/interpreters")
    public String interpretersPage(){
        return "interpreters";
    }

    @GetMapping("/beneficiaries")
    public String beneficiariesPage(){
        return "beneficiaries";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}