-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 23, 2025 at 11:03 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `health_tracker`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_user` (IN `p_user_id` VARCHAR(50), IN `p_full_name` VARCHAR(100), IN `p_password` VARCHAR(255), OUT `p_generated_id` INT)   BEGIN
    INSERT INTO users (user_id, full_name, password, created_at, is_active)
    VALUES (p_user_id, p_full_name, p_password, CURRENT_TIMESTAMP, TRUE);
    
    SET p_generated_id = LAST_INSERT_ID();
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `appointments`
--

CREATE TABLE `appointments` (
  `a_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `reason` varchar(40) NOT NULL,
  `doctor` varchar(40) NOT NULL,
  `specialization` varchar(40) NOT NULL,
  `user` varchar(40) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Scheduled'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointments`
--

INSERT INTO `appointments` (`a_id`, `date`, `time`, `reason`, `doctor`, `specialization`, `user`, `status`) VALUES
(10, '2025-09-30', '12:00:00', 'allergy', 'dr. Jani', 'Clinical Immunology/Allergy', 'neel', 'Scheduled'),
(11, '2025-08-25', '10:32:00', 'sick', 'jd', 'Cardiology', 'nilay123', 'Scheduled');

--
-- Triggers `appointments`
--
DELIMITER $$
CREATE TRIGGER `backup_deleted_appointments` BEFORE DELETE ON `appointments` FOR EACH ROW BEGIN
    INSERT INTO AppointmentsBackup (id, date, time, reason, doctor, specialization, user, status, deleted_at) -- UPDATED: Added status
    VALUES (OLD.id, OLD.date, OLD.time, OLD.reason, OLD.doctor, OLD.specialization, OLD.user, OLD.status, NOW()); -- UPDATED: Added OLD.status
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `check_appointment` BEFORE INSERT ON `appointments` FOR EACH ROW BEGIN
    DECLARE today DATE;
    DECLARE maxDate DATE;

    SET today = CURDATE();
    SET maxDate = DATE_ADD(today, INTERVAL 2 YEAR);

    -- Check if appointment date is outside the valid range
    IF NEW.date < today OR NEW.date > maxDate THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Appointment date must be between today and 2 years from today';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `appointmentsbackup`
--

CREATE TABLE `appointmentsbackup` (
  `a1_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `reason` varchar(40) NOT NULL,
  `doctor` varchar(40) NOT NULL,
  `specialization` varchar(40) NOT NULL,
  `user` varchar(40) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Scheduled',
  `deleted_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointmentsbackup`
--

INSERT INTO `appointmentsbackup` (`a1_id`, `date`, `time`, `reason`, `doctor`, `specialization`, `user`, `status`, `deleted_at`) VALUES
(7, '2024-08-08', '09:10:00', 'chest pain', 'jha', 'Cardiology', 'het', 'Completed', '2024-08-29 10:37:05'),
(8, '2024-02-15', '10:00:00', 'heart attack', 'purav jha', 'Cardiology', 'het', 'Cancelled', '2024-08-29 12:30:54'),
(2, '2024-12-28', '09:25:00', 'skin alleargy', 'Immunology', 'jha', 'neel', 'Scheduled', '2025-08-21 05:46:51'),
(1, '2024-12-28', '09:10:00', 'chest pain', 'dr.shah', 'Cardiology', 'neel', 'Scheduled', '2025-08-21 05:57:18'),
(12, '2025-08-30', '10:10:00', 'stomach ache', 'dr. Jani', 'Gastroenterology', 'neel', 'Scheduled', '2025-08-22 07:29:51');

-- --------------------------------------------------------

--
-- Table structure for table `healthmetrics`
--

CREATE TABLE `healthmetrics` (
  `h_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `bp` bigint(20) NOT NULL,
  `heartRate` bigint(20) NOT NULL,
  `bloodOxygen` int(11) NOT NULL,
  `calories` bigint(20) NOT NULL,
  `user` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `healthmetrics`
--

INSERT INTO `healthmetrics` (`h_id`, `date`, `bp`, `heartRate`, `bloodOxygen`, `calories`, `user`) VALUES
(1, '2024-12-28', 50, 50, 505, 50, 'neel'),
(2, '2025-01-28', 50, 50, 50, 50, 'neel'),
(3, '2025-02-24', 10, 20, 30, 40, 'yatharth'),
(4, '2025-03-28', 50, 50, 50, 50, 'priyal'),
(5, '2025-04-28', 50, 50, 50, 50, 'yatharth'),
(6, '2025-05-27', 500, 500, 50, 50, 'priyal'),
(7, '2025-06-28', 50, 50, 50, 50, 'neel'),
(9, '2025-09-30', 80, 50, 90, 10, 'neel'),
(10, '2025-08-25', 99, 90, 91, 934, 'nilay123');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `user_id`, `full_name`, `password`) VALUES
(1, 'neel', 'Neel shah', '28'),
(2, 'priyal', 'Priyal', '30'),
(3, 'yatharth', 'Yatharth', '32'),
(4, 'priyal2', 'Priyal gupta', '17'),
(5, 'neel4', 'Neel Jain', '10'),
(6, 'nilay', 'Nilay shukla', 'nilay@123'),
(7, 'rahil1', 'Rahil', '89');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`a_id`);

--
-- Indexes for table `healthmetrics`
--
ALTER TABLE `healthmetrics`
  ADD PRIMARY KEY (`h_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `appointments`
--
ALTER TABLE `appointments`
  MODIFY `a_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `healthmetrics`
--
ALTER TABLE `healthmetrics`
  MODIFY `h_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
