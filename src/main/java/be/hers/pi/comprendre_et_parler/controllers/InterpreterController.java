package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("interpretes")
public class InterpreterController {

    @GetMapping("")
    public String showInterpreterList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "") String keyword,
                                      Model model) {
        // logique métier seulement
        return "interpreters/list";
    }

    @GetMapping("/profil/{id}")
    public String showInterpreterProfile(@PathVariable int id,
                                         @RequestHeader(value = "Referer", required = false) String referer,
                                         HttpSession session,
                                         Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        try {
            Interpreter interpreter = SQLWrap.call(
                    (FunctionWithSQLException<Integer, Interpreter>) new DAOInterpreter()::find, id);

            if (interpreter == null) return "redirect:/interpretes";

            Set<Beneficiary> beneficiaries = SQLWrap.call(
                    new DAOBeneficiary()::findReferencedBeneficiaries, id);

            Set<AcademicSkill> allAcademicSkills = SQLWrap.call(new DAOAcademicSkill()::findAll);
            Set<JobSkill> allJobSkills = SQLWrap.call(new DAOJobSkill()::findAll);

            model.addAttribute("interprete", interpreter);
            model.addAttribute("beneficiaries", beneficiaries);
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == id);
            model.addAttribute("isInterpreterAManager", interpreter instanceof Manager);
            model.addAttribute("allAcademicSkills", allAcademicSkills);
            model.addAttribute("allJobSkills", allJobSkills);
            return "interpreters/profile";

        } catch (ConnectionException e) {
            e.printStackTrace();
            return "redirect:/interpretes";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/interpretes";
        }
    }
}