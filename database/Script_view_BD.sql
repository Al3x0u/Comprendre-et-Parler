DROP VIEW BeneficiariesAllInfo;
DROP VIEW InterpretersAllInfo;
DROP VIEW ManagersAllInfo;
DROP VIEW BaseTimeSlot;
DROP VIEW PunctualTimeSlot;


CREATE VIEW BeneficiariesAllInfo
    (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, status, referenceInterpreter)
AS SELECT
    a.*, status, referenceInterpreter
FROM
    AppliUser a, Beneficiary b
WHERE
    a.id = b.id;

CREATE VIEW InterpretersAllInfo
    (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, weekHourlyQuota, yearHourlyQuota, transportMode, location)
AS SELECT
    a.*, weekHourlyQuota, yearHourlyQuota, designation, location
FROM
    AppliUser a, Interpreter i, Transportation t
WHERE
    a.id = i.id AND i.transportmode = t.id;

CREATE VIEW ManagersAllInfo
    (id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, weekHourlyQuota, yearHourlyQuota, transportMode, location)
AS SELECT
    i.*
FROM
   InterpretersAllInfo i, Manager m
WHERE
    i.id = m.id;

CREATE VIEW BaseTimeSlot
    (id, startTime, endTime, day)
AS SELECT
    *
FROM
    TimeSlot
WHERE
    day IS NOT NULL;

CREATE VIEW PunctualTimeSlot
    (id, startTime, endTime)
AS SELECT
    id, startTime, endTime
FROM
    TimeSlot
WHERE
    day IS NULL;


commit;