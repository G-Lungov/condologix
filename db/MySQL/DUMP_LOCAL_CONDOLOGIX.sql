-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: condologix
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `concierge`
--

DROP TABLE IF EXISTS `concierge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `concierge` (
  `ID_CONCIERGE` int NOT NULL AUTO_INCREMENT,
  `CONCIERGE_NAME` varchar(50) NOT NULL,
  `CONCIERGE_CONTACT` decimal(16,0) NOT NULL,
  `ID_CONDOMINIUM` int DEFAULT NULL,
  PRIMARY KEY (`ID_CONCIERGE`),
  KEY `FK_ID_CONDOMINIUM_CONCIERGE` (`ID_CONDOMINIUM`),
  CONSTRAINT `FK_ID_CONDOMINIUM_CONCIERGE` FOREIGN KEY (`ID_CONDOMINIUM`) REFERENCES `condominium` (`ID_CONDOMINIUM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concierge`
--

LOCK TABLES `concierge` WRITE;
/*!40000 ALTER TABLE `concierge` DISABLE KEYS */;
/*!40000 ALTER TABLE `concierge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `condominium`
--

DROP TABLE IF EXISTS `condominium`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `condominium` (
  `ID_CONDOMINIUM` int NOT NULL AUTO_INCREMENT,
  `COND_NAME` varchar(50) NOT NULL,
  `COND_CONTACT` decimal(16,0) NOT NULL,
  `AD_AVENUE_STREET_ROAD` varchar(100) NOT NULL,
  `AD_KILOMETER` decimal(5,0) DEFAULT NULL,
  `AD_NUMBER` decimal(5,0) NOT NULL,
  `AD_POSTAL_CODE` decimal(8,0) NOT NULL,
  `AD_NEIGHBORHOOD` varchar(50) NOT NULL,
  `AD_CITY` varchar(50) NOT NULL,
  `AD_STATE` varchar(30) NOT NULL,
  PRIMARY KEY (`ID_CONDOMINIUM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `condominium`
--

LOCK TABLES `condominium` WRITE;
/*!40000 ALTER TABLE `condominium` DISABLE KEYS */;
/*!40000 ALTER TABLE `condominium` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `ID_NOTIFICATION` int NOT NULL AUTO_INCREMENT,
  `NOTIFICATION_DATE` datetime NOT NULL,
  `ID_PACKAGE` int DEFAULT NULL,
  PRIMARY KEY (`ID_NOTIFICATION`),
  KEY `FK_ID_PACKAGE_NOTIFICATION` (`ID_PACKAGE`),
  CONSTRAINT `FK_ID_PACKAGE_NOTIFICATION` FOREIGN KEY (`ID_PACKAGE`) REFERENCES `package` (`ID_PACKAGE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package`
--

DROP TABLE IF EXISTS `package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package` (
  `ID_PACKAGE` int NOT NULL AUTO_INCREMENT,
  `PACKAGE_SENDER_NAME` varchar(100) DEFAULT NULL,
  `PACKAGE_ARRIVAL_DATE` datetime NOT NULL,
  `PACKAGE_PICKUP_DATE` datetime NOT NULL,
  `PACKAGE_SCAN` tinyint(1) NOT NULL DEFAULT '0',
  `ID_TERMINAL` int DEFAULT NULL,
  `ID_RESIDENT` int DEFAULT NULL,
  `ID_CONCIERGE` int DEFAULT NULL,
  PRIMARY KEY (`ID_PACKAGE`),
  KEY `FK_ID_TERMINAL_PACKAGE` (`ID_TERMINAL`),
  KEY `FK_ID_RESIDENT_PACKAGE` (`ID_RESIDENT`),
  KEY `FK_ID_CONCIERGE_PACKAGE` (`ID_CONCIERGE`),
  CONSTRAINT `FK_ID_CONCIERGE_PACKAGE` FOREIGN KEY (`ID_CONCIERGE`) REFERENCES `concierge` (`ID_CONCIERGE`),
  CONSTRAINT `FK_ID_RESIDENT_PACKAGE` FOREIGN KEY (`ID_RESIDENT`) REFERENCES `resident` (`ID_RESIDENT`),
  CONSTRAINT `FK_ID_TERMINAL_PACKAGE` FOREIGN KEY (`ID_TERMINAL`) REFERENCES `terminal` (`ID_TERMINAL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package`
--

LOCK TABLES `package` WRITE;
/*!40000 ALTER TABLE `package` DISABLE KEYS */;
/*!40000 ALTER TABLE `package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resident`
--

DROP TABLE IF EXISTS `resident`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resident` (
  `ID_RESIDENT` int NOT NULL AUTO_INCREMENT,
  `RESIDENT_NAME` varchar(50) NOT NULL,
  `RESIDENT_CONTACT` decimal(16,0) NOT NULL,
  `ID_TERMINAL` int DEFAULT NULL,
  PRIMARY KEY (`ID_RESIDENT`),
  KEY `FK_ID_TERMINAL_RESIDENT` (`ID_TERMINAL`),
  CONSTRAINT `FK_ID_TERMINAL_RESIDENT` FOREIGN KEY (`ID_TERMINAL`) REFERENCES `terminal` (`ID_TERMINAL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resident`
--

LOCK TABLES `resident` WRITE;
/*!40000 ALTER TABLE `resident` DISABLE KEYS */;
/*!40000 ALTER TABLE `resident` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `terminal`
--

DROP TABLE IF EXISTS `terminal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terminal` (
  `ID_TERMINAL` int NOT NULL AUTO_INCREMENT,
  `TEMRINAL_BLOCK` varchar(20) NOT NULL,
  `TERMINAL_NUMBER` decimal(3,0) NOT NULL,
  `ID_CONDOMINIUM` int DEFAULT NULL,
  PRIMARY KEY (`ID_TERMINAL`),
  KEY `FK_ID_CONDOMINIUM_TERMINAL` (`ID_CONDOMINIUM`),
  CONSTRAINT `FK_ID_CONDOMINIUM_TERMINAL` FOREIGN KEY (`ID_CONDOMINIUM`) REFERENCES `condominium` (`ID_CONDOMINIUM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terminal`
--

LOCK TABLES `terminal` WRITE;
/*!40000 ALTER TABLE `terminal` DISABLE KEYS */;
/*!40000 ALTER TABLE `terminal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `ID_USER` int NOT NULL AUTO_INCREMENT,
  `USER_NAME_EMAIL` varchar(100) NOT NULL,
  `USER_PASSWORD` varchar(100) NOT NULL,
  `USER_ROLE` varchar(1) NOT NULL,
  `USER_DB` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`ID_USER`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'gabriel@wedevelops.com','$2a$10$Cb8ux0fJO1E8SOETLvuileFGTbcVjQ6IK.ZVUZPiwVg.AsvDUpUI6','A','03045002VLDR'),(2,'cesare@wedevelops.com','$2a$10$79sPodsnWxER0uTuOkm.zecFgNiJ21b3F.NYFCeeBUjCMp5XCd3W.','A','03045002VLDR'),(3,'luciano@wedevelops.com','$2a$10$K9C6fu3A0Wm2W8.60IeaYOsOfsoK0Zzll.LTnImCSRl82ScbOMhK6','A','03059050LPQC');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'condologix'
--

--
-- Dumping routines for database 'condologix'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-10  0:19:00
