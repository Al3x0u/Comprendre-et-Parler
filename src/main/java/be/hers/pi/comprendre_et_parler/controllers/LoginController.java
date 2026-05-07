package be.hers.pi.comprendre_et_parler.controllers;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * Redirect the root URL to the login page
     * @return a redirect to /login
     */
    @GetMapping("/")
    public String index(){
        return "redirect:/login";
    }

    /**
     * Display the login page
     * @return the login template
     */
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    /**
     * Process the login form submission and authenticate the user
     * @param login the login entered by the user
     * @param password the password entered by the user
     * @param model the Spring model used to pass error messages to the view
     * @param session the HTTP session in which to store the authenticated user
     * @return a redirect to /dashboard if the user is a Manager, a redirect to /horaire
     * if the user is an Interpreter or Beneficiary, or the login template with an error
     * message if authentication failed
     */
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
