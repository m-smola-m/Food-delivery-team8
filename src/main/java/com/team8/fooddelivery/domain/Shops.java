package com.team8.fooddelivery.domain;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class Shops {  // Shop
  private Long shopId;
  private String naming;
  private String description;
  private String publicEmail;
  private String emailForAU; // emailForAuth
  private String phoneForAU;
  private String publicPhone;
  private ShopStatus status;
  private Address address;
  private WorkingHours workingHours;
  private List<Product> products;
  private String ownerName;
  private String ownerContactPhone;
  final private LocalDateTime registrationDate = LocalDateTime.now(); // Instant
  private Double rating = 0.0;
  private ShopType type;
  private String password;
      // private List<Notification> notifications;

  public enum ShopStatus {
    PENDING, ACTIVE, SUSPENDED, CLOSED
  }

  public enum ShopType {
    RESTAURANT, CAFE, BAKERY, GROCERY, PHARMACY, OTHER
  }
}
