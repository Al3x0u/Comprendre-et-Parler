package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/demandes")
public class RequestController {

    private final MissionService missionService = new MissionService();
    private final BeneficiaryService beneficiaryService = new BeneficiaryService();
    private final JobSkillService jobSkillService = new JobSkillService();
    private final AcademicSkillService academicSkillService = new AcademicSkillService();
    private final CityService cityService = new CityService();

    /**
     * Display the list of pending requests, optionally filtered by beneficiary.
     * @param beneficiaireId the id of the beneficiary to filter by, optional
     * @param model the Spring model to populate
     * @param session the current HTTP session
     * @return the requests view
     */
    @GetMapping("")
    public String showRequests(@RequestParam(required = false) Integer beneficiaireId,
                               @RequestParam(defaultValue = "1") int page,
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

            List<Mission> allDemandes = missionService.getByFilter(filter);
            List<Beneficiary> beneficiaires = beneficiaryService.getAllBeneficiaries();

            int total = allDemandes.size();
            int totalPages = PaginationUtils.calculateTotalPages(total, 10);
            page = Math.max(1, Math.min(page, totalPages));
            List<Mission> pageDemandes = PaginationUtils.getPage(allDemandes, page, 10);
            int startItem = total > 0 ? (page - 1) * 10 + 1 : 0;
            int endItem = total > 0 ? startItem + pageDemandes.size() - 1 : 0;

            model.addAttribute("demandes", pageDemandes);
            model.addAttribute("beneficiaires", beneficiaires);
            model.addAttribute("selectedBeneficiaireId", beneficiaireId);
            model.addAttribute("pageNumber", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", total);
            model.addAttribute("startItem", startItem);
            model.addAttribute("endItem", endItem);
            model.addAttribute("hasPrevious", page > 1);
            model.addAttribute("hasNext", page < totalPages);

            model.addAttribute("professionalSkills", jobSkillService.getAllJobSkills());
            model.addAttribute("academicSkills", academicSkillService.getAllAcademicSkills());
            model.addAttribute("allCities", cityService.getAllCities());
            model.addAttribute("timeSlots", generateTimeSlots());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "requests";
    }

    /**
     * Generates a list of time slots between 08:00 and 22:00, in 30-minute increments.
     * @return the list of time slots formatted as "HH:mm"
     */
    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 8; hour <= 22; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                if (hour == 22 && minute > 0) break;
                slots.add(String.format("%02d:%02d", hour, minute));
            }
        }
        return slots;
    }
}