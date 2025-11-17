package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.service.CartService;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
import com.team8.fooddelivery.service.impl.ClientServiceImpl;

import java.util.List;

public class userStoryTest {
    public static void main(String[] args) {

        // =====================
        // 0. Инициализация сервисов
        // =====================
        ClientServiceImpl clientService = new ClientServiceImpl();
        CartService cartService = new CartServiceImpl();

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
        cartService.createCartForClient(client.getId());
        Cart cart = cartService.getCartForClient(client.getId());
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
        System.out.println("Корзина после добавления товаров: " + cart);
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
        } catch (Exception e) {
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
        List<CartItem> orderItems = cartService.listItems(client.getId());
        long totalPrice = cartService.calculateTotal(client.getId());

        System.out.println("Оформляем заказ с товарами: " + orderItems);
        System.out.println("Общая сумма заказа: " + totalPrice + " коп.");

        // ---------------------
        // здесь можно создать OrderService и PaymentService
        // для завершения User Story 7
        // ---------------------

        // =====================
        // 7. Очистка корзины после заказа (US5)
        // =====================
        cartService.clear(client.getId());
        System.out.println("Корзина после оформления заказа: " + cartService.getCartForClient(client.getId()));
    }
}
