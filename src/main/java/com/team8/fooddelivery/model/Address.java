package com.team8.fooddelivery.model;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class Address {
  private String country;
  private String city;
  private String street;
  private String building;
  private String apartment;
  private String entrance;
  private Integer floor;
  private double latitude;
  private double longitude;
  private String addressNote;
  private String district;
}