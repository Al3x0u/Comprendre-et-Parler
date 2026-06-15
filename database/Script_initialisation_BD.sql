INSERT INTO City VALUES (NULL, 'Anderlecht', 1070);
INSERT INTO City VALUES (NULL, 'Auderghem', 1160);
INSERT INTO City VALUES (NULL, 'Berchem-Sainte-Agathe', 1082);
INSERT INTO City VALUES (NULL, 'Bruxelles', 1000);
INSERT INTO City VALUES (NULL, 'Bruxelles', 1040);
INSERT INTO City VALUES (NULL, 'Bruxelles', 1050);
INSERT INTO City VALUES (NULL, 'Laeken', 1020);
INSERT INTO City VALUES (NULL, 'Neder-Over-Heembeek', 1120);
INSERT INTO City VALUES (NULL, 'Haren', 1130);
INSERT INTO City VALUES (NULL, 'Etterbeek', 1040);
INSERT INTO City VALUES (NULL, 'Evere', 1140);
INSERT INTO City VALUES (NULL, 'Forest', 1190);
INSERT INTO City VALUES (NULL, 'Ganshoren', 1083);
INSERT INTO City VALUES (NULL, 'Ixelles', 1050);
INSERT INTO City VALUES (NULL, 'Jette', 1090);
INSERT INTO City VALUES (NULL, 'Koekelberg', 1081);
INSERT INTO City VALUES (NULL, 'Molenbeek-Saint-Jean', 1080);
INSERT INTO City VALUES (NULL, 'Saint-Gilles', 1060);
INSERT INTO City VALUES (NULL, 'Saint-Josse-ten-Noode', 1210);
INSERT INTO City VALUES (NULL, 'Schaerbeek', 1030);
INSERT INTO City VALUES (NULL, 'Uccle', 1180);
INSERT INTO City VALUES (NULL, 'Watermael-Boitsfort', 1170);
INSERT INTO City VALUES (NULL, 'Woluwe-Saint-Lambert', 1200);
INSERT INTO City VALUES (NULL, 'Woluwe-Saint-Pierre', 1150);

INSERT INTO Location VALUES (NULL, NULL, 14, 'Boulevard de la Cambre', '17b', 5);
INSERT INTO Location VALUES (NULL, NULL, 6, 'Place du Petit Sablon', '7', NULL);
INSERT INTO Location VALUES (NULL, NULL, 5, 'Rue de l Amigo', '73a', 8);
INSERT INTO Location VALUES (NULL, NULL, 5, 'Impasse aux Huîtres', '15', NULL);
INSERT INTO Location VALUES (NULL, 'Campus du Ceria', 1, 'Avenue Emile Gryson', '1', NULL);
INSERT INTO Location VALUES (NULL, 'Institut Redouté-Peiffer', 1, 'Avenue Marius Renard', '1', NULL);
INSERT INTO Location VALUES (NULL, 'Athénée Royal Andrée Thomas', 12, 'Avenue Reine Marie-Henriette', '47', NULL);
INSERT INTO Location VALUES (NULL, 'Athénée Royal de Koekelberg', 16, 'Rue Omer Lepreux', '15', NULL);
INSERT INTO Location VALUES (NULL, 'Institut Alexandre Herlin', 3, 'Rue de Dilbeek', '1', NULL);
INSERT INTO Location VALUES (NULL, 'Institut Charles Gheude', 4, 'Rue des Tanneurs', '41', NULL);
INSERT INTO Location VALUES (NULL, 'Institut Lallemand', 4, 'Rue du Meiboom', '16', NULL);
INSERT INTO Location VALUES (NULL, 'Athénée Royal de Ganshoren', 13, 'Rue Auguste De Cock', '1', NULL);
INSERT INTO Location VALUES (NULL, 'Institut Roger Lambion', 1, 'Avenue Emile Gryson', '1', NULL);
INSERT INTO Location VALUES (NULL, 'Ecole Supérieure des Arts du Cirque', 1, 'Avenue Emile Gryson', '1', NULL);
INSERT INTO Location VALUES (NULL, 'Athénée Royal de Jette', 15, 'Avenue de Levis Mirepoix', '100', NULL);
INSERT INTO Location VALUES (NULL, 'Ecole fondamentale libre Magellan', 4, 'Rue De Lenglentier', '6', NULL);
INSERT INTO Location VALUES (NULL, 'Ecole maternelle de la Marolle', 4, 'Rue Sainte-Thérèse', '1', NULL);
INSERT INTO Location VALUES (NULL, 'Ecole fondamentale spécialisé Nos Pilifs', 7, 'Avenue des Pagodes', '212', NULL);
INSERT INTO Location VALUES (NULL, 'Ecole maternelle Autonome 17', 20, 'Rue Désiré Desmet', '16', NULL);
INSERT INTO Location VALUES (NULL, 'Athénée communal Charles Janssens', 14, 'Place de Londres', '5', NULL);
INSERT INTO Location VALUES (NULL, 'Athénée Royal Crommelynck', 24, 'Avenue Orban', '73', NULL);
INSERT INTO Location VALUES (NULL, 'Athénée Royal du Sippelberg', 17, 'Avenue du Sippelberg', '2', NULL);

INSERT INTO AcademicSkill VALUES (NULL, 'Anglais');
INSERT INTO AcademicSkill VALUES (NULL, 'Français');
INSERT INTO AcademicSkill VALUES (NULL, 'Néerlandais');
INSERT INTO AcademicSkill VALUES (NULL, 'Mathématiques');
INSERT INTO AcademicSkill VALUES (NULL, 'Biologie');
INSERT INTO AcademicSkill VALUES (NULL, 'Chimie');
INSERT INTO AcademicSkill VALUES (NULL, 'Physique');
INSERT INTO AcademicSkill VALUES (NULL, 'Géographie');
INSERT INTO AcademicSkill VALUES (NULL, 'Sociologie');

INSERT INTO JobSkill VALUES (NULL, 'Interprétation');
INSERT INTO JobSkill VALUES (NULL, 'Translittération');
INSERT INTO JobSkill VALUES (NULL, 'Transcription');

INSERT INTO Status VALUES (NULL, 'D1-a', 30);
INSERT INTO Status VALUES (NULL, 'D1-b1', 25);
INSERT INTO Status VALUES (NULL, 'D1-b2', 27);
INSERT INTO Status VALUES (NULL, 'D1-b3', 20);
INSERT INTO Status VALUES (NULL, 'D1-c', 22);
INSERT INTO Status VALUES (NULL, 'D2', 19);
INSERT INTO Status VALUES (NULL, 'D4', 26);
INSERT INTO Status VALUES (NULL, 'D5-e', 18);
INSERT INTO Status VALUES (NULL, 'D5-a', 10);

INSERT INTO Interpreter VALUES (NULL, NULL, 'Isabelle', 'Hulin', to_date('10/05/1975', 'dd/mm/yyyy'), '$2a$10$lTLeymVyNqFvWjFgtVVaUeCWAOgvLjifH9CReFXAvyA9J5Lvobgz6', 'isabelle.hulin@gmail.com', '0756/98.14.75', NULL, 25, 1201, 'Auto', 1);
INSERT INTO ManagerT VALUES (1);
INSERT INTO Interpreter VALUES (NULL, NULL, 'Benoit', 'Brisefer', to_date('01/01/1960', 'dd/mm/yyyy'), '$2a$10$lTLeymVyNqFvWjFgtVVaUeCWAOgvLjifH9CReFXAvyA9J5Lvobgz6', 'benoit.brisefer@gmail.com', NULL, NULL, 12, 74, NULL, 2);
INSERT INTO ManagerT VALUES (2);
INSERT INTO Interpreter VALUES (NULL, NULL, 'Benjamin', 'Brisefer', to_date('05/10/1987', 'dd/mm/yyyy'), '$2a$10$lTLeymVyNqFvWjFgtVVaUeCWAOgvLjifH9CReFXAvyA9J5Lvobgz6', 'benjamin.brisefer@gmail.com', NULL, NULL, 34, 1507, 'Vélo', 3);
INSERT INTO Interpreter VALUES (NULL, NULL, 'Alice', 'Charpentier', to_date('24/02/1972', 'dd/mm/yyyy'), '$2a$10$lTLeymVyNqFvWjFgtVVaUeCWAOgvLjifH9CReFXAvyA9J5Lvobgz6', 'alice.charpentier@gmail.com', '967/14.75.13', NULL, 29, 1342, NULL, 3);
INSERT INTO Beneficiary VALUES (NULL, NULL, 'Jessica', 'DuBuisson', to_date('29/12/2012', 'dd/mm/yyyy'), '$2a$10$lTLeymVyNqFvWjFgtVVaUeCWAOgvLjifH9CReFXAvyA9J5Lvobgz6', 'jessica.dubuisson@gmail.com', '7544/35.98.74',NULL, 4, 2);
INSERT INTO Beneficiary VALUES (NULL, NULL, 'Roberto', 'Roberto', to_date('29/12/2020', 'dd/mm/yyyy'), '$2a$10$lTLeymVyNqFvWjFgtVVaUeCWAOgvLjifH9CReFXAvyA9J5Lvobgz6', 'roberto.roberto@gmail.com', NULL, NULL, 1, 3);
UPDATE AppliUserT SET passwordUpdated = 1;

INSERT INTO academicskillinterpreter VALUES(1, 1);
INSERT INTO academicskillinterpreter VALUES(1, 3);
INSERT INTO academicskillinterpreter VALUES(1, 2);
INSERT INTO academicskillinterpreter VALUES(1, 9);
INSERT INTO academicskillinterpreter VALUES(2, 5);
INSERT INTO academicskillinterpreter VALUES(2, 4);
INSERT INTO academicskillinterpreter VALUES(3, 4);
INSERT INTO academicskillinterpreter VALUES(3, 3);

INSERT INTO JobSkillInterpreter VALUES(1, 1);
INSERT INTO JobSkillInterpreter VALUES(1, 2);
INSERT INTO JobSkillInterpreter VALUES(1, 3);
INSERT INTO JobSkillInterpreter VALUES(2, 1);
INSERT INTO JobSkillInterpreter VALUES(2, 3);
INSERT INTO JobSkillInterpreter VALUES(4, 2);

INSERT INTO TimeSlot VALUES(NULL, to_date('15/06/2026 10:30', 'dd/mm/yyyy hh24:mi'), to_date('15/06/2026 12:30', 'dd/mm/yyyy hh24:mi'), NULL);
INSERT INTO TimeSlot VALUES(NULL, to_date('17/06/2026 16:00', 'dd/mm/yyyy hh24:mi'), to_date('17/06/2026 17:30', 'dd/mm/yyyy hh24:mi'), NULL);
INSERT INTO TimeSlot VALUES(NULL, to_date('19/06/2026 08:00', 'dd/mm/yyyy hh24:mi'), to_date('19/06/2026 10:00', 'dd/mm/yyyy hh24:mi'), NULL);
INSERT INTO TimeSlot VALUES(NULL, to_date('19/06/2026 13:30', 'dd/mm/yyyy hh24:mi'), to_date('19/06/2026 16:30', 'dd/mm/yyyy hh24:mi'), NULL);
INSERT INTO TimeSlot VALUES(NULL, to_date('20/06/2026 10:00', 'dd/mm/yyyy hh24:mi'), to_date('20/06/2026 15:00', 'dd/mm/yyyy hh24:mi'), NULL);

INSERT INTO Mission VALUES(NULL, 5, 0, 'Cours de chimie', 1, NULL, 1, 1, 6, 9, 'B9');
INSERT INTO Mission VALUES(NULL, 5, 1, 'Cours de biologie', 1, NULL, 2, 3, 5, 9, 'B9');
INSERT INTO Mission VALUES(NULL, 5, 0, 'examen médical', 1, NULL, 1, 3, 2, 7, NULL);
INSERT INTO Mission VALUES(NULL, 6, 2, 'Cours de néérlandais', 1, NULL, 3, 1, 3, 4, 'A3');
INSERT INTO Mission VALUES(NULL, 5, 0, 'Cours de chimie', 2, NULL, 4, 2, 6, 9, 'B8');
INSERT INTO Mission VALUES(NULL, 5, 3, 'sortie scolaire', 5, 'Visite de Bruxelles', 5, 1, 1, 9, NULL);

INSERT INTO InterpreterMission VALUES(1, 1);
INSERT INTO InterpreterMission VALUES(2, 3);
INSERT INTO InterpreterMission VALUES(5, 2);
INSERT INTO InterpreterMission VALUES(5, 1);


commit;