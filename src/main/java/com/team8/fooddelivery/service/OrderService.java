package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.Order;
import com.team8.fooddelivery.model.PaymentMethodForOrder;

public interface OrderService {
    Order placeOrder(Long clientId, Address deliveryAddress, PaymentMethodForOrder paymentMethod);
}
