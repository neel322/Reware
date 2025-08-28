package HealthTracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseConnection {

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String DB_URL = "jdbc:mysql://localhost:3306/health_tracker";
        String DB_USER = "root";
        String DB_PASSWORD = "";
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertHealthMetric(String date, String bp, String hr, String bo, String cal, String user)
            throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        Date sqlDate = Date.valueOf(localDate);
        long bp_long = Long.parseLong(bp);
        long hr_long = Long.parseLong(hr);
        int bo_int = Integer.parseInt(bo);
        long cal_long = Long.parseLong(cal);

        String sql = "INSERT INTO healthmetrics(date,bp,heartRate,bloodOxygen,calories,user) VALUES (?,?,?,?,?,?)";
        try (Connection con = getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDate(1, sqlDate);
            pstmt.setLong(2, bp_long);
            pstmt.setLong(3, hr_long);
            pstmt.setInt(4, bo_int);
            pstmt.setLong(5, cal_long);
            pstmt.setString(6, user);
            pstmt.executeUpdate();
            System.out.println("Health metric inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting health metric: " + e.getMessage());
            throw e;
        }
    }

    public static void insertAppointment(String date, String time, String reason, String doctor, String specialization,
                                         String status, String user) throws SQLException {

        String sql = "INSERT INTO appointments (date, time, reason, doctor, specialization, status, user) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, date);
            pstmt.setString(2, time);
            pstmt.setString(3, reason);
            pstmt.setString(4, doctor);
            pstmt.setString(5, specialization);
            pstmt.setString(6, status);
            pstmt.setString(7, user);

            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
