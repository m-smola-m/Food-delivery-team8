package com.team8.fooddelivery.service.imp;

import com.team8.fooddelivery.model.Courier;
import com.team8.fooddelivery.model.Order; 
import com.team8.fooddelivery.service.CourierService; 
import com.team8.fooddelivery.fooddelivery.ValidationUtils;

import java.util.*;
import java.util.stream.Collectors; 

public class CourierServiceImpl implements CourierService { 

  private final Map<Long, Courier> couriers = Courier.TEST_COURIERS;
  private final Map<Long, Order> orders = Order.TEST_ORDERS;

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
    Order o = orders.get(orderId);
    if (c != null && "AVAILABLE".equals(c.getStatus()) && o != null && "READY_FOR_PICKUP".equals(o.getStatus())) {
      c.setStatus("DELIVERING");
      c.setCurrentOrderId(orderId);
      o.setStatus("DELIVERING");
      o.setCourierId(courierId);
      return true;
    }
    return false;
  }

  @Override
  public void pickupOrder(Long courierId, Long orderId) {
    Courier c = couriers.get(courierId);
    Order o = orders.get(orderId); 

    if (c != null && Objects.equals(c.getCurrentOrderId(), orderId) && o != null) {
      c.setStatus("PICKED_UP");
      o.setStatus("PICKED_UP");
    }
  }

  @Override
  public void completeOrder(Long courierId, Long orderId) {
    Courier c = couriers.get(courierId);
    Order o = orders.get(orderId);
    if (c != null && Objects.equals(c.getCurrentOrderId(), orderId) && o != null) {
      c.setStatus("AVAILABLE");
      c.setCurrentOrderId(null);
      c.setCurrentBalance(c.getCurrentBalance() + 100.0);
      o.setStatus("COMPLETED");
    }
  }

  @Override
  public List<Order> getOrderHistory(Long courierId) { 
    Courier c = couriers.get(courierId);
    if (c == null) return List.of();
    return orders.values().stream()
        .filter(o -> courierId.equals(o.getCourierId()) && "COMPLETED".equals(o.getStatus()))
        .collect(Collectors.toList());
  }

  @Override
  public Courier getCourierById(Long courierId) {
    return couriers.get(courierId);
  }
}
