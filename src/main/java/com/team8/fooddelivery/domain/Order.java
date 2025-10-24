package com.team8.fooddelivery.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
public class Order {
  private Long orderId;
  private Long shopId;
  private String customerName;
  private String customerPhone;
  private List<Product> items;
  private Address deliveryAddress;
  private StoreOrderStatus status;
  private double totalAmount;
  private double storeRevenue;
  private LocalDateTime orderTime;
  private LocalDateTime estimatedReadyTime;
  private LocalDateTime actualReadyTime;
  private String customerNotes;
  private String internalNotes;
  private String rejectionReason;
  private Integer preparationTime;
  private boolean isPaid;
  private String paymentMethod;

  public enum StoreOrderStatus {
    PENDING, CONFIRMED, PREPARING, READY, PICKED_UP, COMPLETED, REJECTED, CANCELLED
  }
}
