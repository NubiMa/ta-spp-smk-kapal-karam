package aplikasi.pembayaran.spp;

import aplikasi.pembayaran.spp.model.Koneksi;
import aplikasi.pembayaran.spp.view.LoginPage;
import javax.swing.*;

/**
 * AplikasiPembayaranSPP - Main Class
 * Entry point aplikasi yang sesuai dengan nama project lo
 */
public class AplikasiPembayaranSPP {
    
    public static void main(String[] args) {
        System.out.println("=== SPP PAYMENT SYSTEM ===");
        System.out.println("Starting application...");
        
        // Set Look and Feel
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            System.out.println("✅ UI Theme loaded");
        } catch (Exception e) {
            System.out.println("⚠️ Using default theme");
        }
        
        // Test database connection
        System.out.println("Testing database connection...");
        if (Koneksi.getConnection() != null) {
            System.out.println("✅ Database connected successfully");

            // Setup database tables if needed
            Koneksi.setupDatabase();
        } else {
            System.out.println("❌ Database connection failed");
            JOptionPane.showMessageDialog(null,
                "Database connection failed!\n" +
                "Please check:\n" +
                "1. MySQL server is running\n" +
                "2. Database 'db_spp' exists\n" +
                "3. Username/password is correct",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }

        // Launch application
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
            System.out.println("✅ Application launched");
        });
    }
}