package aplikasi.pembayaran.spp.model;

/**
 * Kelas Model - Represents a class with class name, year, and SPP amount
 * Fields: id (auto-generated), kelas (class name), angkatan (year), nominalSPP (SPP amount)
 */
public class Kelas {
    private int id;
    private String kelas;
    private String angkatan;
    private double nominalSPP;

    // Default constructor
    public Kelas() {}

    // Constructor with parameters (without ID - for new records)
    public Kelas(String kelas, String angkatan, double nominalSPP) {
        this.kelas = kelas;
        this.angkatan = angkatan;
        this.nominalSPP = nominalSPP;
    }

    // Constructor with parameters (with ID - for existing records)
    public Kelas(int id, String kelas, String angkatan, double nominalSPP) {
        this.id = id;
        this.kelas = kelas;
        this.angkatan = angkatan;
        this.nominalSPP = nominalSPP;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getKelas() {
        return kelas;
    }

    public String getAngkatan() {
        return angkatan;
    }

    public double getNominalSPP() {
        return nominalSPP;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public void setAngkatan(String angkatan) {
        this.angkatan = angkatan;
    }

    public void setNominalSPP(double nominalSPP) {
        this.nominalSPP = nominalSPP;
    }

    @Override
    public String toString() {
        return kelas + " (" + angkatan + ") - Rp " + String.format("%.0f", nominalSPP);
    }
}