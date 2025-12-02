package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.PembayaranController;
import aplikasi.pembayaran.spp.controller.SiswaController;
import aplikasi.pembayaran.spp.model.Pembayaran;
import aplikasi.pembayaran.spp.model.Siswa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class FormMonitorTunggakan extends JFrame {
    
    private JTable tableTunggakan;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private JButton btnRefresh, btnExport, btnKeluar;
    private JLabel lblTotalTunggakan, lblJumlahSiswa, lblTotalNominal;
    
    private PembayaranController pembayaranController;
    private SiswaController siswaController;
    
    private String currentRole;
    
    private static final String[] BULAN_ARRAY = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };
    
    public FormMonitorTunggakan(String role) {
        this.currentRole = role;
        this.pembayaranController = new PembayaranController();
        this.siswaController = new SiswaController();
        
        initComponents();
        loadDataTunggakan();
        updateStatistik();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("Monitor Tunggakan SPP");
        setSize(1400, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // ===== PANEL HEADER =====
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(231, 76, 60));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel lblTitle = new JLabel("⚠️ MONITOR TUNGGAKAN SPP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        
        LocalDate today = LocalDate.now();
        JLabel lblPeriode = new JLabel("Periode s/d " + BULAN_ARRAY[today.getMonthValue()-1] + " " + today.getYear());
        lblPeriode.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPeriode.setForeground(new Color(236, 240, 241));
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblPeriode);
        
        panelHeader.add(titlePanel, BorderLayout.WEST);
        
        // ===== PANEL STATISTIK =====
        JPanel panelStats = new JPanel(new GridLayout(1, 3, 15, 0));
        panelStats.setBackground(new Color(236, 240, 241));
        panelStats.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        panelStats.setPreferredSize(new Dimension(0, 100));
        
        JPanel card1 = createStatCard("Jumlah Siswa Tunggakan", "0", new Color(231, 76, 60));
        lblJumlahSiswa = (JLabel) ((JPanel)card1.getComponent(0)).getComponent(1);
        
        JPanel card2 = createStatCard("Total Nominal Tunggakan", "Rp 0", new Color(230, 126, 34));
        lblTotalNominal = (JLabel) ((JPanel)card2.getComponent(0)).getComponent(1);
        
        JPanel card3 = createStatCard("Status", "Perlu Tindakan", new Color(192, 57, 43));
        lblTotalTunggakan = (JLabel) ((JPanel)card3.getComponent(0)).getComponent(1);
        
        panelStats.add(card1);
        panelStats.add(card2);
        panelStats.add(card3);
        
        // ===== PANEL FILTER & SEARCH =====
        JPanel panelFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFilter.setBackground(Color.WHITE);
        panelFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("🔍 Filter & Pencarian"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        panelFilter.add(new JLabel("Cari NIS/Nama:"));
        txtSearch = new JTextField(20);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTunggakan();
            }
        });
        panelFilter.add(txtSearch);
        
        panelFilter.add(new JLabel("Filter Kelas:"));
        cmbFilter = new JComboBox<>(new String[]{"Semua Kelas", "X", "XI", "XII"});
        cmbFilter.addActionListener(e -> filterByKelas());
        panelFilter.add(cmbFilter);
        
        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> {
            loadDataTunggakan();
            updateStatistik();
        });
        panelFilter.add(btnRefresh);
        
        // ===== PANEL TABLE =====
        String[] columns = {"NIS", "Nama Lengkap", "Kelas", "Bulan Belum Bayar", 
                           "Jumlah Bulan", "Nominal/Bulan", "Total Tunggakan", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableTunggakan = new JTable(tableModel);
        tableTunggakan.setRowHeight(30);
        tableTunggakan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTunggakan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableTunggakan.getTableHeader().setBackground(new Color(231, 76, 60));
        tableTunggakan.getTableHeader().setForeground(Color.WHITE);
        tableTunggakan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Set column widths
        tableTunggakan.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableTunggakan.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableTunggakan.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableTunggakan.getColumnModel().getColumn(3).setPreferredWidth(300);
        tableTunggakan.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableTunggakan.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableTunggakan.getColumnModel().getColumn(6).setPreferredWidth(130);
        tableTunggakan.getColumnModel().getColumn(7).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tableTunggakan);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        // ===== PANEL ACTIONS =====
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelActions.setBackground(new Color(236, 240, 241));
        
//        btnBayarSekarang = new JButton("💰 Bayar Sekarang");
//        btnBayarSekarang.setPreferredSize(new Dimension(160, 40));
//        btnBayarSekarang.setBackground(new Color(46, 204, 113));
//        btnBayarSekarang.setForeground(Color.WHITE);
//        btnBayarSekarang.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btnBayarSekarang.setFocusPainted(false);
//        btnBayarSekarang.addActionListener(e -> bayarTunggakan());
//        
//        btnKirimNotifikasi = new JButton("📧 Kirim Notifikasi");
//        btnKirimNotifikasi.setPreferredSize(new Dimension(160, 40));
//        btnKirimNotifikasi.setBackground(new Color(52, 152, 219));
//        btnKirimNotifikasi.setForeground(Color.WHITE);
//        btnKirimNotifikasi.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btnKirimNotifikasi.setFocusPainted(false);
//        btnKirimNotifikasi.addActionListener(e -> kirimNotifikasi());
        
        btnExport = new JButton("📄 Export EXCEL");
        btnExport.setPreferredSize(new Dimension(160, 40));
        btnExport.setBackground(new Color(155, 89, 182));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> exportData());
        
        btnKeluar = new JButton("❌ KELUAR");
        btnKeluar.setPreferredSize(new Dimension(160, 40));
        btnKeluar.setBackground(new Color(127, 140, 141));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKeluar.setFocusPainted(false);
        btnKeluar.addActionListener(e -> dispose());
        
//        panelActions.add(btnBayarSekarang);
//        panelActions.add(btnKirimNotifikasi);
        panelActions.add(btnExport);
        panelActions.add(btnKeluar);
        
        // ===== ASSEMBLE =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelHeader, BorderLayout.NORTH);
        topPanel.add(panelStats, BorderLayout.CENTER);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(236, 240, 241));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        centerPanel.add(panelFilter, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(panelActions, BorderLayout.SOUTH);
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(127, 140, 141));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(color);
        
        textPanel.add(lblTitle);
        textPanel.add(lblValue);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadDataTunggakan() {
        tableModel.setRowCount(0);

        List<Siswa> allSiswa = siswaController.getAllSiswa();
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue(); // 1-12
        
        int totalSiswaWithTunggakan = 0;
        double totalNominalTunggakan = 0;

        for (Siswa siswa : allSiswa) {
            if (!"Aktif".equals(siswa.getStatusSiswa())) {
                continue;
            }

            List<Pembayaran> pembayaranList = pembayaranController.getPembayaranByNIS(siswa.getNis());
            
            // Get bulan yang sudah dibayar lunas
            Set<String> bulanLunas = new HashSet<>();
            for (Pembayaran p : pembayaranList) {
                if ("Lunas".equals(p.getStatusPembayaran())) {
                    String bulanTahun = p.getBulanTahun();
                    if (bulanTahun != null) {
                        bulanLunas.add(bulanTahun);
                    }
                }
            }
            
            // Cek bulan yang belum dibayar (Januari s/d bulan sekarang)
            List<String> bulanBelumBayar = new ArrayList<>();
            
            for (int month = 1; month <= currentMonth; month++) {
                String bulanName = BULAN_ARRAY[month - 1];
                String bulanTahun = bulanName + " " + currentYear;
                
                if (!bulanLunas.contains(bulanTahun)) {
                    bulanBelumBayar.add(bulanName);
                }
            }
            
            // Jika ada tunggakan, tampilkan
            if (!bulanBelumBayar.isEmpty()) {
                totalSiswaWithTunggakan++;
                
                int jumlahBulan = bulanBelumBayar.size();
                double nominalPerBulan = siswa.getNominalSPP();
                double totalTunggakan = nominalPerBulan * jumlahBulan;
                totalNominalTunggakan += totalTunggakan;
                
                String bulanStr = String.join(", ", bulanBelumBayar);
                
                Object[] row = {
                    siswa.getNis(),
                    siswa.getNamaLengkap(),
                    siswa.getKelas(),
                    bulanStr,
                    jumlahBulan + " bulan",
                    String.format("Rp %.0f", nominalPerBulan),
                    String.format("Rp %.0f", totalTunggakan),
                    jumlahBulan >= 3 ? "🔴 Urgent" : "⚠️ Perlu Bayar"
                };
                tableModel.addRow(row);
            }
        }
        
        System.out.println("✅ Found " + totalSiswaWithTunggakan + " siswa dengan tunggakan");
        
        // Update statistik
        lblJumlahSiswa.setText(String.valueOf(totalSiswaWithTunggakan));
        lblTotalNominal.setText(String.format("Rp %.0f", totalNominalTunggakan));
    }
    
    private void updateStatistik() {
        try {
            int jumlahSiswa = tableModel.getRowCount();
            
            if (jumlahSiswa > 10) {
                lblTotalTunggakan.setText("🔴 Urgent!");
                lblTotalTunggakan.setForeground(new Color(192, 57, 43));
            } else if (jumlahSiswa > 5) {
                lblTotalTunggakan.setText("⚡ Perlu Tindakan");
                lblTotalTunggakan.setForeground(new Color(230, 126, 34));
            } else if (jumlahSiswa > 0) {
                lblTotalTunggakan.setText("⚠️ Ada Tunggakan");
                lblTotalTunggakan.setForeground(new Color(241, 196, 15));
            } else {
                lblTotalTunggakan.setText("✅ Terkendali");
                lblTotalTunggakan.setForeground(new Color(46, 204, 113));
            }
            
        } catch (Exception e) {
            System.err.println("Error update statistik: " + e.getMessage());
        }
    }
    
    private void searchTunggakan() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        
        if (keyword.isEmpty()) {
            loadDataTunggakan();
            updateStatistik();
            return;
        }
        
        // Filter table based on keyword
        DefaultTableModel tempModel = new DefaultTableModel(
            new String[]{"NIS", "Nama Lengkap", "Kelas", "Bulan Belum Bayar", 
                        "Jumlah Bulan", "Nominal/Bulan", "Total Tunggakan", "Status"}, 0);
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nis = tableModel.getValueAt(i, 0).toString().toLowerCase();
            String nama = tableModel.getValueAt(i, 1).toString().toLowerCase();
            
            if (nis.contains(keyword) || nama.contains(keyword)) {
                Object[] row = new Object[8];
                for (int j = 0; j < 8; j++) {
                    row[j] = tableModel.getValueAt(i, j);
                }
                tempModel.addRow(row);
            }
        }
        
        tableModel.setRowCount(0);
        for (int i = 0; i < tempModel.getRowCount(); i++) {
            Object[] row = new Object[8];
            for (int j = 0; j < 8; j++) {
                row[j] = tempModel.getValueAt(i, j);
            }
            tableModel.addRow(row);
        }
    }
    
    private void filterByKelas() {
        String selectedKelas = (String) cmbFilter.getSelectedItem();
        
        if ("Semua Kelas".equals(selectedKelas)) {
            loadDataTunggakan();
            updateStatistik();
            return;
        }
        
        DefaultTableModel tempModel = new DefaultTableModel(
            new String[]{"NIS", "Nama Lengkap", "Kelas", "Bulan Belum Bayar", 
                        "Jumlah Bulan", "Nominal/Bulan", "Total Tunggakan", "Status"}, 0);
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String kelas = tableModel.getValueAt(i, 2).toString();
            
            if (kelas.contains(selectedKelas)) {
                Object[] row = new Object[8];
                for (int j = 0; j < 8; j++) {
                    row[j] = tableModel.getValueAt(i, j);
                }
                tempModel.addRow(row);
            }
        }
        
        tableModel.setRowCount(0);
        for (int i = 0; i < tempModel.getRowCount(); i++) {
            Object[] row = new Object[8];
            for (int j = 0; j < 8; j++) {
                row[j] = tempModel.getValueAt(i, j);
            }
            tableModel.addRow(row);
        }
    }
    
    private void bayarTunggakan() {
        int selectedRow = tableTunggakan.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Pilih siswa yang akan dibayarkan tunggakannya!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nis = (String) tableModel.getValueAt(selectedRow, 0);
        String nama = (String) tableModel.getValueAt(selectedRow, 1);
        String bulanBelumBayar = (String) tableModel.getValueAt(selectedRow, 3);
        String totalTunggakan = (String) tableModel.getValueAt(selectedRow, 6);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Buka form pembayaran untuk:\n\n" +
                "NIS: " + nis + "\n" +
                "Nama: " + nama + "\n" +
                "Bulan: " + bulanBelumBayar + "\n" +
                "Total: " + totalTunggakan,
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new FormInputPembayaran(currentRole, currentRole, this);
        }
    }
    
    private void kirimNotifikasi() {
        int selectedRow = tableTunggakan.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Pilih siswa yang akan dikirim notifikasi!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nama = (String) tableModel.getValueAt(selectedRow, 1);
        String bulan = (String) tableModel.getValueAt(selectedRow, 3);
        String tunggakan = (String) tableModel.getValueAt(selectedRow, 6);
        
        JOptionPane.showMessageDialog(this, 
                "Notifikasi dikirim ke: " + nama + "\n" +
                "Bulan: " + bulan + "\n" +
                "Tunggakan: " + tunggakan + "\n\n" +
                "(Fitur email/SMS integration coming soon...)",
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportData() {
        try {
            // Cek apakah ada data
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                        "Tidak ada data tunggakan untuk di-export!", 
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Buat file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan Laporan Tunggakan");

            // Set default filename dengan tanggal
            LocalDate today = LocalDate.now();
            String defaultFilename = "Laporan_Tunggakan_" + 
                                    today.getYear() + "_" + 
                                    String.format("%02d", today.getMonthValue()) + "_" +
                                    String.format("%02d", today.getDayOfMonth()) + ".xlsx";
            fileChooser.setSelectedFile(new java.io.File(defaultFilename));

            // Filter untuk file Excel
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(java.io.File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".xlsx");
                }
                public String getDescription() {
                    return "Excel Files (*.xlsx)";
                }
            });

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                // Tambahkan ekstensi .xlsx jika belum ada
                if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                    fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
                }

                // Konfirmasi jika file sudah ada
                if (fileToSave.exists()) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "File sudah ada. Timpa file?",
                            "Konfirmasi", JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                // Export ke Excel
                exportToExcel(fileToSave);

                // Tampilkan pesan sukses
                int openFile = JOptionPane.showConfirmDialog(this,
                        "Export berhasil!\n\nFile: " + fileToSave.getName() + 
                        "\nLokasi: " + fileToSave.getParent() + 
                        "\n\nBuka file sekarang?",
                        "Success", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (openFile == JOptionPane.YES_OPTION) {
                    // Buka file dengan aplikasi default
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(fileToSave);
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error saat export data:\n" + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void exportToExcel(java.io.File file) throws Exception {
        // Import yang diperlukan (tambahkan di bagian atas class):
        // import org.apache.poi.ss.usermodel.*;
        // import org.apache.poi.xssf.usermodel.XSSFWorkbook;
        // import java.io.FileOutputStream;

        org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Laporan Tunggakan SPP");

        // ===== STYLE UNTUK HEADER =====
        org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);

        // ===== STYLE UNTUK TITLE =====
        org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

        // ===== STYLE UNTUK DATA =====
        org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        // ===== STYLE UNTUK ANGKA =====
        org.apache.poi.ss.usermodel.CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.cloneStyleFrom(dataStyle);
        org.apache.poi.ss.usermodel.DataFormat format = workbook.createDataFormat();
        currencyStyle.setDataFormat(format.getFormat("Rp #,##0"));

        // ===== STYLE UNTUK STATUS URGENT =====
        org.apache.poi.ss.usermodel.CellStyle urgentStyle = workbook.createCellStyle();
        urgentStyle.cloneStyleFrom(dataStyle);
        urgentStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.ROSE.getIndex());
        urgentStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        org.apache.poi.ss.usermodel.Font urgentFont = workbook.createFont();
        urgentFont.setBold(true);
        urgentFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        urgentStyle.setFont(urgentFont);

        int rowNum = 0;

        // ===== TITLE =====
        org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("⚠️ LAPORAN TUNGGAKAN SPP");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));
        titleRow.setHeightInPoints(25);

        // ===== INFO PERIODE =====
        org.apache.poi.ss.usermodel.Row periodeRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell periodeCell = periodeRow.createCell(0);
        LocalDate today = LocalDate.now();
        periodeCell.setCellValue("Periode s/d " + BULAN_ARRAY[today.getMonthValue()-1] + " " + today.getYear());

        // ===== STATISTIK =====
        org.apache.poi.ss.usermodel.Row statsRow = sheet.createRow(rowNum++);
        statsRow.createCell(0).setCellValue("Jumlah Siswa Tunggakan: " + lblJumlahSiswa.getText());

        org.apache.poi.ss.usermodel.Row nominalRow = sheet.createRow(rowNum++);
        nominalRow.createCell(0).setCellValue("Total Nominal: " + lblTotalNominal.getText());

        rowNum++; // Baris kosong

        // ===== HEADER TABLE =====
        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"NIS", "Nama Lengkap", "Kelas", "Bulan Belum Bayar", 
                           "Jumlah Bulan", "Nominal/Bulan", "Total Tunggakan", "Status"};

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        headerRow.setHeightInPoints(25);

        // ===== DATA ROWS =====
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowNum++);
            dataRow.setHeightInPoints(20);

            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                org.apache.poi.ss.usermodel.Cell cell = dataRow.createCell(j);
                Object value = tableModel.getValueAt(i, j);

                if (value != null) {
                    String strValue = value.toString();

                    // Kolom Nominal/Bulan dan Total Tunggakan (format currency)
                    if (j == 5 || j == 6) {
                        try {
                            // Hapus "Rp" dan parse ke double
                            String numStr = strValue.replace("Rp", "").replace(".", "").replace(",", ".").trim();
                            double numValue = Double.parseDouble(numStr);
                            cell.setCellValue(numValue);
                            cell.setCellStyle(currencyStyle);
                        } catch (NumberFormatException e) {
                            cell.setCellValue(strValue);
                            cell.setCellStyle(dataStyle);
                        }
                    }
                    // Kolom Status
                    else if (j == 7) {
                        cell.setCellValue(strValue);
                        if (strValue.contains("Urgent")) {
                            cell.setCellStyle(urgentStyle);
                        } else {
                            cell.setCellStyle(dataStyle);
                        }
                    }
                    // Kolom lainnya
                    else {
                        cell.setCellValue(strValue);
                        cell.setCellStyle(dataStyle);
                    }
                }
            }
        }

        // ===== AUTO SIZE COLUMNS =====
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            // Tambah sedikit padding
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, currentWidth + 1000);
        }

        // ===== SIMPAN FILE =====
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }

        workbook.close();
    }
}