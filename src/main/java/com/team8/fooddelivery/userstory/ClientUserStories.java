package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.service.imp.ClientServiceImp;


public class ClientUserStories {
    public static void main(String[] args) {
        ClientServiceImp clientService = new ClientServiceImp();

        // =====================
        // 1. Регистрация клиентов
        // =====================

        Address address1 = new Address(
                "Россия", "Москва", "Тверская", 1, 10, 1, 3,
                55.7558, 37.6173, "Квартира с видом на Кремль", "ЦАО"
        );

        Address address2 = new Address(
                "Россия", "Санкт-Петербург", "Невский", 10, 5, 2, 2,
                59.9311, 30.3609, "Рядом с метро Площадь Восстания", "Центральный"
        );

        Client c1 = clientService.register(
                "Иван Иванов",
                "ivan@example.com",
                "89991112233",
                address1,
                "Ivan123!"
        );

        Client c2 = clientService.register(
                "Мария Петрова",
                "maria@example.com",
                "89995556677",
                address2,
                "Maria456@"
        );

        System.out.println("Клиенты зарегистрированы:");
        clientService.listAll().forEach(System.out::println);

        // =====================
        // 2. Аутентификация
        // =====================
        System.out.println("\nПроверка входа:");
        System.out.println("Иван, правильный пароль: " +
                clientService.authenticate("ivan@example.com", "Ivan123!")); // true
        System.out.println("Иван, неверный пароль: " +
                clientService.authenticate("ivan@example.com", "wrongpass")); // false
        System.out.println("Несуществующий email: " +
                clientService.authenticate("unknown@example.com", "123456")); // false

        // =====================
        // 3. Добавление истории заказов
        // =====================
        clientService.addOrderHistoryEntry(c2.getId(), "Заказ #1006: тушеная картошечка, 1250₽");
        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1001: суши, 1250₽");
        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1015: пицца, 780₽");

        System.out.println("\nИстория заказов клиента " + c1.getName() + ":");
        clientService.getOrderHistory(c1.getId()).forEach(System.out::println);

        // =====================
        // 4. Обновление клиента (изменяем email)
        // =====================
        clientService.update(c2.getId(), null, "masha@example.com", null, address1);
        Client updatedC2 = clientService.getById(c2.getId());
        System.out.println("\nПосле обновления email второго клиента:");
        System.out.println(updatedC2);

        // =====================
        // 5. Деактивация
        // =====================
        clientService.deactivate(c2.getId());
        System.out.println("\nПосле деактивации второго клиента:");
        System.out.println(updatedC2);

        // =====================
        // 6. Проверка входа после деактивации
        // =====================
        System.out.println("\nПопытка аутентификации деактивированного клиента:");
        System.out.println("Мария: " +
                clientService.authenticate("masha@example.com", "Maria456@")); // false

        // =====================
        // 7. Попытка добавления заказа для деактивированного клиента
        // =====================
        try {
            clientService.addOrderHistoryEntry(c2.getId(), "Заказ #2001: бургер, 500₽");
        } catch (IllegalStateException e) {
            System.out.println("\nОшибка при добавлении заказа деактивированного клиента:");
            System.out.println(e.getMessage());
        }

        // =====================
        // 8. Добавление заказа активного клиента
        // =====================
        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1018: роллы, 950₽");
        System.out.println("\nОбновлённая история заказов активного клиента " + c1.getName() + ":");
        clientService.getOrderHistory(c1.getId()).forEach(System.out::println);

        // =====================
        // 9. Активация клиента
        // =====================
        clientService.activate(c2.getId());
        System.out.println("\nПосле активации второго клиента:");
        System.out.println(clientService.getById(c2.getId()));

        // Проверка входа после активации
        System.out.println("\nПопытка аутентификации после активации:");
        System.out.println("Мария: " +
                clientService.authenticate("masha@example.com", "Maria456@")); // true

        // Добавление заказа после активации
        clientService.addOrderHistoryEntry(c2.getId(), "Заказ #2002: салат, 300₽");
        System.out.println("\nИстория заказов повторно активированного клиента:");
        clientService.getOrderHistory(c2.getId()).forEach(System.out::println);
    }
}
