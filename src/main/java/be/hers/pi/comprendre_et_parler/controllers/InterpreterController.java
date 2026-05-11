package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.DTO.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("interpretes")
public class InterpreterController {

    private final InterpreterService interpreterService = new InterpreterService();
    private final DAOBeneficiary daoBeneficiary = new DAOBeneficiary();
    private final DAOAcademicSkill daoAcademicSkill = new DAOAcademicSkill();
    private final DAOJobSkill daoJobSkill = new DAOJobSkill();
    private final DAOInterpreter daoInterpreter = new DAOInterpreter();

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
            Interpreter interpreter = SQLWrap.call(
                    (FunctionWithSQLException<Integer, Interpreter>) daoInterpreter::find, id);
            if (interpreter == null) return "redirect:/interpretes";

            List<Beneficiary> beneficiaries = new ArrayList<>(
                    SQLWrap.call(daoBeneficiary::findReferencedBeneficiaries, id));

            model.addAttribute("interprete", interpreter);
            model.addAttribute("beneficiaries", beneficiaries);
            model.addAttribute("referer", referer);
            model.addAttribute("isOwnProfile", user.getId() == id);
            model.addAttribute("isInterpreterAManager", interpreter instanceof Manager);
            model.addAttribute("allAcademicSkills", new ArrayList<>(SQLWrap.call(daoAcademicSkill::findAll)));
            model.addAttribute("allJobSkills", new ArrayList<>(SQLWrap.call(daoJobSkill::findAll)));
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
            Interpreter interpreter = SQLWrap.call(
                    (FunctionWithSQLException<Integer, Interpreter>) daoInterpreter::find, id);
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

    @GetMapping("/creation")
    public String showCreateInterpreter(Model model) {
        try {
            model.addAttribute("interpreterForm", new InterpreterCreationForm());
            model.addAttribute("allAcademicSkills", new ArrayList<>(SQLWrap.call(daoAcademicSkill::findAll)));
            model.addAttribute("allJobSkills", new ArrayList<>(SQLWrap.call(daoJobSkill::findAll)));
            return "interpreters/creation";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/interpretes";
        }
    }

    @PostMapping("/creation")
    public String createInterpreter(@ModelAttribute("interpreterForm") InterpreterCreationForm form,
                                    @RequestParam(required = false) String returnUrl) {
        try {
            interpreterService.createInterpreter(form);
        } catch (Exception e) {
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
}