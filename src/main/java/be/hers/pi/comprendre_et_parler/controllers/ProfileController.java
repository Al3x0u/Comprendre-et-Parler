package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class ProfileController {

    private final BeneficiaryService beneficiaryService = new BeneficiaryService();

    /**
     * Display the profile of the connected user
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the profile page
     */
    @GetMapping("/profil")
    public String showProfile(HttpSession session, Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");

        try {
            if (user instanceof Manager m) {
                m.setAssignedBeneficiaries(beneficiaryService.getBeneficiariesOf(m.getId()));
                model.addAttribute("interprete", m);
                model.addAttribute("isInterpreterAManager", true);
                model.addAttribute("userRole", "MANAGER");
                model.addAttribute("allAcademicSkills", new AcademicSkillService().getAllAcademicSkills());
                model.addAttribute("allJobSkills", new JobSkillService().getAllJobSkills());
            } else if (user instanceof Interpreter i) {
                i.setAssignedBeneficiaries(beneficiaryService.getBeneficiariesOf(i.getId()));
                model.addAttribute("interprete", i);
                model.addAttribute("userRole", "INTERPRETER");
                model.addAttribute("allAcademicSkills", new AcademicSkillService().getAllAcademicSkills());
                model.addAttribute("allJobSkills", new JobSkillService().getAllJobSkills());
            } else if (user instanceof Beneficiary b) {
                model.addAttribute("beneficiaire", b);
                model.addAttribute("age", Period.between(b.getBirthDate(), LocalDate.now()).getYears());
                model.addAttribute("userRole", "BENEFICIARY");
            }

            model.addAttribute("isOwnProfile", true);
            return "profile";

        } catch (ConnectionException e) {
            e.printStackTrace();
            return "redirect:/login";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }
}