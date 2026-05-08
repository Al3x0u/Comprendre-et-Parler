package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RequestMapping("beneficiaires")
public class BeneficiaryController {

    private final DAOBeneficiary daoBeneficiary = new DAOBeneficiary();

    @GetMapping("")
    public String showBeneficiaryList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "") String keyword,
                                      Model model) {
        // logique métier seulement
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
}