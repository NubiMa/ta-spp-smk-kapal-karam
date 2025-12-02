package aplikasi.pembayaran.spp.controller;

import aplikasi.pembayaran.spp.model.Koneksi;
import java.sql.*;
import java.util.*;

public class LaporanController {
    
    // Get laporan berdasarkan periode (bulan-tahun)
    public Map<String, Object> getLaporanByPeriode(String bulanTahun) {
        Map<String, Object> laporan = new HashMap<>();
        String query = "SELECT " +
                      "COUNT(*) as total_transaksi, " +
                      "SUM(jumlah_bayar) as total_pemasukan, " +
                      "AVG(jumlah_bayar) as rata_rata, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Cash' THEN jumlah_bayar ELSE 0 END) as total_cash, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Transfer' THEN jumlah_bayar ELSE 0 END) as total_transfer, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Kartu Debit' THEN jumlah_bayar ELSE 0 END) as total_kartu, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Cash' THEN 1 END) as jumlah_cash, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Transfer' THEN 1 END) as jumlah_transfer, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Kartu Debit' THEN 1 END) as jumlah_kartu, " +
                      "COUNT(CASE WHEN status_pembayaran = 'Lunas' THEN 1 END) as jumlah_lunas " +
                      "FROM pembayaran " +
                      "WHERE bulan_tahun = ?";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, bulanTahun);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                laporan.put("total_transaksi", rs.getInt("total_transaksi"));
                laporan.put("total_pemasukan", rs.getDouble("total_pemasukan"));
                laporan.put("rata_rata", rs.getDouble("rata_rata"));
                laporan.put("total_cash", rs.getDouble("total_cash"));
                laporan.put("total_transfer", rs.getDouble("total_transfer"));
                laporan.put("total_kartu", rs.getDouble("total_kartu"));
                laporan.put("jumlah_cash", rs.getInt("jumlah_cash"));
                laporan.put("jumlah_transfer", rs.getInt("jumlah_transfer"));
                laporan.put("jumlah_kartu", rs.getInt("jumlah_kartu"));
                laporan.put("jumlah_lunas", rs.getInt("jumlah_lunas"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting laporan: " + e.getMessage());
        }
        
        return laporan;
    }
    
    // Get laporan berdasarkan range tanggal
    public Map<String, Object> getLaporanByDateRange(java.util.Date startDate, java.util.Date endDate) {
        Map<String, Object> laporan = new HashMap<>();
        String query = "SELECT " +
                      "COUNT(*) as total_transaksi, " +
                      "SUM(jumlah_bayar) as total_pemasukan, " +
                      "AVG(jumlah_bayar) as rata_rata, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Cash' THEN jumlah_bayar ELSE 0 END) as total_cash, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Transfer' THEN jumlah_bayar ELSE 0 END) as total_transfer, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Kartu Debit' THEN jumlah_bayar ELSE 0 END) as total_kartu, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Cash' THEN 1 END) as jumlah_cash, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Transfer' THEN 1 END) as jumlah_transfer, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Kartu Debit' THEN 1 END) as jumlah_kartu, " +
                      "COUNT(CASE WHEN status_pembayaran = 'Lunas' THEN 1 END) as jumlah_lunas " +
                      "FROM pembayaran " +
                      "WHERE DATE(tanggal_bayar) BETWEEN ? AND ?";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                laporan.put("total_transaksi", rs.getInt("total_transaksi"));
                laporan.put("total_pemasukan", rs.getDouble("total_pemasukan"));
                laporan.put("rata_rata", rs.getDouble("rata_rata"));
                laporan.put("total_cash", rs.getDouble("total_cash"));
                laporan.put("total_transfer", rs.getDouble("total_transfer"));
                laporan.put("total_kartu", rs.getDouble("total_kartu"));
                laporan.put("jumlah_cash", rs.getInt("jumlah_cash"));
                laporan.put("jumlah_transfer", rs.getInt("jumlah_transfer"));
                laporan.put("jumlah_kartu", rs.getInt("jumlah_kartu"));
                laporan.put("jumlah_lunas", rs.getInt("jumlah_lunas"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting laporan: " + e.getMessage());
        }
        
        return laporan;
    }
    
    // Get detail transaksi untuk periode tertentu
    public List<Map<String, Object>> getDetailTransaksi(String bulanTahun) {
        List<Map<String, Object>> transaksiList = new ArrayList<>();
        String query = "SELECT id_transaksi, nis_siswa, nama_siswa, " +
                      "tanggal_bayar, jumlah_bayar, metode_pembayaran, " +
                      "status_pembayaran, keterangan " +
                      "FROM pembayaran " +
                      "WHERE bulan_tahun = ? " +
                      "ORDER BY tanggal_bayar DESC";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, bulanTahun);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> transaksi = new HashMap<>();
                transaksi.put("id_transaksi", rs.getString("id_transaksi"));
                transaksi.put("nis_siswa", rs.getString("nis_siswa"));
                transaksi.put("nama_siswa", rs.getString("nama_siswa"));
                transaksi.put("tanggal_bayar", rs.getTimestamp("tanggal_bayar"));
                transaksi.put("jumlah_bayar", rs.getDouble("jumlah_bayar"));
                transaksi.put("metode_pembayaran", rs.getString("metode_pembayaran"));
                transaksi.put("status_pembayaran", rs.getString("status_pembayaran"));
                transaksi.put("keterangan", rs.getString("keterangan"));
                transaksiList.add(transaksi);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting detail transaksi: " + e.getMessage());
        }
        
        return transaksiList;
    }
    
    // Get detail transaksi berdasarkan range tanggal
    public List<Map<String, Object>> getDetailTransaksiByDateRange(java.util.Date startDate, java.util.Date endDate) {
        List<Map<String, Object>> transaksiList = new ArrayList<>();
        String query = "SELECT id_transaksi, nis_siswa, nama_siswa, bulan_tahun, " +
                      "tanggal_bayar, jumlah_bayar, metode_pembayaran, " +
                      "status_pembayaran, keterangan " +
                      "FROM pembayaran " +
                      "WHERE DATE(tanggal_bayar) BETWEEN ? AND ? " +
                      "ORDER BY tanggal_bayar DESC";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> transaksi = new HashMap<>();
                transaksi.put("id_transaksi", rs.getString("id_transaksi"));
                transaksi.put("nis_siswa", rs.getString("nis_siswa"));
                transaksi.put("nama_siswa", rs.getString("nama_siswa"));
                transaksi.put("bulan_tahun", rs.getString("bulan_tahun"));
                transaksi.put("tanggal_bayar", rs.getTimestamp("tanggal_bayar"));
                transaksi.put("jumlah_bayar", rs.getDouble("jumlah_bayar"));
                transaksi.put("metode_pembayaran", rs.getString("metode_pembayaran"));
                transaksi.put("status_pembayaran", rs.getString("status_pembayaran"));
                transaksi.put("keterangan", rs.getString("keterangan"));
                transaksiList.add(transaksi);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting detail transaksi: " + e.getMessage());
        }
        
        return transaksiList;
    }
    
    // Get list bulan-tahun yang tersedia untuk laporan
    public List<String> getAvailablePeriods() {
        List<String> periods = new ArrayList<>();
        String query = "SELECT DISTINCT bulan_tahun FROM pembayaran ORDER BY bulan_tahun DESC";
        
        try (Connection conn = Koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                periods.add(rs.getString("bulan_tahun"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting periods: " + e.getMessage());
        }
        
        return periods;
    }
}