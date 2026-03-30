package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.List;

public class Manager extends Interpreter{

    public Manager(String id, String lastName, String firstName, LocalDate birthday,
                   String hashPassword, String mail, String phone, int quotaWeek,
                   int quotaYear, Transportation transport, List<AcademicSkill> academicSkill,
                   List<JobSkill> jobList, List<Beneficiary> beneficiaries) {
        super(id, lastName, firstName, birthday, hashPassword, mail, phone, quotaWeek, quotaYear,transport, academicSkill, jobList, beneficiaries);
    }
}
