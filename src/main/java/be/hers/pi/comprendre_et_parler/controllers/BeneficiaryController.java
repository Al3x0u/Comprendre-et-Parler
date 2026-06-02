package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DTO.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("beneficiaires")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService = new BeneficiaryService();
    private final InterpreterService interpreterService = new InterpreterService();
    private final StatusService statusService = new StatusService();

    /**
     * Display the paginated and filtered list of beneficiaries.
     * @param page the page number to display, defaults to 1
     * @param keyword the search keyword to filter by login, firstName or lastName, defaults to empty
     * @param error optional error parameter, triggers an error message if set to hasMissions
     * @param model the Spring model to populate
     * @return the beneficiaries list view, or a redirect to the list on error
     */
    @GetMapping("")
    public String showBeneficiaryList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "") String keyword,
                                      @RequestParam(defaultValue = "") String error,
                                      Model model) {
        try {
            List<Beneficiary> allBeneficiaries = beneficiaryService.getAllBeneficiaries();
            List<Beneficiary> filtered = PaginationUtils.filter(allBeneficiaries, keyword);
            int total = filtered.size();
            int totalPages = PaginationUtils.calculateTotalPages(total, 10);
            page = Math.max(1, Math.min(page, totalPages));
            List<Beneficiary> page_ = PaginationUtils.getPage(filtered, page, 10);
            int startItem = total > 0 ? (page - 1) * 10 + 1 : 0;
            int endItem = total > 0 ? startItem + page_.size() - 1 : 0;

            model.addAttribute("beneficiaires", page_);
            model.addAttribute("keyword", keyword);
            model.addAttribute("pageNumber", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", total);
            model.addAttribute("startItem", startItem);
            model.addAttribute("endItem", endItem);
            model.addAttribute("hasPrevious", page > 1);
            model.addAttribute("hasNext", page < totalPages);
            if (!error.isEmpty()) {
                model.addAttribute("errorMessage", "Ce bénéficiaire ne peut pas être supprimé car il est lié à des missions actives.");
            }
        } catch (SQLException e) {
            return "redirect:/beneficiaires";
        }catch (ConnectionException e){
            return "redirect:/beneficiaires";
        }
        return "beneficiaries/list";
    }

    /**
     * Display the profile of a beneficiary.
     * @param id the id of the beneficiary to display
     * @param referer the URL of the referring page, used for the back button
     * @param session the current HTTP session, used to retrieve the connected user
     * @param model the Spring model to populate
     * @return the beneficiary profile view, or a redirect to the list if not found
     */
    @GetMapping("/profil/{id}")
    public String showBeneficiaryProfile(@PathVariable int id,
                                         @RequestHeader(value = "Referer", required = false) String referer,
                                         HttpSession session,
                                         Model model) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            Beneficiary beneficiary = beneficiaryService.getOneBeneficiary(id);
            if (beneficiary == null) return "redirect:/beneficiaires";

            model.addAttribute("beneficiaire", beneficiary);
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == id);
            model.addAttribute("interpreters", interpreterService.getAllInterpreters());
            model.addAttribute("age", beneficiaryService.calculateAge(beneficiary.getBirthDate()));
            model.addAttribute("allStatuses", statusService.findAll());
        } catch (SQLException e) {
            return "redirect:/beneficiaires";
        }catch (ConnectionException e){
            return "redirect:/beneficiaires";
        }

        return "beneficiaries/profile";
    }

    /**
     * Display the edit form for a beneficiary's profile.
     * @param id the id of the beneficiary to edit
     * @param referer the URL of the referring page, used for the cancel button
     * @param model the Spring model to populate
     * @return the edit profile view, or a redirect to the list if not found
     */
    @GetMapping("/profil/{id}/modifier")
    public String showEditBeneficiaryProfile(HttpSession session, @PathVariable int id,
                                             @RequestHeader(value = "Referer", required = false) String referer,
                                             Model model) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            Beneficiary beneficiary = beneficiaryService.getOneBeneficiary(id);
            if (beneficiary == null) return "redirect:/beneficiaires";

            model.addAttribute("updateBeneficiaryForm", new UpdateBeneficiaryForm(beneficiary));
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == beneficiary.getId());
        } catch (SQLException e) {
            return "redirect:/beneficiaires";
        } catch (ConnectionException e) {
            return "redirect:/beneficiaires";
        }
        return "beneficiaries/edit-profile";
    }

    /**
     * Display the creation form for a new beneficiary.
     * @param model the Spring model to populate
     * @return the creation view, or a redirect to the list on error
     */
    @GetMapping("/creer")
    public String showCreateBeneficiaryForm(Model model) {
        try {
            model.addAttribute("beneficiaryForm", new CreateBeneficiaryForm());
            model.addAttribute("allStatuses", statusService.findAll());
            model.addAttribute("allInterpreters", interpreterService.getAllInterpreters());
            model.addAttribute("submitState", null);
            return "beneficiaries/creation";
        } catch (ConnectionException e) {
            e.printStackTrace();
            return "redirect:/beneficiaires";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/beneficiaires";
        }
    }

    /**
     * Handle the submission of the beneficiary creation form.
     * @param beneficiaryForm the form containing the new beneficiary's information
     * @param model the Spring model to populate
     * @return the creation view with credentials on success, or a redirect on error
     */
    @PostMapping("/creer")
    public String createBeneficiary(@ModelAttribute("beneficiaryForm") CreateBeneficiaryForm beneficiaryForm,
                                    @ModelAttribute("birthdate") LocalDate birthdate,
                                    @RequestParam(required = false) String returnUrl,
                                    Model model) {
        if (returnUrl == null) {
            try {
                beneficiaryForm.setBirthDate(birthdate);
                UserCredentials newUser = beneficiaryService.createBeneficiary(beneficiaryForm);
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
                return "beneficiaries/creation";
            }
        }
        return "redirect:" + returnUrl;
    }

    /**
     * Populate the model with the data needed for the beneficiary creation form.
     * @param model the Spring model to populate
     * @throws SQLException if any database error occurs
     * @throws ConnectionException if the database could not be reached
     * @post the model contains a blank CreateBeneficiaryForm, all statuses and all interpreters
     */
    private void populateCreationModel(Model model) {
        try {
            model.addAttribute("beneficiaryForm", new CreateBeneficiaryForm());
            model.addAttribute("allStatuses", statusService.findAll());
            model.addAttribute("allInterpreters", interpreterService.getAllInterpreters());
        } catch (SQLException e ) {
            e.printStackTrace();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle the deletion of a beneficiary.
     * @param id the id of the beneficiary to delete
     * @return a redirect to the list, with an error parameter if the beneficiary has active missions
     */
    @PostMapping("/{id}/supprimer")
    public String deleteBeneficiary(@PathVariable int id){
        try {
            beneficiaryService.deleteBeneficiary(id);
        }catch (IllegalArgumentException e){
            return "redirect:/beneficiaires?error=hasMissions";
        } catch (SQLException e) {
            return "redirect:/beneficiaires";
        }
        return "redirect:/beneficiaires";
    }

    /**
     * Handle the submission of the beneficiary profile edit form.
     * @param id the id of the beneficiary to update
     * @param form the form containing the updated information
     * @return a redirect to the beneficiary's profile on success, or to the list on error
     */
    @PostMapping("/profil/{id}/modifier")
    public String updateBeneficiary(@PathVariable int id, @ModelAttribute UpdateBeneficiaryForm form){
        try{
            beneficiaryService.updateBeneficiary(id, form);
        } catch (SQLException e) {
            return "redirect:/beneficiaires";
        }
        return "redirect:/beneficiaires/profil/"+ id;
    }

    /**
     * Handle the submission of the interpreter reference modification form.
     * @param id the id of the beneficiary to update
     * @param interpreterRefId the id of the new reference interpreter
     * @return a redirect to the beneficiary's profile on success, or to the list on error
     */
    @PostMapping("/profil/{id}/modifier-interprete")
    public String updateInterpreterRef(@PathVariable int id, @RequestParam int interpreterRefId){
        try{
            beneficiaryService.updateInterpreterRef(id, interpreterRefId);
        } catch (SQLException e) {
            return "redirect:/beneficiaires";
        }
        return "redirect:/beneficiaires/profil/" + id;
    }

    /**
     * Handle the submission of the status modification form.
     * @param id the id of the beneficiary to update
     * @param statusId the id of the new status
     * @return a redirect to the beneficiary's profile on success, or to the list on error
     */
    @PostMapping("/profil/{id}/modifier-statut")
    public String updateStatus(@PathVariable int id, @RequestParam int statusId){
        try{
            beneficiaryService.updateStatus(id, statusId);
        } catch (SQLException e) {
            return "redirect:/beneficiaires";
        }
        return "redirect:/beneficiaires/profil/" + id;
    }
}