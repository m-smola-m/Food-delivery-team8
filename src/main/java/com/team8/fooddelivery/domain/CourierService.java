package com.team8.fooddelivery.domain;

public interface CourierService {

  /**
   * Курьер выходит на смену.
   * Меняет его статус на "online".
   */
  void startShift(long courierId);

  /**
   * Курьер заканчивает смену.
   * Меняет его статус на  "offline".
   */
  void endShift(long courierId);

  /**
   * Курьер нажал "Принять заказ".
   * Система должна привязать заказ к курьеру.
   *
   */
  boolean acceptOrder(long courierId, long currentOrderId);

  /**
   * Курьер нажал "Заказ забрал" (в ресторане).
   * Система должна:
   * 2. Обновить `lastAddress` курьера
   * на адрес РЕСТОРАНА из этого заказа.
   */
  void pickupOrder(long courierId, long currentOrderId);


  /**
   * Курьер нажал "Заказ доставлен" (у клиента).
   * Система должна:
   * 1. Поменять статус курьера на "online".
   * 2. Обновить `lastAddress` курьера
   * на адрес КЛИЕНТА из этого заказа.
   */
  void completeOrder(long courierId, long currentOrderId);

  /**
   * Регистрация нового курьера в системе.
   *
   * @param name          ФИО.
   * @param phoneNumber   Телефон.
   * @param transportType Тип транспорта ("car", "bike", "foot")
   * @return ID нового созданного курьера.
   */
  long registerNewCourier(String name, String phoneNumber, String transportType);
}
