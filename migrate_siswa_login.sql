-- ============================================================
-- MIGRASI: Siswa Login Menggunakan NIS
-- Jalankan script ini pada database yang sudah berjalan
-- untuk menerapkan perubahan tanpa reset data.
-- Kompatibel dengan MySQL 5.x dan 8.x (Laragon)
-- ============================================================

USE db_spp;

-- 1. Tambahkan kolom password ke tabel siswa (jika belum ada)
--    Menggunakan stored procedure agar kompatibel dengan MySQL 5.x
--    (ADD COLUMN IF NOT EXISTS hanya tersedia di MySQL 8.0.3+)
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

-- 2. Hapus semua user dengan role Siswa dari tabel users
--    Siswa tidak lagi butuh akun di tabel users.
--    Login sekarang menggunakan NIS langsung dari tabel siswa.
DELETE FROM users WHERE role = 'Siswa';

-- 3. Verifikasi hasil
SELECT 'Users tersisa (hanya staff):' AS info;
SELECT id, username, role, nama_lengkap, is_active FROM users ORDER BY role;

SELECT 'Kolom password pada tabel siswa:' AS info;
SHOW COLUMNS FROM siswa LIKE 'password';
