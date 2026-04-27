DROP TRIGGER IIR_InsertionAppliUser;
DROP TRIGGER IDR_DeleteAppliUser;
DROP TRIGGER IIR_InsertionInterpreter;
DROP TRIGGER IDR_DeleteInterpreter;
DROP TRIGGER IUR_UpdateTransportModeInterpreter;
DROP TRIGGER IIR_InsertionManager;
DROP TRIGGER IDR_DeleteManager;
DROP TRIGGER IUR_UpdateTransportModeManager;
DROP TRIGGER IIR_InsertionBeneficiary;
DROP TRIGGER IDR_DeleteBeneficiary;
DROP TRIGGER IIR_InsertionTransportationView;


CREATE TRIGGER IIR_InsertionAppliUser
INSTEAD OF INSERT ON AppliUser
FOR EACH ROW
BEGIN
    INSERT INTO AppliUserT 
    VALUES
        (NULL, SYSDATE, NULL, :NEW.login, :NEW.firstName, :NEW.lastName,
    :NEW.birthDate, :NEW.hashedPassword, :NEW.email, :NEW.phoneNumber);
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
    VALUES (NULL, :NEW.transportMode);
    SELECT id INTO idTransportation
    FROM TransportationView
    WHERE designation = INITCAP(:NEW.transportMode);
    INSERT INTO AppliUser
    VALUES
        (NULL, :NEW.login, :NEW.firstName, :NEW.lastName,
    :NEW.birthDate, :NEW.hashedPassword, :NEW.email, :NEW.phoneNumber);
    SELECT id INTO newID
    FROM AppliUser
    WHERE login = :NEW.login;
    INSERT INTO InterpreterT 
    VALUES
        (newID, :NEW.weekHourlyQuota, :NEW.yearHourlyQuota, idTransportation, :NEW.location);
END;
/

CREATE TRIGGER IDR_DeleteInterpreter
INSTEAD OF DELETE ON Interpreter
FOR EACH ROW
BEGIN
    DELETE FROM AppliUser WHERE login = :OLD.login;
END;
/

CREATE TRIGGER IUR_UpdateTransportModeInterpreter
INSTEAD OF UPDATE ON Interpreter
FOR EACH ROW
DECLARE
    idTransportation INTEGER;
BEGIN
    INSERT INTO TransportationView
    VALUES (NULL, :NEW.transportMode);
    SELECT id INTO idTransportation
    FROM TransportationView
    WHERE designation = INITCAP(:NEW.transportMode);
    UPDATE AppliUser SET login = :NEW.login, firstName = :NEW.firstName, lastName = :NEW.lastName, birthDate = :NEW.birthDate,
    hashedPassword = :NEW.hashedPassword, email = :NEW.email, phoneNumber = :NEW.phoneNumber WHERE id = :OLD.id;
    UPDATE InterpreterT SET weekHourlyQuota = :NEW.weekHourlyQuota, yearHourlyQuota = :NEW.yearHourlyQuota,
    transportMode = idTransportation, location = :NEW.location WHERE id = :OLD.id;
END;
/

CREATE TRIGGER IIR_InsertionManager
INSTEAD OF INSERT ON Manager
FOR EACH ROW
DECLARE
    newID INTEGER;
BEGIN
    INSERT INTO Interpreter 
    VALUES
        (NULL, :NEW.login, :NEW.firstName, :NEW.lastName,
    :NEW.birthDate, :NEW.hashedPassword, :NEW.email, :NEW.phoneNumber,
    :NEW.weekHourlyQuota, :NEW.yearHourlyQuota, :NEW.transportMode, :NEW.location);
    SELECT id INTO newID
    FROM AppliUser
    WHERE login = :NEW.login;
    INSERT INTO ManagerT
    VALUES (newID);
END;
/

CREATE TRIGGER IDR_DeleteManager
INSTEAD OF DELETE ON Manager
FOR EACH ROW
BEGIN
    DELETE FROM AppliUser WHERE login = :OLD.login;
END;
/

CREATE TRIGGER IUR_UpdateTransportModeManager
INSTEAD OF UPDATE ON Manager
FOR EACH ROW
BEGIN
    UPDATE Interpreter SET login = :NEW.login, firstName = :NEW.firstName, lastName = :NEW.lastName, birthDate = :NEW.birthDate,
    hashedPassword = :NEW.hashedPassword, email = :NEW.email, phoneNumber = :NEW.phoneNumber,
    weekHourlyQuota = :NEW.weekHourlyQuota, yearHourlyQuota = :NEW.yearHourlyQuota,
    transportMode = :NEW.transportMode, location = :NEW.location WHERE id = :OLD.id;
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
        (NULL, :NEW.login, :NEW.firstName, :NEW.lastName,
    :NEW.birthDate, :NEW.hashedPassword, :NEW.email, :NEW.phoneNumber);
    SELECT id INTO newID
    FROM AppliUser
    WHERE login = :NEW.login;
    INSERT INTO BeneficiaryT 
    VALUES
        (newID, :NEW.status, :NEW.referenceInterpreter);
END;
/

CREATE TRIGGER IDR_DeleteBeneficiary
INSTEAD OF DELETE ON Beneficiary
FOR EACH ROW
BEGIN
    DELETE FROM AppliUser WHERE login = :OLD.login;
END;
/

CREATE TRIGGER IIR_InsertionTransportationView
INSTEAD OF INSERT ON TransportationView
FOR EACH ROW
DECLARE
    alreadyExist INTEGER;
BEGIN
    SELECT count(id) INTO alreadyExist
    FROM TransportationView
    WHERE designation = INITCAP(:NEW.designation);
    if(alreadyExist = 0) THEN
        INSERT INTO Transportation
        VALUES
            (NULL, INITCAP(:NEW.designation));
    END IF;
END;
/