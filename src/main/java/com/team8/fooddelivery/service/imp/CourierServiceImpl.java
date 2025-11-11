package com.team8.fooddelivery.service.imp;

import com.team8.fooddelivery.model.Courier;
import com.team8.fooddelivery.service.CourierManagementService;
import com.team8.fooddelivery.service.CourierWorkService;
import com.team8.fooddelivery.fooddelivery.ValidationUtils; 
import java.util.*;

public class CourierServiceImpl implements CourierManagementService, CourierWorkService {

  private final Map<Long, Courier> couriers = Courier.TEST_COURIERS;

  @Override
  public Long registerNewCourier(String name, String phoneNumber, String password, String transportType) {
        if (!ValidationUtils.isValidPhone(phoneNumber)) {
        throw new IllegalArgumentException("Неверный формат номера телефона.");
    }
    if (!ValidationUtils.isValidPassword(password)) {
        throw new IllegalArgumentException("Пароль не соответствует требованиям безопасности.");
    }
    long newId = couriers.size() + 1L;
    Courier c = new Courier();
    c.setId(newId);
    c.setName(name);
    c.setPhoneNumber(phoneNumber);
    c.setPassword(password);
    c.setTransportType(transportType);
    c.setStatus("AVAILABLE");
    c.setCurrentBalance(0.0);
    c.setBankCard(1111222233334444L);
    couriers.put(newId, c);
    return newId;
  }

  @Override
  public Courier login(String phoneNumber, String password) {
    return couriers.values().stream()
        .filter(c -> c.getPhoneNumber().equals(phoneNumber) && c.getPassword().equals(password))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void updateCourierData(Long courierId, String name, String phoneNumber, String password, String transportType) {
    Courier c = couriers.get(courierId);
    if (c != null) {
      if (!ValidationUtils.isValidPhone(phoneNumber)) {
          throw new IllegalArgumentException("Неверный формат номера телефона.");
      }
      
      if (!ValidationUtils.isValidPassword(password)) {
          throw new IllegalArgumentException("Пароль не соответствует требованиям безопасности.");
      }
      c.setName(name);
      c.setPhoneNumber(phoneNumber);
      c.setPassword(password);
      c.setTransportType(transportType);
    }
  }

  @Override
  public void startShift(Long courierId) {
    Courier c = couriers.get(courierId);
    if (c != null) c.setStatus("ON_SHIFT");
  }

  @Override
  public void endShift(Long courierId) {
    Courier c = couriers.get(courierId);
    if (c != null) c.setStatus("OFF_SHIFT");
  }

  @Override
  public boolean acceptOrder(Long courierId, Long orderId) {
    Courier c = couriers.get(courierId);
    if (c != null && "AVAILABLE".equals(c.getStatus())) {
      c.setStatus("DELIVERING");
      c.setCurrentOrderId(orderId);
      return true;
    }
    return false;
  }

  @Override
  public void pickupOrder(Long courierId, Long orderId) {
    Courier c = couriers.get(courierId);
    if (c != null && Objects.equals(c.getCurrentOrderId(), orderId)) {
      c.setStatus("PICKED_UP");
    }
  }

  @Override
  public void completeOrder(Long courierId, Long orderId) {
    Courier c = couriers.get(courierId);
    if (c != null && Objects.equals(c.getCurrentOrderId(), orderId)) {
      c.setStatus("AVAILABLE");
      c.setCurrentOrderId(null);
      c.setCurrentBalance(c.getCurrentBalance() + 100.0);
    }
  }

  @Override
  public List<String> getOrderHistory(Long courierId) {
    Courier c = couriers.get(courierId);
    if (c == null) return List.of();
    return List.of("Order #101", "Order #102", "Order #103");
  }

  @Override
  public Courier getCourierById(Long courierId) {
    return couriers.get(courierId);
  }
}
