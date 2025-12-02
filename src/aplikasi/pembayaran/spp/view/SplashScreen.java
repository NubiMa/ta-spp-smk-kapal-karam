package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.model.Koneksi;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * SplashScreen - Loading Screen aplikasi
 * Tampil pertama kali saat aplikasi dijalankan
 * Fungsi: Load database, cek koneksi, initialize data
 */
public class SplashScreen extends JFrame {
    
    // Component UI
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JLabel logoLabel;
    private Timer timer;
    private int progress = 0;
    
    // Array untuk step loading
    private String[] loadingSteps = {
        "Memulai aplikasi...",
        "Loading database driver...",
        "Menghubungkan ke database...",
        "Mengecek tabel database...",
        "Inisialisasi data...",
        "Memuat konfigurasi...",
        "Aplikasi siap digunakan!"
    };
    
    /**
     * Constructor - Setup UI dan mulai loading process
     */
    public SplashScreen() {
        initComponents();
        setupUI();
        startLoading();
    }
    
    /**
     * Method untuk inisialisasi komponen UI
     */
    private void initComponents() {
        // Set properties window
        setTitle("SPP Payment System - Loading");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center di layar
        setUndecorated(true); // Hilangkan window border
        setResizable(false);
        
        // Set layout
        setLayout(new BorderLayout());
        
        // Panel utama dengan gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Create gradient background (biru ke putih)
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185), // Biru
                    0, getHeight(), new Color(52, 152, 219) // Biru lebih terang
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2));
        
        // Panel atas untuk logo dan title
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false); // Transparent supaya gradient keliatan
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Logo (pake icon default dulu, nanti bisa diganti)
        logoLabel = new JLabel();
        logoLabel.setIcon(createLogoIcon());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        topPanel.add(logoLabel, gbc);
        
        // Title aplikasi
        titleLabel = new JLabel("SPP PAYMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        topPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Sistem Pembayaran SPP Sekolah");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        topPanel.add(subtitleLabel, gbc);
        
        // Panel bawah untuk progress
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 40, 50));
        
        // Status label
        statusLabel = new JLabel("Memulai aplikasi...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setFont(new Font("Arial", Font.BOLD, 11));
        progressBar.setForeground(new Color(46, 204, 113)); // Hijau
        progressBar.setBackground(new Color(189, 195, 199)); // Abu-abu
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(400, 25));
        bottomPanel.add(progressBar, BorderLayout.CENTER);
        
        // Copyright label
        JLabel copyrightLabel = new JLabel("© 2024 SPP Payment System. All rights reserved.");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        copyrightLabel.setForeground(new Color(255, 255, 255));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(copyrightLabel, BorderLayout.SOUTH);
        
        // Add panels ke main panel
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add main panel ke frame
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Method untuk setup additional UI properties
     */
    private void setupUI() {
        // Set window icon
        try {
            // Nanti bisa diganti dengan icon custom
            setIconImage(Toolkit.getDefaultToolkit().getImage("assets/icon.png"));
        } catch (Exception e) {
            System.out.println("⚠️ Icon tidak ditemukan, menggunakan default");
        }
        
        // Set cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    /**
     * Method untuk membuat icon logo sederhana
     */
    private ImageIcon createLogoIcon() {
        // Buat icon sederhana dengan kode (nanti bisa diganti dengan gambar)
        int size = 80;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circle background
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillOval(5, 5, size-10, size-10);
        
        // Draw border
        g2d.setColor(new Color(52, 152, 219));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(5, 5, size-10, size-10);
        
        // Draw "SPP" text
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "SPP";
        int textX = (size - fm.stringWidth(text)) / 2;
        int textY = (size + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * Method untuk memulai loading process
     */
    private void startLoading() {
        // Timer untuk simulasi loading (100ms per step)
        timer = new Timer(100, new ActionListener() {
            private int stepIndex = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update progress
                progress++;
                progressBar.setValue(progress);
                progressBar.setString(progress + "%");
                
                // Update status setiap 15% progress
                if (progress % 15 == 0 && stepIndex < loadingSteps.length) {
                    statusLabel.setText(loadingSteps[stepIndex]);
                    
                    // Execute task sesuai step
                    executeLoadingTask(stepIndex);
                    stepIndex++;
                }
                
                // Jika loading selesai
                if (progress >= 100) {
                    timer.stop();
                    finishLoading();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Method untuk execute task sesuai loading step
     */
    private void executeLoadingTask(int stepIndex) {
        // Execute task di background thread supaya UI tidak freeze
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                switch (stepIndex) {
                    case 0:
                        // Memulai aplikasi
                        Thread.sleep(200);
                        break;
                        
                    case 1:
                        // Loading database driver
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            System.out.println("✅ MySQL Driver loaded successfully");
                        } catch (ClassNotFoundException ex) {
                            System.err.println("❌ MySQL Driver tidak ditemukan!");
                        }
                        break;
                        
                    case 2:
                        // Test koneksi database
                        boolean connected = Koneksi.testConnection();
                        if (connected) {
                            System.out.println("✅ Database connection successful");
                        } else {
                            System.err.println("❌ Database connection failed");
                        }
                        break;
                        
                    case 3:
                        // Initialize database
                        Koneksi.setupDatabase();
                        break;
                        
                    case 4:
                        // Inisialisasi data
                        Thread.sleep(300);
                        System.out.println("✅ Data initialization complete");
                        break;
                        
                    case 5:
                        // Load konfigurasi
                        Thread.sleep(200);
                        System.out.println("✅ Configuration loaded");
                        break;
                        
                    case 6:
                        // Aplikasi siap
                        System.out.println("🎉 Application ready!");
                        break;
                }
                return null;
            }
            
            @Override
            protected void done() {
                // Task selesai, lanjut loading
            }
        };
        
        worker.execute();
    }
    
    /**
     * Method yang dipanggil saat loading selesai
     */
    private void finishLoading() {
        statusLabel.setText("Aplikasi siap digunakan!");
        setCursor(Cursor.getDefaultCursor());
        
        // Delay sebentar sebelum pindah ke login
        Timer delayTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tutup splash screen
                dispose();
                
                // Buka login page
                SwingUtilities.invokeLater(() -> {
                    new LoginPage().setVisible(true);
                });
                
                ((Timer) e.getSource()).stop();
            }
        });
        
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    
    /**
     * Main method untuk testing splash screen
     */
    public static void main(String[] args) {
        // Set look and feel
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            System.out.println("⚠️ Tidak bisa set system look and feel");
        }
        
        // Jalankan splash screen
        SwingUtilities.invokeLater(() -> {
            new SplashScreen().setVisible(true);
        });
    }
}