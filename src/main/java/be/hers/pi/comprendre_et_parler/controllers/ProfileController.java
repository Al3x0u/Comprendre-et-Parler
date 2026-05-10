package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.DAOBeneficiary;
import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.InterpreterService;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
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

        if (user instanceof Beneficiary b) {
            List<Interpreter> interpreters = new ArrayList<>();
            try {
                interpreters.add(new InterpreterService().getAssignedInterpreter(user.getId()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            model.addAttribute("userRole", "BENEFICIARY");
            model.addAttribute("beneficiaire", b);
            model.addAttribute("age", Period.between(b.getBirthDate(), LocalDate.now()).getYears());
            model.addAttribute("interpreters", interpreters); // Currently a list but we should only need one
        }
        else {
            List<Beneficiary> beneficiaries = new ArrayList<>();
            try {
                beneficiaries = new ArrayList<>(SQLWrap.call(new DAOBeneficiary()::findReferencedBeneficiaries, user.getId()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if (user instanceof Manager m) {
                model.addAttribute("userRole", "MANAGER");
                model.addAttribute("interprete", m);
                model.addAttribute("beneficiaries", beneficiaries);
            } else if (user instanceof Interpreter i) {
                model.addAttribute("userRole", "INTERPRETER");
                model.addAttribute("interprete", i);
                model.addAttribute("beneficiaries", beneficiaries);
            }
        }


        model.addAttribute("currentPage", "profile");
        model.addAttribute("isManager", user instanceof Manager);
        model.addAttribute("isOwnProfile", true);
        return "profile";
    }
}