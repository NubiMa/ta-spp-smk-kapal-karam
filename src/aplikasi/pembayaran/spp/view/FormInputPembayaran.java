package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.PembayaranController;
import aplikasi.pembayaran.spp.controller.SiswaController;
import aplikasi.pembayaran.spp.model.Pembayaran;
import aplikasi.pembayaran.spp.model.Siswa;
import aplikasi.pembayaran.spp.view.NumericValidator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormInputPembayaran extends JFrame {
    
    // Components
    private JTextField txtNIS, txtNamaSiswa, txtKelas, txtNominalSPP, txtJumlahBayar, txtIdTransaksi;
    private JLabel lblTotalBayar;
    private JComboBox<String> cmbTahun, cmbMetodePembayaran;
    private JTextArea txtKeterangan;
    private JButton btnCariSiswa, btnHitung, btnSimpan, btnReset, btnKeluar;
    private JLabel lblTanggal, lblUser, lblSisaBayar;
    
    // Checkbox untuk bulan
    private Map<String, JCheckBox> bulanCheckBoxes;
    private JPanel bulanPanel;
    
    // Controllers
    private SiswaController siswaController;
    private PembayaranController pembayaranController;
    
    // Data
    private String currentUser;
    private String currentRole;
    private Siswa currentSiswa;
    private JFrame parentFrame;
    
    private static final String[] BULAN_ARRAY = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };
    
    public FormInputPembayaran(String username, String role) {
        this(username, role, null);
    }

    public FormInputPembayaran(String username, String role, JFrame parentFrame) {
        this.currentUser = username;
        this.currentRole = role;
        this.parentFrame = parentFrame;
        this.siswaController = new SiswaController();
        this.pembayaranController = new PembayaranController();
        this.bulanCheckBoxes = new HashMap<>();

        initComponents();
        setLocationRelativeTo(parentFrame);
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("Form Input Pembayaran SPP");
        setSize(950, 750);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // ===== PANEL HEADER =====
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(41, 128, 185));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("INPUT PEMBAYARAN SPP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        
        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        panelInfo.setOpaque(false);
        lblTanggal = new JLabel("Tanggal: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblTanggal.setForeground(Color.WHITE);
        lblUser = new JLabel("User: " + currentUser + " (" + currentRole + ")");
        lblUser.setForeground(Color.WHITE);
        panelInfo.add(lblTanggal);
        panelInfo.add(lblUser);
        
        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(panelInfo, BorderLayout.EAST);
        
        // ===== PANEL FORM =====
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0 - ID Transaksi
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("ID Transaksi:"), gbc);
        
        txtIdTransaksi = new JTextField(pembayaranController.generateIdTransaksi());
        txtIdTransaksi.setEditable(false);
        txtIdTransaksi.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        panelForm.add(txtIdTransaksi, gbc);
        gbc.gridwidth = 1;
        
        // Row 1 - NIS + Button Cari
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("NIS Siswa: *"), gbc);
        
        txtNIS = new JTextField();
        txtNIS.setPreferredSize(new Dimension(300, 30)); // Increase size
        gbc.gridx = 1; gbc.gridy = 1;
        panelForm.add(txtNIS, gbc);
        
        btnCariSiswa = new JButton("🔍 Cari");
        btnCariSiswa.setBackground(new Color(52, 152, 219));
        btnCariSiswa.setForeground(Color.WHITE);
        btnCariSiswa.setFocusPainted(false);
        btnCariSiswa.addActionListener(e -> cariSiswa());
        gbc.gridx = 2; gbc.gridy = 1;
        panelForm.add(btnCariSiswa, gbc);
        
        // Row 2 - Nama Siswa
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Nama Siswa:"), gbc);
        
        txtNamaSiswa = new JTextField();
        txtNamaSiswa.setEditable(false);
        txtNamaSiswa.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        panelForm.add(txtNamaSiswa, gbc);
        gbc.gridwidth = 1;
        
        // Row 3 - Kelas
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Kelas:"), gbc);
        
        txtKelas = new JTextField();
        txtKelas.setEditable(false);
        txtKelas.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(txtKelas, gbc);
        gbc.gridwidth = 1;
        
        // Row 4 - Tahun
        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Tahun: *"), gbc);
        
        String[] tahun = {"2024", "2025", "2026"};
        cmbTahun = new JComboBox<>(tahun);
        cmbTahun.setSelectedItem(String.valueOf(LocalDateTime.now().getYear()));
        cmbTahun.addActionListener(e -> updateBulanCheckboxes());
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
        panelForm.add(cmbTahun, gbc);
        gbc.gridwidth = 1;
        
        // Row 5 - Bulan (Checkbox Grid)
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelForm.add(new JLabel("Pilih Bulan: *"), gbc);
        
        bulanPanel = createBulanCheckboxPanel();
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panelForm.add(bulanPanel, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 1;
        
        // Row 6 - Nominal SPP
        gbc.gridx = 0; gbc.gridy = 6;
        panelForm.add(new JLabel("Nominal SPP:"), gbc);

        txtNominalSPP = new JTextField("0");
        txtNominalSPP.setEditable(false);
        txtNominalSPP.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 2;
        panelForm.add(txtNominalSPP, gbc);
        gbc.gridwidth = 1;

        // Row 7 - Total Bayar
        gbc.gridx = 0; gbc.gridy = 7;
        panelForm.add(new JLabel("Total Bayar:"), gbc);

        lblTotalBayar = new JLabel("Rp 0");
        lblTotalBayar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalBayar.setForeground(new Color(52, 152, 219));
        gbc.gridx = 1; gbc.gridy = 7; gbc.gridwidth = 2;
        panelForm.add(lblTotalBayar, gbc);
        gbc.gridwidth = 1;
        
        // Row 8 - Jumlah Bayar
        gbc.gridx = 0; gbc.gridy = 8;
        panelForm.add(new JLabel("Jumlah Bayar: *"), gbc);

        txtJumlahBayar = new JTextField("0");
        txtJumlahBayar.setPreferredSize(new Dimension(300, 30)); // Increase size
        NumericValidator.makeNumericWithDecimalOnly(txtJumlahBayar); // Add numeric validation
        txtJumlahBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateTotalBayar(); // Update total bayar when jumlah bayar changes
                hitungSisaBayar();
            }
        });
        gbc.gridx = 1; gbc.gridy = 8;
        panelForm.add(txtJumlahBayar, gbc);

        btnHitung = new JButton("💰 Hitung");
        btnHitung.setBackground(new Color(46, 204, 113));
        btnHitung.setForeground(Color.WHITE);
        btnHitung.setFocusPainted(false);
        btnHitung.addActionListener(e -> {
            updateTotalBayar();
            hitungSisaBayar();
        });
        gbc.gridx = 2; gbc.gridy = 8;
        panelForm.add(btnHitung, gbc);
        
        // Row 9 - Sisa Bayar
        gbc.gridx = 0; gbc.gridy = 9;
        panelForm.add(new JLabel("Sisa Bayar:"), gbc);

        lblSisaBayar = new JLabel("Rp 0");
        lblSisaBayar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSisaBayar.setForeground(new Color(231, 76, 60));
        gbc.gridx = 1; gbc.gridy = 9; gbc.gridwidth = 2;
        panelForm.add(lblSisaBayar, gbc);
        gbc.gridwidth = 1;

        // Row 10 - Metode Pembayaran
        gbc.gridx = 0; gbc.gridy = 10;
        panelForm.add(new JLabel("Metode Bayar: *"), gbc);

        String[] metode = {"Cash", "Transfer", "Kartu Debit"};
        cmbMetodePembayaran = new JComboBox<>(metode);
        gbc.gridx = 1; gbc.gridy = 10; gbc.gridwidth = 2;
        panelForm.add(cmbMetodePembayaran, gbc);
        gbc.gridwidth = 1;

        // Row 11 - Keterangan
        gbc.gridx = 0; gbc.gridy = 11;
        panelForm.add(new JLabel("Keterangan:"), gbc);

        txtKeterangan = new JTextArea(3, 20);
        txtKeterangan.setLineWrap(true);
        txtKeterangan.setWrapStyleWord(true);
        JScrollPane scrollKeterangan = new JScrollPane(txtKeterangan);
        gbc.gridx = 1; gbc.gridy = 11; gbc.gridwidth = 2;
        panelForm.add(scrollKeterangan, gbc);
        
        // ===== PANEL BUTTON =====
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelButton.setBackground(new Color(236, 240, 241));
        
        btnSimpan = new JButton("💾 SIMPAN");
        btnSimpan.setPreferredSize(new Dimension(130, 40));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(e -> simpanPembayaran());

        btnReset = new JButton("🔄 RESET");
        btnReset.setPreferredSize(new Dimension(130, 40));
        btnReset.setBackground(new Color(241, 196, 15));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> resetForm());

        btnKeluar = new JButton("❌ KELUAR");
        btnKeluar.setPreferredSize(new Dimension(130, 40));
        btnKeluar.setBackground(new Color(231, 76, 60));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKeluar.setFocusPainted(false);
        btnKeluar.addActionListener(e -> dispose());

        panelButton.add(btnSimpan);
        panelButton.add(btnReset);
        panelButton.add(btnKeluar);
        
        // Add panels to frame
        JScrollPane scrollPane = new JScrollPane(panelForm);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(panelHeader, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }
    
    private JPanel createBulanCheckboxPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        for (String bulan : BULAN_ARRAY) {
            JCheckBox checkBox = new JCheckBox(bulan);
            checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            checkBox.setBackground(Color.WHITE);
            checkBox.setEnabled(false); // Disabled until siswa selected
            checkBox.addActionListener(e -> updateTotalBayar()); // Update total when checkbox is selected/deselected
            bulanCheckBoxes.put(bulan, checkBox);
            panel.add(checkBox);
        }
        
        return panel;
    }
    
    private void cariSiswa() {
        String nis = txtNIS.getText().trim();
        
        if (nis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan NIS terlebih dahulu!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentSiswa = siswaController.getSiswaByNis(nis);

        if (currentSiswa != null) {
            txtNamaSiswa.setText(currentSiswa.getNamaLengkap());
            txtKelas.setText(currentSiswa.getKelas());
            txtNominalSPP.setText(String.valueOf(currentSiswa.getNominalSPP()));

            // Enable checkbox dan update status pembayaran
            updateBulanCheckboxes();
            updateTotalBayar(); // Update total bayar after updating checkboxes
            hitungSisaBayar();

            JOptionPane.showMessageDialog(this,
                    "Siswa ditemukan!\n" + currentSiswa.getNamaLengkap() + " - " + currentSiswa.getKelas(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Siswa dengan NIS " + nis + " tidak ditemukan!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            resetFormSiswa();
        }
    }
    
    private void updateBulanCheckboxes() {
        if (currentSiswa == null) {
            // Disable all if no student selected
            for (JCheckBox cb : bulanCheckBoxes.values()) {
                cb.setEnabled(false);
                cb.setSelected(false);
            }
            updateTotalBayar(); // Update total when no student is selected
            return;
        }

        String tahun = (String) cmbTahun.getSelectedItem();

        // Get pembayaran history
        List<Pembayaran> pembayaranList = pembayaranController.getPembayaranByNIS(currentSiswa.getNis());

        // Enable all checkboxes
        for (Map.Entry<String, JCheckBox> entry : bulanCheckBoxes.entrySet()) {
            String bulan = entry.getKey();
            JCheckBox cb = entry.getValue();

            cb.setEnabled(true);
            cb.setSelected(false);
            cb.setForeground(Color.BLACK);

            // Check if already paid - updated to handle combined payment records
            boolean sudahBayar = false;
            for (Pembayaran p : pembayaranList) {
                String keterangan = p.getKeterangan();
                // Check if this specific month is mentioned in the keterangan as paid
                if (keterangan != null && keterangan.contains("Pembayaran untuk bulan:")) {
                    // Extract the months from keterangan - format: "Pembayaran untuk bulan: month1, month2 year | Jumlah per bulan: amount"
                    int bulanStart = keterangan.indexOf("Pembayaran untuk bulan: ") + "Pembayaran untuk bulan: ".length();
                    int separatorIndex = keterangan.indexOf(" | Jumlah per bulan:");
                    if (separatorIndex == -1) separatorIndex = keterangan.length(); // If no separator, go to end

                    String bulanDalamKeterangan = keterangan.substring(bulanStart, separatorIndex).trim();

                    // Check that the year matches
                    if (bulanDalamKeterangan.contains(" " + tahun)) {
                        // Extract just the month names by removing the year part
                        String monthsOnly = bulanDalamKeterangan.substring(0, bulanDalamKeterangan.lastIndexOf(" " + tahun)).trim();

                        String[] months = monthsOnly.split(", ");
                        for(String month : months) {
                            if(month.trim().equals(bulan)) { // Since all payments are "Lunas", just check if month exists
                                sudahBayar = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (sudahBayar) {
                cb.setSelected(true);
                cb.setEnabled(false); // Disable if already paid
                cb.setForeground(new Color(46, 204, 113)); // Green color
                cb.setText(bulan + " ✓");
            } else {
                cb.setText(bulan);
            }
        }

        updateTotalBayar(); // Update total after updating checkboxes
    }
    
    private List<String> getSelectedBulan() {
        List<String> selected = new ArrayList<>();
        for (Map.Entry<String, JCheckBox> entry : bulanCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected() && entry.getValue().isEnabled()) {
                selected.add(entry.getKey());
            }
        }
        return selected;
    }
    
    private void updateTotalBayar() {
        try {
            double nominalSPP = Double.parseDouble(txtNominalSPP.getText());
            List<String> selectedBulan = getSelectedBulan();
            double totalBayar = nominalSPP * selectedBulan.size();

            lblTotalBayar.setText(String.format("Rp %.0f (untuk %d bulan)", totalBayar, selectedBulan.size()));
        } catch (NumberFormatException e) {
            lblTotalBayar.setText("Rp 0 (untuk 0 bulan)");
        }
    }

    private void hitungSisaBayar() {
        try {
            double nominalSPP = Double.parseDouble(txtNominalSPP.getText());
            double jumlahBayar = Double.parseDouble(txtJumlahBayar.getText());

            List<String> selectedBulan = getSelectedBulan();
            double totalHarusBayar = nominalSPP * selectedBulan.size();
            double sisaBayar = totalHarusBayar - jumlahBayar;

            lblSisaBayar.setText(String.format("Rp %.0f (untuk %d bulan)", sisaBayar, selectedBulan.size()));

            // All payments are considered "Lunas" regardless of amount
            lblSisaBayar.setForeground(new Color(46, 204, 113));

        } catch (NumberFormatException e) {
            lblSisaBayar.setText("Rp 0");
        }
    }
    
    private void simpanPembayaran() {
        // Validasi input
        if (currentSiswa == null) {
            JOptionPane.showMessageDialog(this, "Cari siswa terlebih dahulu!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<String> selectedBulan = getSelectedBulan();
        if (selectedBulan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal 1 bulan!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (txtJumlahBayar.getText().trim().isEmpty() || 
            Double.parseDouble(txtJumlahBayar.getText()) <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar harus lebih dari 0!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String tahun = (String) cmbTahun.getSelectedItem();
        String bulanList = String.join(", ", selectedBulan);
        
        // Konfirmasi
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Simpan pembayaran untuk bulan:\n" + bulanList + " " + tahun + "\n\n" +
                "Siswa: " + txtNamaSiswa.getText() + "\n" +
                "Jumlah: Rp " + txtJumlahBayar.getText(),
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean allSuccess = true;

                // Create a single payment record to act as proof of payment for multiple months
                Pembayaran pembayaran = new Pembayaran();
                pembayaran.setIdTransaksi(pembayaranController.generateIdTransaksi());
                pembayaran.setNisSiswa(currentSiswa.getNis());
                pembayaran.setNamaSiswa(currentSiswa.getNamaLengkap());
                // Combine all selected months in the bulanTahun field - compact format
                pembayaran.setBulanTahun(selectedBulan.size() + " bulan (" + tahun + ")");
                pembayaran.setNominalSPP(Double.parseDouble(txtNominalSPP.getText()));
                pembayaran.setPotongan(0.0);
                pembayaran.setJumlahBayar(Double.parseDouble(txtJumlahBayar.getText())); // Total amount for all months
                pembayaran.setTanggalBayar(LocalDateTime.now());
                pembayaran.setMetodePembayaran((String) cmbMetodePembayaran.getSelectedItem());
                pembayaran.setStatusPembayaran("Lunas"); // All payments are automatically marked as "Lunas"
                pembayaran.setKeterangan("Pembayaran untuk bulan: " + String.join(", ", selectedBulan) + " " + tahun
                                         + " | Jumlah per bulan: " + String.format("%.0f", (Double.parseDouble(txtJumlahBayar.getText()) / selectedBulan.size())));
                pembayaran.setUserInput(currentUser);

                boolean sukses = pembayaranController.inputPembayaran(pembayaran, currentRole);
                if (!sukses) {
                    allSuccess = false;
                }

                if (allSuccess) {
                    JOptionPane.showMessageDialog(this,
                        "Pembayaran berhasil disimpan untuk " + selectedBulan.size() + " bulan!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh parent frame
                    if (parentFrame != null) {
                        SwingUtilities.invokeLater(() -> {
                            if (parentFrame instanceof DashboardTU) {
                                ((DashboardTU) parentFrame).refreshDashboard();
                            }
                        });
                    }

                    // Generate and show receipt automatically after successful payment
                    String receipt = generateReceiptText();
                    showReceiptDialog(receipt);

                    // Optionally reset the form after receipt is closed
                    // resetForm();
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void resetForm() {
        txtIdTransaksi.setText(pembayaranController.generateIdTransaksi());
        txtNIS.setText("");
        resetFormSiswa();
        txtJumlahBayar.setText("0");
        cmbTahun.setSelectedItem(String.valueOf(LocalDateTime.now().getYear()));
        cmbMetodePembayaran.setSelectedIndex(0);
        txtKeterangan.setText("");
        lblSisaBayar.setText("Rp 0");
        lblTotalBayar.setText("Rp 0 (untuk 0 bulan)"); // Reset total bayar
        currentSiswa = null;

        for (JCheckBox cb : bulanCheckBoxes.values()) {
            cb.setSelected(false);
            cb.setEnabled(false);
            cb.setForeground(Color.BLACK);
            cb.setText(cb.getText().replace(" ✓", ""));
        }

        txtNIS.requestFocus();
    }
    
    private void resetFormSiswa() {
        txtNamaSiswa.setText("");
        txtKelas.setText("");
        txtNominalSPP.setText("0");
    }

    private void cetakBuktiPembayaran() {
        if (currentSiswa == null) {
            JOptionPane.showMessageDialog(this, "Silakan cari dan pilih siswa terlebih dahulu!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> selectedBulan = getSelectedBulan();
        if (selectedBulan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih minimal 1 bulan!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtJumlahBayar.getText().trim().isEmpty() ||
            Double.parseDouble(txtJumlahBayar.getText()) <= 0) {
            JOptionPane.showMessageDialog(this, "Silakan masukkan jumlah bayar!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Generate receipt text
        String receipt = generateReceiptText();

        // Show receipt in a dialog with print/download options
        showReceiptDialog(receipt);
    }

    private String generateReceiptText() {
        StringBuilder receipt = new StringBuilder();

        receipt.append("==========================================\n");
        receipt.append("           BUKTI PEMBAYARAN SPP\n");
        receipt.append("==========================================\n");
        receipt.append("ID Transaksi   : ").append(txtIdTransaksi.getText()).append("\n");
        receipt.append("Tanggal Bayar  : ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        receipt.append("User Input     : ").append(currentUser).append("\n");
        receipt.append("------------------------------------------\n");
        receipt.append("NIS Siswa      : ").append(txtNIS.getText()).append("\n");
        receipt.append("Nama Siswa     : ").append(txtNamaSiswa.getText()).append("\n");
        receipt.append("Kelas          : ").append(txtKelas.getText()).append("\n");
        receipt.append("------------------------------------------\n");
        receipt.append("Bulan Dibayar  : ").append(String.join(", ", getSelectedBulan())).append(" ").append(cmbTahun.getSelectedItem()).append("\n");
        receipt.append("Nominal SPP    : Rp ").append(String.format("%.0f", Double.parseDouble(txtNominalSPP.getText()))).append("\n");
        receipt.append("Jumlah Bayar   : Rp ").append(txtJumlahBayar.getText()).append("\n");
        receipt.append("Jumlah/Bulan   : Rp ").append(String.format("%.0f", Double.parseDouble(txtJumlahBayar.getText()) / getSelectedBulan().size())).append("\n");
        receipt.append("Metode Bayar   : ").append(cmbMetodePembayaran.getSelectedItem()).append("\n");
        receipt.append("Status Bayar   : Lunas\n");
        if (!txtKeterangan.getText().trim().isEmpty()) {
            receipt.append("Keterangan     : ").append(txtKeterangan.getText()).append("\n");
        }
        receipt.append("------------------------------------------\n");
        receipt.append("Terima kasih atas pembayaran Anda.\n");
        receipt.append("==========================================\n");

        return receipt.toString();
    }

    private void showReceiptDialog(String receipt) {
        JTextArea textArea = new JTextArea(receipt);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnPrint = new JButton("🖨️ Print");
        btnPrint.setBackground(new Color(52, 152, 219));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.addActionListener(e -> printReceipt(receipt));

        JButton btnSave = new JButton("💾 Simpan File");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveReceiptToFile(receipt));

        JButton btnClose = new JButton("❌ Tutup");
        btnClose.setBackground(new Color(231, 76, 60));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> SwingUtilities.getWindowAncestor(buttonPanel).dispose());

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClose);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JOptionPane.showOptionDialog(this, mainPanel, "Bukti Pembayaran",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                   null, new Object[]{}, null);
    }

    private void printReceipt(String receipt) {
        try {
            JTextArea textArea = new JTextArea(receipt);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));

            boolean success = textArea.print();
            if (success) {
                JOptionPane.showMessageDialog(this, "Bukti pembayaran berhasil dicetak!",
                                            "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mencetak bukti pembayaran!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saat mencetak: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveReceiptToFile(String receipt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Bukti Pembayaran");
        fileChooser.setSelectedFile(new File("bukti_pembayaran_" + txtIdTransaksi.getText() + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                writer.print(receipt);
                JOptionPane.showMessageDialog(this, "Bukti pembayaran berhasil disimpan ke:\n" +
                                            fileToSave.getAbsolutePath(),
                                            "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saat menyimpan file: " + e.getMessage(),
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}