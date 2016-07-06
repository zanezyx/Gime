-- phpMyAdmin SQL Dump
-- version 4.4.11
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: 2016-07-06 12:40:42
-- 服务器版本： 10.1.13-MariaDB
-- PHP Version: 5.6.23

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `location`
--

-- --------------------------------------------------------

--
-- 表的结构 `location_operation`
--

CREATE TABLE IF NOT EXISTS `location_operation` (
  `id` int(40) NOT NULL COMMENT 'id',
  `imei` text NOT NULL COMMENT 'imei',
  `type` int(10) NOT NULL COMMENT 'type',
  `number` varchar(30) NOT NULL COMMENT 'number',
  `latitude` double NOT NULL COMMENT 'latitude',
  `longitude` double NOT NULL COMMENT 'longitude',
  `locationStatus` int(10) DEFAULT '1' COMMENT 'locationStatus'
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

--
-- 转存表中的数据 `location_operation`
--

INSERT INTO `location_operation` (`id`, `imei`, `type`, `number`, `latitude`, `longitude`, `locationStatus`) VALUES
(17, '867068022162860', 0, '13728987188', 22.52637, 113.936848, 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `location_operation`
--
ALTER TABLE `location_operation`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `location_operation`
--
ALTER TABLE `location_operation`
  MODIFY `id` int(40) NOT NULL AUTO_INCREMENT COMMENT 'id',AUTO_INCREMENT=18;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
