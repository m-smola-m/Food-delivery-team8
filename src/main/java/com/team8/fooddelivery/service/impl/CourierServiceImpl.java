package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.Courier;
import com.team8.fooddelivery.model.CourierStatus;
import com.team8.fooddelivery.model.Order;
import com.team8.fooddelivery.model.OrderStatus;
import com.team8.fooddelivery.service.CourierManagementService;
import com.team8.fooddelivery.service.CourierWorkService;
import com.team8.fooddelivery.util.ValidationUtils;
import com.team8.fooddelivery.util.PasswordUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CourierServiceImpl implements CourierManagementService, CourierWorkService {

  private final Map<Long, Courier> couriers = Courier.TEST_COURIERS;
  private final Map<Long, Order> orders = Order.TEST_ORDERS;

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
        // ИСПРАВЛЕНИЕ: Хешируем пароль перед сохранением
        c.setPassword(PasswordUtils.hashPassword(password));
        c.setTransportType(transportType);
        c.setStatus(CourierStatus.OFF_SHIFT);
        c.setCurrentBalance(0.0);
        c.setBankCard(1111222233334444L);
        couriers.put(newId, c);
        return newId;
    }

    @Override
    public Courier login(String phoneNumber, String password) {
        return couriers.values().stream()
                .filter(c -> c.getPhoneNumber().equals(phoneNumber))
                .filter(c -> {
                    // ИСПРАВЛЕНИЕ: Правильная проверка пароля через PasswordUtils
                    try {
                        return PasswordUtils.checkPassword(password, c.getPassword());
                    } catch (Exception e) {
                        return false;
                    }
                })
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
            // ИСПРАВЛЕНИЕ: Хешируем новый пароль
            c.setPassword(PasswordUtils.hashPassword(password));
            c.setTransportType(transportType);
        }
    }

  @Override
  public void startShift(Long courierId) {
    Courier c = couriers.get(courierId);
    if (c != null) c.setStatus(CourierStatus.ON_SHIFT); 
  }

  @Override
  public void endShift(Long courierId) {
    Courier c = couriers.get(courierId);
    if (c != null) c.setStatus(CourierStatus.OFF_SHIFT); 
  }

  @Override
  public boolean acceptOrder(Long courierId, Long orderId) {
    Courier c = couriers.get(courierId);
    Order o = orders.get(orderId);

    if (c != null && c.getStatus() == CourierStatus.ON_SHIFT && 
        o != null && o.getStatus() == OrderStatus.READY_FOR_PICKUP) { 
      
      c.setStatus(CourierStatus.DELIVERING); 
      c.setCurrentOrderId(orderId);
      
      o.setStatus(OrderStatus.DELIVERING);
      o.setCourierId(courierId);
      return true;
    }
    return false;
  }

  @Override
  public void pickupOrder(Long courierId, Long orderId) {
    Courier c = couriers.get(courierId);
    Order o = orders.get(orderId);

    if (c != null && Objects.equals(c.getCurrentOrderId(), orderId)) {
      c.setStatus(CourierStatus.PICKED_UP); 
      
      if (o != null) {
        o.setStatus(OrderStatus.PICKED_UP); 
      }
    }
  }

  @Override
  public void completeOrder(Long courierId, Long orderId) {
    Courier c = couriers.get(courierId);
    Order o = orders.get(orderId);

    if (c != null && Objects.equals(c.getCurrentOrderId(), orderId)) {
      c.setStatus(CourierStatus.ON_SHIFT); 
      c.setCurrentOrderId(null);
      c.setCurrentBalance(c.getCurrentBalance() + 100.0);
      
      if (o != null) {
        o.setStatus(OrderStatus.COMPLETED); 
      }
    }
  }

  @Override
  public List<Order> getOrderHistory(Long courierId) {
    Courier c = couriers.get(courierId);
    if (c == null) return List.of();
    return orders.values().stream()
        .filter(o -> courierId.equals(o.getCourierId()) && o.getStatus() == OrderStatus.COMPLETED)
        .collect(Collectors.toList());
  }
  @Override
  public Courier getCourierById(Long courierId) {
    return couriers.get(courierId);
  }
}
