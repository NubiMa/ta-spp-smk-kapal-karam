package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.PembayaranController;
import aplikasi.pembayaran.spp.controller.SiswaController;
import aplikasi.pembayaran.spp.model.Pembayaran;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FormStatistik extends JFrame {
    
    private String currentRole;
    private PembayaranController pembayaranController;
    private SiswaController siswaController;
    
    private JPanel chartPanel;
    private JComboBox<String> cmbPeriode;
    private JLabel lblTotalPemasukan, lblRataRata, lblTertinggi, lblTerendah;
    
    public FormStatistik(String role) {
        this.currentRole = role;
        this.pembayaranController = new PembayaranController();
        this.siswaController = new SiswaController();
        
        initComponents();
        loadStatistik("Bulan Ini");
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("Statistik Pembayaran SPP");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        getContentPane().setBackground(new Color(236, 240, 241));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("📈 Statistik & Analisis Pembayaran");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JButton btnClose = new JButton("✕ Tutup");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(192, 57, 43));
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Filter Panel
        mainPanel.add(createFilterPanel(), BorderLayout.NORTH);
        
        // Content Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        splitPane.setLeftComponent(createChartPanel());
        splitPane.setRightComponent(createSummaryPanel());
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblFilter = new JLabel("Periode:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        cmbPeriode = new JComboBox<>(new String[]{
            "Bulan Ini", "3 Bulan Terakhir", "6 Bulan Terakhir", "Tahun Ini", "Semua Data"
        });
        cmbPeriode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbPeriode.setPreferredSize(new Dimension(180, 30));
        cmbPeriode.addActionListener(e -> loadStatistik((String) cmbPeriode.getSelectedItem()));
        
        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadStatistik((String) cmbPeriode.getSelectedItem()));
        
        filterPanel.add(lblFilter);
        filterPanel.add(cmbPeriode);
        filterPanel.add(btnRefresh);
        
        return filterPanel;
    }
    
    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel title = new JLabel("Grafik Pembayaran per Bulan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        chartPanel = new JPanel();
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setLayout(new BorderLayout());
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel title = new JLabel("Ringkasan Statistik");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel statsContainer = new JPanel(new GridLayout(8, 1, 0, 15));
        statsContainer.setBackground(Color.WHITE);
        
        lblTotalPemasukan = new JLabel("Rp 0");
        lblRataRata = new JLabel("Rp 0");
        lblTertinggi = new JLabel("Rp 0");
        lblTerendah = new JLabel("Rp 0");
        
        statsContainer.add(createStatRow("💰 Total Pemasukan:", lblTotalPemasukan, new Color(46, 204, 113)));
        statsContainer.add(createStatRow("📊 Rata-rata per Bulan:", lblRataRata, new Color(52, 152, 219)));
        statsContainer.add(createStatRow("⬆️ Pemasukan Tertinggi:", lblTertinggi, new Color(241, 196, 15)));
        statsContainer.add(createStatRow("⬇️ Pemasukan Terendah:", lblTerendah, new Color(230, 126, 34)));
        
        // Additional stats
        JPanel additionalStats = createAdditionalStats();
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(statsContainer, BorderLayout.CENTER);
        panel.add(additionalStats, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatRow(String label, JLabel valueLabel, Color color) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(248, 249, 250));
        row.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLabel.setForeground(new Color(52, 73, 94));
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        row.add(lblLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        
        return row;
    }
    
    private JPanel createAdditionalStats() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        try {
            String[] stats = pembayaranController.getStatistikPembayaran();
            int totalSiswa = siswaController.getAllSiswa().size();
            int sudahBayar = Integer.parseInt(stats[2]);
            int belumBayar = totalSiswa - sudahBayar;
            
            panel.add(createInfoCard("👥 Total Siswa", String.valueOf(totalSiswa), new Color(52, 152, 219)));
            panel.add(createInfoCard("✅ Sudah Bayar", String.valueOf(sudahBayar), new Color(46, 204, 113)));
            panel.add(createInfoCard("⚠️ Belum Bayar", String.valueOf(belumBayar), new Color(231, 76, 60)));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return panel;
    }
    
    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(new Color(248, 249, 250));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(127, 140, 141));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(color);
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadStatistik(String periode) {
        try {
            List<Pembayaran> allPembayaran = pembayaranController.getAllPembayaran(currentRole);
            List<Pembayaran> filteredData = filterByPeriode(allPembayaran, periode);
            
            // Update chart
            updateChart(filteredData);
            
            // Calculate statistics
            calculateStatistics(filteredData);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading statistik: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<Pembayaran> filterByPeriode(List<Pembayaran> data, String periode) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        
        switch (periode) {
            case "Bulan Ini":
                startDate = now.withDayOfMonth(1);
                break;
            case "3 Bulan Terakhir":
                startDate = now.minusMonths(3);
                break;
            case "6 Bulan Terakhir":
                startDate = now.minusMonths(6);
                break;
            case "Tahun Ini":
                startDate = now.withDayOfYear(1);
                break;
            default: // Semua Data
                return data;
        }
        
        return data.stream()
            .filter(p -> {
                LocalDateTime paymentDate = p.getTanggalBayar();
                LocalDate paymentLocalDate = paymentDate.toLocalDate();
                return !paymentLocalDate.isBefore(startDate);
            })
            .collect(Collectors.toList());
    }
    
    private void updateChart(List<Pembayaran> data) {
        chartPanel.removeAll();
        
        // Group by month
        Map<String, Double> monthlyData = new TreeMap<>();
        
        for (Pembayaran p : data) {
            try {
                LocalDateTime date = p.getTanggalBayar();
                String monthKey = date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                
                monthlyData.merge(monthKey, p.getJumlahBayar(), Double::sum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (monthlyData.isEmpty()) {
            JLabel noData = new JLabel("Tidak ada data untuk periode ini");
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noData.setForeground(new Color(127, 140, 141));
            noData.setHorizontalAlignment(SwingConstants.CENTER);
            chartPanel.add(noData, BorderLayout.CENTER);
        } else {
            chartPanel.add(new BarChartPanel(monthlyData), BorderLayout.CENTER);
        }
        
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    private void calculateStatistics(List<Pembayaran> data) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        if (data.isEmpty()) {
            lblTotalPemasukan.setText("Rp 0");
            lblRataRata.setText("Rp 0");
            lblTertinggi.setText("Rp 0");
            lblTerendah.setText("Rp 0");
            return;
        }
        
        // Total pemasukan
        double total = data.stream().mapToDouble(Pembayaran::getJumlahBayar).sum();
        lblTotalPemasukan.setText(currencyFormat.format(total));
        
        // Group by month for averages
        Map<String, Double> monthlyData = new HashMap<>();
        for (Pembayaran p : data) {
            try {
                LocalDateTime date = p.getTanggalBayar();
                String monthKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                monthlyData.merge(monthKey, p.getJumlahBayar(), Double::sum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Rata-rata per bulan
        if (!monthlyData.isEmpty()) {
            double average = monthlyData.values().stream()
                .mapToDouble(Double::doubleValue).average().orElse(0);
            lblRataRata.setText(currencyFormat.format(average));
            
            // Tertinggi dan terendah
            double max = monthlyData.values().stream()
                .mapToDouble(Double::doubleValue).max().orElse(0);
            double min = monthlyData.values().stream()
                .mapToDouble(Double::doubleValue).min().orElse(0);
            
            lblTertinggi.setText(currencyFormat.format(max));
            lblTerendah.setText(currencyFormat.format(min));
        }
    }
    
    // Inner class for bar chart
    private class BarChartPanel extends JPanel {
        private Map<String, Double> data;
        private double maxValue;
        
        public BarChartPanel(Map<String, Double> data) {
            this.data = data;
            this.maxValue = data.values().stream()
                .mapToDouble(Double::doubleValue).max().orElse(1);
            setBackground(Color.WHITE);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int padding = 50;
            int chartHeight = height - 2 * padding;
            int barWidth = Math.max(30, (width - 2 * padding) / data.size() - 10);
            
            // Draw axes
            g2d.setColor(new Color(189, 195, 199));
            g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
            g2d.drawLine(padding, padding, padding, height - padding); // Y-axis
            
            // Draw bars
            int x = padding + 5;
            Color[] colors = {
                new Color(52, 152, 219), new Color(46, 204, 113), 
                new Color(155, 89, 182), new Color(241, 196, 15),
                new Color(230, 126, 34), new Color(231, 76, 60)
            };
            int colorIndex = 0;
            
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double value = entry.getValue();
                int barHeight = (int) ((value / maxValue) * chartHeight);
                
                // Draw bar
                g2d.setColor(colors[colorIndex % colors.length]);
                g2d.fillRoundRect(x, height - padding - barHeight, barWidth, barHeight, 5, 5);
                
                // Draw value on top of bar
                g2d.setColor(new Color(44, 62, 80));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String valueStr = String.format("%.0fK", value / 1000);
                g2d.drawString(valueStr, x + barWidth/4, height - padding - barHeight - 5);
                
                // Draw label
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2d.drawString(entry.getKey(), x - 5, height - padding + 15);
                
                x += barWidth + 10;
                colorIndex++;
            }
        }
    }
}