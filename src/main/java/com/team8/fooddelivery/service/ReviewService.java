package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.Review;
import com.team8.fooddelivery.repository.ReviewRepository;

import java.sql.SQLException;
import java.util.List;

public class ReviewService {
    private final ReviewRepository reviewRepository = new ReviewRepository();

    public Long addReview(Review review) throws SQLException {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForProduct(Long productId) throws SQLException {
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getReviewsByClient(Long clientId) throws SQLException {
        return reviewRepository.findByClientId(clientId);
    }

    public void updateReview(Review review) throws SQLException {
        reviewRepository.update(review);
    }

    public void deleteReview(Long reviewId) throws SQLException {
        reviewRepository.delete(reviewId);
    }

    public double getAverageRating(Long productId) throws SQLException {
        List<Review> reviews = getReviewsForProduct(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
}
