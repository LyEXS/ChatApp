-- MySQL dump 10.13  Distrib 8.4.6, for Linux (x86_64)
--
-- Host: localhost    Database: chat_app_db
-- ------------------------------------------------------
-- Server version	8.4.6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Conversation`
--

DROP TABLE IF EXISTS `Conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Conversation` (
  `id_conversation` varchar(36) NOT NULL,
  `nom` varchar(100) DEFAULT NULL,
  `est_groupe` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_conversation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Conversation`
--

LOCK TABLES `Conversation` WRITE;
/*!40000 ALTER TABLE `Conversation` DISABLE KEYS */;
INSERT INTO `Conversation` VALUES ('14a41514-5015-4839-9f53-0e407ca4ce94','Lyessss et Lyes',1),('2641000c-759f-43e7-9dda-4f308912677f','Lyessss et Anais',0),('59493a55-fdfb-46c5-b48f-b0918069bdcd','samira et Lyes',0),('630917d7-2031-49a8-944d-e61fb189f006','Lyes et Islem',0),('ff189b36-6e52-4f9c-9515-ce360d13a72d','Khaled et Islem',0);
/*!40000 ALTER TABLE `Conversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Message`
--

DROP TABLE IF EXISTS `Message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Message` (
  `id_message` varchar(36) NOT NULL,
  `id_conversation` varchar(36) NOT NULL,
  `id_utilisateur` varchar(36) NOT NULL,
  `content` text NOT NULL,
  `date_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `read` tinyint DEFAULT '0',
  PRIMARY KEY (`id_message`),
  KEY `id_conversation` (`id_conversation`),
  KEY `id_utilisateur` (`id_utilisateur`),
  CONSTRAINT `Message_ibfk_1` FOREIGN KEY (`id_conversation`) REFERENCES `Conversation` (`id_conversation`) ON DELETE CASCADE,
  CONSTRAINT `Message_ibfk_2` FOREIGN KEY (`id_utilisateur`) REFERENCES `Utilisateur` (`id_utilisateur`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Message`
--

LOCK TABLES `Message` WRITE;
/*!40000 ALTER TABLE `Message` DISABLE KEYS */;
INSERT INTO `Message` VALUES ('0278053e-4536-496b-a17a-5766a68872d9','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:42:15',0),('10695e37-5cd2-4fdb-86f2-9207df7ba619','630917d7-2031-49a8-944d-e61fb189f006','idLyes','test','2026-03-08 14:08:34',0),('150a49d2-0b16-409e-a6ca-ab0012165989','59493a55-fdfb-46c5-b48f-b0918069bdcd','idLyes','test','2026-03-08 14:10:39',0),('15d62e05-ee1d-4979-bd1d-61f78cb86e16','14a41514-5015-4839-9f53-0e407ca4ce94','idLyes','test','2026-03-08 12:14:12',0),('170cf9c2-7f25-478d-9e74-4d7424a33906','630917d7-2031-49a8-944d-e61fb189f006','idIslem','alooo','2026-03-08 13:39:52',0),('19b65d95-c20b-41d4-a542-bb1165c4ba79','59493a55-fdfb-46c5-b48f-b0918069bdcd','idLyes','test','2026-03-08 12:14:07',0),('1dba0cc5-3602-4778-a14b-8b0492ee21b4','14a41514-5015-4839-9f53-0e407ca4ce94','idLyes','oui','2026-03-08 12:21:26',0),('20a4b509-f8ac-4d1c-9bbb-f42f609bbf57','14a41514-5015-4839-9f53-0e407ca4ce94','idLyes','test','2026-03-06 15:26:42',0),('20d7afc7-fdbd-4098-bdee-520582b042ff','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:42:56',0),('265ca008-3635-4569-bdb6-434602e1330a','630917d7-2031-49a8-944d-e61fb189f006','idLyes','test','2026-03-08 14:08:39',0),('38df1cdd-dd0f-48a1-bb8d-6690bb35a144','14a41514-5015-4839-9f53-0e407ca4ce94','idAnais','acho','2026-03-08 12:21:03',0),('39965528-95ae-4992-a9bb-9a119149a8f9','630917d7-2031-49a8-944d-e61fb189f006','idLyes','test','2026-03-08 13:51:48',0),('45655d8c-c630-4c5f-8ecd-110df3c90e2c','630917d7-2031-49a8-944d-e61fb189f006','idLyes','test','2026-03-08 13:51:47',0),('5951f54e-b7a6-4475-8bae-9178b4f0b034','ff189b36-6e52-4f9c-9515-ce360d13a72d','idKhaled','ceci est un test','2026-03-08 14:05:01',0),('5a867a2a-6c2e-46a9-adba-6c2d9be18937','14a41514-5015-4839-9f53-0e407ca4ce94','idAnais','piiii','2026-03-08 12:21:34',0),('5dbe9ec4-2a38-4ddf-8764-9030a9f490cd','59493a55-fdfb-46c5-b48f-b0918069bdcd','idLyes','test','2026-03-08 14:10:38',0),('64472574-ef44-497f-98e6-1ea2148844bd','ff189b36-6e52-4f9c-9515-ce360d13a72d','idKhaled','ceci est un test','2026-03-08 14:04:47',0),('7b7a0a13-6144-4885-af30-fa1a46f9df2f','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:58:56',0),('7f239d35-e0f4-4eee-bb65-7c8c13562806','14a41514-5015-4839-9f53-0e407ca4ce94','idLyes','alo alo','2026-03-08 12:14:16',0),('857774b1-03db-4b5d-946e-1512d1ace599','630917d7-2031-49a8-944d-e61fb189f006','idLyes','test','2026-03-08 13:59:07',0),('87e5fc52-78fc-4e94-a021-e02ee742a8e4','630917d7-2031-49a8-944d-e61fb189f006','idLyes','test','2026-03-08 14:08:30',0),('943c3403-63e0-49b6-ac8c-ee21fed9f648','630917d7-2031-49a8-944d-e61fb189f006','idLyes','test','2026-03-08 13:51:49',0),('9f055193-f08a-4370-8886-7e64bcfab432','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:51:41',0),('9f505480-25f9-4190-ae7c-96deccc332d1','59493a55-fdfb-46c5-b48f-b0918069bdcd','idLyes','test','2026-03-08 14:10:37',0),('a1c0fba8-8152-46da-84c4-9c7d94b191cc','14a41514-5015-4839-9f53-0e407ca4ce94','idLyes','dachu tebghit','2026-03-08 12:21:30',0),('af12596b-01f7-431f-8d76-a186453f02cc','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:58:54',0),('aff7b86c-fd42-4708-b2a7-84c3f85088bb','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:51:38',0),('b59f0df2-9a25-4d8d-a0be-1e14cf9ce2d9','630917d7-2031-49a8-944d-e61fb189f006','idLyes','test','2026-03-08 13:51:50',0),('b69c96f7-1e1a-4811-9d34-9e9812ae2b72','630917d7-2031-49a8-944d-e61fb189f006','idLyes','salut','2026-03-08 13:34:59',0),('bb9c7b02-bdd5-4c45-9e00-86824301993e','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:51:42',0),('bc2876fe-bf8f-47dc-ab7e-c566fba1bb57','14a41514-5015-4839-9f53-0e407ca4ce94','idAnais','dachu tebghit','2026-03-08 12:21:11',0),('bf3091ec-108f-410b-8c2a-f45873cbe05b','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:59:03',0),('cad94a2a-a805-44bd-a027-14e0662e51dc','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:58:55',0),('d22a303f-ada5-4268-bcfb-d7459a7e769d','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:42:20',0),('d4d73b54-a8f1-4e68-b177-e0eb27cfb163','14a41514-5015-4839-9f53-0e407ca4ce94','idLyes','dachu bghigh ?','2026-03-08 12:21:17',0),('d9e96942-cd78-46a3-bf52-1f8858ecb20f','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:58:52',0),('e1b4e3b1-d198-4965-b869-e6c20babc103','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:58:51',0),('e3b703dd-96f0-47db-bb7d-ae7ba406a8e6','630917d7-2031-49a8-944d-e61fb189f006','idLyes','Bonjour','2026-03-08 13:34:30',0),('eaba64ca-a8af-435c-8693-58b8983de57c','59493a55-fdfb-46c5-b48f-b0918069bdcd','idLyes','test','2026-03-08 14:10:39',0),('ec9637c6-62e3-4941-8940-6d0c4bd4c5ce','630917d7-2031-49a8-944d-e61fb189f006','idIslem','alo','2026-03-08 13:39:48',0),('ee09fe99-1301-4f23-b10d-b722231f3ee2','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:51:40',0),('f41f48cc-0d5b-42eb-a344-e8eeec2aae80','630917d7-2031-49a8-944d-e61fb189f006','idIslem','test','2026-03-08 13:42:54',0);
/*!40000 ALTER TABLE `Message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Utilisateur`
--

DROP TABLE IF EXISTS `Utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Utilisateur` (
  `id_utilisateur` varchar(36) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id_utilisateur`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Utilisateur`
--

LOCK TABLES `Utilisateur` WRITE;
/*!40000 ALTER TABLE `Utilisateur` DISABLE KEYS */;
INSERT INTO `Utilisateur` VALUES ('86298186-061f-41b1-9dba-d65699db9a3e','samira','samira1970'),('d3847f18-e4d7-4f26-b67a-4e0d5f44848a','Lyessss','lyes'),('idAnais','Anais','Anais2005'),('idIslem','Islem','Islem2007'),('idKhaled','Khaled','khaled2007'),('idLyes','Lyes','Lyes2005');
/*!40000 ALTER TABLE `Utilisateur` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Utilisateur_2`
--

DROP TABLE IF EXISTS `Utilisateur_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Utilisateur_2` (
  `id_utilisateur` varchar(36) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Utilisateur_2`
--

LOCK TABLES `Utilisateur_2` WRITE;
/*!40000 ALTER TABLE `Utilisateur_2` DISABLE KEYS */;
INSERT INTO `Utilisateur_2` VALUES ('idLyes','Lyes','Lyes2005');
/*!40000 ALTER TABLE `Utilisateur_2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Utilisateur_Conversation`
--

DROP TABLE IF EXISTS `Utilisateur_Conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Utilisateur_Conversation` (
  `id_utilisateur` varchar(36) NOT NULL,
  `id_conversation` varchar(36) NOT NULL,
  PRIMARY KEY (`id_utilisateur`,`id_conversation`),
  KEY `id_conversation` (`id_conversation`),
  CONSTRAINT `Utilisateur_Conversation_ibfk_1` FOREIGN KEY (`id_utilisateur`) REFERENCES `Utilisateur` (`id_utilisateur`) ON DELETE CASCADE,
  CONSTRAINT `Utilisateur_Conversation_ibfk_2` FOREIGN KEY (`id_conversation`) REFERENCES `Conversation` (`id_conversation`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Utilisateur_Conversation`
--

LOCK TABLES `Utilisateur_Conversation` WRITE;
/*!40000 ALTER TABLE `Utilisateur_Conversation` DISABLE KEYS */;
INSERT INTO `Utilisateur_Conversation` VALUES ('d3847f18-e4d7-4f26-b67a-4e0d5f44848a','14a41514-5015-4839-9f53-0e407ca4ce94'),('idAnais','14a41514-5015-4839-9f53-0e407ca4ce94'),('idLyes','14a41514-5015-4839-9f53-0e407ca4ce94'),('d3847f18-e4d7-4f26-b67a-4e0d5f44848a','2641000c-759f-43e7-9dda-4f308912677f'),('idAnais','2641000c-759f-43e7-9dda-4f308912677f'),('86298186-061f-41b1-9dba-d65699db9a3e','59493a55-fdfb-46c5-b48f-b0918069bdcd'),('idLyes','59493a55-fdfb-46c5-b48f-b0918069bdcd'),('idIslem','630917d7-2031-49a8-944d-e61fb189f006'),('idLyes','630917d7-2031-49a8-944d-e61fb189f006'),('idIslem','ff189b36-6e52-4f9c-9515-ce360d13a72d'),('idKhaled','ff189b36-6e52-4f9c-9515-ce360d13a72d');
/*!40000 ALTER TABLE `Utilisateur_Conversation` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-10 22:57:46
