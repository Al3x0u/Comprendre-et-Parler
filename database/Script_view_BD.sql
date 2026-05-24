DROP VIEW AppliUser;
DROP VIEW Beneficiary;
DROP VIEW Interpreter;
DROP VIEW TransportationView;


CREATE VIEW AppliUser
    (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, passwordUpdated)
AS SELECT
    id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, passwordUpdated
FROM
    AppliUserT
WHERE
    end IS NULL;

CREATE VIEW Beneficiary
    (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, passwordUpdated, status, referenceInterpreter)
AS SELECT
    a.*, status, referenceInterpreter
FROM
    AppliUser a
JOIN
    BeneficiaryT b ON a.id = b.id;

CREATE VIEW Interpreter
            (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber,
                passwordUpdated, weekHourlyQuota, yearHourlyQuota, transportMode, location)
AS SELECT
    a.*, weekHourlyQuota, yearHourlyQuota, designation, location
FROM
    AppliUser a
JOIN
    InterpreterT i ON a.id = i.id
JOIN
    Transportation t ON transportmode = t.id;

CREATE VIEW TransportationView
    (id, designation)
AS SELECT
    *
FROM
    Transportation;


commit;