DROP VIEW AppliUser;
DROP VIEW Beneficiary;
DROP VIEW Interpreter;
DROP VIEW Manager;
DROP VIEW BaseTimeSlot;
DROP VIEW PunctualTimeSlot;
DROP VIEW TransportationView;


CREATE VIEW AppliUser
            (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber)
AS SELECT
       id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber
   FROM
       AppliUserT
   WHERE
       end IS NULL;

CREATE VIEW Beneficiary
            (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, status, referenceInterpreter)
AS SELECT
       a.*, status, referenceInterpreter
   FROM
       AppliUser a, BeneficiaryT b
   WHERE
       a.id = b.id;

CREATE VIEW Interpreter
            (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, weekHourlyQuota, yearHourlyQuota, transportMode, location)
AS SELECT
       a.*, weekHourlyQuota, yearHourlyQuota, designation, location
   FROM
       AppliUser a, InterpreterT i, Transportation t
   WHERE
       a.id = i.id AND i.transportmode = t.id;

CREATE VIEW Manager
            (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, weekHourlyQuota, yearHourlyQuota, transportMode, location)
AS SELECT
       i.*
   FROM
       Interpreter i, ManagerT m
   WHERE
       i.id = m.id;

CREATE VIEW BaseTimeSlot
            (id, startDateTime, endDateTime, day)
AS SELECT
       *
   FROM
       TimeSlot
   WHERE
       day IS NOT NULL;

CREATE VIEW PunctualTimeSlot
            (id, startDateTime, endDateTime)
AS SELECT
       id, startDateTime, endDateTime
   FROM
       TimeSlot
   WHERE
       day IS NULL;

CREATE VIEW TransportationView
            (id, designation)
AS SELECT
       *
   FROM
       Transportation;


commit;