package com.team8.fooddelivery.repository;

import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.model.product.ProductCategory;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
  private static final Logger logger = LoggerFactory.getLogger(ProductRepository.class);

  public Long save(Product product) throws SQLException {
    // Этот метод не должен использоваться напрямую, используйте saveForShop
    throw new SQLException("Используйте saveForShop для сохранения продукта с shop_id");
  }

  public Long saveForShop(Long shopId, Product product) throws SQLException {
    String sql = "INSERT INTO products (shop_id, name, description, weight, price, category, is_available, cooking_time_minutes, photo_url) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING product_id";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, shopId);
      stmt.setString(2, product.getName());
      stmt.setString(3, product.getDescription());
      stmt.setObject(4, product.getWeight(), Types.DOUBLE);
      stmt.setDouble(5, product.getPrice());
      stmt.setString(6, product.getCategory() != null ? product.getCategory().name() : null);
      stmt.setBoolean(7, product.getAvailable());
      stmt.setLong(8, product.getCookingTimeMinutes() != null ? product.getCookingTimeMinutes().getSeconds() : 0);
      stmt.setString(9, product.getPhotoUrl());

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        Long id = rs.getLong("product_id");
        logger.debug("Продукт сохранен с id={} для магазина shopId={}", id, shopId);
        return id;
      }
      throw new SQLException("Не удалось сохранить продукт");
    }
  }

  public Optional<Product> findById(Long productId) throws SQLException {
    String sql = "SELECT * FROM products WHERE product_id = ?";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, productId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return Optional.of(mapResultSetToProduct(rs));
      }
      return Optional.empty();
    }
  }

  public List<Product> findByShopId(Long shopId) throws SQLException {
    String sql = "SELECT * FROM products WHERE shop_id = ?";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, shopId);
      ResultSet rs = stmt.executeQuery();

      List<Product> products = new ArrayList<>();
      while (rs.next()) {
        products.add(mapResultSetToProduct(rs));
      }
      return products;
    }
  }

  public List<ProductCategory> findCategoriesByShopId(Long shopId) throws SQLException {
    String sql = "SELECT DISTINCT category FROM products WHERE shop_id = ? AND category IS NOT NULL AND is_available = true";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, shopId);
      ResultSet rs = stmt.executeQuery();

      List<ProductCategory> categories = new ArrayList<>();
      while (rs.next()) {
        String categoryStr = rs.getString("category");
        if (categoryStr != null) {
          try {
            categories.add(ProductCategory.valueOf(categoryStr));
          } catch (IllegalArgumentException e) {
            logger.warn("Unknown category '{}' for shop {}, skipping", categoryStr, shopId);
            // Пропускаем неизвестные категории
          }
        }
      }
      return categories;
    }
  }

  public List<Product> findByShopIdAndCategory(Long shopId, ProductCategory category) throws SQLException {
    String sql = "SELECT * FROM products WHERE shop_id = ? AND category = ?";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, shopId);
      stmt.setString(2, category.name());
      ResultSet rs = stmt.executeQuery();

      List<Product> products = new ArrayList<>();
      while (rs.next()) {
        products.add(mapResultSetToProduct(rs));
      }
      return products;
    }
  }

  public void update(Product product) throws SQLException {
    String sql = "UPDATE products SET name=?, description=?, weight=?, price=?, category=?, is_available=?, cooking_time_minutes=?, photo_url=? WHERE product_id=?";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, product.getName());
      stmt.setString(2, product.getDescription());
      stmt.setObject(3, product.getWeight(), Types.DOUBLE);
      stmt.setDouble(4, product.getPrice());
      stmt.setString(5, product.getCategory() != null ? product.getCategory().name() : null);
      stmt.setBoolean(6, product.getAvailable());
      stmt.setLong(7, product.getCookingTimeMinutes() != null ? product.getCookingTimeMinutes().getSeconds() : 0);
      stmt.setString(8, product.getPhotoUrl());
      stmt.setLong(9, product.getProductId());

      stmt.executeUpdate();
      logger.debug("Продукт обновлен: id={}", product.getProductId());
    }
  }

  public void delete(Long productId) throws SQLException {
    String sql = "DELETE FROM products WHERE product_id = ?";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, productId);
      stmt.executeUpdate();
      logger.debug("Продукт удален: id={}", productId);
    }
  }

  private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
    Long productId = rs.getLong("product_id");
    Long shopId = rs.getObject("shop_id") != null ? rs.getLong("shop_id") : null;
    String name = rs.getString("name");
    String description = rs.getString("description");
    Double weight = rs.getObject("weight", Double.class);
    Double price = rs.getObject("price", Double.class);

    ProductCategory category = null;
    String categoryStr = rs.getString("category");
    if (categoryStr != null) {
      try {
        category = ProductCategory.valueOf(categoryStr);
      } catch (IllegalArgumentException e) {
        logger.warn("Unknown category '{}' for product {}, setting to OTHER", categoryStr, productId);
        category = ProductCategory.OTHER;
      }
    }

    boolean isAvailable = rs.getBoolean("is_available");

    Long cookingTimeMinutes = rs.getObject("cooking_time_minutes", Long.class);
    Duration cookingTime = null;
    if (cookingTimeMinutes != null && cookingTimeMinutes > 0) {
      cookingTime = Duration.ofMinutes(cookingTimeMinutes);
    }

    String photoUrl = rs.getString("photo_url");

    // Use explicit constructor instead of Lombok builder to avoid relying on annotation processing in all environments
    return new Product(productId, shopId, name, description, weight, price, category, isAvailable, cookingTime, photoUrl);
  }
}