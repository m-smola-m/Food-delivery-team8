package com.team8.fooddelivery.domain;


import jakarta.persistence.*;
import java.time.Instant;

public class Courier {

  private long courierId;

  private String name;

  private String password;

  private String phoneNumber;


  private String status;


  private String transportType;

  private long currentOrderId;


  private double currentBalance;


  private long bankCard;

  public Courier(long courierId, String name, String password, int phoneNumber, String status,
      String transportType, long currentOrderId, double currentBalance, long bankCard) {
    this.courierId = courierId;
    this.name = name;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.status = status;
    this.transportType = transportType;
    this.currentOrderId = currentOrderId;
    this.currentBalance = currentBalance;
    this.bankCard = bankCard;
  }


  public Courier() {
  }


  public long getCourierId() {
    return courierId;
  }

  public void setCourierId(long courierId) {
    this.courierId = courierId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(int phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTransportType() {
    return transportType;
  }

  public void setTransportType(String transportType) {
    this.transportType = transportType;
  }

  public long getCurrentOrderId() {
    return currentOrderId;
  }

  public void setCurrentOrderId(long currentOrderId) {
    this.currentOrderId = currentOrderId;
  }

  public String getLastAddress() {
    return lastAddress;
  }

  public double getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(double currentBalance) {
    this.currentBalance = currentBalance;
  }

  public long getBankCard() {
    return bankCard;
  }

  public void setBankCard(long bankCard) {
    this.bankCard = bankCard;
  }


}
