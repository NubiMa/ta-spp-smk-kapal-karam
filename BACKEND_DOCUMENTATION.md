# Sistem Pembayaran SPP SMK - Backend Documentation

## Overview

Sistem Pembayaran SPP SMK adalah aplikasi desktop berbasis Java yang mengelola pembayaran Siswa Pencaharian Uang (SPP) di lingkungan sekolah menengah kejuruan. Sistem ini menggunakan database MySQL untuk penyimpanan data dan Java Swing untuk antarmuka pengguna.

## Architecture

### Technology Stack
- **Language**: Java 8+
- **Database**: MySQL 8.0+
- **JDBC Driver**: mysql-connector-j-9.4.0
- **UI Framework**: Java Swing
- **Build Tool**: Apache Ant
- **Logging**: Log4j 2.20.0
- **Additional Libraries**: Apache POI, Apache Commons

### Project Structure
```
src/
└── aplikasi/
    └── pembayaran/
        └── spp/
            ├── AplikasiPembayaranSPP.java (Main Entry Point)
            ├── model/          (Data Models)
            ├── controller/     (Business Logic)
            ├── view/           (User Interface)
            ├── test/           (Unit Tests)
            └── assets/         (Resources)
```

## Database Schema

### Database Configuration
- **Database Name**: `db_spp`
- **Connection URL**: `jdbc:mysql://localhost:3306/db_spp`
- **Default User**: `root`
- **Default Password**: `""` (empty)

### Tables

#### 1. `users` - User Management
| Column | Type | Description |
|--------|------|-------------|
| `id` | INT (PK, AUTO_INCREMENT) | Unique user identifier |
| `username` | VARCHAR(50) (UNIQUE) | Login username |
| `password` | VARCHAR(100) | User password (plain text) |
| `role` | ENUM('Kepsek','Bendahara','TU','Siswa') | User role |
| `nama_lengkap` | VARCHAR(100) | Full user name |
| `no_telepon` | VARCHAR(20) | Phone number (optional) |
| `is_active` | TINYINT(1) | Account status (1=active) |
| `created_at` | TIMESTAMP | Registration timestamp |

#### 2. `siswa` - Student Data
| Column | Type | Description |
|--------|------|-------------|
| `nis` | VARCHAR(20) (PK) | Student ID number |
| `user_id` | INT (FK to users.id) | Linked user account |
| `nama_lengkap` | VARCHAR(100) | Full student name |
| `kelas` | VARCHAR(20) | Class name |
| `tahun_ajaran` | VARCHAR(10) | Academic year |
| `no_telepon` | VARCHAR(20) | Phone number (optional) |
| `alamat` | TEXT | Student address |
| `nama_ortu` | VARCHAR(100) | Parent name |
| `nominal_spp` | DECIMAL(10,2) | Standard SPP amount |
| `total_potongan` | DECIMAL(10,2) | Total discount amount |
| `status_siswa` | ENUM('Aktif','Lulus','Pindah','Drop Out') | Student status |
| `created_at` | TIMESTAMP | Registration timestamp |

#### 3. `kelas` - Class Management
| Column | Type | Description |
|--------|------|-------------|
| `id` | INT (PK, AUTO_INCREMENT) | Class identifier |
| `kelas` | VARCHAR(20) | Class name (X, XI, XII) |
| `angkatan` | VARCHAR(10) | Academic year/batch |
| `nominal_spp` | DECIMAL(10,2) | SPP amount for this class |
| `created_at` | TIMESTAMP | Creation timestamp |

#### 4. `pembayaran` - Payment Transactions
| Column | Type | Description |
|--------|------|-------------|
| `id_transaksi` | VARCHAR(20) (PK) | Transaction ID |
| `nis_siswa` | VARCHAR(20) (FK) | Student ID |
| `nama_siswa` | VARCHAR(100) | Student name |
| `bulan_tahun` | VARCHAR(20) | Payment period (e.g., "Januari 2024") |
| `nominal_spp` | DECIMAL(10,2) | Standard SPP amount |
| `potongan` | DECIMAL(10,2) | Discount amount (deprecated) |
| `jumlah_bayar` | DECIMAL(10,2) | Amount paid |
| `tanggal_bayar` | DATETIME | Payment timestamp |
| `metode_pembayaran` | ENUM('Cash','Transfer','Kartu Debit') | Payment method |
| `status_pembayaran` | ENUM('Lunas','Belum Lunas','Cicilan') | Payment status |
| `keterangan` | TEXT | Additional notes |
| `user_input` | VARCHAR(50) (FK) | User who recorded payment |

#### 5. `potongan_spp` - Discount Management
| Column | Type | Description |
|--------|------|-------------|
| `id` | INT (PK, AUTO_INCREMENT) | Discount identifier |
| `nis_siswa` | VARCHAR(20) (FK) | Student ID |
| `nama_siswa` | VARCHAR(100) | Student name |
| `jenis_potongan` | ENUM('Beasiswa Prestasi','Beasiswa Kurang Mampu','Potongan Khusus','Anak Guru') | Discount type |
| `nominal_potongan` | DECIMAL(10,2) | Discount amount |
| `persentase_potongan` | DECIMAL(5,2) | Discount percentage |
| `periode_mulai` | DATE | Discount start date |
| `periode_selesai` | DATE | Discount end date |
| `status_potongan` | ENUM('Aktif','Selesai','Suspended') | Discount status |
| `keterangan` | TEXT | Additional notes |
| `user_input` | VARCHAR(50) (FK) | User who created discount |
| `created_at` | TIMESTAMP | Creation timestamp |

### Database Views

#### 1. `laporan_bulanan`
Monthly financial report view:
```sql
SELECT 
    DATE_FORMAT(tanggal_bayar, '%Y-%m') AS periode,
    COUNT(*) AS total_transaksi,
    SUM(jumlah_bayar) AS total_pemasukan,
    AVG(jumlah_bayar) AS rata_rata_bayar,
    SUM(CASE WHEN status_pembayaran = 'Lunas' THEN 1 ELSE 0 END) AS jumlah_lunas
FROM pembayaran 
GROUP BY DATE_FORMAT(tanggal_bayar, '%Y-%m')
ORDER BY periode DESC
```

#### 2. `siswa_tunggakan`
Students with outstanding payments view:
```sql
SELECT 
    s.nis, s.nama_lengkap, s.kelas, s.nominal_spp, s.total_potongan,
    (s.nominal_spp - s.total_potongan) AS spp_harus_bayar,
    COALESCE(p.total_bayar, 0) AS total_sudah_bayar,
    ((s.nominal_spp - s.total_potongan) - COALESCE(p.total_bayar, 0)) AS sisa_tunggakan
FROM siswa s 
LEFT JOIN (
    SELECT nis_siswa, SUM(jumlah_bayar) AS total_bayar 
    FROM pembayaran 
    WHERE DATE_FORMAT(tanggal_bayar, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')
    GROUP BY nis_siswa
) p ON s.nis = p.nis_siswa
WHERE s.status_siswa = 'Aktif' 
AND ((s.nominal_spp - s.total_potongan) - COALESCE(p.total_bayar, 0)) > 0
```

### Database Triggers

#### `update_potongan_siswa`
Automatically updates student's total discount when new discount is added:
```sql
CREATE TRIGGER update_potongan_siswa
AFTER INSERT ON potongan_spp
FOR EACH ROW
BEGIN
    UPDATE siswa 
    SET total_potongan = (
        SELECT COALESCE(SUM(nominal_potongan), 0)
        FROM potongan_spp 
        WHERE nis_siswa = NEW.nis_siswa 
        AND status_potongan = 'Aktif'
    )
    WHERE nis = NEW.nis_siswa;
END
```

## Data Models (Java)

### 1. User Model
**File**: `src/aplikasi/pembayaran/spp/model/User.java`

Represents system users with authentication and authorization.

**Properties**:
- `username`: Login identifier
- `password`: User password
- `role`: User role (Kepsek, Bendahara, TU, Siswa)
- `namaLengkap`: Full name
- `noTelepon`: Phone number
- `active`: Account status

**Key Methods**:
- `isValidRole()`: Validates user role
- `toString()`: Returns formatted user information

### 2. Siswa Model
**File**: `src/aplikasi/pembayaran/spp/model/Siswa.java`

Represents student data and academic information.

**Properties**:
- `nis`: Student identification number
- `namaLengkap`: Full student name
- `kelas`: Class assignment
- `tahunAjaran`: Academic year
- `nominalSPP`: Standard SPP amount
- `totalPotongan`: Total discount amount
- `statusSiswa`: Enrollment status

**Key Methods**:
- `validateSiswaData()`: Validates student data
- `createSiswaFromResultSet()`: Maps database result to object

### 3. Kelas Model
**File**: `src/aplikasi/pembayaran/spp/model/Kelas.java`

Represents class information and SPP rates.

**Properties**:
- `id`: Class identifier
- `kelas`: Class name (X, XI, XII)
- `angkatan`: Academic year/batch
- `nominalSPP`: SPP amount for this class

### 4. Pembayaran Model
**File**: `src/aplikasi/pembayaran/spp/model/Pembayaran.java`

Represents payment transactions.

**Properties**:
- `idTransaksi`: Unique transaction ID
- `nisSiswa`: Student ID
- `namaSiswa`: Student name
- `bulanTahun`: Payment period
- `nominalSPP`: Standard amount
- `jumlahBayar`: Amount paid
- `tanggalBayar`: Payment timestamp
- `metodePembayaran`: Payment method
- `statusPembayaran`: Payment status

### 5. Tagihan Model
**File**: `src/aplikasi/pembayaran/spp/model/Tagihan.java`

Represents billing information and payment status.

**Properties**:
- `nisSiswa`: Student ID
- `namaSiswa`: Student name
- `bulanTahun`: Billing period
- `nominalSPP`: Standard amount
- `potongan`: Discount amount
- `jumlahBayar`: Amount paid
- `sisaTagihan`: Remaining balance

**Key Methods**:
- `hitungSisaTagihan()`: Calculates remaining balance
- `isLunas()`: Checks if payment is complete
- `hasTunggakan()`: Checks for outstanding payments
- `getPersentasePembayaran()`: Calculates payment percentage

### 6. Koneksi Model
**File**: `src/aplikasi/pembayaran/spp/model/Koneksi.java`

Database connection and setup management.

**Properties**:
- Static connection instance
- Database configuration constants

**Key Methods**:
- `getConnection()`: Returns database connection
- `setupDatabase()`: Creates required tables
- `testConnection()`: Validates database connectivity

## Business Logic (Controllers)

### 1. UserController
**File**: `src/aplikasi/pembayaran/spp/controller/UserController.java`

Handles user authentication and management.

**Key Methods**:
- `login(String username, String password)`: User authentication
- `getAllUsers()`: Retrieves all users
- `getUserByUsername(String username)`: Gets specific user
- `createUser(User user)`: Creates new user
- `updateUser(User user)`: Updates user data
- `deleteUser(String username)`: Deletes user
- `hasPermission(String role, String action)`: Permission validation

**Role Permissions**:
- **Kepsek**: Read access to all modules
- **Bendahara**: Full payment management
- **TU**: Student and class management
- **Siswa**: Limited access to own data

### 2. SiswaController
**File**: `src/aplikasi/pembayaran/spp/controller/SiswaController.java`

Manages student data and academic records.

**Key Methods**:
- `getAllSiswa()`: Retrieves all students
- `getSiswaByNIS(String nis)`: Gets specific student
- `getSiswaByKelas(String kelas)`: Gets students by class
- `createSiswa(Siswa siswa)`: Creates new student record
- `updateSiswa(Siswa siswa)`: Updates student data
- `deleteSiswa(String nis)`: Deletes student record
- `searchSiswa(String keyword)`: Search functionality

### 3. KelasController
**File**: `src/aplikasi/pembayaran/spp/controller/KelasController.java`

Manages class information and SPP rates.

**Key Methods**:
- `getAllKelas()`: Retrieves all classes
- `getKelasById(int id)`: Gets specific class
- `getKelasByAngkatan(String angkatan)`: Gets classes by batch
- `createKelas(Kelas kelas)`: Creates new class
- `updateKelas(Kelas kelas)`: Updates class data
- `deleteKelas(int id)`: Deletes class

### 4. PembayaranController
**File**: `src/aplikasi/pembayaran/spp/controller/PembayaranController.java`

Handles payment transactions and financial operations.

**Key Methods**:
- `inputPembayaran(Pembayaran pembayaran, String currentUserRole)`: Records payment
- `getAllPembayaran()`: Retrieves all payments
- `getPembayaranByNIS(String nis)`: Gets student payments
- `getPembayaranByPeriod(String bulanTahun)`: Gets period payments
- `updatePembayaran(Pembayaran pembayaran)`: Updates payment record
- `generateLaporanKeuangan(String periode)`: Generates financial report
- `calculateTotalPemasukan(String periode)`: Calculates income
- `generateIdTransaksi()`: Creates unique transaction ID

### 5. TagihanController
**File**: `src/aplikasi/pembayaran/spp/controller/TagihanController.java`

Manages billing and outstanding payments.

**Key Methods**:
- `generateTagihanBulanan()`: Creates monthly bills
- `getTagihanByNIS(String nis)`: Gets student bills
- `getAllTagihan()`: Retrieves all bills
- `markAsLunas(String nis, String bulanTahun)`: Marks bill as paid
- `hitungTunggakan(String nis)`: Calculates outstanding payments
- `getSiswaTunggakan()`: Gets students with arrears

### 6. LaporanController
**File**: `src/aplikasi/pembayaran/spp/controller/LaporanController.java`

Generates various reports and analytics.

**Key Methods**:
- `generateLaporanKeuangan(String startDate, String endDate)`: Financial report
- `generateLaporanTunggakan()`: Arrears report
- `generateLaporanPembayaranPeriode(String periode)`: Period payment report
- `getStatistikPembayaran()`: Payment statistics
- `exportToExcel(List data, String filename)`: Excel export functionality

## Application Entry Point

### Main Class
**File**: `src/aplikasi/pembayaran/spp/AplikasiPembayaranSPP.java`

Application initialization and startup procedures.

**Initialization Sequence**:
1. Database connection testing
2. Database table setup
3. UI theme configuration
4. Login screen launch
5. Error handling and user feedback

**Key Methods**:
- `main(String[] args)`: Application entry point
- Database connection validation
- Error handling and logging

## Security Considerations

### Current Security Issues
1. **Plain Text Passwords**: Passwords stored without encryption
2. **SQL Injection**: Some queries may be vulnerable (though most use PreparedStatement)
3. **Input Validation**: Limited input validation in some areas
4. **Session Management**: No proper session timeout

### Recommended Improvements
1. **Password Encryption**: Implement BCrypt or similar
2. **Input Validation**: Add comprehensive validation
3. **Connection Pooling**: Implement HikariCP or similar
4. **Audit Logging**: Add comprehensive logging
5. **Role-Based Access Control**: Enhanced RBAC implementation

## Configuration

### Database Configuration
Located in `Koneksi.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/db_spp";
private static final String USER = "root";
private static final String PASS = "";
```

### Application Properties
- Build configuration: `build.xml`
- Manifest: `manifest.mf`
- Logging: Configured via Log4j2

## Development Guidelines

### Code Structure
- Follow MVC (Model-View-Controller) pattern
- Use prepared statements for database operations
- Implement proper exception handling
- Add meaningful comments for complex logic

### Database Operations
- Always use transactions for multi-step operations
- Implement proper connection management
- Use parameterized queries to prevent SQL injection
- Handle database errors gracefully

### Testing
- Unit tests in `src/aplikasi/pembayaran/spp/test/`
- Test database operations separately
- Mock external dependencies where possible

## Deployment

### Prerequisites
1. Java 8+ JRE/JDK
2. MySQL Server 8.0+
3. Required JAR files in classpath

### Database Setup
1. Create database `db_spp`
2. Import `db/db_spp.sql`
3. Update connection credentials in `Koneksi.java`

### Application Deployment
1. Compile Java source files
2. Package into JAR with dependencies
3. Ensure MySQL server is running
4. Run application with proper classpath

## Troubleshooting

### Common Issues
1. **Database Connection**: Check MySQL server status and credentials
2. **Class Not Found**: Verify all JAR dependencies are in classpath
3. **SQL Syntax**: Validate SQL statements against database schema
4. **Permission Errors**: Verify database user has required privileges

### Error Messages
- "Driver MySQL tidak ditemukan": Add MySQL connector to classpath
- "Gagal konek ke database": Check database server and credentials
- "Database connection failed": Verify database exists and is accessible

## Future Enhancements

### Recommended Features
1. **Web-based Interface**: Migrate from Swing to web
2. **API Layer**: RESTful API for mobile integration
3. **Real-time Notifications**: Email/SMS payment reminders
4. **Advanced Reporting**: Enhanced analytics and dashboards
5. **Payment Gateway Integration**: Online payment processing
6. **Multi-tenant Support**: Support for multiple schools

### Technical Improvements
1. **Framework Migration**: Spring Boot or Jakarta EE
2. **ORM Integration**: Hibernate or JPA implementation
3. **Caching Layer**: Redis or similar for performance
4. **Message Queue**: Asynchronous processing
5. **Containerization**: Docker deployment support

---

**Document Version**: 1.0  
**Last Updated**: December 2025  
**Author**: System Analysis Team
