package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController (PasswordService service){
        this.passwordService = service;
    }

    @PostMapping("/profil/modifier-mot-de-passe")
    public String changePassword(
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        AppliUser user = (AppliUser) session.getAttribute("user");

        if(!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Les mots de passe ne correspondent pas.");
            return "redirect:" + request.getHeader("Referer");
        }

        try {
            passwordService.changePassword(user, newPassword);

            session.setAttribute("user", user);

            redirectAttributes.addFlashAttribute("passwordSuccess", "Mot de passe modifié.");
            return "redirect:" + request.getHeader("Referer");

        } catch(Exception e) {
            redirectAttributes.addFlashAttribute("passwordError", "Erreur lors de la modification du mot de passe.");
            return "redirect:" + request.getHeader("Referer");
        }
    }
}
