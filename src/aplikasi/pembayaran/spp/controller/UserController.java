package aplikasi.pembayaran.spp.controller;

import aplikasi.pembayaran.spp.model.User;
import aplikasi.pembayaran.spp.model.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller untuk handle semua operasi terkait User
 * Termasuk login, registrasi, dan manajemen user
 */
public class UserController {
    
    // Instance connection database
    private Connection connection;
    
    // Constructor - inisialisasi koneksi database
    public UserController() {
        this.connection = Koneksi.getConnection();
    }
    
    /**
     * Method untuk login user
     * @param username - username yang diinput user
     * @param password - password yang diinput user
     * @return User object jika berhasil login, null jika gagal
     */
    public User login(String username, String password) {
        // Query SQL untuk cari user berdasarkan username dan password
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = TRUE";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setNoTelepon(rs.getString("no_telepon"));
                user.setActive(rs.getBoolean("is_active"));
                
                System.out.println("✅ Login berhasil: " + user.getNamaLengkap() + " (" + user.getRole() + ")");
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat login: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error database saat login!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        // ─── Fallback: Login Siswa menggunakan NIS ───────────────────────────
        // Jika tidak ditemukan di tabel users, coba cocokkan sebagai NIS.
        // Password yang valid = NIS itu sendiri (default), atau password yang
        // sudah diubah oleh siswa dan tersimpan di kolom password tabel siswa.
        User siswaUser = loginSiswaByNis(username, password);
        if (siswaUser != null) {
            System.out.println("✅ Login siswa via NIS berhasil: " + siswaUser.getNamaLengkap());
            return siswaUser;
        }
        
        System.out.println("❌ Login gagal: Username atau password salah");
        return null;
    }
    
    /**
     * Login khusus Siswa: cari NIS di tabel siswa, password default = NIS.
     * Password yang sudah diubah disimpan di kolom siswa.password (jika ada),
     * atau tetap default NIS selama belum diubah.
     */
    private User loginSiswaByNis(String nis, String password) {
        // Cari siswa berdasarkan NIS. Password default = NIS itu sendiri.
        // Jika siswa sudah ganti password, tersimpan di kolom siswa.password.
        String sqlSimple = "SELECT * FROM siswa WHERE nis = ? AND status_siswa = 'Aktif'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sqlSimple)) {
            pstmt.setString(1, nis);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Cek password: coba baca kolom 'password' jika ada
                String storedPassword = null;
                try {
                    storedPassword = rs.getString("password");
                } catch (SQLException ex) {
                    // Kolom password belum ada di tabel siswa, gunakan NIS sebagai default
                    storedPassword = null;
                }
                
                // Jika belum pernah diubah (null/kosong), password default = NIS
                if (storedPassword == null || storedPassword.trim().isEmpty()) {
                    storedPassword = nis;
                }
                
                if (!storedPassword.equals(password)) {
                    return null; // Password salah
                }
                
                // Buat User object untuk siswa
                User user = new User();
                user.setUsername(rs.getString("nis")); // username = NIS
                user.setPassword(storedPassword);
                user.setRole("Siswa");
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setNoTelepon(rs.getString("no_telepon"));
                user.setActive(true);
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat login siswa: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Method untuk registrasi user baru
     * Hanya bisa dilakukan oleh TU atau Admin
     */
    public boolean registerUser(User user, String currentUserRole) {
        // Validasi permission - hanya TU dan Admin yang bisa buat user baru
        if (!currentUserRole.equals("TU") && !currentUserRole.equals("Admin")) {
            JOptionPane.showMessageDialog(null, "Anda tidak memiliki akses untuk menambah user!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validasi data user
        if (!validateUserData(user)) {
            return false;
        }
        
        // Query SQL untuk insert user baru
        String sql = "INSERT INTO users (username, password, role, nama_lengkap, no_telepon, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getNamaLengkap());
            pstmt.setString(5, user.getNoTelepon());
            pstmt.setBoolean(6, user.isActive());
            
            // Execute insert
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ User berhasil didaftarkan: " + user.getUsername());
                JOptionPane.showMessageDialog(null, "User berhasil didaftarkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL error code untuk duplicate key
                System.err.println("❌ Username sudah digunakan: " + user.getUsername());
                JOptionPane.showMessageDialog(null, "Username sudah digunakan! Pilih username lain.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("❌ Error saat registrasi: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Error database saat registrasi!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;
    }
    
    /**
     * Method untuk update profile user
     */
    public boolean updateProfile(User user, String currentUsername) {
        // Validasi - user hanya bisa update profile sendiri, kecuali TU/Admin
        if (!user.getUsername().equals(currentUsername) && 
            !getCurrentUserRole(currentUsername).equals("TU") && 
            !getCurrentUserRole(currentUsername).equals("Admin")) {
            JOptionPane.showMessageDialog(null, "Anda hanya bisa update profile sendiri!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String sql = "UPDATE users SET nama_lengkap = ?, no_telepon = ?, password = ? WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getNamaLengkap());
            pstmt.setString(2, user.getNoTelepon());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUsername());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Profile berhasil diupdate: " + user.getUsername());
                JOptionPane.showMessageDialog(null, "Profile berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat update profile: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saat update profile!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    
    public boolean activateUser(String username, String currentUserRole) {
        System.out.println("🔍 Attempting to activate user: " + username);
        System.out.println("🔍 Current user role: " + currentUserRole);

        // Normalisasi role
        String normalizedRole = currentUserRole.trim().toLowerCase();

        // Admin, Bendahara, dan Kepsek boleh aktifkan user
        if (!normalizedRole.equals("admin") && 
            !normalizedRole.equals("bendahara") && 
            !normalizedRole.equals("kepsek")) {
            JOptionPane.showMessageDialog(null, 
                "Hanya Admin, Bendahara, atau Kepsek yang bisa mengaktifkan user!", 
                "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE users SET is_active = TRUE WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User berhasil diaktifkan kembali: " + username);
                JOptionPane.showMessageDialog(null, 
                    "User berhasil diaktifkan kembali!", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saat activate user: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error saat mengaktifkan user!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }
    /**
     * Method untuk deactivate user (soft delete)
     * Hanya bisa dilakukan oleh Admin
     */
    public boolean deactivateUser(String username, String currentUserRole) {
        System.out.println("🔍 Attempting to deactivate user: " + username);
        System.out.println("🔍 Current user role: " + currentUserRole);

        // Normalisasi role (case insensitive & trim)
        String normalizedRole = currentUserRole.trim().toLowerCase();

        // Admin, Bendahara, dan Kepsek boleh nonaktifkan user
        if (!normalizedRole.equals("admin") && 
            !normalizedRole.equals("bendahara") && 
            !normalizedRole.equals("kepsek")) {
            JOptionPane.showMessageDialog(null, 
                "Hanya Admin, Bendahara, atau Kepsek yang bisa menonaktifkan user!", 
                "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE users SET is_active = FALSE WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User berhasil dinonaktifkan: " + username);
                JOptionPane.showMessageDialog(null, 
                    "User berhasil dinonaktifkan!", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saat deactivate user: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error saat menonaktifkan user!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }
    
    public boolean deleteUser(String username, String currentUserRole) {
        System.out.println("🔍 Attempting to DELETE user: " + username);
        System.out.println("🔍 Current user role: " + currentUserRole);

        // Normalisasi role
        String normalizedRole = currentUserRole.trim().toLowerCase();

        // Hanya Admin yang boleh hapus permanent
        if (!normalizedRole.equals("admin")) {
            JOptionPane.showMessageDialog(null, 
                "Hanya Admin yang bisa menghapus user secara permanen!", 
                "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "DELETE FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User berhasil dihapus: " + username);
                JOptionPane.showMessageDialog(null, 
                    "User berhasil dihapus secara permanen!", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saat delete user: " + e.getMessage());

            // Check jika error karena foreign key constraint
            if (e.getMessage().contains("foreign key constraint")) {
                JOptionPane.showMessageDialog(null, 
                    "User ini tidak bisa dihapus karena masih memiliki data transaksi!\n" +
                    "Gunakan fitur 'Nonaktifkan' sebagai gantinya.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Error saat menghapus user: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return false;
    }

    
    /**
     * Method untuk mendapatkan semua user (untuk Kepsek dan Admin)
     */
    public List<User> getAllUsers(String currentUserRole) {
        List<User> users = new ArrayList<>();
        
        String sql = "SELECT * FROM users ORDER BY role, nama_lengkap";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            // Loop through hasil query dan create User objects
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setNoTelepon(rs.getString("no_telepon"));
                user.setActive(rs.getBoolean("is_active"));
                
                users.add(user);
            }
            
            System.out.println("✅ Berhasil load " + users.size() + " users");
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat mengambil data users: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saat mengambil data users!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return users;
    }
    
    /**
     * Overload tanpa parameter untuk backwards compatibility
     */
    public List<User> getAllUsers() {
        return getAllUsers("");
    }

    /**
     * Method untuk mendapatkan role user berdasarkan username
     */
    public String getCurrentUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("role");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat mengambil role user: " + e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Method untuk validasi data user sebelum disimpan
     */
    private boolean validateUserData(User user) {
        // Cek apakah semua field required sudah diisi
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (user.getNamaLengkap() == null || user.getNamaLengkap().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama lengkap tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!user.isValidRole()) {
            JOptionPane.showMessageDialog(null, "Role tidak valid! Pilih: Kepsek, Admin, TU, atau Siswa", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validasi panjang username (min 3 karakter)
        if (user.getUsername().length() < 3) {
            JOptionPane.showMessageDialog(null, "Username minimal 3 karakter!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validasi panjang password (min 6 karakter)
        if (user.getPassword().length() < 6) {
            JOptionPane.showMessageDialog(null, "Password minimal 6 karakter!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Method untuk cek apakah username sudah digunakan
     */
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat cek username: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Method untuk mendapatkan statistik user berdasarkan role
     */
    public void showUserStatistics() {
        String sql = "SELECT role, COUNT(*) as jumlah FROM users WHERE is_active = TRUE GROUP BY role";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            System.out.println("\n=== STATISTIK USER ===");
            while (rs.next()) {
                System.out.println(rs.getString("role") + ": " + rs.getInt("jumlah") + " user");
            }
            System.out.println("=====================\n");
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat mengambil statistik user: " + e.getMessage());
        }
    }

    /**
     * Method untuk mendapatkan user by username
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
        User user = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setNoTelepon(rs.getString("no_telepon"));
                user.setActive(rs.getBoolean("is_active"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getUserByUsername: " + e.getMessage());
        }

        return user;
    }

    /**
     * Method untuk update user
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET password = ?, role = ?, nama_lengkap = ?, no_telepon = ?, is_active = ? WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getRole());
            pstmt.setString(3, user.getNamaLengkap());
            pstmt.setString(4, user.getNoTelepon());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setString(6, user.getUsername());

            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("✅ User berhasil diupdate: " + user.getUsername());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updateUser: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saat update user!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }

    /**
     * Update password siswa di tabel siswa (bukan users).
     * Dipanggil dari DashboardSiswa ketika siswa ganti password.
     */
    public boolean updateSiswaPassword(String nis, String newPassword) {
        // Pastikan kolom password ada dulu (ALTER jika belum)
        // Kita coba langsung UPDATE; jika kolom belum ada akan throw exception
        // dan kita tangani dengan ALTER TABLE otomatis.
        String sqlUpdate = "UPDATE siswa SET password = ? WHERE nis = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, nis);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("\u2705 Password siswa berhasil diupdate: " + nis);
                JOptionPane.showMessageDialog(null, "Password berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            // Jika kolom 'password' belum ada, tambahkan kolom lalu coba lagi
            if (e.getMessage() != null && e.getMessage().contains("Unknown column")) {
                try {
                    String sqlAlter = "ALTER TABLE siswa ADD COLUMN password VARCHAR(100) DEFAULT NULL";
                    connection.prepareStatement(sqlAlter).execute();
                    System.out.println("\u2705 Kolom password berhasil ditambahkan ke tabel siswa");
                    
                    // Coba update lagi setelah ALTER
                    try (PreparedStatement pstmt2 = connection.prepareStatement(sqlUpdate)) {
                        pstmt2.setString(1, newPassword);
                        pstmt2.setString(2, nis);
                        int rows2 = pstmt2.executeUpdate();
                        if (rows2 > 0) {
                            System.out.println("\u2705 Password siswa berhasil diupdate setelah ALTER: " + nis);
                            JOptionPane.showMessageDialog(null, "Password berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            return true;
                        }
                    }
                } catch (SQLException ex2) {
                    System.err.println("\u274c Error ALTER TABLE siswa: " + ex2.getMessage());
                }
            } else {
                System.err.println("\u274c Error updateSiswaPassword: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Error saat mengubah password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
}