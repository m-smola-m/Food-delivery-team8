package com.team8.fooddelivery.service;

public interface CourierService { // + methods from user stories + methods for courier management


  void startShift(Long courierId);


  void endShift(Long courierId);


  boolean acceptOrder(Long courierId, Long orderId);


  void pickupOrder(Long courierId, Long orderId);


  void completeOrder(Long courierId, Long orderId);


  Long registerNewCourier(String name, String phoneNumber, String password, String transportType);
}
