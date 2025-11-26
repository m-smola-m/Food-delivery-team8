package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.service.impl.CourierServiceImpl;

public class CourierUserStory {

    public static void main(String[] args) {
        CourierServiceImpl courierService = new CourierServiceImpl();

        System.out.println("=== СИСТЕМА ДОСТАВКИ: КУРЬЕР ===");

        Long courierId = courierService.registerNewCourier(
                "Ivan Tokaev",
                "+79997776655",
                "Ivan123!",
                "scooter"
        );
        System.out.println("Зарегистрирован новый курьер с ID: " + courierId);

        Courier courier = courierService.login("+79997776655", "Ivan123!");
        if (courier != null) {
            System.out.println("Вход выполнен: " + courier.getName());
        } else {
            System.out.println("Ошибка входа");
            return;
        }

        courierService.startShift(courierId);
        System.out.println("Курьер начал смену. Статус: " + courier.getStatus());

        boolean accepted = courierService.acceptOrder(courierId, 101L);
        if (accepted) {
            System.out.println("Заказ #101 принят");
        } else {
            System.out.println("Не удалось принять заказ");
        }

        courierService.pickupOrder(courierId, 101L);
        System.out.println("Заказ забран. Текущий статус: " + courier.getStatus());

        courierService.completeOrder(courierId, 101L);
        System.out.println("Заказ доставлен. Статус: " + courier.getStatus());
        System.out.println("Баланс курьера: " + courier.getCurrentBalance());

        System.out.println("История заказов:");
        courierService.getOrderHistory(courierId)
                .forEach(order -> System.out.println(" - " + order));

        courierService.endShift(courierId);
        System.out.println("Курьер закончил смену. Статус: " + courier.getStatus());
    }
}
