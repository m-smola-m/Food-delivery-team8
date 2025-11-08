package com.team8.fooddelivery.service;

import java.util.List;

public interface CourierWorkService {
  boolean acceptOrder(Long courierId, Long orderId);
  void pickupOrder(Long courierId, Long orderId);
  void completeOrder(Long courierId, Long orderId);
  List<String> getOrderHistory(Long courierId);
  void startShift(Long courierId);
  void endShift(Long courierId);
}
