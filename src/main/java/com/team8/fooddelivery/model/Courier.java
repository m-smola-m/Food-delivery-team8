package com.team8.fooddelivery.model;

// import jakarta.persistence.*;
// import java.time.Instant;

// @Data
// @AllArgsConstructor
public class Courier {

  // private long courierId;
  private Long id;
  private String name;
  private String password;
  private String phoneNumber;
  private String status;         // enum
  private String transportType;
  private long currentOrderId;
  private double currentBalance;
  private long bankCard;
      // private List<Notification> notifications;

  // public void setStatus(String status) {
  //   this.status = status;
  // }

  // public String getTransportType() {
  //   return transportType;
  // }

  // public void setTransportType(String transportType) {
  //   this.transportType = transportType;
  // }

  // public long getCurrentOrderId() {
  //   return currentOrderId;
  // }

  // public void setCurrentOrderId(long currentOrderId) {
  //   this.currentOrderId = currentOrderId;
  // }

  // public String getLastAddress() {
  //   return lastAddress;
  // }

  // public double getCurrentBalance() {
  //   return currentBalance;
  // }

  // public void setCurrentBalance(double currentBalance) {
  //   this.currentBalance = currentBalance;
  // }

  // public long getBankCard() {
  //   return bankCard;
  // }

  // public void setBankCard(long bankCard) {
  //   this.bankCard = bankCard;
  // }


}
