-- MySQL dump 10.13  Distrib 5.7.24, for osx11.1 (x86_64)
--
-- Host: localhost    Database: stridewallet_db
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `Contacts`
--

DROP TABLE IF EXISTS `Contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Contacts` (
  `user_id` int NOT NULL,
  `contact_cashtag` varchar(50) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`contact_cashtag`),
  CONSTRAINT `contacts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Contacts`
--

LOCK TABLES `Contacts` WRITE;
/*!40000 ALTER TABLE `Contacts` DISABLE KEYS */;
INSERT INTO `Contacts` VALUES (1,'janesmith','2025-05-20 06:21:14'),(1,'sarahw','2025-05-20 06:21:14'),(2,'johndoe','2025-05-20 06:21:14'),(2,'mikebrown','2025-05-20 06:21:14'),(2,'robjohnson','2025-05-20 06:21:14'),(3,'johndoe','2025-05-20 06:21:14'),(3,'sarahw','2025-05-20 06:21:14'),(4,'janesmith','2025-05-20 06:21:14'),(4,'mikebrown','2025-05-20 06:21:14'),(5,'johndoe','2025-05-20 06:21:14'),(5,'robjohnson','2025-05-20 06:21:14');
/*!40000 ALTER TABLE `Contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `FitnessActivities`
--

DROP TABLE IF EXISTS `FitnessActivities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FitnessActivities` (
  `activity_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `steps_count` int NOT NULL,
  `activity_date` date NOT NULL,
  `reward_transaction_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`activity_id`),
  UNIQUE KEY `user_id` (`user_id`,`activity_date`),
  KEY `reward_transaction_id` (`reward_transaction_id`),
  KEY `idx_fitness_activities_user_date` (`user_id`,`activity_date`),
  CONSTRAINT `fitnessactivities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fitnessactivities_ibfk_2` FOREIGN KEY (`reward_transaction_id`) REFERENCES `Transactions` (`transaction_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FitnessActivities`
--

LOCK TABLES `FitnessActivities` WRITE;
/*!40000 ALTER TABLE `FitnessActivities` DISABLE KEYS */;
/*!40000 ALTER TABLE `FitnessActivities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Transactions`
--

DROP TABLE IF EXISTS `Transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `sender_wallet_id` int DEFAULT NULL,
  `receiver_wallet_id` int DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `note` text,
  `transaction_type` enum('send','request','reward','deposit','withdrawal') NOT NULL,
  `status` enum('pending','completed','failed','cancelled','requested','declined') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`transaction_id`),
  KEY `idx_transactions_sender_wallet` (`sender_wallet_id`),
  KEY `idx_transactions_receiver_wallet` (`receiver_wallet_id`),
  KEY `idx_transactions_type` (`transaction_type`),
  KEY `idx_transactions_status` (`status`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`sender_wallet_id`) REFERENCES `Wallets` (`wallet_id`) ON DELETE RESTRICT,
  CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`receiver_wallet_id`) REFERENCES `Wallets` (`wallet_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Transactions`
--

LOCK TABLES `Transactions` WRITE;
/*!40000 ALTER TABLE `Transactions` DISABLE KEYS */;
INSERT INTO `Transactions` VALUES (37,1,12,50.00,'Dinner last night','send','completed','2025-05-01 19:30:00','2025-05-20 06:34:45'),(38,13,14,25.50,'Movie tickets','send','completed','2025-05-02 15:15:00','2025-05-20 06:34:45'),(39,15,1,100.00,'Rent contribution','send','completed','2025-05-03 14:45:00','2025-05-20 06:34:45'),(40,12,14,35.75,'Lunch at cafe','request','requested','2025-05-04 17:30:00','2025-05-20 06:34:45'),(41,14,12,35.75,'Lunch at cafe','send','completed','2025-05-04 19:20:00','2025-05-20 06:34:45'),(42,13,15,75.00,'Car repair','send','completed','2025-05-05 21:10:00','2025-05-20 06:34:45'),(43,12,1,200.00,'Trip expenses','send','cancelled','2025-05-06 16:05:00','2025-05-20 06:34:45'),(44,NULL,1,500.00,'Bank transfer','deposit','completed','2025-05-07 14:30:00','2025-05-20 06:34:45'),(45,14,NULL,50.00,'ATM withdrawal','withdrawal','completed','2025-05-08 20:45:00','2025-05-20 06:34:45'),(46,NULL,1,50.00,'Deposit from bank','deposit','completed','2025-05-20 06:35:50','2025-05-20 06:35:50'),(47,1,12,20.00,'Delivery Pizza Tip','send','completed','2025-05-20 06:36:10','2025-05-20 06:36:10'),(48,NULL,1,10.00,'Deposit from bank','deposit','completed','2025-05-20 06:47:35','2025-05-20 06:47:35'),(49,12,1,10.00,'','request','completed','2025-05-20 06:48:24','2025-05-20 06:49:29'),(50,NULL,12,10.00,'Deposit from bank','deposit','completed','2025-05-20 06:54:22','2025-05-20 06:54:22'),(51,1,12,10.00,'pizza2','send','completed','2025-05-20 06:57:32','2025-05-20 06:57:32'),(52,NULL,1,2.00,'Deposit from bank','deposit','completed','2025-05-20 06:57:57','2025-05-20 06:57:57'),(53,1,12,10.00,'','send','completed','2025-05-20 07:00:26','2025-05-20 07:00:26'),(54,12,1,10.00,'Tip','send','completed','2025-05-20 11:54:11','2025-05-20 11:54:11'),(55,NULL,12,5.00,'Deposit from bank','deposit','completed','2025-05-20 11:54:38','2025-05-20 11:54:38'),(56,12,1,5.00,'Request for money','request','completed','2025-05-20 11:56:08','2025-05-20 12:11:50'),(57,12,1,10.00,'request for money2','request','completed','2025-05-20 11:56:27','2025-05-20 11:57:43'),(58,NULL,12,10.00,'Deposit from bank','deposit','completed','2025-05-20 12:04:46','2025-05-20 12:04:46'),(59,NULL,12,10.00,'Deposit from bank','deposit','completed','2025-05-20 12:07:54','2025-05-20 12:07:54'),(60,NULL,12,5.00,'Deposit from bank','deposit','completed','2025-05-20 12:08:05','2025-05-20 12:08:05'),(61,NULL,12,10.00,'Deposit from bank','deposit','completed','2025-05-20 12:10:42','2025-05-20 12:10:42'),(62,NULL,12,5.00,'Deposit from bank','deposit','completed','2025-05-20 12:11:11','2025-05-20 12:11:11'),(63,12,1,5.00,'money tip','send','completed','2025-05-20 12:12:29','2025-05-20 12:12:29'),(64,1,12,5.00,'','request','completed','2025-05-20 12:12:41','2025-05-20 12:13:00'),(65,NULL,1,5.00,'Deposit from bank','deposit','completed','2025-05-20 12:22:09','2025-05-20 12:22:09'),(66,NULL,12,10.00,'Deposit from bank','deposit','completed','2025-05-20 12:50:06','2025-05-20 12:50:06'),(67,1,12,5.00,'','request','completed','2025-05-20 12:50:21','2025-05-20 12:50:58'),(68,12,1,10.00,'','send','completed','2025-05-20 12:50:29','2025-05-20 12:50:29'),(69,NULL,1,5.00,'Deposit from bank','deposit','completed','2025-05-20 13:07:51','2025-05-20 13:07:51');
/*!40000 ALTER TABLE `Transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `cashtag` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `cashtag` (`cashtag`),
  UNIQUE KEY `phone_number` (`phone_number`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_phone_number` (`phone_number`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES (1,'5551234567','john.doe@example.com','John','Doe','johndoe','008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601','2025-05-19 04:01:06','2025-05-20 06:06:10'),(2,'5559876543','jane.smith@example.com','Jane','Smith','janesmith','008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601','2025-05-19 04:01:06','2025-05-20 06:48:51'),(3,'5557890123','robert.johnson@example.com','Robert','Johnson','robjohnson','008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601','2025-05-19 04:01:06','2025-05-20 13:42:29'),(4,'5552345678','sarah.williams@example.com','Sarah','Williams','sarahw','008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601','2025-05-19 04:01:06','2025-05-20 13:42:29'),(5,'5558901234','michael.brown@example.com','Michael','Brown','mikebrown','008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601','2025-05-19 04:01:06','2025-05-20 13:42:29');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Wallets`
--

DROP TABLE IF EXISTS `Wallets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Wallets` (
  `wallet_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(3) NOT NULL DEFAULT 'USD',
  `wallet_type` varchar(50) NOT NULL DEFAULT 'primary',
  `is_default` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`wallet_id`),
  UNIQUE KEY `user_id` (`user_id`,`wallet_type`),
  KEY `idx_wallets_user_id` (`user_id`),
  KEY `idx_wallets_currency` (`currency`),
  KEY `idx_wallets_type` (`wallet_type`),
  CONSTRAINT `wallets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Wallets`
--

LOCK TABLES `Wallets` WRITE;
/*!40000 ALTER TABLE `Wallets` DISABLE KEYS */;
INSERT INTO `Wallets` VALUES (1,1,87.00,'USD','primary',1,'2025-05-20 06:06:26','2025-05-20 13:07:51'),(12,2,955.25,'USD','primary',1,'2025-05-20 06:32:10','2025-05-20 12:50:58'),(13,3,3400.75,'USD','primary',1,'2025-05-20 06:32:10','2025-05-20 06:32:10'),(14,4,125.00,'USD','primary',1,'2025-05-20 06:32:10','2025-05-20 06:32:10'),(15,5,7520.30,'USD','primary',1,'2025-05-20 06:32:10','2025-05-20 06:32:10');
/*!40000 ALTER TABLE `Wallets` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-20  8:43:14
