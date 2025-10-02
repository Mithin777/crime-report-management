package user;

import java.sql.*;

public class Test {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/crime_db";
        String user = "root";   // change if your MySQL user is different
        String password = "mithu777@mkt"; // change if your MySQL password is different

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to database!");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
