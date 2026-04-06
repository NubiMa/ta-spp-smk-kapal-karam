-- ============================================================
-- Reset Kelas & Siswa Data - SMK KAPAL KARAM
-- ============================================================

USE db_spp;

-- Disable FK checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Clear data safely (skip if table doesn't exist)
DELETE FROM pembayaran WHERE 1=1;
DELETE FROM siswa WHERE 1=1;
DELETE FROM kelas WHERE 1=1;

-- Hapus semua user dengan role Siswa dari tabel users
-- Mulai sekarang siswa login pakai NIS, tidak perlu akun di users
DELETE FROM users WHERE role = 'Siswa';

-- Tambahkan kolom password ke tabel siswa (jika belum ada)
-- Menggunakan stored procedure agar kompatibel dengan MySQL 5.x
DROP PROCEDURE IF EXISTS add_siswa_password_col;

DELIMITER //
CREATE PROCEDURE add_siswa_password_col()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'siswa'
          AND COLUMN_NAME  = 'password'
    ) THEN
        ALTER TABLE siswa
          ADD COLUMN `password` VARCHAR(100) DEFAULT NULL
          COMMENT 'NULL = gunakan NIS sebagai password default';
    END IF;
END//
DELIMITER ;

CALL add_siswa_password_col();
DROP PROCEDURE IF EXISTS add_siswa_password_col;

-- Re-enable FK checks
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- INSERT KELAS
-- 4 jurusan x 3 angkatan = 12 kelas
-- nominal_spp = 500000 per kelas
-- ============================================================
INSERT INTO kelas (kelas, angkatan, nominal_spp) VALUES
('X RPL',   '2024/2025', 500000),
('X AKL',   '2024/2025', 500000),
('X PB',    '2024/2025', 500000),
('X TKJ',   '2024/2025', 500000),
('XI RPL',  '2024/2025', 500000),
('XI AKL',  '2024/2025', 500000),
('XI PB',   '2024/2025', 500000),
('XI TKJ',  '2024/2025', 500000),
('XII RPL', '2024/2025', 500000),
('XII AKL', '2024/2025', 500000),
('XII PB',  '2024/2025', 500000),
('XII TKJ', '2024/2025', 500000);

-- ============================================================
-- INSERT SISWA (3 siswa per kelas = 36 siswa total)
-- Format NIS: XXYYY (XX = kelas, YYY = nomor urut)
-- ============================================================

-- X RPL (NIS 1001x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('10011', 'Andi Pratama',       'X RPL',   '2024/2025', '081234567001', 'Jl. Merdeka No.1',   500000, 0, 'Aktif', 'Budi Pratama'),
('10012', 'Beni Saputra',       'X RPL',   '2024/2025', '081234567002', 'Jl. Merdeka No.2',   500000, 0, 'Aktif', 'Candra Saputra'),
('10013', 'Citra Dewi',         'X RPL',   '2024/2025', '081234567003', 'Jl. Merdeka No.3',   500000, 0, 'Aktif', 'Dedi Dewi');

-- X AKL (NIS 1002x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('10021', 'Dani Kurniawan',     'X AKL',   '2024/2025', '081234567004', 'Jl. Ahmad Yani No.1',500000, 0, 'Aktif', 'Eko Kurniawan'),
('10022', 'Eka Putri',          'X AKL',   '2024/2025', '081234567005', 'Jl. Ahmad Yani No.2',500000, 0, 'Aktif', 'Fajar Putri'),
('10023', 'Faisal Hadi',        'X AKL',   '2024/2025', '081234567006', 'Jl. Ahmad Yani No.3',500000, 0, 'Aktif', 'Gunawan Hadi');

-- X PB (NIS 1003x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('10031', 'Gilang Ramadhan',    'X PB',    '2024/2025', '081234567007', 'Jl. Sudirman No.1',  500000, 0, 'Aktif', 'Hendra Ramadhan'),
('10032', 'Hani Fitriani',      'X PB',    '2024/2025', '081234567008', 'Jl. Sudirman No.2',  500000, 0, 'Aktif', 'Indra Fitriani'),
('10033', 'Irfan Hakim',        'X PB',    '2024/2025', '081234567009', 'Jl. Sudirman No.3',  500000, 0, 'Aktif', 'Joko Hakim');

-- X TKJ (NIS 1004x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('10041', 'Jeni Susanti',       'X TKJ',   '2024/2025', '081234567010', 'Jl. Imam Bonjol No.1',500000, 0,'Aktif', 'Karto Susanti'),
('10042', 'Kevin Pratama',      'X TKJ',   '2024/2025', '081234567011', 'Jl. Imam Bonjol No.2',500000, 0,'Aktif', 'Lukman Pratama'),
('10043', 'Laila Sari',         'X TKJ',   '2024/2025', '081234567012', 'Jl. Imam Bonjol No.3',500000, 0,'Aktif', 'Mahmud Sari');

-- XI RPL (NIS 1101x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('11011', 'Maulana Akbar',      'XI RPL',  '2024/2025', '081234567013', 'Jl. Diponegoro No.1',500000, 0, 'Aktif', 'Nasir Akbar'),
('11012', 'Nadia Rahma',        'XI RPL',  '2024/2025', '081234567014', 'Jl. Diponegoro No.2',500000, 0, 'Aktif', 'Omar Rahma'),
('11013', 'Okta Wijaya',        'XI RPL',  '2024/2025', '081234567015', 'Jl. Diponegoro No.3',500000, 0, 'Aktif', 'Pandu Wijaya');

-- XI AKL (NIS 1102x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('11021', 'Putri Anggraini',    'XI AKL',  '2024/2025', '081234567016', 'Jl. Gatot Subroto No.1',500000,0,'Aktif','Qomar Anggraini'),
('11022', 'Rafi Hidayat',       'XI AKL',  '2024/2025', '081234567017', 'Jl. Gatot Subroto No.2',500000,0,'Aktif','Rizal Hidayat'),
('11023', 'Sari Indah',         'XI AKL',  '2024/2025', '081234567018', 'Jl. Gatot Subroto No.3',500000,0,'Aktif','Suyanto Indah');

-- XI PB (NIS 1103x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('11031', 'Taufik Ismail',      'XI PB',   '2024/2025', '081234567019', 'Jl. HR. Rasuna Said No.1',500000,0,'Aktif','Udin Ismail'),
('11032', 'Umi Kalsum',         'XI PB',   '2024/2025', '081234567020', 'Jl. HR. Rasuna Said No.2',500000,0,'Aktif','Vino Kalsum'),
('11033', 'Vivi Ratnasari',     'XI PB',   '2024/2025', '081234567021', 'Jl. HR. Rasuna Said No.3',500000,0,'Aktif','Wahyu Ratnasari');

-- XI TKJ (NIS 1104x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('11041', 'Wahyu Setiawan',     'XI TKJ',  '2024/2025', '081234567022', 'Jl. Veteran No.1',   500000, 0, 'Aktif', 'Xander Setiawan'),
('11042', 'Xena Puspita',       'XI TKJ',  '2024/2025', '081234567023', 'Jl. Veteran No.2',   500000, 0, 'Aktif', 'Yanto Puspita'),
('11043', 'Yoga Firmansyah',    'XI TKJ',  '2024/2025', '081234567024', 'Jl. Veteran No.3',   500000, 0, 'Aktif', 'Zainal Firmansyah');

-- XII RPL (NIS 1201x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('12011', 'Zara Amelia',        'XII RPL', '2024/2025', '081234567025', 'Jl. Pemuda No.1',    500000, 0, 'Aktif', 'Ahmad Amelia'),
('12012', 'Aldi Nugroho',       'XII RPL', '2024/2025', '081234567026', 'Jl. Pemuda No.2',    500000, 0, 'Aktif', 'Bambang Nugroho'),
('12013', 'Bella Safitri',      'XII RPL', '2024/2025', '081234567027', 'Jl. Pemuda No.3',    500000, 0, 'Aktif', 'Cahyo Safitri');

-- XII AKL (NIS 1202x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('12021', 'Cahya Permana',      'XII AKL', '2024/2025', '081234567028', 'Jl. Pahlawan No.1',  500000, 0, 'Aktif', 'Darmawan Permana'),
('12022', 'Desi Rahayu',        'XII AKL', '2024/2025', '081234567029', 'Jl. Pahlawan No.2',  500000, 0, 'Aktif', 'Eko Rahayu'),
('12023', 'Edo Santoso',        'XII AKL', '2024/2025', '081234567030', 'Jl. Pahlawan No.3',  500000, 0, 'Aktif', 'Fadhil Santoso');

-- XII PB (NIS 1203x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('12031', 'Fitri Handayani',    'XII PB',  '2024/2025', '081234567031', 'Jl. Cendrawasih No.1',500000,0,'Aktif','Galih Handayani'),
('12032', 'Gita Pertiwi',       'XII PB',  '2024/2025', '081234567032', 'Jl. Cendrawasih No.2',500000,0,'Aktif','Heri Pertiwi'),
('12033', 'Hendra Junaidi',     'XII PB',  '2024/2025', '081234567033', 'Jl. Cendrawasih No.3',500000,0,'Aktif','Imam Junaidi');

-- XII TKJ (NIS 1204x)
INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES
('12041', 'Intan Permatasari',  'XII TKJ', '2024/2025', '081234567034', 'Jl. Kenanga No.1',   500000, 0, 'Aktif', 'Joko Permatasari'),
('12042', 'Jodi Prasetyo',      'XII TKJ', '2024/2025', '081234567035', 'Jl. Kenanga No.2',   500000, 0, 'Aktif', 'Koko Prasetyo'),
('12043', 'Kartika Dewi',       'XII TKJ', '2024/2025', '081234567036', 'Jl. Kenanga No.3',   500000, 0, 'Aktif', 'Lian Dewi');

-- ============================================================
-- Verify
-- ============================================================
SELECT 'Total Kelas:' AS info, COUNT(*) AS jumlah FROM kelas
UNION ALL
SELECT 'Total Siswa:', COUNT(*) FROM siswa;
