package com.team8.fooddelivery.repository;

import com.team8.fooddelivery.model.Order;
import com.team8.fooddelivery.model.OrderStatus;
import com.team8.fooddelivery.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {
  private static final Logger logger = LoggerFactory.getLogger(OrderRepository.class);

  public Long save(Order order) throws SQLException {
    String sql = "INSERT INTO orders (status, customer_id, restaurant_id, delivery_address_id, courier_id, total_price) " +
        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      // deliveryAddress в Order это String, а не Address объект
      // Если нужно сохранить адрес как объект, нужно будет изменить модель Order
      // Пока сохраняем null для delivery_address_id
      Long deliveryAddressId = null;

      stmt.setString(1, order.getStatus() != null ? order.getStatus().name() : OrderStatus.PENDING.name());
      stmt.setObject(2, order.getCustomerId(), Types.BIGINT);
      stmt.setObject(3, order.getRestaurantId(), Types.BIGINT);
      stmt.setObject(4, deliveryAddressId, Types.BIGINT);
      stmt.setObject(5, order.getCourierId(), Types.BIGINT);
      stmt.setObject(6, order.getTotalPrice(), Types.DOUBLE);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        Long id = rs.getLong("id");

        // Сохраняем элементы заказа
        if (order.getItems() != null && !order.getItems().isEmpty()) {
          saveOrderItems(id, order.getItems(), conn);
        }

        logger.debug("Заказ сохранен с id={}", id);
        return id;
      }
      throw new SQLException("Не удалось сохранить заказ");
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
    String sql = "SELECT * FROM orders WHERE customer_id = ?";

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
    String sql = "SELECT * FROM orders WHERE courier_id = ?";

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
    String sql = "SELECT * FROM orders WHERE status = ?";

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
    String sql = "SELECT * FROM orders";

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

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      // deliveryAddress в Order это String, а не Address объект
      Long deliveryAddressId = null;

      stmt.setString(1, order.getStatus() != null ? order.getStatus().name() : OrderStatus.PENDING.name());
      stmt.setObject(2, order.getCustomerId(), Types.BIGINT);
      stmt.setObject(3, order.getRestaurantId(), Types.BIGINT);
      stmt.setObject(4, deliveryAddressId, Types.BIGINT);
      stmt.setObject(5, order.getCourierId(), Types.BIGINT);
      stmt.setObject(6, order.getTotalPrice(), Types.DOUBLE);
      stmt.setLong(7, order.getId());

      stmt.executeUpdate();
      logger.debug("Заказ обновлен: id={}", order.getId());
    }
  }

  public void delete(Long id) throws SQLException {
    String sql = "DELETE FROM orders WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);
      stmt.executeUpdate();
      logger.debug("Заказ удален: id={}", id);
    }
  }

  private Order mapResultSetToOrder(ResultSet rs, Connection conn) throws SQLException {
    Order order = new Order();
    order.setId(rs.getLong("id"));
    order.setStatus(OrderStatus.valueOf(rs.getString("status")));
    order.setCustomerId(rs.getObject("customer_id", Long.class));
    order.setRestaurantId(rs.getObject("restaurant_id", Long.class));
    order.setCourierId(rs.getObject("courier_id", Long.class));


    double totalPriceValue = rs.getDouble("total_price");
    if (rs.wasNull()) {
      order.setTotalPrice(null);
    } else {
      order.setTotalPrice(totalPriceValue);
    }

    order.setDeliveryAddress(null);

    // Загружаем элементы заказа
    List<String> items = findOrderItemsByOrderId(order.getId(), conn);
    order.setItems(items);

    return order;
  }

  private List<String> findOrderItemsByOrderId(Long orderId, Connection conn) throws SQLException {
    String sql = "SELECT product_name FROM order_items WHERE order_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, orderId);
      ResultSet rs = stmt.executeQuery();

      List<String> items = new ArrayList<>();
      while (rs.next()) {
        items.add(rs.getString("product_name"));
      }
      return items;
    }
  }

  private void saveOrderItems(Long orderId, List<String> items, Connection conn) throws SQLException {
    String sql = "INSERT INTO order_items (order_id, product_name, quantity, price) VALUES (?, ?, ?, ?)";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (String itemName : items) {
        stmt.setLong(1, orderId);
        stmt.setString(2, itemName);
        stmt.setInt(3, 1); // По умолчанию количество 1
        stmt.setDouble(4, 0.0); // Цена будет обновлена позже
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

}