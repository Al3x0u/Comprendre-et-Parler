package be.hers.pi.comprendre_et_parler.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InterpreterController {
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
}
