package Database;

import javax.swing.*;
import java.sql.*;

public class DataBaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/surveillance?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public void insertRecord(byte[] imageData) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "INSERT INTO motion (created_at, image_data) VALUES (?, ?)";

            pstmt = conn.prepareStatement(sql);

            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(1, currentTimestamp);

            if (imageData != null) {
                pstmt.setBytes(2, imageData);
            } else {
                pstmt.setNull(2, Types.BLOB);
            }

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Record added to the database successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add the record to the database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
