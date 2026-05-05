package be.hers.pi.comprendre_et_parler.controllers;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;

import jakarta.servlet.http.HttpSession;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/")
    public String index(){
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String getUserLogin(@RequestParam String login, @RequestParam String password, Model model, HttpSession session){
        AppliUser user = loginService.getUserData(login, password);
        if(user != null){
            session.setAttribute("user", user);
            if(user instanceof Manager){
                return "redirect:/dashboard";
            } else {
                return "redirect:/horaire";
            }
        } else {
            model.addAttribute("error", "Identifiants incorrects");
            return "login";
        }
    }


}
