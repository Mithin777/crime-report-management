package user;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import Database.DBConnection;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Crime Report Management - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Crime Report Login", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setOpaque(true);
        title.setBackground(new Color(44, 62, 80));
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(new Color(236, 240, 241));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(39, 174, 96));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);

        formPanel.add(new JLabel());
        formPanel.add(loginBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        loginBtn.addActionListener(e -> authenticate());
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "✅ Login Successful!");
                dispose();
                new Dashboard(username).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid Credentials!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }
}
