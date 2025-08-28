🩺 Personal Health Tracker System
📌 Overview

The Personal Health Tracker is a Java Swing + MySQL based desktop application that allows users to track their health metrics, book appointments, and manage their profiles.
It also provides an Admin Dashboard to manage users and appointments, backed by database triggers for validation and backups.

🚀 Features
👤 User

Login & Signup with credentials stored in DB and local file (user.txt).

Add Health Metrics (Blood Pressure, Heart Rate, Blood Oxygen, Calories).

Book Appointments with doctors (date, time, reason, specialization).

BMI Calculator with category classification.

Secure User Data with CRUD operations.

🛠️ Admin

Admin Login (admin / admin123 by default).

View & Delete Appointments (auto backup before deletion).

View, Edit & Delete Users.

Navigate Between Screens using a custom NavigationManager (Stack DS).

Back Button implemented with stack-based navigation.

⚡ Database

Triggers:

Prevent appointments outside range today → today + 2 years.

Backup deleted appointments into AppointmentsBackup.

Stored Procedure: insert_user for new user registration.

Tables: users, appointments, appointmentsbackup, healthmetrics.

🗂️ File Structure
HealthTracker/
│
├── AdminScreen.java          # Admin Dashboard (appointments + users management)
├── Appointment.java          # Appointment model with Node subclass (Linked List DS)
├── AppointmentList.java      # Linked List to manage appointments
├── DatabaseConnection.java   # MySQL connection utility
├── HealthTrackerGUI.java     # Login & Signup window
├── MainScreen.java           # Main user dashboard (metrics, BMI, appointments)
├── NavigationManager.java    # Stack DS for screen navigation
└── (Other helper classes...)

🛢️ Database Schema (MySQL)

Database Name: health_tracker

Tables

users (id, user_id, full_name, password)

appointments (a_id, date, time, reason, doctor, specialization, user, status)

appointmentsbackup (backup of deleted appointments)

healthmetrics (h_id, date, bp, heartRate, bloodOxygen, calories, user)

⚙️ Technologies Used

Java Swing (GUI)

JDBC (MySQL Connector) for database operations

MySQL / MariaDB for storage

Data Structures:

Linked List → Appointment management

Stack → Navigation between screens

OOP Concepts: Encapsulation, Abstraction, Inheritance, Polymorphism

▶️ How to Run

Import into IntelliJ IDEA as a Java project.

Setup the database:

Import the provided health_tracker.sql into phpMyAdmin / MySQL.

Ensure database credentials in DatabaseConnection.java match your MySQL config.

Login:

Admin → admin / admin123

User → Register with SignUp.
