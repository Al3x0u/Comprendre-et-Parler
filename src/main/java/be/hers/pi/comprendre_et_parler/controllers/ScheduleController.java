package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.AppliUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ScheduleController {
    /**
     * Display the schedule page
     * @param session the current HTTP session
     * @param model the model to pass data to the view
     * @return the schedule view or redirect to login if not authenticated
     */
    @GetMapping("/schedule")
    public String showSchedule(HttpSession session, Model model) {
        //AppliUser user = (AppliUser) session.getAttribute("user");
        //if (user == null) return "redirect:/login";

        model.addAttribute("currentPage", "schedule");

        List<Map<String, String>> events = getHardcodedMissions();

        ObjectMapper mapper = new ObjectMapper();
        try {
            model.addAttribute("events", mapper.writeValueAsString(events));
        } catch (Exception e) {
            model.addAttribute("events", "[]");
        }

        return "schedule";
    }

    //Temporaire pour créer des missions
    private List<Map<String, String>> getHardcodedMissions() {
        List<Map<String, String>> events = new ArrayList<>();

        Map<String, String> e1 = new HashMap<>();
        e1.put("title", "Mathématique");
        e1.put("start", "2026-04-27T09:00:00");
        e1.put("end", "2026-04-27T10:30:00");
        e1.put("color", getColor("Acceptée"));
        e1.put("type", "Translitération");
        e1.put("room", "M2");
        e1.put("interpreter", "A. Charpentier");
        e1.put("status", "Acceptée");
        events.add(e1);

        Map<String, String> e2 = new HashMap<>();
        e2.put("title", "Physique");
        e2.put("start", "2026-04-27T11:00:00");
        e2.put("end", "2026-04-27T12:30:00");
        e2.put("color", getColor("En attente"));
        e2.put("type", "Transcription");
        e2.put("room", "A6");
        e2.put("interpreter", "J. DuBuisson");
        e2.put("status", "En attente");
        events.add(e2);

        Map<String, String> e3 = new HashMap<>();
        e3.put("title", "Anglais");
        e3.put("start", "2026-04-27T13:30:00");
        e3.put("end", "2026-04-27T15:30:00");
        e3.put("color", getColor("Acceptée"));
        e3.put("type", "Translitération");
        e3.put("room", "A6");
        e3.put("interpreter", "J. DuBuisson");
        e3.put("status", "Acceptée");
        events.add(e3);

        Map<String, String> e4 = new HashMap<>();
        e4.put("title", "Anglais");
        e4.put("start", "2026-04-28T10:30:00");
        e4.put("end", "2026-04-28T12:30:00");
        e4.put("color", getColor("Acceptée"));
        e4.put("type", "Transcription");
        e4.put("room", "E12");
        e4.put("interpreter", "A. Charpentier");
        e4.put("status", "Acceptée");
        events.add(e4);

        Map<String, String> e10 = new HashMap<>();
        e10.put("title", "Géographie");
        e10.put("start", "2026-04-28T10:00:00");
        e10.put("end", "2026-04-28T12:00:00");
        e10.put("color", getColor("En attente"));
        e10.put("type", "Transcription");
        e10.put("room", "B9");
        e10.put("interpreter", "J. DuBuisson");
        e10.put("status", "En attente");
        events.add(e10);

        Map<String, String> e5 = new HashMap<>();
        e5.put("title", "Mathématique");
        e5.put("start", "2026-04-28T13:30:00");
        e5.put("end", "2026-04-28T15:00:00");
        e5.put("color", getColor("Acceptée"));
        e5.put("type", "Transcription");
        e5.put("room", "E12");
        e5.put("interpreter", "J. DuBuisson");
        e5.put("status", "Acceptée");
        events.add(e5);

        Map<String, String> e6 = new HashMap<>();
        e6.put("title", "Histoire");
        e6.put("start", "2026-04-29T11:00:00");
        e6.put("end", "2026-04-29T12:30:00");
        e6.put("color", getColor("Refusée"));
        e6.put("type", "Translitération");
        e6.put("room", "A2");
        e6.put("interpreter", "J. DuBuisson");
        e6.put("status", "Refusée");
        events.add(e6);

        Map<String, String> e7 = new HashMap<>();
        e7.put("title", "Mathématique");
        e7.put("start", "2026-04-29T09:30:00");
        e7.put("end", "2026-04-29T10:30:00");
        e7.put("color", getColor("Acceptée"));
        e7.put("type", "Transcription");
        e7.put("room", "A3");
        e7.put("interpreter", "A. Charpentier");
        e7.put("status", "Acceptée");
        events.add(e7);

        Map<String, String> e8 = new HashMap<>();
        e8.put("title", "Education Physique");
        e8.put("start", "2026-04-30T08:30:00");
        e8.put("end", "2026-04-30T10:30:00");
        e8.put("color", getColor("Horaire de base"));
        e8.put("type", "Transcription");
        e8.put("room", "A3");
        e8.put("interpreter", "A. Charpentier");
        e8.put("status", "Horaire de base");
        events.add(e8);

        Map<String, String> e9 = new HashMap<>();
        e9.put("title", "Chimie");
        e9.put("start", "2026-05-01T08:30:00");
        e9.put("end", "2026-05-01T10:30:00");
        e9.put("color", getColor("Refusée"));
        e9.put("type", "Translitération");
        e9.put("room", "Labo 3");
        e9.put("interpreter", "J. DuBuisson");
        e9.put("status", "Refusée");
        events.add(e9);

        return events;
    }

    private String getColor(String status) {
        return switch (status) {
            case "Acceptée"        -> "#40c057";
            case "En attente"      -> "#fab005";
            case "Refusée"         -> "#fa5252";
            case "Horaire de base" -> "#4dabf7";
            default                -> "#adb5bd";
        };
    }
}
