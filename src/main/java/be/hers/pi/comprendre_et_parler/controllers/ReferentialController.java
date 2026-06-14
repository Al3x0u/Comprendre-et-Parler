package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.AcademicSkillService;
import be.hers.pi.comprendre_et_parler.services.JobSkillService;
import be.hers.pi.comprendre_et_parler.services.StatusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/gestion")
public class ReferentialController {
    private final static AcademicSkillService academicSkillService = new AcademicSkillService();
    private final static JobSkillService jobSkillService = new JobSkillService();
    private final static StatusService statusService = new StatusService();

    /**
     * Display the referential management page, listing academic skills, job skills and statuses.
     * @param model the Spring model to populate
     * @return the gestion view
     */
    @GetMapping("")
    public String showGestion(Model model) {
        try {
            model.addAttribute("academicSkills", academicSkillService.getAllAcademicSkills());
            model.addAttribute("jobSkills", jobSkillService.getAllJobSkills());
            model.addAttribute("statuts", statusService.getAllStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "gestion";
    }

    // ─── Compétences académiques ─────────────────────────────────────────────

    /**
     * Handle the creation of a new academic skill.
     * @param designation the designation of the new academic skill
     * @return a redirect to the referential management page
     */
    @PostMapping("/competences/academiques/ajouter")
    public String addAcademicSkill(@RequestParam String designation) {
        try {
            academicSkillService.createAcademicSkill(new AcademicSkill(designation));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    /**
     * Handle the modification of an existing academic skill.
     * @param id the id of the academic skill to update
     * @param designation the new designation of the academic skill
     * @return a redirect to the referential management page
     */
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

    /**
     * Handle the deletion of an academic skill.
     * @param id the id of the academic skill to delete
     * @return a redirect to the referential management page
     */
    @PostMapping("/competences/academiques/{id}/supprimer")
    public String deleteAcademicSkill(@PathVariable int id) {
        try {
            academicSkillService.deleteAcademicSkill(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    // ─── Compétences métier ──────────────────────────────────────────────────

    /**
     * Handle the creation of a new job skill.
     * @param designation the designation of the new job skill
     * @return a redirect to the referential management page
     */
    @PostMapping("/competences/metier/ajouter")
    public String addJobSkill(@RequestParam String designation) {
        try {
            jobSkillService.createJobSkill(new JobSkill(designation));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    /**
     * Handle the modification of an existing job skill.
     * @param id the id of the job skill to update
     * @param designation the new designation of the job skill
     * @return a redirect to the referential management page
     */
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

    /**
     * Handle the deletion of a job skill.
     * @param id the id of the job skill to delete
     * @return a redirect to the referential management page
     */
    @PostMapping("/competences/metier/{id}/supprimer")
    public String deleteJobSkill(@PathVariable int id) {
        try {
            jobSkillService.deleteJobSkill(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    // ─── Statuts ─────────────────────────────────────────────────────────────

    /**
     * Handle the creation of a new status.
     * @param designation the designation of the new status
     * @param hourQuota the hour quota associated with the new status
     * @return a redirect to the referential management page
     */
    @PostMapping("/statuts/ajouter")
    public String addStatus(@RequestParam String designation,
                            @RequestParam int hourQuota) {
        try {
            statusService.createStatus(new Status(designation, hourQuota));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    /**
     * Handle the modification of an existing status.
     * @param id the id of the status to update
     * @param designation the new designation of the status
     * @param hourQuota the new hour quota associated with the status
     * @return a redirect to the referential management page
     */
    @PostMapping("/statuts/{id}/modifier")
    public String updateStatus(@PathVariable int id,
                               @RequestParam String designation,
                               @RequestParam int hourQuota) {
        try {
            statusService.updateStatus(id, new Status(designation, hourQuota));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }

    /**
     * Handle the deletion of a status.
     * @param id the id of the status to delete
     * @return a redirect to the referential management page
     */
    @PostMapping("/statuts/{id}/supprimer")
    public String deleteStatus(@PathVariable int id) {
        try {
            statusService.deleteStatus(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/gestion";
    }
}