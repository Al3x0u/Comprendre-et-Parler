package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.AcademicSkillService;
import be.hers.pi.comprendre_et_parler.services.JobSkillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gestion")
public class ReferentialController {
    private final AcademicSkillService academicSkillService = new AcademicSkillService();
    private final JobSkillService jobSkillService = new JobSkillService();

    /**
     * Display the referential management page
     * @param model the model to pass data to the view
     * @return the gestion view or redirect if not authorized
     */
    @GetMapping("")
    public String showGestion(Model model) {
        try {
            model.addAttribute("academicSkills", academicSkillService.findAll());
            model.addAttribute("jobSkills", jobSkillService.findAll());
            model.addAttribute("statuts", getHardcodedStatuts());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "gestion";
    }

    // ─── Compétences académiques ─────────────────────────────────────────────
    @PostMapping("/competences/academiques/ajouter")
    public String addAcademicSkill(@RequestParam String designation) {
        try {
            academicSkillService.createAcademicSkill(new AcademicSkill(designation));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    @PostMapping("/competences/academiques/{id}/modifier")
    public String updateAcademicSkill(@PathVariable int id,
                                      @RequestParam String designation) {
        try {
            academicSkillService.updateAcademicSkill(id, new AcademicSkill(designation));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    @PostMapping("/competences/academiques/{id}/supprimer")
    public String deleteAcademicSkill(@PathVariable int id) {
        try {
            academicSkillService.deleteAcademicSkill(new AcademicSkill(id, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    // ─── Compétences métier ──────────────────────────────────────────────────
    @PostMapping("/competences/metier/ajouter")
    public String addJobSkill(@RequestParam String designation) {
        try {
            jobSkillService.createJobSkill(new JobSkill(designation));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    @PostMapping("/competences/metier/{id}/modifier")
    public String updateJobSkill(@PathVariable int id,
                                 @RequestParam String designation) {
        try {
            jobSkillService.updateJobSkill(id, new JobSkill(designation));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    @PostMapping("/competences/metier/{id}/supprimer")
    public String deleteJobSkill(@PathVariable int id) {
        try {
            jobSkillService.deleteJobSkill(new JobSkill(id, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    // ─── Statuts ─────────────────────────────────────────────────────────────
    @PostMapping("/statuts/ajouter")
    public String addStatus(@RequestParam String designation,
                            @RequestParam int hourQuota) {
        return "redirect:/gestion";
    }

    @PostMapping("/statuts/{id}/modifier")
    public String updateStatus(@PathVariable int id,
                               @RequestParam String designation,
                               @RequestParam int hourQuota) {
        return "redirect:/gestion";
    }

    @PostMapping("/statuts/{id}/supprimer")
    public String deleteStatus(@PathVariable int id) {
        return "redirect:/gestion";
    }

    // ─── Données hardcodées temporaires ──────────────────────────────────────

    private List<Status> getHardcodedStatuts() {
        List<Status> statuts = new ArrayList<>();
        statuts.add(new Status(1, "Etudiant", 960));
        statuts.add(new Status(2, "Travailleur", 1200));
        statuts.add(new Status(3, "Sans emploi", 600));
        return statuts;
    }
}