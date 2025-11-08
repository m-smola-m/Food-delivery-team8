
package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.service.CourierService;
import com.team8.fooddelivery.model.Courier;
import com.team8.fooddelivery.service.CourierServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CourierUserStory {

  public static void main(String[] args) {
    CourierService courierService = new CourierServiceImpl();

    log.info("=== СИСТЕМА ДОСТАВКИ: КУРЬЕР ===");

    Long courierId = courierService.registerNewCourier(
        "Ivan Tokaev",
        "79997776655",
        "mypassword",
        "scooter"
    );
    log.info("Зарегистрирован новый курьер с ID: {}", courierId);

    Courier courier = courierService.login("79997776655", "mypassword");
    if (courier != null) {
      log.info("Вход выполнен: {}", courier.getName());
    } else {
      log.error("Ошибка входа");
      return;
    }

    courierService.startShift(courierId);
    log.info("Курьер начал смену. Статус: {}", courier.getStatus());

    boolean accepted = courierService.acceptOrder(courierId, 101L);
    if (accepted) {
      log.info("Заказ #101 принят");
    } else {
      log.warn("Не удалось принять заказ");
    }

    courierService.pickupOrder(courierId, 101L);
    log.info("Заказ забран. Текущий статус: {}", courier.getStatus());

    courierService.completeOrder(courierId, 101L);
    log.info("Заказ доставлен. Статус: {}", courier.getStatus());
    log.info("Баланс курьера: {}", courier.getCurrentBalance());

    log.info("История заказов:");
    courierService.getOrderHistory(courierId).forEach(order -> log.info(" - {}", order));

    courierService.endShift(courierId);
    log.info("Курьер закончил смену. Статус: {}", courier.getStatus());
  }
}
