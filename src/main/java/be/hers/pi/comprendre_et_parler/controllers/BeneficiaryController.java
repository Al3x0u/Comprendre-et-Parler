package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("beneficiaires")
public class BeneficiaryController {

    /**
     * Display the BeneficiaryList (only for manager)
     * @param page the page which the user arrive
     * @param keyword the filter option for Beneficiaries
     * @param model the model to pass data to the view
     * @return the beneficiaries list
     */
    @GetMapping("")
    public String showBeneficiaryList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String keyword, Model model) {

        int beneficiariesPerPage = 10;

        List<Beneficiary> allBeneficiaries = buildFakeBeneficiaries();
        List<Beneficiary> filteredBeneficiaries = filterBeneficiaries(allBeneficiaries, keyword);
        int totalBeneficiaries = filteredBeneficiaries.size();

        int totalPages = calculateTotalPages(totalBeneficiaries, beneficiariesPerPage);

        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        List<Beneficiary> beneficiariesForCurrentPage = getBeneficiariesForPage(filteredBeneficiaries, page, beneficiariesPerPage);

        int startItem = 0;
        int endItem = 0;

        if (totalBeneficiaries > 0) {
            startItem = (page - 1) * beneficiariesPerPage + 1;
            endItem = startItem + beneficiariesForCurrentPage.size() - 1;
        }

        model.addAttribute("beneficiaires", beneficiariesForCurrentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageNumber", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalBeneficiaries);
        model.addAttribute("startItem", startItem);
        model.addAttribute("endItem", endItem);
        model.addAttribute("hasPrevious", page > 1);
        model.addAttribute("hasNext", page < totalPages);
        model.addAttribute("currentPage", "beneficiaries");

        return "beneficiaries/list";
    }

    /**
     * Display the profile of a specific beneficiary
     * @param id the id of the beneficiary
     * @param model the model to pass data to the view
     * @return the beneficiary profile page
     */
    @GetMapping("/profil/{id}")
    public String showBeneficiaryProfile(@PathVariable int id, Model model) {
        List<Beneficiary> allBeneficiaries = buildFakeBeneficiaries();

        Beneficiary beneficiary = allBeneficiaries.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);

        if (beneficiary == null) return "redirect:/beneficiaires";

        model.addAttribute("beneficiaire", beneficiary);
        model.addAttribute("currentPage", "beneficiaries");

        return "beneficiaries/profile";
    }

    /**
     * Display the edit form for a specific beneficiary
     * @param id the id of the beneficiary
     * @param model the model to pass data to the view
     * @return the edit beneficiary profile page
     */
    @GetMapping("/profil/{id}/modifier")
    public String showEditBeneficiaryProfile(@PathVariable int id, Model model) {
        List<Beneficiary> allBeneficiaries = buildFakeBeneficiaries();

        Beneficiary beneficiary = allBeneficiaries.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);

        if (beneficiary == null) return "redirect:/beneficiaires";

        model.addAttribute("beneficiaire", beneficiary);
        model.addAttribute("currentPage", "beneficiaries");

        return "beneficiaries/edit-profile";
    }

    /**
     * Update the profile of a specific beneficiary
     * @param id the id of the beneficiary
     * @param formBeneficiary the beneficiary data from the form
     * @return redirect to the beneficiary profile page
     */
    @PostMapping("/profil/{id}/modifier")
    public String updateBeneficiaryProfile(@PathVariable int id, @ModelAttribute("beneficiaire") Beneficiary formBeneficiary) {
        return "redirect:/beneficiaires/profil/" + id;
    }

    /**
     * Filter beneficiaries based on a keyword (login, first name, or last name)
     */
    private List<Beneficiary> filterBeneficiaries(List<Beneficiary> beneficiaries, String keyword) {
        List<Beneficiary> filtered = new ArrayList<>();
        String searchedText = keyword.trim().toLowerCase();

        for (Beneficiary b : beneficiaries) {
            boolean matchesLogin = b.getLogin().toLowerCase().contains(searchedText);
            boolean matchesFirstName = b.getFirstName().toLowerCase().contains(searchedText);
            boolean matchesLastName = b.getLastName().toLowerCase().contains(searchedText);

            if (searchedText.isEmpty() || matchesLogin || matchesFirstName || matchesLastName) {
                filtered.add(b);
            }
        }
        return filtered;
    }

    /**
     * Calculate the total number of pages for pagination
     */
    private int calculateTotalPages(int totalItems, int itemsPerPage) {
        if (totalItems == 0) return 1;
        int totalPages = totalItems / itemsPerPage;
        if (totalItems % itemsPerPage != 0) totalPages++;
        return totalPages;
    }

    /**
     * Get the list of beneficiaries for a specific page
     */
    private List<Beneficiary> getBeneficiariesForPage(List<Beneficiary> beneficiaries, int page, int itemsPerPage) {
        List<Beneficiary> result = new ArrayList<>();
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, beneficiaries.size());
        for (int i = startIndex; i < endIndex; i++) {
            result.add(beneficiaries.get(i));
        }
        return result;
    }

    // FONCTION TEMPORAIRE
    private List<Beneficiary> buildFakeBeneficiaries() {
        Interpreter interpreterRef = new Interpreter(
                1, "i0001", "Jessica", "DuBuisson",
                LocalDate.of(1990, 1, 1), "hashed",
                "jessica@test.be", "0470000000",
                0, 0, null, null, null, null, null, null
        );

        List<Beneficiary> beneficiaries = new ArrayList<>();
        beneficiaries.add(buildFakeBeneficiary(1, "Lucas", "Martin", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(2, "Emma", "Dupont", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(3, "Léa", "Moreau", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(4, "Nathan", "Dubois", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(5, "Camille", "Laurent", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(6, "Théo", "Lefebvre", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(7, "Chloé", "Garcia", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(8, "Maxime", "Rousseau", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(9, "Inès", "Blanc", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(10, "Antoine", "Girard", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(11, "Manon", "Bonnet", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(12, "Romain", "Chevalier", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(13, "Zoé", "Fournier", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(14, "Alexis", "Morel", interpreterRef));
        beneficiaries.add(buildFakeBeneficiary(15, "Clara", "Perrin", interpreterRef));
        return beneficiaries;
    }

    // FONCTION TEMPORAIRE
    private Beneficiary buildFakeBeneficiary(int id, String firstName, String lastName, Interpreter interpreterRef) {
        return new Beneficiary(
                id,
                "b" + String.format("%04d", id),
                firstName,
                lastName,
                LocalDate.of(2005, 1, 1),
                "hashedPassword",
                (firstName + "." + lastName + "@test.be").toLowerCase(),
                "0470/12.34.56",
                null,
                interpreterRef
        );
    }
}
