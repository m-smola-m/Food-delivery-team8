package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.Notification;
import com.team8.fooddelivery.service.NotificationService;

import java.util.List;

public class NotificationsUserStory {

    public static void main(String[] args) {
        NotificationService notificationService = new NotificationService();
        Long clientId = 1L;

        // ==== 1. Отправка уведомлений ====
        notificationService.notifyAccount(clientId, "Ваш аккаунт активирован");
        notificationService.notifyOrder(clientId, "Ваш заказ принят");
        notificationService.notifyDelivery(clientId, "Ваш заказ доставлен");

        // ==== 2. Получение уведомлений ====
        List<Notification> notifications = notificationService.getNotifications(clientId);
        System.out.println("Уведомления клиента " + clientId + ":");
        notifications.forEach(System.out::println);

        // ==== 3. Проверка количества уведомлений ====
        if (notifications.size() == 3) {
            System.out.println("Тест пройден: все уведомления отправлены");
        } else {
            System.out.println("Тест провален: ожидалось 3 уведомления, но получили " + notifications.size());
        }

        // ==== 4. Очистка уведомлений ====
        notificationService.printNotifications(clientId);
        notificationService.getNotifications(clientId).clear(); // альтернативно: добавить метод clear()
        System.out.println("После очистки:");
        notificationService.printNotifications(clientId);
    }
}
