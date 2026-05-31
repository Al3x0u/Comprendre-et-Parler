package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.DTO.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("beneficiaires")
public class BeneficiaryController {

    private final DAOBeneficiary daoBeneficiary = new DAOBeneficiary();

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("")
    public String showBeneficiaryList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "") String keyword,
                                      Model model) {
        return "beneficiaries/list";
    }

    @GetMapping("/profil/{id}/modifier")
    public String showEditBeneficiaryProfile(@PathVariable int id,
                                             @RequestHeader(value = "Referer", required = false) String referer,
                                             Model model) {
        try {
            Beneficiary beneficiary = SQLWrap.call((FunctionWithSQLException<Integer, Beneficiary>) daoBeneficiary::find, id);
            if (beneficiary == null) return "redirect:/beneficiaires";

            model.addAttribute("beneficiaire", beneficiary);
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", false);
            return "beneficiaries/edit-profile";

        } catch (ConnectionException e) {
            e.printStackTrace();
            return "redirect:/beneficiaires";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/beneficiaires";
        }
    }

    @PostMapping("/profil/{id}/modifier")
    public String editBeneficiaryProfile(@PathVariable int id,
                                         @ModelAttribute("beneficiaire") Beneficiary formBeneficiary,
                                         @RequestParam LocalDate birthdate,
                                         @RequestParam(required = false) String returnUrl) {
            return "redirect:/beneficiaires";
    }

    @GetMapping("/creer")
    public String showCreateBeneficiaryForm(Model model) {
        model.addAttribute("beneficiaryForm", new CreateBeneficiaryForm());
        populateCreationModel(model);
        return "beneficiaries/creation";
    }

    @PostMapping("/creer")
    public String createBeneficiary(@ModelAttribute("beneficiaryForm") CreateBeneficiaryForm beneficiaryForm,
                                    @RequestParam LocalDate birthdate,
                                    @RequestParam(required = false) String returnUrl,
                                    Model model) {
        if (returnUrl == null) {
            try {
                beneficiaryForm.setBirthDate(birthdate);
                UserCredentials newUser = beneficiaryService.createBeneficiary(beneficiaryForm);
                model.addAttribute("newUser", newUser);
                model.addAttribute("submitState", "success");
                model.addAttribute("beneficiaryForm", new CreateBeneficiaryForm());
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

    private void populateCreationModel(Model model) {
        try {
            List<Status> allStatus = new ArrayList<>(SQLWrap.call(new DAOStatus()::findAll));
            allStatus.sort(Status::compareTo);
            List<Interpreter> allInterpreters = new ArrayList<>(SQLWrap.call(new DAOInterpreter()::findAll));
            allInterpreters.sort(Interpreter::compareTo);

            model.addAttribute("allStatuses", allStatus);
            model.addAttribute("allInterpreters", allInterpreters);
        } catch (ConnectionException | SQLException e) {
            e.printStackTrace();
        }
    }
}