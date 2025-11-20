package com.team8.fooddelivery.repository;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {
  private static final Logger logger = LoggerFactory.getLogger(OrderRepository.class);
  private final AddressRepository addressRepository = new AddressRepository();

  public Long save(Order order) throws SQLException {
    String orderSql = "INSERT INTO orders (status, customer_id, restaurant_id, delivery_address_id, courier_id, total_price) " +
        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

    Connection conn = null;
    try {
      conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false);

      // Сохраняем адрес доставки
      Long deliveryAddressId = null;
      if (order.getDeliveryAddress() != null) {
        deliveryAddressId = addressRepository.save(order.getDeliveryAddress());
      }

      // Сохраняем основной заказ
      Long orderId;
      try (PreparedStatement stmt = conn.prepareStatement(orderSql)) {
        stmt.setString(1, order.getStatus() != null ? order.getStatus().name() : OrderStatus.PENDING.name());
        stmt.setObject(2, order.getClientId(), Types.BIGINT);
        stmt.setObject(3, order.getShopId(), Types.BIGINT);
        stmt.setObject(4, deliveryAddressId, Types.BIGINT);
        stmt.setObject(5, order.getCourierId(), Types.BIGINT);
        stmt.setObject(6, order.getTotalAmount(), Types.DECIMAL);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
          orderId = rs.getLong("id");
        } else {
          throw new SQLException("Не удалось сохранить заказ, ID не получен");
        }
      }

      // Сохраняем элементы заказа
      if (order.getItems() != null && !order.getItems().isEmpty()) {
        saveOrderItems(orderId, order.getItems(), conn);
      }

      conn.commit();
      logger.debug("Заказ сохранен с id={}", orderId);
      return orderId;

    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback();
      }
      throw e;
    } finally {
      if (conn != null) {
        conn.setAutoCommit(true);
        conn.close();
      }
    }
  }

  public Optional<Order> findById(Long id) throws SQLException {
    String sql = "SELECT * FROM orders WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return Optional.of(mapResultSetToOrder(rs, conn));
      }
      return Optional.empty();
    }
  }

  public List<Order> findByCustomerId(Long customerId) throws SQLException {
    String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, customerId);
      ResultSet rs = stmt.executeQuery();

      List<Order> orders = new ArrayList<>();
      while (rs.next()) {
        orders.add(mapResultSetToOrder(rs, conn));
      }
      return orders;
    }
  }

  public List<Order> findByCourierId(Long courierId) throws SQLException {
    String sql = "SELECT * FROM orders WHERE courier_id = ? ORDER BY created_at DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, courierId);
      ResultSet rs = stmt.executeQuery();

      List<Order> orders = new ArrayList<>();
      while (rs.next()) {
        orders.add(mapResultSetToOrder(rs, conn));
      }
      return orders;
    }
  }

  public List<Order> findByStatus(OrderStatus status) throws SQLException {
    String sql = "SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, status.name());
      ResultSet rs = stmt.executeQuery();

      List<Order> orders = new ArrayList<>();
      while (rs.next()) {
        orders.add(mapResultSetToOrder(rs, conn));
      }
      return orders;
    }
  }

  public List<Order> findAll() throws SQLException {
    String sql = "SELECT * FROM orders ORDER BY created_at DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

      List<Order> orders = new ArrayList<>();
      while (rs.next()) {
        orders.add(mapResultSetToOrder(rs, conn));
      }
      return orders;
    }
  }

  public void update(Order order) throws SQLException {
    String sql = "UPDATE orders SET status=?, customer_id=?, restaurant_id=?, delivery_address_id=?, " +
        "courier_id=?, total_price=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";

    Connection conn = null;
    try {
      conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false);

      // Обновляем или сохраняем адрес доставки
      Long deliveryAddressId = null;
      if (order.getDeliveryAddress() != null) {
        if (order.getDeliveryAddress().getId() != null) {
          // Адрес уже существует - обновляем
          addressRepository.update(order.getDeliveryAddress().getId(), order.getDeliveryAddress());
          deliveryAddressId = order.getDeliveryAddress().getId();
        } else {
          // Новый адрес - сохраняем
          deliveryAddressId = addressRepository.save(order.getDeliveryAddress());
        }
      }

      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, order.getStatus() != null ? order.getStatus().name() : OrderStatus.PENDING.name());
        stmt.setObject(2, order.getClientId(), Types.BIGINT);
        stmt.setObject(3, order.getShopId(), Types.BIGINT);
        stmt.setObject(4, deliveryAddressId, Types.BIGINT);
        stmt.setObject(5, order.getCourierId(), Types.BIGINT);
        stmt.setObject(6, order.getTotalAmount(), Types.DECIMAL);
        stmt.setLong(7, order.getOrderId());

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
          throw new SQLException("Заказ не найден для обновления: id=" + order.getOrderId());
        }
      }

      // Обновляем элементы заказа
      updateOrderItems(order.getOrderId(), order.getItems(), conn);

      conn.commit();
      logger.debug("Заказ обновлен: id={}", order.getOrderId());

    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback();
      }
      throw e;
    } finally {
      if (conn != null) {
        conn.setAutoCommit(true);
        conn.close();
      }
    }
  }

  public void delete(Long id) throws SQLException {
    Connection conn = null;
    try {
      conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false);

      // Получаем заказ чтобы узнать ID адреса
      Optional<Order> orderOpt = findById(id);
      if (orderOpt.isPresent()) {
        Order order = orderOpt.get();
        // Удаляем адрес доставки если он есть
        if (order.getDeliveryAddress() != null && order.getDeliveryAddress().getId() != null) {
          addressRepository.delete(order.getDeliveryAddress().getId());
        }
      }

      // Сначала удаляем элементы заказа
      deleteOrderItems(id, conn);

      // Затем удаляем сам заказ
      String sql = "DELETE FROM orders WHERE id = ?";
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setLong(1, id);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
          throw new SQLException("Заказ не найден для удаления: id=" + id);
        }
      }

      conn.commit();
      logger.debug("Заказ удален: id={}", id);

    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback();
      }
      throw e;
    } finally {
      if (conn != null) {
        conn.setAutoCommit(true);
        conn.close();
      }
    }
  }

  private Order mapResultSetToOrder(ResultSet rs, Connection conn) throws SQLException {
    Order order = new Order();
    order.setOrderId(rs.getLong("id"));
    order.setStatus(OrderStatus.valueOf(rs.getString("status")));
    order.setClientId(rs.getObject("customer_id", Long.class));
    order.setShopId(rs.getObject("restaurant_id", Long.class));
    order.setCourierId(rs.getObject("courier_id", Long.class));
    order.setTotalAmount(rs.getObject("total_price", Double.class));

    // Загружаем адрес доставки
    Long deliveryAddressId = rs.getObject("delivery_address_id", Long.class);
    if (deliveryAddressId != null) {
      Optional<Address> address = addressRepository.findById(deliveryAddressId);
      address.ifPresent(order::setDeliveryAddress);
    }

    // Загружаем продукты заказа
    List<Product> products = findOrderItemsByOrderId(order.getOrderId(), conn);
    order.setItems(products);

    return order;
  }

  private List<Product> findOrderItemsByOrderId(Long orderId, Connection conn) throws SQLException {
    String sql = "SELECT oi.*, p.description, p.weight, p.category, p.is_available, p.cooking_time_minutes " +
        "FROM order_items oi " +
        "LEFT JOIN products p ON oi.product_id = p.id " +
        "WHERE oi.order_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, orderId);
      ResultSet rs = stmt.executeQuery();

      List<Product> products = new ArrayList<>();
      while (rs.next()) {
        Product product = Product.builder()
            .productId(rs.getObject("product_id", Long.class))
            .name(rs.getString("product_name"))
            .description(rs.getString("description"))
            .weight(rs.getObject("weight", Double.class))
            .price(rs.getObject("price", Double.class))
            .category(rs.getString("category") != null ?
                ProductCategory.valueOf(rs.getString("category")) : null)
            .available(rs.getBoolean("is_available"))
            .cookingTimeMinutes(rs.getObject("cooking_time_minutes", Integer.class) != null ?
                java.time.Duration.ofMinutes(rs.getInt("cooking_time_minutes")) : null)
            .build();
        products.add(product);
      }
      return products;
    }
  }

  private void saveOrderItems(Long orderId, List<Product> items, Connection conn) throws SQLException {
    String sql = "INSERT INTO order_items (order_id, product_id, product_name, quantity, price) VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (Product product : items) {
        stmt.setLong(1, orderId);
        stmt.setObject(2, product.getProductId(), Types.BIGINT);
        stmt.setString(3, product.getName());
        stmt.setInt(4, 1); // Количество по умолчанию
        stmt.setDouble(5, product.getPrice() != null ? product.getPrice() : 0.0);
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  private void updateOrderItems(Long orderId, List<Product> items, Connection conn) throws SQLException {
    // Удаляем старые элементы
    deleteOrderItems(orderId, conn);

    // Добавляем новые
    if (items != null && !items.isEmpty()) {
      saveOrderItems(orderId, items, conn);
    }
  }

  private void deleteOrderItems(Long orderId, Connection conn) throws SQLException {
    String sql = "DELETE FROM order_items WHERE order_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, orderId);
      stmt.executeUpdate();
    }
  }
}