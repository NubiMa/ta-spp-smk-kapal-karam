package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.LaporanController;
import aplikasi.pembayaran.spp.controller.PembayaranController;
import aplikasi.pembayaran.spp.controller.SiswaController;
import aplikasi.pembayaran.spp.controller.TagihanController;
import aplikasi.pembayaran.spp.model.User;
import aplikasi.pembayaran.spp.controller.UserController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard untuk Kepala Sekolah
 * Role: READ-ONLY semua data, monitoring, laporan
 * 
 * Fitur Kepsek:
 * - Lihat statistik pembayaran
 * - Lihat laporan keuangan
 * - Monitor tunggakan siswa
 * - Lihat data siswa (read-only)
 * - Monitoring sistem pembayaran dan keuangan
 */
public class DashboardKepsek extends JFrame implements ActionListener {
    
    // User yang sedang login
    private User currentUser;
    
    // Controllers
    private UserController userController;
    private SiswaController siswaController;
    private PembayaranController pembayaranController;
    private LaporanController laporanController;
    private TagihanController tagihanController;
    
    // Main components
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JLabel userInfoLabel;
    private JLabel timeLabel;
    
    // Menu buttons
    private JButton btnDashboard;
    private JButton btnLaporanKeuangan;
    private JButton btnDataSiswa;
    private JButton btnTunggakan;
    private JButton btnStatistik;
    private JButton btnLogout;
    
    // Content panels (untuk switch content)
    private JPanel dashboardContentPanel;
    private JPanel laporanContentPanel;
    private JPanel siswaContentPanel;
    private JPanel tunggakanContentPanel;
    private JPanel statistikContentPanel;
    
    // Timer untuk update waktu
    private Timer timeUpdateTimer;
    // Timer untuk update data secara berkala
    private Timer dataRefreshTimer;
    // Timer untuk live search di student panel
    private Timer studentSearchTimer;
    
    /**
     * Constructor - Setup dashboard untuk Kepsek
     */
    public DashboardKepsek(User user) {
        this.currentUser = user;
        this.userController = new UserController();
        this.siswaController = new SiswaController();
        this.pembayaranController = new PembayaranController();
        this.laporanController = new LaporanController();
        this.tagihanController = new TagihanController();

        initComponents();
        setupUI();
        setupEventHandlers();
        startTimeUpdate();
        startDataRefreshTimer(); // Start auto-refresh timer
        showDashboardContent(); // Default show dashboard

        System.out.println("🏫 Dashboard Kepsek loaded untuk: " + user.getNamaLengkap());
    }
    
    /**
     * Method untuk inisialisasi komponen UI
     */
    private void initComponents() {
        // Set properties window
        setTitle("SPP Payment System - Dashboard Kepala Sekolah");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Create main panels
        createHeaderPanel();
        createSidebarPanel();
        createContentPanel();
        
        // Setup main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Method untuk membuat header panel
     */
    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94)); // Dark blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        // Title di kiri
        JLabel titleLabel = new JLabel("📊 DASHBOARD KEPALA SEKOLAH");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // User info & time di kanan
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        rightPanel.setOpaque(false);
        
        userInfoLabel = new JLabel("👤 " + currentUser.getNamaLengkap() + " (" + currentUser.getRole() + ")");
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userInfoLabel.setForeground(Color.WHITE);
        userInfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(236, 240, 241));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        rightPanel.add(userInfoLabel);
        rightPanel.add(timeLabel);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }
    
    /**
     * Method untuk membuat sidebar menu
     */
    private void createSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(44, 62, 80)); // Darker blue
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Menu buttons
        btnDashboard = createMenuButton("🏠 Dashboard", "Halaman utama dengan ringkasan");
        btnLaporanKeuangan = createMenuButton("📈 Laporan Keuangan", "Laporan pemasukan dan pengeluaran");
        btnDataSiswa = createMenuButton("👥 Data Siswa", "Daftar siswa dan status pembayaran");
        btnTunggakan = createMenuButton("⚠️ Monitor Tunggakan", "Siswa dengan tunggakan SPP");
        btnStatistik = createMenuButton("📊 Statistik", "Grafik dan analisis data");
        
        // Spacer
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(createSeparator());
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        btnLogout = createMenuButton("🚪 Logout", "Keluar dari aplikasi");
        btnLogout.setBackground(new Color(231, 76, 60)); // Red color
        
        // Set dashboard as active by default
        setActiveButton(btnDashboard);
    }
    
    /**
     * Method untuk membuat menu button
     */
    private JButton createMenuButton(String text, String tooltip) {
        JButton button = new JButton("<html><div style='text-align: left; padding: 5px;'>" + text + "</div></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(230, 50));
        button.setToolTipText(tooltip);
        button.addActionListener(this);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground() != new Color(41, 128, 185)) { // Not active
                    button.setBackground(new Color(58, 80, 107));
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground() != new Color(41, 128, 185)) { // Not active
                    button.setBackground(new Color(52, 73, 94));
                }
            }
        });
        
        sidebarPanel.add(button);
        sidebarPanel.add(Box.createVerticalStrut(5));
        
        return button;
    }
    
    /**
     * Method untuk membuat separator
     */
    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(127, 140, 141));
        separator.setMaximumSize(new Dimension(200, 1));
        return separator;
    }
    
    /**
     * Method untuk membuat content panel
     */
    private void createContentPanel() {
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(236, 240, 241)); // Light gray
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create different content panels
        createDashboardContent();
        createLaporanContent();
        createSiswaContent();
        createTunggakanContent();
        createStatistikContent();
        
        // Add to CardLayout
        contentPanel.add(dashboardContentPanel, "DASHBOARD");
        contentPanel.add(laporanContentPanel, "LAPORAN");
        contentPanel.add(siswaContentPanel, "SISWA");
        contentPanel.add(tunggakanContentPanel, "TUNGGAKAN");
        contentPanel.add(statistikContentPanel, "STATISTIK");
    }
    
    /**
     * Method untuk membuat dashboard content (halaman utama)
     */
    private void createDashboardContent() {
        dashboardContentPanel = new JPanel(new BorderLayout());
        dashboardContentPanel.setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Stats cards panel - use grid layout to show summary information
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);

        // Create stats cards with real-time data
        updateDashboardStats(statsPanel);

        // Recent activities panel
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBackground(Color.WHITE);
        recentPanel.setBorder(BorderFactory.createTitledBorder("Informasi Dashboard"));
        recentPanel.setPreferredSize(new Dimension(0, 200));

        String[] dashboardInfo = {
            "🔹 Selamat datang di Dashboard Kepala Sekolah",
            "🔹 Gunakan menu di sisi kiri untuk mengakses fitur",
            "🔹 Lihat laporan keuangan dan data siswa secara real-time",
            "🔹 Monitor siswa dengan tunggakan SPP",
            "🔹 Semua data diperbarui secara otomatis"
        };

        JList<String> infoList = new JList<>(dashboardInfo);
        infoList.setFont(new Font("Arial", Font.PLAIN, 12));
        infoList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        recentPanel.add(new JScrollPane(infoList), BorderLayout.CENTER);

        // Layout dashboard content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.CENTER);
        centerPanel.add(recentPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(centerPanel, BorderLayout.CENTER);

        dashboardContentPanel.add(topPanel, BorderLayout.NORTH);
    }
    
    /**
     * Method untuk membuat stats card
     */
    private JPanel createStatsCard(String title, String value, String description, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Icon panel
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("●");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 30));
        iconLabel.setForeground(color);
        iconPanel.add(iconLabel);

        // Text panel
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(127, 140, 141));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(new Color(52, 73, 94));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(new Color(149, 165, 166));

        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        textPanel.add(descLabel);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Method untuk update dashboard stats dengan data real-time
     */
    private void updateDashboardStats(JPanel statsPanel) {
        statsPanel.removeAll();

        try {
            // Get real-time statistics using direct database queries to ensure fresh connections
            String[] tagihanStats = getTagihanStatsFromDatabase();
            String[] pembayaranStats = getPembayaranStatsFromDatabase();

            // Total siswa (from tagihan stats)
            String totalSiswa = tagihanStats[0];
            // Siswa tunggakan (from tagihan stats)
            String siswaTunggakan = tagihanStats[2];
            // Siswa sudah bayar (from tagihan stats)
            int totalSiswaNum = Integer.parseInt(tagihanStats[0]);
            int sudahBayarNum = Integer.parseInt(tagihanStats[1]);
            String siswaSudahBayar = String.valueOf(sudahBayarNum);
            // Tingkat pembayaran (from tagihan stats)
            String tingkatPembayaran = tagihanStats[3];
            // Pemasukan bulan ini (from pembayaran stats)
            String pemasukanBulanIni = pembayaranStats[1];

            // Create stats cards with real-time data
            statsPanel.add(createStatsCard("👥 Total Siswa", totalSiswa, "siswa terdaftar", new Color(52, 152, 219)));
            statsPanel.add(createStatsCard("💰 Pemasukan Bulan Ini", pemasukanBulanIni, "dari pembayaran SPP", new Color(46, 204, 113)));
            statsPanel.add(createStatsCard("⚠️ Siswa Tunggakan", siswaTunggakan, "siswa belum bayar", new Color(231, 76, 60)));
            statsPanel.add(createStatsCard("📈 Tingkat Pembayaran", tingkatPembayaran, "persentase pembayaran", new Color(155, 89, 182)));
        } catch (Exception e) {
            System.err.println("Error updating dashboard stats: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            // Add error placeholders
            statsPanel.add(createStatsCard("👥 Total Siswa", "Error", "Gagal memuat data", new Color(52, 152, 219)));
            statsPanel.add(createStatsCard("💰 Pemasukan Bulan Ini", "Error", "Gagal memuat data", new Color(46, 204, 113)));
            statsPanel.add(createStatsCard("⚠️ Siswa Tunggakan", "Error", "Gagal memuat data", new Color(231, 76, 60)));
            statsPanel.add(createStatsCard("📈 Tingkat Pembayaran", "Error", "Gagal memuat data", new Color(155, 89, 182)));
        }

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    /**
     * Helper method to get tagihan stats directly from database with fresh connection
     */
    private String[] getTagihanStatsFromDatabase() {
        String[] ringkasan = new String[4];

        try (java.sql.Connection conn = aplikasi.pembayaran.spp.model.Koneksi.getConnection()) {
            // Total siswa aktif
            String sql1 = "SELECT COUNT(*) FROM siswa WHERE status_siswa = 'Aktif'";
            try (java.sql.PreparedStatement pstmt1 = conn.prepareStatement(sql1);
                 java.sql.ResultSet rs1 = pstmt1.executeQuery()) {
                rs1.next();
                ringkasan[0] = String.valueOf(rs1.getInt(1));
            }

            // Siswa sudah bayar bulan ini
            String sql2 = "SELECT COUNT(DISTINCT nis_siswa) FROM pembayaran " +
                    "WHERE YEAR(tanggal_bayar) = YEAR(CURDATE()) " +
                    "AND MONTH(tanggal_bayar) = MONTH(CURDATE()) " +
                    "AND status_pembayaran = 'Lunas'";
            try (java.sql.PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                 java.sql.ResultSet rs2 = pstmt2.executeQuery()) {
                rs2.next();
                ringkasan[1] = String.valueOf(rs2.getInt(1));
            }

            // Siswa belum bayar
            int totalSiswa = Integer.parseInt(ringkasan[0]);
            int sudahBayar = Integer.parseInt(ringkasan[1]);
            ringkasan[2] = String.valueOf(totalSiswa - sudahBayar);

            // Persentase pembayaran
            if (totalSiswa > 0) {
                double persentase = (double) sudahBayar / totalSiswa * 100;
                ringkasan[3] = String.format("%.1f%%", persentase);
            } else {
                ringkasan[3] = "0%";
            }

        } catch (java.sql.SQLException e) {
            System.out.println("❌ Error get ringkasan tagihan: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            ringkasan[0] = "0"; ringkasan[1] = "0"; ringkasan[2] = "0"; ringkasan[3] = "0%";
        }

        return ringkasan;
    }

    /**
     * Helper method to get pembayaran stats directly from database with fresh connection
     */
    private String[] getPembayaranStatsFromDatabase() {
        String[] stats = new String[4];

        try (java.sql.Connection conn = aplikasi.pembayaran.spp.model.Koneksi.getConnection()) {
            // Total transaksi hari ini
            String sqlHariIni = "SELECT COUNT(*) FROM pembayaran WHERE DATE(tanggal_bayar) = CURDATE()";
            try (java.sql.PreparedStatement pstmt1 = conn.prepareStatement(sqlHariIni);
                 java.sql.ResultSet rs1 = pstmt1.executeQuery()) {
                rs1.next();
                stats[0] = String.valueOf(rs1.getInt(1));
            }

            // Total pemasukan hari ini
            String sqlPemasukan = "SELECT COALESCE(SUM(jumlah_bayar), 0) FROM pembayaran WHERE DATE(tanggal_bayar) = CURDATE()";
            try (java.sql.PreparedStatement pstmt2 = conn.prepareStatement(sqlPemasukan);
                 java.sql.ResultSet rs2 = pstmt2.executeQuery()) {
                rs2.next();
                stats[1] = String.format("Rp %.0f", rs2.getDouble(1));
            }

            // Total transaksi bulan ini
            String sqlBulanIni = "SELECT COUNT(*) FROM pembayaran WHERE YEAR(tanggal_bayar) = YEAR(CURDATE()) AND MONTH(tanggal_bayar) = MONTH(CURDATE())";
            try (java.sql.PreparedStatement pstmt3 = conn.prepareStatement(sqlBulanIni);
                 java.sql.ResultSet rs3 = pstmt3.executeQuery()) {
                rs3.next();
                stats[2] = String.valueOf(rs3.getInt(1));
            }

            // Transaksi pending/cicilan
            String sqlPending = "SELECT COUNT(*) FROM pembayaran WHERE status_pembayaran IN ('Belum Lunas', 'Cicilan')";
            try (java.sql.PreparedStatement pstmt4 = conn.prepareStatement(sqlPending);
                 java.sql.ResultSet rs4 = pstmt4.executeQuery()) {
                rs4.next();
                stats[3] = String.valueOf(rs4.getInt(1));
            }

        } catch (java.sql.SQLException e) {
            System.err.println("❌ Error get statistik pembayaran: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            stats[0] = "0"; stats[1] = "Rp 0"; stats[2] = "0"; stats[3] = "0";
        }

        return stats;
    }

    
    /**
     * Method untuk membuat laporan content
     */
    private void createLaporanContent() {
        laporanContentPanel = new JPanel(new BorderLayout());
        laporanContentPanel.setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("📈 Laporan Keuangan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Laporan"));

        filterPanel.add(new JLabel("Periode:"));
        JComboBox<String> periodCombo = new JComboBox<>();
        // Load available periods from database using direct access
        List<String> availablePeriods = getAvailablePeriodsFromDatabase();
        for (String period : availablePeriods) {
            periodCombo.addItem(period);
        }
        periodCombo.addItem("Semua");
        filterPanel.add(periodCombo);

        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Jenis:"));
        JComboBox<String> jenisCombo = new JComboBox<>(new String[]{"SPP", "Semua Pemasukan"});
        filterPanel.add(jenisCombo);

        JButton btnGenerate = new JButton("Generate Laporan");
        btnGenerate.setBackground(new Color(52, 152, 219));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        filterPanel.add(btnGenerate);

        // Create table with empty model first
        String[] columns = {"Bulan", "Total Pemasukan", "Total Siswa", "Tingkat Bayar", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only untuk Kepsek
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);

        // Add action listener to generate button
        btnGenerate.addActionListener(e -> {
            String selectedPeriod = (String) periodCombo.getSelectedItem();
            String selectedJenis = (String) jenisCombo.getSelectedItem();

            // Update table with real-time data
            updateFinancialReport(table, selectedPeriod, selectedJenis);
        });

        // Initialize with all data
        updateFinancialReport(table, "Semua", "SPP");

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        laporanContentPanel.add(topPanel, BorderLayout.NORTH);
        laporanContentPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Method untuk update laporan keuangan berdasarkan filter
     */
    private void updateFinancialReport(JTable table, String selectedPeriod, String selectedJenis) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0); // Clear existing data

        try {
            if ("Semua".equals(selectedPeriod)) {
                // Get all available periods using direct database access
                List<String> availablePeriods = getAvailablePeriodsFromDatabase();
                for (String period : availablePeriods) {
                    // Get report data for each period with fresh connection
                    Map<String, Object> laporan = getLaporanByPeriodeFromDatabase(period);

                    // Only add if there's data for this period
                    if (laporan.get("total_transaksi") != null) {
                        int totalTransaksi = (Integer) laporan.get("total_transaksi");
                        double totalPemasukan = laporan.get("total_pemasukan") != null ? (Double) laporan.get("total_pemasukan") : 0.0;
                        String formattedPemasukan = "Rp " + String.format("%.0f", totalPemasukan);

                        // Calculate student count and payment rate (for demo purposes, use a simple calculation)
                        int totalSiswa = totalTransaksi > 0 ? totalTransaksi : 0;
                        String status = totalPemasukan > 0 ? "✅ Lunas" : "⏳ Proses";

                        tableModel.addRow(new Object[]{period, formattedPemasukan, totalSiswa, "100%", status});
                    }
                }
            } else {
                // Get specific period with fresh connection
                Map<String, Object> laporan = getLaporanByPeriodeFromDatabase(selectedPeriod);

                if (laporan.get("total_transaksi") != null) {
                    int totalTransaksi = (Integer) laporan.get("total_transaksi");
                    double totalPemasukan = laporan.get("total_pemasukan") != null ? (Double) laporan.get("total_pemasukan") : 0.0;
                    String formattedPemasukan = "Rp " + String.format("%.0f", totalPemasukan);

                    // Calculate student count and payment rate (for demo purposes, use a simple calculation)
                    int totalSiswa = totalTransaksi > 0 ? totalTransaksi : 0;
                    String status = totalPemasukan > 0 ? "✅ Lunas" : "⏳ Proses";

                    tableModel.addRow(new Object[]{selectedPeriod, formattedPemasukan, totalSiswa, "100%", status});
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating financial report: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            JOptionPane.showMessageDialog(this, "Error loading financial report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to get available periods directly from database with fresh connection
     */
    private List<String> getAvailablePeriodsFromDatabase() {
        List<String> periods = new ArrayList<>();
        String query = "SELECT DISTINCT bulan_tahun FROM pembayaran ORDER BY bulan_tahun DESC";

        try (java.sql.Connection conn = aplikasi.pembayaran.spp.model.Koneksi.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                periods.add(rs.getString("bulan_tahun"));
            }

        } catch (java.sql.SQLException e) {
            System.err.println("Error getting periods: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }

        return periods;
    }

    /**
     * Helper method to get laporan by periode directly from database with fresh connection
     */
    private Map<String, Object> getLaporanByPeriodeFromDatabase(String bulanTahun) {
        Map<String, Object> laporan = new HashMap<>();
        String query = "SELECT " +
                      "COUNT(*) as total_transaksi, " +
                      "SUM(jumlah_bayar) as total_pemasukan, " +
                      "AVG(jumlah_bayar) as rata_rata, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Cash' THEN jumlah_bayar ELSE 0 END) as total_cash, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Transfer' THEN jumlah_bayar ELSE 0 END) as total_transfer, " +
                      "SUM(CASE WHEN metode_pembayaran = 'Kartu Debit' THEN jumlah_bayar ELSE 0 END) as total_kartu, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Cash' THEN 1 END) as jumlah_cash, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Transfer' THEN 1 END) as jumlah_transfer, " +
                      "COUNT(CASE WHEN metode_pembayaran = 'Kartu Debit' THEN 1 END) as jumlah_kartu, " +
                      "COUNT(CASE WHEN status_pembayaran = 'Lunas' THEN 1 END) as jumlah_lunas " +
                      "FROM pembayaran " +
                      "WHERE bulan_tahun = ?";

        try (java.sql.Connection conn = aplikasi.pembayaran.spp.model.Koneksi.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bulanTahun);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    laporan.put("total_transaksi", rs.getInt("total_transaksi"));
                    laporan.put("total_pemasukan", rs.getDouble("total_pemasukan"));
                    laporan.put("rata_rata", rs.getDouble("rata_rata"));
                    laporan.put("total_cash", rs.getDouble("total_cash"));
                    laporan.put("total_transfer", rs.getDouble("total_transfer"));
                    laporan.put("total_kartu", rs.getDouble("total_kartu"));
                    laporan.put("jumlah_cash", rs.getInt("jumlah_cash"));
                    laporan.put("jumlah_transfer", rs.getInt("jumlah_transfer"));
                    laporan.put("jumlah_kartu", rs.getInt("jumlah_kartu"));
                    laporan.put("jumlah_lunas", rs.getInt("jumlah_lunas"));
                }
            }

        } catch (java.sql.SQLException e) {
            System.err.println("Error getting laporan: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }

        return laporan;
    }
    
    /**
     * Method untuk membuat siswa content
     */
    private void createSiswaContent() {
        siswaContentPanel = new JPanel(new BorderLayout());
        siswaContentPanel.setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("👥 Data Siswa (Read-Only)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Pencarian Siswa"));

        searchPanel.add(new JLabel("Cari:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);

        // Remove the search button since we're implementing live search
        // JButton btnSearch = new JButton("🔍 Cari");
        // btnSearch.setBackground(new Color(52, 152, 219));
        // btnSearch.setForeground(Color.WHITE);
        // searchPanel.add(btnSearch);

        // Table siswa with empty model first (removed Tunggakan column)
        String[] columns = {"NIS", "Nama Lengkap", "Kelas", "SPP/Bulan", "Status Bayar"};
        DefaultTableModel siswaTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only untuk Kepsek
            }
        };

        JTable siswaTable = new JTable(siswaTableModel);
        siswaTable.setFont(new Font("Arial", Font.PLAIN, 12));
        siswaTable.setRowHeight(25);
        siswaTable.getTableHeader().setBackground(new Color(52, 73, 94));
        siswaTable.getTableHeader().setForeground(Color.WHITE);

        // Add live search functionality - search as user types with a small delay
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (studentSearchTimer != null) studentSearchTimer.stop();
                studentSearchTimer = new javax.swing.Timer(300, e1 -> performSearch(searchField.getText().trim(), siswaTable));
                studentSearchTimer.setRepeats(false);
                studentSearchTimer.start();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (studentSearchTimer != null) studentSearchTimer.stop();
                studentSearchTimer = new javax.swing.Timer(300, e1 -> performSearch(searchField.getText().trim(), siswaTable));
                studentSearchTimer.setRepeats(false);
                studentSearchTimer.start();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // Plain text components don't fire these events
            }
        });

        // Initial load of all students
        updateSiswaTable(siswaTable, "");

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        siswaContentPanel.add(topPanel, BorderLayout.NORTH);
        siswaContentPanel.add(new JScrollPane(siswaTable), BorderLayout.CENTER);
    }

    /**
     * Helper method to perform search with delay
     */
    private void performSearch(String searchTerm, JTable siswaTable) {
        updateSiswaTable(siswaTable, searchTerm);
    }

    /**
     * Method untuk update tabel siswa dengan data real-time
     */
    private void updateSiswaTable(JTable table, String searchTerm) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0); // Clear existing data

        try {
            // Get all students with direct database access to ensure fresh connection
            List<aplikasi.pembayaran.spp.model.Siswa> allSiswa = getAllSiswaFromDatabase();

            for (aplikasi.pembayaran.spp.model.Siswa siswa : allSiswa) {
                // If search term provided, filter results
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    if (!siswa.getNis().toLowerCase().contains(searchTerm.toLowerCase()) &&
                        !siswa.getNamaLengkap().toLowerCase().contains(searchTerm.toLowerCase()) &&
                        !siswa.getKelas().toLowerCase().contains(searchTerm.toLowerCase())) {
                        continue;
                    }
                }

                // Create fresh PembayaranController to ensure valid connection
                PembayaranController freshPembayaranController = new PembayaranController();

                // Calculate total payments and determine status
                List<aplikasi.pembayaran.spp.model.Pembayaran> pembayaranList =
                    freshPembayaranController.getPembayaranByNIS(siswa.getNis());

                double totalBayar = 0;
                String statusBayar = "⏳ Belum";

                for (aplikasi.pembayaran.spp.model.Pembayaran pembayaran : pembayaranList) {
                    if ("Lunas".equals(pembayaran.getStatusPembayaran())) {
                        totalBayar += pembayaran.getJumlahBayar();
                        statusBayar = "✅ Lunas";
                    } else if ("Cicilan".equals(pembayaran.getStatusPembayaran())) {
                        totalBayar += pembayaran.getJumlahBayar();
                        statusBayar = "⚠️ Cicilan";
                    }
                }

                // Format currency values
                String sppPerBulan = "Rp " + String.format("%.0f", siswa.getNominalSPP());

                tableModel.addRow(new Object[]{
                    siswa.getNis(),
                    siswa.getNamaLengkap(),
                    siswa.getKelas(),
                    sppPerBulan,
                    statusBayar
                });
            }
        } catch (Exception e) {
            System.err.println("Error updating student table: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            JOptionPane.showMessageDialog(this, "Error loading student data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to get all students directly from database with fresh connection
     */
    private List<aplikasi.pembayaran.spp.model.Siswa> getAllSiswaFromDatabase() {
        List<aplikasi.pembayaran.spp.model.Siswa> list = new ArrayList<>();
        String sql = "SELECT * FROM siswa";

        try (java.sql.Connection conn = aplikasi.pembayaran.spp.model.Koneksi.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                aplikasi.pembayaran.spp.model.Siswa siswa = new aplikasi.pembayaran.spp.model.Siswa(
                        rs.getString("nis"),
                        rs.getString("nama_lengkap"),
                        rs.getString("kelas"),
                        rs.getString("tahun_ajaran"),
                        rs.getString("no_telepon"),
                        rs.getString("alamat"),
                        rs.getDouble("nominal_spp"),
                        0, // total_potongan is always 0 since feature is removed
                        rs.getString("status_siswa"),
                        rs.getString("nama_ortu")
                );
                list.add(siswa);
            }

        } catch (java.sql.SQLException e) {
            System.out.println("❌ Error get all siswa: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
        return list;
    }
    
    /**
     * Method untuk membuat tunggakan content
     */
    private void createTunggakanContent() {
        tunggakanContentPanel = new JPanel(new BorderLayout());
        tunggakanContentPanel.setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("⚠️ Monitor Tunggakan SPP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(231, 76, 60)); // Red color
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Alert panel
        JPanel alertPanel = new JPanel(new BorderLayout());
        alertPanel.setBackground(new Color(231, 76, 60, 50)); // Light red
        alertPanel.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 2));

        // Table tunggakan with empty model first
        String[] columns = {"NIS", "Nama Siswa", "Kelas", "Bulan Tunggak", "Jumlah Tunggakan"};
        DefaultTableModel tunggakanTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tunggakanTable = new JTable(tunggakanTableModel);
        tunggakanTable.setFont(new Font("Arial", Font.PLAIN, 12));
        tunggakanTable.setRowHeight(25);
        tunggakanTable.getTableHeader().setBackground(new Color(231, 76, 60));
        tunggakanTable.getTableHeader().setForeground(Color.WHITE);
        tunggakanTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Load real-time data
        updateTunggakanTable(tunggakanTable, alertPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(alertPanel, BorderLayout.CENTER);

        tunggakanContentPanel.add(topPanel, BorderLayout.NORTH);
        tunggakanContentPanel.add(new JScrollPane(tunggakanTable), BorderLayout.CENTER);
    }

    /**
     * Method untuk update tabel tunggakan dengan data real-time
     */
    private void updateTunggakanTable(JTable table, JPanel alertPanel) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0); // Clear existing data

        try {
            // Instead of using TagihanController, we'll query directly to ensure fresh connection
            // This bypasses the static connection issue in the controller
            List<aplikasi.pembayaran.spp.model.Tagihan> tunggakanList = getTunggakanListFromDatabase();

            // Update alert panel with count of students with overdue payments
            String alertText = "  ⚠️ Terdapat " + tunggakanList.size() + " siswa dengan tunggakan SPP!";
            alertPanel.removeAll();
            alertPanel.add(new JLabel(alertText), BorderLayout.CENTER);
            alertPanel.revalidate();
            alertPanel.repaint();

            // Add data to table
            for (aplikasi.pembayaran.spp.model.Tagihan tagihan : tunggakanList) {
                // Format the data to display
                String jumlahTunggakan = "Rp " + String.format("%.0f", tagihan.getSisaTagihan());

                // Get months the student is overdue for (this would require additional logic to determine the specific months)
                // For now, using a placeholder
                String bulanTunggak = "Beberapa Bulan";

                // Create fresh PembayaranController for this operation
                PembayaranController freshPembayaranController = new PembayaranController();

                // Try to get more specific information for the months
                List<aplikasi.pembayaran.spp.model.Pembayaran> pembayaranList =
                    freshPembayaranController.getPembayaranByNIS(tagihan.getNisSiswa());

                if (!pembayaranList.isEmpty()) {
                    // Calculate which months are overdue by checking the payment history
                    StringBuilder bulanTunggakBuilder = new StringBuilder();
                    // This is a simplified approach that would need to be enhanced in a real implementation
                    bulanTunggak = "Beberapa Bulan";
                }

                tableModel.addRow(new Object[]{
                    tagihan.getNisSiswa(),
                    tagihan.getNamaSiswa(),
                    tagihan.getKelas(),
                    bulanTunggak,
                    jumlahTunggakan
                });
            }
        } catch (Exception e) {
            System.err.println("Error updating tunggakan table: " + e.getMessage());
            e.printStackTrace(); // This will help us identify the exact error
            JOptionPane.showMessageDialog(this, "Error loading overdue data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to get overdue list directly from database with fresh connection
     */
    private List<aplikasi.pembayaran.spp.model.Tagihan> getTunggakanListFromDatabase() {
        List<aplikasi.pembayaran.spp.model.Tagihan> tunggakanList = new ArrayList<>();

        String sql = "SELECT " +
                "s.nis, " +
                "s.nama_lengkap, " +
                "s.kelas, " +
                "s.nominal_spp, " +
                "0 as total_potongan, " +
                "s.nominal_spp as harus_bayar, " +
                "COALESCE(SUM(p.jumlah_bayar), 0) as total_bayar, " +
                "(s.nominal_spp - COALESCE(SUM(p.jumlah_bayar), 0)) as sisa_tunggakan, " +
                "COUNT(p.id_transaksi) as jumlah_transaksi " +
                "FROM siswa s " +
                "LEFT JOIN pembayaran p ON s.nis = p.nis_siswa AND p.status_pembayaran IN ('Lunas', 'Cicilan') " +
                "WHERE s.status_siswa = 'Aktif' " +
                "GROUP BY s.nis, s.nama_lengkap, s.kelas, s.nominal_spp " +
                "HAVING sisa_tunggakan > 0 " +
                "ORDER BY sisa_tunggakan DESC";

        try (java.sql.Connection conn = aplikasi.pembayaran.spp.model.Koneksi.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                aplikasi.pembayaran.spp.model.Tagihan tagihan = new aplikasi.pembayaran.spp.model.Tagihan();
                tagihan.setNisSiswa(rs.getString("nis"));
                tagihan.setNamaSiswa(rs.getString("nama_lengkap"));
                tagihan.setKelas(rs.getString("kelas"));
                tagihan.setNominalSPP(rs.getDouble("nominal_spp"));
                tagihan.setPotongan(0); // Potongan feature removed, always 0
                tagihan.setJumlahBayar(rs.getDouble("total_bayar"));
                tagihan.setSisaTagihan(rs.getDouble("sisa_tunggakan"));
                tagihan.setBulanTahun("Tunggakan"); // Mark sebagai tunggakan
                tagihan.setStatusPembayaran("Belum Lunas");

                tunggakanList.add(tagihan);
            }

            System.out.println("✅ Found " + tunggakanList.size() + " siswa dengan tunggakan");

        } catch (java.sql.SQLException e) {
            System.out.println("❌ Error get tunggakan: " + e.getMessage());
            // Print stack trace for debugging
            e.printStackTrace();
        }

        return tunggakanList;
    }
    
    /**
     * Method untuk membuat statistik content
     */
    private void createStatistikContent() {
        statistikContentPanel = new JPanel(new BorderLayout());
        statistikContentPanel.setBackground(new Color(236, 240, 241));
        
        JLabel titleLabel = new JLabel("📊 Statistik & Analisis");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        // Placeholder untuk chart (nanti bisa pake JFreeChart atau chart library lain)
        JPanel chartPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        chartPanel.setBackground(new Color(236, 240, 241));
        
        chartPanel.add(createChartPlaceholder("Grafik Pembayaran Per Bulan", new Color(52, 152, 219)));
        chartPanel.add(createChartPlaceholder("Tingkat Tunggakan Per Kelas", new Color(231, 76, 60)));
        chartPanel.add(createChartPlaceholder("Trend Pemasukan SPP", new Color(46, 204, 113)));
        chartPanel.add(createChartPlaceholder("Distribusi Siswa Per Kelas", new Color(155, 89, 182)));
        
        statistikContentPanel.add(titleLabel, BorderLayout.NORTH);
        statistikContentPanel.add(chartPanel, BorderLayout.CENTER);
    }
    
    /**
     * Method untuk membuat placeholder chart
     */
    private JPanel createChartPlaceholder(String title, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        
        JLabel chartLabel = new JLabel("📈 " + title, SwingConstants.CENTER);
        chartLabel.setFont(new Font("Arial", Font.BOLD, 16));
        chartLabel.setForeground(color);
        
        panel.add(chartLabel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Method untuk setup UI additional properties
     */
    private void setupUI() {
        // Set icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("assets/kepsek_icon.png"));
        } catch (Exception e) {
            // Use default icon
        }
    }
    
    /**
     * Method untuk setup event handlers
     */
    private void setupEventHandlers() {
        // Window closing event
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleLogout();
            }
        });
    }
    
    /**
     * Method untuk start timer update waktu
     */
    private void startTimeUpdate() {
        timeUpdateTimer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            timeLabel.setText("🕐 " + now.format(formatter));
        });
        timeUpdateTimer.start();
    }

    /**
     * Method untuk start timer refresh data
     */
    private void startDataRefreshTimer() {
        dataRefreshTimer = new Timer(30000, e -> { // Refresh every 30 seconds
            refreshCurrentViewData();
        });
        dataRefreshTimer.start();
    }
    
    /**
     * Helper method to ensure the controllers have valid connections
     */
    private void refreshControllerConnections() {
        // For controllers that we're not manually refreshing in each method,
        // we'll keep the instances but acknowledge that they might get new connections
        // The main issue is handled by creating fresh instances in each method call
    }

    /**
     * Method untuk refresh data berdasarkan view yang sedang aktif
     */
    private void refreshCurrentViewData() {
        // Refresh connections to prevent "No operations allowed after connection closed" error
        refreshControllerConnections();

        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        Component currentComponent = null;

        // Find which panel is currently visible
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) {
                currentComponent = comp;
                break;
            }
        }

        try {
            // Update data based on the current view
            if (currentComponent == dashboardContentPanel) {
                // Refresh dashboard stats
                Component[] components = dashboardContentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        Component[] children = panel.getComponents();
                        for (Component child : children) {
                            if (child instanceof JPanel) {
                                JPanel childPanel = (JPanel) child;
                                // Look for the stats panel (2x2 grid layout)
                                if (childPanel.getLayout() instanceof GridLayout) {
                                    GridLayout layout = (GridLayout) childPanel.getLayout();
                                    if (layout.getRows() == 2 && layout.getColumns() == 2) {
                                        updateDashboardStats(childPanel);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (currentComponent == laporanContentPanel) {
                // Refresh financial report data - update with current filter
                Component[] components = laporanContentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        Component[] children = panel.getComponents();
                        for (Component child : children) {
                            if (child instanceof JScrollPane) {
                                JScrollPane scrollPane = (JScrollPane) child;
                                Component view = scrollPane.getViewport().getView();
                                if (view instanceof JTable) {
                                    JTable table = (JTable) view;
                                    // Find the filter panel to get current selections
                                    JPanel topPanel = (JPanel) panel.getComponent(0); // Top panel contains filters
                                    if (topPanel instanceof JPanel) {
                                        Component[] topComponents = topPanel.getComponents();
                                        JComboBox<String> periodCombo = null;
                                        JComboBox<String> jenisCombo = null;

                                        for (Component topComp : topComponents) {
                                            if (topComp instanceof JPanel) {
                                                JPanel filterPanel = (JPanel) topComp;
                                                Component[] filterComponents = filterPanel.getComponents();
                                                for (Component filterComp : filterComponents) {
                                                    if (filterComp instanceof JComboBox) {
                                                        @SuppressWarnings("unchecked")
                                                        JComboBox<String> combo = (JComboBox<String>) filterComp;
                                                        if (periodCombo == null) {
                                                            periodCombo = combo;
                                                        } else if (jenisCombo == null) {
                                                            jenisCombo = combo;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (periodCombo != null && jenisCombo != null) {
                                            String selectedPeriod = (String) periodCombo.getSelectedItem();
                                            String selectedJenis = (String) jenisCombo.getSelectedItem();
                                            updateFinancialReport(table, selectedPeriod, selectedJenis);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            } else if (currentComponent == siswaContentPanel) {
                // Refresh student data - update with current search term
                Component[] components = siswaContentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        Component[] children = panel.getComponents();
                        for (Component child : children) {
                            if (child instanceof JScrollPane) {
                                JScrollPane scrollPane = (JScrollPane) child;
                                Component view = scrollPane.getViewport().getView();
                                if (view instanceof JTable) {
                                    JTable table = (JTable) view;
                                    // Find the search field to get current search term
                                    JPanel topPanel = (JPanel) panel.getComponent(0); // Top panel contains search
                                    if (topPanel instanceof JPanel) {
                                        Component[] topComponents = topPanel.getComponents();
                                        JTextField searchField = null;

                                        for (Component topComp : topComponents) {
                                            if (topComp instanceof JPanel) {
                                                JPanel searchPanel = (JPanel) topComp;
                                                Component[] searchComponents = searchPanel.getComponents();
                                                for (Component searchComp : searchComponents) {
                                                    if (searchComp instanceof JTextField) {
                                                        searchField = (JTextField) searchComp;
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        String searchTerm = searchField != null ? searchField.getText().trim() : "";
                                        updateSiswaTable(table, searchTerm);
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            } else if (currentComponent == tunggakanContentPanel) {
                // Refresh overdue data
                Component[] components = tunggakanContentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        Component[] children = panel.getComponents();
                        for (Component child : children) {
                            if (child instanceof JScrollPane) {
                                JScrollPane scrollPane = (JScrollPane) child;
                                Component view = scrollPane.getViewport().getView();
                                if (view instanceof JTable) {
                                    JTable table = (JTable) view;
                                    // Find the alert panel to update as well
                                    JPanel topPanel = (JPanel) panel.getComponent(0); // Top panel contains title and alert
                                    if (topPanel instanceof JPanel) {
                                        Component[] topComponents = topPanel.getComponents();
                                        JPanel alertPanel = null;

                                        for (Component topComp : topComponents) {
                                            if (topComp instanceof JPanel) {
                                                alertPanel = (JPanel) topComp;
                                                break;
                                            }
                                        }

                                        if (alertPanel != null) {
                                            updateTunggakanTable(table, alertPanel);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during data refresh: " + e.getMessage());
            // Log the stack trace for debugging
            e.printStackTrace();
        }
    }

    /**
     * Method untuk set active button
     */
    private void setActiveButton(JButton activeButton) {
        // Reset all buttons
        JButton[] buttons = {btnDashboard, btnLaporanKeuangan, btnDataSiswa, btnTunggakan, btnStatistik};
        for (JButton btn : buttons) {
            if (btn != btnLogout) {
                btn.setBackground(new Color(52, 73, 94));
            }
        }

        // Set active button
        activeButton.setBackground(new Color(41, 128, 185)); // Blue active color
    }
    
    /**
     * Method untuk switch content panel
     */
    private void showContent(String contentName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, contentName);
    }
    
    // Content switcher methods
    private void showDashboardContent() {
        showContent("DASHBOARD");
        setActiveButton(btnDashboard);
    }
    
    private void showLaporanContent() {
        showContent("LAPORAN");
        setActiveButton(btnLaporanKeuangan);
    }
    
    private void showSiswaContent() {
        showContent("SISWA");
        setActiveButton(btnDataSiswa);
    }
    
    private void showTunggakanContent() {
        showContent("TUNGGAKAN");
        setActiveButton(btnTunggakan);
    }
    
    private void showStatistikContent() {
        showContent("STATISTIK");
        setActiveButton(btnStatistik);
    }
    
    /**
     * Method untuk handle logout
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Stop timer
            if (timeUpdateTimer != null) {
                timeUpdateTimer.stop();
            }
            
            System.out.println("🏫 Kepsek " + currentUser.getNamaLengkap() + " logout");

            // Stop timers
            if (timeUpdateTimer != null) {
                timeUpdateTimer.stop();
            }
            if (dataRefreshTimer != null) {
                dataRefreshTimer.stop();
            }
            if (studentSearchTimer != null) {
                studentSearchTimer.stop();
            }

            // Tutup window dan kembali ke login
            dispose();
            new LoginPage().setVisible(true);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnDashboard) {
            showDashboardContent();
        } else if (source == btnLaporanKeuangan) {
            showLaporanContent();
        } else if (source == btnDataSiswa) {
            showSiswaContent();
        } else if (source == btnTunggakan) {
            showTunggakanContent();
        } else if (source == btnStatistik) {
            showStatistikContent();
        } else if (source == btnLogout) {
            handleLogout();
        }
    }
}