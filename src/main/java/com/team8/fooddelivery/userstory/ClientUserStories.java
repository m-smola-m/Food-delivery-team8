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
        System.out.println("\n1. Регистрация клиентов\n");

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
        System.out.println("\n2. Аутентификация\n");

        System.out.println("Иван, правильный пароль: ");
        System.out.println(clientService.authenticate("ivan@example.com", "Ivan123!")); // true
        System.out.println("Иван, неверный пароль: ");
        System.out.println(clientService.authenticate("ivan@example.com", "wrongpass")); // false
        System.out.println("Несуществующий email: ");
        System.out.println(clientService.authenticate("unknown@example.com", "123456")); // false

        // =====================
        // 3. Добавление истории заказов
        // =====================
        System.out.println("\n3. Добавление истории заказов\n");

        clientService.addOrderHistoryEntry(c2.getId(), "Заказ #1006: тушеная картошечка, 1250₽");
        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1001: суши, 1250₽");
        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1015: пицца, 780₽");

        System.out.println("История заказов клиента " + c1.getName() + ":");
        clientService.getOrderHistory(c1.getId()).forEach(System.out::println);

        // =====================
        // 4. Обновление клиента (изменяем email)
        // =====================
        System.out.println("\n4. Обновление клиента (изменяем email)\n");

        clientService.update(c2.getId(), null, "masha@example.com", null, address1);
        Client updatedC2 = clientService.getById(c2.getId());
        System.out.println("После обновления email второго клиента:");
        System.out.println(updatedC2);

        // =====================
        // 5. Деактивация
        // =====================
        System.out.println("\n5. Деактивация\n");

        clientService.deactivate(c2.getId());
        System.out.println("После деактивации второго клиента:");
        System.out.println(updatedC2);

        // =====================
        // 6. Проверка входа после деактивации
        // =====================
        System.out.println("\n6. Проверка входа после деактивации\n");

        System.out.println("Попытка аутентификации деактивированного клиента:");
        System.out.println("Мария: " +
                clientService.authenticate("masha@example.com", "Maria456@")); // false

        // =====================
        // 7. Попытка добавления заказа для деактивированного клиента
        // =====================
        System.out.println("\n7. Попытка добавления заказа для деактивированного клиента\n");

        try {
            clientService.addOrderHistoryEntry(c2.getId(), "Заказ #2001: бургер, 500₽");
        } catch (IllegalStateException e) {
            System.out.println("Ошибка при добавлении заказа деактивированного клиента:");
            System.out.println(e.getMessage());
        }

        // =====================
        // 8. Добавление заказа активного клиента
        // =====================
        System.out.println("\n8. Добавление заказа активного клиента\n");

        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1018: роллы, 950₽");
        System.out.println("Обновлённая история заказов активного клиента " + c1.getName() + ":");
        clientService.getOrderHistory(c1.getId()).forEach(System.out::println);

        // =====================
        // 9. Активация клиента
        // =====================
        System.out.println("\n9. Активация клиента\n");

        clientService.activate(c2.getId());
        System.out.println("После активации второго клиента:");
        System.out.println(clientService.getById(c2.getId()));

        System.out.println("\nПопытка аутентификации после активации\n");

        System.out.println("Мария: " +
                clientService.authenticate("masha@example.com", "Maria456@")); // true

        System.out.println("\nДобавление заказа после активации\n");

        clientService.addOrderHistoryEntry(c2.getId(), "Заказ #2002: салат, 300₽");
        System.out.println("История заказов повторно активированного клиента:");
        clientService.getOrderHistory(c2.getId()).forEach(System.out::println);

        // =====================
        // 10. Смена пароля
        // =====================
        System.out.println("\n10. Смена пароля\n");

        System.out.println("Попытка смены пароля с неверным старым паролем:");
        try {
            clientService.changePassword(c1.getId(), "WrongOld123", "NewPass123!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("\nПопытка смены пароля с некорректным новым паролем:");
        try {
            clientService.changePassword(c1.getId(), "Ivan123!", "123");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("\nСмена пароля с корректными данными:");
        clientService.changePassword(c1.getId(), "Ivan123!", "Ivan789@");

        System.out.println("Проверка аутентификации со старым и новым паролем:");
        System.out.println("Старый пароль: " + clientService.authenticate("ivan@example.com", "Ivan123!")); // false
        System.out.println("Новый пароль: " + clientService.authenticate("ivan@example.com", "Ivan789@"));  // true
    }
}
