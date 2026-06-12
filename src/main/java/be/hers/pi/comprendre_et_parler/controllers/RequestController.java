package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/demandes")
public class RequestController {

    private final MissionService missionService = new MissionService();
    private final BeneficiaryService beneficiaryService = new BeneficiaryService();

    /**
     * Display the list of pending requests, optionally filtered by beneficiary.
     * @param beneficiaireId the id of the beneficiary to filter by, optional
     * @param model the Spring model to populate
     * @param session the current HTTP session
     * @return the requests view
     */
    @GetMapping("")
    public String showRequests(@RequestParam(required = false) Integer beneficiaireId,
                               Model model,
                               HttpSession session) {
        try {
            AppliUser user = (AppliUser) session.getAttribute("user");
            if (!(user instanceof Manager))
                return "redirect:/horaire";

            MissionFilter filter = new MissionFilter();
            filter.setStateOfMission(MissionState.PENDING);

            if (beneficiaireId != null) {
                Beneficiary beneficiary = beneficiaryService.getOneBeneficiary(beneficiaireId);
                filter.setBeneficiary(beneficiary);
            }

            List<Mission> demandes = missionService.getByFilter(filter);
            List<Beneficiary> beneficiaires = beneficiaryService.getAllBeneficiaries();

            model.addAttribute("demandes", demandes);
            model.addAttribute("beneficiaires", beneficiaires);
            model.addAttribute("selectedBeneficiaireId", beneficiaireId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "requests";
    }
}