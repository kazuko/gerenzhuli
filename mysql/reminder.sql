-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: 2015-11-28 14:38:15
-- 服务器版本： 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `gerenzhuli`
--

-- --------------------------------------------------------

--
-- 表的结构 `reminder`
--

CREATE TABLE IF NOT EXISTS `reminder` (
  `id` int(11) NOT NULL,
  `title` text NOT NULL,
  `date` text NOT NULL,
  `time` text NOT NULL,
  `is_repeat` tinyint(1) NOT NULL,
  `repeat_no` int(11) NOT NULL,
  `repeat_type` text NOT NULL,
  `active` tinyint(1) NOT NULL,
  `time_stamp` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `reminder`
--

INSERT INTO `reminder` (`id`, `title`, `date`, `time`, `is_repeat`, `repeat_no`, `repeat_type`, `active`, `time_stamp`) VALUES
(1, 'Anew', '28/11/2015', '16:29', 1, 1, 'Hour', 1, '2015-11-28 16:32:29'),
(3, 'C', '30/11/2015', '16:29', 0, 1, 'Hour', 1, '2015-11-28 16:30:02'),
(4, 'D2', '28/11/2015', '19:35', 1, 2, 'Hour', 0, '2015-11-28 16:37:29');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
