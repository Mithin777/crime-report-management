package user;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import Database.DBConnection;

public class DeleteCrimeReport extends JFrame {
    private JTextField crimeNoField;
    private String username;

    public DeleteCrimeReport(String user) {
        this.username = user;
        setTitle("Delete Crime Report");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        panel.add(new JLabel("Crime Number:"));
        crimeNoField = new JTextField();
        panel.add(crimeNoField);

        JButton deleteBtn = new JButton("Delete Report");
        deleteBtn.setBackground(new Color(192, 57, 43));
        deleteBtn.setForeground(Color.WHITE);

        panel.add(new JLabel());
        panel.add(deleteBtn);

        add(panel);

        deleteBtn.addActionListener(e -> deleteReport());
    }

    private void deleteReport() {
        int crimeNo = Integer.parseInt(crimeNoField.getText());

        try (Connection conn = DBConnection.getConnection()) {
            String select = "SELECT fir_path, photo_path FROM crimes WHERE crime_no=? AND username=?";
            PreparedStatement stmt = conn.prepareStatement(select);
            stmt.setInt(1, crimeNo);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String firPath = rs.getString("fir_path");
                String photoPath = rs.getString("photo_path");

                // Delete record from DB
                String delete = "DELETE FROM crimes WHERE crime_no=? AND username=?";
                PreparedStatement delStmt = conn.prepareStatement(delete);
                delStmt.setInt(1, crimeNo);
                delStmt.setString(2, username);
                int rows = delStmt.executeUpdate();

                if (rows > 0) {
                    // Delete files from disk
                    deleteFile(firPath);
                    deleteFile(photoPath);

                    JOptionPane.showMessageDialog(this, "✅ Report and files deleted!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "⚠ Report not found or permission denied!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Report not found or permission denied!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteFile(String path) {
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
        }
    }
}
