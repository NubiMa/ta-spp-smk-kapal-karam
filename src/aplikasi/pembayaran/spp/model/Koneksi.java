package aplikasi.pembayaran.spp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Koneksi Database - Fixed version
 * Sesuai dengan struktur project lo
 */
public class Koneksi {
    private static Connection conn;
    
    // Database config - pastikan sesuai dengan database lo
    private static final String URL = "jdbc:mysql://localhost:3306/db_spp";
    private static final String USER = "root";
    private static final String PASS = "";
    
    /**
     * Method untuk mendapatkan koneksi database
     */
    public static Connection getConnection() {  
        try {
            if (conn == null || conn.isClosed()) {
                // Load MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Buat koneksi
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✅ Koneksi database berhasil");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver MySQL tidak ditemukan");
            System.out.println("Pastikan mysql-connector-j-9.4.0.jar ada di classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Gagal konek ke database: " + e.getMessage());
            System.out.println("Pastikan MySQL server jalan dan database 'db_spp' ada");
            e.printStackTrace();
        }
        return conn;
    }
    
    /**
     * Method untuk tutup koneksi
     */
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔒 Koneksi database ditutup");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error tutup koneksi: " + e.getMessage());
        }
    }
    
    /**
     * Method untuk setup database tables
     * Dipanggil otomatis saat aplikasi start
     */
    public static void setupDatabase() {
        Connection connection = getConnection();
        if (connection == null) {
            System.out.println("❌ Tidak bisa setup database karena koneksi gagal");
            return;
        }

        try (Statement stmt = connection.createStatement()) {
            // Create kelas table if it doesn't exist
            String createKelasTable = "CREATE TABLE IF NOT EXISTS kelas (" +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "kelas VARCHAR(20) NOT NULL," +
                    "angkatan VARCHAR(10) NOT NULL," +
                    "nominal_spp DECIMAL(10,2) NOT NULL," +
                    "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (id)," +
                    "UNIQUE KEY kelas_angkatan (kelas, angkatan)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci";

            stmt.executeUpdate(createKelasTable);
            System.out.println("✅ Tabel kelas siap digunakan");

            // Ensure siswa table has the total_potongan column (in case it was missing)
            try {
                stmt.executeUpdate("ALTER TABLE siswa ADD COLUMN total_potongan DECIMAL(10,2) DEFAULT '0.00'");
                System.out.println("✅ Kolom total_potongan ditambahkan ke tabel siswa");
            } catch (SQLException e) {
                // This is expected if column already exists
                if (e.getErrorCode() == 1060) { // MySQL duplicate column error
                    System.out.println("ℹ️ Kolom total_potongan sudah ada di tabel siswa");
                } else {
                    System.out.println("ℹ️ Kolom total_potongan mungkin sudah ada (error: " + e.getMessage() + ")");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error setup database: " + e.getMessage());
        }
    }
    
    /**
     * Method untuk test koneksi
     */
    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✅ Test koneksi berhasil");
                return true;
            }
        } catch (Exception e) {
            System.out.println("❌ Test koneksi gagal: " + e.getMessage());
        }
        return false;
    }
}