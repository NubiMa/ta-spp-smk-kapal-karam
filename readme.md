# SPP Payment System - Project Analysis Report

## Project Overview

The **SPP Payment System** is a Java-based desktop application designed for managing school tuition fee payments in Indonesian educational institutions. The application provides a comprehensive solution for handling student payments, tracking outstanding balances, and generating financial reports.

## Project Structure

The application follows a Model-View-Controller (MVC) architecture with the following structure:

```
src/
└── aplikasi/
    └── pembayaran/
        └── spp/
            ├── AplikasiPembayaranSPP.java (Main class)
            ├── assets/
            ├── controller/
            │   ├── PembayaranController.java
            │   ├── SiswaController.java
            │   ├── TagihanController.java
            │   └── UserController.java
            ├── model/
            │   ├── Koneksi.java
            │   ├── Pembayaran.java
            │   ├── Siswa.java
            │   ├── Tagihan.java
            │   └── User.java
            └── view/
                ├── DashboardAdmin.java
                ├── DashboardKepsek.java
                ├── DashboardSiswa.java
                ├── DashboardTU.java
                ├── FormDataSiswa.java
                ├── FormEditUser.java
                ├── FormInputPembayaran.java
                ├── FormMonitorTunggakan.java
                ├── FormTambahEditSiswa.java
                ├── FormTambahUser.java
                ├── LoginPage.java
                └── SplashScreen.java
```

## Technical Stack

- **Language**: Java 8
- **Database**: MySQL 8.0
- **GUI Framework**: Swing
- **Database Connector**: MySQL Connector/J 9.4.0
- **Additional Library**: JCalendar 1.4 for date selection
- **IDE**: NetBeans (based on project configuration)

## Database Schema

The application uses a MySQL database named `db_spp` with the following key tables:

### Core Tables:
- **users**: Stores application users with roles (Kepsek, Bendahara, TU, Siswa)
- **siswa**: Student information including NIS (Student ID), class, and SPP nominal
- **pembayaran**: Payment transactions with details like amount, date, and status
- **potongan_spp**: Discount and scholarship information for students

### Views:
- **laporan_bulanan**: Monthly financial reports
- **siswa_tunggakan**: Student outstanding balances

### Key Features:

1. **Multi-role Access Control**:
   - Kepsek (Principal): Administrative functions
   - Bendahara (Treasurer): Financial management and payment processing
   - TU (Administrative Staff): Basic operations and data entry
   - Siswa (Student): Limited access to view payment status

2. **Payment Management**:
   - Record SPP payments with different payment methods (Cash, Transfer, Debit Card)
   - Track payment status (Lunas/Cicilan/Belum Lunas)
   - Handle discounts and scholarships
   - Automatic generation of transaction IDs

3. **Student Management**:
   - Maintain student records with NIS, class, and contact information
   - Track student payment history
   - Manage outstanding balances

4. **Discount Management**:
   - Support for various discount types (Beasiswa Prestasi, Beasiswa Kurang Mampu, etc.)
   - Percentage and fixed amount discounts
   - Time-limited discount periods

5. **Reporting**:
   - Monthly financial reports
   - Outstanding payment tracking
   - Transaction history

## User Interface

The application features a role-based dashboard system with different interfaces for each user type:

- **SplashScreen**: Loading screen with progress indication
- **LoginPage**: Authentication interface
- **Dashboard Components**: Role-specific dashboards with appropriate features
- **Form Components**: Data entry and management interfaces

## Key Classes and Components

### AplikasiPembayaranSPP.java
The main application class that initializes the database connection and launches the splash screen.

### Koneksi.java
Database connection management with built-in connection pooling and error handling.

### UserController.java
Handles user authentication, registration, and profile management.

### PembayaranController.java
Manages payment transactions, validation, and business logic.

### FormInputPembayaran.java
A comprehensive payment entry form with automatic calculation and validation.

## Database Configuration

The application connects to MySQL using these parameters:
- **URL**: jdbc:mysql://localhost:3306/db_spp
- **User**: root
- **Password**: (empty)

## Security Features

- Role-based access control
- Input validation to prevent SQL injection
- Proper separation of user permissions
- Session management through role-based dashboards

## Project Configuration

The project uses:
- NetBeans project structure
- Standard Java compilation with Java 8
- External JAR dependencies (MySQL connector and JCalendar)
- Main class configured as SplashScreen in project properties

## Summary

This is a well-structured, role-based desktop application for managing school tuition payments with comprehensive functionality for different user types. The application follows proper MVC architecture and includes features for payment processing, student management, financial reporting, and discount management.