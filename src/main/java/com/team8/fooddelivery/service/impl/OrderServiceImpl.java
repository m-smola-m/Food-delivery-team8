package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.client.Payment;
import com.team8.fooddelivery.model.client.PaymentMethodForOrder;
import com.team8.fooddelivery.model.client.PaymentStatus;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.repository.OrderRepository;
import com.team8.fooddelivery.repository.PaymentRepository;
import com.team8.fooddelivery.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final CartServiceImpl cartService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationServiceImpl notificationService;
    private final ClientRepository clientRepository;

    public OrderServiceImpl(CartServiceImpl cartService) {
        this.cartService = cartService;
        this.orderRepository = new OrderRepository();
        this.paymentRepository = new PaymentRepository();
        this.notificationService = NotificationServiceImpl.getInstance();
        this.clientRepository = new ClientRepository();
    }

    @Override
    public Order placeOrder(Long clientId, Address deliveryAddress, PaymentMethodForOrder paymentMethod) {
        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));
            if (!client.isActive()) {
                throw new IllegalStateException("Деактивированный клиент не может оформлять заказы");
            }

            Cart cart = cartService.getCartForClient(clientId);
            if (cart == null) {
                throw new IllegalStateException("Корзина для клиента не найдена");
            }
            if (cart.getItems().isEmpty()) {
                throw new IllegalStateException("Невозможно оформить пустой заказ");
            }

            validateCartItems(cart.getItems());

            Order order = new Order();
            order.setCustomerId(clientId);
            order.setStatus(OrderStatus.PENDING);
            order.setDeliveryAddress(deliveryAddress);
            order.setItems(cart.getItems());
            order.setTotalPrice(calculateTotal(cart.getItems()));
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setCreatedAt(Instant.now());
            order.setEstimatedDeliveryTime(Instant.now().plus(Duration.ofMinutes(45)));

            Long orderId = orderRepository.save(order);
            order.setId(orderId);

            Payment payment = processPayment(order);
            paymentRepository.save(payment);

            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                order.setStatus(OrderStatus.PAID);
                order.setPaymentStatus(PaymentStatus.SUCCESS);
            } else if (paymentMethod == PaymentMethodForOrder.CASH) {
                order.setStatus(OrderStatus.CONFIRMED);
            } else {
                order.setStatus(OrderStatus.CANCELLED);
            }

            orderRepository.update(order);

            notificationService.notifyOrderPlaced(clientId, orderId, order.getTotalPrice().longValue());
            if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
                notificationService.notifyOrderPaid(clientId, orderId);
            }

            cartService.clear(clientId);
            return order;
        } catch (SQLException e) {
            logger.error("Ошибка при оформлении заказа", e);
            throw new RuntimeException("Не удалось оформить заказ", e);
        }
    }

    @Override
    public List<Order> getAvailableOrdersForCourier() {
        try {
            List<Order> allOrders = orderRepository.findAll();
            return allOrders.stream()
                    .filter(order -> order.getStatus() == OrderStatus.PAID)
                    .filter(order -> order.getCourierId() == null || order.getCourierId() == 0)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            logger.error("Ошибка при получении доступных заказов", e);
            return List.of();
        }
    }

    @Override
    public List<Order> getCourierDeliveryHistoryByDate(Long courierId, LocalDate date) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
            List<Order> allOrders = orderRepository.findAll();

            return allOrders.stream()
                    .filter(order -> order.getCourierId() != null && order.getCourierId().equals(courierId))
                    .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                    .filter(order -> {
                        LocalDate orderDate = order.getCreatedAt().atZone(zoneId).toLocalDate();
                        return orderDate.equals(date);
                    })
                    .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            logger.error("Ошибка при получении истории доставок", e);
            return List.of();
        }
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        try {
            return orderRepository.findById(orderId);
        } catch (SQLException e) {
            logger.error("Ошибка при получении заказа по ID", e);
            return Optional.empty();
        }
    }

    @Override
    public void updateOrder(Order order) {
        try {
            orderRepository.update(order);
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении заказа", e);
            throw new RuntimeException("Не удалось обновить заказ", e);
        }
    }

    @Override
    public List<Order> getOrdersByClient(Long clientId) {
        try {
            return orderRepository.findByCustomerId(clientId);
        } catch (SQLException e) {
            logger.error("Ошибка при получении заказов клиента {}", clientId, e);
            return List.of();
        }
    }

    @Override
    public Order repeatOrder(Long clientId, Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .filter(o -> o.getCustomerId() != null && o.getCustomerId().equals(clientId))
                    .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
            cartService.addItemsFromOrder(clientId, order.getItems());
            notificationService.notifyAccount(clientId, "Повтор заказа #" + orderId);
            return order;
        } catch (SQLException e) {
            logger.error("Не удалось повторить заказ", e);
            throw new RuntimeException("Не удалось повторить заказ", e);
        }
    }

    private void validateCartItems(List<CartItem> items) {
        boolean invalid = items.stream()
                .anyMatch(i -> i.getQuantity() <= 0 || i.getPrice() <= 0 || i.getProductName() == null || i.getProductName().isBlank());
        if (invalid) {
            throw new IllegalArgumentException("Некорректные товары в корзине");
        }
    }

    private double calculateTotal(List<CartItem> items) {
        return items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    private Payment processPayment(Order order) {
        PaymentStatus status = order.getPaymentMethod() == PaymentMethodForOrder.CASH
                ? PaymentStatus.PENDING
                : PaymentStatus.SUCCESS;

        return Payment.builder()
                .orderId(order.getId())
                .amount(order.getTotalPrice())
                .method(order.getPaymentMethod())
                .status(status)
                .createdAt(Instant.now())
                .build();
    }
}
