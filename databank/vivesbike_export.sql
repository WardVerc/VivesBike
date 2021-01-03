-- phpMyAdmin SQL Dump
-- version 4.0.4.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 03, 2021 at 05:07 PM
-- Server version: 5.6.13
-- PHP Version: 5.4.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `vivesbike`
--
CREATE DATABASE IF NOT EXISTS `vivesbike` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `vivesbike`;

-- --------------------------------------------------------

--
-- Table structure for table `fiets`
--

CREATE TABLE IF NOT EXISTS `fiets` (
  `registratienummer` int(11) NOT NULL AUTO_INCREMENT,
  `status` enum('actief','herstel','uit_omloop') NOT NULL,
  `standplaats` enum('Oostende','Brugge','Tielt','Torhout','Roeselare','Kortrijk') NOT NULL,
  `opmerkingen` text,
  PRIMARY KEY (`registratienummer`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=413 ;

-- --------------------------------------------------------

--
-- Table structure for table `lid`
--

CREATE TABLE IF NOT EXISTS `lid` (
  `rijksregisternummer` varchar(11) NOT NULL,
  `voornaam` varchar(255) NOT NULL,
  `naam` varchar(255) NOT NULL,
  `emailadres` varchar(255) NOT NULL,
  `start_lidmaatschap` date NOT NULL,
  `einde_lidmaatschap` date DEFAULT NULL,
  `opmerking` text,
  PRIMARY KEY (`rijksregisternummer`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `lid`
--

INSERT INTO `lid` (`rijksregisternummer`, `voornaam`, `naam`, `emailadres`, `start_lidmaatschap`, `einde_lidmaatschap`, `opmerking`) VALUES
('92010100264', 'Kyra', 'Matton', 'kyra@hotmail.be', '2021-01-01', NULL, 'Testing'),
('94010100110', 'Ward', 'Vercuyssen', 'ward@hotmail.be', '2021-01-01', NULL, 'Test opmerking'),
('94010700223', 'Ianka', 'Beys', 'ianka@hotmail.be', '2021-01-01', NULL, 'Geen opmerking'),
('94011700115', 'Filip', 'De Feyter', 'filip@hotmail.be', '2021-01-01', NULL, 'Opmerking'),
('96031700171', 'Michiel', 'Demoor', 'michiel@hotmail.be', '2021-01-01', NULL, 'Test');

-- --------------------------------------------------------

--
-- Table structure for table `rit`
--

CREATE TABLE IF NOT EXISTS `rit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starttijd` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `eindtijd` timestamp NULL DEFAULT NULL,
  `prijs` decimal(2,0) DEFAULT NULL,
  `lid_rijksregisternummer` varchar(11) NOT NULL,
  `fiets_registratienummer` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_fiets_registratienummer_idx` (`fiets_registratienummer`),
  KEY `fk_lid_rijksregisternummer_idx` (`lid_rijksregisternummer`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=269 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `rit`
--
ALTER TABLE `rit`
  ADD CONSTRAINT `fk_fiets_registratienummer` FOREIGN KEY (`fiets_registratienummer`) REFERENCES `fiets` (`registratienummer`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_lid_rijksregisternummer` FOREIGN KEY (`lid_rijksregisternummer`) REFERENCES `lid` (`rijksregisternummer`) ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
