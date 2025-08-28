package HealthTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.HashMap;

import static HealthTracker.DatabaseConnection.getConnection;

public class HealthTrackerGUI {

    public static final HashMap<String, String> userCredentials = new HashMap<>();

    static {
        checkUserCredentials();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Personal Health Tracker Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JLabel titleLabel = new JLabel("Personal Health Tracker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        northPanel.add(titlePanel, BorderLayout.CENTER);
        frame.getContentPane().add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        frame.add(centerPanel, BorderLayout.CENTER);
        loginComponents(centerPanel);

        frame.setVisible(true);
    }

    public static void loginComponents(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userLabel = new JLabel("User ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(userLabel, gbc);

        JTextField userText = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(userText, gbc);

        JLabel password = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(password, gbc);

        JPasswordField passwordText = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordText, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        JButton signUpButton = new JButton("Sign Up");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(signUpButton, gbc);

        loginButton.addActionListener(e -> {
            String user = userText.getText();
            String pw = new String(passwordText.getPassword());

            if (user.equals("admin") && pw.equals("admin123")) {
                JOptionPane.showMessageDialog(panel, "Admin Login Successful");
                AdminScreen.showAdminScreen();
                return;
            }

            try {
                if (userCredentials.get(user).equals(pw)) {
                    JOptionPane.showMessageDialog(panel, "Login successful");
                    MainScreen.showMainScreen(user);
                } else {
                    JOptionPane.showMessageDialog(panel, "User Does Not Exist Please Sign Up");
                }
            } catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(panel, "User Does Not Exist Please Sign Up");
            }
        });

        signUpButton.addActionListener(e -> {
            try {
                showNewUserDialog(panel);
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        });
    }

    public static void showNewUserDialog(JPanel panel) throws Exception {
        File f = new File("D:\\code\\user.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        JDialog dialog = new JDialog((Frame) null, "New User Sign Up", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        JLabel newUserLabel = new JLabel("User ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(newUserLabel, gbc);

        JTextField newUserText = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        dialog.add(newUserText, gbc);

        JLabel name = new JLabel("Full Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(name, gbc);

        JTextField nameText = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        dialog.add(nameText, gbc);

        JLabel newpassword = new JLabel("Set Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(newpassword, gbc);

        JPasswordField newPasswordText = new JPasswordField(25);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        dialog.add(newPasswordText, gbc);

        JButton signUpButton = new JButton("Sign Up");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(signUpButton, gbc);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newUser = newUserText.getText();
                String name = nameText.getText();
                String newPwd = new String(newPasswordText.getPassword());
                if (name.matches("[a-zA-Z ]+")){
                    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (!line.startsWith("User ID:")) continue;
                            String user_id = line.split(":")[1].trim();

                            line = br.readLine();
                            if (line == null || !line.startsWith("Password:")) continue;
                            String password = line.split(":")[1].trim();

                            if (user_id.equals(newUser) && password.equals(newPwd)) {
                                JOptionPane.showMessageDialog(dialog, "User ID already exists.", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    if (!newUser.isEmpty() && !newPwd.isEmpty()) {
                        try {
                            bw.write("User ID: " + newUser);
                            bw.newLine();
                            bw.write("Password: " + newPwd);
                            bw.newLine();
                            userCredentials.put(newUser, newPwd);
                            String sql = "{call insert_user(?, ?, ?)}";

                            try (Connection con = getConnection();
                                 CallableStatement cstmt = con.prepareCall(sql)) {

                                cstmt.setString(1, newUser);
                                cstmt.setString(2, name);
                                cstmt.setString(3, newPwd);
                                cstmt.executeUpdate();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(panel, "Sign Up successful! You can now log in.");
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "User ID or password are Empty.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
        bw.flush();
        bw.close();
    }

    public static void checkUserCredentials() {
        File f = new File("D:\\code\\user.txt");//stores the user id and pass
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String user = null;
            String password = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("User ID: ")) {
                    user = line.split(": ")[1];
                } else if (line.startsWith("Password: ")) {
                    password = line.split(": ")[1];
                }
                if (user != null && password != null) {
                    userCredentials.put(user, password);
                    user = null;
                    password = null;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
