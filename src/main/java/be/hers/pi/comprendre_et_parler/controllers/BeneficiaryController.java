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

    @GetMapping("/creer")
    public String showCreateBeneficiaryForm(Model model) {
        try {
            model.addAttribute("beneficiaireToCreate", new CreateBeneficiaryForm());
            model.addAttribute("allStatuses", SQLWrap.call(new DAOStatus()::findAll));
            model.addAttribute("allInterpreters", SQLWrap.call(new DAOInterpreter()::findAll));
            return "beneficiaries/creation";
        } catch (ConnectionException e) {
            e.printStackTrace();
            return "redirect:/beneficiaires";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/beneficiaires";
        }
    }

    @PostMapping("/creer")
    public String createBeneficiary(@ModelAttribute CreateBeneficiaryForm form, Model model) {
        try {
            BeneficiaryCredentials credentials = beneficiaryService.createBeneficiary(form);
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

    private void populateCreationModel(Model model) throws SQLException, ConnectionException {
        model.addAttribute("beneficiaireToCreate", new CreateBeneficiaryForm());
        model.addAttribute("allStatuses", SQLWrap.call(new DAOStatus()::findAll));
        model.addAttribute("allInterpreters", SQLWrap.call(new DAOInterpreter()::findAll));
    }
}