package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.UserController;
import aplikasi.pembayaran.spp.model.User;
import aplikasi.pembayaran.spp.view.NumericValidator;

import javax.swing.*;
import java.awt.*;

public class FormTambahUser extends JDialog {
    
    // Components
    private JTextField txtUsername, txtNamaLengkap, txtNoTelepon;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbRole;
    private JCheckBox chkActive;
    private JButton btnSimpan, btnBatal;
    
    // Controller
    private UserController userController;
    
    // Parent reference untuk refresh
    private JFrame parentFrame;
    
    // Role user yang sedang login
    private String currentUserRole;
    
    public FormTambahUser(JFrame parent) {
        super(parent, "Tambah User Baru", true);
        this.parentFrame = parent;
        this.userController = new UserController();
        
        // 🔥 AMBIL ROLE DARI PARENT (DashboardAdmin)
        if (parent instanceof DashboardAdmin) {
            DashboardAdmin dashboard = (DashboardAdmin) parent;
            this.currentUserRole = dashboard.currentUser.getRole();
            System.out.println("✅ Current user role: " + this.currentUserRole);
        } else {
            // Default ke Admin jika tidak bisa detect
            this.currentUserRole = "Admin";
            System.out.println("⚠️ Cannot detect parent role, defaulting to Admin");
        }
        
        initComponents();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initComponents() {
        setSize(500, 550);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        
        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(46, 204, 113));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("➕ TAMBAH USER BARU");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0 - Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username: *"), gbc);
        
        txtUsername = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(txtUsername, gbc);
        
        // Row 1 - Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password: *"), gbc);
        
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtPassword, gbc);
        
        // Row 2 - Confirm Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Konfirmasi Password: *"), gbc);
        
        txtConfirmPassword = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(txtConfirmPassword, gbc);
        
        // Row 3 - Role
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Role: *"), gbc);
        
        cmbRole = new JComboBox<>(new String[]{"Kepsek", "Admin", "TU", "Siswa"});
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(cmbRole, gbc);
        
        // Row 4 - Nama Lengkap
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Nama Lengkap: *"), gbc);
        
        txtNamaLengkap = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(txtNamaLengkap, gbc);
        
        // Row 5 - No Telepon
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("No. Telepon:"), gbc);
        
        txtNoTelepon = new JTextField(20);
        NumericValidator.makeNumericOnly(txtNoTelepon); // Add numeric validation for phone number
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(txtNoTelepon, gbc);
        
        // Row 6 - Status Active
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Status:"), gbc);
        
        chkActive = new JCheckBox("Aktif");
        chkActive.setSelected(true);
        chkActive.setBackground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(chkActive, gbc);
        
        // Row 7 - Info
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("* Field wajib diisi");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblInfo.setForeground(Color.RED);
        formPanel.add(lblInfo, gbc);
        
        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        btnSimpan = new JButton("💾 SIMPAN");
        btnSimpan.setPreferredSize(new Dimension(140, 40));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 13));
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(e -> simpanUser());
        
        btnBatal = new JButton("❌ BATAL");
        btnBatal.setPreferredSize(new Dimension(140, 40));
        btnBatal.setBackground(new Color(231, 76, 60));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 13));
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        
        // Add panels to dialog
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void simpanUser() {
        // Validasi input
        if (!validateInput()) {
            return;
        }
        
        try {
            // Buat object User
            User newUser = new User();
            newUser.setUsername(txtUsername.getText().trim());
            newUser.setPassword(new String(txtPassword.getPassword()));
            newUser.setRole((String) cmbRole.getSelectedItem());
            newUser.setNamaLengkap(txtNamaLengkap.getText().trim());
            newUser.setNoTelepon(txtNoTelepon.getText().trim());
            newUser.setActive(chkActive.isSelected());
            
            // 🔥 KIRIM ROLE YANG BENAR KE CONTROLLER
            System.out.println("🔍 Calling registerUser with role: " + this.currentUserRole);
            boolean sukses = userController.registerUser(newUser, this.currentUserRole);
            
            if (sukses) {
                // Refresh table di parent
                if (parentFrame instanceof DashboardAdmin) {
                    ((DashboardAdmin) parentFrame).refreshUserTable();
                }
                dispose();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean validateInput() {
        // Cek username
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return false;
        }
        
        // Cek panjang username
        if (txtUsername.getText().trim().length() < 3) {
            JOptionPane.showMessageDialog(this, "Username minimal 3 karakter!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return false;
        }
        
        // Cek username sudah ada
        if (userController.isUsernameExists(txtUsername.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Username sudah digunakan!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return false;
        }
        
        // Cek password
        if (txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return false;
        }
        
        // Cek panjang password
        if (txtPassword.getPassword().length < 6) {
            JOptionPane.showMessageDialog(this, "Password minimal 6 karakter!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return false;
        }
        
        // Cek konfirmasi password
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password tidak cocok!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtConfirmPassword.requestFocus();
            return false;
        }
        
        // Cek nama lengkap
        if (txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtNamaLengkap.requestFocus();
            return false;
        }
        
        return true;
    }
}