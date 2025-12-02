package aplikasi.pembayaran.spp.controller;

import aplikasi.pembayaran.spp.model.Siswa;
import aplikasi.pembayaran.spp.model.Koneksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SiswaController {
    private Connection conn;

    public SiswaController() {
        conn = Koneksi.getConnection();
    }

    // ✅ Ambil semua siswa
    public List<Siswa> getAllSiswa() {
        List<Siswa> list = new ArrayList<>();
        String sql = "SELECT * FROM siswa";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Siswa siswa = new Siswa(
                        rs.getString("nis"),
                        rs.getString("nama_lengkap"),
                        rs.getString("kelas"),
                        rs.getString("tahun_ajaran"),
                        rs.getString("no_telepon"),
                        rs.getString("alamat"),
                        rs.getDouble("nominal_spp"),
                        0, // total_potongan is always 0 since feature is removed
                        rs.getString("status_siswa"),
                        rs.getString("nama_ortu")
                );
                list.add(siswa);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Cari siswa by NIS
    public Siswa getSiswaByNis(String nis) {
        String sql = "SELECT * FROM siswa WHERE nis = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nis);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Siswa(
                        rs.getString("nis"),
                        rs.getString("nama_lengkap"),
                        rs.getString("kelas"),
                        rs.getString("tahun_ajaran"),
                        rs.getString("no_telepon"),
                        rs.getString("alamat"),
                        rs.getDouble("nominal_spp"),
                        0, // total_potongan is always 0 since feature is removed
                        rs.getString("status_siswa"),
                        rs.getString("nama_ortu")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Tambah siswa
    public boolean tambahSiswa(Siswa s) {
        String sql = "INSERT INTO siswa (nis, nama_lengkap, kelas, tahun_ajaran, no_telepon, alamat, nominal_spp, total_potongan, status_siswa, nama_ortu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNis());
            stmt.setString(2, s.getNamaLengkap());
            stmt.setString(3, s.getKelas());
            stmt.setString(4, s.getTahunAjaran());
            stmt.setString(5, s.getNoTelepon());
            stmt.setString(6, s.getAlamat());
            stmt.setDouble(7, s.getNominalSPP());
            stmt.setDouble(8, 0); // total_potongan is always 0 since feature is removed
            stmt.setString(9, s.getStatusSiswa());
            stmt.setString(10, s.getNamaOrtu());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Update siswa
    public boolean updateSiswa(Siswa s) {
        String sql = "UPDATE siswa SET nama_lengkap=?, kelas=?, tahun_ajaran=?, no_telepon=?, alamat=?, nominal_spp=?, total_potongan=0, status_siswa=?, nama_ortu=? WHERE nis=?"; // total_potongan is set to 0 since feature is removed
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNamaLengkap());
            stmt.setString(2, s.getKelas());
            stmt.setString(3, s.getTahunAjaran());
            stmt.setString(4, s.getNoTelepon());
            stmt.setString(5, s.getAlamat());
            stmt.setDouble(6, s.getNominalSPP());
            stmt.setString(7, s.getStatusSiswa());
            stmt.setString(8, s.getNamaOrtu());
            stmt.setString(9, s.getNis());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Hapus siswa
    public boolean hapusSiswa(String nis) {
        String sql = "DELETE FROM siswa WHERE nis=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nis);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get unique kelas from database
    public List<String> getUniqueKelas() {
        List<String> uniqueKelas = new ArrayList<>();
        String sql = "SELECT DISTINCT kelas FROM siswa ORDER BY kelas";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String kelas = rs.getString("kelas");
                if (kelas != null && !kelas.trim().isEmpty()) {
                    uniqueKelas.add(kelas.trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return uniqueKelas;
    }
}
