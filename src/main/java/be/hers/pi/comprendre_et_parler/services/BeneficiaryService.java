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
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class BeneficiaryService {
    private final static DAOBeneficiary daoBeneficiary = new DAOBeneficiary();
    private final static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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

        return new UserCredentials(beneficiary.getFirstName(), beneficiary.getLogin(), plainPassword, beneficiary.getEmail());
    }

    /**
     * Build a Beneficiary object from a CreateBeneficiaryForm.
     * @param form the form containing the beneficiary's information, must not be null
     * @return a Beneficiary object built from the form data, with status and interpreter loaded from the database
     * @throws SQLException if any database error occurs
     * @throws ConnectionException if the database could not be reached
     */
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
     * Retrieve all Beneficiaries from the database.
     * @return every Beneficiaries present in database, sorted by their compareTo()
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public List<Beneficiary> getAllBeneficiaries()throws ConnectionException, SQLException {
        List<Beneficiary> allBeneficiaries = new ArrayList<>(SQLWrap.call(daoBeneficiary::findAll));
        allBeneficiaries.sort(Beneficiary::compareTo);
        return allBeneficiaries;
    }

    /**
     * @return all beneficiaries referenced by the interpreter with the given id present in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public Set<Beneficiary> getBeneficiariesOf(int idInterpreter)throws ConnectionException, SQLException {
        return SQLWrap.call(daoBeneficiary::findReferencedBeneficiaries, idInterpreter);
    }

    /**
     * Calculate the age of a person based on their birthdate.
     * @param birthdate the birthdate of the person, must not be null
     * @return the age in years
     */
    public int calculateAge(LocalDate birthdate){
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    /**
     * Retrieve a Beneficiary from the database by its id.
     * @param id the id of the Beneficiary to retrieve
     * @return the Beneficiary with the given id, or null if none was found
     * @throws SQLException if any database error occurs
     */
    public Beneficiary getOneBeneficiary(int id)throws SQLException{
        return SQLWrap.call(
                (FunctionWithSQLException<Integer, Beneficiary>) daoBeneficiary::find, id);
    }

    /**
     * Disables a beneficiary account by setting its end date to today.
     * The account remains in the database but the user can no longer log in.
     * @param id the id of the beneficiary to disable
     * @throws NoSuchElementException if the beneficiary does not exist in the database
     * @throws SQLException if the database could not be reached
     */
    public void disableBeneficiary(int id) throws SQLException, NoSuchElementException {
        SQLWrap.callTransaction(new DAOAppliUser()::disableAccount, id);
    }

    /**
     * Delete a Beneficiary from the database by its id.
     * @param id the id of the Beneficiary to delete
     * @throws IllegalArgumentException if the Beneficiary has active missions
     * @throws SQLException if any database error occurs
     * @post the Beneficiary has been soft-deleted from the database
     */
    public void deleteBeneficiary(int id) throws SQLException, IllegalArgumentException{
        SQLWrap.callTransaction( (userId) -> {
            if(new DAOMission().hasActiveMissions(userId)){
                throw new IllegalArgumentException("Cannot delete beneficiary with existing missions");
            }
            daoBeneficiary.delete(userId);
        }, id);
    }

    /**
     * Update the personal information of a Beneficiary in the database.
     * @param id the id of the Beneficiary to update
     * @param beneficiaryForm the form containing the updated information, must not be null
     * @throws SQLException if any database error occurs
     * @post the Beneficiary's personal information has been updated in the database
     */
    public void updateBeneficiary(int id, UpdateBeneficiaryForm beneficiaryForm)throws SQLException{
        Beneficiary beneficiary = getOneBeneficiary(id);
        beneficiary.setFirstName(beneficiaryForm.getFirstName());
        beneficiary.setLastName(beneficiaryForm.getLastName());
        beneficiary.setEmail(beneficiaryForm.getEmail());
        beneficiary.setBirthDate(beneficiaryForm.getBirthDate());
        beneficiary.setPhoneNumber(beneficiaryForm.getPhoneNumber());
        SQLWrap.callTransaction((ConsumerWithSQLException<Beneficiary>) daoBeneficiary::update, beneficiary);
    }

    /**
     * Update the reference interpreter of a Beneficiary in the database.
     * @param beneficiaryId the id of the Beneficiary to update
     * @param interpreterId the id of the new reference interpreter
     * @throws SQLException if any database error occurs
     * @post the referenceInterpreter of the Beneficiary has been updated in the database
     */
    public void updateInterpreterRef(int beneficiaryId, int interpreterId)throws SQLException{
        SQLWrap.callTransaction(
                (BiConsumerWithSQLException<Integer, Integer>) daoBeneficiary::updateInterpreterRef,
                beneficiaryId, interpreterId);
    }

    /**
     * Update the status of a Beneficiary in the database.
     * Does nothing if the new status is the same as the current one.
     * @param beneficiaryId the id of the Beneficiary to update
     * @param statusId the id of the new status
     * @throws SQLException if any database error occurs
     * @post the status of the Beneficiary has been updated in the database, unless the new status was identical to the current one
     */
    public void updateStatus(int beneficiaryId, int statusId)throws SQLException{
        Beneficiary beneficiary = getOneBeneficiary(beneficiaryId);
        if (beneficiary.getStatus().getId() == statusId) {
            return;
        }
        SQLWrap.callTransaction(
                (BiConsumerWithSQLException<Integer, Integer>) daoBeneficiary::updateStatus,
                beneficiaryId, statusId);
    }

    /**
     * Counts beneficiaries
     * @return the number of beneficiaries in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public int countBeneficiaries() throws SQLException, ConnectionException {
        return SQLWrap.call(daoBeneficiary::count);
    }
}