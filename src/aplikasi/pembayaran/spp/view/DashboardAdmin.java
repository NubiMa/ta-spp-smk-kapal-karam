    package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.model.User;
import aplikasi.pembayaran.spp.model.Pembayaran;  // Added for dashboard queries
import aplikasi.pembayaran.spp.controller.UserController;
import aplikasi.pembayaran.spp.controller.SiswaController;
import aplikasi.pembayaran.spp.controller.KelasController;
import aplikasi.pembayaran.spp.model.Koneksi;  // Added for direct DB queries
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.Timer;

/**
 * Dashboard untuk Admin
 * Role: FULL CONTROL keuangan, CRUD transaksi
 * <p>
 * Fitur Admin:
 * - CRUD semua data keuangan
 * - Koreksi dan hapus transaksi yang salah
 * - Generate laporan keuangan
 * - Manage user (create/update/delete)
 * - ✅ Edit data siswa SEKARANG WORKING
 */
public class DashboardAdmin extends JFrame implements ActionListener {
    // User yang sedang login
    public User currentUser;
    // Controllers
    private UserController userController;
    private SiswaController siswaController;
    private KelasController kelasController;
    // Main components
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JLabel userInfoLabel;
    private JLabel timeLabel;
    // Table User
    private JTable userTable;
    private DefaultTableModel userTableModel;
    // 🔥 Label jumlah hasil (dibuat field agar bisa diakses di method lain)
    private JLabel lblUserResultCount;
    // Menu buttons
    private JButton btnDashboard;
    private JButton btnTransaksi;
    private JButton btnLaporanKeuangan;
    private JButton btnManageUser;
    private JButton btnDataSiswa;
    private JButton btnManageKelas;
    private JButton btnLogout;
    // Content panels
    private JPanel dashboardContentPanel;
    private JPanel transaksiContentPanel;
    private JPanel laporanContentPanel;
    private JPanel userContentPanel;
    private JPanel siswaContentPanel;
    // Table references
    private JTable siswaTable;
    private JTable kelasTable;
    // Timer untuk update waktu
    private Timer timeUpdateTimer;
    // Timer untuk live search di transaksi panel
    private Timer transaksiSearchTimer;

    /**
     * Constructor - Setup dashboard untuk Admin
     */
    public DashboardAdmin(User user) {
        this.currentUser = user;
        this.userController = new UserController();
        this.siswaController = new SiswaController();
        this.kelasController = new KelasController();
        initComponents();
        setupUI();
        setupEventHandlers();
        startTimeUpdate();
        showDashboardContent(); // Default show dashboard
        System.out.println("💰 Dashboard Admin loaded untuk: " + user.getNamaLengkap());
    }

    /**
     * Method untuk inisialisasi komponen UI
     */
    private void initComponents() {
        // Set properties window
        setTitle("SPP Payment System - Dashboard Admin");
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
        headerPanel.setBackground(new Color(46, 204, 113)); // Green color untuk admin
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        // Title di kiri
        JLabel titleLabel = new JLabel("💰 DASHBOARD ADMIN");
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
        timeLabel.setForeground(new Color(255, 255, 255, 200));
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
        sidebarPanel.setBackground(new Color(39, 174, 96)); // Darker green
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        // Menu buttons
        btnDashboard = createMenuButton("🏠 Dashboard", "Halaman utama dengan ringkasan");
        btnTransaksi = createMenuButton("💳 Kelola Transaksi", "CRUD transaksi pembayaran SPP");
        btnLaporanKeuangan = createMenuButton("📊 Laporan Keuangan", "Generate dan export laporan");
        btnManageUser = createMenuButton("👥 Kelola User", "CRUD user dan permission");
        btnDataSiswa = createMenuButton("🎓 Data Siswa", "Kelola data siswa");
        btnManageKelas = createMenuButton("🏫 Kelola Kelas", "CRUD data kelas, angkatan, dan nominal SPP");
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
        button.setBackground(new Color(46, 204, 113));
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
                if (!button.getBackground().equals(new Color(22, 160, 133))) { // Not active
                    button.setBackground(new Color(52, 211, 153));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(new Color(22, 160, 133))) { // Not active
                    button.setBackground(new Color(46, 204, 113));
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
        separator.setForeground(new Color(255, 255, 255, 100));
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
        createTransaksiContent();
//        createPotonganContent();
        createLaporanContent();
        createUserContent();
        createSiswaContent();
        // Add to CardLayout
        contentPanel.add(dashboardContentPanel, "DASHBOARD");
        contentPanel.add(transaksiContentPanel, "TRANSAKSI");
        contentPanel.add(laporanContentPanel, "LAPORAN");
        contentPanel.add(userContentPanel, "USER");
        contentPanel.add(siswaContentPanel, "SISWA");
        contentPanel.add(createKelasContentPanel(), "KELAS");
    }

    /**
     * Method untuk membuat dashboard content (halaman utama)
     */
    private void createDashboardContent() {
        dashboardContentPanel = new JPanel(new BorderLayout());
        dashboardContentPanel.setBackground(new Color(236, 240, 241));
        // Title
        JLabel titleLabel = new JLabel("Dashboard Keuangan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(39, 174, 96));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        // Stats cards panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);
        // Create stats cards with placeholders - to be updated with real data
        JPanel saldoCard = createStatsCard("💰 Saldo Kas", "Rp 0", "total saldo sekolah", new Color(46, 204, 113));
        JPanel pemasukanCard = createStatsCard("📈 Pemasukan Hari Ini", "Rp 0", "dari 0 transaksi", new Color(52, 152, 219));
        JPanel totalTransaksiCard = createStatsCard("📊 Total Transaksi", "0", "bulan ini", new Color(155, 89, 182));
        JPanel pendingCard = createStatsCard("⚠️ Transaksi Pending", "0", "perlu review admin", new Color(230, 126, 34));

        statsPanel.add(saldoCard);
        statsPanel.add(pemasukanCard);
        statsPanel.add(totalTransaksiCard);
        statsPanel.add(pendingCard);

        // Load real data for stats cards
        SwingUtilities.invokeLater(() -> {
            updateStatsCards(saldoCard, pemasukanCard, totalTransaksiCard, pendingCard);
        });
        // Recent transactions panel
        JPanel recentTransPanel = new JPanel(new BorderLayout());
        recentTransPanel.setBackground(Color.WHITE);
        recentTransPanel.setBorder(BorderFactory.createTitledBorder("💳 Transaksi Terbaru"));
        recentTransPanel.setPreferredSize(new Dimension(0, 200));
        String[] columns = {"Waktu", "NIS", "Nama Siswa", "Jumlah", "Status"};
        DefaultTableModel recentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table read-only
            }
        };
        JTable recentTable = new JTable(recentTableModel);
        // Load recent transactions data from database
        loadRecentTransactions(recentTableModel);
        recentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        recentTable.setRowHeight(25);
        recentTable.getTableHeader().setBackground(new Color(46, 204, 113));
        recentTable.getTableHeader().setForeground(Color.WHITE);
        recentTransPanel.add(new JScrollPane(recentTable), BorderLayout.CENTER);
        // Layout dashboard content
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setOpaque(false);
        middlePanel.add(statsPanel, BorderLayout.CENTER);
        middlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        dashboardContentPanel.add(topPanel, BorderLayout.NORTH);
        dashboardContentPanel.add(middlePanel, BorderLayout.CENTER);
        dashboardContentPanel.add(recentTransPanel, BorderLayout.SOUTH);
    }

    /**
     * Method to load recent transactions from database
     */
    private void loadRecentTransactions(DefaultTableModel tableModel) {
        try {
            // Clear existing data
            tableModel.setRowCount(0);

            // Load recent transactions from database (last 10 transactions)
            String sql = "SELECT p.id_transaksi, p.tanggal_bayar, p.nis_siswa, s.nama_lengkap, " +
                         "p.jumlah_bayar, p.status_pembayaran " +
                         "FROM pembayaran p " +
                         "JOIN siswa s ON p.nis_siswa = s.nis " +
                         "ORDER BY p.tanggal_bayar DESC " +
                         "LIMIT 10";

            try (java.sql.PreparedStatement stmt = Koneksi.getConnection().prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String waktu = rs.getTimestamp("tanggal_bayar").toString().substring(11, 16); // Extract time HH:MM
                    String nis = rs.getString("nis_siswa");
                    String nama = rs.getString("nama_lengkap");
                    double jumlah = rs.getDouble("jumlah_bayar");
                    String status = rs.getString("status_pembayaran");

                    String statusDisplay;
                    switch (status) {
                        case "Lunas":
                            statusDisplay = "✅ Berhasil";
                            break;
                        case "Cicilan":
                            statusDisplay = "⏳ Cicilan";
                            break;
                        default:
                            statusDisplay = "⚠️ " + status;
                    }

                    Object[] row = {
                        waktu,
                        nis,
                        nama,
                        "Rp " + String.format("%.0f", jumlah),
                        statusDisplay
                    };

                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading recent transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method to update stats cards with real data from database
     */
    private void updateStatsCards(JPanel saldoCard, JPanel pemasukanCard, JPanel totalTransaksiCard, JPanel pendingCard) {
        try {
            // Update saldo kas
            String totalSaldo = getTotalSaldo();
            SwingUtilities.invokeLater(() -> {
                updateCardValue(saldoCard, "Rp " + totalSaldo, "total saldo sekolah");
            });

            // Update pemasukan hari ini
            Object[] todayStats = getPemasukanHariIni();
            SwingUtilities.invokeLater(() -> {
                updateCardValue(pemasukanCard, "Rp " + String.valueOf(todayStats[0]), "dari " + String.valueOf(todayStats[1]) + " transaksi");
            });

            // Update total transaksi bulan ini
            int totalTransaksiBulan = getTotalTransaksiBulanIni();
            SwingUtilities.invokeLater(() -> {
                updateCardValue(totalTransaksiCard, String.valueOf(totalTransaksiBulan), "bulan ini");
            });

            // Update pending transactions
            int pendingTransaksi = getTotalTransaksiPending();
            SwingUtilities.invokeLater(() -> {
                updateCardValue(pendingCard, String.valueOf(pendingTransaksi), "perlu review admin");
            });

        } catch (Exception e) {
            System.err.println("❌ Error updating stats cards: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method to update card value and description
     */
    private void updateCardValue(JPanel card, String value, String description) {
        // Find value label and description label in the card
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] innerComps = panel.getComponents();
                for (int i = 0; i < innerComps.length; i++) {
                    if (innerComps[i] instanceof JLabel) {
                        JLabel label = (JLabel) innerComps[i];
                        // We're looking for the value and description labels
                        // Value is typically the second label (index 1 in a 3-label grid)
                        if (i == 1) {
                            label.setText(value);
                        } else if (i == 2) {
                            label.setText("<html>" + description + "</html>");
                        }
                    }
                }
            }
        }
    }

    /**
     * Method to get total saldo from database
     */
    private String getTotalSaldo() {
        try {
            // This would be calculated from all payments in the system
            String sql = "SELECT SUM(jumlah_bayar) as total_saldo FROM pembayaran WHERE status_pembayaran = 'Lunas'";
            try (java.sql.PreparedStatement stmt = Koneksi.getConnection().prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total_saldo");
                    if (rs.wasNull()) total = 0;
                    return String.format("%.0f", total);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting total saldo: " + e.getMessage());
        }
        return "0";
    }

    /**
     * Method to get pemasukan hari ini
     */
    private Object[] getPemasukanHariIni() {
        try {
            String sql = "SELECT SUM(jumlah_bayar) as total_hari_ini, COUNT(*) as jumlah_transaksi " +
                         "FROM pembayaran WHERE DATE(tanggal_bayar) = CURDATE()";
            try (java.sql.PreparedStatement stmt = Koneksi.getConnection().prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total_hari_ini");
                    if (rs.wasNull()) total = 0;
                    int count = rs.getInt("jumlah_transaksi");
                    if (rs.wasNull()) count = 0;
                    return new Object[]{total, count};
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting pemasukan hari ini: " + e.getMessage());
        }
        return new Object[]{0, 0};
    }

    /**
     * Method to get total transaksi bulan ini
     */
    private int getTotalTransaksiBulanIni() {
        try {
            String sql = "SELECT COUNT(*) as total_transaksi " +
                         "FROM pembayaran WHERE YEAR(tanggal_bayar) = YEAR(CURDATE()) AND MONTH(tanggal_bayar) = MONTH(CURDATE())";
            try (java.sql.PreparedStatement stmt = Koneksi.getConnection().prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_transaksi");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting total transaksi bulan ini: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Method to get total pending transactions
     */
    private int getTotalTransaksiPending() {
        try {
            String sql = "SELECT COUNT(*) as pending_count " +
                         "FROM pembayaran WHERE status_pembayaran = 'Belum Lunas'";
            try (java.sql.PreparedStatement stmt = Koneksi.getConnection().prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("pending_count");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting total pending transaksi: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Method untuk refresh dashboard data (stats & recent transactions)
     */
    public void refreshDashboard() {
        // Update stats cards with fresh data
        JPanel[] cards = getDashboardStatsCards(); // Get the stats card panels
        if (cards != null && cards.length >= 4) {
            updateStatsCards(cards[0], cards[1], cards[2], cards[3]); // saldo, pemasukan, total_transaksi, pending
        }

        // Refresh recent transactions table
        if (this.dashboardContentPanel != null) {
            // We need to get the recent transactions table and reload its data
            // Find the table in the dashboard panel
            SwingUtilities.invokeLater(() -> {
                // Update recent transactions if we can find the table
                Component[] components = dashboardContentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        Component[] innerComponents = panel.getComponents();
                        for (Component innerComp : innerComponents) {
                            if (innerComp instanceof JScrollPane) {
                                JScrollPane scroll = (JScrollPane) innerComp;
                                Component view = scroll.getViewport().getView();
                                if (view instanceof JTable) {
                                    JTable table = (JTable) view;
                                    // Assuming this is the recent transactions table, update it
                                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                                    loadRecentTransactions(model);
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Helper method to get dashboard stats card panels - for refresh purposes
     */
    private JPanel[] getDashboardStatsCards() {
        // For now returning null as this would require complex component traversal
        // A better approach is to store references to these panels as class fields
        return null;
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
     * Method untuk membuat transaksi content (changed to history view)
     */
    private void createTransaksiContent() {
        transaksiContentPanel = new JPanel(new BorderLayout());
        transaksiContentPanel.setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("💳 Histori Transaksi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 204, 113));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Pencarian Transaksi"));

        searchPanel.add(new JLabel("Cari:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);

        // Table transaksi - without Aksi column
        String[] columns = {
            "ID Transaksi", "Tanggal", "NIS", "Nama Siswa", "Bulan SPP",
            "Nominal", "Total Bayar", "Status", "User Input"
        };
        DefaultTableModel transaksiTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are read-only
            }
        };

        JTable transaksiTable = new JTable(transaksiTableModel);
        transaksiTable.setFont(new Font("Arial", Font.PLAIN, 11));
        transaksiTable.setRowHeight(25);
        transaksiTable.getTableHeader().setBackground(new Color(46, 204, 113));
        transaksiTable.getTableHeader().setForeground(Color.WHITE);
        transaksiTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(transaksiTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));

        // Add live search functionality - search as user types with a small delay
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (transaksiSearchTimer != null) transaksiSearchTimer.stop();
                transaksiSearchTimer = new Timer(300, e1 -> performSearch(searchField.getText().trim(), transaksiTableModel));
                transaksiSearchTimer.setRepeats(false);
                transaksiSearchTimer.start();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (transaksiSearchTimer != null) transaksiSearchTimer.stop();
                transaksiSearchTimer = new Timer(300, e1 -> performSearch(searchField.getText().trim(), transaksiTableModel));
                transaksiSearchTimer.setRepeats(false);
                transaksiSearchTimer.start();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // Plain text components don't fire these events
            }
        });

        // Load all data from database initially
        loadAllTransaksiData(transaksiTableModel);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        transaksiContentPanel.add(topPanel, BorderLayout.NORTH);
        transaksiContentPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Helper method to load all transactions from database
     */
    private void loadAllTransaksiData(DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        try {
            String sql = "SELECT p.*, s.nama_lengkap FROM pembayaran p " +
                         "JOIN siswa s ON p.nis_siswa = s.nis " +
                         "ORDER BY p.tanggal_bayar DESC";

            try (java.sql.PreparedStatement stmt = Koneksi.getConnection().prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    // Format the data as needed
                    String statusDisplay = rs.getString("status_pembayaran");
                    if ("Lunas".equals(statusDisplay)) {
                        statusDisplay = "✅ Lunas";
                    } else if ("Cicilan".equals(statusDisplay)) {
                        statusDisplay = "⏳ Cicilan";
                    } else if ("Belum Lunas".equals(statusDisplay)) {
                        statusDisplay = "⚠️ Belum Lunas";
                    } else {
                        statusDisplay = "❓ " + statusDisplay;
                    }

                    Object[] row = {
                        rs.getString("id_transaksi"),
                        rs.getTimestamp("tanggal_bayar").toString(),
                        rs.getString("nis_siswa"),
                        rs.getString("nama_lengkap"),
                        rs.getString("bulan_tahun"),
                        "Rp " + String.format("%.0f", rs.getDouble("nominal_spp")),
                        "Rp " + String.format("%.0f", rs.getDouble("jumlah_bayar")),
                        statusDisplay,
                        rs.getString("user_input")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading transaksi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to perform search with delay
     */
    private void performSearch(String searchTerm, DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        try {
            String sql = "SELECT p.*, s.nama_lengkap FROM pembayaran p " +
                         "JOIN siswa s ON p.nis_siswa = s.nis " +
                         "WHERE p.id_transaksi LIKE ? OR p.nis_siswa LIKE ? OR s.nama_lengkap LIKE ? " +
                         "OR p.bulan_tahun LIKE ? OR p.status_pembayaran LIKE ? " +
                         "ORDER BY p.tanggal_bayar DESC";

            try (java.sql.PreparedStatement stmt = Koneksi.getConnection().prepareStatement(sql)) {
                String searchPattern = "%" + searchTerm + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
                stmt.setString(5, searchPattern);

                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Format the data as needed
                        String statusDisplay = rs.getString("status_pembayaran");
                        if ("Lunas".equals(statusDisplay)) {
                            statusDisplay = "✅ Lunas";
                        } else if ("Cicilan".equals(statusDisplay)) {
                            statusDisplay = "⏳ Cicilan";
                        } else if ("Belum Lunas".equals(statusDisplay)) {
                            statusDisplay = "⚠️ Belum Lunas";
                        } else {
                            statusDisplay = "❓ " + statusDisplay;
                        }

                        Object[] row = {
                            rs.getString("id_transaksi"),
                            rs.getTimestamp("tanggal_bayar").toString(),
                            rs.getString("nis_siswa"),
                            rs.getString("nama_lengkap"),
                            rs.getString("bulan_tahun"),
                            "Rp " + String.format("%.0f", rs.getDouble("nominal_spp")),
                            "Rp " + String.format("%.0f", rs.getDouble("jumlah_bayar")),
                            statusDisplay,
                            rs.getString("user_input")
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error searching transaksi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method untuk membuat laporan content (changed to detailed monthly payments)
     */
    private void createLaporanContent() {
        laporanContentPanel = new JPanel(new BorderLayout());
        laporanContentPanel.setBackground(new Color(236, 240, 241));
        // Title
        JLabel titleLabel = new JLabel("📊 Laporan Keuangan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 152, 219));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Search panel for filtering
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Pencarian Laporan"));

        searchPanel.add(new JLabel("Periode:"));
        JComboBox<String> periodCombo = new JComboBox<>(new String[]{"Semua", "Januari 2024", "Februari 2024", "Maret 2024", "April 2024", "Mei 2024", "Juni 2024"});
        searchPanel.add(periodCombo);

        searchPanel.add(new JLabel("Status:"));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Semua", "Lunas", "Cicilan", "Belum Lunas"});
        searchPanel.add(statusCombo);

        JButton btnSearch = new JButton("🔍 Cari");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(btnSearch);

        // Detailed payments table (read-only view)
        String[] reportColumns = {"ID Transaksi", "Tanggal", "NIS", "Nama Siswa", "Kelas", "Bulan/Tahun", "Nominal SPP", "Jumlah Bayar", "Status", "Metode"};
        DefaultTableModel reportTableModel = new DefaultTableModel(reportColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }
        };
        JTable reportTable = new JTable(reportTableModel);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 12));
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setBackground(new Color(52, 152, 219));
        reportTable.getTableHeader().setForeground(Color.WHITE);

        // Load initial detailed payment data
        loadDetailedPaymentData(reportTableModel);

        JScrollPane tableScrollPane = new JScrollPane(reportTable);

        // Add action listener for search
        btnSearch.addActionListener(e -> {
            loadDetailedPaymentData(reportTableModel); // Reload with current filters
        });

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        laporanContentPanel.add(topPanel, BorderLayout.NORTH);
        laporanContentPanel.add(tableScrollPane, BorderLayout.CENTER);
    }

    /**
     * Helper method to load detailed payment data from database
     */
    private void loadDetailedPaymentData(DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        try {
            // Query to get detailed payment information
            String sql = "SELECT p.*, s.nama_lengkap, s.kelas FROM pembayaran p " +
                         "JOIN siswa s ON p.nis_siswa = s.nis " +
                         "ORDER BY p.tanggal_bayar DESC " +
                         "LIMIT 100"; // Limit to last 100 records for performance

            try (java.sql.PreparedStatement stmt = Koneksi.getConnection().prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String statusDisplay = rs.getString("status_pembayaran");
                    if ("Lunas".equals(statusDisplay)) {
                        statusDisplay = "✅ Lunas";
                    } else if ("Cicilan".equals(statusDisplay)) {
                        statusDisplay = "⏳ Cicilan";
                    } else if ("Belum Lunas".equals(statusDisplay)) {
                        statusDisplay = "⚠️ Belum Lunas";
                    } else {
                        statusDisplay = "❓ " + statusDisplay;
                    }

                    Object[] row = {
                        rs.getString("id_transaksi"),
                        rs.getTimestamp("tanggal_bayar").toString(),
                        rs.getString("nis_siswa"),
                        rs.getString("nama_lengkap"),
                        rs.getString("kelas"),
                        rs.getString("bulan_tahun"),
                        "Rp " + String.format("%.0f", rs.getDouble("nominal_spp")),
                        "Rp " + String.format("%.0f", rs.getDouble("jumlah_bayar")),
                        statusDisplay,
                        rs.getString("metode_pembayaran")
                    };

                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading detailed payment data: " + e.getMessage());
            e.printStackTrace();

            // Add some placeholder data in case of error
            Object[][] reportData = {
                {"TRX20240401001", "2024-04-01 14:30:25", "2024001", "Ahmad Rizky", "XII IPA 1", "Maret 2024", "Rp 150,000", "Rp 150,000", "✅ Lunas", "Cash"},
                {"TRX20240401002", "2024-04-01 14:25:10", "2024002", "Siti Nurhaliza", "XII IPS 2", "Maret 2024", "Rp 150,000", "Rp 75,000", "⏳ Cicilan", "Transfer"},
                {"TRX20240401003", "2024-04-01 14:20:05", "2024003", "Budi Santoso", "XI IPA 1", "Maret 2024", "Rp 125,000", "Rp 125,000", "✅ Lunas", "Kartu Debit"},
                {"TRX20240331001", "2024-03-31 15:45:30", "2024004", "Maya Sari", "X IPS 1", "Maret 2024", "Rp 100,000", "Rp 100,000", "✅ Lunas", "Cash"},
                {"TRX20240330005", "2024-03-30 10:15:22", "2024005", "Dedi Kurniawan", "XI IPA 2", "Maret 2024", "Rp 130,000", "Rp 80,000", "⏳ Cicilan", "Transfer"},
            };

            for (Object[] row : reportData) {
                tableModel.addRow(row);
            }
        }
    }

    // ========================================
    // 🔥 UPDATED: Method refreshUserTable() — Include result count update
    // ========================================
    public void refreshUserTable() {
        if (userTableModel == null) {
            System.err.println("❌ userTableModel is null!");
            return;
        }
        if (currentUser == null) {
            System.err.println("❌ currentUser is null!");
            return;
        }
        List<User> users = userController.getAllUsers();
        userTableModel.setRowCount(0);
        for (User u : users) {
            userTableModel.addRow(new Object[]{
                    u.getUsername(),
                    u.getNamaLengkap(),
                    u.getRole(),
                    u.getNoTelepon(),
                    u.isActive() ? "Aktif" : "Nonaktif"
            });
        }
        // Update result count label
        int total = userTableModel.getRowCount();
        if (lblUserResultCount != null) {
            lblUserResultCount.setText("Total: " + total + " user");
            lblUserResultCount.setForeground(new Color(52, 152, 219));
        }
        System.out.println("🔄 User table refreshed – Total: " + total + " rows");
    }

    // ========================================
    // 🔥 NEW METHOD: applyUserFilter — real-time search & filter
    // ========================================
    private void applyUserFilter(String searchText, String selectedRole, String selectedStatus) {
        System.out.println("🔍 Filtering - Search: '" + searchText + "', Role: " + selectedRole + ", Status: " + selectedStatus);
        List<User> allUsers = userController.getAllUsers();
        userTableModel.setRowCount(0);
        String search = searchText.trim().toLowerCase();
        int count = 0;
        for (User u : allUsers) {
            boolean searchMatch = search.isEmpty() ||
                    u.getUsername().toLowerCase().contains(search) ||
                    u.getNamaLengkap().toLowerCase().contains(search) ||
                    (u.getNoTelepon() != null && u.getNoTelepon().toLowerCase().contains(search));
            boolean roleMatch = "Semua".equals(selectedRole) ||
                    u.getRole().equalsIgnoreCase(selectedRole);
            boolean statusMatch = "Semua".equals(selectedStatus) ||
                    ("Aktif".equals(selectedStatus) && u.isActive()) ||
                    ("Nonaktif".equals(selectedStatus) && !u.isActive());
            if (searchMatch && roleMatch && statusMatch) {
                userTableModel.addRow(new Object[]{
                        u.getUsername(),
                        u.getNamaLengkap(),
                        u.getRole(),
                        u.getNoTelepon(),
                        u.isActive() ? "Aktif" : "Nonaktif"
                });
                count++;
            }
        }
        // Update result label
        if (lblUserResultCount != null) {
            lblUserResultCount.setText("Total: " + count + " user");
            lblUserResultCount.setForeground(count == 0 ? Color.RED : new Color(52, 152, 219));
        }
        System.out.println("✅ Filter applied – " + count + " user(s) shown");
    }

    /**
     * Method untuk membuat user management content — DIPERBARUI DENGAN LIVE SEARCH
     */
    private void createUserContent() {
        userContentPanel = new JPanel(new BorderLayout());
        userContentPanel.setBackground(new Color(236, 240, 241));
        // Title
        JLabel titleLabel = new JLabel("👥 Kelola User & Permission");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(230, 126, 34));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        // Action buttons panel
        JPanel userActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        userActionPanel.setBackground(Color.WHITE);
        userActionPanel.setBorder(BorderFactory.createTitledBorder("⚡ Aksi User"));
        JButton btnTambahUser = createActionButton("➕ Tambah User", new Color(46, 204, 113));
        JButton btnEditUser = createActionButton("✏️ Edit User", new Color(52, 152, 219));
        JButton btnNonaktifkanUser = createActionButton("🔒 Nonaktifkan", new Color(230, 126, 34));
        JButton btnAktifkanUser = createActionButton("✅ Aktifkan Kembali", new Color(46, 204, 113));
        JButton btnHapusUser = createActionButton("🗑️ Hapus Permanen", new Color(231, 76, 60));
        userActionPanel.add(btnTambahUser);
        userActionPanel.add(btnEditUser);
        userActionPanel.add(btnNonaktifkanUser);
        userActionPanel.add(btnAktifkanUser);
        userActionPanel.add(btnHapusUser);
        // 🔥 Filter panel dengan LIVE SEARCH
        JPanel userFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        userFilterPanel.setBackground(Color.WHITE);
        userFilterPanel.setBorder(BorderFactory.createTitledBorder("🔍 Pencarian & Filter User"));
        userFilterPanel.add(new JLabel("Cari:"));
        JTextField txtSearchUser = new JTextField(20);
        txtSearchUser.setToolTipText("Cari berdasarkan username, nama, atau no. telepon");
        userFilterPanel.add(txtSearchUser);
        userFilterPanel.add(new JLabel("Role:"));
        JComboBox<String> roleFilterCombo = new JComboBox<>(new String[]{
                "Semua", "Kepsek", "Admin", "Bendahara", "TU", "Siswa"
        });
        userFilterPanel.add(roleFilterCombo);
        userFilterPanel.add(new JLabel("Status:"));
        JComboBox<String> statusFilterCombo = new JComboBox<>(new String[]{
                "Semua", "Aktif", "Nonaktif"
        });
        userFilterPanel.add(statusFilterCombo);
        JButton btnFilterUser = createActionButton("🔍 Filter", new Color(52, 152, 219));
        JButton btnResetFilter = createActionButton("🔄 Reset", new Color(149, 165, 166));
        userFilterPanel.add(btnFilterUser);
        userFilterPanel.add(btnResetFilter);
        // 🔥 Label jumlah hasil — DIBUAT FIELD CLASS
        lblUserResultCount = new JLabel("Total: 0 user");
        lblUserResultCount.setFont(new Font("Arial", Font.ITALIC, 11));
        lblUserResultCount.setForeground(new Color(52, 152, 219));
        userFilterPanel.add(lblUserResultCount);
        // Table users
        String[] userColumns = {"Username", "Nama Lengkap", "Role", "No. Telepon", "Status"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        userTable.setRowHeight(25);
        userTable.getTableHeader().setBackground(new Color(230, 126, 34));
        userTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setPreferredSize(new Dimension(0, 400));
        // Layout utama
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(userActionPanel, BorderLayout.CENTER);
        topPanel.add(userFilterPanel, BorderLayout.SOUTH);
        userContentPanel.add(topPanel, BorderLayout.NORTH);
        userContentPanel.add(userScrollPane, BorderLayout.CENTER);
        // ========================================
        // 🔥 EVENT HANDLERS — LIVE SEARCH & FILTER
        // ========================================
        // 🔥 Live search (real-time)
        txtSearchUser.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyUserFilter(
                        txtSearchUser.getText(),
                        (String) roleFilterCombo.getSelectedItem(),
                        (String) statusFilterCombo.getSelectedItem()
                );
            }
        });
        // 🔥 Filter manually
        btnFilterUser.addActionListener(e -> {
            applyUserFilter(
                    txtSearchUser.getText(),
                    (String) roleFilterCombo.getSelectedItem(),
                    (String) statusFilterCombo.getSelectedItem()
            );
        });
        // 🔥 Reset semua filter
        btnResetFilter.addActionListener(e -> {
            txtSearchUser.setText("");
            roleFilterCombo.setSelectedIndex(0);
            statusFilterCombo.setSelectedIndex(0);
            refreshUserTable();
        });
        // 🔥 Auto-filter saat role/status berubah
        roleFilterCombo.addActionListener(e -> {
            if ("comboBoxChanged".equals(e.getActionCommand())) {
                applyUserFilter(
                        txtSearchUser.getText(),
                        (String) roleFilterCombo.getSelectedItem(),
                        (String) statusFilterCombo.getSelectedItem()
                );
            }
        });
        statusFilterCombo.addActionListener(e -> {
            if ("comboBoxChanged".equals(e.getActionCommand())) {
                applyUserFilter(
                        txtSearchUser.getText(),
                        (String) roleFilterCombo.getSelectedItem(),
                        (String) statusFilterCombo.getSelectedItem()
                );
            }
        });
        // ========================================
        // 🔥 BUTTON ACTION HANDLERS
        // ========================================
        btnTambahUser.addActionListener(e -> {
            new FormTambahUser(this);
        });
        btnEditUser.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih user yang akan diedit!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) userTableModel.getValueAt(selectedRow, 0);
            User user = userController.getUserByUsername(username);
            if (user != null) {
                new FormEditUser(this, user);
            }
        });
        btnNonaktifkanUser.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih user yang akan dinonaktifkan!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) userTableModel.getValueAt(selectedRow, 0);
            String nama = (String) userTableModel.getValueAt(selectedRow, 1);
            String status = (String) userTableModel.getValueAt(selectedRow, 4);
            if ("Nonaktif".equals(status)) {
                JOptionPane.showMessageDialog(this,
                        "User ini sudah nonaktif. Gunakan tombol 'Aktifkan Kembali'.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Nonaktifkan user ini?\nUsername: " + username + "\nNama: " + nama,
                    "Konfirmasi Nonaktifkan User", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (userController.deactivateUser(username, currentUser.getRole())) {
                    refreshUserTable();
                }
            }
        });
        btnAktifkanUser.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih user yang akan diaktifkan!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) userTableModel.getValueAt(selectedRow, 0);
            String nama = (String) userTableModel.getValueAt(selectedRow, 1);
            String status = (String) userTableModel.getValueAt(selectedRow, 4);
            if ("Aktif".equals(status)) {
                JOptionPane.showMessageDialog(this,
                        "User ini sudah aktif!",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Aktifkan user ini?\nUsername: " + username + "\nNama: " + nama,
                    "Konfirmasi Aktifkan User", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (userController.activateUser(username, currentUser.getRole())) {
                    refreshUserTable();
                }
            }
        });
        btnHapusUser.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih user yang akan dihapus!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) userTableModel.getValueAt(selectedRow, 0);
            if (username.equals(currentUser.getUsername())) {
                JOptionPane.showMessageDialog(this,
                        "Tidak bisa menghapus user yang sedang login!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "⚠️ Hapus PERMANEN user ini?\nUsername: " + username,
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                String input = JOptionPane.showInputDialog(this, "Ketik 'HAPUS' untuk konfirmasi:");
                if ("HAPUS".equals(input)) {
                    if (userController.deleteUser(username, currentUser.getRole())) {
                        refreshUserTable();
                    }
                } else if (input != null) {
                    JOptionPane.showMessageDialog(this, "Batal: Konfirmasi gagal.");
                }
            }
        });
        // Load data awal
        refreshUserTable();
    }

    // Helper: createActionButton (reusable)
    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Method untuk membuat siswa content — ✅ EDIT SISWA FIX
     */
    private void createSiswaContent() {
        siswaContentPanel = new JPanel(new BorderLayout());
        siswaContentPanel.setBackground(new Color(236, 240, 241));
        // Title
        JLabel titleLabel = new JLabel("🎓 Kelola Data Siswa");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        // Action buttons panel
        JPanel siswaActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        siswaActionPanel.setBackground(Color.WHITE);
        siswaActionPanel.setBorder(BorderFactory.createTitledBorder("⚡ Aksi Siswa"));
        JButton btnTambahSiswa = new JButton("➕ Tambah Siswa");
        btnTambahSiswa.setBackground(new Color(46, 204, 113));
        btnTambahSiswa.setForeground(Color.WHITE);
        btnTambahSiswa.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JButton btnEditSiswa = new JButton("✏️ Edit Data Siswa");  // 🔥 INI TOMBOL EDIT
        btnEditSiswa.setBackground(new Color(52, 152, 219));
        btnEditSiswa.setForeground(Color.WHITE);
        btnEditSiswa.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JButton btnHapusSiswa = new JButton("🗑️ Hapus Siswa");
        btnHapusSiswa.setBackground(new Color(231, 76, 60)); // Red color for delete
        btnHapusSiswa.setForeground(Color.WHITE);
        btnHapusSiswa.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JButton btnBuatAkun = new JButton("🔑 Buat Akun Login");
        btnBuatAkun.setBackground(new Color(155, 89, 182)); // Purple color
        btnBuatAkun.setForeground(Color.WHITE);
        btnBuatAkun.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        siswaActionPanel.add(btnTambahSiswa);
        siswaActionPanel.add(btnEditSiswa);
        siswaActionPanel.add(btnHapusSiswa);
        siswaActionPanel.add(btnBuatAkun);

        // Add action listener for delete button
        btnHapusSiswa.addActionListener(e -> hapusSelectedSiswa());

        // Add action listener for create account button
        btnBuatAkun.addActionListener(e -> buatAkunLogin());

        // ✅ TAMBAH SISWA — mode tambah
        btnTambahSiswa.addActionListener(e -> {
            new FormTambahEditSiswa(null, this, currentUser.getRole()); // Use DashboardAdmin as parent reference
        });

        // ✅ EDIT SISWA — mode edit (INI YANG DIPERBAIKI!)
        btnEditSiswa.addActionListener(e -> editSelectedSiswa()); // 🔥 DIPANGGIL SECARA BENAR!

        // Search and filter
        JPanel siswaSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        siswaSearchPanel.setBackground(Color.WHITE);
        siswaSearchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Pencarian & Filter"));
        siswaSearchPanel.add(new JLabel("Cari:"));
        JTextField siswaSearchField = new JTextField(15);
        siswaSearchPanel.add(siswaSearchField);
        siswaSearchPanel.add(new JLabel("Kelas:"));
        JComboBox<String> kelasCombo = new JComboBox<>();
        kelasCombo.addItem("Semua");
        // Populate kelas combo with actual class names from data
        List<String> uniqueKelas = siswaController.getUniqueKelas();
        for (String kelas : uniqueKelas) {
            kelasCombo.addItem(kelas);
        }
        siswaSearchPanel.add(kelasCombo);
        siswaSearchPanel.add(new JLabel("Status:"));
        JComboBox<String> statusSiswaCombo = new JComboBox<>(new String[]{"Semua", "Aktif", "Lulus", "Pindah"});
        siswaSearchPanel.add(statusSiswaCombo);
        JButton btnCariSiswa = new JButton("🔍 Cari");
        btnCariSiswa.setBackground(new Color(52, 152, 219));
        btnCariSiswa.setForeground(Color.WHITE);
        siswaSearchPanel.add(btnCariSiswa);

        // Table siswa
        String[] siswaColumns = {"NIS", "Nama Lengkap", "Kelas", "Tahun Ajaran", "Nominal SPP", "Status", "Login"};
        DefaultTableModel siswaTableModel = new DefaultTableModel(siswaColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.siswaTable = new JTable(siswaTableModel);
        this.siswaTable.setFont(new Font("Arial", Font.PLAIN, 11));
        this.siswaTable.setRowHeight(25);
        this.siswaTable.getTableHeader().setBackground(new Color(52, 73, 94));
        this.siswaTable.getTableHeader().setForeground(Color.WHITE);
        this.siswaTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane siswaScrollPane = new JScrollPane(this.siswaTable);
        siswaScrollPane.setPreferredSize(new Dimension(0, 400));

        // Load data from database
        loadSiswaData(siswaTableModel);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(siswaActionPanel, BorderLayout.CENTER);
        topPanel.add(siswaSearchPanel, BorderLayout.SOUTH);
        siswaContentPanel.add(topPanel, BorderLayout.NORTH);
        siswaContentPanel.add(siswaScrollPane, BorderLayout.CENTER);

        // Add live search functionality - update as user types
        JTextField finalSiswaSearchField = siswaSearchField;
        JComboBox<String> finalKelasCombo = kelasCombo;
        JComboBox<String> finalStatusSiswaCombo = statusSiswaCombo;
        JTable finalSiswaTable = this.siswaTable;

        siswaSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String searchKeyword = finalSiswaSearchField.getText().toLowerCase();
                String selectedKelas = (String) finalKelasCombo.getSelectedItem();
                String selectedStatus = (String) finalStatusSiswaCombo.getSelectedItem();
                loadFilteredSiswaData((DefaultTableModel) finalSiswaTable.getModel(), searchKeyword, selectedKelas, selectedStatus);
            }
        });

        // Also keep the button search for compatibility (will still work)
        btnCariSiswa.addActionListener(e -> {
            String searchKeyword = finalSiswaSearchField.getText().toLowerCase();
            String selectedKelas = (String) finalKelasCombo.getSelectedItem();
            String selectedStatus = (String) finalStatusSiswaCombo.getSelectedItem();
            loadFilteredSiswaData((DefaultTableModel) finalSiswaTable.getModel(), searchKeyword, selectedKelas, selectedStatus);
        });

        // Add listeners to the combo boxes for live filtering by class and status
        finalKelasCombo.addActionListener(e -> {
            String searchKeyword = finalSiswaSearchField.getText().toLowerCase();
            String selectedKelas = (String) finalKelasCombo.getSelectedItem();
            String selectedStatus = (String) finalStatusSiswaCombo.getSelectedItem();
            loadFilteredSiswaData((DefaultTableModel) finalSiswaTable.getModel(), searchKeyword, selectedKelas, selectedStatus);
        });

        finalStatusSiswaCombo.addActionListener(e -> {
            String searchKeyword = finalSiswaSearchField.getText().toLowerCase();
            String selectedKelas = (String) finalKelasCombo.getSelectedItem();
            String selectedStatus = (String) finalStatusSiswaCombo.getSelectedItem();
            loadFilteredSiswaData((DefaultTableModel) finalSiswaTable.getModel(), searchKeyword, selectedKelas, selectedStatus);
        });
    }

    /**
     * ✅ Method untuk edit siswa terpilih — SEKARANG WORK!
     */
    private void editSelectedSiswa() {
        int selectedRow = this.siswaTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Silakan pilih siswa yang ingin diedit terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil NIS dari kolom pertama (index 0)
        Object nisObj = this.siswaTable.getValueAt(selectedRow, 0);
        if (nisObj == null) {
            JOptionPane.showMessageDialog(this,
                    "Data NIS tidak ditemukan pada baris yang dipilih.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nis = nisObj.toString().trim();
        if (nis.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "NIS tidak valid. Silakan refresh data.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ambil data lengkap dari database
        aplikasi.pembayaran.spp.model.Siswa selectedSiswa = siswaController.getSiswaByNis(nis);
        if (selectedSiswa == null) {
            JOptionPane.showMessageDialog(this,
                    "Data siswa dengan NIS '" + nis + "' tidak ditemukan di database!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Buka form EDIT dengan data siswa - Use the current admin dashboard as parent reference
        new FormTambahEditSiswa(selectedSiswa, this, currentUser.getRole()); // This allows for refresh after edit
    }

    /**
     * Method untuk refresh data siswa setelah CRUD
     */
    public void refreshSiswaTable() {
        if (this.siswaTable != null) {
            loadSiswaData((DefaultTableModel) this.siswaTable.getModel());
        }
    }

    /**
     * Method untuk membuat akun login untuk siswa terpilih
     */
    private void buatAkunLogin() {
        int selectedRow = this.siswaTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Silakan pilih siswa yang ingin dibuatkan akun login terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil NIS dari kolom pertama (index 0)
        Object nisObj = this.siswaTable.getValueAt(selectedRow, 0);
        if (nisObj == null) {
            JOptionPane.showMessageDialog(this,
                    "Data NIS tidak ditemukan pada baris yang dipilih.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nis = nisObj.toString().trim();
        if (nis.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "NIS tidak valid. Silakan refresh data.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if user already exists
        if (userController.isUsernameExists(nis)) {
            JOptionPane.showMessageDialog(this,
                    "Akun untuk NIS " + nis + " sudah terdaftar!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Ambil data lengkap dari database
        aplikasi.pembayaran.spp.model.Siswa selectedSiswa = siswaController.getSiswaByNis(nis);
        if (selectedSiswa == null) {
            JOptionPane.showMessageDialog(this,
                    "Data siswa dengan NIS '" + nis + "' tidak ditemukan di database!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create default password (can be changed later)
        String defaultPassword = JOptionPane.showInputDialog(this,
                "Masukkan password default untuk " + selectedSiswa.getNamaLengkap() + " (NIS: " + nis + ")\n\n" +
                "Catatan: Minimal 6 karakter",
                "Set Password Akun Siswa", JOptionPane.QUESTION_MESSAGE);

        if (defaultPassword == null || defaultPassword.isEmpty()) {
            return; // User cancelled
        }

        if (defaultPassword.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password harus minimal 6 karakter!",
                    "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create user account
        aplikasi.pembayaran.spp.model.User newUser = new aplikasi.pembayaran.spp.model.User();
        newUser.setUsername(nis);
        newUser.setPassword(defaultPassword);
        newUser.setRole("Siswa");
        newUser.setNamaLengkap(selectedSiswa.getNamaLengkap());
        newUser.setNoTelepon(selectedSiswa.getNoTelepon());
        newUser.setActive(true);

        boolean success = userController.registerUser(newUser, currentUser.getRole());
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Akun login berhasil dibuat untuk " + selectedSiswa.getNamaLengkap() + " (NIS: " + nis + ")",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            // Refresh the table to show updated login status
            loadSiswaData((DefaultTableModel) this.siswaTable.getModel());
        }
    }

    /**
     * Method untuk refresh data kelas setelah CRUD
     */
    public void refreshKelasTable() {
        if (this.kelasTable != null) {
            loadKelasData((DefaultTableModel) this.kelasTable.getModel());
        }
    }

    /**
     * ✅ Method untuk hapus siswa terpilih
     */
    private void hapusSelectedSiswa() {
        int selectedRow = this.siswaTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Silakan pilih siswa yang ingin dihapus terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil NIS dari kolom pertama (index 0)
        Object nisObj = this.siswaTable.getValueAt(selectedRow, 0);
        if (nisObj == null) {
            JOptionPane.showMessageDialog(this,
                    "Data NIS tidak ditemukan pada baris yang dipilih.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nis = nisObj.toString().trim();
        if (nis.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "NIS tidak valid. Silakan refresh data.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get student name for confirmation message
        Object namaObj = this.siswaTable.getValueAt(selectedRow, 1); // Assuming name is in column 1
        String nama = (namaObj != null) ? namaObj.toString() : "Tidak diketahui";

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus siswa berikut?\n\n" +
                "NIS: " + nis + "\nNama: " + nama + "\n\n" +
                "Data yang dihapus tidak dapat dikembalikan!",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean sukses = siswaController.hapusSiswa(nis);

            if (sukses) {
                JOptionPane.showMessageDialog(this,
                        "Siswa dengan NIS " + nis + " berhasil dihapus!",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                // Reload data to show updated table
                loadSiswaData((DefaultTableModel) this.siswaTable.getModel());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus siswa. Mungkin ada data terkait yang masih aktif.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Method untuk load data siswa ke table
     */
    private void loadSiswaData(DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);
        // Get data from controller
        List<aplikasi.pembayaran.spp.model.Siswa> siswaList = siswaController.getAllSiswa();
        // Add to table
        for (aplikasi.pembayaran.spp.model.Siswa s : siswaList) {
            // Check if user account exists for this student
            String loginStatus = userController.isUsernameExists(s.getNis()) ? "✅ Sudah" : "❌ Belum";
            Object[] rowData = {
                    s.getNis(),
                    s.getNamaLengkap(),
                    s.getKelas(),
                    s.getTahunAjaran(),
                    String.format("Rp %.0f", s.getNominalSPP()),
                    s.getStatusSiswa(),
                    loginStatus
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * Method untuk load data siswa dengan filter ke table
     */
    private void loadFilteredSiswaData(DefaultTableModel tableModel, String searchKeyword, String selectedKelas, String selectedStatus) {
        // Clear existing data
        tableModel.setRowCount(0);
        // Get data from controller
        List<aplikasi.pembayaran.spp.model.Siswa> siswaList = siswaController.getAllSiswa();
        // Apply filters
        for (aplikasi.pembayaran.spp.model.Siswa s : siswaList) {
            boolean matchesSearch = searchKeyword.isEmpty() ||
                    s.getNis().toLowerCase().contains(searchKeyword) ||
                    s.getNamaLengkap().toLowerCase().contains(searchKeyword) ||
                    s.getKelas().toLowerCase().contains(searchKeyword);
            boolean matchesKelas = "Semua".equals(selectedKelas) || s.getKelas().equals(selectedKelas);
            boolean matchesStatus = "Semua".equals(selectedStatus) || s.getStatusSiswa().equals(selectedStatus);
            if (matchesSearch && matchesKelas && matchesStatus) {
                // Check if user account exists for this student
                String loginStatus = userController.isUsernameExists(s.getNis()) ? "✅ Sudah" : "❌ Belum";
                Object[] rowData = {
                        s.getNis(),
                        s.getNamaLengkap(),
                        s.getKelas(),
                        s.getTahunAjaran(),
                        String.format("Rp %.0f", s.getNominalSPP()),
                        s.getStatusSiswa(),
                        loginStatus
                };
                tableModel.addRow(rowData);
            }
        }
    }

    /**
     * Method untuk membuat content panel untuk kelas (sebagai JPanel bukan void)
     */
    private JPanel createKelasContentPanel() {
        JPanel kelasContentPanel = new JPanel(new BorderLayout());
        kelasContentPanel.setBackground(new Color(236, 240, 241));
        // Title
        JLabel titleLabel = new JLabel("🏫 Management Data Kelas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Action buttons panel
        JPanel kelasActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        kelasActionPanel.setBackground(Color.WHITE);
        kelasActionPanel.setBorder(BorderFactory.createTitledBorder("⚡ Aksi Kelas"));

        JButton btnTambahKelas = new JButton("➕ Tambah Kelas");
        btnTambahKelas.setBackground(new Color(46, 204, 113));
        btnTambahKelas.setForeground(Color.WHITE);
        btnTambahKelas.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnTambahKelas.addActionListener(e -> {
            new FormKelas(currentUser.getRole(), this); // Pass 'this' sebagai parent frame
        });

        JButton btnEditKelas = new JButton("✏️ Edit Kelas");
        btnEditKelas.setBackground(new Color(52, 152, 219));
        btnEditKelas.setForeground(Color.WHITE);
        btnEditKelas.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnEditKelas.addActionListener(e -> {
            int selectedRow = this.kelasTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this,
                        "Silakan pilih kelas yang ingin diedit terlebih dahulu!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object kelasNameObj = this.kelasTable.getValueAt(selectedRow, 0);
            Object angkatanObj = this.kelasTable.getValueAt(selectedRow, 1);

            if (kelasNameObj == null || angkatanObj == null) {
                JOptionPane.showMessageDialog(this,
                        "Data kelas tidak ditemukan pada baris yang dipilih.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String kelasName = kelasNameObj.toString().trim();
            String angkatan = angkatanObj.toString().trim();

            List<aplikasi.pembayaran.spp.model.Kelas> allKelas = kelasController.getAllKelas();
            aplikasi.pembayaran.spp.model.Kelas selectedKelas = null;
            for (aplikasi.pembayaran.spp.model.Kelas k : allKelas) {
                if (k.getKelas().equals(kelasName) && k.getAngkatan().equals(angkatan)) {
                    selectedKelas = k;
                    break;
                }
            }

            if (selectedKelas == null) {
                JOptionPane.showMessageDialog(this,
                        "Data kelas '" + kelasName + "' angkatan '" + angkatan + "' tidak ditemukan di database!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 🔥 PENTING: Pass 'this' sebagai parent frame!
            new FormKelas(selectedKelas, currentUser.getRole(), this);
        });

        JButton btnHapusKelas = new JButton("🗑️ Hapus Kelas");
        btnHapusKelas.setBackground(new Color(231, 76, 60));
        btnHapusKelas.setForeground(Color.WHITE);
        btnHapusKelas.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnHapusKelas.addActionListener(e -> {
            // Placeholder for delete functionality
            JOptionPane.showMessageDialog(null, "Hapus Kelas - Coming Soon!");
        });

        kelasActionPanel.add(btnTambahKelas);
        kelasActionPanel.add(btnEditKelas);
        kelasActionPanel.add(btnHapusKelas);

        // Search and filter panel
        JPanel kelasSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        kelasSearchPanel.setBackground(Color.WHITE);
        kelasSearchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Pencarian & Filter"));
        kelasSearchPanel.add(new JLabel("Cari:"));
        JTextField kelasSearchField = new JTextField(15);
        kelasSearchPanel.add(kelasSearchField);
        kelasSearchPanel.add(new JLabel("Angkatan:"));
        JComboBox<String> angkatanCombo = new JComboBox<>(new String[]{"Semua", "2024/2025", "2025/2026", "2026/2027"});
        kelasSearchPanel.add(angkatanCombo);
        JButton btnCariKelas = new JButton("🔍 Cari");
        btnCariKelas.setBackground(new Color(52, 152, 219));
        btnCariKelas.setForeground(Color.WHITE);
        kelasSearchPanel.add(btnCariKelas);

        // Table kelas
        String[] kelasColumns = {"Kelas", "Angkatan", "Nominal SPP"};
        DefaultTableModel kelasTableModel = new DefaultTableModel(kelasColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table read-only, edit via button
            }
        };

        JTable kelasTable = new JTable(kelasTableModel);
        kelasTable.setFont(new Font("Arial", Font.PLAIN, 12));
        kelasTable.setRowHeight(25);
        kelasTable.getTableHeader().setBackground(new Color(52, 73, 94));
        kelasTable.getTableHeader().setForeground(Color.WHITE);
        kelasTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane kelasScrollPane = new JScrollPane(kelasTable);
        kelasScrollPane.setPreferredSize(new Dimension(0, 400));

        // Assign table to class field for external access
        this.kelasTable = kelasTable;

        // Load data from database initially
        loadKelasData(kelasTableModel);

        // Create a method to reload data with filters
        java.util.function.BiConsumer<String, String> reloadKelasData = (searchKeyword, selectedAngkatan) -> {
            loadFilteredKelasData(kelasTableModel, searchKeyword, selectedAngkatan);
        };

        // Add live search functionality
        kelasSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String searchKeyword = kelasSearchField.getText().toLowerCase();
                String selectedAngkatan = (String) angkatanCombo.getSelectedItem();
                reloadKelasData.accept(searchKeyword, selectedAngkatan);
            }
        });

        // Add action listener for search button
        btnCariKelas.addActionListener(e -> {
            String searchKeyword = kelasSearchField.getText().toLowerCase();
            String selectedAngkatan = (String) angkatanCombo.getSelectedItem();
            reloadKelasData.accept(searchKeyword, selectedAngkatan);
        });

        // Add action listener for angkatan filter
        angkatanCombo.addActionListener(e -> {
            String searchKeyword = kelasSearchField.getText().toLowerCase();
            String selectedAngkatan = (String) angkatanCombo.getSelectedItem();
            reloadKelasData.accept(searchKeyword, selectedAngkatan);
        });

        // Update the edit button to work with selected row
//        btnEditKelas.addActionListener(e -> editSelectedKelas());

        // Update the hapus button to work with selected row
        btnHapusKelas.addActionListener(e -> hapusSelectedKelas());

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(kelasActionPanel, BorderLayout.CENTER);
        topPanel.add(kelasSearchPanel, BorderLayout.SOUTH);
        kelasContentPanel.add(topPanel, BorderLayout.NORTH);
        kelasContentPanel.add(kelasScrollPane, BorderLayout.CENTER);

        return kelasContentPanel;
    }

    /**
     * Method untuk load data kelas ke table
     */
    private void loadKelasData(DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get data from controller
        List<aplikasi.pembayaran.spp.model.Kelas> kelasList = kelasController.getAllKelas();

        // Add to table
        for (aplikasi.pembayaran.spp.model.Kelas k : kelasList) {
            Object[] rowData = {
                k.getKelas(),
                k.getAngkatan(),
                String.format("Rp %.0f", k.getNominalSPP())
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * Method untuk load data kelas dengan filter ke table
     */
    private void loadFilteredKelasData(DefaultTableModel tableModel, String searchKeyword, String selectedAngkatan) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get data from controller
        List<aplikasi.pembayaran.spp.model.Kelas> kelasList = kelasController.getAllKelas();

        // Apply filters
        for (aplikasi.pembayaran.spp.model.Kelas k : kelasList) {
            boolean matchesSearch = searchKeyword.isEmpty() ||
                    k.getKelas().toLowerCase().contains(searchKeyword) ||
                    k.getAngkatan().toLowerCase().contains(searchKeyword);
            boolean matchesAngkatan = "Semua".equals(selectedAngkatan) || k.getAngkatan().equals(selectedAngkatan);

            if (matchesSearch && matchesAngkatan) {
                Object[] rowData = {
                    k.getKelas(),
                    k.getAngkatan(),
                    String.format("Rp %.0f", k.getNominalSPP())
                };
                tableModel.addRow(rowData);
            }
        }
    }

    /**
     * Method untuk edit kelas terpilih
     */
    private void editSelectedKelas() {
        int selectedRow = this.kelasTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Silakan pilih kelas yang ingin diedit terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get kelas name from the first column to identify the record
        Object kelasNameObj = this.kelasTable.getValueAt(selectedRow, 0);
        Object angkatanObj = this.kelasTable.getValueAt(selectedRow, 1);

        if (kelasNameObj == null || angkatanObj == null) {
            JOptionPane.showMessageDialog(this,
                    "Data kelas tidak ditemukan pada baris yang dipilih.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String kelasName = kelasNameObj.toString().trim();
        String angkatan = angkatanObj.toString().trim();

        // Find the full class object in the database
        List<aplikasi.pembayaran.spp.model.Kelas> allKelas = kelasController.getAllKelas();
        aplikasi.pembayaran.spp.model.Kelas selectedKelas = null;
        for (aplikasi.pembayaran.spp.model.Kelas k : allKelas) {
            if (k.getKelas().equals(kelasName) && k.getAngkatan().equals(angkatan)) {
                selectedKelas = k;
                break;
            }
        }

        if (selectedKelas == null) {
            JOptionPane.showMessageDialog(this,
                    "Data kelas '" + kelasName + "' angkatan '" + angkatan + "' tidak ditemukan di database!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open the edit form with the class data
        new FormKelas(selectedKelas, currentUser.getRole());
    }

    /**
     * Method untuk hapus kelas terpilih
     */
    private void hapusSelectedKelas() {
        int selectedRow = this.kelasTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Silakan pilih kelas yang ingin dihapus terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get kelas name and angkatan from the visible columns to identify the record
        Object kelasNameObj = this.kelasTable.getValueAt(selectedRow, 0);
        Object angkatanObj = this.kelasTable.getValueAt(selectedRow, 1);

        if (kelasNameObj == null || angkatanObj == null) {
            JOptionPane.showMessageDialog(this,
                    "Data kelas tidak ditemukan pada baris yang dipilih.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String kelasName = kelasNameObj.toString().trim();
        String angkatan = angkatanObj.toString().trim();

        // Use kelasName and angkatan for confirmation message
        String namaKelas = kelasName;

        // Find the full class object to get its ID for deletion
        List<aplikasi.pembayaran.spp.model.Kelas> allKelas = kelasController.getAllKelas();
        aplikasi.pembayaran.spp.model.Kelas classToDelete = null;
        for (aplikasi.pembayaran.spp.model.Kelas k : allKelas) {
            if (k.getKelas().equals(kelasName) && k.getAngkatan().equals(angkatan)) {
                classToDelete = k;
                break;
            }
        }

        if (classToDelete == null) {
            JOptionPane.showMessageDialog(this,
                    "Data kelas '" + kelasName + "' angkatan '" + angkatan + "' tidak ditemukan!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus kelas berikut?\n\n" +
                "Kelas: " + kelasName + "\nAngkatan: " + angkatan + "\n\n" +
                "Data yang dihapus tidak dapat dikembalikan!",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Use the correct delete method that accepts kelas name and angkatan
            boolean sukses = kelasController.deleteKelas(classToDelete.getKelas(), classToDelete.getAngkatan());

            if (sukses) {
                JOptionPane.showMessageDialog(this,
                        "Kelas " + namaKelas + " berhasil dihapus!",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                // Reload data to show updated table
                loadKelasData((DefaultTableModel) this.kelasTable.getModel());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus kelas. Mungkin ada data terkait yang masih aktif.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Method untuk setup UI additional properties
     */
    private void setupUI() {
        // Set icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("assets/bendahara_icon.png"));
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
     * Method untuk set active button
     */
    private void setActiveButton(JButton activeButton) {
        JButton[] buttons = {btnDashboard, btnTransaksi, btnLaporanKeuangan, btnManageUser, btnDataSiswa, btnManageKelas};
        for (JButton btn : buttons) {
            if (btn != btnLogout) {
                btn.setBackground(new Color(46, 204, 113));
            }
        }
        activeButton.setBackground(new Color(22, 160, 133)); // Darker green active color
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

    private void showTransaksiContent() {
        showContent("TRANSAKSI");
        setActiveButton(btnTransaksi);
    }

    private void showLaporanContent() {
        showContent("LAPORAN");
        setActiveButton(btnLaporanKeuangan);
    }

    private void showUserContent() {
        showContent("USER");
        setActiveButton(btnManageUser);
    }

    private void showKelasContent() {
        showContent("KELAS");
        setActiveButton(btnManageKelas);
    }

    private void showSiswaContent() {
        showContent("SISWA");
        setActiveButton(btnDataSiswa);
    }

    /**
     * Method untuk handle logout
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (timeUpdateTimer != null) timeUpdateTimer.stop();
            if (transaksiSearchTimer != null) transaksiSearchTimer.stop();
            System.out.println("💰 Admin " + currentUser.getNamaLengkap() + " logout");
            dispose();
            new LoginPage().setVisible(true);
        }
    }

    /**
     * Action listener untuk button clicks
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDashboard) {
            showDashboardContent();
        } else if (e.getSource() == btnTransaksi) {
            showTransaksiContent();
        } else if (e.getSource() == btnLaporanKeuangan) {
            showLaporanContent();
        } else if (e.getSource() == btnManageUser) {
            showUserContent();
        } else if (e.getSource() == btnDataSiswa) {
            showSiswaContent();
        } else if (e.getSource() == btnManageKelas) {
            showKelasContent();
        } else if (e.getSource() == btnLogout) {
            handleLogout();
        }
    }

    /**
     * Main method untuk testing dashboard
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                System.out.println("⚠️ Tidak bisa set look and feel");
            }
//            User testUser = new User("admin", "admin123", "Admin", "Admin Sistem", "081234567890");
//            new DashboardAdmin(testUser).setVisible(true);
        });
    }
}