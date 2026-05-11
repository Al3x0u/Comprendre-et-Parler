package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    /**
     * Display the dashboard page (only for managers)
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the dashboard view or redirect to login if not authenticated
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager)) return "redirect:/horaire";

        int interpreterCount = 25;
        int beneficiaryCount = 50;
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("managerFirstName", user.getFirstName());
        model.addAttribute("interpreterCount", interpreterCount);
        model.addAttribute("beneficiaryCount", beneficiaryCount);
        model.addAttribute("isManager", true);
        return "dashboard";
    }

    @GetMapping("/gestion")
    public String showGestion(HttpSession session, Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager)) return "redirect:/horaire";

        model.addAttribute("currentPage", "gestion");
        model.addAttribute("isManager", true);
        model.addAttribute("academicSkills", getHardcodedAcademicSkills());
        model.addAttribute("jobSkills", getHardcodedJobSkills());
        model.addAttribute("statuts", getHardcodedStatuts());

        return "gestion";
    }

    // FONCTION TEMPORAIRE
    private List<AcademicSkill> getHardcodedAcademicSkills() {
        List<AcademicSkill> skills = new ArrayList<>();
        skills.add(new AcademicSkill(1, "Mathématiques"));
        skills.add(new AcademicSkill(2, "Informatique"));
        skills.add(new AcademicSkill(3, "Langues"));
        return skills;
    }

    // FONCTION TEMPORAIRE
    private List<JobSkill> getHardcodedJobSkills() {
        List<JobSkill> skills = new ArrayList<>();
        skills.add(new JobSkill(1, "LSFB"));
        skills.add(new JobSkill(2, "Translitération"));
        skills.add(new JobSkill(3, "Transcription"));
        return skills;
    }

    // FONCTION TEMPORAIRE
    private List<Status> getHardcodedStatuts() {
        List<Status> statuts = new ArrayList<>();
        statuts.add(new Status(1, "Etudiant", 960));
        statuts.add(new Status(2, "Travailleur", 1200));
        statuts.add(new Status(3, "Sans emploi", 600));
        return statuts;
    }
}
