    package aplikasi.pembayaran.spp.model;

import java.time.LocalDateTime;

public class Pembayaran {
    private String idTransaksi;
    private String nisSiswa;
    private String namaSiswa;
    private String bulanTahun;
    private double nominalSPP;
    private double potongan;
    private double jumlahBayar;
    private LocalDateTime tanggalBayar;
    private String metodePembayaran;
    private String statusPembayaran;
    private String keterangan;
    private String userInput;

    // === GETTER & SETTER ===
    public String getIdTransaksi() {
        return idTransaksi;
    }
    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getNisSiswa() {
        return nisSiswa;
    }
    public void setNisSiswa(String nisSiswa) {
        this.nisSiswa = nisSiswa;
    }

    public String getNamaSiswa() {
        return namaSiswa;
    }
    public void setNamaSiswa(String namaSiswa) {
        this.namaSiswa = namaSiswa;
    }

    public String getBulanTahun() {
        return bulanTahun;
    }
    public void setBulanTahun(String bulanTahun) {
        this.bulanTahun = bulanTahun;
    }

    public double getNominalSPP() {
        return nominalSPP;
    }
    public void setNominalSPP(double nominalSPP) {
        this.nominalSPP = nominalSPP;
    }

    public double getPotongan() {
        return potongan;
    }
    public void setPotongan(double potongan) {
        this.potongan = potongan;
    }

    public double getJumlahBayar() {
        return jumlahBayar;
    }
    public void setJumlahBayar(double jumlahBayar) {
        this.jumlahBayar = jumlahBayar;
    }

    public LocalDateTime getTanggalBayar() {
        return tanggalBayar;
    }
    public void setTanggalBayar(LocalDateTime tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }
    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }
    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public String getKeterangan() {
        return keterangan;
    }
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getUserInput() {
        return userInput;
    }
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}
