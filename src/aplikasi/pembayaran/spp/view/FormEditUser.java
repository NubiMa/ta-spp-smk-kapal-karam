package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.UserController;
import aplikasi.pembayaran.spp.model.User;
import aplikasi.pembayaran.spp.view.NumericValidator;

import javax.swing.*;
import java.awt.*;

public class FormEditUser extends JDialog {
    // Components
    private JTextField txtUsername, txtNamaLengkap, txtNoTelepon;
    private JPasswordField txtPasswordBaru, txtConfirmPassword;
    private JComboBox<String> cmbRole;
    private JCheckBox chkActive, chkGantiPassword;
    private JButton btnUpdate, btnBatal;
    
    // Controller
    private UserController userController;
    
    // Data user yang di-edit
    private User userEdit;
    
    // Parent reference
    private JFrame parentFrame;
    
    // Role user yang sedang login
    private String currentUserRole;
    
    public FormEditUser(JFrame parent, User user) {
        super(parent, "Edit Data User", true);
        this.parentFrame = parent;
        this.userEdit = user;
        this.userController = new UserController();
        
        // 🔥 AMBIL ROLE DARI PARENT
        if (parent instanceof DashboardAdmin) {
            DashboardAdmin dashboard = (DashboardAdmin) parent;
            this.currentUserRole = dashboard.currentUser.getRole();
            System.out.println("✅ Current user role: " + this.currentUserRole);
        } else {
            this.currentUserRole = "Admin";
            System.out.println("⚠️ Cannot detect parent role, defaulting to Admin");
        }
        
        initComponents();
        loadUserData();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initComponents() {
        setSize(500, 600);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        
        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("✏️ EDIT DATA USER");
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
        
        // Row 0 - Username (readonly)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        txtUsername = new JTextField(20);
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(txtUsername, gbc);
        
        // Row 1 - Checkbox ganti password
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        chkGantiPassword = new JCheckBox("Ganti Password");
        chkGantiPassword.setBackground(Color.WHITE);
        chkGantiPassword.addActionListener(e -> togglePasswordFields());
        formPanel.add(chkGantiPassword, gbc);
        gbc.gridwidth = 1;
        
        // Row 2 - Password Baru
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblPassword = new JLabel("Password Baru:");
        formPanel.add(lblPassword, gbc);
        
        txtPasswordBaru = new JPasswordField(20);
        txtPasswordBaru.setEnabled(false);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(txtPasswordBaru, gbc);
        
        // Row 3 - Confirm Password
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblConfirm = new JLabel("Konfirmasi Password:");
        formPanel.add(lblConfirm, gbc);
        
        txtConfirmPassword = new JPasswordField(20);
        txtConfirmPassword.setEnabled(false);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(txtConfirmPassword, gbc);
        
        // Row 4 - Role
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Role: *"), gbc);
        
        cmbRole = new JComboBox<>(new String[]{"Kepsek", "Admin", "TU", "Siswa"});
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(cmbRole, gbc);
        
        // Row 5 - Nama Lengkap
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Nama Lengkap: *"), gbc);
        
        txtNamaLengkap = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(txtNamaLengkap, gbc);
        
        // Row 6 - No Telepon
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("No. Telepon:"), gbc);
        
        txtNoTelepon = new JTextField(20);
        NumericValidator.makeNumericOnly(txtNoTelepon); // Add numeric validation for phone number
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(txtNoTelepon, gbc);
        
        // Row 7 - Status Active
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Status:"), gbc);
        
        chkActive = new JCheckBox("Aktif");
        chkActive.setBackground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 7;
        formPanel.add(chkActive, gbc);
        
        // Row 8 - Info
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("* Field wajib diisi");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblInfo.setForeground(Color.RED);
        formPanel.add(lblInfo, gbc);
        
        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        btnUpdate = new JButton("💾 UPDATE");
        btnUpdate.setPreferredSize(new Dimension(140, 40));
        btnUpdate.setBackground(new Color(52, 152, 219));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 13));
        btnUpdate.setFocusPainted(false);
        btnUpdate.addActionListener(e -> updateUser());
        
        btnBatal = new JButton("❌ BATAL");
        btnBatal.setPreferredSize(new Dimension(140, 40));
        btnBatal.setBackground(new Color(231, 76, 60));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 13));
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dispose());
        
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnBatal);
        
        // Add panels to dialog
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadUserData() {
        if (userEdit != null) {
            txtUsername.setText(userEdit.getUsername());
            cmbRole.setSelectedItem(userEdit.getRole());
            txtNamaLengkap.setText(userEdit.getNamaLengkap());
            txtNoTelepon.setText(userEdit.getNoTelepon());
            chkActive.setSelected(userEdit.isActive());
        }
    }
    
    private void togglePasswordFields() {
        boolean enabled = chkGantiPassword.isSelected();
        txtPasswordBaru.setEnabled(enabled);
        txtConfirmPassword.setEnabled(enabled);
        
        if (!enabled) {
            txtPasswordBaru.setText("");
            txtConfirmPassword.setText("");
        }
    }
    
    private void updateUser() {
        // Validasi input
        if (!validateInput()) {
            return;
        }
        
        try {
            // Update data user
            userEdit.setRole((String) cmbRole.getSelectedItem());
            userEdit.setNamaLengkap(txtNamaLengkap.getText().trim());
            userEdit.setNoTelepon(txtNoTelepon.getText().trim());
            userEdit.setActive(chkActive.isSelected());
            
            // Jika ganti password
            if (chkGantiPassword.isSelected()) {
                userEdit.setPassword(new String(txtPasswordBaru.getPassword()));
            }
            
            // Update via controller
            boolean sukses = userController.updateUser(userEdit);
            
            if (sukses) {
                JOptionPane.showMessageDialog(this,
                        "Data user berhasil diupdate!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

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
        // Cek nama lengkap
        if (txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtNamaLengkap.requestFocus();
            return false;
        }
        
        // Validasi password jika ganti password
        if (chkGantiPassword.isSelected()) {
            if (txtPasswordBaru.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Password baru tidak boleh kosong!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                txtPasswordBaru.requestFocus();
                return false;
            }
            
            if (txtPasswordBaru.getPassword().length < 6) {
                JOptionPane.showMessageDialog(this, "Password minimal 6 karakter!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                txtPasswordBaru.requestFocus();
                return false;
            }
            
            String password = new String(txtPasswordBaru.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Password tidak cocok!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                txtConfirmPassword.requestFocus();
                return false;
            }
        }
        
        return true;
    }
}