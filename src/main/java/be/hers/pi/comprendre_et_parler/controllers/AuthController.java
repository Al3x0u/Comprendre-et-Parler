package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.AppliUser;
import be.hers.pi.comprendre_et_parler.models.Manager;
import be.hers.pi.comprendre_et_parler.services.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Display the login page
     * @return the login view
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Handle login form submission
     * @param login the user's login
     * @param password the user's password
     * @param model the model to pass data to the view
     * @param session the current HTTP session
     * @return redirect to schedule page if successful, login page with error otherwise
     */
    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password, Model model, HttpSession session) {
        AppliUser user = loginService.getUserData(login, password);

        if (user == null) {
            model.addAttribute("error", "Identifiant ou mot de passe incorrect");
            return "login";
        }

        session.setAttribute("user", user);

        if (user instanceof Manager) {
            return "redirect:/dashboard";
        }
        return "redirect:/horaire";
    }

    /**
     * Handle logout
     * @param session the current HTTP session
     * @return redirect to login page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}