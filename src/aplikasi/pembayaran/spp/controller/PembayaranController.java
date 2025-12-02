package aplikasi.pembayaran.spp.controller;

import aplikasi.pembayaran.spp.model.Koneksi;
import aplikasi.pembayaran.spp.model.Pembayaran;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 * PembayaranController - Handle transaksi pembayaran SPP
 * Versi fixed: Bug pada PreparedStatement telah diperbaiki
 */
public class PembayaranController {

    private Connection conn;

    public PembayaranController() {
        this.conn = Koneksi.getConnection();
    }

    /**
     * Input pembayaran baru
     * BUG FIX: Parameter index 6 (potongan) yang di-comment menyebabkan index shift
     */
    public boolean inputPembayaran(Pembayaran pembayaran, String currentUserRole) {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Koneksi database belum tersedia.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Permission check - hanya TU dan Bendahara yang bisa input
        if (!hasInputPermission(currentUserRole)) {
            JOptionPane.showMessageDialog(null, "Anda tidak memiliki akses untuk input pembayaran!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validasi data pembayaran
        if (!validatePembayaranData(pembayaran)) return false;

        // Generate ID transaksi otomatis jika belum ada
        if (pembayaran.getIdTransaksi() == null || pembayaran.getIdTransaksi().trim().isEmpty()) {
            pembayaran.setIdTransaksi(generateIdTransaksi());
        }

        // Jika tanggalBayar null -> set sekarang
        if (pembayaran.getTanggalBayar() == null) {
            pembayaran.setTanggalBayar(LocalDateTime.now());
        }

        // FIX: Query SQL disesuaikan dengan struktur tabel database (tanpa kolom potongan)
        String sql = "INSERT INTO pembayaran (id_transaksi, nis_siswa, nama_siswa, bulan_tahun, nominal_spp, "
           + "jumlah_bayar, tanggal_bayar, metode_pembayaran, status_pembayaran, keterangan, user_input) "
           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pembayaran.getIdTransaksi());
            pstmt.setString(2, pembayaran.getNisSiswa());
            pstmt.setString(3, pembayaran.getNamaSiswa());
            pstmt.setString(4, pembayaran.getBulanTahun());
            pstmt.setDouble(5, pembayaran.getNominalSPP());
            // INDEX 6 DIHAPUS - potongan sudah tidak digunakan
            pstmt.setDouble(6, pembayaran.getJumlahBayar());  // Sebelumnya index 7
            pstmt.setTimestamp(7, Timestamp.valueOf(pembayaran.getTanggalBayar())); // Sebelumnya index 8
            pstmt.setString(8, pembayaran.getMetodePembayaran()); // Sebelumnya index 9
            pstmt.setString(9, pembayaran.getStatusPembayaran()); // Sebelumnya index 10
            pstmt.setString(10, pembayaran.getKeterangan()); // Sebelumnya index 11
            pstmt.setString(11, pembayaran.getUserInput()); // Sebelumnya index 12

            int result = pstmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Pembayaran berhasil diinput: " + pembayaran.getIdTransaksi());
                JOptionPane.showMessageDialog(null,
                    "Pembayaran berhasil diinput!\nID Transaksi: " + pembayaran.getIdTransaksi(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate key (MySQL)
                JOptionPane.showMessageDialog(null, "ID Transaksi sudah ada! Gunakan ID yang berbeda.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.err.println("❌ Error input pembayaran: " + e.getMessage());
            e.printStackTrace(); // Tambahkan untuk debugging
        }

        return false;
    }

    /**
     * Get semua pembayaran
     */
    public List<Pembayaran> getAllPembayaran(String currentUserRole) {
        List<Pembayaran> pembayaranList = new ArrayList<>();
        if (conn == null) return pembayaranList;

        if (!hasReadPermission(currentUserRole)) {
            JOptionPane.showMessageDialog(null, "Anda tidak memiliki akses untuk melihat data pembayaran!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return pembayaranList;
        }

        String sql = "SELECT * FROM pembayaran ORDER BY tanggal_bayar DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                pembayaranList.add(createPembayaranFromResultSet(rs));
            }
            System.out.println("✅ Loaded " + pembayaranList.size() + " pembayaran records");

        } catch (SQLException e) {
            System.err.println("❌ Error get all pembayaran: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error mengambil data pembayaran: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return pembayaranList;
    }

    /**
     * Get pembayaran berdasarkan NIS siswa
     */
    public List<Pembayaran> getPembayaranByNIS(String nis) {
        List<Pembayaran> pembayaranList = new ArrayList<>();
        if (conn == null) return pembayaranList;

        String sql = "SELECT * FROM pembayaran WHERE nis_siswa = ? ORDER BY tanggal_bayar DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nis);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pembayaranList.add(createPembayaranFromResultSet(rs));
                }
            }
            System.out.println("✅ Found " + pembayaranList.size() + " pembayaran for NIS: " + nis);

        } catch (SQLException e) {
            System.err.println("❌ Error get pembayaran by NIS: " + e.getMessage());
        }

        return pembayaranList;
    }

    /**
     * Get pembayaran berdasarkan ID transaksi
     */
    public Pembayaran getPembayaranById(String idTransaksi) {
        if (conn == null) return null;

        String sql = "SELECT * FROM pembayaran WHERE id_transaksi = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idTransaksi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return createPembayaranFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error get pembayaran by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Update status pembayaran
     */
    public boolean updateStatusPembayaran(String idTransaksi, String statusBaru, String currentUserRole) {
        if (conn == null) return false;

        if (!hasUpdatePermission(currentUserRole)) {
            JOptionPane.showMessageDialog(null, "Anda tidak memiliki akses untuk mengupdate status pembayaran!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE pembayaran SET status_pembayaran = ? WHERE id_transaksi = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, statusBaru);
            pstmt.setString(2, idTransaksi);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Status pembayaran diupdate: " + idTransaksi + " -> " + statusBaru);
                JOptionPane.showMessageDialog(null, "Status pembayaran berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Transaksi dengan ID tersebut tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error update status: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error update status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    /**
     * Generate ID transaksi otomatis (lebih unik dengan detik + millis)
     */
    public String generateIdTransaksi() {
        LocalDateTime now = LocalDateTime.now();
        int ms = now.getNano() / 1_000_000;
        return String.format("TRX%04d%02d%02d%02d%02d%02d%03d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond(), ms);
    }

    /**
     * Create Pembayaran object dari ResultSet (null-safe timestamp)
     */
    private Pembayaran createPembayaranFromResultSet(ResultSet rs) throws SQLException {
        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setIdTransaksi(rs.getString("id_transaksi"));
        pembayaran.setNisSiswa(rs.getString("nis_siswa"));
        pembayaran.setNamaSiswa(rs.getString("nama_siswa"));
        pembayaran.setBulanTahun(rs.getString("bulan_tahun"));
        pembayaran.setNominalSPP(rs.getDouble("nominal_spp"));
        pembayaran.setPotongan(0); // Always 0 since feature is removed
        pembayaran.setJumlahBayar(rs.getDouble("jumlah_bayar"));

        Timestamp ts = rs.getTimestamp("tanggal_bayar");
        if (ts != null) {
            pembayaran.setTanggalBayar(ts.toLocalDateTime());
        } else {
            pembayaran.setTanggalBayar(LocalDateTime.now());
        }

        pembayaran.setMetodePembayaran(rs.getString("metode_pembayaran"));
        pembayaran.setStatusPembayaran(rs.getString("status_pembayaran"));
        pembayaran.setKeterangan(rs.getString("keterangan"));
        pembayaran.setUserInput(rs.getString("user_input"));
        return pembayaran;
    }

    /**
     * Validasi data pembayaran
     */
    private boolean validatePembayaranData(Pembayaran pembayaran) {
        if (pembayaran == null) {
            JOptionPane.showMessageDialog(null, "Data pembayaran kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (pembayaran.getNisSiswa() == null || pembayaran.getNisSiswa().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "NIS siswa tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (pembayaran.getNamaSiswa() == null || pembayaran.getNamaSiswa().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama siswa tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (pembayaran.getBulanTahun() == null || pembayaran.getBulanTahun().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bulan/tahun tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (pembayaran.getNominalSPP() <= 0) {
            JOptionPane.showMessageDialog(null, "Nominal SPP harus lebih dari 0!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (pembayaran.getJumlahBayar() <= 0) {
            JOptionPane.showMessageDialog(null, "Jumlah bayar harus lebih dari 0!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Check input permission
     */
    private boolean hasInputPermission(String role) {
        return role != null && (role.equals("TU") || role.equals("Bendahara") || role.equals("Admin"));
    }

    /**
     * Check read permission
     */
    private boolean hasReadPermission(String role) {
        return role != null && (role.equals("Kepsek") || role.equals("Bendahara") || role.equals("TU") || role.equals("Siswa") || role.equals("Admin"));
    }

    /**
     * Check update permission
     */
    private boolean hasUpdatePermission(String role) {
        return role != null && (role.equals("Bendahara") || role.equals("TU") || role.equals("Admin"));
    }

    /**
     * Get statistik pembayaran untuk dashboard (safe, using try-with-resources)
     */
    public String[] getStatistikPembayaran() {
        String[] stats = new String[4];
        if (conn == null) {
            stats[0] = "0"; stats[1] = "Rp 0"; stats[2] = "0"; stats[3] = "0";
            return stats;
        }

        try {
            // Total transaksi hari ini
            String sqlHariIni = "SELECT COUNT(*) FROM pembayaran WHERE DATE(tanggal_bayar) = CURDATE()";
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlHariIni);
                 ResultSet rs1 = pstmt1.executeQuery()) {
                rs1.next();
                stats[0] = String.valueOf(rs1.getInt(1));
            }

            // Total pemasukan hari ini
            String sqlPemasukan = "SELECT COALESCE(SUM(jumlah_bayar), 0) FROM pembayaran WHERE DATE(tanggal_bayar) = CURDATE()";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlPemasukan);
                 ResultSet rs2 = pstmt2.executeQuery()) {
                rs2.next();
                stats[1] = String.format("Rp %.0f", rs2.getDouble(1));
            }

            // Total transaksi bulan ini
            String sqlBulanIni = "SELECT COUNT(*) FROM pembayaran WHERE YEAR(tanggal_bayar) = YEAR(CURDATE()) AND MONTH(tanggal_bayar) = MONTH(CURDATE())";
            try (PreparedStatement pstmt3 = conn.prepareStatement(sqlBulanIni);
                 ResultSet rs3 = pstmt3.executeQuery()) {
                rs3.next();
                stats[2] = String.valueOf(rs3.getInt(1));
            }

            // Total siswa dengan tunggakan (using the new accurate method)
            stats[3] = String.valueOf(getJumlahSiswaTunggakan());

        } catch (SQLException e) {
            System.err.println("❌ Error get statistik: " + e.getMessage());
            stats[0] = "0"; stats[1] = "Rp 0"; stats[2] = "0"; stats[3] = "0";
        }

        return stats;
    }

    /**
     * Get jumlah siswa dengan tunggakan untuk dashboard
     * This method uses the same logic as FormMonitorTunggakan
     */
    public int getJumlahSiswaTunggakan() {
        // Get all active students
        String sqlSiswa = "SELECT nis, nominal_spp FROM siswa WHERE status_siswa = 'Aktif'";

        // Create a map of active students
        Map<String, Double> activeSiswaMap = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sqlSiswa);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String nis = rs.getString("nis");
                double nominalSPP = rs.getDouble("nominal_spp");
                activeSiswaMap.put(nis, nominalSPP);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting active students for tunggakan calculation: " + e.getMessage());
            return 0;  // Return 0 if there's an error
        }

        // Current year and month
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();  // 1-12

        int totalSiswaWithTunggakan = 0;

        for (String nis : activeSiswaMap.keySet()) {
            // Get all payment records for this student in the current year
            String sqlPembayaran = "SELECT * FROM pembayaran WHERE nis_siswa = ? AND bulan_tahun LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPembayaran)) {
                pstmt.setString(1, nis);
                pstmt.setString(2, "% " + currentYear);  // Format: "X bulan (Year)" or "Januari, Februari 2024"

                try (ResultSet rs = pstmt.executeQuery()) {
                    // Get the paid months from all payment records for this student
                    Set<String> bulanLunas = new HashSet<>();
                    while (rs.next()) {
                        String keterangan = rs.getString("keterangan");
                        if (keterangan != null && keterangan.contains("Pembayaran untuk bulan:")) {
                            // Extract months from keterangan: "Pembayaran untuk bulan: Januari, Februari 2024 | Jumlah per bulan: ..."
                            int bulanStart = keterangan.indexOf("Pembayaran untuk bulan: ") + "Pembayaran untuk bulan: ".length();
                            int separatorIndex = keterangan.indexOf(" | Jumlah per bulan:");
                            if (separatorIndex == -1) separatorIndex = keterangan.length(); // If no separator, go to end

                            String bulanDalamKeterangan = keterangan.substring(bulanStart, separatorIndex).trim();

                            // Check that the year matches
                            if (bulanDalamKeterangan.contains(" " + currentYear)) {
                                // Extract just the month names by removing the year part
                                String monthsOnly = bulanDalamKeterangan.substring(0, bulanDalamKeterangan.lastIndexOf(" " + currentYear)).trim();

                                String[] months = monthsOnly.split(", ");
                                for(String month : months) {
                                    bulanLunas.add(month.trim());
                                }
                            }
                        }
                    }

                    // Check if student has payments for all months that should be paid (Jan to current month)
                    boolean hasTunggakan = false;
                    String[] BULAN_ARRAY = {
                        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                    };

                    for (int month = 1; month <= currentMonth; month++) {
                        String bulanName = BULAN_ARRAY[month - 1];
                        if (!bulanLunas.contains(bulanName)) {
                            hasTunggakan = true;
                            break;
                        }
                    }

                    if (hasTunggakan) {
                        totalSiswaWithTunggakan++;
                    }
                }
            } catch (SQLException e) {
                System.err.println("❌ Error checking payments for NIS " + nis + ": " + e.getMessage());
                continue;  // Continue with other students
            }
        }

        return totalSiswaWithTunggakan;
    }
}