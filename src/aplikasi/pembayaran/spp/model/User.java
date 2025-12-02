package aplikasi.pembayaran.spp.model;

/**
 * User Model - Simple version
 * Sesuai struktur project yang udah ada
 */
public class User {
    private String username;
    private String password;
    public String role;
    private String namaLengkap;
    private String noTelepon;
    private boolean active;

    // Constructor kosong
    public User() {}

    // Constructor dengan parameter lengkap
    public User(String username, String password, String role, String namaLengkap, String noTelepon, boolean active) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.namaLengkap = namaLengkap;
        this.noTelepon = noTelepon;
        this.active = active;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getNoTelepon() { return noTelepon; }
    public boolean isActive() { return active; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Validasi role - UPDATED untuk support "Admin"
     */
    public boolean isValidRole() {
        if (role == null || role.trim().isEmpty()) {
            System.out.println("❌ Role is null or empty");
            return false;
        }
        
        // Normalisasi role ke lowercase untuk comparison
        String normalizedRole = role.trim().toLowerCase();
        
        System.out.println("🔍 Validating role: '" + role + "' (normalized: '" + normalizedRole + "')");
        
        switch (normalizedRole) {
            case "kepsek":
            case "admin":
            case "tu":
            case "siswa":
                System.out.println("✅ Role valid: " + role);
                return true;
            default:
                System.out.println("❌ Role tidak valid: " + role);
                return false;
        }
    }
    
    @Override
    public String toString() {
        return namaLengkap + " (" + role + ") - " + (active ? "Aktif" : "Nonaktif");
    }
}