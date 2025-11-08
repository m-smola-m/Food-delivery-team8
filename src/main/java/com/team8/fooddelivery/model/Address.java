package com.team8.fooddelivery.model;

import lombok.*;

@Data
@AllArgsConstructor
public class Address {
  private String country;
  private String city;
  private String street;
  private Integer building;
  private Integer apartment;
  private Integer entrance;
  private Integer floor;
  private Double latitude;
  private Double longitude;
  private String addressNote;
  private String district;
}
