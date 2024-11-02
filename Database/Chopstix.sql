CREATE DATABASE  IF NOT EXISTS `chopstix` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `chopstix`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: chopstix
-- ------------------------------------------------------
-- Server version	8.4.0

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
-- Table structure for table `bill`
--

DROP TABLE IF EXISTS `bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill` (
  `bill_id` int NOT NULL AUTO_INCREMENT,
  `invoice` varchar(45) NOT NULL,
  `order_id` int NOT NULL,
  `payment_date` datetime NOT NULL,
  PRIMARY KEY (`bill_id`),
  KEY `oder_id_idx` (`order_id`),
  CONSTRAINT `oder_id` FOREIGN KEY (`order_id`) REFERENCES `order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill`
--

LOCK TABLES `bill` WRITE;
/*!40000 ALTER TABLE `bill` DISABLE KEYS */;
INSERT INTO `bill` VALUES (21,'INV-1725457603950',85,'2024-09-04 20:46:43'),(22,'INV-1725460967141',92,'2024-09-04 21:42:47'),(23,'INV-1725490209571',133,'2024-09-05 05:50:09'),(24,'INV-1725490465348',135,'2024-09-05 05:54:25'),(25,'INV-1725500439816',149,'2024-09-05 08:40:39'),(26,'INV-1725500481488',150,'2024-09-05 08:41:21'),(27,'INV-1725504088334',163,'2024-09-05 09:41:28'),(28,'INV-1726742087126',165,'2024-09-19 17:34:47');
/*!40000 ALTER TABLE `bill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking` (
  `booking_id` int NOT NULL AUTO_INCREMENT,
  `costumer_name` varchar(45) NOT NULL,
  `contact_numberr` varchar(45) NOT NULL,
  `table_id` int NOT NULL,
  `booking_date` datetime NOT NULL,
  `Booking_statue` enum('pending','done') NOT NULL,
  PRIMARY KEY (`booking_id`),
  KEY `table_idx` (`table_id`),
  CONSTRAINT `table` FOREIGN KEY (`table_id`) REFERENCES `room` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
INSERT INTO `booking` VALUES (13,'Vatanak','0541105451415',1,'2024-09-05 00:00:00','pending'),(14,'Vatanak','0541105451415',1,'2024-09-05 00:00:00','pending'),(15,'hdsbhs','2552',2,'2024-09-05 00:00:00','done');
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(45) NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Drink'),(2,'Khmer'),(3,'Chinese'),(4,'Fastfood'),(5,'Seafood');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_menu`
--

DROP TABLE IF EXISTS `food_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_menu` (
  `food_id` int NOT NULL AUTO_INCREMENT,
  `food_code` varchar(45) NOT NULL,
  `food_name` varchar(45) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`food_id`),
  KEY `category_idx` (`category_id`),
  CONSTRAINT `category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_menu`
--

LOCK TABLES `food_menu` WRITE;
/*!40000 ALTER TABLE `food_menu` DISABLE KEYS */;
INSERT INTO `food_menu` VALUES (1,'F0001','baychean',2.50,2),(3,'F0002','Crab',22.00,5),(4,'F0003','sdkjfhs',1.00,1);
/*!40000 ALTER TABLE `food_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `total_amount` decimal(10,2) NOT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=167 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (82,0.00),(83,0.00),(84,0.00),(85,2.68),(86,0.00),(87,0.00),(88,0.00),(89,0.00),(90,0.00),(91,0.00),(92,112.89),(93,0.00),(94,0.00),(95,0.00),(96,0.00),(97,0.00),(98,0.00),(99,0.00),(100,0.00),(101,0.00),(102,0.00),(103,0.00),(104,0.00),(105,0.00),(106,0.00),(107,0.00),(108,0.00),(109,0.00),(110,0.00),(111,0.00),(112,0.00),(113,0.00),(114,0.00),(115,0.00),(116,0.00),(117,0.00),(118,0.00),(119,0.00),(120,0.00),(121,0.00),(122,0.00),(123,0.00),(124,0.00),(125,0.00),(126,0.00),(127,0.00),(128,0.00),(129,0.00),(130,0.00),(131,0.00),(132,0.00),(133,107.54),(134,0.00),(135,107.54),(136,0.00),(137,0.00),(138,0.00),(139,0.00),(140,0.00),(141,0.00),(142,0.00),(143,0.00),(144,0.00),(145,0.00),(146,0.00),(147,0.00),(148,0.00),(149,23.54),(150,13.38),(151,0.00),(152,0.00),(153,0.00),(154,0.00),(155,0.00),(156,0.00),(157,0.00),(158,0.00),(159,0.00),(160,0.00),(161,0.00),(162,0.00),(163,5.35),(164,0.00),(165,2.68),(166,0.00);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `food_id` int NOT NULL,
  `order_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `food_idx` (`food_id`),
  KEY `order_id_idx` (`order_id`),
  CONSTRAINT `food` FOREIGN KEY (`food_id`) REFERENCES `food_menu` (`food_id`),
  CONSTRAINT `order` FOREIGN KEY (`order_id`) REFERENCES `order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (127,1,85,1),(128,1,92,2),(129,3,92,4),(130,1,92,5),(131,1,129,3),(132,3,133,4),(133,1,133,5),(135,3,135,4),(136,1,135,5),(138,3,149,1),(139,1,150,2),(140,1,150,3),(141,1,163,2),(142,1,165,1);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `room_id` int NOT NULL AUTO_INCREMENT,
  `room_number` int NOT NULL,
  `capacity` int NOT NULL,
  `statue` enum('available','occupied') NOT NULL,
  PRIMARY KEY (`room_id`),
  UNIQUE KEY `table_number_UNIQUE` (`room_number`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,101,15,'available'),(2,102,15,'available'),(3,103,15,'available'),(4,104,15,'available'),(5,105,15,'available'),(6,106,15,'available');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `password_UNIQUE` (`password`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'sovatanak','vatanak123'),(2,'nara','nara123');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'chopstix'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-31 19:58:32
