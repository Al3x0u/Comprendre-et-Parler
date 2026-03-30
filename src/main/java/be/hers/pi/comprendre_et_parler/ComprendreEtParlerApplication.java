package be.hers.pi.comprendre_et_parler;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.DAOs.DatabaseConnector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class ComprendreEtParlerApplication {

    public static void main(String[] args) {
        try {
            DatabaseConnector.initialize();
        }catch (SQLException e){
            e.printStackTrace();
        }
        SpringApplication.run(ComprendreEtParlerApplication.class, args);

        DAOInterpreter DAOInterpreter = new DAOInterpreter();

        Transportation transport = new Transportation(1, "Voiture");

        List<AcademicSkill> academicSkills = new ArrayList<>();
        AcademicSkill academicSkill = new AcademicSkill(1, "Médical");
        academicSkills.add(academicSkill);

        List<JobSkill> jobSkills = new ArrayList<>();
        JobSkill jobSkill = new JobSkill(1, "Traduction");
        jobSkills.add(jobSkill);

        List<Beneficiary> beneficiaries = new ArrayList<>();

        Interpreter interpreter = new Interpreter("b0000000", "Juni", "Samou", LocalDate.now(), "qwertzuiop", "samoujuni@gmail.com", "0489/13.44.40", 5,60, transport, academicSkills, jobSkills, beneficiaries);
        try {
            DAOInterpreter.create(interpreter);
        }catch(AlreadyExistsException e){
            System.out.println("L#interprète " + interpreter.getLogin() + " existe déjà dans la BD");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("<html><body><h1>Hello %s!</h1></body></html>", name);
    }

}
