package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shops {
  private UUID shopId = null;
  private String naming;
  private String description;
  private String publicEmail;
  private String emailForAU;
  private String phoneForAU;
  private String publicPhone;
  private ShopStatus status;
  private Address address;
  private WorkingHours workingHours;
  private List<Product> products;
  private String ownerName;
  private String ownerContactPhone;
  final private LocalDateTime registrationDate;
  private Double rating;
  private ShopType type;
  private String password;

  public enum ShopStatus {
    PENDING, ACTIVE, SUSPENDED, CLOSED
  }

  public enum ShopType {
    RESTAURANT, CAFE, BAKERY, GROCERY, PHARMACY, OTHER
  }

  public Shops(String naming, String description, String publicEmail, String publicPhone,
              Address address, WorkingHours workingHours, String ownerName,
              String ownerContactPhone, ShopType type, String emailForAU, String phoneForAU, String password) {
    this.naming = naming;
    this.emailForAU = emailForAU;
    this.phoneForAU = phoneForAU;
    this.description = description;
    this.publicEmail = publicEmail;
    this.publicPhone = publicPhone;
    this.status = ShopStatus.PENDING;
    this.address = address;
    this.workingHours = workingHours;
    this.products = new ArrayList<>();
    this.ownerName = ownerName;
    this.ownerContactPhone = ownerContactPhone;
    this.registrationDate = LocalDateTime.now();
    this.rating = 0.0;
    this.type = type;
    this.password = password;
  }

  public static class Builder {
    private String naming;
    private String description;
    private String publicEmail;
    private String publicPhone;
    private Address address;
    private WorkingHours workingHours;
    private String ownerName;
    private String ownerContactPhone;
    private ShopType type;
    private String emailForAU;
    private String phoneForAU;
    private String password;

    public Builder setEmailForAU(String emailForAU) {
      this.emailForAU = emailForAU;
      return this;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public Builder setPhoneForAU(String phoneForAU) {
      this.phoneForAU = phoneForAU;
      return this;
    }

    public Builder setNaming(String naming) {
      this.naming = naming;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setPublicEmail(String publicEmail) {
      this.publicEmail = publicEmail;
      return this;
    }

    public Builder setPublicPhone(String publicPhone) {
      this.publicPhone = publicPhone;
      return this;
    }

    public Builder setAddress(Address address) {
      this.address = address;
      return this;
    }

    public Builder setWorkingHours(WorkingHours workingHours) {
      this.workingHours = workingHours;
      return this;
    }

    public Builder setOwnerName(String ownerName) {
      this.ownerName = ownerName;
      return this;
    }

    public Builder setOwnerContactPhone(String ownerContactPhone) {
      this.ownerContactPhone = ownerContactPhone;
      return this;
    }

    public Builder setType(ShopType type) {
      this.type = type;
      return this;
    }

    public Shops build() {
      return new Shops(naming, description, publicEmail, publicPhone, address,
              workingHours, ownerName, ownerContactPhone, type, emailForAU, phoneForAU, password);
    }
  }

  public UUID getShopId() { return shopId; }
  public String getNaming() { return naming; }
  public String getDescription() { return description; }
  public String getPublicEmail() { return publicEmail; }
  public String getPublicPhone() { return publicPhone; }
  public ShopStatus getStatus() { return status; }
  public Address getAddress() { return address; }
  public WorkingHours getWorkingHours() { return workingHours; }
  public List<Product> getProducts() { return products; }
  public String getOwnerName() { return ownerName; }
  public String getOwnerContactPhone() { return ownerContactPhone; }
  public LocalDateTime getRegistrationDate() { return registrationDate; }
  public Double getRating() { return rating; }
  public ShopType getType() { return type; }
  public String getEmailForAU() {return emailForAU; }
  public String getPhoneForAU() {return phoneForAU; }
  public String getPassword() {return password; }

  public void setDescription(String description) { this.description = description; }
  public void setPublicEmail(String publicEmail) { this.publicEmail = publicEmail; }
  public void setPublicPhone(String publicPhone) { this.publicPhone = publicPhone; }
  public void setEmailForAU(String emailForAU) {this.emailForAU = emailForAU; }
  public void setPhoneForAU(String phoneForAU) {this.phoneForAU = phoneForAU; }
  public void setPassword(String password) {this.password = password; }
  public void setStatus(ShopStatus status) { this.status = status; }
  public void setWorkingHours(WorkingHours workingHours) { this.workingHours = workingHours; }
  public void setRating(Double rating) { this.rating = rating; }
}
