package database_connections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            if (imageData != null) pstmt.setBytes(2, imageData); else pstmt.setNull(2, Types.BLOB);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<MotionRecord> retrieveRecords() {
        List<MotionRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, created_at, image_data FROM motion");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp timestamp = rs.getTimestamp("created_at");
                byte[] imageData = rs.getBytes("image_data");
                records.add(new MotionRecord(id, timestamp, imageData));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    public void deleteAllRecords() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM motion");
            stmt.executeUpdate("ALTER TABLE motion AUTO_INCREMENT = 1");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
