package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.Shop;
import com.team8.fooddelivery.service.impl.ShopInfoServiceImpl;

public class ShopUserStory {
  public static void main(String[] args) {
    ShopInfoServiceImpl service = new ShopInfoServiceImpl();

    Shop shop = new Shop();
    shop.setNaming("Test Shop");
    shop.setPhoneForAuth("+79123456789");
    Shop registered = service.registerShop(shop, "Test@mail.com", "Password123!", "+79123456789");
    System.out.println("Магазин зарегистрирован: " + registered.getShopId() + ", статус: " + registered.getStatus());

    boolean approved = service.approveShop(registered.getShopId());
    System.out.println("Магазин одобрен: " + approved);

    System.out.println(shop);

    String passResult = service.changePassword(registered.getShopId(), "Test@mail.com", "+79123456789", "NewPassword123!", "Password123!");
    System.out.println("Смена пароля: " + passResult);

    String emailResult = service.changeEmailForAuth(registered.getShopId(), "+79123456789", "NewPassword123!", "new@mail.com");
    System.out.println("Смена email: " + emailResult);

    String phoneResult = service.changePhoneForAuth(registered.getShopId(), "new@mail.com", "NewPassword123!", "+79099999999");
    System.out.println("Смена телефона: " + phoneResult);

  }
}
import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.service.impl.ClientServiceImpl;

public class ClientUserStories {
    public static void main(String[] args) {
        ClientServiceImpl clientService = new ClientServiceImpl();

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
                "89991112233",         // телефон = логин
                "Ivan123!",            // пароль
                "Иван Иванов",         // имя
                "ivan@example.com",    // email
                address1               // адрес
        );

        Client c2 = clientService.register(
                "89995556677",
                "Maria456@",
                "Мария Петрова",
                "maria@example.com",
                address2
        );

        System.out.println("Клиенты зарегистрированы:");
        clientService.listAll().forEach(System.out::println);

        // =====================
        // 2. Аутентификация по телефону
        // =====================
        System.out.println("\n2. Аутентификация\n");

        System.out.println("Иван, правильный пароль: ");
        System.out.println(clientService.authenticate("89991112233", "Ivan123!")); // true
        System.out.println("Иван, неверный пароль: ");
        System.out.println(clientService.authenticate("89991112233", "wrongpass")); // false
        System.out.println("Несуществующий телефон: ");
        System.out.println(clientService.authenticate("89990000000", "123456")); // false

        // =====================
        // 3. Добавление истории заказов
        // =====================
        System.out.println("\n3. Добавление истории заказов\n");

        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1006: тушеная картошечка, 1250₽");
        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1001: суши, 1250₽");
        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1015: пицца, 780₽");

        System.out.println("История заказов клиента " + c1.getName() + ":");
        clientService.getOrderHistory(c1.getId()).forEach(System.out::println);

        // =====================
        // 4. Обновление клиента (изменяем email)
        // =====================
        System.out.println("\n4. Обновление клиента (изменяем email)\n");

        clientService.update(c2.getId(), null, "masha@example.com", address1);
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
                clientService.authenticate("89995556677", "Maria456@")); // false

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
                clientService.authenticate("89995556677", "Maria456@")); // true

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
        System.out.println("Старый пароль: " + clientService.authenticate("89991112233", "Ivan123!")); // false
        System.out.println("Новый пароль: " + clientService.authenticate("89991112233", "Ivan789@"));  // true
    }
}
