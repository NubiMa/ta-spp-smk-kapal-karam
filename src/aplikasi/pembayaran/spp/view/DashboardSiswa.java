package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.PembayaranController;
import aplikasi.pembayaran.spp.controller.SiswaController;
import aplikasi.pembayaran.spp.controller.UserController;
import aplikasi.pembayaran.spp.model.User;
import aplikasi.pembayaran.spp.model.Pembayaran;
import aplikasi.pembayaran.spp.model.Siswa;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardSiswa extends JFrame {
    private User currentUser;
    private Siswa currentSiswa;

    private SiswaController siswaController;
    private PembayaranController pembayaranController;
    private UserController userController;
    
    private JLabel lblNamaSiswa, lblKelas, lblNIS, lblTunggakanCount, lblTotalTunggakan;
    private JTable tunggakanTable;
    private DefaultTableModel tableModel;

    public DashboardSiswa(User user) {
        this.currentUser = user;
        this.siswaController = new SiswaController();
        this.pembayaranController = new PembayaranController();
        this.userController = new UserController();

        // Load student data based on NIS (which should be the username)
        this.currentSiswa = siswaController.getSiswaByNis(user.getUsername());

        if (this.currentSiswa == null) {
            JOptionPane.showMessageDialog(this,
                "Data siswa tidak ditemukan untuk NIS: " + user.getUsername(),
                "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        loadTunggakanData();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Dashboard Siswa - " + currentSiswa.getNamaLengkap());
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Main container dengan background
        JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
        mainContainer.setBackground(new Color(236, 240, 243));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Content Panel (Stats + Table)
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(new Color(236, 240, 243));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Stats Panel
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        // Tunggakan Table
        JPanel tablePanel = createTunggakanTable();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(contentPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JMenu menuAkun = new JMenu("⚙ Akun");
        menuAkun.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        menuAkun.setForeground(new Color(60, 60, 60));

        JMenuItem menuItemGantiPassword = new JMenuItem("🔒 Ganti Password");
        menuItemGantiPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        menuItemGantiPassword.addActionListener(e -> gantiPassword());

        JMenuItem menuItemLogout = new JMenuItem("🚪 Logout");
        menuItemLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        menuItemLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginPage().setVisible(true);
            }
        });

        menuAkun.add(menuItemGantiPassword);
        menuAkun.addSeparator();
        menuAkun.add(menuItemLogout);

        menuBar.add(menuAkun);

        return menuBar;
    }

    private void gantiPassword() {
        JPasswordField currentPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmNewPasswordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Password Saat Ini:"));
        panel.add(currentPasswordField);
        panel.add(new JLabel(" "));
        panel.add(new JLabel(" "));
        panel.add(new JLabel("Password Baru:"));
        panel.add(newPasswordField);
        panel.add(new JLabel(" "));
        panel.add(new JLabel(" "));
        panel.add(new JLabel("Konfirmasi Password Baru:"));
        panel.add(confirmNewPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Ganti Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmNewPassword = new String(confirmNewPasswordField.getPassword());

            // Validate input
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Semua field harus diisi!",
                    "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this,
                    "Password baru harus minimal 6 karakter!",
                    "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                JOptionPane.showMessageDialog(this,
                    "Password baru dan konfirmasi password tidak cocok!",
                    "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verify current password is correct
            if (!currentUser.getPassword().equals(currentPassword)) {
                JOptionPane.showMessageDialog(this,
                    "Password saat ini salah!",
                    "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the password
            User updatedUser = new User();
            updatedUser.setUsername(currentUser.getUsername());
            updatedUser.setPassword(newPassword);
            updatedUser.setRole(currentUser.getRole());
            updatedUser.setNamaLengkap(currentUser.getNamaLengkap());
            updatedUser.setNoTelepon(currentUser.getNoTelepon());
            updatedUser.setActive(currentUser.isActive());

            boolean success = userController.updateProfile(updatedUser, currentUser.getUsername());
            if (success) {
                // Update current user's password
                currentUser.setPassword(newPassword);
                JOptionPane.showMessageDialog(this,
                    "Password berhasil diubah!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Left side - Title
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("DASHBOARD SISWA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSubtitle = new JLabel("Sistem Pembayaran SPP");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        
        leftPanel.add(lblTitle);
        leftPanel.add(lblSubtitle);

        // Right side - User Info Card
        JPanel userInfoCard = new JPanel(new GridLayout(3, 1, 0, 3));
        userInfoCard.setBackground(new Color(52, 152, 219));
        userInfoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        lblNIS = new JLabel("NIS: " + currentSiswa.getNis());
        lblNIS.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNIS.setForeground(Color.WHITE);
        
        lblNamaSiswa = new JLabel("Nama: " + currentSiswa.getNamaLengkap());
        lblNamaSiswa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNamaSiswa.setForeground(Color.WHITE);
        
        lblKelas = new JLabel("Kelas: " + currentSiswa.getKelas());
        lblKelas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblKelas.setForeground(Color.WHITE);

        userInfoCard.add(lblNIS);
        userInfoCard.add(lblNamaSiswa);
        userInfoCard.add(lblKelas);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(userInfoCard, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);

        Object[] cards = createStatCard("Bulan Tunggakan", "0", "spp belum dibayar", 
            new Color(231, 76, 60), "📅");
        JPanel tunggakanCard = (JPanel) cards[0];
        lblTunggakanCount = (JLabel) cards[1];

        Object[] totalCards = createStatCard("Total Tunggakan", "Rp 0", "jumlah yang harus dibayar", 
            new Color(230, 126, 34), "💰");
        JPanel totalTunggakanCard = (JPanel) totalCards[0];
        lblTotalTunggakan = (JLabel) totalCards[1];

        statsPanel.add(tunggakanCard);
        statsPanel.add(totalTunggakanCard);

        return statsPanel;
    }

    private Object[] createStatCard(String title, String value, String description, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Icon panel dengan background rounded
        JPanel iconContainer = new JPanel(new GridBagLayout());
        iconContainer.setPreferredSize(new Dimension(70, 70));
        iconContainer.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        iconContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setForeground(color);
        iconContainer.add(iconLabel);

        // Text panel
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(127, 140, 141));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(color);

        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(149, 165, 166));

        textPanel.add(lblTitle);
        textPanel.add(lblValue);
        textPanel.add(lblDesc);

        card.add(iconContainer, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return new Object[]{card, lblValue};
    }

    private JPanel createTunggakanTable() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setOpaque(false);

        // Header panel untuk tabel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTableTitle = new JLabel("📋 Daftar SPP Belum Dibayar");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTableTitle, BorderLayout.WEST);

        String[] columns = {"No", "Bulan", "Tahun", "Nominal SPP", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tunggakanTable = new JTable(tableModel);
        tunggakanTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tunggakanTable.setRowHeight(40);
        tunggakanTable.setShowVerticalLines(false);
        tunggakanTable.setGridColor(new Color(240, 240, 240));
        tunggakanTable.setSelectionBackground(new Color(52, 152, 219, 30));
        tunggakanTable.setSelectionForeground(new Color(50, 50, 50));
        
        // Custom header
        tunggakanTable.getTableHeader().setBackground(new Color(231, 76, 60));
        tunggakanTable.getTableHeader().setForeground(Color.WHITE);
        tunggakanTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tunggakanTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        tunggakanTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Center alignment untuk kolom tertentu
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tunggakanTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // No
        tunggakanTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Tahun
        
        // Status column dengan warna
        tunggakanTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (!isSelected) {
                    setBackground(new Color(231, 76, 60, 20));
                    setForeground(new Color(192, 57, 43));
                }
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        });

        // Set column widths
        tunggakanTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // No
        tunggakanTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Bulan
        tunggakanTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Tahun
        tunggakanTable.getColumnModel().getColumn(3).setPreferredWidth(150);  // Nominal
        tunggakanTable.getColumnModel().getColumn(4).setPreferredWidth(150);  // Status

        JScrollPane scrollPane = new JScrollPane(tunggakanTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(headerPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void loadTunggakanData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get current month and year
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Get all payment records for this student
        List<Pembayaran> pembayaranList = pembayaranController.getPembayaranByNIS(currentSiswa.getNis());

        // Create set of months already paid in the current year
        Set<String> bulanLunas = new HashSet<>();
        for (Pembayaran p : pembayaranList) {
            if ("Lunas".equals(p.getStatusPembayaran())) {
                String bulanTahun = p.getBulanTahun();
                if (bulanTahun != null && bulanTahun.contains(String.valueOf(currentYear))) {
                    if (bulanTahun.contains("bulan")) {
                        String keterangan = p.getKeterangan();
                        if (keterangan != null && keterangan.contains("Pembayaran untuk bulan:")) {
                            int bulanStart = keterangan.indexOf("Pembayaran untuk bulan: ") + "Pembayaran untuk bulan: ".length();
                            int separatorIndex = keterangan.indexOf(" | ");
                            if (separatorIndex == -1) separatorIndex = keterangan.length();
                            
                            String bulanDalamKeterangan = keterangan.substring(bulanStart, separatorIndex).trim();
                            
                            if (bulanDalamKeterangan.contains(" " + currentYear)) {
                                String monthsOnly = bulanDalamKeterangan.substring(0, bulanDalamKeterangan.lastIndexOf(" " + currentYear)).trim();
                                String[] months = monthsOnly.split(", ");
                                for(String month : months) {
                                    bulanLunas.add(month.trim());
                                }
                            }
                        }
                    } else {
                        String[] parts = bulanTahun.split(" ");
                        if (parts.length >= 2 && parts[1].equals(String.valueOf(currentYear))) {
                            bulanLunas.add(parts[0]);
                        }
                    }
                }
            }
        }

        // Define months array
        String[] BULAN_ARRAY = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };

        // Count total unpaid months and calculate amount
        int totalUnpaidMonths = 0;
        double totalTunggakan = 0;

        // Add unpaid months to the table
        for (int month = 1; month <= currentMonth; month++) {
            String bulanName = BULAN_ARRAY[month - 1];
            
            if (!bulanLunas.contains(bulanName)) {
                totalUnpaidMonths++;
                totalTunggakan += currentSiswa.getNominalSPP();

                Object[] row = {
                    totalUnpaidMonths,
                    bulanName,
                    currentYear,
                    "Rp " + String.format("%,.0f", currentSiswa.getNominalSPP()),
                    "Belum Bayar"
                };
                tableModel.addRow(row);
            }
        }

        // Update stats
        lblTunggakanCount.setText(String.valueOf(totalUnpaidMonths));
        lblTotalTunggakan.setText("Rp " + String.format("%,d", (long)totalTunggakan));
    }

    public void refreshDashboard() {
        loadTunggakanData();
    }
}