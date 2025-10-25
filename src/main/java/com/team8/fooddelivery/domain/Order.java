package com.team8.fooddelivery.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
public class Order {
  private Long orderId;
  private Long shopId;
  private String customerName; // Long clientId
  private String customerPhone; // delete
  private List<Product> items;
  private Address deliveryAddress;
  private StoreOrderStatus status;
  private double totalAmount;
  private double storeRevenue;
  private LocalDateTime orderTime;
  private LocalDateTime estimatedReadyTime;  // Instant
  private LocalDateTime actualReadyTime;
  private String customerNotes;
  private String internalNotes;
  private String rejectionReason;
  private Integer preparationTime;   // Instant
  private boolean isPaid;
  private String paymentMethod; // enum

  public enum StoreOrderStatus {
    PENDING, CONFIRMED, PREPARING, READY, PICKED_UP, COMPLETED, REJECTED, CANCELLED
  }
}
