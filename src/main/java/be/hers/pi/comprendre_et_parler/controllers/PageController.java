package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.AppliUser;
import be.hers.pi.comprendre_et_parler.models.Manager;
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




}