-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 01, 2025 at 08:08 PM
-- Server version: 10.4.32-MariaDB-log
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_spp`
--

-- --------------------------------------------------------

--
-- Table structure for table `kelas`
--

CREATE TABLE `kelas` (
  `id` int(11) NOT NULL,
  `kelas` varchar(20) NOT NULL,
  `angkatan` varchar(10) NOT NULL,
  `nominal_spp` decimal(10,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `kelas`
--

INSERT INTO `kelas` (`id`, `kelas`, `angkatan`, `nominal_spp`, `created_at`) VALUES
(1, 'XII RPL 1', '2025', 200000.00, '2025-11-30 02:56:50'),
(2, 'XII RPL 2', '2025', 200000.00, '2025-11-30 08:26:34'),
(3, 'XII TKJ 1', '2025/2026', 200000.00, '2025-11-30 08:38:01'),
(4, 'XII TKJ 2', '2025/2026', 200000.00, '2025-11-30 09:16:55'),
(5, 'XII TKJ 3', '2025/2026', 200000.00, '2025-11-30 09:45:16'),
(6, 'XII TKJ 4', '2025/2026', 200000.00, '2025-11-30 09:55:34'),
(7, 'XII TKJ 5', '2025/2026', 200000.00, '2025-11-30 09:55:55'),
(8, 'XII TM', '2025/2026', 200000.00, '2025-11-30 10:05:32');

-- --------------------------------------------------------

--
-- Stand-in structure for view `laporan_bulanan`
-- (See below for the actual view)
--
CREATE TABLE `laporan_bulanan` (
`periode` varchar(7)
,`total_transaksi` bigint(21)
,`total_pemasukan` decimal(32,2)
,`rata_rata_bayar` decimal(14,6)
,`jumlah_lunas` decimal(22,0)
);

-- --------------------------------------------------------

--
-- Table structure for table `pembayaran`
--

CREATE TABLE `pembayaran` (
  `id_transaksi` varchar(20) NOT NULL,
  `nis_siswa` varchar(20) NOT NULL,
  `nama_siswa` varchar(100) NOT NULL,
  `bulan_tahun` varchar(20) NOT NULL,
  `nominal_spp` decimal(10,2) NOT NULL,
  `jumlah_bayar` decimal(10,2) NOT NULL,
  `tanggal_bayar` datetime DEFAULT current_timestamp(),
  `metode_pembayaran` enum('Cash','Transfer','Kartu Debit') DEFAULT 'Cash',
  `status_pembayaran` enum('Lunas','Belum Lunas','Cicilan') DEFAULT 'Lunas',
  `keterangan` text DEFAULT NULL,
  `user_input` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pembayaran`
--

INSERT INTO `pembayaran` (`id_transaksi`, `nis_siswa`, `nama_siswa`, `bulan_tahun`, `nominal_spp`, `jumlah_bayar`, `tanggal_bayar`, `metode_pembayaran`, `status_pembayaran`, `keterangan`, `user_input`) VALUES
('TRX2024010001', '2024001', 'Ahmad Rizky Pratama', 'Januari 2024', 150000.00, 125000.00, '2024-01-05 08:30:00', 'Cash', 'Lunas', 'Pembayaran tepat waktu', NULL),
('TRX2024010002', '2024002', 'Siti Nurhaliza', 'Januari 2024', 150000.00, 150000.00, '2024-01-07 09:15:00', 'Transfer', 'Lunas', 'Transfer BCA', NULL),
('TRX2024010003', '2024003', 'Budi Santoso', 'Januari 2024', 150000.00, 150000.00, '2024-01-10 10:00:00', 'Cash', 'Lunas', 'Pembayaran lancar', NULL),
('TRX2024020001', '2024001', 'Ahmad Rizky Pratama', 'Februari 2024', 150000.00, 125000.00, '2024-02-03 08:45:00', 'Cash', 'Lunas', 'Beasiswa prestasi', NULL),
('TRX2024020002', '2024002', 'Siti Nurhaliza', 'Februari 2024', 150000.00, 150000.00, '2024-02-05 14:20:00', 'Transfer', 'Lunas', 'Transfer Mandiri', NULL),
('TRX2024020003', '2025001', 'Lisa Permata', 'Februari 2024', 125000.00, 125000.00, '2024-02-08 11:30:00', 'Cash', 'Lunas', 'Pembayaran kelas XI', NULL),
('TRX2024030001', '2024001', 'Ahmad Rizky Pratama', 'Maret 2024', 150000.00, 125000.00, '2024-03-05 09:00:00', 'Cash', 'Lunas', 'Konsisten mendapat beasiswa', NULL),
('TRX2024030002', '2024004', 'Maya Sari Indah', 'Maret 2024', 150000.00, 75000.00, '2024-03-07 13:45:00', 'Cash', 'Lunas', 'Beasiswa kurang mampu', NULL),
('TRX2024030003', '2025002', 'Andi Rahman', 'Maret 2024', 125000.00, 100000.00, '2024-03-10 08:15:00', 'Transfer', 'Lunas', 'Potongan prestasi', NULL),
('TRX2024040001', '2026001', 'Fajar Ramadan', 'April 2024', 100000.00, 100000.00, '2024-04-02 07:30:00', 'Cash', 'Lunas', 'Pembayaran kelas X', NULL),
('TRX2024040002', '2026003', 'Arif Setiawan', 'April 2024', 100000.00, 80000.00, '2024-04-05 16:00:00', 'Cash', 'Lunas', 'Potongan khusus', NULL),
('TRX20251012001019539', '2024001', 'Ahmad Rizky Pratama', 'Oktober 2025', 150000.00, 200000.00, '2025-10-12 00:11:41', 'Cash', 'Lunas', '', NULL),
('TRX20251107064239986', '15215', 'Daniel Suki', 'November 2025', 200000.00, 200000.00, '2025-11-07 06:42:56', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070036465', '15215', 'Daniel Suki', 'Mei 2025', 200000.00, 200000.00, '2025-11-07 07:00:36', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070037307', '15215', 'Daniel Suki', 'Agustus 2025', 200000.00, 200000.00, '2025-11-07 07:00:37', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070037940', '15215', 'Daniel Suki', 'Oktober 2025', 200000.00, 200000.00, '2025-11-07 07:00:37', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070038429', '15215', 'Daniel Suki', 'September 2025', 200000.00, 200000.00, '2025-11-07 07:00:38', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070038878', '15215', 'Daniel Suki', 'Juni 2025', 200000.00, 200000.00, '2025-11-07 07:00:38', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070039244', '15215', 'Daniel Suki', 'Maret 2025', 200000.00, 200000.00, '2025-11-07 07:00:39', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070039421', '15215', 'Daniel Suki', 'Juli 2025', 200000.00, 200000.00, '2025-11-07 07:00:39', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070039597', '15215', 'Daniel Suki', 'Januari 2025', 200000.00, 200000.00, '2025-11-07 07:00:39', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070039780', '15215', 'Daniel Suki', 'Februari 2025', 200000.00, 200000.00, '2025-11-07 07:00:39', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251107070039964', '15215', 'Daniel Suki', 'April 2025', 200000.00, 200000.00, '2025-11-07 07:00:39', 'Cash', 'Lunas', '', 'tatausaha'),
('TRX20251130073649289', '2024001', 'Ahmad Rizky Pratama', 'November 2025', 150000.00, 125000.00, '2025-11-30 07:37:28', 'Cash', 'Lunas', 'Lunas', NULL),
('TRX20251201113859061', '15215', 'Daniel Suki', 'Desember 2025', 200000.00, 200000.00, '2025-12-01 11:38:59', 'Transfer', 'Lunas', 'test', 'tatausaha');

-- --------------------------------------------------------

--
-- Table structure for table `siswa`
--

CREATE TABLE `siswa` (
  `nis` varchar(20) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `kelas` varchar(20) NOT NULL,
  `tahun_ajaran` varchar(10) NOT NULL,
  `no_telepon` varchar(20) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `nama_ortu` varchar(100) DEFAULT NULL,
  `nominal_spp` decimal(10,2) NOT NULL DEFAULT 0.00,
  `status_siswa` enum('Aktif','Lulus','Pindah','Drop Out') DEFAULT 'Aktif',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `total_potongan` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `siswa`
--

INSERT INTO `siswa` (`nis`, `user_id`, `nama_lengkap`, `kelas`, `tahun_ajaran`, `no_telepon`, `alamat`, `nama_ortu`, `nominal_spp`, `status_siswa`, `created_at`, `total_potongan`) VALUES
('15215', NULL, 'Daniel Suki', 'XII RPL 1', '2025', '', '', '', 200000.00, 'Aktif', '2025-11-30 03:38:17', 0.00),
('2024', NULL, 'bu mega', 'XII RPL 1', '2025', '', '', '', 200000.00, 'Aktif', '2025-11-24 04:25:13', 0.00),
('2024001', 4, 'Ahmad Rizky Athorik', 'XII RPL 1', '2025', '', '', '', 200000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2024002', 5, 'Siti Nurhaliza', 'XII RPL 2', '2025', '', '', '', 200000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2024003', 6, 'Budi Santoso', 'XII RPL 2', '2025', '', '', '', 200000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2024004', NULL, 'Maya Sari Indah', 'XII TM', '2025/2026', '', '', '', 200000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2024005', NULL, 'Dedi Kurniawan', 'XII IPA 1', '2024/2025', '081234567897', 'Jl. Sudirman No. 5, Surabaya', 'Kurnia Wijaya', 150000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2025001', NULL, 'Lisa Permata', 'XI IPA 1', '2024/2025', '081234567898', 'Jl. Thamrin No. 6, Surabaya', 'Permata Sari', 125000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2025002', NULL, 'Andi Rahman', 'XI IPS 1', '2024/2025', '081234567899', 'Jl. Gatot Subroto No. 7, Surabaya', 'Rahman Andi', 125000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2025003', NULL, 'Dewi Lestari', 'XI IPA 2', '2024/2025', '081234567800', 'Jl. Ahmad Yani No. 8, Surabaya', 'Lestari Dewi', 125000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2025004', NULL, 'Riko Pratama', 'XI IPS 2', '2024/2025', '081234567801', 'Jl. Basuki Rahmat No. 9, Surabaya', 'Pratama Jaya', 125000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2025005', NULL, 'Sari Wulandari', 'XI IPA 1', '2024/2025', '081234567802', 'Jl. Veteran No. 10, Surabaya', 'Wulan Sari', 125000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2026001', NULL, 'Fajar Ramadan', 'X IPA 1', '2024/2025', '081234567803', 'Jl. Raya Darmo No. 11, Surabaya', 'Ramadan Fajar', 100000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2026002', NULL, 'Nina Karlina', 'X IPS 1', '2024/2025', '081234567804', 'Jl. Ngagel No. 12, Surabaya', 'Karlin Nina', 100000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2026003', NULL, 'Arif Setiawan', 'X IPA 2', '2024/2025', '081234567805', 'Jl. Ketintang No. 13, Surabaya', 'Setia Arif', 100000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2026004', NULL, 'Rina Melati', 'X IPS 2', '2024/2025', '081234567806', 'Jl. Wonokromo No. 14, Surabaya', 'Melati Indah', 100000.00, 'Aktif', '2025-09-25 00:45:19', 0.00),
('2026005', NULL, 'Bagus Purnomo', 'X IPA 1', '2024/2025', '081234567807', 'Jl. Gubeng No. 15, Surabaya', 'Purno Bagus', 100000.00, 'Aktif', '2025-09-25 00:45:19', 0.00);

-- --------------------------------------------------------

--
-- Stand-in structure for view `siswa_tunggakan`
-- (See below for the actual view)
--
CREATE TABLE `siswa_tunggakan` (
`nis` varchar(20)
,`nama_lengkap` varchar(100)
,`kelas` varchar(20)
,`nominal_spp` decimal(10,2)
,`spp_harus_bayar` decimal(10,2)
,`total_sudah_bayar` decimal(32,2)
,`sisa_tunggakan` decimal(33,2)
);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('Kepsek','Admin','TU','Siswa') NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `no_telepon` varchar(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `nama_lengkap`, `no_telepon`, `is_active`, `created_at`) VALUES
(2, 'kepsek', 'kepsek123', 'Kepsek', 'Dr. Ahmad Suryadi, M.Pd', '081234567891', 1, '2025-09-25 00:45:19'),
(3, 'bendahara', 'bendahara123', 'Admin', 'Siti Rahmawati, S.E', '081234567892', 1, '2025-09-25 00:45:19'),
(4, 'siswa_001', 'siswa123', 'Siswa', 'Ahmad Rizky Pratama', '081234567893', 1, '2025-09-25 00:45:19'),
(5, 'siswa_002', 'siswa123', 'Siswa', 'Siti Nurhaliza', '081234567894', 0, '2025-09-25 00:45:19'),
(6, 'siswa_003', 'siswa123', 'Siswa', 'Budi Santoso', '081234567895', 1, '2025-09-25 00:45:19'),
(7, 'amba', 'amba123', 'Siswa', 'Mas Amba', '086944776969', 1, '2025-11-20 15:32:27'),
(8, 'Mr. Ironi', 'ironi123', 'Siswa', 'Mr. Ironi S.Coem', '008122600960', 1, '2025-11-20 15:36:02'),
(9, 'JPC hama', '123456', 'Admin', 'Mochammad Eko Noviansyah', '08777565656', 1, '2025-11-22 12:30:18'),
(11, 'djahdihqIHI', '123456', 'Kepsek', 'DFE', '12344', 0, '2025-11-22 14:35:57'),
(12, 'ambatukam', 'ambatukam123', 'Admin', 'ambatukam', '081313', 1, '2025-11-30 01:09:09'),
(13, 'admin', 'admin123', 'Admin', 'admin', 'admin', 1, '2025-11-30 01:30:57'),
(14, 'tatausaha', 'tatausaha123', 'TU', 'Ambatubus', '0819218312', 1, '2025-11-30 02:57:57');

-- --------------------------------------------------------

--
-- Structure for view `laporan_bulanan`
--
DROP TABLE IF EXISTS `laporan_bulanan`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `laporan_bulanan`  AS SELECT date_format(`pembayaran`.`tanggal_bayar`,'%Y-%m') AS `periode`, count(0) AS `total_transaksi`, sum(`pembayaran`.`jumlah_bayar`) AS `total_pemasukan`, avg(`pembayaran`.`jumlah_bayar`) AS `rata_rata_bayar`, sum(case when `pembayaran`.`status_pembayaran` = 'Lunas' then 1 else 0 end) AS `jumlah_lunas` FROM `pembayaran` GROUP BY date_format(`pembayaran`.`tanggal_bayar`,'%Y-%m') ORDER BY date_format(`pembayaran`.`tanggal_bayar`,'%Y-%m') DESC ;

-- --------------------------------------------------------

--
-- Structure for view `siswa_tunggakan`
--
DROP TABLE IF EXISTS `siswa_tunggakan`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `siswa_tunggakan`  AS SELECT `s`.`nis` AS `nis`, `s`.`nama_lengkap` AS `nama_lengkap`, `s`.`kelas` AS `kelas`, `s`.`nominal_spp` AS `nominal_spp`, `s`.`nominal_spp` AS `spp_harus_bayar`, coalesce(`p`.`total_bayar`,0) AS `total_sudah_bayar`, `s`.`nominal_spp`- coalesce(`p`.`total_bayar`,0) AS `sisa_tunggakan` FROM (`siswa` `s` left join (select `pembayaran`.`nis_siswa` AS `nis_siswa`,sum(`pembayaran`.`jumlah_bayar`) AS `total_bayar` from `pembayaran` where date_format(`pembayaran`.`tanggal_bayar`,'%Y-%m') = date_format(curdate(),'%Y-%m') group by `pembayaran`.`nis_siswa`) `p` on(`s`.`nis` = `p`.`nis_siswa`)) WHERE `s`.`status_siswa` = 'Aktif' AND `s`.`nominal_spp` - coalesce(`p`.`total_bayar`,0) > 0 ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `kelas`
--
ALTER TABLE `kelas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kelas_angkatan` (`kelas`,`angkatan`);

--
-- Indexes for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `nis_siswa` (`nis_siswa`),
  ADD KEY `user_input` (`user_input`);

--
-- Indexes for table `siswa`
--
ALTER TABLE `siswa`
  ADD PRIMARY KEY (`nis`),
  ADD KEY `fk_siswa_user` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `kelas`
--
ALTER TABLE `kelas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD CONSTRAINT `pembayaran_ibfk_1` FOREIGN KEY (`nis_siswa`) REFERENCES `siswa` (`nis`) ON DELETE CASCADE,
  ADD CONSTRAINT `pembayaran_ibfk_2` FOREIGN KEY (`user_input`) REFERENCES `users` (`username`) ON DELETE SET NULL;

--
-- Constraints for table `siswa`
--
ALTER TABLE `siswa`
  ADD CONSTRAINT `fk_siswa_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
