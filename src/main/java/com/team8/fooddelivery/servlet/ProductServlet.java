package com.team8.fooddelivery.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team8.fooddelivery.model.Review;
import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.repository.ProductRepository;
import com.team8.fooddelivery.service.ReviewService;
import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/product/*")
public class ProductServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ProductServlet.class);
    private final ProductRepository productRepository = new ProductRepository();
    private final ReviewService reviewService = new ReviewService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String path = request.getPathInfo();
        if (path == null) {
            response.sendError(404);
            return;
        }

        try {
            if (path.startsWith("/details/")) {
                String productIdStr = path.substring("/details/".length());
                Long productId = Long.parseLong(productIdStr);
                showProductDetails(request, response, productId);
            } else if (path.startsWith("/reviews/")) {
                String productIdStr = path.substring("/reviews/".length());
                Long productId = Long.parseLong(productIdStr);
                sendReviews(request, response, productId);
            } else {
                response.sendError(404);
            }
        } catch (Exception e) {
            log.error("Error in doGet", e);
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String path = request.getPathInfo();
        if (path == null) {
            response.sendError(404);
            return;
        }

        try {
            if (path.startsWith("/review/")) {
                String productIdStr = path.substring("/review/".length());
                Long productId = Long.parseLong(productIdStr);
                addReview(request, response, productId);
            } else {
                response.sendError(404);
            }
        } catch (Exception e) {
            log.error("Error in doPost", e);
            response.sendError(500, e.getMessage());
        }
    }

    private void showProductDetails(HttpServletRequest request, HttpServletResponse response, Long productId)
            throws SQLException, ServletException, IOException {

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            response.sendError(404, "Product not found");
            return;
        }

        List<Review> reviews = reviewService.getReviewsForProduct(productId);
        double averageRating = reviewService.getAverageRating(productId);

        request.setAttribute("product", product);
        request.setAttribute("reviews", reviews);
        request.setAttribute("averageRating", averageRating);

        request.getRequestDispatcher("/WEB-INF/jsp/client/product_details.jsp").forward(request, response);
    }

    private void sendReviews(HttpServletRequest request, HttpServletResponse response, Long productId)
            throws SQLException, IOException {

        List<Review> reviews = reviewService.getReviewsForProduct(productId);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), reviews);
    }

    private void addReview(HttpServletRequest request, HttpServletResponse response, Long productId)
            throws SQLException, IOException {

        Long clientId = SessionManager.getUserId(request.getSession());
        if (clientId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        Review review = Review.builder()
                .productId(productId)
                .clientId(clientId)
                .rating(rating)
                .comment(comment)
                .build();

        reviewService.addReview(review);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
}
