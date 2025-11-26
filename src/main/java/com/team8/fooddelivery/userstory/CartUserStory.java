package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.model.Cart;
import com.team8.fooddelivery.model.CartItem;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
import com.team8.fooddelivery.service.impl.ClientServiceImpl;
import com.team8.fooddelivery.service.impl.OrderServiceImpl;

import java.util.List;

public class CartUserStory {
    public static void main(String[] args) {

        // =====================
        // 0. Инициализация сервисов
        // =====================
        CartServiceImpl cartService = new CartServiceImpl();
        ClientServiceImpl clientService = new ClientServiceImpl(cartService);
        OrderServiceImpl orderService = new OrderServiceImpl(cartService);

        // =====================
        // 1. Регистрация клиента (US1)
        // =====================
        Address address = new Address(
                "Россия", "Москва", "Тверская", "1", "10", "1", 3,
                55.7558, 37.6173, "Квартира с видом на Кремль", "ЦАО"
        );

        Client client = clientService.register(
                "89991112233",
                "Ivan123!",
                "Иван Иванов",
                "ivan@example.com",
                address
        );

        System.out.println("Зарегистрирован клиент: " + client);
        System.out.println("Статус: " + client.getStatus());
        System.out.println("Дата регистрации: " + client.getCreatedAt());

        // =====================
        // 2. Создание корзины автоматически (US5)
        // =====================
        Cart cart = cartService.createCartForClient(client.getId());
        client.setCart(cart);
        System.out.println("Корзина создана для клиента: " + cart);

        // =====================
        // 3. Добавление товаров в корзину (US5)
        // =====================
        cartService.addItem(client.getId(), CartItem.builder()
                .productId(101L)
                .productName("Пицца Маргарита")
                .quantity(2)
                .price(1200L)
                .build());

        cartService.addItem(client.getId(), CartItem.builder()
                .productId(102L)
                .productName("Суши сет")
                .quantity(1)
                .price(2500L)
                .build());

        cart = cartService.getCartForClient(client.getId());
        System.out.println("Корзина после добавления товаров: " + cart.getItems());
        System.out.println("TotalPrice: " + cart.getTotalPrice() + " коп.");

        // =====================
        // 4. Деактивация клиента (US4)
        // =====================
        clientService.deactivate(client.getId());
        System.out.println("Клиент деактивирован, статус: " + client.getStatus());

        try {
            cartService.addItem(client.getId(), CartItem.builder()
                    .productId(103L)
                    .productName("Салат Цезарь")
                    .quantity(1)
                    .price(800L)
                    .build());
        } catch (IllegalStateException e) {
            System.out.println("Ошибка при добавлении товара деактивированным клиентом: " + e.getMessage());
        }

        // =====================
        // 5. Активация клиента
        // =====================
        clientService.activate(client.getId());
        System.out.println("Клиент снова активен, статус: " + client.getStatus());

        // =====================
        // 6. Оформление заказа (US7)
        // =====================
        Order order = orderService.placeOrder(
                client.getId(),
                Address.builder()
                    .country("Россия")
                    .city("Москва")
                    .street("Пример")
                    .building("1")
                    .build(),
                PaymentMethodForOrder.CARD
        );

        System.out.println("Создан заказ: " + order.getId());
        System.out.println("Статус оплаты: " + order.getPaymentStatus());
        System.out.println("Статус заказа: " + order.getStatus());
        System.out.println("Состав заказа: " + order.getItems());
        System.out.println("Сумма заказа: " + order.getTotalPrice());
        System.out.println("ETA: " + order.getEstimatedDeliveryTime());

        // =====================
        // 7. Очистка корзины после заказа (US5)
        // =====================
        cartService.clear(client.getId());
        cart = cartService.getCartForClient(client.getId());
        System.out.println("Корзина после оформления заказа: " + cart.getItems());
    }
}
