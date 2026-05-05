package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("interpretes")
public class InterpreterController {

    /**
     * Display the InterpreterList (only for manager)
     * @param page the page which the user arrive
     * @param keyword the filter option for Interpreters
     * @param model the model to pass data to the view
     * @return the interpreters list
     */
    @GetMapping("")
    public String showInterpreterList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String keyword, Model model) {

        int interpretersPerPage = 10;

        List<Interpreter> allInterpreters = buildFakeInterpreters();
        List<Interpreter> filteredInterpreters = filterInterpreters(allInterpreters, keyword);
        int totalInterpreters = filteredInterpreters.size();

        int totalPages = calculateTotalPages(totalInterpreters, interpretersPerPage);

        if (page < 1) {
            page = 1;
        }

        if (page > totalPages) {
            page = totalPages;
        }

        List<Interpreter> interpretersForCurrentPage = getInterpretersForPage(filteredInterpreters, page, interpretersPerPage);

        int startItem = 0;
        int endItem = 0;

        if (totalInterpreters > 0) {
            startItem = (page - 1) * interpretersPerPage + 1;
            endItem = startItem + interpretersForCurrentPage.size() - 1;
        }

        model.addAttribute("interpretes", interpretersForCurrentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageNumber", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalInterpreters);
        model.addAttribute("startItem", startItem);
        model.addAttribute("endItem", endItem);
        model.addAttribute("hasPrevious", page > 1);
        model.addAttribute("hasNext", page < totalPages);
        model.addAttribute("currentPage", "interpreters");

        return "interpreters/list";
    }
    /**
     * Display the profile of a specific interpreter
     * @param id the id of the interpreter
     * @param model the model to pass data to the view
     * @return the interpreter profile page
     */
    @GetMapping("/profil/{id}")
    public String showInterpreterProfile(@PathVariable int id, Model model) {
        List<Interpreter> allInterpreters = buildFakeInterpreters();

        Interpreter interpreter = allInterpreters.stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);

        if (interpreter == null) return "redirect:/interpretes";

        List<Beneficiary> beneficiaries = getHardcodedBeneficiaries(interpreter);

        model.addAttribute("interprete", interpreter);
        model.addAttribute("beneficiaries", beneficiaries);
        model.addAttribute("currentPage", "interpreters");
        model.addAttribute("actualWeekQuota", 10);
        model.addAttribute("actualYearQuota", 200);

        return "interpreters/profile";
    }

    /**
     * Display the edit form for a specific interpreter
     * @param id the id of the interpreter
     * @param model the model to pass data to the view
     * @return the edit interpreter profile page
     */
    @GetMapping("/profil/{id}/modifier")
    public String showEditInterpreterProfile(@PathVariable int id, Model model) {
        List<Interpreter> allInterpreters = buildFakeInterpreters();

        Interpreter interpreter = allInterpreters.stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);

        if (interpreter == null) return "redirect:/interpretes";

        model.addAttribute("interprete", interpreter);
        model.addAttribute("currentPage", "interpreters");

        return "interpreters/edit-profile";
    }

    /**
     * Update the profile of a specific interpreter
     * @param id the id of the interpreter
     * @param formInterpreter the interpreter data from the form
     * @return redirect to the interpreter profile page
     */
    @PostMapping("/profil/{id}/modifier")
    public String updateInterpreterProfile(@PathVariable int id, @ModelAttribute("interprete") Interpreter formInterpreter) {


        return "redirect:/interpreters/profile/" + id;
    }

    /**
     * Filter interpreters based on a keyword (login, first name, or last name)
     * @param interpreters the list of interpreters to filter
     * @param keyword the search keyword
     * @return the filtered list of interpreters
     */
    private List<Interpreter> filterInterpreters(List<Interpreter> interpreters, String keyword) {
        List<Interpreter> filteredInterpreters = new ArrayList<>();

        String searchedText = keyword.trim().toLowerCase();

        for (Interpreter interpreter : interpreters) {
            String login = interpreter.getLogin().toLowerCase();
            String firstName = interpreter.getFirstName().toLowerCase();
            String lastName = interpreter.getLastName().toLowerCase();

            boolean matchesLogin = login.contains(searchedText);
            boolean matchesFirstName = firstName.contains(searchedText);
            boolean matchesLastName = lastName.contains(searchedText);

            if (searchedText.isEmpty() || matchesLogin || matchesFirstName || matchesLastName) {
                filteredInterpreters.add(interpreter);
            }
        }

        return filteredInterpreters;
    }

    /**
     * Calculate the total number of pages for pagination
     * @param totalItems the total number of items
     * @param itemsPerPage the number of items per page
     * @return the total number of pages
     */
    private int calculateTotalPages(int totalItems, int itemsPerPage) {
        if (totalItems == 0) {
            return 1;
        }

        int totalPages = totalItems/ itemsPerPage;

        if (totalItems %itemsPerPage  != 0) {
            totalPages = totalPages + 1;
        }

        return totalPages;
    }

    /**
     * Get the list of interpreters for a specific page
     * @param interpreters the full list of interpreters
     * @param page the current page number
     * @param itemsPerPage the number of items per page
     * @return the list of interpreters for the current page
     */
    private List<Interpreter> getInterpretersForPage(List<Interpreter> interpreters, int page, int itemsPerPage) {
        List<Interpreter> interpretersForPage = new ArrayList<>();

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = startIndex + itemsPerPage;

        if (endIndex > interpreters.size()) {
            endIndex = interpreters.size();
        }

        for (int i = startIndex; i < endIndex; i++) {
            interpretersForPage.add(interpreters.get(i));
        }

        return interpretersForPage;
    }

    //FONCTION TEMPORAIRE
    private List<Interpreter> buildFakeInterpreters() {
        List<Interpreter> interpreters = new ArrayList<>();

        interpreters.add(buildFakeInterpreter(1, "Roberto", "Dupont"));
        interpreters.add(buildFakeInterpreter(2, "Julie", "Leroy"));
        interpreters.add(buildFakeInterpreter(3, "Amine", "Bernard"));
        interpreters.add(buildFakeInterpreter(4, "Sarah", "Simon"));
        interpreters.add(buildFakeInterpreter(5, "Nicolas", "Legrand"));
        interpreters.add(buildFakeInterpreter(6, "Emma", "Lambert"));
        interpreters.add(buildFakeInterpreter(7, "Louis", "Fontaine"));
        interpreters.add(buildFakeInterpreter(8, "Ines", "Leclercq"));
        interpreters.add(buildFakeInterpreter(9, "Thomas", "Remy"));
        interpreters.add(buildFakeInterpreter(10, "Lina", "Petit"));
        interpreters.add(buildFakeInterpreter(11, "Hugo", "Marchal"));
        interpreters.add(buildFakeInterpreter(12, "Nora", "Colin"));
        interpreters.add(buildFakeInterpreter(13, "Lucas", "Hubert"));
        interpreters.add(buildFakeInterpreter(14, "Jade", "Henry"));
        interpreters.add(buildFakeInterpreter(15, "Noah", "Mertens"));

        return interpreters;
    }

    //FONCTION TEMPORAIRE

    private Interpreter buildFakeInterpreter(int id) {
        return buildFakeInterpreter(id, "Roberto", "Dupont");
    }

    //FONCTION TEMPORAIRE
    private Interpreter buildFakeInterpreter(int id, String firstName, String lastName) {
        City city = new City("Libramont", 6600);
        Location location = new Location("Domicile", city, "Rue des Robertos", "12", 0);

        HashSet<AcademicSkill> academicSkills = new HashSet<>();
        academicSkills.add(new AcademicSkill("Mathématiques"));
        academicSkills.add(new AcademicSkill("Informatique"));

        HashSet<JobSkill> jobSkills = new HashSet<>();
        jobSkills.add(new JobSkill("LSFB"));
        jobSkills.add(new JobSkill("Interprétation médicale"));

        Interpreter fakeInterpreter = new Interpreter(
                id,
                "i" + String.format("%04d", id),
                firstName,
                lastName,
                LocalDate.of(1998, 5, 14),
                "hashedPassword",
                (firstName + "." + lastName + "@test.be").toLowerCase(),
                "0470/12.34.56",
                38,
                1600,
                "Tricycle",
                academicSkills,
                jobSkills,
                location,
                new HashSet<>(),
                new HashSet<>()
        );

        HashSet<ExceptionalUnavailability> unavailabilities = new HashSet<>();

        PunctualTimeSlot slot1 = new PunctualTimeSlot(
                LocalDateTime.of(2026, 5, 2, 9, 0),
                LocalDateTime.of(2026, 5, 2, 12, 0)
        );

        PunctualTimeSlot slot2 = new PunctualTimeSlot(
                LocalDateTime.of(2026, 5, 5, 0, 0),
                LocalDateTime.of(2026, 5, 9, 0, 0)
        );

        ExceptionalUnavailability unavailability1 = new ExceptionalUnavailability(
                "Rendez-vous médical",
                slot1,
                fakeInterpreter
        );

        ExceptionalUnavailability unavailability2 = new ExceptionalUnavailability(
                "Congé maladie",
                slot2,
                fakeInterpreter
        );

        unavailabilities.add(unavailability1);
        unavailabilities.add(unavailability2);

        fakeInterpreter.setUnavailability(unavailabilities);

        return fakeInterpreter;
    }

    private List<Beneficiary> getHardcodedBeneficiaries(Interpreter fakeInterpreter) {
        List<Beneficiary> beneficiaries = new ArrayList<>();
        beneficiaries.add(new Beneficiary(
                1, "B001", "Lucas", "Martin",
                LocalDate.of(2005, 3, 15),
                "hashed", "lucas@hers.be", "0470000002",
                null, fakeInterpreter
        ));
        beneficiaries.add(new Beneficiary(
                2, "B002", "Emma", "Dupont",
                LocalDate.of(2006, 5, 20),
                "hashed", "emma@hers.be", "0470000003",
                null, fakeInterpreter
        ));
        return beneficiaries;
    }
}