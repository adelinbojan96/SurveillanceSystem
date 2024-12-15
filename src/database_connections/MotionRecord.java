package database_connections;

import java.sql.Timestamp;

public record MotionRecord(int id, Timestamp timestamp, byte[] imageData) {
}
