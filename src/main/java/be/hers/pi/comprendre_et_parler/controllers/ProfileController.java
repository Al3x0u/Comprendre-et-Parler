package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProfileController {

    /**
     * Display the profile of the connected user
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the profile page
     */
    @GetMapping("/profil")
    public String showProfile(HttpSession session, Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (user instanceof Manager m) {
            model.addAttribute("userRole", "MANAGER");
            model.addAttribute("interprete", m);
            model.addAttribute("beneficiaries", getHardcodedBeneficiaries(m));
        } else if (user instanceof Interpreter i) {
            model.addAttribute("userRole", "INTERPRETER");
            model.addAttribute("interprete", i);
            model.addAttribute("beneficiaries", getHardcodedBeneficiaries(i));
        } else if (user instanceof Beneficiary b) {
            model.addAttribute("userRole", "BENEFICIARY");
            model.addAttribute("beneficiaire", b);
            model.addAttribute("age", Period.between(b.getBirthDate(), LocalDate.now()).getYears());
            model.addAttribute("interpreters", getHardcodedInterpreters());
        }
        model.addAttribute("currentPage", "profile");
        model.addAttribute("isManager", user instanceof Manager);
        return "profile";
    }

    // FONCTION TEMPORAIRE
    private List<Beneficiary> getHardcodedBeneficiaries(Interpreter interpreterRef) {
        List<Beneficiary> beneficiaries = new ArrayList<>();
        beneficiaries.add(new Beneficiary(1, "b0001", "Lucas", "Martin", LocalDate.of(2005, 3, 15), "hashed", "lucas@hers.be", "0470000002", new Status(1, "Etudiant", 960), interpreterRef));
        beneficiaries.add(new Beneficiary(2, "b0002", "Emma", "Dupont", LocalDate.of(2006, 5, 20), "hashed", "emma@hers.be", "0470000003", new Status(1, "Etudiant", 960), interpreterRef));
        return beneficiaries;
    }

    // FONCTION TEMPORAIRE
    private List<Interpreter> getHardcodedInterpreters() {
        List<Interpreter> interpreters = new ArrayList<>();
        interpreters.add(new Interpreter(1, "i0001", "Roberto", "Dupont", LocalDate.of(1998, 5, 14), "hashed",
                "roberto.dupont@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(2, "i0002", "Julie", "Leroy", LocalDate.of(1998, 5, 14), "hashed",
                "julie.leroy@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        return interpreters;
    }
}