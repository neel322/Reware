package HealthTracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminScreen {

    public static void showAdminScreen() {

        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setSize(900, 500);

        String[] colNames = {"ID", "User", "Date", "Time", "Reason", "Doctor","specialization","status"};
        DefaultTableModel tableModel = new DefaultTableModel(colNames, 0);
        JTable table = new JTable(tableModel);

        try {
            Connection con = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM appointments ORDER BY a_id ASC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("a_id"),
                        rs.getString("user"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("reason"),
                        rs.getString("doctor"),
                        rs.getString("specialization"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(table);
        JButton manageAppointmentButton = new JButton("Manage Appointment");
        JButton viewUsersButton = new JButton("View Users");
        JButton logoutButton = new JButton("Logout");

        manageAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    Date sqlDate = (Date) tableModel.getValueAt(selectedRow, 2);
                    String date = sqlDate.toString();

                    Time sqlTime = (Time) tableModel.getValueAt(selectedRow, 3);
                    String time = sqlTime.toString();

                    String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);

                    showAppointmentManagement(id, date, time, currentStatus, selectedRow, tableModel);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an appointment first.");
                }
            }
        });

        viewUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NavigationManager.pushPage(adminFrame);
                JFrame userFrame = new JFrame("Registered Users");
                userFrame.setSize(600, 400);

                String[] cols = { "ID", "User ID", "Full Name", "Password" };
                DefaultTableModel model = new DefaultTableModel(cols, 0);
                JTable userTable = new JTable(model);

                try {
                    Connection con = DatabaseConnection.getConnection();
                    String sql = "SELECT id, user_id, full_name, password FROM users";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String uid = rs.getString("user_id");
                        String fullname = rs.getString("full_name");
                        String pass = rs.getString("password");
                        model.addRow(new Object[] { id, uid, fullname, pass });
                    }

                    rs.close();
                    ps.close();
                    con.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                JScrollPane scrollPane = new JScrollPane(userTable);

                JButton delUserBtn = new JButton("Delete Selected User");
                JButton editUserBtn = new JButton("Edit Selected User");
                JButton backBtn = new JButton("Back");

                delUserBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int selectedRow = userTable.getSelectedRow();
                        if (selectedRow >= 0) {
                            int userId = (int) model.getValueAt(selectedRow, 0);

                            try {
                                Connection con = DatabaseConnection.getConnection();
                                PreparedStatement ps = con.prepareStatement(
                                        "DELETE FROM users WHERE id = ?");
                                ps.setInt(1, userId);
                                int rows = ps.executeUpdate();
                                ps.close();
                                con.close();

                                if (rows > 0) {
                                    JOptionPane.showMessageDialog(userFrame, "User deleted successfully.");
                                    model.removeRow(selectedRow);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(userFrame, "Select a row first.");
                        }
                    }
                });

                editUserBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int selectedRow = userTable.getSelectedRow();
                        if (selectedRow >= 0) {
                            int userId = (int) model.getValueAt(selectedRow, 0);
                            String currentUid = (String) model.getValueAt(selectedRow, 1);
                            String currentName = (String) model.getValueAt(selectedRow, 2);
                            String currentPass = (String) model.getValueAt(selectedRow, 3);

                            String newUid = JOptionPane.showInputDialog(userFrame, "Edit User ID:", currentUid);
                            String newName = JOptionPane.showInputDialog(userFrame, "Edit Full Name:", currentName);
                            String newPass = JOptionPane.showInputDialog(userFrame, "Edit Password:", currentPass);

                            if (newUid != null && newName != null && newPass != null) {
                                try {
                                    Connection con = DatabaseConnection.getConnection();
                                    PreparedStatement ps = con.prepareStatement(
                                            "UPDATE users SET user_id = ?, full_name = ?, password = ? WHERE id = ?");
                                    ps.setString(1, newUid);
                                    ps.setString(2, newName);
                                    ps.setString(3, newPass);
                                    ps.setInt(4, userId);
                                    int rows = ps.executeUpdate();
                                    ps.close();
                                    con.close();

                                    if (rows > 0) {
                                        JOptionPane.showMessageDialog(userFrame, "User updated successfully.");

                                        model.setValueAt(newUid, selectedRow, 1);
                                        model.setValueAt(newName, selectedRow, 2);
                                        model.setValueAt(newPass, selectedRow, 3);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(userFrame, "Select a row first.");
                        }
                    }
                });

                backBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFrame previous = NavigationManager.popPage();
                        userFrame.dispose();
                        previous.setVisible(true);
                    }
                });

                JPanel panelSouth = new JPanel();
                panelSouth.setLayout(new GridLayout(1, 3, 10, 10));
                panelSouth.add(delUserBtn);
                panelSouth.add(editUserBtn);
                panelSouth.add(backBtn);

                userFrame.setLayout(new BorderLayout());
                userFrame.add(scrollPane, BorderLayout.CENTER);
                userFrame.add(panelSouth, BorderLayout.SOUTH);

                userFrame.setVisible(true);
            }
        });


        logoutButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.add(manageAppointmentButton);
        buttonPanel.add(viewUsersButton);
        buttonPanel.add(logoutButton);

        adminFrame.setLayout(new BorderLayout());
        adminFrame.add(scrollPane, BorderLayout.CENTER);
        adminFrame.add(buttonPanel, BorderLayout.SOUTH);

        adminFrame.setVisible(true);
    }

    static void showAppointmentManagement(int appointmentId, String date, String time,
                                               String currentStatus, int selectedRow, DefaultTableModel tableModel) {
        JFrame managementFrame = new JFrame("Manage Appointment");
        managementFrame.setSize(400, 400);
        managementFrame.setLayout(new GridLayout(3, 1, 10, 10));
        managementFrame.setLocationRelativeTo(null);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Appointment Details"));
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(String.valueOf(appointmentId)));
        infoPanel.add(new JLabel("Date:"));
        infoPanel.add(new JLabel(date));
        infoPanel.add(new JLabel("Time:"));
        infoPanel.add(new JLabel(time));
        infoPanel.add(new JLabel("Current Status:"));
        infoPanel.add(new JLabel(currentStatus));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton updateStatusButton = new JButton("Update Status");
        JButton deleteButton = new JButton("Delete Appointment");

        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newStatus = (String) JOptionPane.showInputDialog(
                        managementFrame,
                        "Select new status:",
                        "Update Appointment Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Scheduled", "Completed", "Cancelled", "Rescheduled"},
                        currentStatus
                );

                if (newStatus != null && !newStatus.equals(currentStatus)) {
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        String sql = "UPDATE appointments SET status = ? WHERE a_id = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, newStatus);
                        ps.setInt(2, appointmentId);

                        int rows = ps.executeUpdate();
                        ps.close();
                        con.close();

                        if (rows > 0) {
                            tableModel.setValueAt(newStatus, selectedRow, 7);
                            JOptionPane.showMessageDialog(managementFrame,
                                    "Status updated successfully to: " + newStatus);
                            managementFrame.dispose();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(managementFrame,
                                "Failed to update status: " + ex.getMessage());
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        managementFrame,
                        "Are you sure you want to delete this appointment?\nThis action cannot be undone.",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement(
                                "DELETE FROM appointments WHERE a_id = ?");
                        ps.setInt(1, appointmentId);
                        int rows = ps.executeUpdate();
                        ps.close();
                        con.close();

                        if (rows > 0) {
                            tableModel.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(managementFrame,
                                    "Appointment deleted successfully!");
                            managementFrame.dispose();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(managementFrame,
                                "Failed to delete appointment: " + ex.getMessage());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        buttonPanel.add(updateStatusButton);
        buttonPanel.add(deleteButton);

        JPanel closePanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> managementFrame.dispose());
        closePanel.add(closeButton);

        managementFrame.add(infoPanel);
        managementFrame.add(buttonPanel);
        managementFrame.add(closePanel);

        managementFrame.setVisible(true);
    }
}
