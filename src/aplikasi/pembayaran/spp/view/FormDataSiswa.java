package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.controller.SiswaController;
import aplikasi.pembayaran.spp.model.Siswa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormDataSiswa extends JFrame {
    
    // Components
    private JTable tableSiswa;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh, btnKeluar;
    
    // Controller
    private SiswaController siswaController;
    
    // Data
    private String currentRole;
    
    public FormDataSiswa(String role) {
        this.currentRole = role;
        this.siswaController = new SiswaController();
        
        initComponents();
        loadDataSiswa();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("Data Siswa");
        setSize(1100, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // ===== PANEL HEADER =====
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(52, 152, 219));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("DATA SISWA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        panelHeader.add(lblTitle, BorderLayout.WEST);
        
        // ===== PANEL SEARCH =====
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSearch.setBackground(Color.WHITE);
        panelSearch.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panelSearch.add(new JLabel("🔍 Cari Siswa:"));
        txtSearch = new JTextField(25);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchSiswa();
            }
        });
        panelSearch.add(txtSearch);
        
        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadDataSiswa());
        panelSearch.add(btnRefresh);
        
        // ===== PANEL TABLE =====
        String[] columns = {"NIS", "Nama Lengkap", "Kelas", "Tahun Ajaran",
                           "Nominal SPP", "SPP Harus Bayar", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };
        
        tableSiswa = new JTable(tableModel);
        tableSiswa.setRowHeight(25);
        tableSiswa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSiswa.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableSiswa.getTableHeader().setBackground(new Color(52, 152, 219));
        tableSiswa.getTableHeader().setForeground(Color.WHITE);
        
        // Set column widths
        tableSiswa.getColumnModel().getColumn(0).setPreferredWidth(80);  // NIS
        tableSiswa.getColumnModel().getColumn(1).setPreferredWidth(200); // Nama
        tableSiswa.getColumnModel().getColumn(2).setPreferredWidth(80);  // Kelas
        tableSiswa.getColumnModel().getColumn(3).setPreferredWidth(100); // Tahun Ajaran
        tableSiswa.getColumnModel().getColumn(4).setPreferredWidth(100); // Nominal
        tableSiswa.getColumnModel().getColumn(5).setPreferredWidth(120); // Harus Bayar
        tableSiswa.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        
        JScrollPane scrollPane = new JScrollPane(tableSiswa);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        // ===== PANEL BUTTON =====
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelButton.setBackground(new Color(236, 240, 241));
        
        btnTambah = new JButton("➕ TAMBAH SISWA");
        btnTambah.setPreferredSize(new Dimension(160, 40));
        btnTambah.setBackground(new Color(46, 204, 113));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> tambahSiswa());
        
        btnEdit = new JButton("✏️ EDIT SISWA");
        btnEdit.setPreferredSize(new Dimension(160, 40));
        btnEdit.setBackground(new Color(241, 196, 15));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEdit.setFocusPainted(false);
        btnEdit.addActionListener(e -> editSiswa());
        
        btnHapus = new JButton("🗑️ HAPUS SISWA");
        btnHapus.setPreferredSize(new Dimension(160, 40));
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> hapusSiswa());
        
        btnKeluar = new JButton("❌ KELUAR");
        btnKeluar.setPreferredSize(new Dimension(160, 40));
        btnKeluar.setBackground(new Color(127, 140, 141));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKeluar.setFocusPainted(false);
        btnKeluar.addActionListener(e -> dispose());
        
        panelButton.add(btnTambah);
        panelButton.add(btnEdit);
        panelButton.add(btnHapus);
        panelButton.add(btnKeluar);
        
        // Add panels to frame
        add(panelHeader, BorderLayout.NORTH);
        add(panelSearch, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }
    
    // Method untuk load data siswa ke table
    private void loadDataSiswa() {
        tableModel.setRowCount(0); // Clear table
        
        List<Siswa> listSiswa = siswaController.getAllSiswa();
        
        for (Siswa s : listSiswa) {
            double sppHarusBayar = s.getNominalSPP(); // Removed potongan calculation

            Object[] row = {
                s.getNis(),
                s.getNamaLengkap(),
                s.getKelas(),
                s.getTahunAjaran(),
                String.format("Rp %.0f", s.getNominalSPP()),
                String.format("Rp %.0f", sppHarusBayar),
                s.getStatusSiswa()
            };
            tableModel.addRow(row);
        }
        
        System.out.println("✅ Loaded " + listSiswa.size() + " siswa data");
    }
    
    // Method untuk search siswa
    private void searchSiswa() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        
        if (keyword.isEmpty()) {
            loadDataSiswa();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Siswa> listSiswa = siswaController.getAllSiswa();
        
        for (Siswa s : listSiswa) {
            if (s.getNis().toLowerCase().contains(keyword) ||
                s.getNamaLengkap().toLowerCase().contains(keyword) ||
                s.getKelas().toLowerCase().contains(keyword)) {
                
                double sppHarusBayar = s.getNominalSPP(); // Removed potongan calculation

                Object[] row = {
                    s.getNis(),
                    s.getNamaLengkap(),
                    s.getKelas(),
                    s.getTahunAjaran(),
                    String.format("Rp %.0f", s.getNominalSPP()),
                    String.format("Rp %.0f", sppHarusBayar),
                    s.getStatusSiswa()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    // Method untuk tambah siswa
    private void tambahSiswa() {
        new FormTambahEditSiswa(null, this, currentRole);
    }
    
    // Method untuk edit siswa
    private void editSiswa() {
        int selectedRow = tableSiswa.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih siswa yang akan diedit!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nis = (String) tableModel.getValueAt(selectedRow, 0);
        Siswa siswa = siswaController.getSiswaByNis(nis);
        
        if (siswa != null) {
            new FormTambahEditSiswa(siswa, this, currentRole);
        }
    }
    
    // Method untuk hapus siswa
    private void hapusSiswa() {
        int selectedRow = tableSiswa.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih siswa yang akan dihapus!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nis = (String) tableModel.getValueAt(selectedRow, 0);
        String nama = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus siswa ini?\n\nNIS: " + nis + "\nNama: " + nama,
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean sukses = siswaController.hapusSiswa(nis);
            
            if (sukses) {
                JOptionPane.showMessageDialog(this, "Siswa berhasil dihapus!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDataSiswa();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus siswa!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Method to refresh the student table data
     */
    public void refreshTable() {
        loadDataSiswa();
    }
}