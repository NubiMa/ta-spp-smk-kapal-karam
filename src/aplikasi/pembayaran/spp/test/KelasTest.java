package aplikasi.pembayaran.spp.test;

import aplikasi.pembayaran.spp.controller.KelasController;
import aplikasi.pembayaran.spp.model.Kelas;
import aplikasi.pembayaran.spp.model.Koneksi;

import java.util.List;

/**
 * Simple test class to verify Kelas CRUD functionality
 */
public class KelasTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Kelas CRUD Functionality ===");
        
        // Test database connection
        if (Koneksi.testConnection()) {
            System.out.println("✅ Database connection successful");
        } else {
            System.out.println("❌ Database connection failed");
            return;
        }
        
        KelasController controller = new KelasController();
        
        // Test 1: Create a new class
        System.out.println("\n--- Test 1: Create Kelas ---");
        Kelas newKelas = new Kelas("XII RPL", "2024/2025", 175000.00);
        boolean createResult = controller.createKelas(newKelas);
        System.out.println("Create result: " + createResult);
        
        // Test 2: Read all classes
        System.out.println("\n--- Test 2: Read All Kelas ---");
        List<Kelas> kelasList = controller.getAllKelas();
        System.out.println("Total kelas found: " + kelasList.size());
        for (Kelas k : kelasList) {
            System.out.println("Kelas: " + k.getKelas() + 
                             ", Angkatan: " + k.getAngkatan() + 
                             ", Nominal SPP: " + k.getNominalSPP());
        }
        
        // Test 3: Update a class (if any exists)
        System.out.println("\n--- Test 3: Update Kelas ---");
        if (!kelasList.isEmpty()) {
            Kelas toUpdate = kelasList.get(0);
            System.out.println("Original: " + toUpdate);
            
            // Get the original values to update
            Kelas updateKelas = new Kelas(toUpdate.getKelas(), toUpdate.getAngkatan(), toUpdate.getNominalSPP() + 5000);
            boolean updateResult = controller.updateKelas(updateKelas);
            System.out.println("Update result: " + updateResult);
        } else {
            System.out.println("No existing kelas to update");
        }
        
        System.out.println("\n=== Kelas CRUD Test Completed ===");
    }
}