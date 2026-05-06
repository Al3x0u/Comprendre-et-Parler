package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("beneficiaires")
public class BeneficiaryController {

    @GetMapping("")
    public String showBeneficiaryList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "") String keyword,
                                      HttpSession session,
                                      Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager)) return "redirect:/horaire";

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
        model.addAttribute("isManager", true);

        return "beneficiaries/list";
    }

    @GetMapping("/profil/{id}")
    public String showBeneficiaryProfile(@PathVariable int id,
                                         @RequestHeader(value = "Referer", required = false) String referer,
                                         HttpSession session,
                                         Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager) && user.getId() != id) return "redirect:/profil";

        List<Beneficiary> allBeneficiaries = buildFakeBeneficiaries();
        Beneficiary beneficiary = allBeneficiaries.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);

        if (beneficiary == null) return "redirect:/beneficiaires";

        model.addAttribute("beneficiaire", beneficiary);
        model.addAttribute("interpreters", getHardcodedInterpreters());
        model.addAttribute("referer", referer);
        model.addAttribute("age", java.time.Period.between(beneficiary.getBirthDate(), java.time.LocalDate.now()).getYears());
        model.addAttribute("isManager", user instanceof Manager);
        model.addAttribute("currentPage", user instanceof Manager ? "beneficiaries" : "profile");

        return "beneficiaries/profile";
    }

    @GetMapping("/profil/{id}/modifier")
    public String showEditBeneficiaryProfile(@PathVariable int id,
                                             @RequestHeader(value = "Referer", required = false) String referer,
                                             HttpSession session,
                                             Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager) && user.getId() != id) return "redirect:/profil";

        Beneficiary beneficiary;

        if (user instanceof Manager) {
            List<Beneficiary> allBeneficiaries = buildFakeBeneficiaries();
            beneficiary = allBeneficiaries.stream()
                    .filter(b -> b.getId() == id)
                    .findFirst()
                    .orElse(null);
            model.addAttribute("currentPage", "beneficiaries");
            model.addAttribute("isOwnProfile", false);
        } else {
            beneficiary = (Beneficiary) user;
            model.addAttribute("currentPage", "profile");
            model.addAttribute("isOwnProfile", true);
        }

        if (beneficiary == null) return "redirect:/beneficiaires";

        model.addAttribute("beneficiaire", beneficiary);
        model.addAttribute("referer", referer);
        model.addAttribute("isManager", user instanceof Manager);

        return "beneficiaries/edit-profile";
    }

    @PostMapping("/profil/{id}/modifier")
    public String updateBeneficiaryProfile(@PathVariable int id,
                                           @ModelAttribute("beneficiaire") Beneficiary formBeneficiary,
                                           @RequestParam(required = false) String returnUrl,
                                           HttpSession session) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager) && user.getId() != id) return "redirect:/profil";

        if (!(user instanceof Manager)) return "redirect:/profil";

        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/beneficiaires/profil/" + id;
    }

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

    private int calculateTotalPages(int totalItems, int itemsPerPage) {
        if (totalItems == 0) return 1;
        int totalPages = totalItems / itemsPerPage;
        if (totalItems % itemsPerPage != 0) totalPages++;
        return totalPages;
    }

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
                0, 0, null, null, null, null, null
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
        Status status = new Status(1, "Etudiant", 960);
        return new Beneficiary(
                id,
                "b" + String.format("%04d", id),
                firstName,
                lastName,
                LocalDate.of(2005, 1, 1),
                "hashedPassword",
                (firstName + "." + lastName + "@test.be").toLowerCase(),
                "0470/12.34.56",
                status,
                interpreterRef
        );
    }

    // FONCTION TEMPORAIRE
    private List<Interpreter> getHardcodedInterpreters() {
        List<Interpreter> interpreters = new ArrayList<>();
        interpreters.add(new Interpreter(1, "i0001", "Roberto", "Dupont", LocalDate.of(1998, 5, 14), "hashed", "roberto.dupont@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(2, "i0002", "Julie", "Leroy", LocalDate.of(1998, 5, 14), "hashed", "julie.leroy@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(3, "i0003", "Amine", "Bernard", LocalDate.of(1998, 5, 14), "hashed", "amine.bernard@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(4, "i0004", "Sarah", "Simon", LocalDate.of(1998, 5, 14), "hashed", "sarah.simon@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(5, "i0005", "Nicolas", "Legrand", LocalDate.of(1998, 5, 14), "hashed", "nicolas.legrand@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(6, "i0006", "Emma", "Lambert", LocalDate.of(1998, 5, 14), "hashed", "emma.lambert@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(7, "i0007", "Louis", "Fontaine", LocalDate.of(1998, 5, 14), "hashed", "louis.fontaine@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(8, "i0008", "Ines", "Leclercq", LocalDate.of(1998, 5, 14), "hashed", "ines.leclercq@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(9, "i0009", "Thomas", "Remy", LocalDate.of(1998, 5, 14), "hashed", "thomas.remy@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(10, "i0010", "Lina", "Petit", LocalDate.of(1998, 5, 14), "hashed", "lina.petit@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(11, "i0011", "Hugo", "Marchal", LocalDate.of(1998, 5, 14), "hashed", "hugo.marchal@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(12, "i0012", "Nora", "Colin", LocalDate.of(1998, 5, 14), "hashed", "nora.colin@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(13, "i0013", "Lucas", "Hubert", LocalDate.of(1998, 5, 14), "hashed", "lucas.hubert@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(14, "i0014", "Jade", "Henry", LocalDate.of(1998, 5, 14), "hashed", "jade.henry@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        interpreters.add(new Interpreter(15, "i0015", "Noah", "Mertens", LocalDate.of(1998, 5, 14), "hashed", "noah.mertens@test.be", "0470/12.34.56", 0, 0, null, null, null, null, null));
        return interpreters;
    }
}