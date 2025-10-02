package user;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.*;
import Database.DBConnection;

public class ViewCrimeReport extends JFrame {
    private JTextField crimeNoField;
    private JButton searchButton;
    private JTable table;
    private DefaultTableModel model;
    private JPanel filePanel; // Panel to show files/images

    public ViewCrimeReport() {
        setTitle("View Crime Report by Crime Number");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔝 Top panel for input
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Enter Crime Number:"));
        crimeNoField = new JTextField(10);
        topPanel.add(crimeNoField);
        searchButton = new JButton("Search");
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // 📋 Table with Evidence column
        String[] cols = {"Crime No", "Location", "Type", "Witness", "Info", "Evidence", "Created By"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 📁 Panel to show files
        filePanel = new JPanel();
        filePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JScrollPane fileScroll = new JScrollPane(filePanel);
        fileScroll.setPreferredSize(new Dimension(1000, 120));
        add(fileScroll, BorderLayout.SOUTH);

        // 🔎 Search button action
        searchButton.addActionListener(e -> searchCrime());

        setVisible(true);
    }

    private void searchCrime() {
        String crimeNo = crimeNoField.getText().trim();
        if (crimeNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a crime number!");
            return;
        }

        // Clear previous data
        model.setRowCount(0);
        filePanel.removeAll();

        try (Connection conn = DBConnection.getConnection()) {
            // Include evidence, fir_path, and photo_path
            String sql = "SELECT crime_no, location, type, witness, info, evidence, fir_path, photo_path, username " +
                         "FROM crimes WHERE crime_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, crimeNo);
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "No report found for Crime No: " + crimeNo);
                return;
            }

            while (rs.next()) {
                Object[] row = {
                        rs.getString("crime_no"),
                        rs.getString("location"),
                        rs.getString("type"),
                        rs.getString("witness"),
                        rs.getString("info"),
                        rs.getString("evidence"),  // ➕ Evidence column
                        rs.getString("username")
                };
                model.addRow(row);

                // Show FIR and photo files
                String firPath = rs.getString("fir_path");
                String photoPath = rs.getString("photo_path");

                if (firPath != null && !firPath.isEmpty()) {
                    addFileButton(firPath);
                }
                if (photoPath != null && !photoPath.isEmpty()) {
                    addFileButton(photoPath);
                }
            }

            filePanel.revalidate();
            filePanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    // 📂 Helper method to add file-opening buttons
    private void addFileButton(String path) {
        File f = new File(path);
        if (f.exists()) {
            JButton btn = new JButton(f.getName());
            btn.addActionListener(ev -> {
                try {
                    Desktop.getDesktop().open(f);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Cannot open file: " + ex.getMessage());
                }
            });
            filePanel.add(btn);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewCrimeReport::new);
    }
}
