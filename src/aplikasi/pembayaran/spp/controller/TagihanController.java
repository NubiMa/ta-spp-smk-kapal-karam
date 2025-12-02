package aplikasi.pembayaran.spp.controller;

import aplikasi.pembayaran.spp.model.Koneksi;
import aplikasi.pembayaran.spp.model.Tagihan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * TagihanController - Handle tagihan dan tunggakan SPP
 * Simple version untuk kebutuhan essential
 */
public class TagihanController {
    
    private Connection conn;
    
    /**
     * Constructor
     */
    public TagihanController() {
        this.conn = Koneksi.getConnection();
    }
    
    /**
     * Get tagihan siswa berdasarkan NIS
     * @param nis - NIS siswa
     * @return List tagihan siswa
     */
    public List<Tagihan> getTagihanSiswa(String nis) {
        List<Tagihan> tagihanList = new ArrayList<>();
        
        // Query untuk get data siswa dan pembayarannya
        String sql = "SELECT " +
                "s.nis, " +
                "s.nama_lengkap, " +
                "s.kelas, " +
                "s.nominal_spp, " +
                "0 as total_potongan, " +
                "COALESCE(p.bulan_tahun, 'Belum Bayar') as bulan_tahun, " +
                "COALESCE(p.jumlah_bayar, 0) as jumlah_bayar, " +
                "COALESCE(p.status_pembayaran, 'Belum Lunas') as status_pembayaran, " +
                "COALESCE(p.tanggal_bayar, null) as tanggal_bayar " +
                "FROM siswa s " +
                "LEFT JOIN pembayaran p ON s.nis = p.nis_siswa " +
                "WHERE s.nis = ? " +
                "ORDER BY p.tanggal_bayar DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nis);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Tagihan tagihan = new Tagihan();
                tagihan.setNisSiswa(rs.getString("nis"));
                tagihan.setNamaSiswa(rs.getString("nama_lengkap"));
                tagihan.setKelas(rs.getString("kelas"));
                tagihan.setBulanTahun(rs.getString("bulan_tahun"));
                tagihan.setNominalSPP(rs.getDouble("nominal_spp"));
                double potongan = rs.getDouble("total_potongan");
                tagihan.setPotongan(0); // Potongan feature removed, always 0
                tagihan.setJumlahBayar(rs.getDouble("jumlah_bayar"));
                tagihan.setStatusPembayaran(rs.getString("status_pembayaran"));

                // Hitung sisa tagihan - potongan is now always 0
                double sisaTagihan = tagihan.getNominalSPP() - tagihan.getJumlahBayar();
                tagihan.setSisaTagihan(Math.max(0, sisaTagihan));
                
                if (rs.getTimestamp("tanggal_bayar") != null) {
                    tagihan.setTanggalBayar(rs.getTimestamp("tanggal_bayar").toLocalDateTime());
                }
                
                tagihanList.add(tagihan);
            }
            
            System.out.println("✅ Loaded " + tagihanList.size() + " tagihan for NIS: " + nis);
            
        } catch (SQLException e) {
            System.out.println("❌ Error get tagihan siswa: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error mengambil tagihan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return tagihanList;
    }
    
    /**
     * Get semua siswa yang memiliki tunggakan
     * @param currentUserRole - Role user untuk permission check
     * @return List siswa dengan tunggakan
     */
    public List<Tagihan> getAllTunggakan(String currentUserRole) {
        List<Tagihan> tunggakanList = new ArrayList<>();
        
        // Permission check
        if (!hasReadPermission(currentUserRole)) {
            JOptionPane.showMessageDialog(null, "Anda tidak memiliki akses untuk melihat data tunggakan!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return tunggakanList;
        }
        
        String sql = "SELECT " +
                "s.nis, " +
                "s.nama_lengkap, " +
                "s.kelas, " +
                "s.nominal_spp, " +
                "0 as total_potongan, " +
                "s.nominal_spp as harus_bayar, " +
                "COALESCE(SUM(p.jumlah_bayar), 0) as total_bayar, " +
                "(s.nominal_spp - COALESCE(SUM(p.jumlah_bayar), 0)) as sisa_tunggakan, " +
                "COUNT(p.id_transaksi) as jumlah_transaksi " +
                "FROM siswa s " +
                "LEFT JOIN pembayaran p ON s.nis = p.nis_siswa AND p.status_pembayaran IN ('Lunas', 'Cicilan') " +
                "WHERE s.status_siswa = 'Aktif' " +
                "GROUP BY s.nis, s.nama_lengkap, s.kelas, s.nominal_spp " +
                "HAVING sisa_tunggakan > 0 " +
                "ORDER BY sisa_tunggakan DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Tagihan tagihan = new Tagihan();
                tagihan.setNisSiswa(rs.getString("nis"));
                tagihan.setNamaSiswa(rs.getString("nama_lengkap"));
                tagihan.setKelas(rs.getString("kelas"));
                tagihan.setNominalSPP(rs.getDouble("nominal_spp"));
                tagihan.setPotongan(0); // Potongan feature removed, always 0
                tagihan.setJumlahBayar(rs.getDouble("total_bayar"));
                tagihan.setSisaTagihan(rs.getDouble("sisa_tunggakan"));
                tagihan.setBulanTahun("Tunggakan"); // Mark sebagai tunggakan
                tagihan.setStatusPembayaran("Belum Lunas");
                
                tunggakanList.add(tagihan);
            }
            
            System.out.println("✅ Found " + tunggakanList.size() + " siswa dengan tunggakan");
            
        } catch (SQLException e) {
            System.out.println("❌ Error get tunggakan: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error mengambil data tunggakan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return tunggakanList;
    }
    
    /**
     * Generate tagihan bulanan untuk semua siswa aktif
     */
    public boolean generateTagihanBulanan(String bulanTahun, String currentUserRole) {
        if (!hasGeneratePermission(currentUserRole)) {
            JOptionPane.showMessageDialog(null, "Anda tidak memiliki akses untuk generate tagihan!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Cek apakah tagihan bulan tersebut sudah ada
        if (isTagihanExists(bulanTahun)) {
            JOptionPane.showMessageDialog(null, "Tagihan untuk " + bulanTahun + " sudah pernah di-generate!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        String sql = "INSERT INTO tagihan_bulanan (nis_siswa, nama_siswa, kelas, bulan_tahun, nominal_spp, potongan, " +
                "jumlah_tagihan, status_tagihan, created_at) " +
                "SELECT nis, nama_lengkap, kelas, ?, nominal_spp, total_potongan, " +
                "(nominal_spp - total_potongan) as jumlah_tagihan, 'Belum Lunas', NOW() " +
                "FROM siswa WHERE status_siswa = 'Aktif'";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bulanTahun);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                System.out.println("✅ Generated " + result + " tagihan for " + bulanTahun);
                JOptionPane.showMessageDialog(null, 
                    "Berhasil generate " + result + " tagihan untuk " + bulanTahun, 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error generate tagihan: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error generate tagihan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Get ringkasan tagihan untuk dashboard
     */
    public String[] getRingkasanTagihan() {
        String[] ringkasan = new String[4];
        
        try {
            // Total siswa aktif
            String sql1 = "SELECT COUNT(*) FROM siswa WHERE status_siswa = 'Aktif'";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            ResultSet rs1 = pstmt1.executeQuery();
            rs1.next();
            ringkasan[0] = String.valueOf(rs1.getInt(1));
            
            // Siswa sudah bayar bulan ini
            String sql2 = "SELECT COUNT(DISTINCT nis_siswa) FROM pembayaran " +
                    "WHERE YEAR(tanggal_bayar) = YEAR(CURDATE()) " +
                    "AND MONTH(tanggal_bayar) = MONTH(CURDATE()) " +
                    "AND status_pembayaran = 'Lunas'";
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();
            rs2.next();
            ringkasan[1] = String.valueOf(rs2.getInt(1));
            
            // Siswa belum bayar
            int totalSiswa = Integer.parseInt(ringkasan[0]);
            int sudahBayar = Integer.parseInt(ringkasan[1]);
            ringkasan[2] = String.valueOf(totalSiswa - sudahBayar);
            
            // Persentase pembayaran
            if (totalSiswa > 0) {
                double persentase = (double) sudahBayar / totalSiswa * 100;
                ringkasan[3] = String.format("%.1f%%", persentase);
            } else {
                ringkasan[3] = "0%";
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error get ringkasan tagihan: " + e.getMessage());
            ringkasan[0] = "0"; ringkasan[1] = "0"; ringkasan[2] = "0"; ringkasan[3] = "0%";
        }
        
        return ringkasan;
    }
    
    /**
     * Cek apakah tagihan untuk bulan tertentu sudah ada
     */
    private boolean isTagihanExists(String bulanTahun) {
        String sql = "SELECT COUNT(*) FROM pembayaran WHERE bulan_tahun = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bulanTahun);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error check tagihan exists: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get siswa dengan tagihan terbanyak (untuk laporan)
     */
    public List<Tagihan> getSiswaTagihanTerbanyak(int limit) {
        List<Tagihan> topTunggakan = new ArrayList<>();
        
        String sql = "SELECT s.nis, s.nama_lengkap, s.kelas, " +
                "COUNT(p.id_transaksi) as jumlah_tagihan, " +
                "SUM(s.nominal_spp) as total_harus_bayar, " +
                "COALESCE(SUM(p.jumlah_bayar), 0) as total_sudah_bayar, " +
                "(SUM(s.nominal_spp) - COALESCE(SUM(p.jumlah_bayar), 0)) as total_tunggakan " +
                "FROM siswa s " +
                "LEFT JOIN pembayaran p ON s.nis = p.nis_siswa " +
                "WHERE s.status_siswa = 'Aktif' " +
                "GROUP BY s.nis, s.nama_lengkap, s.kelas " +
                "HAVING total_tunggakan > 0 " +
                "ORDER BY total_tunggakan DESC " +
                "LIMIT ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Tagihan tagihan = new Tagihan();
                tagihan.setNisSiswa(rs.getString("nis"));
                tagihan.setNamaSiswa(rs.getString("nama_lengkap"));
                tagihan.setKelas(rs.getString("kelas"));
                tagihan.setSisaTagihan(rs.getDouble("total_tunggakan"));
                tagihan.setStatusPembayaran("Tunggakan Besar");
                
                topTunggakan.add(tagihan);
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error get top tunggakan: " + e.getMessage());
        }
        
        return topTunggakan;
    }
    
    /**
     * Check read permission
     */
    private boolean hasReadPermission(String role) {
        return role.equals("Kepsek") || role.equals("Bendahara") || role.equals("TU") || role.equals("Siswa");
    }
    
    /**
     * Check generate permission
     */
    private boolean hasGeneratePermission(String role) {
        return role.equals("Bendahara") || role.equals("TU");
    }
}
