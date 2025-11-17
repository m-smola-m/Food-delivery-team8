package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.model.notification.Notification;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.payment.PaymentMethod;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
import com.team8.fooddelivery.service.impl.NotificationServiceImpl;
import com.team8.fooddelivery.service.impl.OrderServiceImpl;

import java.util.List;

public class OrderUserStory {

    public static void main(String[] args) {

        // =====================
        // 0. Инициализация сервисов
        // =====================
        NotificationServiceImpl notificationService = new NotificationServiceImpl();
        CartServiceImpl cartService = new CartServiceImpl();
        OrderServiceImpl orderService = new OrderServiceImpl(notificationService);

        Long clientId = 1L;

        // =====================
        // 1. Создаем корзину и добавляем товары
        // =====================
        Cart cart = cartService.createCartForClient(clientId);

        cartService.addItem(clientId, CartItem.builder()
                .productId(101L)
                .productName("Пицца Маргарита")
                .quantity(2)
                .price(1200L)
                .build());

        cartService.addItem(clientId, CartItem.builder()
                .productId(102L)
                .productName("Суши сет")
                .quantity(1)
                .price(2500L)
                .build());

        System.out.println("Корзина клиента: " + cartService.getCartForClient(clientId));

        // =====================
        // 2. Выбираем адрес доставки и метод оплаты
        // =====================
        Address deliveryAddress = new Address(
                "Россия", "Москва", "Тверская", "1", "10", "1", 3,
                55.7558, 37.6173, "Квартира с видом на Кремль", "ЦАО"
        );

        PaymentMethod paymentMethod = new PaymentMethod(
                clientId, "CARD", "1234567812345678", "Ivan Ivanov", "12/25", "123"
        );

        // =====================
        // 3. Создание заказа
        // =====================
        Order order = orderService.createOrder(clientId, cartService.listItems(clientId), deliveryAddress, paymentMethod);
        System.out.println("Создан заказ: " + order);

        // =====================
        // 4. Оплата заказа
        // =====================
        order = orderService.payOrder(order.getId(), paymentMethod);
        System.out.println("Статус оплаты после оплаты: " + order.getPaymentStatus());

        // =====================
        // 5. Обновление статуса заказа (сборка и доставка)
        // =====================
        orderService.updateStatus(order.getId(), OrderStatus.PICK_AND_PACKING);
        System.out.println("Статус заказа после сборки: " + order.getStatus());

        orderService.updateStatus(order.getId(), OrderStatus.SHIPPED);
        System.out.println("Статус заказа после передачи в доставку: " + order.getStatus());

        orderService.updateStatus(order.getId(), OrderStatus.DELIVERED);
        System.out.println("Статус заказа после доставки: " + order.getStatus());

        // =====================
        // 6. Проверка уведомлений
        // =====================
        List<Notification> notifications = notificationService.getNotifications(clientId);
        System.out.println("Уведомления клиента:");
        notifications.forEach(System.out::println);

        // =====================
        // 7. Очистка корзины после оформления заказа
        // =====================
        cartService.clear(clientId);
        System.out.println("Корзина после оформления заказа: " + cartService.getCartForClient(clientId));
    }
}
