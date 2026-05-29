DROP TRIGGER IIR_InsertionAppliUser;
DROP TRIGGER BIR_InsertionLoginAppliUser;
DROP TRIGGER IDR_DeleteAppliUser;
DROP TRIGGER IIR_InsertionInterpreter;
DROP TRIGGER IDR_DeleteInterpreter;
DROP TRIGGER IUR_UpdateInterpreter;
DROP TRIGGER IIR_InsertionBeneficiary;
DROP TRIGGER IDR_DeleteBeneficiary;
DROP TRIGGER IUR_UpdateBeneficiary;
DROP TRIGGER IIR_InsertionTransportationView;


CREATE TRIGGER IIR_InsertionAppliUser
    INSTEAD OF INSERT ON AppliUser
    FOR EACH ROW
BEGIN
    INSERT INTO AppliUserT
    VALUES
        (NULL, SYSDATE, NULL, :NEW.login, :NEW.firstName, :NEW.lastName,
         :NEW.birthDate, :NEW.hashedPassword, :NEW.email, :NEW.phoneNumber, :NEW.passwordUpdated);
END;
/

CREATE TRIGGER BIR_InsertionLoginAppliUser
    BEFORE INSERT ON AppliUserT
    FOR EACH ROW
    WHEN (NEW.login IS NULL)
DECLARE
    numeroMax INTEGER;
    year VARCHAR2(2 CHAR);
    initial VARCHAR2(2 CHAR);
    beginLogin VARCHAR2(4 CHAR);
BEGIN
    initial := CONCAT(SUBSTR(:NEW.firstName, 0, 1), SUBSTR(:NEW.lastName, 0, 1));
    SELECT to_char(SYSDATE, 'YY') INTO year FROM DUAL;
    beginLogin := CONCAT(initial, year);
    SELECT MAX(TO_NUMBER(REGEXP_SUBSTR(login, '\d+'))) INTO numeroMax
    FROM AppliUserT WHERE login LIKE CONCAT(beginLogin, '%');
    
    IF (numeroMax IS NULL) THEN
        :NEW.login := CONCAT(beginLogin, '01');
    ELSE
        :NEW.login := CONCAT(initial, numeroMax + 1);
    END IF;
END;
/

CREATE TRIGGER IDR_DeleteAppliUser
    INSTEAD OF DELETE ON AppliUser
    FOR EACH ROW
DECLARE
    beginDate DATE;
BEGIN
    SELECT begin INTO beginDate FROM AppliUserT
    WHERE end IS NULL AND login = :OLD.login;

    UPDATE AppliUserT SET end = SYSDATE
    WHERE login = :OLD.login AND begin = beginDate;
END;
/

CREATE TRIGGER IIR_InsertionInterpreter
    INSTEAD OF INSERT ON Interpreter
    FOR EACH ROW
DECLARE
    newID INTEGER;
    idTransportation INTEGER;
BEGIN
    INSERT INTO TransportationView
    VALUES (NULL, :NEW.TransportMode);
    IF (:NEW.TransportMode IS NULL) THEN
        SELECT id INTO idTransportation
        FROM TransportationView
        WHERE designation IS NULL;
    ELSE
        SELECT id INTO idTransportation
        FROM TransportationView
        WHERE designation = INITCAP(:NEW.TransportMode);
    END IF;
    INSERT INTO AppliUser
    VALUES
        (NULL, NULL, :NEW.firstName, :NEW.lastName,
         :NEW.birthDate, :NEW.hashedPassword, :NEW.email, :NEW.phoneNumber, 0);
    SELECT id INTO newID
    FROM AppliUser
    WHERE firstName = :NEW.firstName AND lastName = :NEW.lastName AND birthDate = :NEW.birthDate
      AND hashedPassword = :NEW.hashedPassword AND email = :NEW.email AND (phoneNumber = :NEW.phoneNumber OR phoneNumber IS NULL);
    INSERT INTO InterpreterT
    VALUES (newID, :NEW.weekHourlyQuota, :NEW.yearHourlyQuota, idTransportation, :NEW.location);
END;
/

CREATE TRIGGER IDR_DeleteInterpreter
    INSTEAD OF DELETE ON Interpreter
    FOR EACH ROW
BEGIN
    DELETE FROM AppliUser WHERE login = :OLD.login;
END;
/

CREATE TRIGGER IUR_UpdateInterpreter
    INSTEAD OF UPDATE ON Interpreter
    FOR EACH ROW
DECLARE
    idTransportation INTEGER;
    newID INTEGER;
    beginDate DATE;
BEGIN
    INSERT INTO TransportationView
    VALUES (NULL, :NEW.TransportMode);
    IF (:NEW.TransportMode IS NULL) THEN
        SELECT id INTO idTransportation
        FROM TransportationView
        WHERE designation IS NULL;
    ELSE
        SELECT id INTO idTransportation
        FROM TransportationView
        WHERE designation = INITCAP(:NEW.TransportMode);
    END IF;
    IF :NEW.weekHourlyQuota <> :OLD.weekHourlyQuota OR :NEW.yearHourlyQuota <> :OLD.yearHourlyQuota THEN
        SELECT begin INTO beginDate FROM AppliUserT WHERE
            login = :OLD.login AND firstName = :OLD.firstName AND lastName = :OLD.lastName AND
             birthDate = :OLD.birthDate AND email = :OLD.email AND end IS NULL;
        INSERT INTO AppliUserT
        VALUES
            (NULL, beginDate, SYSDATE, :OLD.login, :OLD.firstName, :OLD.lastName,
             :OLD.birthDate, :OLD.hashedPassword, :OLD.email, :OLD.phoneNumber, :OLD.passwordUpdated);
        SELECT id INTO newID
        FROM AppliUserT
        WHERE login = :OLD.login AND end = SYSDATE; 
        INSERT INTO InterpreterT
        VALUES (newID, :OLD.weekHourlyQuota, :OLD.yearHourlyQuota, idTransportation, :OLD.location);
    END IF;

    UPDATE AppliUser SET firstName = :NEW.firstName, lastName = :NEW.lastName,
                    birthDate = :NEW.birthDate, hashedPassword = :NEW.hashedPassword, email = :NEW.email,
                    phoneNumber = :NEW.phoneNumber, passwordUpdated = :NEW.passwordUpdated WHERE id = :OLD.id;
    UPDATE InterpreterT SET weekHourlyQuota = :NEW.weekHourlyQuota, yearHourlyQuota = :NEW.yearHourlyQuota, transportMode = idTransportation, location = :NEW.location WHERE id = :OLD.id;
    
END;
/

CREATE TRIGGER IIR_InsertionBeneficiary
    INSTEAD OF INSERT ON Beneficiary
    FOR EACH ROW
DECLARE
    newID INTEGER;
BEGIN
    INSERT INTO AppliUser
    VALUES
        (NULL, NULL, :NEW.firstName, :NEW.lastName,
         :NEW.birthDate, :NEW.hashedPassword, :NEW.email, :NEW.phoneNumber, 0);
    SELECT id INTO newID
    FROM AppliUser
    WHERE firstName = :NEW.firstName AND lastName = :NEW.lastName AND birthDate = :NEW.birthDate
      AND hashedPassword = :NEW.hashedPassword AND email = :NEW.email AND (phoneNumber = :NEW.phoneNumber OR phoneNumber IS NULL);
    INSERT INTO BeneficiaryT
    VALUES (newID, :NEW.status, :NEW.referenceInterpreter);
END;
/

CREATE TRIGGER IDR_DeleteBeneficiary
    INSTEAD OF DELETE ON Beneficiary
    FOR EACH ROW
BEGIN
    DELETE FROM AppliUser WHERE login = :OLD.login;
END;
/

CREATE TRIGGER IUR_UpdateBeneficiary
    INSTEAD OF UPDATE ON Beneficiary
    FOR EACH ROW
DECLARE
    newId INTEGER;
    beginDate DATE;
BEGIN
    IF :NEW.status <> :OLD.status THEN
        SELECT begin INTO beginDate FROM AppliUserT WHERE
            login = :OLD.login AND firstName = :OLD.firstName AND lastName = :OLD.lastName AND
             birthDate = :OLD.birthDate AND email = :OLD.email AND end IS NULL;
        INSERT INTO AppliUserT
        VALUES
            (NULL, beginDate, SYSDATE, :OLD.login, :OLD.firstName, :OLD.lastName,
             :OLD.birthDate, :OLD.hashedPassword, :OLD.email, :OLD.phoneNumber, :OLD.passwordUpdated);
        SELECT id INTO newID
        FROM AppliUserT
        WHERE login = :OLD.login AND end = SYSDATE;
        INSERT INTO BeneficiaryT
        VALUES (newID, :OLD.status, :OLD.referenceInterpreter);
    END IF;
    UPDATE AppliUser SET login = :NEW.login, firstName = :NEW.firstName, lastName = :NEW.lastName,
                             birthDate = :NEW.birthDate, hashedPassword = :NEW.hashedPassword, email = :NEW.email,
                             phoneNumber = :NEW.phoneNumber, passwordUpdated = :NEW.passwordUpdated WHERE id = :OLD.id;
    UPDATE BeneficiaryT SET status = :NEW.status, referenceInterpreter = :NEW.referenceInterpreter WHERE id = :OLD.id;
END;
/

CREATE TRIGGER IIR_InsertionTransportationView
    INSTEAD OF INSERT ON TransportationView
    FOR EACH ROW
DECLARE
    alreadyExist INTEGER;
BEGIN
    IF :NEW.designation IS NOT NULL THEN
        SELECT count(id) INTO alreadyExist
        FROM TransportationView
        WHERE designation = INITCAP(:NEW.designation);
    ELSE
        SELECT count(id) INTO alreadyExist
        FROM TransportationView
        WHERE designation IS NULL;
    END IF;
    IF (alreadyExist = 0) THEN
        INSERT INTO Transportation
        VALUES (NULL, INITCAP(:NEW.designation));
    END IF;
END;
/


commit;