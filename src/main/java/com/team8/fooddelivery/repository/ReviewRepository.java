package com.team8.fooddelivery.repository;

import com.team8.fooddelivery.model.Review;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewRepository {
    private static final Logger logger = LoggerFactory.getLogger(ReviewRepository.class);

    public Long save(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (product_id, client_id, rating, comment) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, review.getProductId());
            stmt.setLong(2, review.getClientId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                logger.debug("Отзыв сохранен с id={}", id);
                return id;
            }
            throw new SQLException("Не удалось сохранить отзыв");
        }
    }

    public Optional<Review> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE id = ?";

        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToReview(rs));
            }
            return Optional.empty();
        }
    }

    public List<Review> findByProductId(Long productId) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE product_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, productId);
            ResultSet rs = stmt.executeQuery();

            List<Review> reviews = new ArrayList<>();
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
            return reviews;
        }
    }

    public List<Review> findByClientId(Long clientId) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE client_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, clientId);
            ResultSet rs = stmt.executeQuery();

            List<Review> reviews = new ArrayList<>();
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
            return reviews;
        }
    }

    public void update(Review review) throws SQLException {
        String sql = "UPDATE reviews SET rating=?, comment=? WHERE id=?";

        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setLong(3, review.getId());

            stmt.executeUpdate();
            logger.debug("Отзыв обновлен: id={}", review.getId());
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM reviews WHERE id = ?";

        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
            logger.debug("Отзыв удален: id={}", id);
        }
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long productId = rs.getLong("product_id");
        Long clientId = rs.getLong("client_id");
        Integer rating = rs.getInt("rating");
        String comment = rs.getString("comment");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        return new Review(id, productId, clientId, rating, comment, createdAt);
    }
}
