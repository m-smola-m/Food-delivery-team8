package com.team8.fooddelivery.domain;

public class Courier {

  // Уникальный номер курьера в БД
  private long courierId;

  // ФИО курьера
  private String name;

  // Пароль для входа в систему
  private String password;

  // Номер телефона (используется для входа и связи)
  private int phoneNumber;

  /*
   * Статус курьера:
   * "offline" — не на смене
   * "online" — на смене
   * "on_delivery" — выполняет заказ
   */
  private String status;

  /*
   * Тип транспорта:
   * "foot" — пеший
   * "bike" — на велосипеде
   * "car" — на машине
   */
  private String transportType;

  // Номер текущего заказа. Если курьер свободен, значение равно 0.
  private long currentOrderId;

  // Последний адрес, где был курьер (магазин или дом клиента)
  private String lastAddress;

  // Текущий баланс
  private double currentBalance;

  // Номер банковской карты, на которую выводятся деньги
  private long bankCard;

  /**
   * Конструктор со всеми полями.
   */
  public Courier(long courierId, String name, String password, int phoneNumber, String status,
      String transportType, long currentOrderId, String lastAddress,
      double currentBalance, long bankCard) {
    this.courierId = courierId;
    this.name = name;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.status = status;
    this.transportType = transportType;
    this.currentOrderId = currentOrderId;
    this.lastAddress = lastAddress;
    this.currentBalance = currentBalance;
    this.bankCard = bankCard;
  }

  /**
   * Конструктор по умолчанию.
   */
  public Courier() {
  }

  // --- Геттеры и Сеттеры ---

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

  public int getPhoneNumber() {
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

  public void setLastAddress(String lastAddress) {
    this.lastAddress = lastAddress;
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
