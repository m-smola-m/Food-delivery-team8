package com.team8.fooddelivery.repository;

import com.team8.fooddelivery.model.notification.Notification;
import com.team8.fooddelivery.service.DatabaseConnectionService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    public void save(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (client_id, type, message, is_read, created_at) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, notification.getClientId());
            stmt.setString(2, notification.getType());
            stmt.setString(3, notification.getMessage());
            stmt.setBoolean(4, notification.isRead());
            stmt.setTimestamp(5, Timestamp.valueOf(notification.getTimestamp()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                notification.setId(rs.getLong("id"));
            }
        }
    }

    public List<Notification> findByClientId(Long clientId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE client_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clientId);
            ResultSet rs = stmt.executeQuery();
            List<Notification> list = new ArrayList<>();
            while (rs.next()) {
                Notification notification = Notification.builder()
                        .id(rs.getLong("id"))
                        .clientId(rs.getLong("client_id"))
                        .type(rs.getString("type"))
                        .message(rs.getString("message"))
                        .timestamp(rs.getTimestamp("created_at").toLocalDateTime())
                        .isRead(rs.getBoolean("is_read"))
                        .build();
                list.add(notification);
            }
            return list;
        }
    }

    public void markAllAsRead(Long clientId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE client_id = ? AND is_read = FALSE";
        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clientId);
            stmt.executeUpdate();
        }
    }
}
