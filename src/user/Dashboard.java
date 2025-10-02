package user;

import javax.swing.*;
import java.awt.*;
import Database.DBConnection;

public class Dashboard extends JFrame {
    public Dashboard(String username) {
        setTitle("Crime Report Management - Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        panel.setBackground(new Color(236, 240, 241));

        JLabel lbl = new JLabel("Welcome, " + username, JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(new Color(44, 62, 80));
        add(lbl, BorderLayout.NORTH);

        JButton addBtn = styledButton("➕ Add Report");
        JButton viewBtn = styledButton("📄 View Reports");
        JButton updateBtn = styledButton("✏ Update Report");
        JButton deleteBtn = styledButton("🗑 Delete Report");
        JButton logoutBtn = styledButton("🚪 Logout");

        panel.add(addBtn);
        panel.add(viewBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(logoutBtn);

        add(panel);

        addBtn.addActionListener(e -> new AddCrimeReport(username).setVisible(true));
        viewBtn.addActionListener(e -> new ViewCrimeReport().setVisible(true));
        updateBtn.addActionListener(e -> new UpdateCrimeReport(username).setVisible(true));
        deleteBtn.addActionListener(e -> new DeleteCrimeReport(username).setVisible(true));
        logoutBtn.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(39, 174, 96));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        return btn;
    }
}
