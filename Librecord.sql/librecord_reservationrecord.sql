-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: librecord
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
-- Table structure for table `reservationrecord`
--

DROP TABLE IF EXISTS `reservationrecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservationrecord` (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `id` int DEFAULT NULL,
  `username` varchar(256) COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(256) COLLATE utf8mb4_general_ci NOT NULL,
  `date` varchar(256) COLLATE utf8mb4_general_ci NOT NULL,
  `status` varchar(45) COLLATE utf8mb4_general_ci DEFAULT 'Reserved',
  `date_return` varchar(256) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`record_id`),
  KEY `user_id_idx` (`id`) /*!80000 INVISIBLE */,
  CONSTRAINT `fk_user_id` FOREIGN KEY (`id`) REFERENCES `accountdata` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservationrecord`
--

LOCK TABLES `reservationrecord` WRITE;
/*!40000 ALTER TABLE `reservationrecord` DISABLE KEYS */;
INSERT INTO `reservationrecord` VALUES (37,1,'admin','SamTeach Yourself HTML, CSS, and JavaScript','2024-06-03','Reserved','2024-06-06'),(43,1,'admin','Inside Calculus','2024-06-06','Reserved','2024-06-09'),(44,1,'admin','Python Programming: An Introduction to Computer Science','2024-06-06','Reserved','2024-06-09');
/*!40000 ALTER TABLE `reservationrecord` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-28 21:16:22
