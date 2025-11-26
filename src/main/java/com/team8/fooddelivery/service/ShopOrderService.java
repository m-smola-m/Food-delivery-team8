package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.Order;
import java.util.List;

public interface ShopOrderService {

  List<Order> getPendingOrders(Long shopId);

  void changeOrderStatus(Long shopId, Long orderId);

  void rejectOrder(Long orderId, String reason);

}
