package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.models.ExceptionalUnavailability;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.JobSkill;
import be.hers.pi.comprendre_et_parler.models.Location;
import be.hers.pi.comprendre_et_parler.models.PunctualTimeSlot;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/interpreters")
public class InterpreterController {

    @GetMapping
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

    @GetMapping("/profile/{id}")
    public String showInterpreterProfile(@PathVariable int id, Model model) {
        Interpreter fakeInterpreter = buildFakeInterpreter(id);

        model.addAttribute("interprete", fakeInterpreter);
        model.addAttribute("currentPage", "interpreters");

        return "interpreters/profile";
    }

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
}