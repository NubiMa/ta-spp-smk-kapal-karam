package aplikasi.pembayaran.spp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model Tagihan - Handle data tagihan dan tunggakan SPP
 * Simple version dengan fitur essential
 */
public class Tagihan {
    
    // Atribut untuk data tagihan
    private String nisSiswa;
    private String namaSiswa;
    private String kelas;
    private String bulanTahun;
    private double nominalSPP;
    private double potongan;
    private double jumlahBayar;
    private double sisaTagihan;
    private String statusPembayaran;
    private LocalDateTime tanggalBayar;
    private LocalDateTime tanggalJatuhTempo;
    private String keterangan;
    
    /**
     * Constructor kosong
     */
    public Tagihan() {
        this.potongan = 0.0;
        this.jumlahBayar = 0.0;
        this.sisaTagihan = 0.0;
        this.statusPembayaran = "Belum Lunas";
    }
    
    /**
     * Constructor dengan parameter utama
     */
    public Tagihan(String nisSiswa, String namaSiswa, String bulanTahun, double nominalSPP) {
        this();
        this.nisSiswa = nisSiswa;
        this.namaSiswa = namaSiswa;
        this.bulanTahun = bulanTahun;
        this.nominalSPP = nominalSPP;
        this.sisaTagihan = nominalSPP; // Default sisa tagihan = nominal SPP
    }
    
    /**
     * Constructor lengkap
     */
    public Tagihan(String nisSiswa, String namaSiswa, String kelas, String bulanTahun, 
                   double nominalSPP, double potongan, double jumlahBayar) {
        this.nisSiswa = nisSiswa;
        this.namaSiswa = namaSiswa;
        this.kelas = kelas;
        this.bulanTahun = bulanTahun;
        this.nominalSPP = nominalSPP;
        this.potongan = potongan;
        this.jumlahBayar = jumlahBayar;
        this.sisaTagihan = hitungSisaTagihan();
        this.statusPembayaran = determineStatus();
    }
    
    // Getters
    public String getNisSiswa() {
        return nisSiswa;
    }
    
    public String getNamaSiswa() {
        return namaSiswa;
    }
    
    public String getKelas() {
        return kelas;
    }
    
    public String getBulanTahun() {
        return bulanTahun;
    }
    
    public double getNominalSPP() {
        return nominalSPP;
    }
    
    public double getPotongan() {
        return potongan;
    }
    
    public double getJumlahBayar() {
        return jumlahBayar;
    }
    
    public double getSisaTagihan() {
        return sisaTagihan;
    }
    
    public String getStatusPembayaran() {
        return statusPembayaran;
    }
    
    public LocalDateTime getTanggalBayar() {
        return tanggalBayar;
    }
    
    public LocalDateTime getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }
    
    public String getKeterangan() {
        return keterangan;
    }
    
    // Setters
    public void setNisSiswa(String nisSiswa) {
        this.nisSiswa = nisSiswa;
    }
    
    public void setNamaSiswa(String namaSiswa) {
        this.namaSiswa = namaSiswa;
    }
    
    public void setKelas(String kelas) {
        this.kelas = kelas;
    }
    
    public void setBulanTahun(String bulanTahun) {
        this.bulanTahun = bulanTahun;
    }
    
    public void setNominalSPP(double nominalSPP) {
        this.nominalSPP = nominalSPP;
        this.sisaTagihan = hitungSisaTagihan(); // Auto recalculate
    }
    
    public void setPotongan(double potongan) {
        this.potongan = 0; // Potongan is always 0 since feature is removed
        this.sisaTagihan = hitungSisaTagihan(); // Auto recalculate
    }
    
    public void setJumlahBayar(double jumlahBayar) {
        this.jumlahBayar = Math.max(0, jumlahBayar); // Pastikan tidak negatif
        this.sisaTagihan = hitungSisaTagihan(); // Auto recalculate
        this.statusPembayaran = determineStatus(); // Auto update status
    }
    
    public void setSisaTagihan(double sisaTagihan) {
        this.sisaTagihan = Math.max(0, sisaTagihan); // Pastikan tidak negatif
    }
    
    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }
    
    public void setTanggalBayar(LocalDateTime tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }
    
    public void setTanggalJatuhTempo(LocalDateTime tanggalJatuhTempo) {
        this.tanggalJatuhTempo = tanggalJatuhTempo;
    }
    
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    /**
     * Method untuk menghitung sisa tagihan
     * @return sisa tagihan yang harus dibayar
     */
    public double hitungSisaTagihan() {
        double totalHarusBayar = nominalSPP; // Potongan is always 0 now
        double sisa = totalHarusBayar - jumlahBayar;
        return Math.max(0, sisa); // Pastikan tidak negatif
    }
    
    /**
     * Method untuk menghitung jumlah yang harus dibayar (potongan is now always 0)
     * @return jumlah yang harus dibayar
     */
    public double getJumlahHarusBayar() {
        return Math.max(0, nominalSPP); // Potongan is always 0 now
    }
    
    /**
     * Method untuk menentukan status pembayaran berdasarkan jumlah bayar
     * @return status pembayaran
     */
    private String determineStatus() {
        double harusBayar = getJumlahHarusBayar();
        
        if (jumlahBayar <= 0) {
            return "Belum Lunas";
        } else if (jumlahBayar >= harusBayar) {
            return "Lunas";
        } else {
            return "Cicilan";
        }
    }
    
    /**
     * Method untuk cek apakah tagihan sudah lunas
     * @return true jika lunas, false jika belum
     */
    public boolean isLunas() {
        return "Lunas".equals(statusPembayaran) || sisaTagihan <= 0;
    }
    
    /**
     * Method untuk cek apakah ada tunggakan
     * @return true jika ada tunggakan, false jika tidak
     */
    public boolean hasTunggakan() {
        return sisaTagihan > 0;
    }
    
    /**
     * Method untuk menghitung persentase pembayaran
     * @return persentase pembayaran (0-100)
     */
    public double getPersentasePembayaran() {
        double harusBayar = getJumlahHarusBayar();
        if (harusBayar <= 0) {
            return 100.0;
        }
        return Math.min(100.0, (jumlahBayar / harusBayar) * 100);
    }
    
    /**
     * Method untuk format tanggal bayar ke string
     * @return tanggal bayar dalam format dd/MM/yyyy HH:mm
     */
    public String getTanggalBayarFormatted() {
        if (tanggalBayar == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return tanggalBayar.format(formatter);
    }
    
    /**
     * Method untuk format tanggal jatuh tempo ke string
     * @return tanggal jatuh tempo dalam format dd/MM/yyyy
     */
    public String getTanggalJatuhTempoFormatted() {
        if (tanggalJatuhTempo == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return tanggalJatuhTempo.format(formatter);
    }
    
    /**
     * Method untuk mendapat info ringkas tagihan
     * @return string info tagihan
     */
    public String getInfoTagihan() {
        return String.format("%s - %s (%s): Rp %.0f / Rp %.0f - %s",
            nisSiswa, namaSiswa, bulanTahun, 
            jumlahBayar, getJumlahHarusBayar(), statusPembayaran);
    }
    
    /**
     * Method untuk mendapat status dengan icon
     * @return status dengan emoji icon
     */
    public String getStatusWithIcon() {
        switch (statusPembayaran.toLowerCase()) {
            case "lunas":
                return "✅ Lunas";
            case "cicilan":
                return "⏳ Cicilan";
            case "belum lunas":
                return "❌ Belum Lunas";
            case "tunggakan":
                return "⚠️ Tunggakan";
            default:
                return "❓ " + statusPembayaran;
        }
    }
    
    /**
     * Method untuk validasi data tagihan
     * @return true jika data valid, false jika tidak
     */
    public boolean isValidTagihan() {
        return nisSiswa != null && !nisSiswa.trim().isEmpty() &&
               namaSiswa != null && !namaSiswa.trim().isEmpty() &&
               bulanTahun != null && !bulanTahun.trim().isEmpty() &&
               nominalSPP > 0 &&
               potongan == 0 && // Potongan is always 0 since feature is removed
               jumlahBayar >= 0;
    }
    
    /**
     * Method untuk copy tagihan (untuk keperluan edit/update)
     * @return copy dari tagihan ini
     */
    public Tagihan copy() {
        Tagihan copy = new Tagihan();
        copy.setNisSiswa(this.nisSiswa);
        copy.setNamaSiswa(this.namaSiswa);
        copy.setKelas(this.kelas);
        copy.setBulanTahun(this.bulanTahun);
        copy.setNominalSPP(this.nominalSPP);
        copy.setPotongan(this.potongan);
        copy.setJumlahBayar(this.jumlahBayar);
        copy.setStatusPembayaran(this.statusPembayaran);
        copy.setTanggalBayar(this.tanggalBayar);
        copy.setTanggalJatuhTempo(this.tanggalJatuhTempo);
        copy.setKeterangan(this.keterangan);
        return copy;
    }
    
    @Override
    public String toString() {
        return "Tagihan{" +
                "nisSiswa='" + nisSiswa + '\'' +
                ", namaSiswa='" + namaSiswa + '\'' +
                ", bulanTahun='" + bulanTahun + '\'' +
                ", nominalSPP=" + nominalSPP +
                ", potongan=" + potongan +
                ", jumlahBayar=" + jumlahBayar +
                ", sisaTagihan=" + sisaTagihan +
                ", statusPembayaran='" + statusPembayaran + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Tagihan tagihan = (Tagihan) obj;
        return nisSiswa != null ? nisSiswa.equals(tagihan.nisSiswa) : tagihan.nisSiswa == null &&
               bulanTahun != null ? bulanTahun.equals(tagihan.bulanTahun) : tagihan.bulanTahun == null;
    }
    
    @Override
    public int hashCode() {
        int result = nisSiswa != null ? nisSiswa.hashCode() : 0;
        result = 31 * result + (bulanTahun != null ? bulanTahun.hashCode() : 0);
        return result;
    }
}