/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.11-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: hospital_ayd2
-- ------------------------------------------------------
-- Server version	10.11.11-MariaDB-0+deb12u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `consult`
--

DROP TABLE IF EXISTS `consult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `consult` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `costo_consulta` decimal(38,2) NOT NULL,
  `costo_total` decimal(38,2) NOT NULL,
  `is_internado` bit(1) NOT NULL,
  `is_paid` bit(1) NOT NULL,
  `patient_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfe1hlinso040k867g4ajw0f2d` (`patient_id`),
  CONSTRAINT `FKfe1hlinso040k867g4ajw0f2d` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `cui` varchar(100) NOT NULL,
  `desactivated_at` date DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `igss_percentage` decimal(5,2) DEFAULT NULL,
  `irtra_percentage` decimal(5,2) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `salary` decimal(38,2) DEFAULT NULL,
  `employee_type_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hoj3dwawrtr33oqpkfb2jq6ul` (`cui`),
  KEY `FKks0jnjwhw9tjwa2b1l0klv1fb` (`employee_type_id`),
  CONSTRAINT `FKks0jnjwhw9tjwa2b1l0klv1fb` FOREIGN KEY (`employee_type_id`) REFERENCES `employee_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee_consult`
--

DROP TABLE IF EXISTS `employee_consult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_consult` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `consult_id` varchar(50) NOT NULL,
  `employee_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3wx5gq4ofymwv86774jbh20ev` (`consult_id`),
  KEY `FKrwkh5yof61d5hen2qh17ih043` (`employee_id`),
  CONSTRAINT `FK3wx5gq4ofymwv86774jbh20ev` FOREIGN KEY (`consult_id`) REFERENCES `consult` (`id`),
  CONSTRAINT `FKrwkh5yof61d5hen2qh17ih043` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee_history`
--

DROP TABLE IF EXISTS `employee_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_history` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `commentary` varchar(200) DEFAULT NULL,
  `history_date` date DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `history_type_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8rv9s5a7ts4dwrmv59p8c8xlw` (`employee_id`),
  KEY `FKm40iwpug6bxu3oskwt2np00ow` (`history_type_id`),
  CONSTRAINT `FK8rv9s5a7ts4dwrmv59p8c8xlw` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  CONSTRAINT `FKm40iwpug6bxu3oskwt2np00ow` FOREIGN KEY (`history_type_id`) REFERENCES `history_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee_type`
--

DROP TABLE IF EXISTS `employee_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_type` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_skiuwllqq27o0y3mttp1mhq30` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee_type_permissions`
--

DROP TABLE IF EXISTS `employee_type_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_type_permissions` (
  `employee_type_id` varchar(50) NOT NULL,
  `permissions_id` varchar(50) NOT NULL,
  KEY `FKd8oe75p6cuc6gicsfs0tbcg5r` (`permissions_id`),
  KEY `FKbww5jiqubyu78ajf0790bmka9` (`employee_type_id`),
  CONSTRAINT `FKbww5jiqubyu78ajf0790bmka9` FOREIGN KEY (`employee_type_id`) REFERENCES `employee_type` (`id`),
  CONSTRAINT `FKd8oe75p6cuc6gicsfs0tbcg5r` FOREIGN KEY (`permissions_id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_type`
--

DROP TABLE IF EXISTS `history_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `history_type` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medicine`
--

DROP TABLE IF EXISTS `medicine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicine` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `cost` decimal(38,2) NOT NULL,
  `description` text NOT NULL,
  `min_quantity` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fqqmejinij60115028lriha7w` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `parameter`
--

DROP TABLE IF EXISTS `parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `parameter` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parameter_key` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gfhah8xhnc3uwfruupouwi208` (`parameter_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `dpi` varchar(13) NOT NULL,
  `firstnames` varchar(250) NOT NULL,
  `lastnames` varchar(250) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_seqnpmkchflqj3slhhrhjawqi` (`dpi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `action` varchar(100) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7rjakxcyquo4cobst7qpdxkut` (`action`),
  UNIQUE KEY `UK_2ojme20jpga3r4r79tdso17gi` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `daily_maintenance_cost` decimal(38,2) NOT NULL,
  `daily_price` decimal(38,2) NOT NULL,
  `number` varchar(100) NOT NULL,
  `status` tinyint(4) NOT NULL CHECK (`status` between 0 and 2),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_36mkgyjf7t5hsxx4vtp89i9ey` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_history`
--

DROP TABLE IF EXISTS `room_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_history` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `date` date DEFAULT NULL,
  `patient_id` varchar(50) NOT NULL,
  `room_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe3qb3fdk7ymsf2wxv05nsfifm` (`patient_id`),
  KEY `FK5s7nb81wsyejhfl9pc9k47t1r` (`room_id`),
  CONSTRAINT `FK5s7nb81wsyejhfl9pc9k47t1r` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
  CONSTRAINT `FKe3qb3fdk7ymsf2wxv05nsfifm` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_usage`
--

DROP TABLE IF EXISTS `room_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_usage` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `daily_room_maintenance_cost` decimal(38,2) NOT NULL,
  `daily_room_price` decimal(38,2) NOT NULL,
  `usage_days` int(11) NOT NULL,
  `consult_id` varchar(50) DEFAULT NULL,
  `room_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_c7te0tilchg7ca1exyqk5eb3` (`consult_id`),
  KEY `FKbh0omycn5bnb2kuqwsv8tan0a` (`room_id`),
  CONSTRAINT `FK2x5b0pt0qilgin0yh81v3f3i4` FOREIGN KEY (`consult_id`) REFERENCES `consult` (`id`),
  CONSTRAINT `FKbh0omycn5bnb2kuqwsv8tan0a` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_medicine`
--

DROP TABLE IF EXISTS `sale_medicine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `sale_medicine` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `medicine_cost` decimal(38,2) NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `profit` decimal(38,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  `total` decimal(38,2) NOT NULL,
  `consult_id` varchar(50) DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `medicine_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK63u6w0hat70mcbjqxmta91qqt` (`consult_id`),
  KEY `FKpp74t1wijm4n86u2m5idx4f1l` (`employee_id`),
  KEY `FKmjwi3y28x0eoe03e35vrb4h7r` (`medicine_id`),
  CONSTRAINT `FK63u6w0hat70mcbjqxmta91qqt` FOREIGN KEY (`consult_id`) REFERENCES `consult` (`id`),
  CONSTRAINT `FKmjwi3y28x0eoe03e35vrb4h7r` FOREIGN KEY (`medicine_id`) REFERENCES `medicine` (`id`),
  CONSTRAINT `FKpp74t1wijm4n86u2m5idx4f1l` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `specialist_employee`
--

DROP TABLE IF EXISTS `specialist_employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialist_employee` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `apellidos` varchar(100) NOT NULL,
  `dpi` varchar(13) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jtmuewg3bq3w9bxjlnqmh5xxw` (`dpi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `surgery`
--

DROP TABLE IF EXISTS `surgery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `surgery` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `hospital_cost` decimal(38,2) NOT NULL,
  `performed_date` date DEFAULT NULL,
  `surgery_cost` decimal(38,2) NOT NULL,
  `consult_id` varchar(50) NOT NULL,
  `surgery_type_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp4cj2bgo2fxa2mbr701ub9ylj` (`consult_id`),
  KEY `FK30uxxe4jpkgd2mqal5l9cfruy` (`surgery_type_id`),
  CONSTRAINT `FK30uxxe4jpkgd2mqal5l9cfruy` FOREIGN KEY (`surgery_type_id`) REFERENCES `surgery_type` (`id`),
  CONSTRAINT `FKp4cj2bgo2fxa2mbr701ub9ylj` FOREIGN KEY (`consult_id`) REFERENCES `consult` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `surgery_employee`
--

DROP TABLE IF EXISTS `surgery_employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `surgery_employee` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `specialist_payment` decimal(38,2) NOT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `specialist_employee_id` varchar(50) DEFAULT NULL,
  `surgery_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoy0sd0qe1bhthdfalf2k95uxe` (`employee_id`),
  KEY `FKpb2qbg7a2ni8a0m3tev8kvu9a` (`specialist_employee_id`),
  KEY `FKdam356la2y7fw3nvod8cr91ai` (`surgery_id`),
  CONSTRAINT `FKdam356la2y7fw3nvod8cr91ai` FOREIGN KEY (`surgery_id`) REFERENCES `surgery` (`id`),
  CONSTRAINT `FKoy0sd0qe1bhthdfalf2k95uxe` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  CONSTRAINT `FKpb2qbg7a2ni8a0m3tev8kvu9a` FOREIGN KEY (`specialist_employee_id`) REFERENCES `specialist_employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `surgery_type`
--

DROP TABLE IF EXISTS `surgery_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `surgery_type` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `description` text NOT NULL,
  `hospital_cost` decimal(38,2) NOT NULL,
  `specialist_payment` decimal(38,2) NOT NULL,
  `surgery_cost` decimal(38,2) NOT NULL,
  `type` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g1b5vkgmy6udwp7g7uq9yg0vv` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `desactivated_at` date DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`),
  UNIQUE KEY `UK_r1usl9qoplqsbrhha5e0niqng` (`employee_id`),
  CONSTRAINT `FK211dk0pe7l3aibwce8yy61ota` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vacations`
--

DROP TABLE IF EXISTS `vacations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `vacations` (
  `id` varchar(50) NOT NULL,
  `created_at` date NOT NULL,
  `update_at` date DEFAULT NULL,
  `begin_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `period_year` int(11) DEFAULT NULL,
  `was_used` bit(1) DEFAULT NULL,
  `working_days` int(11) DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7yqjd68v7cacf19gnad48jkeg` (`employee_id`),
  CONSTRAINT `FK7yqjd68v7cacf19gnad48jkeg` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'hospital_ayd2'
--

--
-- Dumping routines for database 'hospital_ayd2'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-04  8:58:41
