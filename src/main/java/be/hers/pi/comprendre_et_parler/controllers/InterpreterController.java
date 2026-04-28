package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.Location;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

@Controller
@RequestMapping("/interpreters")
public class InterpreterController {

    @GetMapping
    public String showInterpreterList(Model model) {
        // temporaire : liste d'un seul faux interprete
        var interpretes = new ArrayList<Interpreter>();


        City city = new City("Libramont", 6600);
        Location location = new Location("Domicile", city, "Rue des Robertos", "12", 0);

        Interpreter fakeInterpreter = new Interpreter(
                1,
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
                new HashSet<>(),
                new HashSet<>(),
                location,
                new HashSet<>(),
                new HashSet<>()
        );

        interpretes.add(fakeInterpreter);

        model.addAttribute("interpretes", interpretes);
        return "interpreters/list";
    }

    @GetMapping("/profile/{id}")
    public String showInterpreterProfile(@PathVariable int id, Model model) {
        City city = new City("Libramont", 6600);
        Location location = new Location("Domicile", city, "Rue des Robertos", "12", 0);

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
                new HashSet<>(),
                new HashSet<>(),
                location,
                new HashSet<>(),
                new HashSet<>()
        );

        model.addAttribute("interprete", fakeInterpreter);
        return "interpreters/profile";
    }
}