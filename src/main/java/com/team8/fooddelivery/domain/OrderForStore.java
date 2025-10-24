package com.team8.fooddelivery.domain;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;
import java.time.Instant;

public class OrderForStore {
  private UUID orderId;
  private UUID storeId;
  private String customerName;
  private String customerPhone;
  private List<Product> items;
  private String deliveryAddress;
  private StoreOrderStatus status;
  private double totalAmount;
  private double storeRevenue;
  private LocalDateTime orderTime;
  private LocalDateTime estimatedReadyTime;
  private LocalDateTime actualReadyTime;
  private String customerNotes;
  private String internalNotes;
  private String rejectionReason; // Причина отклонения
  private int preparationTime;
  private boolean isPaid;
  private String paymentMethod;

  public enum StoreOrderStatus {
    PENDING, CONFIRMED, PREPARING, READY, PICKED_UP, COMPLETED, REJECTED, CANCELLED
  }

  public OrderForStore() {
    this.orderId = UUID.randomUUID();
    this.items = new ArrayList<>();
    this.status = StoreOrderStatus.PENDING;
    this.orderTime = LocalDateTime.now();
  }

  public void startPreparation() {
    this.status = StoreOrderStatus.PREPARING;
  }

  public void markAsReady() {
    this.status = StoreOrderStatus.READY;
    this.actualReadyTime = LocalDateTime.now();
  }

  public void rejectOrder(String reason) {
    this.status = StoreOrderStatus.REJECTED;
    this.rejectionReason = reason;
  }

  public void cancelOrder(String reason) {
    this.status = StoreOrderStatus.CANCELLED;
    this.rejectionReason = reason;
  }
  
  public UUID getOrderId() { return orderId; }
  public void setOrderId(UUID orderId) { this.orderId = orderId; }
  public UUID getStoreId() { return storeId; }
  public void setStoreId(UUID storeId) { this.storeId = storeId; }
  public String getCustomerName() { return customerName; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }
  public String getCustomerPhone() { return customerPhone; }
  public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
  public String getDeliveryAddress() { return deliveryAddress; }
  public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
  public StoreOrderStatus getStatus() { return status; }
  public void setStatus(StoreOrderStatus status) { this.status = status; }
  public double getTotalAmount() { return totalAmount; }
  public double getStoreRevenue() { return storeRevenue; }
  public void setStoreRevenue(double storeRevenue) { this.storeRevenue = storeRevenue; }
  public LocalDateTime getOrderTime() { return orderTime; }
  public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
  public LocalDateTime getEstimatedReadyTime() { return estimatedReadyTime; }
  public void setEstimatedReadyTime(LocalDateTime estimatedReadyTime) { this.estimatedReadyTime = estimatedReadyTime; }
  public LocalDateTime getActualReadyTime() { return actualReadyTime; }
  public void setActualReadyTime(LocalDateTime actualReadyTime) { this.actualReadyTime = actualReadyTime; }
  public String getCustomerNotes() { return customerNotes; }
  public void setCustomerNotes(String customerNotes) { this.customerNotes = customerNotes; }
  public String getInternalNotes() { return internalNotes; }
  public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
  public String getRejectionReason() { return rejectionReason; }
  public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
  public int getPreparationTime() { return preparationTime; }
  public void setPreparationTime(int preparationTime) { this.preparationTime = preparationTime; }
  public boolean isPaid() { return isPaid; }
  public void setPaid(boolean paid) { isPaid = paid; }
  public String getPaymentMethod() { return paymentMethod; }
  public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
