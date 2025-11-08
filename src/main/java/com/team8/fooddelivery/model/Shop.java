package com.team8.fooddelivery.model;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class Shop {  // Shop
  private Long shopId;
  private String naming;
  private String description;
  private String publicEmail;
  private String emailForAuth; // emailForAuth
  private String phoneForAuth;
  private String publicPhone;
  private ShopStatus status;
  private Address address;
  private WorkingHours workingHours;
  private List<Product> products;
  private String ownerName;
  private String ownerContactPhone;
  final private Instant registrationDate = Instant.now(); // Instant
  private Double rating = 0.0;
  private ShopType type;
  private String password;
  // private List<Notification> notifications;
}
