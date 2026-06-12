package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DTO.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("interpretes")
public class InterpreterController {

    private final InterpreterService interpreterService = new InterpreterService();
    private final BeneficiaryService beneficiaryService = new BeneficiaryService();

    /**
     * Display the paginated and filtered list of interpreters
     * @param page the page number to display; defaults to 1
     * @param keyword the search keyword to filter by login, firstName or lastName; defaults to empty
     * @param model the Spring model to populate
     * @return the interpreters list view, or a redirect to the list on error
     */
    @GetMapping("")
    public String showInterpreterList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "") String keyword,
                                      Model model) {
        try {
            List<Interpreter> allInterpreters = interpreterService.getAllInterpreters();
            List<Interpreter> filtered = PaginationUtils.filter(allInterpreters, keyword);
            int total = filtered.size();
            int totalPages = PaginationUtils.calculateTotalPages(total, 10);
            page = Math.max(1, Math.min(page, totalPages));
            List<Interpreter> page_ = PaginationUtils.getPage(filtered, page, 10);
            int startItem = total > 0 ? (page - 1) * 10 + 1 : 0;
            int endItem = total > 0 ? startItem + page_.size() - 1 : 0;

            model.addAttribute("interpretes", page_);
            model.addAttribute("keyword", keyword);
            model.addAttribute("pageNumber", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", total);
            model.addAttribute("startItem", startItem);
            model.addAttribute("endItem", endItem);
            model.addAttribute("hasPrevious", page > 1);
            model.addAttribute("hasNext", page < totalPages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "interpreters/list";
    }

    /**
     * Display the profile of an interpreter
     * @param id the ID of the interpreter to display
     * @param referer the URL of the referring page, used for the back button
     * @param error optional error parameter, triggers an error modal if set
     * @param session the current HTTP session, used to retrieve the connected user
     * @param model the Spring model to populate
     * @return the interpreter profile view, or a redirect to the list if not found
     */
    @GetMapping("/profil/{id}")
    public String showInterpreterProfile(@PathVariable int id,
                                         @RequestHeader(value = "Referer", required = false) String referer,
                                         @RequestParam(required = false) String error,
                                         HttpSession session,
                                         Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        try {
            Interpreter interpreter = interpreterService.getOneInterpreter(id);
            if (interpreter == null) return "redirect:/interpretes";

            interpreter.setAssignedBeneficiaries(beneficiaryService.getBeneficiariesOf(id));

            model.addAttribute("interprete", interpreter);
            model.addAttribute("referer", referer);
            model.addAttribute("error", error);
            model.addAttribute("isOwnProfile", user.getId() == id);
            model.addAttribute("isInterpreterAManager", interpreter instanceof Manager);
            model.addAttribute("newUnavailability", new CreateUnavailability());
            getSkills(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/interpretes";
        }
        return "interpreters/profile";
    }

    /**
     * Display the edit form for an interpreter's profile
     * @param id the ID of the interpreter to edit
     * @param referer the URL of the referring page, used for the back button
     * @param session the current HTTP session, used to retrieve the connected user
     * @param model the Spring model to populate
     * @return the edit profile view, or a redirect to the list if not found
     */
    @GetMapping("/profil/{id}/modifier")
    public String showEditInterpreterProfile(@PathVariable int id,
                                             @RequestHeader(value = "Referer", required = false) String referer,
                                             HttpSession session,
                                             Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        try {
            Interpreter interpreter = interpreterService.getOneInterpreter(id);
            if (interpreter == null) return "redirect:/interpretes";

            sortCities(model);
            model.addAttribute("updateInterpreterForm", new UpdateInterpreterForm(interpreter));
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == id);
        } catch (SQLException | ConnectionException e) {
            e.printStackTrace();
            return "redirect:/interpretes";
        }
        return "interpreters/edit-profile";
    }

    /**
     * Handle the submission of the interpreter profile edit form
     * @param id the id of the interpreter to update
     * @param form the form containing the updated information
     * @param birthdate the birthdate of the interpreter
     * @param model the Spring model to populate
     * @return the interpreter's profile views on success, or the list on error
     */
    @PostMapping("/profil/{id}/modifier")
    public String updateInterpreterProfile(@PathVariable int id,
                                           @ModelAttribute UpdateInterpreterForm form,
                                           @ModelAttribute("birthdate") LocalDate birthdate,
                                           @RequestHeader(value = "Referer", required = false) String referer,
                                           HttpSession session,
                                           Model model) {
        try {
            form.setBirthDate(birthdate);
            interpreterService.updateInterpreter(id, form);
        } catch (AlreadyExistsException e) {
            model.addAttribute("submitState", "Cet utilisateur existe déjà");
            sortCities(model);
            AppliUser user = (AppliUser) session.getAttribute("user");
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == id);
            return "interpreters/edit-profile";
        } catch (SQLException | ConnectionException e) {
            e.printStackTrace();
            return "redirect:/interpretes";
        }
        return "redirect:/interpretes/profil/" + id;
    }

    /**
     * Handle the promotion of an interpreter into a manager
     * @param id the id of the interpreter to promote
     * @return the profile view shows the result of the promotion
     */
    @PostMapping("/profil/{id}/promouvoir")
    public String promoteInterpreter(@PathVariable int id) {
        try {
            interpreterService.promoteInterpreter(id);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/interpretes/profil/" + id + "?error=promote";
        }
        return "redirect:/interpretes/profil/" + id;
    }

    /**
     * Handle the update of an interpreter's weekly and yearly hour quotas
     * @param id the id of the interpreter to update
     * @param hourQuotaWeek the new weekly hour quota
     * @param hourQuotaYear the new yearly hour quota
     * @return a redirect to the interpreter's profile
     */
    @PostMapping("/profil/{id}/quota")
    public String updateQuota(@PathVariable int id,
                              @RequestParam int hourQuotaWeek,
                              @RequestParam int hourQuotaYear) {
        try {
            Interpreter interpreter = interpreterService.getOneInterpreter(id);
            interpreterService.updateQuota(interpreter, hourQuotaWeek, hourQuotaYear);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/interpretes/profil/" + id;
    }

    /**
     * Handle the addition of a job skill to an interpreter, either an existing one or a new one
     * @param id the id of the interpreter
     * @param existingSkillId the id of an existing skill to link, empty if a new skill is created
     * @param newSkillName the designation of a new skill to create and link, empty if an existing skill is chosen
     * @param session the current HTTP session, used to check the user's rights
     * @return a redirect to the interpreter's profile
     */
    @PostMapping("/profil/{id}/competences/metier/ajouter")
    public String addJobSkill(@PathVariable int id,
                              @RequestParam(required = false) Integer existingSkillId,
                              @RequestParam(required = false) String newSkillName,
                              HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager) && user.getId() != id)
                return "redirect:/interpretes/profil/" + id;

            JobSkill skill = null;
            if (existingSkillId != null) {
                skill = new JobSkillService().getAllJobSkills().stream()
                        .filter(s -> s.getId() == existingSkillId)
                        .findFirst().orElse(null);
            } else if (newSkillName != null && !newSkillName.isBlank()) {
                skill = new JobSkill(newSkillName.trim());
            }

            if (skill != null)
                interpreterService.addJobSkill(interpreterService.getOneInterpreter(id), skill);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/interpretes/profil/" + id;
    }

    /**
     * Handle the addition of an academic skill to an interpreter, either an existing one or a new one
     * @param id the id of the interpreter
     * @param existingSkillId the id of an existing skill to link, empty if a new skill is created
     * @param newSkillName the designation of a new skill to create and link, empty if an existing skill is chosen
     * @param session the current HTTP session, used to check the user's rights
     * @return a redirect to the interpreter's profile
     */
    @PostMapping("/profil/{id}/competences/academiques/ajouter")
    public String addAcademicSkill(@PathVariable int id,
                              @RequestParam(required = false) Integer existingSkillId,
                              @RequestParam(required = false) String newSkillName,
                              HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager) && user.getId() != id)
                return "redirect:/interpretes/profil/" + id;

            AcademicSkill skill = null;
            if (existingSkillId != null) {
                skill = new AcademicSkillService().getAllAcademicSkills().stream()
                        .filter(s -> s.getId() == existingSkillId)
                        .findFirst().orElse(null);
            } else if (newSkillName != null && !newSkillName.isBlank()) {
                skill = new AcademicSkill(newSkillName.trim());
            }

            if (skill != null)
                interpreterService.addAcademicSkill(interpreterService.getOneInterpreter(id), skill);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/interpretes/profil/" + id;
    }

    /**
     * Handle the creation of an unavailability to an interpreter
     * @param newUnavailability the unavailability to create
     * @param session the current HTTP session, used to check the user's rights
     * @return a redirect to the interpreter's profile
     */
    @PostMapping("/profil/indisponibilites/ajouter")
    public String addUnavailability(@ModelAttribute("newUnavailability") CreateUnavailability newUnavailability,
                                    HttpSession session) {
        Interpreter user = (Interpreter) session.getAttribute("user");
        System.out.println(user.getId());
        System.out.println(user.getLogin());
        System.out.println(newUnavailability.getReason());
        System.out.println(newUnavailability.getStartDate());
        System.out.println(newUnavailability.getEndDate());

        try {
            interpreterService.createUnavailability(user, newUnavailability);
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/profil";
    }
    
     /**
     * Handle the demotion of a manager into an interpreter
     * @param id the id of the manager to demote
     * @return redirect to the interpreter profile
     */
    @PostMapping("/profil/{id}/retrograder")
    public String demoteInterpreter(@PathVariable int id, Model model,
                                    @RequestHeader(value = "Referer", required = false) String referer) {
        try {
            interpreterService.demoteManager(id);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/interpretes/profil/" + id + "?error=demote";
        }
        return "redirect:/interpretes/profil/" + id;
    }

    /**
     * Handle the deactivation of an interpreter account
     * @param id the id of the interpreter to deactivate
     * @return redirect to the interpreter list on success, or back to the profile with an error parameter on failure
     */
    @PostMapping("/profil/{id}/desactiver")
    public String desactivateInterpreter(@PathVariable int id) {
        try {
            Interpreter interpreter = interpreterService.getOneInterpreter(id);
            if (interpreter != null)
                interpreterService.deleteInterpreter(interpreter);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/interpretes/profil/" + id + "?error=disable";
        }
        return "redirect:/interpretes";
    }

    /**
     * Display the creation form for a new interpreter
     * @param model the Spring model to populate
     * @return the creation view
     */
    @GetMapping("/creer")
    public String showCreateInterpreter(Model model) {
        populateCreationModel(model);
        model.addAttribute("interpreterForm", new CreateInterpreterForm());
        model.addAttribute("submitState", null);

        return "interpreters/creation";
    }

    /**
     * Handle the submission of the interpreter creation form.
     * @param interpreterForm the form containing the new interpreter's information
     * @param birthdate the birthdate of the interpreter
     * @param model the Spring model to populate
     * @return the creation view shows the result of the creation or a redirection if the user wants to change the page
     */
    @PostMapping("/creer")
    public String createInterpreter(@ModelAttribute("interpreterForm") CreateInterpreterForm interpreterForm,
                                    @ModelAttribute("birthdate") LocalDate birthdate,
                                    Model model) {
        try {
            interpreterForm.setBirthDate(birthdate);
            UserCredentials newUser = interpreterService.createInterpreter(interpreterForm);
            model.addAttribute("newUser", newUser);
            model.addAttribute("submitState", "success");
            model.addAttribute("interpreterForm", new CreateInterpreterForm());
        } catch (AlreadyExistsException e) {
            model.addAttribute("submitState", "Cet utilisateur existe déjà");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("submitState", "Une erreur est survenue. Veuillez réessayer.");
        } finally {
            populateCreationModel(model);
            return "interpreters/creation";
        }
    }

    /**
     * Email the new interpreter
     * @param user the new interpreter's information
     * @return redirect to the creation form
     */
    @PostMapping("/creer/notifier")
    public String sendMailCreation(@ModelAttribute("newUser") UserCredentials user,
                                   @RequestHeader(value = "Referer", required = false) String referer) {
        new NotificationService().sendUserCredentials(user);

        if (referer != null) {
            referer = referer.replaceFirst(".*?[^\\/]\\/([^\\/])", "redirect:\\/$1");
            return referer;
        }
        return "redirect:/dashboard";
    }

    /**
     * Populate the model with the data needed for the interpreter creation form.
     * @param model The Spring model to populate
     */
    private void populateCreationModel(Model model) {
        getSkills(model);
        sortCities(model);
    }

    /**
     * Get all the skills from the database
     * @param model The model to which the skills will be added
     */
    private void getSkills(Model model) {
        try {
            model.addAttribute("allAcademicSkills", new AcademicSkillService().getAllAcademicSkills());
            model.addAttribute("allJobSkills", new JobSkillService().getAllJobSkills());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all the cities from the database and sort them according to their compareTo()
     * @param model The model to which the skills will be added
     */
    private void sortCities(Model model) {
        try {
            model.addAttribute("allCities", new CityService().getAllCities());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}