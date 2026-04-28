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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@Controller
@RequestMapping("/interpreters")
public class InterpreterController {

    @GetMapping
    public String showInterpreterList(Model model) {
        var interpretes = new ArrayList<Interpreter>();
        interpretes.add(buildFakeInterpreter(1));

        model.addAttribute("interpretes", interpretes);
        return "interpreters/list";
    }

    @GetMapping("/profile/{id}")
    public String showInterpreterProfile(@PathVariable int id, Model model) {
        Interpreter fakeInterpreter = buildFakeInterpreter(id);

        model.addAttribute("interprete", fakeInterpreter);
        return "interpreters/profile";
    }

    private Interpreter buildFakeInterpreter(int id) {
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
                "i0001",
                "Roberto",
                "Dupont",
                LocalDate.of(1998, 5, 14),
                "hashedPassword",
                "roberto.dupont@test.be",
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

        unavailabilities.add(new ExceptionalUnavailability(
                "Rendez-vous médical",
                slot1,
                fakeInterpreter
        ));

        unavailabilities.add(new ExceptionalUnavailability(
                "Congé maladie",
                slot2,
                fakeInterpreter
        ));

        fakeInterpreter.setUnavailability(unavailabilities);

        return fakeInterpreter;
    }
}