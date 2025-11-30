package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.client.PaymentMethodForOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order placeOrder(Long clientId, Address deliveryAddress, PaymentMethodForOrder paymentMethod);

    /**
     * Получить заказы доступные для приема курьером
     * Критерии: статус PAID (оплачены)
     */
    List<Order> getAvailableOrdersForCourier();

    /**
     * Получить историю заказов курьера за конкретную дату
     * с разбивкой по датам
     */
    List<Order> getCourierDeliveryHistoryByDate(Long courierId, LocalDate date);

    /**
     * Получить заказ по ID
     */
    Optional<Order> getOrderById(Long orderId);

    /**
     * Обновить заказ
     */
    void updateOrder(Order order);
}
