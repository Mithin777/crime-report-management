package user;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import Database.DBConnection;

public class AddCrimeReport extends JFrame {
    private JTextField crimeNoField, locationField, typeField;
    private JTextArea infoArea, witnessArea, evidenceArea;
    private JLabel firLabel, photoLabel;
    private String firPath = "", photoPath = "";
    private String username;

    public AddCrimeReport(String user) {
        this.username = user;
        setTitle("Add Crime Report");
        setSize(650, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        panel.add(new JLabel("Crime Number:"));
        crimeNoField = new JTextField();
        panel.add(crimeNoField);

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

        // ➕ Evidence field
        panel.add(new JLabel("Evidence:"));
        evidenceArea = new JTextArea();
        panel.add(new JScrollPane(evidenceArea));

        // FIR upload
        JButton firBtn = new JButton("Upload FIR File");
        firLabel = new JLabel("No file selected");
        firBtn.addActionListener(e -> chooseFIRFile());
        panel.add(firBtn);
        panel.add(firLabel);

        // Photo upload
        JButton photoBtn = new JButton("Upload Crime Scene Photo");
        photoLabel = new JLabel("No photo selected");
        photoBtn.addActionListener(e -> choosePhotoFile());
        panel.add(photoBtn);
        panel.add(photoLabel);

        JButton saveBtn = new JButton("Save Report");
        saveBtn.setBackground(new Color(39, 174, 96));
        saveBtn.setForeground(Color.WHITE);
        panel.add(new JLabel());
        panel.add(saveBtn);

        add(panel);

        saveBtn.addActionListener(e -> saveReport());
    }

    private void chooseFIRFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            firPath = file.getAbsolutePath();
            firLabel.setText(file.getName());
        }
    }

    private void choosePhotoFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            photoPath = file.getAbsolutePath();
            photoLabel.setText(file.getName());
        }
    }

    private void saveReport() {
        try (Connection conn = DBConnection.getConnection()) {
            // ➕ Updated SQL to include evidence
            String sql = "INSERT INTO crimes " +
                    "(crime_no, location, type, witness, info, evidence, fir_path, photo_path, username) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(crimeNoField.getText()));
            stmt.setString(2, locationField.getText());
            stmt.setString(3, typeField.getText());
            stmt.setString(4, witnessArea.getText());
            stmt.setString(5, infoArea.getText());
            stmt.setString(6, evidenceArea.getText());
            stmt.setString(7, firPath);
            stmt.setString(8, photoPath);
            stmt.setString(9, username);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Report Saved Successfully!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
