package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.payment.PaymentMethod;

import java.util.List;

public interface OrderService {

    Order createOrder(Long clientId, List<CartItem> items, Address deliveryAddress, PaymentMethod paymentMethod);

    Order payOrder(Long orderId, PaymentMethod paymentMethod);

    Order updateStatus(Long orderId, OrderStatus status);

    List<Order> getOrders(Long clientId);

    Order getOrder(Long orderId);
}
