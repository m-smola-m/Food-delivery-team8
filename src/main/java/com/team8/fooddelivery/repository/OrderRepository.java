package com.team8.fooddelivery.repository;

import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {
  private static final Logger logger = LoggerFactory.getLogger(OrderRepository.class);
  private final AddressRepository addressRepository = new AddressRepository();

  public Long save(Order order) throws SQLException {
    // Сначала сохраняем адрес, если он указан
    Long deliveryAddressId = null;
    if (order.getDeliveryAddress() != null) {
      deliveryAddressId = addressRepository.save(order.getDeliveryAddress());
    }

    String sql = "INSERT INTO orders (status, customer_id, restaurant_id, delivery_address_id, courier_id, total_price, payment_status, payment_method, created_at, estimated_delivery_time) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, order.getStatus() != null ? order.getStatus().name() : OrderStatus.PENDING.name());
      stmt.setObject(2, order.getCustomerId(), Types.BIGINT);
      stmt.setObject(3, order.getRestaurantId(), Types.BIGINT);
      stmt.setObject(4, deliveryAddressId, Types.BIGINT);
      stmt.setObject(5, order.getCourierId(), Types.BIGINT);
      stmt.setObject(6, order.getTotalPrice(), Types.DOUBLE);

      // Устанавливаем значения по умолчанию для обязательных полей
      stmt.setString(7, order.getPaymentStatus() != null ?
          order.getPaymentStatus().name() : com.team8.fooddelivery.model.client.PaymentStatus.PENDING.name());
      stmt.setString(8, order.getPaymentMethod() != null ?
          order.getPaymentMethod().name() : null);
      stmt.setTimestamp(9, order.getCreatedAt() != null ?
          Timestamp.from(order.getCreatedAt()) : Timestamp.from(Instant.now()));
      stmt.setTimestamp(10, order.getEstimatedDeliveryTime() != null ?
          Timestamp.from(order.getEstimatedDeliveryTime()) : null);

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
    String sql = "SELECT o.* FROM orders o WHERE o.id = ?";

    try (Connection conn = DatabaseConnectionService.getConnection();
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
    String sql = "SELECT o.* FROM orders o WHERE o.customer_id = ? ORDER BY o.created_at DESC";

    try (Connection conn = DatabaseConnectionService.getConnection();
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
    String sql = "SELECT o.* FROM orders o WHERE o.courier_id = ?";

    try (Connection conn = DatabaseConnectionService.getConnection();
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

  public List<Order> findByRestaurantId(Long restaurantId) throws SQLException {
    String sql = "SELECT o.* FROM orders o WHERE o.restaurant_id = ? ORDER BY o.created_at DESC";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, restaurantId);
      ResultSet rs = stmt.executeQuery();

      List<Order> orders = new ArrayList<>();
      while (rs.next()) {
        orders.add(mapResultSetToOrder(rs, conn));
      }
      return orders;
    }
  }

  public List<Order> findByStatus(OrderStatus status) throws SQLException {
    String sql = "SELECT o.* FROM orders o WHERE o.status = ?";

    try (Connection conn = DatabaseConnectionService.getConnection();
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
    String sql = "SELECT o.* FROM orders o";

    try (Connection conn = DatabaseConnectionService.getConnection();
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
    // Обновляем или сохраняем адрес
    Long deliveryAddressId = order.getDeliveryAddressId();
    if (order.getDeliveryAddress() != null) {
      if (deliveryAddressId == null) {
        // Создаем новый адрес
        deliveryAddressId = addressRepository.save(order.getDeliveryAddress());
        order.setDeliveryAddressId(deliveryAddressId);
      } else {
        // Обновляем существующий адрес
        addressRepository.update(deliveryAddressId, order.getDeliveryAddress());
      }
    }

    String sql = "UPDATE orders SET status=?, customer_id=?, restaurant_id=?, delivery_address_id=?, " +
        "courier_id=?, total_price=?, payment_status=?, payment_method=?, estimated_delivery_time=?, " +
        "updated_at=CURRENT_TIMESTAMP WHERE id=?";

    try (Connection conn = DatabaseConnectionService.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, order.getStatus() != null ? order.getStatus().name() : OrderStatus.PENDING.name());
      stmt.setObject(2, order.getCustomerId(), Types.BIGINT);
      stmt.setObject(3, order.getRestaurantId(), Types.BIGINT);
      stmt.setObject(4, deliveryAddressId, Types.BIGINT);
      stmt.setObject(5, order.getCourierId(), Types.BIGINT);
      stmt.setObject(6, order.getTotalPrice(), Types.DOUBLE);
      stmt.setString(7, order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null);
      stmt.setString(8, order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
      stmt.setTimestamp(9, order.getEstimatedDeliveryTime() != null ? Timestamp.from(order.getEstimatedDeliveryTime()) : null);
      stmt.setLong(10, order.getId());

      stmt.executeUpdate();
      logger.debug("Заказ обновлен: id={}", order.getId());
    }
  }

  public void delete(Long id) throws SQLException {
    // Сначала находим заказ, чтобы получить address_id
    Optional<Order> orderOpt = findById(id);
    if (orderOpt.isPresent()) {
      Order order = orderOpt.get();

      // Удаляем заказ
      String deleteOrderSql = "DELETE FROM orders WHERE id = ?";
      try (Connection conn = DatabaseConnectionService.getConnection();
           PreparedStatement stmt = conn.prepareStatement(deleteOrderSql)) {

        stmt.setLong(1, id);
        stmt.executeUpdate();
      }

      // Если есть связанный адрес, удаляем его
      if (order.getDeliveryAddressId() != null) {
        addressRepository.delete(order.getDeliveryAddressId());
      }

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

    Long deliveryAddressId = rs.getObject("delivery_address_id", Long.class);
    order.setDeliveryAddressId(deliveryAddressId);

    // Загружаем адрес через AddressRepository
    if (deliveryAddressId != null) {
      Optional<Address> addressOpt = addressRepository.findById(deliveryAddressId);
      addressOpt.ifPresent(order::setDeliveryAddress);
    }

    // Обработка числовых полей с возможностью null
    Double totalPriceValue = rs.getDouble("total_price");
    order.setTotalPrice(rs.wasNull() ? null : totalPriceValue);

    // Обработка enum полей
    String paymentStatus = rs.getString("payment_status");
    if (paymentStatus != null) {
      order.setPaymentStatus(com.team8.fooddelivery.model.client.PaymentStatus.valueOf(paymentStatus));
    }

    String paymentMethod = rs.getString("payment_method");
    if (paymentMethod != null) {
      order.setPaymentMethod(com.team8.fooddelivery.model.client.PaymentMethodForOrder.fromDbValue(paymentMethod));
    }

    // Обработка временных меток
    Timestamp createdAt = rs.getTimestamp("created_at");
    if (createdAt != null) {
      order.setCreatedAt(createdAt.toInstant());
    }

    Timestamp estimatedDeliveryTime = rs.getTimestamp("estimated_delivery_time");
    if (estimatedDeliveryTime != null) {
      order.setEstimatedDeliveryTime(estimatedDeliveryTime.toInstant());
    }

    Timestamp updatedAt = rs.getTimestamp("updated_at");
    if (updatedAt != null) {
      order.setUpdatedAt(updatedAt.toInstant());
    }

    // Загружаем элементы заказа
    List<CartItem> items = findOrderItemsByOrderId(order.getId(), conn);
    order.setItems(items);

    return order;
  }

  private List<CartItem> findOrderItemsByOrderId(Long orderId, Connection conn) throws SQLException {
    // Убираем product_id из SELECT
    String sql = "SELECT id, order_id, product_id, product_name, quantity, price FROM order_items WHERE order_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, orderId);
      ResultSet rs = stmt.executeQuery();

      List<CartItem> items = new ArrayList<>();
      while (rs.next()) {
        CartItem item = new CartItem();
        item.setId(rs.getLong("id"));
        item.setCartId(rs.getLong("order_id"));
        item.setProductId(rs.getObject("product_id") != null ? rs.getLong("product_id") : null);
        item.setProductName(rs.getString("product_name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPrice(rs.getDouble("price"));
        items.add(item);
      }
      return items;
    }
  }

  private void saveOrderItems(Long orderId, List<CartItem> items, Connection conn) throws SQLException {
    // Убираем product_id из INSERT, так как у нас может не быть реальных продуктов в БД
    String sql = "INSERT INTO order_items (order_id, product_id, product_name, quantity, price) VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (CartItem item : items) {
        stmt.setLong(1, orderId);
        stmt.setObject(2, item.getProductId(), Types.BIGINT);
        stmt.setString(3, item.getProductName());
        stmt.setInt(4, item.getQuantity());
        stmt.setDouble(5, item.getPrice());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }
}