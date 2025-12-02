package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.LaporanController;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class FormLaporanKeuangan extends JFrame {
    
    private String currentRole;
    private LaporanController laporanController;
    
    private JComboBox<String> cbPeriode;
    private JDateChooser dateFrom, dateTo;
    private JRadioButton rbPeriode, rbCustom;
    
    // Summary Labels
    private JLabel lblTotalTransaksi, lblTotalPemasukan, lblRataRata;
    private JLabel lblTotalCash, lblTotalTransfer, lblTotalKartu;
    private JLabel lblJumlahCash, lblJumlahTransfer, lblJumlahKartu;
    
    private JTable tableDetail;
    private DefaultTableModel tableModel;
    
    private NumberFormat currencyFormat;
    
    public FormLaporanKeuangan(String role) {
        this.currentRole = role;
        this.laporanController = new LaporanController();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        initComponents();
        loadPeriodes();
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("Laporan Keuangan - SPP Payment System");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));
        
        getContentPane().setBackground(new Color(236, 240, 241));
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main Content
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(new EmptyBorder(20, 30, 30, 30));
        
        mainPanel.add(createFilterPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("📊 Laporan Keuangan SPP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JButton btnBack = createStyledButton("← Kembali", new Color(52, 73, 94));
        btnBack.addActionListener(e -> dispose());
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout(20, 15));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        // Top: Radio buttons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        radioPanel.setOpaque(false);
        
        rbPeriode = new JRadioButton("Filter berdasarkan Periode", true);
        rbCustom = new JRadioButton("Filter berdasarkan Tanggal");
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbPeriode);
        bg.add(rbCustom);
        
        rbPeriode.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rbCustom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rbPeriode.setOpaque(false);
        rbCustom.setOpaque(false);
        
        rbPeriode.addActionListener(e -> toggleFilterMode());
        rbCustom.addActionListener(e -> toggleFilterMode());
        
        radioPanel.add(rbPeriode);
        radioPanel.add(rbCustom);
        
        // Center: Input fields
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        inputPanel.setOpaque(false);
        
        // Periode ComboBox
        JLabel lblPeriode = new JLabel("Pilih Periode:");
        lblPeriode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbPeriode = new JComboBox<>();
        cbPeriode.setPreferredSize(new Dimension(200, 35));
        cbPeriode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Date Range
        JLabel lblDari = new JLabel("Dari:");
        lblDari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateFrom = new JDateChooser();
        dateFrom.setPreferredSize(new Dimension(150, 35));
        dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setEnabled(false);
        
        JLabel lblSampai = new JLabel("Sampai:");
        lblSampai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateTo = new JDateChooser();
        dateTo.setPreferredSize(new Dimension(150, 35));
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setEnabled(false);
        
        inputPanel.add(lblPeriode);
        inputPanel.add(cbPeriode);
        inputPanel.add(Box.createHorizontalStrut(20));
        inputPanel.add(lblDari);
        inputPanel.add(dateFrom);
        inputPanel.add(lblSampai);
        inputPanel.add(dateTo);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnGenerate = createStyledButton("🔍 Generate Laporan", new Color(46, 204, 113));
        JButton btnCetak = createStyledButton("🖨️ Cetak Laporan", new Color(52, 152, 219));
//        JButton btnExport = createStyledButton("📄 Export Excel", new Color(241, 196, 15));
        
        btnGenerate.addActionListener(e -> generateLaporan());
        btnCetak.addActionListener(e -> cetakLaporan());
//        btnExport.addActionListener(e -> exportToExcel());
        
        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnCetak);
//        buttonPanel.add(btnExport);
        
        filterPanel.add(radioPanel, BorderLayout.NORTH);
        filterPanel.add(inputPanel, BorderLayout.CENTER);
        filterPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return filterPanel;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        
        contentPanel.add(createSummaryPanel(), BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        return contentPanel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        summaryPanel.setOpaque(false);
        
        // Row 1: Total Info
        JPanel row1 = new JPanel(new GridLayout(1, 3, 15, 0));
        row1.setOpaque(false);
        
        lblTotalTransaksi = new JLabel("0");
        lblTotalPemasukan = new JLabel("Rp 0");
        lblRataRata = new JLabel("Rp 0");
        
        row1.add(createSummaryCard("Total Transaksi", lblTotalTransaksi, new Color(52, 152, 219), "📝"));
        row1.add(createSummaryCard("Total Pemasukan", lblTotalPemasukan, new Color(46, 204, 113), "💰"));
        row1.add(createSummaryCard("Rata-rata Pembayaran", lblRataRata, new Color(155, 89, 182), "📊"));
        
        // Row 2: Payment Methods
        JPanel row2 = new JPanel(new GridLayout(1, 3, 15, 0));
        row2.setOpaque(false);
        
        lblTotalCash = new JLabel("Rp 0");
        lblJumlahCash = new JLabel("0 transaksi");
        lblTotalTransfer = new JLabel("Rp 0");
        lblJumlahTransfer = new JLabel("0 transaksi");
        lblTotalKartu = new JLabel("Rp 0");
        lblJumlahKartu = new JLabel("0 transaksi");
        
        row2.add(createPaymentMethodCard("Cash", lblTotalCash, lblJumlahCash, new Color(39, 174, 96), "💵"));
        row2.add(createPaymentMethodCard("Transfer", lblTotalTransfer, lblJumlahTransfer, new Color(41, 128, 185), "🏦"));
        row2.add(createPaymentMethodCard("Kartu Debit", lblTotalKartu, lblJumlahKartu, new Color(142, 68, 173), "💳"));
        
        summaryPanel.add(row1);
        summaryPanel.add(row2);
        
        return summaryPanel;
    }
    
    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(127, 140, 141));
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        
        textPanel.add(lblTitle);
        textPanel.add(valueLabel);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createPaymentMethodCard(String method, JLabel totalLabel, JLabel countLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        textPanel.setOpaque(false);
        
        JLabel lblMethod = new JLabel(method);
        lblMethod.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMethod.setForeground(color);
        
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(new Color(44, 62, 80));
        
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        countLabel.setForeground(new Color(127, 140, 141));
        
        textPanel.add(lblMethod);
        textPanel.add(totalLabel);
        textPanel.add(countLabel);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("📋 Detail Transaksi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        String[] columns = {"ID Transaksi", "NIS", "Nama Siswa", "Periode", "Tanggal", "Jumlah", "Metode", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableDetail = new JTable(tableModel);
        tableDetail.setRowHeight(30);
        tableDetail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableDetail.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableDetail.getTableHeader().setBackground(new Color(52, 73, 94));
        tableDetail.getTableHeader().setForeground(Color.WHITE);
        
        // Center align
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableDetail.getColumnCount(); i++) {
            tableDetail.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(tableDetail);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        tablePanel.add(titleLabel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void toggleFilterMode() {
        boolean isPeriode = rbPeriode.isSelected();
        cbPeriode.setEnabled(isPeriode);
        dateFrom.setEnabled(!isPeriode);
        dateTo.setEnabled(!isPeriode);
    }
    
    private void loadPeriodes() {
        List<String> periods = laporanController.getAvailablePeriods();
        cbPeriode.removeAllItems();
        for (String period : periods) {
            cbPeriode.addItem(period);
        }
    }
    
    private void generateLaporan() {
        Map<String, Object> laporan;
        List<Map<String, Object>> detailTransaksi;
        
        if (rbPeriode.isSelected()) {
            String periode = (String) cbPeriode.getSelectedItem();
            if (periode == null) {
                JOptionPane.showMessageDialog(this, "Pilih periode terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            laporan = laporanController.getLaporanByPeriode(periode);
            detailTransaksi = laporanController.getDetailTransaksi(periode);
        } else {
            if (dateFrom.getDate() == null || dateTo.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Pilih tanggal mulai dan akhir!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            laporan = laporanController.getLaporanByDateRange(dateFrom.getDate(), dateTo.getDate());
            detailTransaksi = laporanController.getDetailTransaksiByDateRange(dateFrom.getDate(), dateTo.getDate());
        }
        
        updateSummary(laporan);
        updateTable(detailTransaksi);
    }
    
    private void updateSummary(Map<String, Object> laporan) {
        int totalTransaksi = (int) laporan.getOrDefault("total_transaksi", 0);
        double totalPemasukan = (double) laporan.getOrDefault("total_pemasukan", 0.0);
        double rataRata = (double) laporan.getOrDefault("rata_rata", 0.0);
        
        double totalCash = (double) laporan.getOrDefault("total_cash", 0.0);
        double totalTransfer = (double) laporan.getOrDefault("total_transfer", 0.0);
        double totalKartu = (double) laporan.getOrDefault("total_kartu", 0.0);
        
        int jumlahCash = (int) laporan.getOrDefault("jumlah_cash", 0);
        int jumlahTransfer = (int) laporan.getOrDefault("jumlah_transfer", 0);
        int jumlahKartu = (int) laporan.getOrDefault("jumlah_kartu", 0);
        
        lblTotalTransaksi.setText(String.valueOf(totalTransaksi));
        lblTotalPemasukan.setText(currencyFormat.format(totalPemasukan));
        lblRataRata.setText(currencyFormat.format(rataRata));
        
        lblTotalCash.setText(currencyFormat.format(totalCash));
        lblJumlahCash.setText(jumlahCash + " transaksi");
        
        lblTotalTransfer.setText(currencyFormat.format(totalTransfer));
        lblJumlahTransfer.setText(jumlahTransfer + " transaksi");
        
        lblTotalKartu.setText(currencyFormat.format(totalKartu));
        lblJumlahKartu.setText(jumlahKartu + " transaksi");
    }
    
    private void updateTable(List<Map<String, Object>> transaksiList) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Map<String, Object> transaksi : transaksiList) {
            Object[] row = {
                transaksi.get("id_transaksi"),
                transaksi.get("nis_siswa"),
                transaksi.get("nama_siswa"),
                transaksi.getOrDefault("bulan_tahun", "-"),
                sdf.format(transaksi.get("tanggal_bayar")),
                currencyFormat.format(transaksi.get("jumlah_bayar")),
                transaksi.get("metode_pembayaran"),
                transaksi.get("status_pembayaran")
            };
            tableModel.addRow(row);
        }
    }
    
    private void cetakLaporan() {
        try {
            tableDetail.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error mencetak laporan: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToExcel() {
        JOptionPane.showMessageDialog(this, "Fitur export Excel akan segera hadir!", 
            "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }
}