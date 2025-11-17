package com.team8.fooddelivery.model.order;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.CartItem;
import com.team8.fooddelivery.model.payment.PaymentMethod;
import com.team8.fooddelivery.model.payment.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private Long id;
    private Long clientId;
    private List<CartItem> items;
    private Long totalPrice; // в копейках
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private Address deliveryAddress;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDeliveryTime;
}