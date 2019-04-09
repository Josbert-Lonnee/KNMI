-- MySQL dump 10.16  Distrib 10.2.14-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: knmi
-- ------------------------------------------------------
-- Server version       10.2.14-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `day`
--

DROP TABLE IF EXISTS `day`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `day` (
  `DAY_NUMBER` int(16) NOT NULL,
  `YEAR` int(16) NOT NULL,
  `MONTH` int(4) NOT NULL,
  `DAY` int(5) NOT NULL,
  PRIMARY KEY (`DAY_NUMBER`),
  KEY `MONTH` (`MONTH`),
  CONSTRAINT `day_ibfk_1` FOREIGN KEY (`MONTH`) REFERENCES `month` (`MONTHID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `day_parameter`
--

DROP TABLE IF EXISTS `day_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `day_parameter` (
  `DAY_PARAMETERID` int(8) NOT NULL,
  `CODE` varchar(7) NOT NULL,
  `DESCRIPTION` varchar(511) NOT NULL,
  PRIMARY KEY (`DAY_PARAMETERID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `day_value`
--

DROP TABLE IF EXISTS `day_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `day_value` (
  `STATIONID` int(16) NOT NULL,
  `YEAR` int(16) NOT NULL,
  `MONTH` int(4) NOT NULL,
  `DAY` int(5) NOT NULL,
  `DAY_PARAMETERID` int(8) NOT NULL,
  `VALUE` int(24) NOT NULL,
  PRIMARY KEY (`STATIONID`,`YEAR`,`MONTH`,`DAY`,`DAY_PARAMETERID`),
  KEY `MONTH` (`MONTH`),
  KEY `parameter_day_values` (`DAY_PARAMETERID`) USING BTREE,
  CONSTRAINT `day_value_ibfk_1` FOREIGN KEY (`DAY_PARAMETERID`) REFERENCES `day_parameter` (`DAY_PARAMETERID`),
  CONSTRAINT `day_value_ibfk_2` FOREIGN KEY (`MONTH`) REFERENCES `month` (`MONTHID`),
  CONSTRAINT `day_value_ibfk_3` FOREIGN KEY (`STATIONID`) REFERENCES `station` (`STATIONID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hour_parameter`
--

DROP TABLE IF EXISTS `hour_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hour_parameter` (
  `HOUR_PARAMETERID` int(8) NOT NULL,
  `CODE` varchar(7) NOT NULL,
  `DESCRIPTION` varchar(511) NOT NULL,
  PRIMARY KEY (`HOUR_PARAMETERID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hour_value`
--

DROP TABLE IF EXISTS `hour_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hour_value` (
  `STATIONID` int(16) NOT NULL,
  `YEAR` int(16) NOT NULL,
  `MONTH` int(4) NOT NULL,
  `DAY` int(5) NOT NULL,
  `HOUR` int(5) NOT NULL,
  `HOUR_PARAMETERID` int(8) NOT NULL,
  `VALUE` int(24) NOT NULL,
  PRIMARY KEY (`STATIONID`,`YEAR`,`MONTH`,`DAY`,`HOUR`,`HOUR_PARAMETERID`),
  KEY `parameter_hour_values` (`HOUR_PARAMETERID`) USING BTREE,
  KEY `month_hour_values` (`YEAR`,`MONTH`),
  KEY `month_parameter_hour_values` (`YEAR`,`MONTH`,`HOUR_PARAMETERID`),
  CONSTRAINT `hour_value_ibfk_1` FOREIGN KEY (`STATIONID`) REFERENCES `station` (`STATIONID`),
  CONSTRAINT `hour_value_ibfk_2` FOREIGN KEY (`HOUR_PARAMETERID`) REFERENCES `hour_parameter` (`HOUR_PARAMETERID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `month`
--

DROP TABLE IF EXISTS `month`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `month` (
  `MONTHID` int(4) NOT NULL,
  `ABBREVIATION` varchar(7) NOT NULL,
  `NAME` varchar(15) NOT NULL,
  `first_night_hour` int(5) NOT NULL,
  `last_night_hour` int(5) NOT NULL,
  PRIMARY KEY (`MONTHID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `month_average_temp`
--

DROP TABLE IF EXISTS `month_average_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `month_average_temp` (
  `YEAR` int(16) NOT NULL,
  `MONTH` int(4) NOT NULL,
  `DAY_PARAMETERID` int(8) NOT NULL,
  `VALUE` float NOT NULL,
  PRIMARY KEY (`YEAR`,`MONTH`,`DAY_PARAMETERID`),
  KEY `DAY_PARAMETERID` (`DAY_PARAMETERID`),
  CONSTRAINT `month_average_temp_ibfk_1` FOREIGN KEY (`DAY_PARAMETERID`) REFERENCES `day_parameter` (`DAY_PARAMETERID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `month_precipitation`
--

DROP TABLE IF EXISTS `month_precipitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `month_precipitation` (
  `YEAR` int(16) NOT NULL,
  `MONTH` int(4) NOT NULL,
  `TOTAL` int(24) NOT NULL,
  `HOURS_REGISTERED` int(24) NOT NULL,
  `HOURS_OVER_30` int(24) NOT NULL,
  PRIMARY KEY (`YEAR`,`MONTH`),
  KEY `MONTH` (`MONTH`),
  CONSTRAINT `month_precipitation_ibfk_1` FOREIGN KEY (`MONTH`) REFERENCES `month` (`MONTHID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `month_wind_dir_share`
--

DROP TABLE IF EXISTS `month_wind_dir_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `month_wind_dir_share` (
  `YEAR` int(16) NOT NULL,
  `MONTH` int(4) NOT NULL,
  `VAR` float NOT NULL,
  `N` float NOT NULL,
  `NE` float NOT NULL,
  `E` float NOT NULL,
  `SE` float NOT NULL,
  `S` float NOT NULL,
  `SW` float NOT NULL,
  `W` float NOT NULL,
  `NW` float NOT NULL,
  PRIMARY KEY (`YEAR`,`MONTH`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `station`
--

DROP TABLE IF EXISTS `station`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `station` (
  `STATIONID` int(16) NOT NULL AUTO_INCREMENT,
  `LONGITUDE` float NOT NULL,
  `LATITUDE` float NOT NULL,
  `ALTITUDE` float NOT NULL,
  `NAME` varchar(64) NOT NULL,
  PRIMARY KEY (`STATIONID`)
) ENGINE=InnoDB AUTO_INCREMENT=392 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `station_day_temps`
--

DROP TABLE IF EXISTS `station_day_temps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `station_day_temps` (
  `DAY_NUMBER` int(16) NOT NULL,
  `STATIONID` int(16) NOT NULL,
  `T1` int(8) DEFAULT NULL,
  `T2` int(8) DEFAULT NULL,
  `T3` int(8) DEFAULT NULL,
  `T4` int(8) DEFAULT NULL,
  `T5` int(8) DEFAULT NULL,
  `T6` int(8) DEFAULT NULL,
  `T7` int(8) DEFAULT NULL,
  `T8` int(8) DEFAULT NULL,
  `T9` int(8) DEFAULT NULL,
  `T10` int(8) DEFAULT NULL,
  `T11` int(8) DEFAULT NULL,
  `T12` int(8) DEFAULT NULL,
  `T13` int(8) DEFAULT NULL,
  `T14` int(8) DEFAULT NULL,
  `T15` int(8) DEFAULT NULL,
  `T16` int(8) DEFAULT NULL,
  `T17` int(8) DEFAULT NULL,
  `T18` int(8) DEFAULT NULL,
  `T19` int(8) DEFAULT NULL,
  `T20` int(8) DEFAULT NULL,
  `T21` int(8) DEFAULT NULL,
  `T22` int(8) DEFAULT NULL,
  `T23` int(8) DEFAULT NULL,
  `T24` int(8) DEFAULT NULL,
  PRIMARY KEY (`DAY_NUMBER`,`STATIONID`),
  KEY `STATIONID` (`STATIONID`),
  CONSTRAINT `station_day_temps_ibfk_1` FOREIGN KEY (`DAY_NUMBER`) REFERENCES `day` (`DAY_NUMBER`),
  CONSTRAINT `station_day_temps_ibfk_2` FOREIGN KEY (`STATIONID`) REFERENCES `station` (`STATIONID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


INSERT INTO `month` VALUES
(1,'Jan','Januari',18,7),
(2,'Feb','Februari',18,6),
(3,'Mar','Maart',20,5),
(4,'Apr','April',21,5),
(5,'Mei','Mei',22,4),
(6,'Jun','Juni',22,4),
(7,'Jul','Juli',22,4),
(8,'Aug','Augustus',22,5),
(9,'Sep','September',21,6),
(10,'Okt','Oktober',20,6),
(11,'Nov','November',17,6),
(12,'Dec','December',17,7);
