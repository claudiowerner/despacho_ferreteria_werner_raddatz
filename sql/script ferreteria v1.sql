-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-07-2021 a las 22:11:41
-- Versión del servidor: 10.4.19-MariaDB
-- Versión de PHP: 8.0.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `bd_despacho_wyr`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `caja_estado`
--

CREATE TABLE `caja_estado` (
  `cod_barra_caja` varchar(30) NOT NULL,
  `estatus` decimal(1,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `caja_estatus_reporte`
--

CREATE TABLE `caja_estatus_reporte` (
  `cod_barra_caja` varchar(30) NOT NULL,
  `num_doc` varchar(50) DEFAULT NULL,
  `fecha` varchar(10) DEFAULT NULL,
  `hora` varchar(5) DEFAULT NULL,
  `estatus` decimal(1,0) DEFAULT NULL,
  `comentario` varchar(1000) DEFAULT NULL,
  `id_dispositivo` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `dispositivo`
--

CREATE TABLE `dispositivo` (
  `id_dispositivo` varchar(30) NOT NULL,
  `marca_modelo` varchar(30) DEFAULT NULL,
  `id_empleado` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado`
--

CREATE TABLE `empleado` (
  `id_empleado` varchar(30) NOT NULL,
  `nombre` varchar(30) DEFAULT NULL,
  `apellido` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
COMMIT;


ALTER TABLE `caja_estado` ADD PRIMARY KEY(`cod_barra_caja`);
ALTER TABLE `caja_estatus_reporte` ADD CONSTRAINT `cod_barra_caja_fk` FOREIGN KEY (`cod_barra_caja`) REFERENCES `caja_estado`(`cod_barra_caja`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `dispositivo` ADD PRIMARY KEY(`id_dispositivo`);
ALTER TABLE `caja_estatus_reporte` ADD CONSTRAINT `id_dispositivo_fk` FOREIGN KEY (`id_dispositivo`) REFERENCES `dispositivo`(`id_dispositivo`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `empleado` ADD PRIMARY KEY(`id_empleado`);
ALTER TABLE `dispositivo` ADD CONSTRAINT `id_empleado_fk` FOREIGN KEY (`id_empleado`) REFERENCES `empleado`(`id_empleado`) ON DELETE CASCADE ON UPDATE CASCADE;


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
