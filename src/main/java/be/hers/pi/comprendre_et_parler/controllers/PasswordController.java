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
            @RequestParam String currentPassword,
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

        // Validation des règles
        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("passwordError", "Le mot de passe doit contenir au moins 8 caractères.");
            return "redirect:" + request.getHeader("Referer");
        }
        if (!newPassword.matches(".*[A-Z].*")) {
            redirectAttributes.addFlashAttribute("passwordError", "Le mot de passe doit contenir au moins une majuscule.");
            return "redirect:" + request.getHeader("Referer");
        }
        if (!newPassword.matches(".*[0-9].*")) {
            redirectAttributes.addFlashAttribute("passwordError", "Le mot de passe doit contenir au moins un chiffre.");
            return "redirect:" + request.getHeader("Referer");
        }
        if (!newPassword.matches(".*[^a-zA-Z0-9].*")) {
            redirectAttributes.addFlashAttribute("passwordError", "Le mot de passe doit contenir au moins un caractère spécial.");
            return "redirect:" + request.getHeader("Referer");
        }

        if(!passwordService.verifyCurrentPassword(user, currentPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Mot de passe actuel incorrect.");
            return "redirect:" + request.getHeader("Referer");
        }

        if(newPassword.equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Le nouveau mot de passe doit être différent de l'ancien.");
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
