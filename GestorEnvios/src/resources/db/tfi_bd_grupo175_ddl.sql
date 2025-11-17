-- MySQL dump 10.13  Distrib 8.4.7, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: tfi_bd_grupo175
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `Empresas`
--

DROP TABLE IF EXISTS `Empresas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Empresas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Envio`
--

DROP TABLE IF EXISTS `Envio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Envio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `eliminado` tinyint(1) NOT NULL DEFAULT '0',
  `tracking` varchar(40) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `id_empresa` int DEFAULT NULL,
  `id_tipo_envio` int DEFAULT NULL,
  `costo` decimal(10,2) DEFAULT NULL,
  `fecha_despacho` date DEFAULT NULL,
  `fecha_estimada` date DEFAULT NULL,
  `id_estado_envio` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tracking` (`tracking`),
  KEY `id_empresa` (`id_empresa`),
  KEY `id_tipo_envio` (`id_tipo_envio`),
  KEY `id_estado_envio` (`id_estado_envio`),
  KEY `idx_costo_envio` (`costo`),
  CONSTRAINT `Envio_ibfk_1` FOREIGN KEY (`id_empresa`) REFERENCES `Empresas` (`id`),
  CONSTRAINT `Envio_ibfk_2` FOREIGN KEY (`id_tipo_envio`) REFERENCES `Tipos_Envio` (`id`),
  CONSTRAINT `Envio_ibfk_3` FOREIGN KEY (`id_estado_envio`) REFERENCES `Estados_Envio` (`id`),
  CONSTRAINT `chk_costo_positivo` CHECK ((`costo` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Estados_Envio`
--

DROP TABLE IF EXISTS `Estados_Envio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Estados_Envio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Estados_Pedido`
--

DROP TABLE IF EXISTS `Estados_Pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Estados_Pedido` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pedido`
--

DROP TABLE IF EXISTS `Pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pedido` (
  `id` int NOT NULL AUTO_INCREMENT,
  `eliminado` tinyint(1) NOT NULL DEFAULT '0',
  `numero` varchar(20) COLLATE utf8mb4_spanish_ci NOT NULL,
  `fecha` date NOT NULL,
  `cliente_nombre` varchar(120) COLLATE utf8mb4_spanish_ci NOT NULL,
  `total` decimal(12,2) DEFAULT NULL,
  `id_estado_pedido` int NOT NULL,
  `id_envio` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero` (`numero`),
  UNIQUE KEY `id_envio` (`id_envio`),
  KEY `id_estado_pedido` (`id_estado_pedido`),
  CONSTRAINT `Pedido_ibfk_1` FOREIGN KEY (`id_estado_pedido`) REFERENCES `Estados_Pedido` (`id`),
  CONSTRAINT `Pedido_ibfk_2` FOREIGN KEY (`id_envio`) REFERENCES `Envio` (`id`),
  CONSTRAINT `chk_formato_numero_pedido` CHECK (regexp_like(`numero`,_utf8mb4'^PED-[0-9]{8}$')),
  CONSTRAINT `chk_total_positivo` CHECK ((`total` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Tipos_Envio`
--

DROP TABLE IF EXISTS `Tipos_Envio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tipos_Envio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-16 16:38:20
