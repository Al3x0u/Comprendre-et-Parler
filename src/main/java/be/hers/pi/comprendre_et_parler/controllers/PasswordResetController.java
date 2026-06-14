package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class PasswordResetController {
    private final PasswordResetService passwordResetService = new PasswordResetService();
    private final PasswordService passwordService = new PasswordService();

    /**show the form for password reset request*/
    @GetMapping("/mot-de-passe-oublie")
    public String showForgotForm(){
        return "forgot-password";
    }

    /**
     * Process the reset request : send the link if the email exist. Generic answer only
     */
    @PostMapping("/mot-de-passe-oublie")
    public String requestReset(@RequestParam String email, RedirectAttributes redirectAttributes){
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        passwordResetService.requestReset(email, baseUrl);
        redirectAttributes.addFlashAttribute("info", "Si un compte est" +
                " associé à cette adresse, un lien de réinitialisation vient d'être envoyé.");

        return "redirect:/mot-de-passe-oublie";
    }

    /**Display the form to enter the new password if the token is valid*/
    @GetMapping("/reinitialiser")
    public String showResetPage(@RequestParam String token, Model model, RedirectAttributes redirectAttributes){
        if(!passwordResetService.isTokenValid(token)){
            redirectAttributes.addFlashAttribute("error",  "Lien invalide ou expiré. Veuillez refaire une demande.");
            return "redirect:/mot-de-passe-oublie";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    /** Apply the new password after check of the rules and the token*/
    @PostMapping("/reinitialiser")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes){
        if(!newPassword.equals(confirmPassword)){
            redirectAttributes.addFlashAttribute("resetError", "Les mots de passe ne correspondent pas:");
            return "redirect:/reinitialiser?token=" + token;
        }
        String ruleError = passwordService.validatePasswordRules(newPassword);
        if(ruleError != null){
            redirectAttributes.addFlashAttribute("resetError", ruleError);
            return "redirect:/reinitialiser?token=" + token;
        }

        int userId = passwordResetService.consumeToken(token);
        if(userId < 0){
            redirectAttributes.addFlashAttribute("error", "Lien invalide ou expiré. Veuillez refaire une demande.");
            return "redirect:/mot-de-pass-oublie";
        }
        try{
            passwordService.resetPassword(userId, newPassword);
            redirectAttributes.addFlashAttribute("success", "Mot de passe réinitialisé. Vous pouvez vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la réinitialisation. Veuillez refaire une demande.");
            return "redirect:/mot-de-passe-oublie";
        }
    }
}
