package user;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import Database.DBConnection;

public class UpdateCrimeReport extends JFrame {
    private JTextField crimeNoField, locationField, typeField;
    private JTextArea infoArea, witnessArea;
    private JLabel firLabel, photoLabel;
    private String firPath = "", photoPath = "";
    private String username;

    public UpdateCrimeReport(String user) {
        this.username = user;
        setTitle("Update Crime Report");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        panel.add(new JLabel("Crime Number:"));
        crimeNoField = new JTextField();
        panel.add(crimeNoField);

        JButton loadBtn = new JButton("Load Report");
        panel.add(loadBtn);
        panel.add(new JLabel());

        panel.add(new JLabel("Location:"));
        locationField = new JTextField();
        panel.add(locationField);

        panel.add(new JLabel("Type of Crime:"));
        typeField = new JTextField();
        panel.add(typeField);

        panel.add(new JLabel("Witness:"));
        witnessArea = new JTextArea();
        panel.add(new JScrollPane(witnessArea));

        panel.add(new JLabel("Additional Info:"));
        infoArea = new JTextArea();
        panel.add(new JScrollPane(infoArea));

        JButton firBtn = new JButton("Replace FIR File");
        firLabel = new JLabel("No file selected");
        firBtn.addActionListener(e -> chooseFIRFile());
        panel.add(firBtn);
        panel.add(firLabel);

        JButton photoBtn = new JButton("Replace Photo");
        photoLabel = new JLabel("No photo selected");
        photoBtn.addActionListener(e -> choosePhotoFile());
        panel.add(photoBtn);
        panel.add(photoLabel);

        JButton updateBtn = new JButton("Update Report");
        updateBtn.setBackground(new Color(41, 128, 185));
        updateBtn.setForeground(Color.WHITE);
        panel.add(new JLabel());
        panel.add(updateBtn);

        add(panel);

        loadBtn.addActionListener(e -> loadReport());
        updateBtn.addActionListener(e -> updateReport());
    }

    private void loadReport() {
        int crimeNo = Integer.parseInt(crimeNoField.getText());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM crimes WHERE crime_no=? AND username=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, crimeNo);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                locationField.setText(rs.getString("location"));
                typeField.setText(rs.getString("type"));
                witnessArea.setText(rs.getString("witness"));
                infoArea.setText(rs.getString("info"));
                firPath = rs.getString("fir_path");
                photoPath = rs.getString("photo_path");
                firLabel.setText(new File(firPath).getName());
                photoLabel.setText(new File(photoPath).getName());
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Report not found or not owned by you!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void chooseFIRFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Delete old FIR file
            deleteFile(firPath);
            File file = chooser.getSelectedFile();
            firPath = file.getAbsolutePath();
            firLabel.setText(file.getName());
        }
    }

    private void choosePhotoFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Delete old Photo
            deleteFile(photoPath);
            File file = chooser.getSelectedFile();
            photoPath = file.getAbsolutePath();
            photoLabel.setText(file.getName());
        }
    }

    private void updateReport() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE crimes SET location=?, type=?, witness=?, info=?, fir_path=?, photo_path=? WHERE crime_no=? AND username=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, locationField.getText());
            stmt.setString(2, typeField.getText());
            stmt.setString(3, witnessArea.getText());
            stmt.setString(4, infoArea.getText());
            stmt.setString(5, firPath);
            stmt.setString(6, photoPath);
            stmt.setInt(7, Integer.parseInt(crimeNoField.getText()));
            stmt.setString(8, username);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Report updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Update failed (wrong user or crime number)!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteFile(String path) {
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) f.delete();
        }
    }
}
