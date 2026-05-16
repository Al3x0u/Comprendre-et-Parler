package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOBeneficiary;
import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;
import be.hers.pi.comprendre_et_parler.DAOs.DAOStatus;
import be.hers.pi.comprendre_et_parler.DTO.BeneficiaryCredentials;
import be.hers.pi.comprendre_et_parler.DTO.CreateBeneficiaryForm;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.FunctionWithSQLException;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

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
    public BeneficiaryCredentials createBeneficiary(CreateBeneficiaryForm form)
            throws AlreadyExistsException, ConnectionException, SQLException {

        Beneficiary beneficiary = buildBeneficiary(form);

        String plainPassword = form.getPassword();
        beneficiary.setHashedPassword(encoder.encode(plainPassword));

        SQLWrap.callTransaction(daoBeneficiary::create, beneficiary);

        return new BeneficiaryCredentials(beneficiary.getLogin(), plainPassword, "/login");
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
}
