package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.SiswaController;
import aplikasi.pembayaran.spp.controller.KelasController;
import aplikasi.pembayaran.spp.model.Siswa;
import aplikasi.pembayaran.spp.model.Kelas;
import aplikasi.pembayaran.spp.view.NumericValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * FormTambahSiswa - Form for adding new student data
 * Features: Create new student with class selection from database
 */
public class FormTambahSiswa extends JDialog {
    // Components
    private JTextField txtNIS, txtNama, txtTahunAjaran, txtNominalSPP;
    private JComboBox<String> cmbKelas, cmbStatus;
    private JButton btnSimpan, btnReset, btnBatal;
    
    // Controllers
    private SiswaController siswaController;
    private KelasController kelasController;

    // Data
    private FormDataSiswa parentForm;
    private String currentRole;

    public FormTambahSiswa(FormDataSiswa parent, String role) {
        super(parent, "Tambah Siswa Baru", true);
        this.parentForm = parent;
        this.currentRole = role;
        this.siswaController = new SiswaController();
        this.kelasController = new KelasController();

        initComponents();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Form Tambah Siswa Baru");
        setSize(600, 500);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // ===== PANEL HEADER =====
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(52, 152, 219));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("➕ TAMBAH SISWA BARU");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel("Lengkapi data siswa sesuai format yang benar");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setForeground(new Color(236, 240, 241));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblInfo);

        panelHeader.add(titlePanel, BorderLayout.CENTER);

        // ===== PANEL FORM =====
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0 - NIS
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("NIS: *"), gbc);

        txtNIS = new JTextField();
        NumericValidator.makeNumericOnly(txtNIS); // Add numeric validation for NIS
        gbc.gridx = 1; gbc.gridy = 0;
        panelForm.add(txtNIS, gbc);

        // Row 1 - Nama Lengkap
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Nama Lengkap: *"), gbc);

        txtNama = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        panelForm.add(txtNama, gbc);

        // Row 2 - Kelas (Dropdown)
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Kelas: *"), gbc);

        cmbKelas = new JComboBox<>();
        // Load kelas options from database
        loadKelasOptions();
        cmbKelas.addActionListener(e -> {
            // When kelas is selected, auto-update tahun ajaran and nominal SPP
            updateTahunAjaranFromKelas();
        });
        gbc.gridx = 1; gbc.gridy = 2;
        panelForm.add(cmbKelas, gbc);

        // Row 3 - Tahun Ajaran (Auto-filled from selected kelas)
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Tahun Ajaran: *"), gbc);

        txtTahunAjaran = new JTextField();
        txtTahunAjaran.setEditable(false);
        txtTahunAjaran.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; gbc.gridy = 3;
        panelForm.add(txtTahunAjaran, gbc);

        // Row 4 - Nominal SPP (Auto-filled from selected kelas)
        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Nominal SPP: *"), gbc);

        txtNominalSPP = new JTextField();
        txtNominalSPP.setEditable(false);
        txtNominalSPP.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; gbc.gridy = 4;
        panelForm.add(txtNominalSPP, gbc);

        // Row 5 - Status Siswa
        gbc.gridx = 0; gbc.gridy = 5;
        panelForm.add(new JLabel("Status Siswa: *"), gbc);

        String[] statusOptions = {"Aktif", "Lulus", "Pindah", "Drop Out"};
        cmbStatus = new JComboBox<>(statusOptions);
        cmbStatus.setSelectedItem("Aktif"); // Default to Aktif
        gbc.gridx = 1; gbc.gridy = 5;
        panelForm.add(cmbStatus, gbc);
        
        // Info note
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel lblNote = new JLabel("* Field wajib diisi");
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNote.setForeground(new Color(192, 57, 43));
        panelForm.add(lblNote, gbc);

        // ===== PANEL BUTTON =====
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelButton.setBackground(new Color(236, 240, 241));

        btnSimpan = new JButton("💾 SIMPAN");
        btnSimpan.setPreferredSize(new Dimension(130, 40));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(e -> simpanData());

        btnReset = new JButton("🔄 RESET");
        btnReset.setPreferredSize(new Dimension(130, 40));
        btnReset.setBackground(new Color(241, 196, 15));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> resetForm());

        btnBatal = new JButton("❌ BATAL");
        btnBatal.setPreferredSize(new Dimension(130, 40));
        btnBatal.setBackground(new Color(231, 76, 60));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dispose());

        panelButton.add(btnSimpan);
        panelButton.add(btnReset);
        panelButton.add(btnBatal);

        // Add panels to dialog
        add(panelHeader, BorderLayout.NORTH);
        add(panelForm, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }
    
    // Method to load kelas options from database
    private void loadKelasOptions() {
        cmbKelas.removeAllItems();
        List<Kelas> kelasList = kelasController.getAllKelas();
        for (Kelas k : kelasList) {
            cmbKelas.addItem(k.getKelas() + " - " + k.getAngkatan());
        }
    }

    // Method to update tahun ajaran and nominal SPP when a kelas is selected
    private void updateTahunAjaranFromKelas() {
        String selectedItem = (String) cmbKelas.getSelectedItem();
        if (selectedItem != null) {
            // Extract kelas name from the selected item (format: "Kelas - Angkatan")
            String kelasName = selectedItem.split(" - ")[0];
            
            // Find the corresponding class in the database
            List<Kelas> kelasList = kelasController.getAllKelas();
            for (Kelas k : kelasList) {
                if (k.getKelas().equals(kelasName)) {
                    txtTahunAjaran.setText(k.getAngkatan());
                    txtNominalSPP.setText(String.valueOf(k.getNominalSPP()));
                    break;
                }
            }
        }
    }

    // Method untuk validasi input
    private boolean validateInput() {
        // Cek NIS
        if (txtNIS.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIS tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtNIS.requestFocus();
            return false;
        }

        // Cek Nama
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocus();
            return false;
        }

        // Cek Kelas - check if something is selected in the combo box
        if (cmbKelas.getSelectedItem() == null || cmbKelas.getSelectedItem().toString().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kelas tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            cmbKelas.requestFocus();
            return false;
        }

        // Cek Tahun Ajaran
        if (txtTahunAjaran.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tahun ajaran tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtTahunAjaran.requestFocus();
            return false;
        }

        // Cek Nominal SPP
        if (txtNominalSPP.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nominal SPP tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtNominalSPP.requestFocus();
            return false;
        }

        try {
            double nominal = Double.parseDouble(txtNominalSPP.getText().trim());
            if (nominal <= 0) {
                JOptionPane.showMessageDialog(this, "Nominal SPP harus lebih dari 0!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                txtNominalSPP.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal SPP harus berupa angka!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtNominalSPP.requestFocus();
            return false;
        }

        return true;
    }

    // Method untuk simpan data
    private void simpanData() {
        // Validasi input
        if (!validateInput()) {
            return;
        }

        // Extract kelas name from the selected item (format: "Kelas - Angkatan")
        String selectedItem = (String) cmbKelas.getSelectedItem();
        String kelasName = "";
        if (selectedItem != null) {
            kelasName = selectedItem.split(" - ")[0];
        }

        try {
            // Buat object Siswa
            Siswa siswa = new Siswa();
            siswa.setNis(txtNIS.getText().trim());
            siswa.setNamaLengkap(txtNama.getText().trim());
            siswa.setKelas(kelasName); // Use the selected kelas name
            siswa.setTahunAjaran(txtTahunAjaran.getText().trim());
            // Set no telepon, alamat, and nama ortu to empty strings since they're removed from UI
            siswa.setNoTelepon("");
            siswa.setAlamat("");
            siswa.setNamaOrtu("");
            siswa.setNominalSPP(Double.parseDouble(txtNominalSPP.getText().trim()));
            siswa.setTotalPotongan(0); // Total potongan is always 0 since feature is removed
            siswa.setStatusSiswa((String) cmbStatus.getSelectedItem());

            // Tambah siswa baru
            boolean sukses = siswaController.tambahSiswa(siswa);

            if (sukses) {
                JOptionPane.showMessageDialog(this,
                        "Siswa baru berhasil ditambahkan!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Close dialog and trigger refresh in parent
                dispose();
                SwingUtilities.invokeLater(() -> {
                    if (parentForm != null) {
                        parentForm.refreshTable();
                    }
                });
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Format nominal SPP tidak valid!\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk reset form
    private void resetForm() {
        txtNIS.setText("");
        txtNama.setText("");
        txtTahunAjaran.setText("");
        txtNominalSPP.setText("");
        cmbStatus.setSelectedItem("Aktif");
        cmbKelas.setSelectedIndex(-1);
        txtNIS.requestFocus();
    }
}