package be.hers.pi.comprendre_et_parler.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BeneficiaryController {
    /**
     * Display the beneficiaries page
     * @param session the current HTTP session
     * @return the beneficiaries view or redirect to login if not authenticated
     */
    @GetMapping("/beneficiaries")
    public String showBeneficiaries(HttpSession session, Model model) {
        // AppliUser user = (AppliUser) session.getAttribute("user");
        // if (user == null) return "redirect:/login";
        model.addAttribute("currentPage", "beneficiaries");
        return "beneficiaries";
    }
}
