package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.DTO.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("beneficiaires")
public class BeneficiaryController {

    private final DAOBeneficiary daoBeneficiary = new DAOBeneficiary();

    private final BeneficiaryService beneficiaryService;
    private final InterpreterService interpreterService;
    private final StatusService statusService;

    public BeneficiaryController(BeneficiaryService beneficiaryService, InterpreterService interpreterService, StatusService statusService) {
        this.beneficiaryService = beneficiaryService;
        this.interpreterService = interpreterService;
        this.statusService = statusService;
    }

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
            Beneficiary beneficiary = beneficiaryService.getBeneficiary(id);
            if (beneficiary == null) return "redirect:/beneficiaires";

            model.addAttribute("beneficiaire", beneficiary);
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == id);
            model.addAttribute("interpreters", interpreterService.getAllInterpreters());
            model.addAttribute("age", beneficiaryService.calculateAge(beneficiary.getBirthDate()));
            model.addAttribute("allStatuses", statusService.getAllStatus());
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
    public String showEditBeneficiaryProfile(@PathVariable int id,
                                             @RequestHeader(value = "Referer", required = false) String referer,
                                             Model model) {
        try {
            Beneficiary beneficiary = beneficiaryService.getBeneficiary(id);
            if (beneficiary == null) return "redirect:/beneficiaires";

            model.addAttribute("updateBeneficiaryForm", new UpdateBeneficiaryForm(beneficiary));
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", false);
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
            model.addAttribute("beneficiaireToCreate", new CreateBeneficiaryForm());
            model.addAttribute("allStatuses", statusService.getAllStatus());
            model.addAttribute("interpreters", interpreterService.getAllInterpreters());
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
     * @param form the form containing the new beneficiary's information
     * @param model the Spring model to populate
     * @return the creation view with credentials on success, or a redirect on error
     */
    @PostMapping("/creer")
    public String createBeneficiary(@ModelAttribute CreateBeneficiaryForm form, Model model) {
        try {
            UserCredentials credentials = beneficiaryService.createBeneficiary(form);
            populateCreationModel(model);
            model.addAttribute("credentials", credentials);
            return "beneficiaries/creation";

        } catch (AlreadyExistsException e) {
            model.addAttribute("error", "Ce bénéficiaire existe déjà.");
            return "beneficiaries/creation";
        } catch (ConnectionException | SQLException e) {
            e.printStackTrace();
            return "redirect:/beneficiaires";
        }
    }

    /**
     * Populate the model with the data needed for the beneficiary creation form.
     * @param model the Spring model to populate
     * @throws SQLException if any database error occurs
     * @throws ConnectionException if the database could not be reached
     * @post the model contains a blank CreateBeneficiaryForm, all statuses and all interpreters
     */
    private void populateCreationModel(Model model) throws SQLException, ConnectionException {
        model.addAttribute("beneficiaireToCreate", new CreateBeneficiaryForm());
        model.addAttribute("allStatuses", statusService.getAllStatus());
        model.addAttribute("allInterpreters", interpreterService.getAllInterpreters());
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

    @PostMapping("/profil/{id}/modifier-interprete")
    public String updateInterpreterRef(@PathVariable int id, @RequestParam int interpreterRefId){
        try{
            beneficiaryService.updateInterpreterRef(id, interpreterRefId);
        } catch (SQLException e) {
            return "redirect:/beneficiaires";
        }
        return "redirect:/beneficiaires/profil/" + id;
    }

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