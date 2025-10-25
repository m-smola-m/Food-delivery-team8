package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;

public interface CourierService { // + methods from user stories + methods for courier management


  void startShift(long courierId);


  void endShift(long courierId);


  boolean acceptOrder(long courierId, long currentOrderId);


  void pickupOrder(long courierId, long currentOrderId);


  void completeOrder(long courierId, long currentOrderId);


  long registerNewCourier(String name, String phoneNumber, String transportType);
}
