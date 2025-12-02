package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.KelasController;
import aplikasi.pembayaran.spp.model.Kelas;
import aplikasi.pembayaran.spp.view.NumericValidator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.border.TitledBorder;

/**
 * FormKelas - CRUD Form for managing class data
 * Features: Create, Read, Update, Delete kelas with class, year, and SPP amount
 */
public class FormKelas extends JFrame {
    // Components
    private JTextField txtKelas, txtAngkatan, txtNominalSPP;
    private JButton btnSimpan, btnReset, btnKeluar;
    private KelasController kelasController;
    private String currentUserRole;

    // Parent reference for refresh after save
    private JPanel parentPanel;
    private JFrame parentFrame;
    
    // 🔥 Flag untuk menandakan apakah ini mode edit
    private boolean isEditMode = false;
    private String originalKelas = null;
    private String originalAngkatan = null;

    // Constructor for add mode (simple form for adding new kelas)
    public FormKelas(String role) {
        this.currentUserRole = role;
        this.kelasController = new KelasController();
        this.parentFrame = null;
        this.parentPanel = null;
        this.isEditMode = false;
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true); // 🔥 TAMBAHKAN INI
    }

    // Constructor for add mode with parent frame (for refresh after save)
    public FormKelas(String role, JFrame parentFrame) {
        this.currentUserRole = role;
        this.kelasController = new KelasController();
        this.parentFrame = parentFrame;
        this.parentPanel = null;
        this.isEditMode = false;
        initComponents();
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true); // 🔥 TAMBAHKAN INI
    }

    // Constructor for edit mode (form for editing existing kelas)
    public FormKelas(Kelas kelas, String role) {
        this.currentUserRole = role;
        this.kelasController = new KelasController();
        this.parentFrame = null;
        this.parentPanel = null;
        this.isEditMode = true;
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        // Load data for edit mode
        if (kelas != null) {
            this.originalKelas = kelas.getKelas();
            this.originalAngkatan = kelas.getAngkatan();
            txtKelas.setText(kelas.getKelas());
            txtAngkatan.setText(kelas.getAngkatan());
            txtNominalSPP.setText(String.valueOf(kelas.getNominalSPP()));
            // 🔥 TIDAK PERLU DISABLE - Biarkan tetap editable
        }
    }

    // Constructor for edit mode with parent frame (for refresh after save)
    public FormKelas(Kelas kelas, String role, JFrame parentFrame) {
        this.currentUserRole = role;
        this.kelasController = new KelasController();
        this.parentFrame = parentFrame;
        this.parentPanel = null;
        this.isEditMode = true;
        initComponents();
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        // Load data for edit mode
        if (kelas != null) {
            this.originalKelas = kelas.getKelas();
            this.originalAngkatan = kelas.getAngkatan();
            txtKelas.setText(kelas.getKelas());
            txtAngkatan.setText(kelas.getAngkatan());
            txtNominalSPP.setText(String.valueOf(kelas.getNominalSPP()));
            // 🔥 TIDAK PERLU DISABLE - Biarkan tetap editable
        }
    }

    private void initComponents() {
        setTitle(isEditMode ? "Edit Kelas" : "Tambah Kelas Baru");
        setSize(600, 500);
        setLayout(new BorderLayout(15, 15));

        // ===== PANEL HEADER =====
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(52, 152, 219));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel(isEditMode ? "✏️ EDIT DATA KELAS" : "🎓 FORM DATA KELAS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel(isEditMode ? "Edit data kelas yang sudah ada" : "Tambah data kelas, angkatan, dan nominal SPP");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblInfo.setForeground(new Color(236, 240, 241));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblInfo);

        panelHeader.add(titlePanel, BorderLayout.WEST);

        // ===== PANEL FORM INPUT =====
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "📝 Form Input Kelas",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), Color.BLACK));
        panelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 - Kelas
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblKelas = new JLabel("Kelas *:");
        lblKelas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelForm.add(lblKelas, gbc);
        txtKelas = new JTextField();
        txtKelas.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtKelas.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        panelForm.add(txtKelas, gbc);
        gbc.gridwidth = 1;

        // Row 1 - Angkatan
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblAngkatan = new JLabel("Angkatan *:");
        lblAngkatan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelForm.add(lblAngkatan, gbc);
        txtAngkatan = new JTextField();
        txtAngkatan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAngkatan.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        panelForm.add(txtAngkatan, gbc);
        gbc.gridwidth = 1;

        // Row 2 - Nominal SPP
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblNominalSPP = new JLabel("Nominal SPP *:");
        lblNominalSPP.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelForm.add(lblNominalSPP, gbc);
        txtNominalSPP = new JTextField();
        NumericValidator.makeNumericWithDecimalOnly(txtNominalSPP); // Add numeric validation
        txtNominalSPP.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtNominalSPP.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        panelForm.add(txtNominalSPP, gbc);
        gbc.gridwidth = 1;

        // ===== PANEL BUTTONS =====
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelButtons.setBackground(Color.WHITE);

        btnSimpan = new JButton(isEditMode ? "💾 UPDATE KELAS" : "💾 SIMPAN KELAS");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setPreferredSize(new Dimension(180, 45));
        btnSimpan.addActionListener(e -> simpanKelas());

        btnReset = new JButton("🔄 RESET FORM");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnReset.setBackground(new Color(149, 165, 166));
        btnReset.setForeground(Color.WHITE);
        btnReset.setPreferredSize(new Dimension(180, 45));
        btnReset.addActionListener(e -> resetForm());
        
        // Hide reset button in edit mode
        if (isEditMode) {
            btnReset.setVisible(false);
        }

        btnKeluar = new JButton("❌ KELUAR");
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnKeluar.setBackground(new Color(142, 68, 173));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setPreferredSize(new Dimension(180, 45));
        btnKeluar.addActionListener(e -> dispose());

        panelButtons.add(btnSimpan);
        panelButtons.add(btnReset);
        panelButtons.add(btnKeluar);

        // Layout components
        add(panelHeader, BorderLayout.NORTH);
        add(panelForm, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
    }

    // 🔥 Method to save kelas (both add and update) - SIMPLIFIED!
    private void simpanKelas() {
        // Validate input
        if (txtKelas.getText().trim().isEmpty() ||
            txtAngkatan.getText().trim().isEmpty() ||
            txtNominalSPP.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Semua field harus diisi!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String kelas = txtKelas.getText().trim();
            String angkatan = txtAngkatan.getText().trim();
            double nominalSPP = Double.parseDouble(txtNominalSPP.getText().trim());

            if (isEditMode) {
                // 🔥 MODE EDIT - Update dengan original key
                boolean success = kelasController.updateKelas(
                    originalKelas,      // WHERE kelas = original
                    originalAngkatan,   // AND angkatan = original
                    kelas,              // SET kelas = new value
                    angkatan,           // SET angkatan = new value
                    nominalSPP          // SET nominal = new value
                );

                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Data kelas berhasil diperbarui!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 🔥 CRITICAL: Refresh SEBELUM dispose
                    refreshParentTable();
                    
                    // 🔥 Delay sedikit agar refresh selesai
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Gagal memperbarui kelas!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 🔥 MODE ADD - Create new kelas
                Kelas newKelas = new Kelas(kelas, angkatan, nominalSPP);
                boolean success = kelasController.createKelas(newKelas);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Kelas baru berhasil ditambahkan!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 🔥 CRITICAL: Refresh SEBELUM dispose/reset
                    refreshParentTable();
                    
                    // 🔥 Delay sedikit agar refresh selesai
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    // Ask user if they want to add more
                    int option = JOptionPane.showConfirmDialog(this,
                        "Apakah Anda ingin menambah kelas lagi?",
                        "Tambah Lagi?",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        resetForm();
                    } else {
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Gagal menambah kelas baru! Mungkin kelas sudah ada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Nominal SPP harus berupa angka!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🔥 NEW METHOD: Refresh parent table
    private void refreshParentTable() {
        if (parentFrame != null && parentFrame instanceof DashboardAdmin) {
            System.out.println("🔄 Refreshing parent DashboardAdmin table...");
            
            // 🔥 Gunakan SwingUtilities untuk memastikan refresh di EDT
            javax.swing.SwingUtilities.invokeLater(() -> {
                ((DashboardAdmin) parentFrame).refreshKelasTable();
                System.out.println("✅ Parent table refreshed!");
            });
        } else {
            System.out.println("⚠️ Parent frame is null or not DashboardAdmin");
        }
    }

    // Method to reset form
    private void resetForm() {
        txtKelas.setText("");
        txtAngkatan.setText("");
        txtNominalSPP.setText("");
        txtKelas.requestFocus();
        
        // Re-enable fields if they were disabled
        txtKelas.setEditable(true);
        txtKelas.setBackground(Color.WHITE);
        txtAngkatan.setEditable(true);
        txtAngkatan.setBackground(Color.WHITE);
    }
}