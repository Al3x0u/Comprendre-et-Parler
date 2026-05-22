package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.DTO.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("interpretes")
public class InterpreterController {

    private final InterpreterService interpreterService = new InterpreterService();
    private final DAOBeneficiary daoBeneficiary = new DAOBeneficiary();

    @GetMapping("")
    public String showInterpreterList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "") String keyword,
                                      Model model) {
        try {
            List<Interpreter> allInterpreters = interpreterService.getAllInterpreters();
            List<Interpreter> filtered = filterInterpreters(allInterpreters, keyword);
            int total = filtered.size();
            int totalPages = calculateTotalPages(total, 10);
            page = Math.max(1, Math.min(page, totalPages));
            List<Interpreter> page_ = getInterpretersForPage(filtered, page, 10);
            int startItem = total > 0 ? (page - 1) * 10 + 1 : 0;
            int endItem = total > 0 ? startItem + page_.size() - 1 : 0;

            model.addAttribute("interpretes", page_);
            model.addAttribute("keyword", keyword);
            model.addAttribute("pageNumber", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", total);
            model.addAttribute("startItem", startItem);
            model.addAttribute("endItem", endItem);
            model.addAttribute("hasPrevious", page > 1);
            model.addAttribute("hasNext", page < totalPages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "interpreters/list";
    }

    @GetMapping("/profil/{id}")
    public String showInterpreterProfile(@PathVariable int id,
                                         @RequestHeader(value = "Referer", required = false) String referer,
                                         HttpSession session,
                                         Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        try {
            Interpreter interpreter = interpreterService.getOneInterpreter(id);
            if (interpreter == null) return "redirect:/interpretes";

            List<Beneficiary> beneficiaries = new ArrayList<>(
                    SQLWrap.call(daoBeneficiary::findReferencedBeneficiaries, id));

            model.addAttribute("interprete", interpreter);
            model.addAttribute("beneficiaries", beneficiaries);
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == id);
            model.addAttribute("isInterpreterAManager", interpreter instanceof Manager);
            sortSkills(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/interpretes";
        }
        return "interpreters/profile";
    }

    @GetMapping("/profil/{id}/modifier")
    public String showEditInterpreterProfile(@PathVariable int id,
                                             @RequestHeader(value = "Referer", required = false) String referer,
                                             HttpSession session,
                                             Model model) {
        AppliUser user = (AppliUser) session.getAttribute("user");
        try {
            Interpreter interpreter = interpreterService.getOneInterpreter(id);
            if (interpreter == null) return "redirect:/interpretes";

            model.addAttribute("interprete", interpreter);
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == id);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/interpretes";
        }
        return "interpreters/edit-profile";
    }

    @PostMapping("/profil/{id}/modifier")
    public String updateInterpreterProfile(@PathVariable int id,
                                           @ModelAttribute("interprete") Interpreter formInterpreter,
                                           @RequestParam(required = false) String returnUrl) {
        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/interpretes/profil/" + id;
    }

    @PostMapping("/profil/{id}/promouvoir")
    public String promoteInterpreter(@PathVariable int id) {
        try {
            interpreterService.promoteInterpreter(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/interpretes/profil/" + id;
    }

    @GetMapping("/creer")
    public String showCreateInterpreter(Model model) {
        sortSkills(model);
        model.addAttribute("interpreterForm", new CreateInterpreterForm());
        model.addAttribute("submitState", null);
        return "interpreters/creation";
    }

    @PostMapping("/creer")
    public String createInterpreter(@ModelAttribute("interpreterForm") CreateInterpreterForm interpreterForm,
                                    @RequestParam(required = false) String returnUrl,
                                    Model model) {
        if (returnUrl == null) {
            try {
                Interpreter interpreter = interpreterService.createInterpreter(interpreterForm);

                model.addAttribute("newUser", interpreter);
                model.addAttribute("submitState", "success");
                model.addAttribute("interpreterForm", new CreateInterpreterForm());
            } catch (AlreadyExistsException e) {
                e.printStackTrace();
                model.addAttribute("submitState", "alreadyExist");
                model.addAttribute("interpreterToCreate", interpreterForm);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("submitState", "Une erreur est survenue. Veuillez réessayer.");
            } finally {
                sortSkills(model);
                return "interpreters/creation";
            }
        }
        return "redirect:" + returnUrl;
    }

    /**
     * Get all the skills from the database and sort them according to their compareTo()
     * @param model The model to which the skills will be added
     */
    private void sortSkills(Model model) {
        try {
            List<AcademicSkill> allAcademicSkills = new ArrayList<>(new AcademicSkillService().findAll());
            allAcademicSkills.sort((a1, a2) -> a1.getDesignation().compareTo(a2.getDesignation()));
            List<JobSkill> allJobSkills = new ArrayList<>(new JobSkillService().findAll());
            allJobSkills.sort((j1, j2) -> j1.getDesignation().compareTo(j2.getDesignation()));

            model.addAttribute("allAcademicSkills", allAcademicSkills);
            model.addAttribute("allJobSkills", allJobSkills);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}