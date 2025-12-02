package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.PembayaranController;
import aplikasi.pembayaran.spp.controller.SiswaController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardTU extends JFrame {
    
    private String currentUsername;
    private String currentRole = "TU";
    
    private PembayaranController pembayaranController;
    private SiswaController siswaController;
    
    private JLabel lblTotalSiswa, lblPemasukanBulan, lblSiswaTunggakan, lblTingkatPembayaran;
    private JLabel timeLabel;
    private Timer timeUpdateTimer;
    
    public DashboardTU(String username) {
        this.currentUsername = username;
        this.pembayaranController = new PembayaranController();
        this.siswaController = new SiswaController();
        
        initComponents();
        startTimeUpdate();
        loadStatistik();
        
        setVisible(true);
        System.out.println("📊 Dashboard TU loaded untuk: " + username);
    }
    
    public DashboardTU() {
        this("TU");
    }
    
    private void initComponents() {
        setTitle("SPP Payment System - Dashboard TU");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Background color
        getContentPane().setBackground(new Color(236, 240, 241));
        
        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main Content - Grid Layout
        add(createMainContent(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Left: Title & Subtitle
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Dashboard Tata Usaha");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Sistem Pembayaran SPP");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        
        // Right: User Info & Time
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("👤 " + currentUsername + " (" + currentRole + ")");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(189, 195, 199));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        rightPanel.add(userLabel);
        rightPanel.add(timeLabel);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Stats Cards (Top)
        mainPanel.add(createStatsPanel(), BorderLayout.NORTH);
        
        // Menu Grid (Center)
        mainPanel.add(createMenuGrid(), BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 140));
        
        lblTotalSiswa = new JLabel("0");
        lblPemasukanBulan = new JLabel("Rp 0");
        lblSiswaTunggakan = new JLabel("0");
        lblTingkatPembayaran = new JLabel("0%");
        
        statsPanel.add(createStatCard("Total Siswa", lblTotalSiswa, "siswa aktif", new Color(52, 152, 219), "👥"));
        statsPanel.add(createStatCard("Pemasukan Bulan Ini", lblPemasukanBulan, "total pemasukan", new Color(46, 204, 113), "💰"));
        statsPanel.add(createStatCard("Siswa Tunggakan", lblSiswaTunggakan, "belum bayar", new Color(231, 76, 60), "⚠️"));
        statsPanel.add(createStatCard("Tingkat Pembayaran", lblTingkatPembayaran, "sudah lunas", new Color(155, 89, 182), "📈"));
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, String subtitle, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(50, 50));
        
        // Text Panel
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        textPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(127, 140, 141));
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitle.setForeground(new Color(149, 165, 166));
        
        textPanel.add(lblTitle);
        textPanel.add(valueLabel);
        textPanel.add(lblSubtitle);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createMenuGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setOpaque(false);
        
        // Menu Cards
        gridPanel.add(createMenuCard("💵 Input Pembayaran", "Tambah transaksi pembayaran baru", 
            new Color(46, 204, 113), e -> openFormInputPembayaran()));
        
        gridPanel.add(createMenuCard("👥 Data Siswa", "Kelola data siswa", 
            new Color(52, 152, 219), e -> new FormDataSiswa(currentRole)));
        
        gridPanel.add(createMenuCard("⚠️ Monitor Tunggakan", "Lihat siswa dengan tunggakan", 
            new Color(231, 76, 60), e -> new FormMonitorTunggakan(currentRole)));
        
        gridPanel.add(createMenuCard("📊 Laporan Keuangan", "Generate laporan pembayaran", 
            new Color(155, 89, 182), e -> new FormLaporanKeuangan(currentRole)));
        
        gridPanel.add(createMenuCard("📈 Statistik", "Lihat grafik dan analisis", 
            new Color(241, 196, 15), e -> new FormStatistik(currentRole)));
        
        gridPanel.add(createMenuCard("🚪 Logout", "Keluar dari sistem", 
            new Color(192, 57, 43), e -> handleLogout()));
        
        return gridPanel;
    }
    
    private JPanel createMenuCard(String title, String description, Color color, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 25, 30, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Icon/Title Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(title.substring(0, 2)); // Get emoji
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title.substring(3)); // Get text without emoji
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topPanel.add(iconLabel, BorderLayout.NORTH);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Description
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(127, 140, 141));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 249, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    new EmptyBorder(30, 25, 30, 25)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(30, 25, 30, 25)
                ));
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                action.actionPerformed(null);
            }
        });
        
        card.add(topPanel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void loadStatistik() {
        try {
            String[] stats = pembayaranController.getStatistikPembayaran();
            int totalSiswa = siswaController.getAllSiswa().size();
            
            lblTotalSiswa.setText(String.valueOf(totalSiswa));
            lblPemasukanBulan.setText(stats[1]);
            lblSiswaTunggakan.setText(stats[3]);
            
            if (totalSiswa > 0) {
                int sudahBayar = Integer.parseInt(stats[2]);
                double persentase = (double) sudahBayar / totalSiswa * 100;
                lblTingkatPembayaran.setText(String.format("%.1f%%", persentase));
            }
        } catch (Exception e) {
            System.err.println("Error loading stats: " + e.getMessage());
        }
    }
    
    private void startTimeUpdate() {
        timeUpdateTimer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("⏰ dd/MM/yyyy HH:mm:ss");
            timeLabel.setText(now.format(formatter));
        });
        timeUpdateTimer.start();
    }
    
    private void openFormInputPembayaran() {
        new FormInputPembayaran(currentUsername, currentRole, this);
    }
    
    public void refreshDashboard() {
        loadStatistik();
    }
    
    private void showComingSoon(String feature) {
        JOptionPane.showMessageDialog(this, 
            "Fitur " + feature + " sedang dalam pengembangan...", 
            "Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin logout?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (timeUpdateTimer != null) timeUpdateTimer.stop();
            dispose();
            new LoginPage().setVisible(true);
        }
    }
}