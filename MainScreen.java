package HealthTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;

public class MainScreen {
    static String currentUser;

    public static void showMainScreen(String user) {
        currentUser = user;
        JFrame mainframe = new JFrame("Personal Health Tracker Main Screen");
        mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainframe.setSize(400, 400);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainframe.add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 50, 15, 50);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 30;
        gbc.ipadx = 30;

        JButton healthMetricsButton = new JButton("Add Health Metrics");
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(healthMetricsButton, gbc);

        JButton appointmentsButton = new JButton("Book Appointments");
        gbc.gridy = 1;
        mainPanel.add(appointmentsButton, gbc);

        JButton userManagementButton = new JButton("Show User Management");
        gbc.gridy = 2;
        mainPanel.add(userManagementButton, gbc);

        JButton updateAppointmentButton = new JButton("Update Appointments Status");
        gbc.gridy = 3;
        mainPanel.add(updateAppointmentButton, gbc);

        JButton removeAppointmentsButton = new JButton("Remove Appointments");
        gbc.gridy = 4;
        mainPanel.add(removeAppointmentsButton, gbc);

        JButton bmiButton = new JButton("BMI Calculator");
        gbc.gridy = 5;
        mainPanel.add(bmiButton, gbc);

        JButton logOutButton = new JButton("LogOut");
        gbc.gridy = 6;
        mainPanel.add(logOutButton, gbc);

        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        healthMetricsButton.setFont(buttonFont);
        appointmentsButton.setFont(buttonFont);
        userManagementButton.setFont(buttonFont);
        updateAppointmentButton.setFont(buttonFont);
        removeAppointmentsButton.setFont(buttonFont);
        bmiButton.setFont(buttonFont);
        logOutButton.setFont(buttonFont);


        healthMetricsButton.addActionListener(e -> {
            NavigationManager.pushPage(mainframe);
            showHealthMetrics(currentUser);
            mainframe.setVisible(false);
        });

        appointmentsButton.addActionListener(e -> {
            NavigationManager.pushPage(mainframe);
            try {
                addAppointments(currentUser);
                mainframe.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        userManagementButton.addActionListener(e -> {
            NavigationManager.pushPage(mainframe);
            try {
                showUserManagement(currentUser);
                mainframe.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        updateAppointmentButton.addActionListener(e -> {
            try {
                updateAppointmentStatus();
                mainframe.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        removeAppointmentsButton.addActionListener(e -> {
            try {
                NavigationManager.pushPage(mainframe);
                removeSelectedAppointment();
                mainframe.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        bmiButton.addActionListener(e -> calculateBMI());
        logOutButton.addActionListener(e -> System.exit(0));

        mainframe.setVisible(true);
    }

    public static void showUserManagement(String user) throws Exception {
        JFrame userManagementFrame = new JFrame("User Health Management");
        userManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userManagementFrame.setSize(800, 400);
        JTabbedPane tabbedPane = new JTabbedPane();
        userManagementFrame.add(tabbedPane, BorderLayout.CENTER);
        JPanel back =new JPanel();
        userManagementFrame.add(back,BorderLayout.SOUTH);
        JPanel metricsPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Health Metrics", metricsPanel);
        String[] columnNames = { "User", "Date", "Blood Pressure", "Heart Rate", "Blood Oxygen", "Calories Burned" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable metricsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(metricsTable);
        metricsPanel.add(scrollPane, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        JButton backButton = new JButton("Back");
        gbc.gridx = 0;
        gbc.gridy = 6;
        back.add(backButton, gbc);

        backButton.addActionListener(e -> {
                JFrame previous = NavigationManager.popPage();
                userManagementFrame.dispose();
                previous.setVisible(true);
        });

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "Select * from healthmetrics where user=? order by h_id asc ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("user"),
                        rs.getDate("date"),
                        rs.getLong("bp"),
                        rs.getLong("heartRate"),
                        rs.getInt("bloodOxygen"),
                        rs.getLong("calories")
                });
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel appointmentsPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Appointments", appointmentsPanel);
        String[] apptColumnNames = { "User", "Date", "Time", "Reason", "Doctor","Status" };
        DefaultTableModel apptTableModel = new DefaultTableModel(apptColumnNames, 0);
        JTable appointmentsTable = new JTable(apptTableModel);
        JScrollPane apptScrollPane = new JScrollPane(appointmentsTable);
        appointmentsPanel.add(apptScrollPane, BorderLayout.CENTER);
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "Select * from appointments where user=? Order by a_id asc";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                apptTableModel.addRow(new Object[] {
                        rs.getString("user"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("reason"),
                        rs.getString("doctor"),
                        rs.getString("status")
                });
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel oldappointmentsPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Appointments History", oldappointmentsPanel);
        String[] oldapptColumnNames = { "User", "Date", "Time", "Reason", "Doctor", "Status", "deletedAt" };
        DefaultTableModel oldapptTableModel = new DefaultTableModel(oldapptColumnNames, 0);
        JTable oldappointmentsTable = new JTable(oldapptTableModel);
        JScrollPane oldapptScrollPane = new JScrollPane(oldappointmentsTable);
        oldappointmentsPanel.add(oldapptScrollPane, BorderLayout.CENTER);
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql5 = "SELECT * FROM appointmentsbackup where user=?";
            PreparedStatement pstmt = conn.prepareStatement(sql5);
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                oldapptTableModel.addRow(new Object[] {
                        rs.getString("user"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("reason"),
                        rs.getString("doctor"),
                        rs.getString("status"),
                        rs.getTimestamp("deleted_at")
                });
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userManagementFrame.setVisible(true);
    }

    private static void updateAppointmentStatus() {
        String updateDate = JOptionPane.showInputDialog("Enter appointment date (YYYY-MM-DD):");
        String updateTime = JOptionPane.showInputDialog("Enter appointment time (HH:MM):");
        String newStatus = (String) JOptionPane.showInputDialog(null,
                "Select new status:", "Update Status",
                JOptionPane.QUESTION_MESSAGE, null,
                Appointment.VALID_STATUSES.toArray(), Appointment.VALID_STATUSES.get(0));

        if (newStatus != null) {
            Appointment.updateAppointmentStatus(updateDate, updateTime, newStatus);
        }
    }

    public static void showHealthMetrics(String user) {
        JFrame metricsFrame = new JFrame("Health Metrics");
        metricsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        metricsFrame.setSize(800, 400);
        String[] columnNames = { "Date", "Blood Pressure", "Heart Rate", "Blood Oxygen", "Calories Burned" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable metricsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(metricsTable);
        JButton addButton = new JButton("Add Metric");
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if (!NavigationManager.isEmpty()) {
                JFrame previous = NavigationManager.popPage();
                metricsFrame.dispose();
                previous.setVisible(true);
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel inputPanel = new JPanel(new GridLayout(0, 2));
                inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
                JTextField dateField = new JTextField();
                inputPanel.add(dateField);
                inputPanel.add(new JLabel("Blood Pressure (mmHg):"));
                JTextField bpField = new JTextField();
                inputPanel.add(bpField);
                inputPanel.add(new JLabel("Heart Rate (bpm):"));
                JTextField hrField = new JTextField();
                inputPanel.add(hrField);
                inputPanel.add(new JLabel("Blood Oxygen (%):"));
                JTextField boField = new JTextField();
                inputPanel.add(boField);
                inputPanel.add(new JLabel("Calories Burned (kcal):"));
                JTextField calField = new JTextField();
                inputPanel.add(calField);
                int result = JOptionPane.showConfirmDialog(metricsFrame, inputPanel, "Add New Metric Entry",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String date = dateField.getText().trim();
                        int bp = Integer.parseInt(bpField.getText().trim());
                        int hr = Integer.parseInt(hrField.getText().trim());
                        int bo = Integer.parseInt(boField.getText().trim());
                        int cal = Integer.parseInt(calField.getText().trim());

                        LocalDate now = LocalDate.now();
                        LocalDate appdate = LocalDate.parse(dateField.getText().trim());

                        if (appdate.isAfter(now)) {
                            if (hr < 40 || hr > 180) {
                                JOptionPane.showMessageDialog(metricsFrame,
                                        "Heart Rate should be between 40 and 180 bpm.\n If entered Correct please consult a doctor");
                                return;
                            }

                            if (bo < 90 || bo > 100) {
                                JOptionPane.showMessageDialog(metricsFrame,
                                        "Blood Oxygen should be between 90% and 100%.\nIf entered Correct please consult a doctor");
                                return;
                            }

                            if (cal < 0 || cal > 5000) {
                                JOptionPane.showMessageDialog(metricsFrame,
                                        "Calories must be between 0 and 5000.\nIf entered Correct please consult a doctor");
                                return;
                            }

                            if (bp < 80 || bp > 200) {
                                JOptionPane.showMessageDialog(metricsFrame,
                                        "Blood Pressure should be within 80 - 200 mmHg.\nIf entered Correct please consult a doctor");
                                return;
                            }
                            tableModel.addRow(new Object[] { date, bp, hr, bo, cal });
                            String  blp= bpField.getText();
                            String  her= hrField.getText();
                            String  blo= boField.getText();
                            String  calo= calField.getText();
                            try {
                                DatabaseConnection.insertHealthMetric(date,blp, her, blo, calo, user);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            JOptionPane.showMessageDialog(metricsFrame, "Metrics added successfully!");
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "Enter Proper date" , "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(metricsFrame,
                                "Please enter valid numeric values for all metrics.");
                    }
                }
            }
        });
        metricsFrame.setLayout(new BorderLayout());
        metricsFrame.add(scrollPane, BorderLayout.CENTER);
        JPanel back=new JPanel(new GridLayout(0,2));
        metricsFrame.add(back,BorderLayout.SOUTH);
        back.add(addButton);
        back.add(backButton);
        metricsFrame.setVisible(true);
    }

    public static void addAppointments(String user) throws Exception {
        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        JTextField dateField = new JTextField();
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Time (HH:MM):"));
        JTextField timeField = new JTextField();
        inputPanel.add(timeField);
        inputPanel.add(new JLabel("Reason:"));
        JTextField reasonField = new JTextField();
        inputPanel.add(reasonField);
        inputPanel.add(new JLabel("Doctor:"));
        JTextField doctorField = new JTextField();
        inputPanel.add(doctorField);
        inputPanel.add(new JLabel("Specialization:"));
        String[] Specialization = {"Cardiology", "Clinical Immunology/Allergy", "Dermatology", "Endocrinology and Metabolism",
                "Gastroenterology", "Hematology", "Neurology", "Obstetrics/Gynecology", "Otolaryngology"};
        JComboBox<String> specializationField = new JComboBox<>(Specialization);
        inputPanel.add(specializationField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Schedule New Appointment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String date = dateField.getText();
            String time = timeField.getText();
            String reason = reasonField.getText();
            String doctor = doctorField.getText();
            String specialization = specializationField.getSelectedItem().toString();
            String status = Appointment.SCHEDULED;
            try {
                DatabaseConnection.insertAppointment(date,time,reason,doctor,specialization,status,user);

                Appointment.addAppointment(date, time, reason, doctor, specialization, status);

                JOptionPane.showMessageDialog(null, "Appointment booked successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error scheduling appointment: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void calculateBMI() {
        String ageInput = JOptionPane.showInputDialog(null, "Enter your age:", "BMI Calculator", JOptionPane.PLAIN_MESSAGE);
        try {
            int age = Integer.parseInt(ageInput);

            if (age < 2 || age > 120) {
                JOptionPane.showMessageDialog(null, "Age must be between 2 and 120.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String weightInput = JOptionPane.showInputDialog(null, "Enter your weight (kg):", "BMI Calculator", JOptionPane.PLAIN_MESSAGE);
            String heightInput = JOptionPane.showInputDialog(null, "Enter your height (cm):", "BMI Calculator", JOptionPane.PLAIN_MESSAGE);

            double weight = Double.parseDouble(weightInput);
            double heightCm = Double.parseDouble(heightInput);
            double heightM = heightCm / 100;
            double bmi = weight / (heightM * heightM);
            DecimalFormat df = new DecimalFormat("#.##");
            String result;

            if (age < 18) {
                result = "For children/adolescents, BMI must be checked against growth charts.\n" +
                        "Your BMI is " + df.format(bmi) + ".\n" +
                        "Consult a doctor or use CDC/WHO growth charts for percentile.";
            } else {
                String bmiCategory;
                if (bmi < 18.5) {
                    bmiCategory = "Underweight";
                } else if (bmi < 25) {
                    bmiCategory = "Normal weight";
                } else if (bmi < 30) {
                    bmiCategory = "Overweight";
                } else if (bmi < 35) {
                    bmiCategory = "Obese (Class 1)";
                } else if (bmi < 40) {
                    bmiCategory = "Obese (Class 2)";
                } else {
                    bmiCategory = "Obese (Class 3)";
                }
                result = "Your BMI is " + df.format(bmi) + ": " + bmiCategory+"\nNormal weight BMI Index should be between 20 to 25";
            }

            JOptionPane.showMessageDialog(null, result, "BMI Result", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input! Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    }

    public static void removeSelectedAppointment() {
        JFrame removeAppointment = new JFrame("Remove Appointment");
        JPanel appointmentsPanel = new JPanel(new BorderLayout());
        String[] apptColumnNames = { "User", "Date", "Time", "Reason", "Doctor", "Status" };
        DefaultTableModel apptTableModel = new DefaultTableModel(apptColumnNames, 0);
        JTable appointmentsTable = new JTable(apptTableModel);
        JScrollPane apptScrollPane = new JScrollPane(appointmentsTable);

        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back");

        backButton.addActionListener(e -> {
            if (!NavigationManager.isEmpty()) {
                JFrame previous = NavigationManager.popPage();
                removeAppointment.dispose();
                previous.setVisible(true);
            }
        });
        JButton deleteAppointmentButton = new JButton("Delete Selected Appointment");
        JButton removeExpiredAppointment = new JButton("Remove Expired Appointment");

        buttonPanel.add(deleteAppointmentButton);
        buttonPanel.add(removeExpiredAppointment);
        buttonPanel.add(backButton);
        appointmentsPanel.add(apptScrollPane, BorderLayout.CENTER);
        appointmentsPanel.add(buttonPanel, BorderLayout.SOUTH);
        removeAppointment.add(appointmentsPanel);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM appointments WHERE user = ? ORDER BY a_id ASC")) {

            pstmt.setString(1, currentUser);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                apptTableModel.addRow(new Object[] {
                        rs.getString("user"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("reason"),
                        rs.getString("doctor"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        deleteAppointmentButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String appointmentUser = apptTableModel.getValueAt(selectedRow, 0).toString();
                Date sqlDate = (Date) apptTableModel.getValueAt(selectedRow, 1);
                String time = apptTableModel.getValueAt(selectedRow, 2).toString();

                int confirm = JOptionPane.showConfirmDialog(
                        appointmentsPanel,
                        "Are you sure you want to delete this appointment?\n" +
                                "Date: " + sqlDate + "\n" +
                                "Time: " + time,
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(
                                 "DELETE FROM appointments WHERE date = ? AND time = ? AND user = ?")) {

                        pstmt.setDate(1, sqlDate);
                        pstmt.setString(2, time);
                        pstmt.setString(3, appointmentUser);

                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            apptTableModel.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(appointmentsPanel,
                                    "Appointment deleted successfully!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(appointmentsPanel,
                                    "Failed to delete appointment.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(appointmentsPanel,
                                "Error deleting appointment: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(appointmentsPanel,
                        "Please select an appointment to delete.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        removeExpiredAppointment.addActionListener(e -> {
            try {
                removeExpiredAppointments();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(appointmentsPanel,
                        "Error removing expired appointments: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        removeAppointment.pack();
        removeAppointment.setVisible(true);
    }

    public static void removeExpiredAppointments() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        boolean hasDeleted = false;
        boolean hasError = false;

        String fetchSql = "SELECT date, time, reason, doctor, specialization, status FROM appointments";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(fetchSql)) {

            while (rs.next()) {
                String date = rs.getString("date");
                String time = rs.getString("time");
                String reason = rs.getString("reason");
                String doctor = rs.getString("doctor");
                String specialization = rs.getString("specialization");
                String status = rs.getString("status");
                Appointment.addAppointmentSilently(date, time, reason, doctor, specialization, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching appointments: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error fetching appointments: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Appointment.Node current = Appointment.head;
        while (current != null) {
            Appointment app = current.appointment;
            LocalDate appointmentDate = LocalDate.parse(app.getDate(), formatter);

            if (appointmentDate.isBefore(now)) {
                Appointment.removeAppointment(app.getDate(), app.getTime());

                String deleteSql = "DELETE FROM appointments WHERE date = ? AND time = ?";
                try (Connection con = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = con.prepareStatement(deleteSql)) {

                    pstmt.setString(1, app.getDate());
                    pstmt.setString(2, app.getTime());
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        hasDeleted = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error removing appointment from database: " + e.getMessage());
                    hasError = true;
                }
            }
            current = current.next;
        }

        if (hasError) {
            JOptionPane.showMessageDialog(null, "Some appointments could not be deleted due to errors.",
                    "Deletion Error", JOptionPane.ERROR_MESSAGE);
        } else if (hasDeleted) {
            JOptionPane.showMessageDialog(null, "Expired appointments have been successfully deleted.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No expired appointments were found to delete.", "No Deletions",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
