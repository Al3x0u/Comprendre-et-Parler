package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.BeneficiaryService;
import be.hers.pi.comprendre_et_parler.services.InterpreterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

@Controller
public class DashboardController {

    private final InterpreterService interpreterService;
    private final BeneficiaryService beneficiaryService;

    public DashboardController(InterpreterService interpreterService, BeneficiaryService beneficiaryService){
        this.interpreterService = interpreterService;
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("/hash")
    @ResponseBody
    public String hash(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode("DemoUserPI");
    }

    /**
     * Display the dashboard page (only for managers)
     * @param model the model to pass data to the view
     * @return the dashboard view
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        try {
            model.addAttribute("interpreterCount", interpreterService.countInterpreters());
            model.addAttribute("beneficiaryCount", beneficiaryService.countBeneficiaries());
        } catch (SQLException | ConnectionException e) {
            e.printStackTrace();
            model.addAttribute("interpreterCount", 0);
            model.addAttribute("beneficiaryCount", 0);
        }
        return "dashboard";
    }
}
