CREATE DATABASE  IF NOT EXISTS `VNScount` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `VNScount`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: VNScount
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `id` binary(16) NOT NULL,
  `created_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `deleted` int NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `modified_date` date DEFAULT NULL,
  `post_id` binary(16) DEFAULT NULL,
  `user_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2jovqhqo324cubdomovkex03b` (`user_id`,`post_id`),
  KEY `FKry8tnr4x2vwemv2bb0h5hyl0x` (`post_id`),
  CONSTRAINT `FKnvx9seeqqyy71bij291pwiwrg` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKry8tnr4x2vwemv2bb0h5hyl0x` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `locations`
--

DROP TABLE IF EXISTS `locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locations` (
  `id` binary(16) NOT NULL,
  `created_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `deleted` int NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `modified_date` date DEFAULT NULL,
  `category` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `check_in_count` bigint DEFAULT NULL,
  `code` varchar(255) COLLATE utf8mb4_0900_as_cs NOT NULL,
  `cover_photo` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `description` text COLLATE utf8mb4_0900_as_cs,
  `golden_hour` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `latitude` decimal(10,7) DEFAULT NULL,
  `level` int NOT NULL,
  `longitude` decimal(10,7) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_0900_as_cs NOT NULL,
  `name_with_type` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `post_count` bigint DEFAULT NULL,
  `slug` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `type` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `parent_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnjcw38t3qcy312pglqpf3pd59` (`code`),
  KEY `FKhjdkpuoptx1cd04r3atchkpi0` (`parent_id`),
  CONSTRAINT `FKhjdkpuoptx1cd04r3atchkpi0` FOREIGN KEY (`parent_id`) REFERENCES `locations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` varchar(50) COLLATE utf8mb4_0900_as_cs NOT NULL,
  `permission_key` enum('APPROVE','CREATE','DELETE','MANAGE','UPDATE','VIEW') COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `permission_type` enum('EXPENSE','INVENTORY','PAYROLL','REPORT','ROLE','SCHEDULE','SCHEDULE_DOCUMENT','TRAVEL','TRUCK','USER') COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `photo_metadata`
--

DROP TABLE IF EXISTS `photo_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photo_metadata` (
  `photo_id` binary(16) NOT NULL,
  `aperture` decimal(4,2) DEFAULT NULL,
  `camera_make` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `camera_model` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `date_taken` datetime(6) DEFAULT NULL,
  `focal_length` decimal(5,2) DEFAULT NULL,
  `gps_latitude` decimal(10,7) DEFAULT NULL,
  `gps_longitude` decimal(10,7) DEFAULT NULL,
  `iso` int DEFAULT NULL,
  `lens_model` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `shutter_speed` text COLLATE utf8mb4_0900_as_cs,
  PRIMARY KEY (`photo_id`),
  CONSTRAINT `FKqtybwctvqpk60lu7eob712twm` FOREIGN KEY (`photo_id`) REFERENCES `photos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `photos`
--

DROP TABLE IF EXISTS `photos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photos` (
  `id` binary(16) NOT NULL,
  `created_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `deleted` int NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `modified_date` date DEFAULT NULL,
  `file_size` bigint DEFAULT NULL,
  `height` int DEFAULT NULL,
  `image_url` text COLLATE utf8mb4_0900_as_cs NOT NULL,
  `location_verified` bit(1) DEFAULT NULL,
  `width` int DEFAULT NULL,
  `post_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6y417rkxpq0v9rurdmrj96034` (`post_id`),
  CONSTRAINT `FK6y417rkxpq0v9rurdmrj96034` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `post_tags`
--

DROP TABLE IF EXISTS `post_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_tags` (
  `post_id` binary(16) NOT NULL,
  `tag_id` binary(16) NOT NULL,
  KEY `FKm6cfovkyqvu5rlm6ahdx3eavj` (`tag_id`),
  KEY `FKkifam22p4s1nm3bkmp1igcn5w` (`post_id`),
  CONSTRAINT `FKkifam22p4s1nm3bkmp1igcn5w` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `FKm6cfovkyqvu5rlm6ahdx3eavj` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id` binary(16) NOT NULL,
  `created_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `deleted` int NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `modified_date` date DEFAULT NULL,
  `caption` text COLLATE utf8mb4_0900_as_cs,
  `like_count` bigint DEFAULT NULL,
  `shooting_tip` text COLLATE utf8mb4_0900_as_cs,
  `location_id` binary(16) DEFAULT NULL,
  `user_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1vpruxtho87bsysr5g2jpntnr` (`location_id`),
  KEY `FK5lidm6cqbc7u4xhqpxm898qme` (`user_id`),
  CONSTRAINT `FK1vpruxtho87bsysr5g2jpntnr` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`),
  CONSTRAINT `FK5lidm6cqbc7u4xhqpxm898qme` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` varchar(50) COLLATE utf8mb4_0900_as_cs NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `role_id` varchar(50) COLLATE utf8mb4_0900_as_cs NOT NULL,
  `permissions_id` varchar(50) COLLATE utf8mb4_0900_as_cs NOT NULL,
  KEY `FKclluu29apreb6osx6ogt4qe16` (`permissions_id`),
  KEY `FKlodb7xh4a2xjv39gc3lsop95n` (`role_id`),
  CONSTRAINT `FKclluu29apreb6osx6ogt4qe16` FOREIGN KEY (`permissions_id`) REFERENCES `permission` (`id`),
  CONSTRAINT `FKlodb7xh4a2xjv39gc3lsop95n` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `id` binary(16) NOT NULL,
  `created_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `deleted` int NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `modified_date` date DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt48xdq560gs3gap9g7jg36kgc` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `user_id` binary(16) NOT NULL,
  `role_id` varchar(50) COLLATE utf8mb4_0900_as_cs NOT NULL,
  KEY `FKa68196081fvovjhkek5m97n3y` (`role_id`),
  KEY `FKj345gk1bovqvfame88rcx7yyx` (`user_id`),
  CONSTRAINT `FKa68196081fvovjhkek5m97n3y` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FKj345gk1bovqvfame88rcx7yyx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` binary(16) NOT NULL,
  `created_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `deleted` int NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `modified_date` date DEFAULT NULL,
  `avatar_url` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `username` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-01 10:39:13
