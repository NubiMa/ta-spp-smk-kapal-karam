package aplikasi.pembayaran.spp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Model Siswa
 * Sesuai field yang dipakai di SiswaController
 */
public class Siswa {
    private String nis;
    private String namaLengkap;
    private String kelas;
    private String tahunAjaran;
    private String noTelepon;
    private String alamat;
    private double nominalSPP;
    private double totalPotongan;
    private String statusSiswa;
    private String namaOrtu;

    // Constructor kosong
    public Siswa() {}

    // Constructor lengkap
    public Siswa(String nis, String namaLengkap, String kelas, String tahunAjaran,
                 String noTelepon, String alamat, double nominalSPP, 
                 double totalPotongan, String statusSiswa, String namaOrtu) {
        this.nis = nis;
        this.namaLengkap = namaLengkap;
        this.kelas = kelas;
        this.tahunAjaran = tahunAjaran;
        this.noTelepon = noTelepon;
        this.alamat = alamat;
        this.nominalSPP = nominalSPP;
        this.totalPotongan = totalPotongan;
        this.statusSiswa = statusSiswa;
        this.namaOrtu = namaOrtu;
    }

    // Getters
    public String getNis() { return nis; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getKelas() { return kelas; }
    public String getTahunAjaran() { return tahunAjaran; }
    public String getNoTelepon() { return noTelepon; }
    public String getAlamat() { return alamat; }
    public double getNominalSPP() { return nominalSPP; }
    public double getTotalPotongan() { return totalPotongan; }
    public String getStatusSiswa() { return statusSiswa; }
    public String getNamaOrtu() { return namaOrtu; }

    // Setters
    public void setNis(String nis) { this.nis = nis; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public void setKelas(String kelas) { this.kelas = kelas; }
    public void setTahunAjaran(String tahunAjaran) { this.tahunAjaran = tahunAjaran; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon != null ? noTelepon : ""; }
    public void setAlamat(String alamat) { this.alamat = alamat != null ? alamat : ""; }
    public void setNominalSPP(double nominalSPP) { this.nominalSPP = nominalSPP; }
    public void setTotalPotongan(double totalPotongan) { this.totalPotongan = 0; } // Total potongan is always 0 since feature is removed
    public void setStatusSiswa(String statusSiswa) { this.statusSiswa = statusSiswa; }
    public void setNamaOrtu(String namaOrtu) { this.namaOrtu = namaOrtu != null ? namaOrtu : ""; }

    
        /**
     * Cek apakah role punya akses create
     */
    private boolean hasCreatePermission(String role) {
        return role.equals("TU") || role.equals("Bendahara");
    }

    /**
     * Cek apakah role punya akses update
     */
    private boolean hasUpdatePermission(String role) {
        return role.equals("TU") || role.equals("Bendahara");
    }

    /**
     * Cek apakah role punya akses read
     */
    private boolean hasReadPermission(String role) {
        return role.equals("TU") || role.equals("Bendahara") || role.equals("Kepsek");
    }

    /**
     * Validasi data siswa sebelum insert/update
     */
    private boolean validateSiswaData(Siswa siswa) {
        if (siswa.getNis() == null || siswa.getNis().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "NIS tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (siswa.getNamaLengkap() == null || siswa.getNamaLengkap().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama siswa tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (siswa.getKelas() == null || siswa.getKelas().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kelas tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (siswa.getNominalSPP() <= 0) {
            JOptionPane.showMessageDialog(null, "Nominal SPP harus lebih dari 0!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Helper untuk convert ResultSet -> Object Siswa
     */
    private Siswa createSiswaFromResultSet(ResultSet rs) throws SQLException {
        Siswa siswa = new Siswa();
        siswa.setNis(rs.getString("nis"));
        siswa.setNamaLengkap(rs.getString("nama_lengkap"));
        siswa.setKelas(rs.getString("kelas"));
        siswa.setTahunAjaran(rs.getString("tahun_ajaran"));
        siswa.setNoTelepon(rs.getString("no_telepon"));
        siswa.setAlamat(rs.getString("alamat"));
        siswa.setNominalSPP(rs.getDouble("nominal_spp"));
        siswa.setTotalPotongan(rs.getDouble("total_potongan"));
        siswa.setStatusSiswa(rs.getString("status_siswa"));
        siswa.setNamaOrtu(rs.getString("nama_ortu"));
        return siswa;
    }

    
    @Override
    public String toString() {
        return namaLengkap + " (" + nis + ") - " + kelas + " [" + statusSiswa + "]";
    }
}
