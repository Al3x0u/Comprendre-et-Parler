package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gestion")
public class ReferentialController {

    /**
     * Display the referential management page
     * @param model the model to pass data to the view
     * @return the gestion view or redirect if not authorized
     */
    @GetMapping("")
    public String showGestion(Model model) {
        model.addAttribute("academicSkills", getHardcodedAcademicSkills());
        model.addAttribute("jobSkills", getHardcodedJobSkills());
        model.addAttribute("statuts", getHardcodedStatuts());
        return "gestion";
    }

    // ─── Compétences académiques ─────────────────────────────────────────────
    @PostMapping("/competences/academiques/ajouter")
    public String addAcademicSkill(@RequestParam String designation) {
        return "redirect:/gestion";
    }

    @PostMapping("/competences/academiques/{id}/modifier")
    public String updateAcademicSkill(@PathVariable int id,
                                      @RequestParam String designation) {
        return "redirect:/gestion";
    }

    @PostMapping("/competences/academiques/{id}/supprimer")
    public String deleteAcademicSkill(@PathVariable int id) {
        return "redirect:/gestion";
    }

    // ─── Compétences métier ──────────────────────────────────────────────────
    @PostMapping("/competences/metier/ajouter")
    public String addJobSkill(@RequestParam String designation) {
        return "redirect:/gestion";
    }

    @PostMapping("/competences/metier/{id}/modifier")
    public String updateJobSkill(@PathVariable int id,
                                 @RequestParam String designation) {
        return "redirect:/gestion";
    }

    @PostMapping("/competences/metier/{id}/supprimer")
    public String deleteJobSkill(@PathVariable int id) {
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

    private List<AcademicSkill> getHardcodedAcademicSkills() {
        List<AcademicSkill> skills = new ArrayList<>();
        skills.add(new AcademicSkill(1, "Mathématiques"));
        skills.add(new AcademicSkill(2, "Informatique"));
        skills.add(new AcademicSkill(3, "Langues"));
        return skills;
    }

    private List<JobSkill> getHardcodedJobSkills() {
        List<JobSkill> skills = new ArrayList<>();
        skills.add(new JobSkill(1, "LSFB"));
        skills.add(new JobSkill(2, "Interprétation médicale"));
        skills.add(new JobSkill(3, "Transcription"));
        return skills;
    }

    private List<Status> getHardcodedStatuts() {
        List<Status> statuts = new ArrayList<>();
        statuts.add(new Status(1, "Etudiant", 960));
        statuts.add(new Status(2, "Travailleur", 1200));
        statuts.add(new Status(3, "Sans emploi", 600));
        return statuts;
    }
}