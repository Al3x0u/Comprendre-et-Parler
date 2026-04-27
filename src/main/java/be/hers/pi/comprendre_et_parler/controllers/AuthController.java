package be.hers.pi.comprendre_et_parler.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
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
    public String login(@RequestParam String login, @RequestParam String password,
                        Model model, HttpSession session) {
        // TODO: implémenter avec AuthService quand les DAOs compilent
        // try {
        //     AppliUser user = authService.login(login, password);
        //     session.setAttribute("user", user);
        //     return "redirect:/schedule";
        // } catch (Exception e) {
        //     model.addAttribute("error", "Identifiant ou mot de passe incorrect");
        //     return "login";
        // }
        model.addAttribute("error", "Identifiant ou mot de passe incorrect");
        return "login";
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