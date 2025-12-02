package aplikasi.pembayaran.spp.controller;

import aplikasi.pembayaran.spp.model.Kelas;
import aplikasi.pembayaran.spp.model.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Kelas Controller - Handles all CRUD operations for Kelas
 * Manages class, year, and SPP amount data
 */
public class KelasController {
    private Connection connection;

    // Constructor - initialize database connection
    public KelasController() {
        this.connection = Koneksi.getConnection();
    }

    /**
     * Create a new class record
     * @param kelas The class object to be added
     * @return true if successful, false otherwise
     */
    public boolean createKelas(Kelas kelas) {
        // Check if class already exists
        if (isKelasExists(kelas.getKelas(), kelas.getAngkatan())) {
            JOptionPane.showMessageDialog(null, 
                "Kelas " + kelas.getKelas() + " untuk angkatan " + kelas.getAngkatan() + " sudah ada!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "INSERT INTO kelas (kelas, angkatan, nominal_spp) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, kelas.getKelas());
            pstmt.setString(2, kelas.getAngkatan());
            pstmt.setDouble(3, kelas.getNominalSPP());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Kelas berhasil ditambahkan: " + kelas.getKelas());
                JOptionPane.showMessageDialog(null, 
                    "Kelas berhasil ditambahkan!", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error
                JOptionPane.showMessageDialog(null, 
                    "Data kelas sudah ada!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("❌ Error saat menambah kelas: " + e.getMessage());
                JOptionPane.showMessageDialog(null, 
                    "Error saat menambah kelas: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    /**
     * Read all class records
     * @return List of all Kelas objects
     */
    public List<Kelas> getAllKelas() {
        List<Kelas> kelasList = new ArrayList<>();
        String sql = "SELECT * FROM kelas ORDER BY kelas, angkatan";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Kelas kelas = new Kelas();
                kelas.setKelas(rs.getString("kelas"));
                kelas.setAngkatan(rs.getString("angkatan"));
                kelas.setNominalSPP(rs.getDouble("nominal_spp"));
                
                kelasList.add(kelas);
            }
            System.out.println("✅ Berhasil load " + kelasList.size() + " kelas");

        } catch (SQLException e) {
            System.err.println("❌ Error saat mengambil data kelas: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error saat mengambil data kelas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        return kelasList;
    }

    /**
     * Get a specific class record
     * @param kelasName The class name
     * @param angkatan The year
     * @return The Kelas object or null if not found
     */
    public Kelas getKelas(String kelasName, String angkatan) {
        String sql = "SELECT * FROM kelas WHERE kelas = ? AND angkatan = ?";
        Kelas kelas = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, kelasName);
            pstmt.setString(2, angkatan);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                kelas = new Kelas();
                kelas.setKelas(rs.getString("kelas"));
                kelas.setAngkatan(rs.getString("angkatan"));
                kelas.setNominalSPP(rs.getDouble("nominal_spp"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saat mengambil data kelas: " + e.getMessage());
        }

        return kelas;
    }

    /**
     * Update a class record (original method - only updates nominal_spp)
     * @param kelas The updated class object
     * @return true if successful, false otherwise
     */
    public boolean updateKelas(Kelas kelas) {
        String sql = "UPDATE kelas SET nominal_spp = ? WHERE kelas = ? AND angkatan = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, kelas.getNominalSPP());
            pstmt.setString(2, kelas.getKelas());
            pstmt.setString(3, kelas.getAngkatan());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Kelas berhasil diperbarui: " + kelas.getKelas());
                JOptionPane.showMessageDialog(null, 
                    "Kelas berhasil diperbarui!", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Kelas tidak ditemukan!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saat update kelas: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error saat update kelas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    /**
     * 🔥 NEW METHOD - Update kelas with original key (supports changing primary key fields)
     * This method allows updating kelas, angkatan, and nominal_spp all at once
     * 
     * @param originalKelas Original class name (for WHERE clause)
     * @param originalAngkatan Original year (for WHERE clause)
     * @param newKelas New class name
     * @param newAngkatan New year
     * @param newNominal New SPP amount
     * @return true if successful, false otherwise
     */
    public boolean updateKelas(String originalKelas, String originalAngkatan, 
                              String newKelas, String newAngkatan, double newNominal) {
        
        // Check if new class already exists (only if primary key changed)
        if (!originalKelas.equals(newKelas) || !originalAngkatan.equals(newAngkatan)) {
            if (isKelasExists(newKelas, newAngkatan)) {
                JOptionPane.showMessageDialog(null, 
                    "Kelas " + newKelas + " untuk angkatan " + newAngkatan + " sudah ada!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        String sql = "UPDATE kelas SET kelas = ?, angkatan = ?, nominal_spp = ? " +
                     "WHERE kelas = ? AND angkatan = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            // SET clause (new values)
            pstmt.setString(1, newKelas);
            pstmt.setString(2, newAngkatan);
            pstmt.setDouble(3, newNominal);
            
            // WHERE clause (original values)
            pstmt.setString(4, originalKelas);
            pstmt.setString(5, originalAngkatan);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Kelas berhasil diupdate:");
                System.out.println("   Dari: " + originalKelas + " / " + originalAngkatan);
                System.out.println("   Jadi: " + newKelas + " / " + newAngkatan + " / Rp " + newNominal);
                
                // Don't show JOptionPane here - let FormKelas handle it
                // JOptionPane.showMessageDialog(null, 
                //     "Kelas berhasil diperbarui!", 
                //     "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                System.err.println("⚠️ Tidak ada data yang diupdate. Kelas tidak ditemukan.");
                JOptionPane.showMessageDialog(null, 
                    "Kelas tidak ditemukan!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating kelas: " + e.getMessage());
            e.printStackTrace();
            
            // Handle specific SQL errors
            if (e.getErrorCode() == 1062) { // Duplicate entry
                JOptionPane.showMessageDialog(null, 
                    "Data kelas sudah ada!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else if (e.getErrorCode() == 1451) { // Foreign key constraint
                JOptionPane.showMessageDialog(null, 
                    "Tidak bisa mengubah kelas karena masih ada data siswa terkait!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Error saat update kelas: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    /**
     * Delete a class record
     * @param kelasName The class name
     * @param angkatan The year
     * @return true if successful, false otherwise
     */
    public boolean deleteKelas(String kelasName, String angkatan) {
        // Check if there are students in this class before deletion
        if (hasStudentsInKelas(kelasName, angkatan)) {
            JOptionPane.showMessageDialog(null, 
                "Tidak bisa menghapus kelas karena masih ada siswa terdaftar!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "DELETE FROM kelas WHERE kelas = ? AND angkatan = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, kelasName);
            pstmt.setString(2, angkatan);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Kelas berhasil dihapus: " + kelasName);
                JOptionPane.showMessageDialog(null, 
                    "Kelas berhasil dihapus!", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Kelas tidak ditemukan!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saat delete kelas: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error saat delete kelas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    /**
     * Check if a class already exists
     * @param kelasName The class name
     * @param angkatan The year
     * @return true if exists, false otherwise
     */
    public boolean isKelasExists(String kelasName, String angkatan) {
        String sql = "SELECT COUNT(*) FROM kelas WHERE kelas = ? AND angkatan = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, kelasName);
            pstmt.setString(2, angkatan);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saat cek kelas: " + e.getMessage());
        }

        return false;
    }

    /**
     * Check if there are students in a class
     * @param kelasName The class name
     * @param angkatan The year
     * @return true if students exist, false otherwise
     */
    private boolean hasStudentsInKelas(String kelasName, String angkatan) {
        // This would need to check the siswa table for students in the class
        // For now, let's check by class name - in a real implementation, 
        // you might need to check the structure of your siswa table
        // This assumes the siswa table has a 'kelas' column that matches the class name
        String sql = "SELECT COUNT(*) FROM siswa WHERE kelas = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, kelasName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saat cek student dalam kelas: " + e.getMessage());
        }

        return false;
    }
}