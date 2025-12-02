package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.model.Koneksi;
import aplikasi.pembayaran.spp.controller.UserController;
import aplikasi.pembayaran.spp.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginPage extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin, btnExit;
    private UserController userController;

    public LoginPage() {
        this.userController = new UserController();

        setTitle("Login - Aplikasi Pembayaran SPP");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // 🔹 Panel Utama: Dua Kolom
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBorder(BorderFactory.createEmptyBorder());

        // 🔹 Panel Kiri: Background Biru + Logo
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = new Color(52, 152, 219);   // Biru cerah
                Color color2 = new Color(41, 128, 185);   // Biru tua
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 🔸 Logo dari assets/logo.png (PERBAIKAN PATH)
        JLabel lblLogo = new JLabel();
        boolean logoLoaded = false;
        try {
            // Path yang benar berdasarkan struktur folder
            java.net.URL logoUrl = getClass().getResource("/aplikasi/pembayaran/spp/assets/logo.png");
            
            if (logoUrl != null) {
                BufferedImage logoImg = ImageIO.read(logoUrl);
                Image scaledLogo = logoImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(scaledLogo));
                logoLoaded = true;
                System.out.println("Logo berhasil dimuat!");
            } else {
                System.err.println("Logo tidak ditemukan di path: /aplikasi/pembayaran/spp/assets/logo.png");
            }
        } catch (IOException e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
            e.printStackTrace();
        }

        if (!logoLoaded) {
            // Fallback: teks SPP jika logo gagal
            lblLogo.setText("SPP");
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 60));
            lblLogo.setForeground(Color.WHITE);
        }
        leftPanel.add(lblLogo, gbc);

        JLabel lblTitle = new JLabel("SISTEM PEMBAYARAN SPP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 1;
        leftPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("SMK Kapal Karam ");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        gbc.gridy = 2;
        leftPanel.add(lblSubtitle, gbc);

        // 🔸 Ikon decorative (DIPERBAIKI - gunakan Unicode symbols yang lebih baik)
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        iconPanel.setOpaque(false);
        
        String[] icons = {"📚", "💰", "📊", "✓"};
        for (String icon : icons) {
            JLabel lblIcon = new JLabel(icon);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            lblIcon.setForeground(new Color(255, 255, 255, 180));
            iconPanel.add(lblIcon);
        }
        
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 0, 0, 0);
        leftPanel.add(iconPanel, gbc);

        // 🔹 Panel Kanan: Form Login
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblWelcome = new JLabel("Selamat Datang!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(lblWelcome, gbc);

        JLabel lblInstruction = new JLabel("Silakan masukkan akun Anda untuk masuk");
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInstruction.setForeground(new Color(120, 120, 120));
        gbc.gridy = 1;
        rightPanel.add(lblInstruction, gbc);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(new Color(70, 70, 70));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        rightPanel.add(lblUser, gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtUsername.setCaretColor(new Color(52, 152, 219));
        gbc.gridy = 3;
        rightPanel.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPass.setForeground(new Color(70, 70, 70));
        gbc.gridy = 4;
        rightPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtPassword.setCaretColor(new Color(52, 152, 219));
        txtPassword.setEchoChar('•');
        gbc.gridy = 5;
        rightPanel.add(txtPassword, gbc);

        chkShowPassword = new JCheckBox("Tampilkan password");
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.setForeground(new Color(100, 100, 100));
        chkShowPassword.setOpaque(false);
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        rightPanel.add(chkShowPassword, gbc);

        // 🔸 Tombol LOGIN dengan Gradient Biru
        btnLogin = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Gradient biru modern
                GradientPaint gp = new GradientPaint(
                    0, 0,
                    new Color(74, 144, 226),   // Biru terang
                    0, getHeight(),
                    new Color(41, 128, 185)    // Biru tua
                );
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

                super.paintComponent(g);
            }
        };
        btnLogin.setPreferredSize(new Dimension(220, 42));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setOpaque(false);
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);
        rightPanel.add(btnLogin, gbc);

        btnExit = new JButton("Keluar");
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnExit.setForeground(new Color(150, 150, 150));
        btnExit.setBorderPainted(false);
        btnExit.setContentAreaFilled(false);
        btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        rightPanel.add(btnExit, gbc);

        // 🔹 Event Listeners
        btnLogin.addActionListener(e -> loginProses());
        btnExit.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> loginProses());

        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("© 2025 Aplikasi Pembayaran SPP | SMK Negeri 1 Contoh", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(new Color(150, 150, 150));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(footer, BorderLayout.SOUTH);
    }

    private void loginProses() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan Password tidak boleh kosong!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userController.login(username, password);

        if (user != null) {
            String role = user.getRole();
            JOptionPane.showMessageDialog(this,
                "Login berhasil sebagai " + role + "!\nSelamat datang, " + user.getNamaLengkap(),
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);

            dispose();

            switch (role) {
                case "Kepsek":
                    new DashboardKepsek(user).setVisible(true);
                    break;
                case "Admin":
                    new DashboardAdmin(user).setVisible(true);
                    break;
                case "TU":
                    new DashboardTU(user.getUsername()).setVisible(true);
                    break;
                case "Siswa":
                    new DashboardSiswa(user).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this,
                        "Role tidak dikenali: " + role,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Username atau Password salah!",
                "Login Gagal", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    // ❌ TIDAK ADA main() — aplikasi dimulai dari SplashScreen
}