package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.DTO.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class BeneficiaryService {

    private final DAOBeneficiary daoBeneficiary = new DAOBeneficiary();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Create a new Beneficiary in the database with a hashed password.
     * @param form the form containing the beneficiary's information entered by the manager, must not be null.
     *             Its password field must contain the plain text password — it will be hashed before insertion.
     * @return a BeneficiaryCredentials object containing the generated login, the plain text password
     *         and the login page URL to display in the confirmation popup
     * @throws AlreadyExistsException if a Beneficiary with the same attributes already exists in the database
     * @throws ConnectionException if a connection error occurred
     * @throws SQLException if any other database error occurred
     * @post the Beneficiary has been inserted in the database with a hashed password and passwordUpdated = false.
     *       The generated login has been set on the beneficiary object.
     */
    public UserCredentials createBeneficiary(CreateBeneficiaryForm form)
            throws AlreadyExistsException, ConnectionException, SQLException {

        Beneficiary beneficiary = buildBeneficiary(form);

        String plainPassword = form.getPassword();
        beneficiary.setHashedPassword(encoder.encode(plainPassword));

        SQLWrap.callTransaction(daoBeneficiary::create, beneficiary);

        return new UserCredentials(beneficiary.getLogin(), plainPassword, "/login");
    }

    private Beneficiary buildBeneficiary(CreateBeneficiaryForm form) throws SQLException, ConnectionException {
        Status status = SQLWrap.call((FunctionWithSQLException<Integer, Status>) new DAOStatus()::find, form.getStatusId());
        Interpreter interpreter = SQLWrap.call((FunctionWithSQLException<Integer, Interpreter>) new DAOInterpreter()::find, form.getInterpreterRefId());
        return new Beneficiary(
                null,
                form.getFirstName(),
                form.getLastName(),
                form.getBirthDate(),
                form.getPassword(),
                form.getEmail(),
                form.getPhoneNumber(),
                status,
                interpreter
        );
    }

    /**
     * @return all beneficiaries present in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public List<Beneficiary> getAllBeneficiaries()throws ConnectionException, SQLException {
        return new ArrayList<>(SQLWrap.call(new DAOBeneficiary()::findAll));
    }

    public int calculateAge(LocalDate birthdate){
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    /***
     *
     * @param id
     * @throws SQLException if any other database error occurs
     * @return
     */
    public Beneficiary getBeneficiary(int id)throws SQLException{
        return SQLWrap.call(
                (FunctionWithSQLException<Integer, Beneficiary>) daoBeneficiary::find, id);
    }

    /***
     *
     * @param id
     * @throws SQLException
     */
    public void deleteBeneficiary(int id) throws SQLException, IllegalArgumentException{
        if(new DAOMission().hasMissions(id)){
            throw new IllegalArgumentException("Cannot delete beneficiary with existing missions");
        }
        SQLWrap.callTransaction((ConsumerWithSQLException<Integer>) new DAOBeneficiary()::delete, id);
    }

    public void updateBeneficiary(int id, UpdateBeneficiaryForm beneficiaryForm)throws SQLException{
        Beneficiary beneficiary = getBeneficiary(id);
        beneficiary.setFirstName(beneficiaryForm.getFirstName());
        beneficiary.setLastName(beneficiaryForm.getLastName());
        beneficiary.setEmail(beneficiaryForm.getEmail());
        beneficiary.setBirthDate(beneficiaryForm.getBirthDate());
        beneficiary.setPhoneNumber(beneficiaryForm.getPhoneNumber());
        daoBeneficiary.update(beneficiary);
    }
}
