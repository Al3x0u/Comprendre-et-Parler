package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.DAOAcademicSkill;
import be.hers.pi.comprendre_et_parler.DAOs.DAOBeneficiary;
import be.hers.pi.comprendre_et_parler.DAOs.DAOJobSkill;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.InterpreterService;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("interpretes")
public class InterpreterController {

    @GetMapping("")
    public String showInterpreterList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "") String keyword,
                                      HttpSession session,
                                      Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager)) return "redirect:/horaire";

        int interpretersPerPage = 10;
        List<Interpreter> allInterpreters = new ArrayList<>();
        try {
             allInterpreters = new InterpreterService().getAllInterpreters();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        List<Interpreter> filteredInterpreters = filterInterpreters(allInterpreters, keyword);
        int totalInterpreters = filteredInterpreters.size();
        int totalPages = calculateTotalPages(totalInterpreters, interpretersPerPage);

        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

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
        model.addAttribute("isManager", true);

        return "interpreters/list";
    }

    @GetMapping("/profil/{id}")
    public String showInterpreterProfile(@PathVariable int id,
                                         @RequestHeader(value = "Referer", required = false) String referer,
                                         HttpSession session,
                                         Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager) && user.getId() != id) {
            if (!(user instanceof Beneficiary b) || b.getInterpreterRef() == null || b.getInterpreterRef().getId() != id) {
                return "redirect:/profil";
            }
        }

        List<Interpreter> allInterpreters = new ArrayList<>();
        List<AcademicSkill> allAcademicSkills = new ArrayList<>();
        List<JobSkill> allJobSkills = new ArrayList<>();
        try {
            allInterpreters = new InterpreterService().getAllInterpreters();
            allAcademicSkills = new ArrayList<>(SQLWrap.call(new DAOAcademicSkill()::findAll));
            allJobSkills = new ArrayList<>(SQLWrap.call(new DAOJobSkill()::findAll));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Interpreter interpreter = allInterpreters.stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);

        if (interpreter == null) return "redirect:/interpretes";

        List<Beneficiary> beneficiaries = new ArrayList<>();
        try {
            beneficiaries = new ArrayList<>(SQLWrap.call(new DAOBeneficiary()::findReferencedBeneficiaries, id));
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        model.addAttribute("interprete", interpreter);
        model.addAttribute("beneficiaries", beneficiaries);
        model.addAttribute("actualWeekQuota", 10);
        model.addAttribute("actualYearQuota", 200);
        model.addAttribute("referer", referer);
        model.addAttribute("isManager", user instanceof Manager);
        model.addAttribute("isInterpreterAManager", interpreter instanceof Manager);
        model.addAttribute("isOwnProfile", user.getId() == id);
        model.addAttribute("currentPage", user instanceof Manager m && m.getId() == id ? "profile" : user instanceof Manager ? "interpreters" : "profile");
        model.addAttribute("allAcademicSkills", allAcademicSkills);
        model.addAttribute("allJobSkills", allJobSkills);
        return "interpreters/profile";
    }

    @GetMapping("/profil/{id}/modifier")
    public String showEditInterpreterProfile(@PathVariable int id,
                                             @RequestHeader(value = "Referer", required = false) String referer,
                                             HttpSession session,
                                             Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager) && user.getId() != id) return "redirect:/profil";

        Interpreter interpreter;

        if (user instanceof Manager m) {
            List<Interpreter> allInterpreters = new ArrayList<>();
            try {
                allInterpreters = new InterpreterService().getAllInterpreters();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            interpreter = allInterpreters.stream()
                    .filter(i -> i.getId() == id)
                    .findFirst()
                    .orElse(null);
            model.addAttribute("isOwnProfile", m.getId() == id);
            model.addAttribute("currentPage", m.getId() == id ? "profile" : "interpreters");
            model.addAttribute("isManager", true);
        } else {
            interpreter = (Interpreter) user;
            model.addAttribute("currentPage", "profile");
            model.addAttribute("isOwnProfile", true);
        }

        if (interpreter == null) return "redirect:/interpretes";

        model.addAttribute("interprete", interpreter);
        model.addAttribute("referer", referer);
        model.addAttribute("isManager", user instanceof Manager);

        return "interpreters/edit-profile";
    }

    @PostMapping("/profil/{id}/modifier")
    public String updateInterpreterProfile(@PathVariable int id,
                                           @ModelAttribute("interprete") Interpreter formInterpreter,
                                           @RequestParam(required = false) String returnUrl,
                                           HttpSession session) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager) && user.getId() != id) return "redirect:/profil";

        if (!(user instanceof Manager)) return "redirect:/profil";

        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/interpretes/profil/" + id;
    }

    @PostMapping("/profil/{id}/promouvoir")
    public String promoteInterpreter(@PathVariable int id, HttpSession session) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager)) return "redirect:/horaire";
        try {
            new InterpreterService().promoteInterpreter(id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/interpretes/profil/" + id;
    }

    @GetMapping("/creation")
    public String showCreateInterpreter(HttpSession session, Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager) ) return "redirect:/horaire";

        Set<AcademicSkill> academicSkills = new HashSet<>();
        Set<JobSkill> jobSkills = new HashSet<>();
        try {
            academicSkills = SQLWrap.call(new DAOAcademicSkill()::findAll);
            jobSkills = SQLWrap.call(new DAOJobSkill()::findAll);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("interpreterToCreate", new Interpreter());
        model.addAttribute("allAcademicSkills", new ArrayList<AcademicSkill>(academicSkills));
        model.addAttribute("allJobSkills", new ArrayList<JobSkill>(jobSkills));
        model.addAttribute("isManager", true);

        return "interpreters/creation";
    }

    @PostMapping("/creation")
    public String createInterpreter(@ModelAttribute("interpreterToCreate") Interpreter interpreterToCreate,
                                    @RequestParam(required = false) String returnUrl,
                                    HttpSession session) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!(user instanceof Manager)) return "redirect:/horaire";
        try {
            new InterpreterService().createInterpreter(interpreterToCreate);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/interpretes";
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
        if (totalItems == 0) return 1;
        int totalPages = totalItems / itemsPerPage;
        if (totalItems % itemsPerPage != 0) totalPages++;
        return totalPages;
    }

    private List<Interpreter> getInterpretersForPage(List<Interpreter> interpreters, int page, int itemsPerPage) {
        List<Interpreter> interpretersForPage = new ArrayList<>();
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, interpreters.size());
        for (int i = startIndex; i < endIndex; i++) {
            interpretersForPage.add(interpreters.get(i));
        }
        return interpretersForPage;
    }


    private List<Beneficiary> getHardcodedBeneficiaries(Interpreter fakeInterpreter) {
        List<Beneficiary> beneficiaries = new ArrayList<>();
        beneficiaries.add(new Beneficiary(1, "B001", "Lucas", "Martin",
                LocalDate.of(2005, 3, 15), "hashed", "lucas@hers.be", "0470000002", null, fakeInterpreter));
        beneficiaries.add(new Beneficiary(2, "B002", "Emma", "Dupont",
                LocalDate.of(2006, 5, 20), "hashed", "emma@hers.be", "0470000003", null, fakeInterpreter));
        return beneficiaries;
    }

    private List<JobSkill> getHardcodedJobSkills() {
        List<JobSkill> jobSkills = new ArrayList<>();
        jobSkills.add(new JobSkill("LSFB"));
        jobSkills.add(new JobSkill("Translitération"));
        jobSkills.add(new JobSkill("Interpreation"));
        return jobSkills;
    }

    private List<AcademicSkill> getHardcodedAcademicSkills() {
        List<AcademicSkill> academicSkills = new ArrayList<>();
        academicSkills.add(new AcademicSkill("Mathématique"));
        academicSkills.add(new AcademicSkill("Sciences"));
        academicSkills.add(new AcademicSkill("Anglais"));
        return academicSkills;
    }
}