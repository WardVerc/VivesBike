CREATE TABLE IF NOT EXISTS `Lid` (
  `rijksregisternummer` VARCHAR(11) NOT NULL,
  `voornaam` VARCHAR(255) NOT NULL,
  `naam` VARCHAR(255) NOT NULL,
  `emailadres` VARCHAR(255) NOT NULL,
  `start_lidmaatschap` DATE NOT NULL,
  `einde_lidmaatschap` DATE NULL,
  `opmerking` TEXT NULL,
  PRIMARY KEY (`rijksregisternummer`));


CREATE TABLE IF NOT EXISTS `Fiets` (
  `registratienummer` INT NOT NULL AUTO_INCREMENT,
  `status` ENUM('actief', 'herstel','uit_omloop') NOT NULL,
  `standplaats` ENUM('Oostende', 'Brugge', 'Tielt','Torhout', 'Roeselare','Kortrijk') NOT NULL,
  `opmerkingen` TEXT NULL,
  PRIMARY KEY (`registratienummer`));


CREATE TABLE IF NOT EXISTS `Rit` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `starttijd` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `eindtijd` TIMESTAMP NULL,
  `prijs` DECIMAL(2) NULL,
  `lid_rijksregisternummer` VARCHAR(11) NOT NULL,
  `fiets_registratienummer` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_fiets_registratienummer_idx` (`fiets_registratienummer` ASC),
  INDEX `fk_lid_rijksregisternummer_idx` (`lid_rijksregisternummer` ASC),
  CONSTRAINT `fk_fiets_registratienummer`
    FOREIGN KEY (`fiets_registratienummer`)
    REFERENCES `Fiets` (`registratienummer`)
    ON DELETE RESTRICT
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_lid_rijksregisternummer`
    FOREIGN KEY (`lid_rijksregisternummer`)
    REFERENCES `Lid` (`rijksregisternummer`)
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);

INSERT INTO Lid (rijksregisternummer, voornaam, naam, emailadres, start_lidmaatschap, opmerking)
values ('94031820982', 'Ward', 'Vercuyssen', 'ward@hotmail.be', '2021-01-01', 'Test opmerking');

INSERT INTO Lid (rijksregisternummer, voornaam, naam, emailadres, start_lidmaatschap, opmerking)
values ('94090200136', 'Michiel', 'Demoor', 'michiel@hotmail.be', '2021-01-01', 'Test');

INSERT INTO Lid (rijksregisternummer, voornaam, naam, emailadres, start_lidmaatschap, opmerking)
values ('96030800249', 'Kyra', 'Matton', 'kyra@hotmail.be', '2021-01-01', 'Testing');

INSERT INTO Lid (rijksregisternummer, voornaam, naam, emailadres, start_lidmaatschap, opmerking)
values ('97010100272', 'Ianka', 'Beys', 'ianka@hotmail.be', '2021-01-01', 'Geen opmerking');

INSERT INTO Lid (rijksregisternummer, voornaam, naam, emailadres, start_lidmaatschap, opmerking)
values ('92030700193', 'Filip', 'De Feyter', 'filip@hotmail.be', '2021-01-01', 'Opmerking');

INSERT INTO Fiets (status, standplaats, opmerkingen)
VALUES ('actief', 'Oostende', 'Test opmerking');

INSERT INTO Fiets (status, standplaats, opmerkingen)
VALUES ('actief', 'Kortrijk', 'Opmerking');

INSERT INTO Fiets (status, standplaats, opmerkingen)
VALUES ('actief', 'Brugge', 'Goede fiets');

INSERT INTO Fiets (status, standplaats, opmerkingen)
VALUES ('actief', 'Tielt', 'Dikke banden');

INSERT INTO Fiets (status, standplaats, opmerkingen)
VALUES ('actief', 'Torhout', 'Geen spatbord');