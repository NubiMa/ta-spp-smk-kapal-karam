-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for db_spp
CREATE DATABASE IF NOT EXISTS `db_spp` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `db_spp`;

-- Dumping structure for view db_spp.laporan_bulanan
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `laporan_bulanan` (
	`periode` VARCHAR(7) NULL COLLATE 'utf8mb4_general_ci',
	`total_transaksi` BIGINT(19) NOT NULL,
	`total_pemasukan` DECIMAL(32,2) NULL,
	`rata_rata_bayar` DECIMAL(14,6) NULL,
	`jumlah_lunas` DECIMAL(23,0) NULL
) ENGINE=MyISAM;

-- Dumping structure for table db_spp.pembayaran
CREATE TABLE IF NOT EXISTS `pembayaran` (
  `id_transaksi` varchar(20) NOT NULL,
  `nis_siswa` varchar(20) NOT NULL,
  `nama_siswa` varchar(100) NOT NULL,
  `bulan_tahun` varchar(20) NOT NULL,
  `nominal_spp` decimal(10,2) NOT NULL,
  `potongan` decimal(10,2) DEFAULT '0.00',
  `jumlah_bayar` decimal(10,2) NOT NULL,
  `tanggal_bayar` datetime DEFAULT CURRENT_TIMESTAMP,
  `metode_pembayaran` enum('Cash','Transfer','Kartu Debit') DEFAULT 'Cash',
  `status_pembayaran` enum('Lunas','Belum Lunas','Cicilan') DEFAULT 'Lunas',
  `keterangan` text,
  `user_input` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_transaksi`),
  KEY `nis_siswa` (`nis_siswa`),
  KEY `user_input` (`user_input`),
  CONSTRAINT `pembayaran_ibfk_1` FOREIGN KEY (`nis_siswa`) REFERENCES `siswa` (`nis`) ON DELETE CASCADE,
  CONSTRAINT `pembayaran_ibfk_2` FOREIGN KEY (`user_input`) REFERENCES `users` (`username`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table db_spp.pembayaran: ~12 rows (approximately)
DELETE FROM `pembayaran`;
INSERT INTO `pembayaran` (`id_transaksi`, `nis_siswa`, `nama_siswa`, `bulan_tahun`, `nominal_spp`, `potongan`, `jumlah_bayar`, `tanggal_bayar`, `metode_pembayaran`, `status_pembayaran`, `keterangan`, `user_input`) VALUES
	('TRX2024010001', '2024001', 'Ahmad Rizky Pratama', 'Januari 2024', 150000.00, 25000.00, 125000.00, '2024-01-05 08:30:00', 'Cash', 'Lunas', 'Pembayaran tepat waktu', 'admin'),
	('TRX2024010002', '2024002', 'Siti Nurhaliza', 'Januari 2024', 150000.00, 0.00, 150000.00, '2024-01-07 09:15:00', 'Transfer', 'Lunas', 'Transfer BCA', 'admin'),
	('TRX2024010003', '2024003', 'Budi Santoso', 'Januari 2024', 150000.00, 0.00, 150000.00, '2024-01-10 10:00:00', 'Cash', 'Lunas', 'Pembayaran lancar', 'admin'),
	('TRX2024020001', '2024001', 'Ahmad Rizky Pratama', 'Februari 2024', 150000.00, 25000.00, 125000.00, '2024-02-03 08:45:00', 'Cash', 'Lunas', 'Beasiswa prestasi', 'admin'),
	('TRX2024020002', '2024002', 'Siti Nurhaliza', 'Februari 2024', 150000.00, 0.00, 150000.00, '2024-02-05 14:20:00', 'Transfer', 'Lunas', 'Transfer Mandiri', 'admin'),
	('TRX2024020003', '2025001', 'Lisa Permata', 'Februari 2024', 125000.00, 0.00, 125000.00, '2024-02-08 11:30:00', 'Cash', 'Lunas', 'Pembayaran kelas XI', 'admin'),
	('TRX2024030001', '2024001', 'Ahmad Rizky Pratama', 'Maret 2024', 150000.00, 25000.00, 125000.00, '2024-03-05 09:00:00', 'Cash', 'Lunas', 'Konsisten mendapat beasiswa', 'admin'),
	('TRX2024030002', '2024004', 'Maya Sari Indah', 'Maret 2024', 150000.00, 75000.00, 75000.00, '2024-03-07 13:45:00', 'Cash', 'Lunas', 'Beasiswa kurang mampu', 'admin'),
	('TRX2024030003', '2025002', 'Andi Rahman', 'Maret 2024', 125000.00, 25000.00, 100000.00, '2024-03-10 08:15:00', 'Transfer', 'Lunas', 'Potongan prestasi', 'admin'),
	('TRX2024040001', '2026001', 'Fajar Ramadan', 'April 2024', 100000.00, 0.00, 100000.00, '2024-04-02 07:30:00', 'Cash', 'Lunas', 'Pembayaran kelas X', 'admin'),
	('TRX2024040002', '2026003', 'Arif Setiawan', 'April 2024', 100000.00, 20000.00, 80000.00, '2024-04-05 16:00:00', 'Cash', 'Lunas', 'Potongan khusus', 'admin'),
	('TRX20251012001019539', '2024001', 'Ahmad Rizky Pratama', 'Oktober 2025', 150000.00, 25000.00, 200000.00, '2025-10-12 00:11:41', 'Cash', 'Lunas', '', 'admin');

-- Dumping structure for table db_spp.potongan_spp
CREATE TABLE IF NOT EXISTS `potongan_spp` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nis_siswa` varchar(20) NOT NULL,
  `nama_siswa` varchar(100) NOT NULL,
  `jenis_potongan` enum('Beasiswa Prestasi','Beasiswa Kurang Mampu','Potongan Khusus','Anak Guru') NOT NULL,
  `nominal_potongan` decimal(10,2) NOT NULL,
  `persentase_potongan` decimal(5,2) NOT NULL,
  `periode_mulai` date NOT NULL,
  `periode_selesai` date NOT NULL,
  `status_potongan` enum('Aktif','Selesai','Suspended') DEFAULT 'Aktif',
  `keterangan` text,
  `user_input` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `nis_siswa` (`nis_siswa`),
  KEY `user_input` (`user_input`),
  CONSTRAINT `potongan_spp_ibfk_1` FOREIGN KEY (`nis_siswa`) REFERENCES `siswa` (`nis`) ON DELETE CASCADE,
  CONSTRAINT `potongan_spp_ibfk_2` FOREIGN KEY (`user_input`) REFERENCES `users` (`username`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table db_spp.potongan_spp: ~5 rows (approximately)
DELETE FROM `potongan_spp`;
INSERT INTO `potongan_spp` (`id`, `nis_siswa`, `nama_siswa`, `jenis_potongan`, `nominal_potongan`, `persentase_potongan`, `periode_mulai`, `periode_selesai`, `status_potongan`, `keterangan`, `user_input`, `created_at`) VALUES
	(1, '2024001', 'Ahmad Rizky Pratama', 'Beasiswa Prestasi', 25000.00, 16.67, '2024-01-01', '2024-12-31', 'Aktif', 'Juara 1 Olimpiade Matematika Tingkat Kota', 'bendahara', '2025-09-25 00:45:19'),
	(2, '2024004', 'Maya Sari Indah', 'Beasiswa Kurang Mampu', 75000.00, 50.00, '2024-01-01', '2024-12-31', 'Aktif', 'Bantuan ekonomi keluarga kurang mampu', 'bendahara', '2025-09-25 00:45:19'),
	(3, '2025002', 'Andi Rahman', 'Beasiswa Prestasi', 25000.00, 20.00, '2024-01-01', '2024-12-31', 'Aktif', 'Prestasi bidang olahraga', 'bendahara', '2025-09-25 00:45:19'),
	(4, '2025005', 'Sari Wulandari', 'Potongan Khusus', 50000.00, 40.00, '2024-01-01', '2024-06-30', 'Aktif', 'Anak yatim piatu', 'bendahara', '2025-09-25 00:45:19'),
	(5, '2026003', 'Arif Setiawan', 'Potongan Khusus', 20000.00, 20.00, '2024-01-01', '2024-12-31', 'Aktif', 'Anak guru sekolah', 'bendahara', '2025-09-25 00:45:19');

-- Dumping structure for table db_spp.siswa
CREATE TABLE IF NOT EXISTS `siswa` (
  `nis` varchar(20) NOT NULL,
  `user_id` int DEFAULT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `kelas` varchar(20) NOT NULL,
  `tahun_ajaran` varchar(10) NOT NULL,
  `no_telepon` varchar(20) DEFAULT NULL,
  `alamat` text,
  `nama_ortu` varchar(100) DEFAULT NULL,
  `nominal_spp` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total_potongan` decimal(10,2) DEFAULT '0.00',
  `status_siswa` enum('Aktif','Lulus','Pindah','Drop Out') DEFAULT 'Aktif',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`nis`),
  KEY `fk_siswa_user` (`user_id`),
  CONSTRAINT `fk_siswa_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table db_spp.siswa: ~16 rows (approximately)
DELETE FROM `siswa`;
INSERT INTO `siswa` (`nis`, `user_id`, `nama_lengkap`, `kelas`, `tahun_ajaran`, `no_telepon`, `alamat`, `nama_ortu`, `nominal_spp`, `total_potongan`, `status_siswa`, `created_at`) VALUES
	('2024', NULL, 'bu mega', '7', '2024/2025', '582252', 'gybhjbygbh', 'gbhbhb b', 100000.00, 0.00, 'Aktif', '2025-11-24 04:25:13'),
	('2024001', 4, 'Ahmad Rizky Pratama', 'XII IPA 1', '2024/2025', '081234567893', 'Jl. Merdeka No. 1, Surabaya', 'Budi Rizky', 150000.00, 25000.00, 'Aktif', '2025-09-25 00:45:19'),
	('2024002', 5, 'Siti Nurhaliza', 'XII IPS 1', '2024/2025', '081234567894', 'Jl. Pahlawan No. 2, Surabaya', 'Rahman Hakim', 150000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2024003', 6, 'Budi Santoso', 'XII IPA 2', '2024/2025', '081234567895', 'Jl. Pemuda No. 3, Surabaya', 'Santoso Wijaya', 150000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2024004', NULL, 'Maya Sari Indah', 'XII IPS 2', '2024/2025', '081234567896', 'Jl. Diponegoro No. 4, Surabaya', 'Joko Sari', 150000.00, 75000.00, 'Aktif', '2025-09-25 00:45:19'),
	('2024005', NULL, 'Dedi Kurniawan', 'XII IPA 1', '2024/2025', '081234567897', 'Jl. Sudirman No. 5, Surabaya', 'Kurnia Wijaya', 150000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2025001', NULL, 'Lisa Permata', 'XI IPA 1', '2024/2025', '081234567898', 'Jl. Thamrin No. 6, Surabaya', 'Permata Sari', 125000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2025002', NULL, 'Andi Rahman', 'XI IPS 1', '2024/2025', '081234567899', 'Jl. Gatot Subroto No. 7, Surabaya', 'Rahman Andi', 125000.00, 25000.00, 'Aktif', '2025-09-25 00:45:19'),
	('2025003', NULL, 'Dewi Lestari', 'XI IPA 2', '2024/2025', '081234567800', 'Jl. Ahmad Yani No. 8, Surabaya', 'Lestari Dewi', 125000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2025004', NULL, 'Riko Pratama', 'XI IPS 2', '2024/2025', '081234567801', 'Jl. Basuki Rahmat No. 9, Surabaya', 'Pratama Jaya', 125000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2025005', NULL, 'Sari Wulandari', 'XI IPA 1', '2024/2025', '081234567802', 'Jl. Veteran No. 10, Surabaya', 'Wulan Sari', 125000.00, 50000.00, 'Aktif', '2025-09-25 00:45:19'),
	('2026001', NULL, 'Fajar Ramadan', 'X IPA 1', '2024/2025', '081234567803', 'Jl. Raya Darmo No. 11, Surabaya', 'Ramadan Fajar', 100000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2026002', NULL, 'Nina Karlina', 'X IPS 1', '2024/2025', '081234567804', 'Jl. Ngagel No. 12, Surabaya', 'Karlin Nina', 100000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2026003', NULL, 'Arif Setiawan', 'X IPA 2', '2024/2025', '081234567805', 'Jl. Ketintang No. 13, Surabaya', 'Setia Arif', 100000.00, 20000.00, 'Aktif', '2025-09-25 00:45:19'),
	('2026004', NULL, 'Rina Melati', 'X IPS 2', '2024/2025', '081234567806', 'Jl. Wonokromo No. 14, Surabaya', 'Melati Indah', 100000.00, 0.00, 'Aktif', '2025-09-25 00:45:19'),
	('2026005', NULL, 'Bagus Purnomo', 'X IPA 1', '2024/2025', '081234567807', 'Jl. Gubeng No. 15, Surabaya', 'Purno Bagus', 100000.00, 0.00, 'Aktif', '2025-09-25 00:45:19');

-- Dumping structure for view db_spp.siswa_tunggakan
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `siswa_tunggakan` (
	`nis` VARCHAR(20) NOT NULL COLLATE 'utf8mb4_general_ci',
	`nama_lengkap` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_general_ci',
	`kelas` VARCHAR(20) NOT NULL COLLATE 'utf8mb4_general_ci',
	`nominal_spp` DECIMAL(10,2) NOT NULL,
	`total_potongan` DECIMAL(10,2) NULL,
	`spp_harus_bayar` DECIMAL(11,2) NULL,
	`total_sudah_bayar` DECIMAL(32,2) NOT NULL,
	`sisa_tunggakan` DECIMAL(33,2) NULL
) ENGINE=MyISAM;

-- Dumping structure for table db_spp.kelas
CREATE TABLE IF NOT EXISTS `kelas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `kelas` varchar(20) NOT NULL,
  `angkatan` varchar(10) NOT NULL,
  `nominal_spp` decimal(10,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `kelas_angkatan` (`kelas`, `angkatan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table db_spp.kelas: (sample data)
INSERT INTO `kelas` (`kelas`, `angkatan`, `nominal_spp`, `created_at`) VALUES
	('X', '2024/2025', 100000.00, '2024-01-01 00:00:00'),
	('XI', '2024/2025', 125000.00, '2024-01-01 00:00:00'),
	('XII', '2024/2025', 150000.00, '2024-01-01 00:00:00');

-- Update siswa table to reference kelas (by having the kelas field match the kelas name)
-- This maintains compatibility while allowing relationship between tables

-- Dumping structure for table db_spp.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('Kepsek','Bendahara','TU','Siswa') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `no_telepon` varchar(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table db_spp.users: ~11 rows (approximately)
DELETE FROM `users`;
INSERT INTO `users` (`id`, `username`, `password`, `role`, `nama_lengkap`, `no_telepon`, `is_active`, `created_at`) VALUES
	(1, 'admin', 'admin123', 'TU', 'Sungadi', '081234567890', 1, '2025-09-25 00:45:19'),
	(2, 'kepsek', 'kepsek123', 'Kepsek', 'Dr. Ahmad Suryadi, M.Pd', '081234567891', 1, '2025-09-25 00:45:19'),
	(3, 'bendahara', 'bendahara123', 'Bendahara', 'Siti Rahmawati, S.E', '081234567892', 1, '2025-09-25 00:45:19'),
	(4, 'siswa_001', 'siswa123', 'Siswa', 'Ahmad Rizky Pratama', '081234567893', 1, '2025-09-25 00:45:19'),
	(5, 'siswa_002', 'siswa123', 'Siswa', 'Siti Nurhaliza', '081234567894', 0, '2025-09-25 00:45:19'),
	(6, 'siswa_003', 'siswa123', 'Siswa', 'Budi Santoso', '081234567895', 1, '2025-09-25 00:45:19'),
	(7, 'amba', 'amba123', 'Siswa', 'Mas Amba', '086944776969', 1, '2025-11-20 15:32:27'),
	(8, 'Mr. Ironi', 'ironi123', 'Siswa', 'Mr. Ironi S.Coem', '008122600960', 1, '2025-11-20 15:36:02'),
	(9, 'JPC hama', '123456', 'Bendahara', 'Mochammad Eko Noviansyah', '08777565656', 0, '2025-11-22 12:30:18'),
	(10, 'test', '123456', 'Kepsek', 'sas', 'asas', 0, '2025-11-22 14:33:40'),
	(11, 'djahdihqIHI', '123456', 'Kepsek', 'DFE', '12344', 0, '2025-11-22 14:35:57');

-- Dumping structure for trigger db_spp.update_potongan_siswa
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `update_potongan_siswa` AFTER INSERT ON `potongan_spp` FOR EACH ROW BEGIN
    UPDATE siswa 
    SET total_potongan = (
        SELECT COALESCE(SUM(nominal_potongan), 0)
        FROM potongan_spp 
        WHERE nis_siswa = NEW.nis_siswa 
        AND status_potongan = 'Aktif'
    )
    WHERE nis = NEW.nis_siswa;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for view db_spp.laporan_bulanan
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `laporan_bulanan`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `laporan_bulanan` AS select date_format(`pembayaran`.`tanggal_bayar`,'%Y-%m') AS `periode`,count(0) AS `total_transaksi`,sum(`pembayaran`.`jumlah_bayar`) AS `total_pemasukan`,avg(`pembayaran`.`jumlah_bayar`) AS `rata_rata_bayar`,sum((case when (`pembayaran`.`status_pembayaran` = 'Lunas') then 1 else 0 end)) AS `jumlah_lunas` from `pembayaran` group by date_format(`pembayaran`.`tanggal_bayar`,'%Y-%m') order by `periode` desc;

-- Dumping structure for view db_spp.siswa_tunggakan
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `siswa_tunggakan`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `siswa_tunggakan` AS select `s`.`nis` AS `nis`,`s`.`nama_lengkap` AS `nama_lengkap`,`s`.`kelas` AS `kelas`,`s`.`nominal_spp` AS `nominal_spp`,`s`.`total_potongan` AS `total_potongan`,(`s`.`nominal_spp` - `s`.`total_potongan`) AS `spp_harus_bayar`,coalesce(`p`.`total_bayar`,0) AS `total_sudah_bayar`,((`s`.`nominal_spp` - `s`.`total_potongan`) - coalesce(`p`.`total_bayar`,0)) AS `sisa_tunggakan` from (`siswa` `s` left join (select `pembayaran`.`nis_siswa` AS `nis_siswa`,sum(`pembayaran`.`jumlah_bayar`) AS `total_bayar` from `pembayaran` where (date_format(`pembayaran`.`tanggal_bayar`,'%Y-%m') = date_format(curdate(),'%Y-%m')) group by `pembayaran`.`nis_siswa`) `p` on((`s`.`nis` = `p`.`nis_siswa`))) where ((`s`.`status_siswa` = 'Aktif') and (((`s`.`nominal_spp` - `s`.`total_potongan`) - coalesce(`p`.`total_bayar`,0)) > 0));

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
