package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.AppliUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model){
        AppliUser user = (AppliUser) session.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "home";
    }
}
